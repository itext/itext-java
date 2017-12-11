package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfEncryptedPayloadDocument extends PdfObjectWrapper<PdfStream> {

    private PdfFileSpec fileSpec;
    private String name;

    public PdfEncryptedPayloadDocument(PdfStream pdfObject, PdfFileSpec fileSpec, String name) {
        super(pdfObject);
        this.fileSpec = fileSpec;
        this.name = name;
    }

    public byte[] getDocumentBytes() {
        return getPdfObject().getBytes();
    }

    public PdfFileSpec getFileSpec() {
        return fileSpec;
    }

    public String getName() {
        return name;
    }

    public PdfEncryptedPayload getEncryptedPayload() {
        return PdfEncryptedPayload.extractFrom(fileSpec);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
