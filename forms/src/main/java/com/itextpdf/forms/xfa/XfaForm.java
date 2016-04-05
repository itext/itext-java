package com.itextpdf.forms.xfa;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.xmp.XmlDomWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Processes XFA forms.
 */
public class XfaForm {

    private Xml2SomTemplate templateSom;
    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private boolean xfaPresent = false;
    private org.w3c.dom.Document domDocument;
    private boolean changed = false;
    /**
     * The URI for the XFA Data schema.
     */
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";

    /**
     * An empty constructor to build on.
     */
    public XfaForm() {
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
     * Return the XFA Object, could be an array, could be a Stream.
     * Returns null f no XFA Object is present.
     *
     * @param pdfDocument a PdfDocument instance
     * @return the XFA object
     */
    public static PdfObject getXfaObject(PdfDocument pdfDocument) {
        PdfDictionary af = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        return af == null ? null : af.get(PdfName.XFA);
    }

    /**
     * Write the XfaForm to the provided PdfDocument.
     *
     * @param document the PdfDocument to write the XFA Form to
     * @throws IOException
     */
    public void write(PdfDocument document) throws IOException {
        if (isChanged()) {
            setXfaForm(this, document);
        }
    }

    /**
     * Sets the XFA key from a byte array. The old XFA is erased.
     *
     * @param form the data
     * @param pdfDocument pdfDocument
     * @throws java.io.IOException on IO error
     */
    public static void setXfaForm(XfaForm form, PdfDocument pdfDocument) throws IOException {
        PdfDictionary af = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        if (af == null) {
            return;
        }
        PdfObject xfa = getXfaObject(pdfDocument);
        assert xfa != null;
        if (xfa.isArray()) {
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
                tStream.setCompressionLevel(pdfDocument.getWriter().getCompressionLevel());
                ar.set(t, tStream);
                PdfStream dStream = new PdfStream(serializeDocument(form.datasetsNode));
                dStream.setCompressionLevel(pdfDocument.getWriter().getCompressionLevel());
                ar.set(d, dStream);
                ar.flush();
                af.put(PdfName.XFA, new PdfArray(ar));
                return;
            }
        }
        //reader.killXref(af.get(PdfName.XFA));
        PdfStream stream = new PdfStream(serializeDocument(form.domDocument));
        stream.setCompressionLevel(pdfDocument.getWriter().getCompressionLevel());
        stream.flush();
        af.put(PdfName.XFA, stream);
    }

    /**
     * Extracts DOM nodes from an XFA document.
     * 
     * @param domDocument an XFA file as a {@link org.w3c.dom.Document DOM
     * document}
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
     * Serializes a XML document to a byte array.
     *
     * @param n the XML document
     * @return the serialized XML document
     * @throws java.io.IOException on error
     */
    public static byte[] serializeDocument(Node n) throws IOException {
        XmlDomWriter xw = new XmlDomWriter();
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        xw.setCanonical(false);
        xw.write(n);
        fout.close();
        return fout.toByteArray();
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
     * Gets the top level DOM document.
     *
     * @return the top level DOM document
     */
    public org.w3c.dom.Document getDomDocument() {
        return domDocument;
    }

    /**
     * Finds the complete field name contained in the "classic" forms from a partial
     * name.
     *
     * @param name the complete or partial name
     * @param af   the fields
     * @return the complete name or <CODE>null</CODE> if not found
     */
    public String findFieldName(String name, PdfAcroForm af) {
        Map<String, PdfFormField> items = af.getFormFields();
        if (items.containsKey(name))
            return name;
        if (acroFieldsSom == null) {
            if (items.isEmpty() && xfaPresent) {
                acroFieldsSom = new AcroFieldsSearch(datasetsSom.getName2Node().keySet());
            } else {
                acroFieldsSom = new AcroFieldsSearch(items.keySet());
            }
        }
        return acroFieldsSom.getAcroShort2LongName().containsKey(name) ? acroFieldsSom.getAcroShort2LongName().get(name) : acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
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
        changed = true;
    }

    /**
     * Sets the XFA form flag signaling that this is a valid XFA form.
     *
     * @param xfaPresent the XFA form flag signaling that this is a valid XFA form
     */
    public void setXfaPresent(boolean xfaPresent) {
        this.xfaPresent = xfaPresent;
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
     * Checks if this XFA form was changed.
     *
     * @return <CODE>true</CODE> if this XFA form was changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Sets the changed status of this XFA instance.
     *
     * @param changed the changed status of this XFA instance
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * Gets the class that contains the template processing section of the XFA.
     *
     * @return the class that contains the template processing section of the XFA
     */
    public Xml2SomTemplate getTemplateSom() {
        return templateSom;
    }

    /**
     * Sets the class that contains the template processing section of the XFA.
     *
     * @param templateSom the class that contains the template processing section of the XFA
     */
    public void setTemplateSom(Xml2SomTemplate templateSom) {
        this.templateSom = templateSom;
    }

    /**
     * Gets the class that contains the datasets processing section of the XFA.
     *
     * @return the class that contains the datasets processing section of the XFA
     */
    public Xml2SomDatasets getDatasetsSom() {
        return datasetsSom;
    }

    /**
     * Sets the class that contains the datasets processing section of the XFA.
     *
     * @param datasetsSom the class that contains the datasets processing section of the XFA
     */
    public void setDatasetsSom(Xml2SomDatasets datasetsSom) {
        this.datasetsSom = datasetsSom;
    }

    /**
     * Gets the class that contains the "classic" fields processing.
     *
     * @return the class that contains the "classic" fields processing
     */
    public AcroFieldsSearch getAcroFieldsSom() {
        return acroFieldsSom;
    }

    /**
     * Sets the class that contains the "classic" fields processing.
     *
     * @param acroFieldsSom the class that contains the "classic" fields processing
     */
    public void setAcroFieldsSom(AcroFieldsSearch acroFieldsSom) {
        this.acroFieldsSom = acroFieldsSom;
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
     * @param file the {@link File}
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
     * @param is the {@link InputStream}
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
     * @param is the {@link InputSource SAX input source}
     * @param readOnly whether or not the resulting DOM document may be modified
     * @throws java.io.IOException on IO error on the {@link InputSource}
     */
    public void fillXfaForm(InputSource is, boolean readOnly) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
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
     * @param node the input {@link org.w3c.dom.Node}
     */
    public void fillXfaForm(Node node) {
        fillXfaForm(node, false);
    }

    /**
     * Replaces the XFA data under datasets/data.
     * @param node the input {@link org.w3c.dom.Node}
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
        setChanged(true);
    }

    private void initXfaForm(PdfObject xfa) throws IOException, ParserConfigurationException, SAXException {
        xfaPresent = true;
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
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(true);
        DocumentBuilder db = fact.newDocumentBuilder();
        domDocument = db.parse(new ByteArrayInputStream(bout.toByteArray()));
        extractNodes();
    }

    /**
     * Extracts the nodes from the domDocument.
     */
    private void extractNodes() {
        Map<String, Node> xfaNodes = extractXFANodes(domDocument);

        if (xfaNodes.containsKey("template")) {
            templateNode = xfaNodes.get("template");
            templateSom = new Xml2SomTemplate(templateNode);
        }
        if (xfaNodes.containsKey("datasets")) {
            datasetsNode = xfaNodes.get("datasets");
            datasetsSom = new Xml2SomDatasets(datasetsNode.getFirstChild());
        }
        if (datasetsNode == null)
            createDatasetsNode(domDocument.getFirstChild());
    }


    /**
     * Some XFA forms don't have a datasets node.
     * If this is the case, we have to add one.
     */
    private void createDatasetsNode(Node n) {
        while (n.getChildNodes().getLength() == 0) {
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
}
