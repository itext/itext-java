package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteUtils;

public class PdfBoolean extends PdfPrimitiveObject {

    private static final long serialVersionUID = -1363839858135046832L;
	
    public static final PdfBoolean PdfTrue = new PdfBoolean(true, true);
    public static final PdfBoolean PdfFalse = new PdfBoolean(false, true);

    private static final byte[] True = ByteUtils.getIsoBytes("true");
    private static final byte[] False = ByteUtils.getIsoBytes("false");

    private boolean value;

    public PdfBoolean(boolean value) {
        this(value, false);
    }

    private PdfBoolean(boolean value, boolean directOnly) {
        super(directOnly);
        this.value = value;
    }

    private PdfBoolean() {
        super();
    }

    public boolean getValue() {
        return value;
    }

    public byte getType() {
        return Boolean;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfBoolean makeIndirect(PdfDocument document) {
        return super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfBoolean makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfBoolean copyTo(PdfDocument document) {
        return super.copyTo(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfBoolean copyTo(PdfDocument document, boolean allowDuplicating) {
        return super.copyTo(document, allowDuplicating);
    }

    @Override
    public String toString() {
        return java.lang.Boolean.toString(value);
    }

    @Override
    protected void generateContent() {
        content = value ? True : False;
    }

    @Override
    protected PdfBoolean newInstance() {
        return new PdfBoolean();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfBoolean bool = (PdfBoolean)from;
        value = bool.value;
    }
}
