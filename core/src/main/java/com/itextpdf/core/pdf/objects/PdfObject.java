package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

public class PdfObject {

    /**
     * PdfDocument object belongs to. For direct objects it can be null.
     */
    protected PdfDocument pdfDocument;

    /**
     * If object is flushed as indirect the reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    public PdfObject() {
        this(null);
    }

    public PdfObject(PdfDocument doc) {
        pdfDocument = doc;
    }

    /**
     * Flushes the object to the document.
     * Document automatically decides if to flush it either as direct or indirect object.
     *
     * @return the flushed object. It can be either object itself or indirect reference if object has been flushed as indirect reference.
     */
    public PdfObject flush() {
        return flush(pdfDocument);
    }

    /**
     * Flushes the object to the document.
     * Document automatically decides if to flush it either as direct or indirect object.
     *
     * @param doc
     * @return the flushed object. It can be either object itself or indirect reference if object has been flushed as indirect reference.
     */
    public PdfObject flush(PdfDocument doc) {
        return flush(doc, null);
    }

    /**
     * Flushes the object to the document.
     *
     * @param flushInfo user may specify the extra information about flushing the object.
     * @return the flushed object. It can be either object itself or indirect reference if object has been flushed as indirect reference.
     */

    public PdfObject flush(PdfObjectFlushInfo flushInfo) {
        return flush(pdfDocument, flushInfo);
    }

    /**
     * Flushes the object to the document.
     *
     * @param doc
     * @param flushInfo user may specify the extra information about flushing the object.
     * @return the flushed object. It can be either object itself or indirect reference if object has been flushed as indirect reference.
     */
    public PdfObject flush(PdfDocument doc, PdfObjectFlushInfo flushInfo) {
        if (indirectReference != null)
            return indirectReference.flush(doc, flushInfo);
        else {
            return this;
        }
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public PdfIndirectReference getIndirectReference() {
        return indirectReference;
    }

    public static class PdfObjectFlushInfo {

        private boolean isDirect = false;
        private int objNr = 0;
        private int genNr = 0;
        private boolean addToObjStm = false;

        /**
         * Marks object for caching.
         * Object will be flushed to output stream but will stay in memory.
         */
        private boolean cacheObject = false;

        static public PdfObjectFlushInfo flushAsDirect() {
            return new PdfObjectFlushInfo(true, 0, 0, false);
        }

        static public PdfObjectFlushInfo flushAsIndirect(int objNr, int genNr) {
            return new PdfObjectFlushInfo(false, objNr, genNr, false);
        }

        static public PdfObjectFlushInfo flushAsIndirect(int objNr, int genNr, boolean addToObjStm) {
            return new PdfObjectFlushInfo(false, objNr, genNr, addToObjStm);
        }

        static public PdfObjectFlushInfo flushAsIndirectAutoRef() {
            return new PdfObjectFlushInfo(false, 0, 0, false);
        }

        static public PdfObjectFlushInfo flushAsIndirectAutoRef(boolean addToObjStm) {
            return new PdfObjectFlushInfo(false, 0, 0, addToObjStm);
        }

        protected PdfObjectFlushInfo(boolean isDirect, int objNr, int genNr, boolean addToObjStm) {
            this.isDirect = isDirect;
            this.objNr = objNr;
            this.genNr = genNr;
            this.addToObjStm = addToObjStm;
        }

        public boolean isDirect() {
            return this.isDirect;
        }

        public int getObjNr() {
            return objNr;
        }

        public int getGenNr() {
            return genNr;
        }

        public boolean addToObjStm() {
            return addToObjStm;
        }

        public void cacheObject() {
            this.cacheObject = true;
        }

    }


}
