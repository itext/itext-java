package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

public class PdfObject {

    protected PdfDocument pdfDocument;
    protected boolean flushed = false;

    public PdfObject() {
        this(null);
    }

    public PdfObject(PdfDocument doc) {

    }

    /**
     * Flushes the object to the document.
     * Document automatically decides if to flush it either as direct or indirect object.
     *
     * @return the representation of flushed object. Can be direct or indirect.
     */
    public PdfObject flush() {
        flushed = true;
        return this;
    }

    /**
     * Flushes the object to the document.
     *
     * @param flushInfo user may specify the extra information about flushing the object.
     * @return the representation of flushed object. Can be direct or indirect depending on closeInfo.
     */
    public PdfObject flush(PdfObjectFlushInfo flushInfo) {
        flushed = true;
        return this;
    }


    public static class PdfObjectFlushInfo {

        private boolean isDirect = false;
        private int objNr = 0;
        private int genNr = 0;
        private boolean addToObjStm = false;

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

    }


}
