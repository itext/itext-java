package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfEncryptedPayload extends PdfObjectWrapper<PdfDictionary> {

    public PdfEncryptedPayload(String subtype) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.Type, PdfName.EncryptedPayload);
        setSubtype(subtype);
    }

    private PdfEncryptedPayload(PdfDictionary pdfObject) {
        super(pdfObject);
    }


    public static PdfEncryptedPayload extractFrom(PdfFileSpec fileSpec) {
        if (fileSpec != null && fileSpec.getPdfObject().isDictionary()) {
            return PdfEncryptedPayload.wrap(((PdfDictionary) fileSpec.getPdfObject()).getAsDictionary(PdfName.EP));
        }
        return null;
    }

    public static PdfEncryptedPayload wrap(PdfDictionary dictionary) {
        PdfName type = dictionary.getAsName(PdfName.Type);
        if (type != null && !type.equals(PdfName.EncryptedPayload)) {
            throw new PdfException(PdfException.EncryptedPayloadShallHaveTypeEqualsToEncryptedPayloadIfPresent);
        }
        if (dictionary.getAsName(PdfName.Subtype) == null) {
            throw new PdfException(PdfException.EncryptedPayloadShallHaveSubtype);
        }
        return new PdfEncryptedPayload(dictionary);
    }

    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    public PdfEncryptedPayload setSubtype(String subtype) {
        return setSubtype(new PdfName(subtype));
    }

    public PdfEncryptedPayload setSubtype(PdfName subtype) {
        setModified();
        getPdfObject().put(PdfName.Subtype, subtype);
        return this;
    }

    public PdfName getVersion() {
        return getPdfObject().getAsName(PdfName.Version);
    }

    public PdfEncryptedPayload setVersion(String version) {
        return setVersion(new PdfName(version));
    }

    public PdfEncryptedPayload setVersion(PdfName version) {
        setModified();
        getPdfObject().put(PdfName.Version, version);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
