/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms.xfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.xmp.XmlDomWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Processes XFA forms.
 */
public class XfaForm {

    private static final int INIT_SERIALIZER_BUFFER_SIZE = 16 * 1024;

    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private boolean xfaPresent = false;
    private org.w3c.dom.Document domDocument;

    /**
     * The URI for the XFA Data schema.
     */
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";

    /**
     * An empty constructor to build on.
     */
    public XfaForm() {
        this(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xdp:xdp xmlns:xdp=\"http://ns.adobe.com/xdp/\"><template xmlns=\"http://www.xfa.org/schema/xfa-template/3.3/\"></template><xfa:datasets xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\"><xfa:data></xfa:data></xfa:datasets></xdp:xdp>".getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Creates an XFA form by the stream containing all xml information
     * @param inputStream the InputStream
     */
    public XfaForm(InputStream inputStream) {
        try {
            initXfaForm(inputStream);
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Creates an XFA form by the {@link Document} containing all xml information
     * @param domDocument The document
     */
    public XfaForm(Document domDocument) {
        setDomDocument(domDocument);
    }

    /**
     * A constructor from a {@link PdfDictionary}. It is assumed, but not
     * necessary for correct initialization, that the dictionary is actually a
     * {@link PdfAcroForm}. An entry in the dictionary with the <code>XFA</code>
     * key must contain correct XFA syntax. If the <code>XFA</code> key is
     * absent, then the constructor essentially does nothing.
     *
     * @param acroFormDictionary the dictionary object to initialize from
     */
    public XfaForm(PdfDictionary acroFormDictionary) {
        PdfObject xfa = acroFormDictionary.get(PdfName.XFA);
        if (xfa != null) {
            try {
                initXfaForm(xfa);
            } catch (Exception e) {
                throw new PdfException(e);
            }
        }
    }

    /**
     * A constructor from a <CODE>PdfDocument</CODE>. It basically does everything
     * from finding the XFA stream to the XML parsing.
     *
     * @param pdfDocument the PdfDocument instance
     */
    public XfaForm(PdfDocument pdfDocument) {
        PdfObject xfa = getXfaObject(pdfDocument);
        if (xfa != null) {
            try {
                initXfaForm(xfa);
            } catch (Exception e) {
                throw new PdfException(e);
            }
        }
    }

    /**
     * Sets the XFA key from a byte array. The old XFA is erased.
     *
     * @param form        the data
     * @param pdfDocument pdfDocument
     * @throws java.io.IOException on IO error
     */
    public static void setXfaForm(XfaForm form, PdfDocument pdfDocument) throws IOException {
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, true);
        setXfaForm(form, acroForm);
    }

    /**
     * Sets the XFA key from a byte array. The old XFA is erased.
     *
     * @param form     the data
     * @param acroForm an {@link PdfAcroForm} instance
     * @throws java.io.IOException on IO error
     */
    public static void setXfaForm(XfaForm form, PdfAcroForm acroForm) throws IOException {
        if (form == null || acroForm == null || acroForm.getPdfDocument() == null) {
            throw new IllegalArgumentException("XfaForm, PdfAcroForm and PdfAcroForm's document shall not be null");
        }
        PdfDocument document = acroForm.getPdfDocument();
        if (VersionConforming.validatePdfVersionForDeprecatedFeatureLogError(document, PdfVersion.PDF_2_0, VersionConforming.DEPRECATED_XFA_FORMS)) {
            return;
        }
        PdfObject xfa = getXfaObject(acroForm);
        if (xfa != null && xfa.isArray()) {
            PdfArray ar = (PdfArray) xfa;
            int t = -1;
            int d = -1;
            for (int k = 0; k < ar.size(); k += 2) {
                PdfString s = ar.getAsString(k);
                if ("template".equals(s.toString())) {
                    t = k + 1;
                }
                if ("datasets".equals(s.toString())) {
                    d = k + 1;
                }
            }
            if (t > -1 && d > -1) {
                //reader.killXref(ar.getAsIndirectObject(t));
                //reader.killXref(ar.getAsIndirectObject(d));
                PdfStream tStream = new PdfStream(serializeDocument(form.templateNode));
                tStream.setCompressionLevel(document.getWriter().getCompressionLevel());
                ar.set(t, tStream);
                PdfStream dStream = new PdfStream(serializeDocument(form.datasetsNode));
                dStream.setCompressionLevel(document.getWriter().getCompressionLevel());
                ar.set(d, dStream);
                ar.setModified();
                ar.flush();
                acroForm.put(PdfName.XFA, new PdfArray(ar));
                acroForm.setModified();
                if (!acroForm.getPdfObject().isIndirect()) {
                    document.getCatalog().setModified();
                }
                return;
            }
        }
        //reader.killXref(af.get(PdfName.XFA));
        PdfStream stream = new PdfStream(serializeDocument(form.domDocument));
        stream.setCompressionLevel(document.getWriter().getCompressionLevel());
        stream.flush();
        acroForm.put(PdfName.XFA, stream);
        acroForm.setModified();
        if (!acroForm.getPdfObject().isIndirect()) {
            document.getCatalog().setModified();
        }
    }

    /**
     * Extracts DOM nodes from an XFA document.
     *
     * @param domDocument an XFA file as a {@link org.w3c.dom.Document DOM
     *                    document}
     * @return a {@link Map} of XFA packet names and their associated
     * {@link org.w3c.dom.Node DOM nodes}
     */
    public static Map<String, Node> extractXFANodes(Document domDocument) {
        Map<String, Node> xfaNodes = new HashMap<>();
        Node n = domDocument.getFirstChild();
        while (n.getChildNodes().getLength() == 0) {
            n = n.getNextSibling();
        }
        n = n.getFirstChild();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String s = n.getLocalName();
                xfaNodes.put(s, n);
            }
            n = n.getNextSibling();
        }

        return xfaNodes;
    }

    /**
     * Write the XfaForm to the provided {@link PdfDocument}.
     *
     * @param document the PdfDocument to write the XFA Form to
     * @throws IOException
     */
    public void write(PdfDocument document) throws IOException {
        setXfaForm(this, document);
    }

    /**
     * Write the XfaForm to the provided {@link PdfAcroForm}.
     *
     * @param acroForm the PdfDocument to write the XFA Form to
     * @throws IOException
     */
    public void write(PdfAcroForm acroForm) throws IOException {
        setXfaForm(this, acroForm);
    }

    /**
     * Changes a field value in the XFA form.
     *
     * @param name  the name of the field to be changed
     * @param value the new value
     */
    public void setXfaFieldValue(String name, String value) {
        if (isXfaPresent()) {
            name = findFieldName(name);
            if (name != null) {
                String shortName = Xml2Som.getShortName(name);
                Node xn = findDatasetsNode(shortName);
                if (xn == null) {
                    xn = datasetsSom.insertNode(getDatasetsNode(), shortName);
                }
                setNodeText(xn, value);
            }
        }
    }

    /**
     * Gets the xfa field value.
     *
     * @param name the fully qualified field name
     * @return the field value
     */
    public String getXfaFieldValue(String name) {
        if (isXfaPresent()) {
            name = findFieldName(name);
            if (name != null) {

                name = Xml2Som.getShortName(name);
                return XfaForm.getNodeText(findDatasetsNode(name));
            }
        }
        return null;
    }

    /**
     * Returns <CODE>true</CODE> if it is a XFA form.
     *
     * @return <CODE>true</CODE> if it is a XFA form
     */
    public boolean isXfaPresent() {
        return xfaPresent;
    }

    /**
     * Finds the complete field name from a partial name.
     *
     * @param name the complete or partial name
     * @return the complete name or <CODE>null</CODE> if not found
     */
    public String findFieldName(String name) {
        if (acroFieldsSom == null && xfaPresent && datasetsSom != null ) {
            acroFieldsSom = new AcroFieldsSearch(datasetsSom.getName2Node().keySet());
        }

        if (acroFieldsSom != null && xfaPresent) {
            return acroFieldsSom.getAcroShort2LongName().containsKey(name) ? acroFieldsSom.getAcroShort2LongName().get(name) : acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
        }

        return null;
    }

    /**
     * Finds the complete SOM name contained in the datasets section from a
     * possibly partial name.
     *
     * @param name the complete or partial name
     * @return the complete name or <CODE>null</CODE> if not found
     */
    public String findDatasetsName(String name) {
        return datasetsSom.getName2Node().containsKey(name) ? name : datasetsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
    }

    /**
     * Finds the <CODE>Node</CODE> contained in the datasets section from a
     * possibly partial name.
     *
     * @param name the complete or partial name
     * @return the <CODE>Node</CODE> or <CODE>null</CODE> if not found
     */
    public Node findDatasetsNode(String name) {
        if (name == null)
            return null;
        name = findDatasetsName(name);
        if (name == null)
            return null;
        return datasetsSom.getName2Node().get(name);
    }

    /**
     * Gets all the text contained in the child nodes of this node.
     *
     * @param n the <CODE>Node</CODE>
     * @return the text found or "" if no text was found
     */
    public static String getNodeText(Node n) {
        return n == null ? "" : getNodeText(n, "");
    }

    /**
     * Sets the text of this node. All the child's node are deleted and a new
     * child text node is created.
     *
     * @param n    the <CODE>Node</CODE> to add the text to
     * @param text the text to add
     */
    public void setNodeText(Node n, String text) {
        if (n == null)
            return;
        Node nc = null;
        while ((nc = n.getFirstChild()) != null) {
            n.removeChild(nc);
        }
        if (n.getAttributes().getNamedItemNS(XFA_DATA_SCHEMA, "dataNode") != null)
            n.getAttributes().removeNamedItemNS(XFA_DATA_SCHEMA, "dataNode");
        n.appendChild(domDocument.createTextNode(text));
    }

    /**
     * Gets the top level DOM document.
     *
     * @return the top level DOM document
     */
    public Document getDomDocument() {
        return domDocument;
    }

    /**
     * Sets the top DOM document.
     *
     * @param domDocument the top DOM document
     */
    public void setDomDocument(org.w3c.dom.Document domDocument) {
        this.domDocument = domDocument;
        extractNodes();
    }

    /**
     * Gets the <CODE>Node</CODE> that corresponds to the datasets part.
     *
     * @return the <CODE>Node</CODE> that corresponds to the datasets part
     */
    public Node getDatasetsNode() {
        return datasetsNode;
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts a {@link File file
     * object} to fill this object with XFA data. The resulting DOM document may
     * be modified.
     *
     * @param file the {@link File}
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(File file) throws IOException {
        fillXfaForm(file, false);
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts a {@link File file
     * object} to fill this object with XFA data.
     *
     * @param file     the {@link File}
     * @param readOnly whether or not the resulting DOM document may be modified
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(File file, boolean readOnly) throws IOException {
        fillXfaForm(new FileInputStream(file), readOnly);
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts an {@link InputStream}
     * to fill this object with XFA data. The resulting DOM document may be
     * modified.
     *
     * @param is the {@link InputStream}
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(InputStream is) throws IOException {
        fillXfaForm(is, false);
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts an {@link InputStream}
     * to fill this object with XFA data.
     *
     * @param is       the {@link InputStream}
     * @param readOnly whether or not the resulting DOM document may be modified
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(InputStream is, boolean readOnly) throws IOException {
        fillXfaForm(new InputSource(is), readOnly);
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts a {@link InputSource
     * SAX input source} to fill this object with XFA data. The resulting DOM
     * document may be modified.
     *
     * @param is the {@link InputSource SAX input source}
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(InputSource is) throws IOException {
        fillXfaForm(is, false);
    }

    /**
     * Replaces the XFA data under datasets/data. Accepts a {@link InputSource
     * SAX input source} to fill this object with XFA data.
     *
     * @param is       the {@link InputSource SAX input source}
     * @param readOnly whether or not the resulting DOM document may be modified
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(InputSource is, boolean readOnly) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            db.setEntityResolver(new SafeEmptyEntityResolver());
            Document newdoc = db.parse(is);
            fillXfaForm(newdoc.getDocumentElement(), readOnly);
        } catch (ParserConfigurationException e) {
            throw new PdfException(e);
        } catch (SAXException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Replaces the XFA data under datasets/data.
     *
     * @param node the input {@link org.w3c.dom.Node}
     */
    public void fillXfaForm(Node node) {
        fillXfaForm(node, false);
    }

    /**
     * Replaces the XFA data under datasets/data.
     *
     * @param node     the input {@link org.w3c.dom.Node}
     * @param readOnly whether or not the resulting DOM document may be modified
     */
    public void fillXfaForm(Node node, boolean readOnly) {
        if (readOnly) {
            NodeList nodeList = domDocument.getElementsByTagName("field");
            for (int i = 0; i < nodeList.getLength(); i++) {
                ((Element) nodeList.item(i)).setAttribute("access", "readOnly");
            }
        }
        NodeList allChilds = datasetsNode.getChildNodes();
        int len = allChilds.getLength();
        Node data = null;
        for (int k = 0; k < len; ++k) {
            Node n = allChilds.item(k);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getLocalName().equals("data") && XFA_DATA_SCHEMA.equals(n.getNamespaceURI())) {
                data = n;
                break;
            }
        }
        if (data == null) {
            data = datasetsNode.getOwnerDocument().createElementNS(XFA_DATA_SCHEMA, "xfa:data");
            datasetsNode.appendChild(data);
        }
        NodeList list = data.getChildNodes();
        if (list.getLength() == 0) {
            data.appendChild(domDocument.importNode(node, true));
        } else {
            // There's a possibility that first child node of XFA data is not an ELEMENT but simply a TEXT. In this case data will be duplicated.
            // data.replaceChild(domDocument.importNode(node, true), data.getFirstChild());
            Node firstNode = getFirstElementNode(data);
            if (firstNode != null)
                data.replaceChild(domDocument.importNode(node, true), firstNode);
        }
        extractNodes();
    }

    private static String getNodeText(Node n, String name) {
        Node n2 = n.getFirstChild();
        while (n2 != null) {
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
                name = getNodeText(n2, name);
            } else if (n2.getNodeType() == Node.TEXT_NODE) {
                name += n2.getNodeValue();
            }
            n2 = n2.getNextSibling();
        }
        return name;
    }

    /**
     * Return the XFA Object, could be an array, could be a Stream.
     * Returns null if no XFA Object is present.
     *
     * @param pdfDocument a PdfDocument instance
     * @return the XFA object
     */
    private static PdfObject getXfaObject(PdfDocument pdfDocument) {
        PdfDictionary af = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        return af == null ? null : af.get(PdfName.XFA);
    }

    /**
     * Return the XFA Object, could be an array, could be a Stream.
     * Returns null if no XFA Object is present.
     *
     * @param acroForm a PdfDocument instance
     * @return the XFA object
     */
    private static PdfObject getXfaObject(PdfAcroForm acroForm) {
        return acroForm == null || acroForm.getPdfObject() == null ? null : acroForm.getPdfObject().get(PdfName.XFA);
    }

    /**
     * Serializes a XML document to a byte array.
     *
     * @param n the XML document
     * @return the serialized XML document
     * @throws java.io.IOException on error
     */
    private static byte[] serializeDocument(Node n) throws IOException {
        XmlDomWriter xw = new XmlDomWriter(false);
        ByteArrayOutputStream fout = new ByteArrayOutputStream(INIT_SERIALIZER_BUFFER_SIZE);
        xw.setOutput(fout, null);
        xw.write(n);
        fout.close();
        return fout.toByteArray();
    }

    private void initXfaForm(PdfObject xfa) throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (xfa.isArray()) {
            PdfArray ar = (PdfArray) xfa;
            for (int k = 1; k < ar.size(); k += 2) {
                PdfObject ob = ar.get(k);
                if (ob instanceof PdfStream) {
                    byte[] b = ((PdfStream) ob).getBytes();
                    bout.write(b);
                }
            }
        } else if (xfa instanceof PdfStream) {
            byte[] b = ((PdfStream) xfa).getBytes();
            bout.write(b);
        }
        bout.close();
        initXfaForm(new ByteArrayInputStream(bout.toByteArray()));
    }

    private void initXfaForm(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(true);
        DocumentBuilder db = fact.newDocumentBuilder();
        db.setEntityResolver(new SafeEmptyEntityResolver());
        setDomDocument(db.parse(inputStream));
        xfaPresent = true;
    }

    /**
     * Extracts the nodes from the domDocument.
     */
    private void extractNodes() {
        Map<String, Node> xfaNodes = extractXFANodes(domDocument);

        if (xfaNodes.containsKey("template")) {
            templateNode = xfaNodes.get("template");
        }
        if (xfaNodes.containsKey("datasets")) {
            datasetsNode = xfaNodes.get("datasets");
            Node dataNode = findDataNode(datasetsNode);
            datasetsSom = new Xml2SomDatasets(dataNode != null ? dataNode : datasetsNode.getFirstChild());
        }
        if (datasetsNode == null)
            createDatasetsNode(domDocument.getFirstChild());
    }


    /**
     * Some XFA forms don't have a datasets node.
     * If this is the case, we have to add one.
     */
    private void createDatasetsNode(Node n) {
        while (n != null && n.getChildNodes().getLength() == 0) {
            n = n.getNextSibling();
        }
        if (n != null) {
            Element e = n.getOwnerDocument().createElement("xfa:datasets");
            e.setAttribute("xmlns:xfa", XFA_DATA_SCHEMA);
            datasetsNode = e;
            n.appendChild(datasetsNode);
        }
    }

    private Node getFirstElementNode(Node src) {
        Node result = null;
        NodeList list = src.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                result = list.item(i);
                break;
            }
        }
        return result;
    }

    private Node findDataNode(Node datasetsNode) {
        NodeList childNodes = datasetsNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName().equals("xfa:data")) {
                return childNodes.item(i);
            }
        }
        return null;
    }

    // Prevents XXE attacks
    private static class SafeEmptyEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }

}
