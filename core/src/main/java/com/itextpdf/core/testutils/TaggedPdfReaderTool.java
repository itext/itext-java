package com.itextpdf.core.testutils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.tagging.IPdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfMcr;
import com.itextpdf.core.pdf.tagging.PdfStructElem;
import com.itextpdf.core.pdf.tagging.PdfStructTreeRoot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Converts a tagged PDF document into an XML file.
 * TODO Currently resultant xml file contains only pdf structure. See #parseTag method
 * TODO Also, this class should be moved to some other package in future
 */
 public class TaggedPdfReaderTool {

    protected PdfDocument document;
    protected PrintWriter out;
    protected String rootTag;

    TaggedPdfReaderTool(PdfDocument document) {
        this.document = document;
    }

    public void convertToXml(OutputStream os)
            throws IOException {
        convertToXml(os, "UTF-8");
    }

    public void convertToXml(OutputStream os, String charset)
            throws IOException {
        OutputStreamWriter outs = new OutputStreamWriter(os, charset);
        out = new PrintWriter(outs);
        if (rootTag != null) {
            out.println("<" + rootTag + ">");
        }
        // get the StructTreeRoot from the document
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        if (structTreeRoot == null)
            throw new PdfException(PdfException.DocumentDoesntContainStructTreeRoot);
        // Inspect the child or children of the StructTreeRoot
        inspectKids(structTreeRoot.getKids());
        if (rootTag != null) {
            out.print("</" + rootTag + ">");
        }
        out.flush();
        out.close();
    }

    public TaggedPdfReaderTool setRootTag(String rootTagName) {
        this.rootTag = rootTagName;
        return this;
    }

    protected void inspectKids(List<IPdfStructElem> kids) {
        if (kids == null)
            return;

        for (IPdfStructElem kid : kids) {
            inspectKid(kid);
        }
    }

    protected void inspectKid(IPdfStructElem kid) {
        if (kid instanceof PdfStructElem) {
            PdfStructElem structElemKid = (PdfStructElem) kid;
            PdfName s = structElemKid.getRole();
            String tagN = s.getValue();
            String tag = fixTagName(tagN);
            out.print("<");
            out.print(tag);

            inspectAttributes(structElemKid);

            out.println(">");

            PdfString alt = (structElemKid).getAlt();

            if (alt != null) {
                out.print("<alt><![CDATA[");
                out.print(alt.getValue().replaceAll("[\\000]*", ""));
                out.println("]]></alt>");
            }

            inspectKids(structElemKid.getKids());
            out.print("</");
            out.print(tag);
            out.println(">");
        } else {
            parseTag((PdfMcr) kid);
        }
    }

    protected void inspectAttributes(PdfStructElem kid) {
        PdfObject attrObj = kid.getAttributes(false);

        if (attrObj != null) {
            //TODO may be improve attributes handling:
            //there may be several attributes objects, and each of them may be followed by a number, which specifies revision
            PdfDictionary attrDict;
            if (attrObj instanceof PdfArray) {
                attrDict = ((PdfArray) attrObj).getAsDictionary(0);
            } else {
                attrDict = (PdfDictionary) attrObj;
            }
            for (Map.Entry<PdfName, PdfObject> entry : attrDict.entrySet()) {
                out.print(' ');
                String attrName = entry.getKey().getValue();
                out.print(Character.toLowerCase(attrName.charAt(0)) + attrName.substring(1));
                out.print("=\"");
                out.print(entry.getValue().toString());
                out.print("\"");
            }
        }
    }

    protected void parseTag(PdfMcr kid) {
        Integer mcid = kid.getMcid();
        PdfDictionary page = kid.getPageObject();

        if (mcid != null) {
            //TODO extract content of the tag, when some analog of PdfContentStreamProcessor is implemented

            //TODO also suggest implementing some caching logic, that will parse content stream only once:
            // it will extract all page's tags content at same time, saving all this contents in some map
            // see CmpTaggedPdfReaderTool in itext5

            //temporary logic
            out.println(String.format("Page %s MCID %s", page.getIndirectReference().toString(), mcid.toString()));
        }
    }

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
            }
            else {
                if (!nameMiddle)
                    c = '-';
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
