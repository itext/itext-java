package com.itextpdf.pdfa;

import com.itextpdf.canvas.color.IccBased;
import com.itextpdf.core.pdf.*;

import java.io.InputStream;

public class PdfOutputIntent extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates output intent dictionary. Null values are allowed to
     * suppress any key.
     * By default output intent subtype is GTS_PDFA1, use setter to change it.
     */
    public PdfOutputIntent(String outputConditionIdentifier, String outputCondition, String registryName, String info, InputStream iccStream) {
        super(new PdfDictionary());
        setOutputIntentSubtype(PdfName.GTS_PDFA1);
        getPdfObject().put(PdfName.Type,PdfName.OutputIntents);
        if (outputCondition != null)
            setOutputCondition(outputCondition);
        if (outputConditionIdentifier != null)
            setOutputConditionIdentifier(outputConditionIdentifier);
        if (registryName != null)
            setRegistryName(registryName);
        if (info != null)
            setInfo(info);
        if (iccStream != null) {
            setDestOutputProfile(iccStream);
        }
    }

    public PdfStream getDestOutputProfile() {
        return getPdfObject().getAsStream(PdfName.DestOutputProfile);
    }

    public void setDestOutputProfile(InputStream iccStream) {
        PdfStream stream = IccBased.getIccProfileStream(iccStream);
        getPdfObject().put(PdfName.DestOutputProfile, stream);
    }

    public PdfString getInfo() {
        return getPdfObject().getAsString(PdfName.Info);
    }

    public void setInfo(String info) {
        getPdfObject().put(PdfName.Info, new PdfString(info));
    }

    public PdfString getRegistryName() {
        return getPdfObject().getAsString(PdfName.RegistryName);
    }

    public void setRegistryName(String registryName) {
        getPdfObject().put(PdfName.RegistryName, new PdfString(registryName));
    }

    public PdfString getOutputConditionIdentifier() {
        return getPdfObject().getAsString(PdfName.OutputConditionIdentifier);
    }

    public void setOutputConditionIdentifier(String outputConditionIdentifier) {
        getPdfObject().put(PdfName.OutputConditionIdentifier, new PdfString(outputConditionIdentifier));
    }

    public void getOutputCondition() {
        getPdfObject().getAsString(PdfName.OutputCondition);
    }

    public void setOutputCondition(String outputCondition) {
        getPdfObject().put(PdfName.OutputCondition, new PdfString(outputCondition));
    }

    public void getOutputIntentSubtype() {
        getPdfObject().getAsName(PdfName.S);
    }

    public void setOutputIntentSubtype(PdfName subtype) {
        getPdfObject().put(PdfName.S, subtype);
    }

}
