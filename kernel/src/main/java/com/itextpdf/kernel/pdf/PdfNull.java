package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.OutputStream;

/**
 * Representation of the null object in the PDF specification.
 */
public class PdfNull extends PdfPrimitiveObject {

    public static final PdfNull PdfNull = new PdfNull(true);
    private static final byte[] NullContent = OutputStream.getIsoBytes("null");

    /**
     * Creates a PdfNull instance.
     */
    public PdfNull() {
        super();
    }

    private PdfNull(boolean directOnly) {
        super(directOnly);
    }

    @Override
    public int getType() {
        return Null;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfNull makeIndirect(PdfDocument document) {
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
    public PdfNull makeIndirect(PdfDocument document, PdfIndirectReference reference) {
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
    public PdfNull copyToDocument(PdfDocument document) {
        return super.copyToDocument(document, true);
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
    public PdfNull copyToDocument(PdfDocument document, boolean allowDuplicating) {
        return super.copyToDocument(document, allowDuplicating);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    protected void generateContent() {
        content = NullContent;
    }

    //Here we create new object, because if we use static object it can cause unpredictable behavior during copy objects
    @Override
    protected PdfNull newInstance() {
        return new PdfNull();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }
}