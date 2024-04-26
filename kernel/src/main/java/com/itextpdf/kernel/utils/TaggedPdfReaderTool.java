/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.utils;

import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts a tagged PDF document into an XML file.
 */
public class TaggedPdfReaderTool {

    protected PdfDocument document;
    protected OutputStreamWriter out;
    protected String rootTag;

    // key - page dictionary; value - a mapping of mcids to text in them
    protected Map<PdfDictionary, Map<Integer, String>> parsedTags = new HashMap<>();

    /**
     * Constructs a {@link TaggedPdfReaderTool} via a given {@link PdfDocument}.
     *
     * @param document the document to read tag structure from
     */
    public TaggedPdfReaderTool(PdfDocument document) {
        this.document = document;
    }

    /**
     * Checks if a character value should be escaped/unescaped.
     *
     * @param c a character value
     *
     * @return true if it's OK to escape or unescape this value.
     */
    public static boolean isValidCharacterValue(int c) {
        return (c == 0x9 || c == 0xA || c == 0xD
                || c >= 0x20 && c <= 0xD7FF
                || c >= 0xE000 && c <= 0xFFFD
                || c >= 0x10000 && c <= 0x10FFFF);
    }

    /**
     * Converts the current tag structure into an XML file with default encoding (UTF-8).
     * @param os the output stream to save XML file to
     * @throws java.io.IOException in case of any I/O error
     */
    public void convertToXml(OutputStream os)
            throws IOException {
        convertToXml(os, "UTF-8");
    }

    /**
     * Converts the current tag structure into an XML file with provided encoding.
     * @param os the output stream to save XML file to
     * @param charset the charset of the resultant XML file
     * @throws java.io.IOException in case of any I/O error
     */
    public void convertToXml(OutputStream os, String charset)
            throws IOException {
        out = new OutputStreamWriter(os, Charset.forName(charset));
        if (rootTag != null) {
            out.write("<" + rootTag + ">" + System.lineSeparator());
        }
        // get the StructTreeRoot from the document
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        if (structTreeRoot == null)
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_DOES_NOT_CONTAIN_STRUCT_TREE_ROOT);
        // Inspect the child or children of the StructTreeRoot
        inspectKids(structTreeRoot.getKids());
        if (rootTag != null) {
            out.write("</" + rootTag + ">");
        }
        out.flush();
        out.close();
    }

    /**
     * Sets the name of the root tag of the resultant XML file
     * @param rootTagName the name of the root tag
     * @return this object
     */
    public TaggedPdfReaderTool setRootTag(String rootTagName) {
        this.rootTag = rootTagName;
        return this;
    }

    /**
     * Inspect the children of the StructTreeRoot.
     *
     * @param kids list of the direct kids of the StructTreeRoot
     */
    protected void inspectKids(List<IStructureNode> kids) {
        if (kids == null)
            return;

        for (IStructureNode kid : kids) {
            inspectKid(kid);
        }
    }

    /**
     * Inspect the child of the StructTreeRoot.
     *
     * @param kid the direct kid of the StructTreeRoot
     */
    protected void inspectKid(IStructureNode kid) {
        try {
            if (kid instanceof PdfStructElem) {
                PdfStructElem structElemKid = (PdfStructElem) kid;
                PdfName s = structElemKid.getRole();
                String tagN = s.getValue();
                String tag = fixTagName(tagN);
                out.write("<");
                out.write(tag);

                inspectAttributes(structElemKid);

                out.write(">" + System.lineSeparator());

                PdfString alt = (structElemKid).getAlt();

                if (alt != null) {
                    out.write("<alt><![CDATA[");
                    out.write(alt.getValue().replaceAll("[\\000]*", ""));
                    out.write("]]></alt>" + System.lineSeparator());
                }

                inspectKids(structElemKid.getKids());
                out.write("</");
                out.write(tag);
                out.write(">" + System.lineSeparator());
            } else if (kid instanceof PdfMcr) {
                parseTag((PdfMcr) kid);
            } else {
                out.write(" <flushedKid/> ");
            }
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.UNKNOWN_IO_EXCEPTION, e);
        }
    }

    /**
     * Inspects attributes dictionary of the StructTreeRoot child.
     *
     * @param kid the direct kid of the StructTreeRoot
     */
    protected void inspectAttributes(PdfStructElem kid) {
        PdfObject attrObj = kid.getAttributes(false);

        if (attrObj != null) {
            PdfDictionary attrDict;
            if (attrObj instanceof PdfArray) {
                attrDict = ((PdfArray) attrObj).getAsDictionary(0);
            } else {
                attrDict = (PdfDictionary) attrObj;
            }
            try {
                for (PdfName key : attrDict.keySet()) {
                    out.write(' ');
                    String attrName = key.getValue();
                    out.write(Character.toLowerCase(attrName.charAt(0)) + attrName.substring(1));
                    out.write("=\"");
                    out.write(attrDict.get(key, false).toString());
                    out.write("\"");
                }
            } catch (java.io.IOException e) {
                throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.UNKNOWN_IO_EXCEPTION, e);
            }
        }
    }

    /**
     * Parses tag of the Marked Content Reference (MCR) kid of the StructTreeRoot.
     *
     * @param kid the direct {@link PdfMcr} kid of the StructTreeRoot
     */
    protected void parseTag(PdfMcr kid) {
        int mcid = kid.getMcid();
        PdfDictionary pageDic = kid.getPageObject();

        String tagContent = "";
        if (mcid != -1) {
            if (!parsedTags.containsKey(pageDic)) {
                MarkedContentEventListener listener = new MarkedContentEventListener();

                PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
                PdfPage page = document.getPage(pageDic);
                processor.processContent(page.getContentBytes(), page.getResources());

                parsedTags.put(pageDic, listener.getMcidContent());
            }

            if (parsedTags.get(pageDic).containsKey(mcid))
                tagContent = parsedTags.get(pageDic).get(mcid);

        } else {
            PdfObjRef objRef = (PdfObjRef) kid;
            PdfObject object = objRef.getReferencedObject();
            if (object.isDictionary()) {
                PdfName subtype = ((PdfDictionary) object).getAsName(PdfName.Subtype);
                tagContent = subtype.toString();
            }
        }
        try {
            out.write(escapeXML(tagContent, true));
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.exceptions.IOException(IoExceptionMessageConstant.UNKNOWN_IO_EXCEPTION, e);
        }
    }

    /**
     * Fixes specified tag name to be valid XML tag.
     *
     * @param tag tag name to fix
     *
     * @return fixed tag name.
     */
    protected static String fixTagName(String tag) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < tag.length(); ++k) {
            char c = tag.charAt(k);
            boolean nameStart =
                    c == ':'
                            || (c >= 'A' && c <= 'Z')
                            || c == '_'
                            || (c >= 'a' && c <= 'z')
                            || (c >= '\u00c0' && c <= '\u00d6')
                            || (c >= '\u00d8' && c <= '\u00f6')
                            || (c >= '\u00f8' && c <= '\u02ff')
                            || (c >= '\u0370' && c <= '\u037d')
                            || (c >= '\u037f' && c <= '\u1fff')
                            || (c >= '\u200c' && c <= '\u200d')
                            || (c >= '\u2070' && c <= '\u218f')
                            || (c >= '\u2c00' && c <= '\u2fef')
                            || (c >= '\u3001' && c <= '\ud7ff')
                            || (c >= '\uf900' && c <= '\ufdcf')
                            || (c >= '\ufdf0' && c <= '\ufffd');
            boolean nameMiddle =
                    c == '-'
                            || c == '.'
                            || (c >= '0' && c <= '9')
                            || c == '\u00b7'
                            || (c >= '\u0300' && c <= '\u036f')
                            || (c >= '\u203f' && c <= '\u2040')
                            || nameStart;
            if (k == 0) {
                if (!nameStart)
                    c = '_';
            } else {
                if (!nameMiddle)
                    c = '-';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * NOTE: copied from itext5 XMLUtils class
     *
     * Escapes a string with the appropriated XML codes.
     *
     * @param s         the string to be escaped
     * @param onlyASCII codes above 127 will always be escaped with &amp;#nn; if <CODE>true</CODE>
     * @return the escaped string
     */
    protected static String escapeXML(String s, boolean onlyASCII) {
        char[] cc = s.toCharArray();
        int len = cc.length;
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < len; ++k) {
            int c = cc[k];
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (isValidCharacterValue(c)) {
                        if (onlyASCII && c > 127)
                            sb.append("&#").append(c).append(';');
                        else
                            sb.append((char) c);
                    }
            }
        }
        return sb.toString();
    }

    private class MarkedContentEventListener implements IEventListener {
        private Map<Integer, ITextExtractionStrategy> contentByMcid = new HashMap<>();

        public Map<Integer, String> getMcidContent() {
            Map<Integer, String> content = new HashMap<>();
            for (int id : contentByMcid.keySet()) {
                content.put(id, contentByMcid.get(id).getResultantText());
            }
            return content;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            switch (type) {
                case RENDER_TEXT:
                    TextRenderInfo textInfo = (TextRenderInfo) data;
                    int mcid = textInfo.getMcid();
                    if (mcid != -1) {
                        ITextExtractionStrategy textExtractionStrategy = contentByMcid.get(mcid);
                        if (textExtractionStrategy == null) {
                            textExtractionStrategy = new LocationTextExtractionStrategy();
                            contentByMcid.put(mcid, textExtractionStrategy);
                        }
                        textExtractionStrategy.eventOccurred(data, type);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return null;
        }
    }
}
