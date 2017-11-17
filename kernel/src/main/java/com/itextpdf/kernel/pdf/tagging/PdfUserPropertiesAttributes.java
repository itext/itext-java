package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import java.util.List;

public class PdfUserPropertiesAttributes extends PdfStructureAttributes {
    private static final long serialVersionUID = -3680551925943527773L;

    public PdfUserPropertiesAttributes(PdfDictionary attributesDict) {
        super(attributesDict);
    }

    public PdfUserPropertiesAttributes() {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfName.UserProperties);
        getPdfObject().put(PdfName.P, new PdfArray());
    }

    public PdfUserPropertiesAttributes(List<PdfUserProperty> userProperties) {
        this();
        PdfArray arr = getPdfObject().getAsArray(PdfName.P);
        for (PdfUserProperty userProperty : userProperties) {
            arr.add(userProperty.getPdfObject());
        }
    }

    public PdfUserPropertiesAttributes addUserProperty(PdfUserProperty userProperty) {
        getPdfObject().getAsArray(PdfName.P).add(userProperty.getPdfObject());
        setModified();
        return this;
    }

    public PdfUserProperty getUserProperty(int i) {
        PdfDictionary propDict = getPdfObject().getAsArray(PdfName.P).getAsDictionary(i);
        if (propDict == null) {
            return null;
        }
        return new PdfUserProperty(propDict);
    }

    public PdfUserPropertiesAttributes removeUserProperty(int i) {
        getPdfObject().getAsArray(PdfName.P).remove(i);
        return this;
    }

    public int getNumberOfUserProperties() {
        return getPdfObject().getAsArray(PdfName.P).size();
    }
}

