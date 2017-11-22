package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfStructureAttributes extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 3972284224659975750L;

    public PdfStructureAttributes(PdfDictionary attributesDict) {
        super(attributesDict);
    }

    public PdfStructureAttributes(String owner) {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName(owner));
    }

    public PdfStructureAttributes(PdfNamespace namespace) {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfName.NSO);
        getPdfObject().put(PdfName.NS, namespace.getPdfObject());
    }

    public PdfStructureAttributes addEnumAttribute(String attributeName, String attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfName(attributeValue));
        setModified();
        return this;
    }

    public PdfStructureAttributes addTextAttribute(String attributeName, String attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfString(attributeValue, PdfEncodings.UNICODE_BIG));
        setModified();
        return this;
    }

    public PdfStructureAttributes addIntAttribute(String attributeName, int attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfNumber(attributeValue));
        setModified();
        return this;
    }

    public PdfStructureAttributes addFloatAttribute(String attributeName, float attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfNumber(attributeValue));
        setModified();
        return this;
    }

    public String getAttributeAsEnum(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfName attrVal = getPdfObject().getAsName(name);
        return attrVal != null ? attrVal.getValue() : null;
    }

    public String getAttributeAsText(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfString attrVal = getPdfObject().getAsString(name);
        return attrVal != null ? attrVal.toUnicodeString() : null;
    }

    public Integer getAttributeAsInt(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfNumber attrVal = getPdfObject().getAsNumber(name);
        return attrVal != null ? (Integer) attrVal.intValue() : (Integer)null;
    }

    public Float getAttributeAsFloat(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfNumber attrVal = getPdfObject().getAsNumber(name);
        return attrVal != null ? (Float)attrVal.floatValue() : (Float)null;
    }

    public PdfStructureAttributes removeAttribute(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().remove(name);
        setModified();
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
