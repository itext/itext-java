package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PdfObject {

    static public final int Array = 1;
    static public final int Boolean = 2;
    static public final int Dictionary = 3;
    static public final int IndirectReference = 4;
    static public final int Name = 5;
    static public final int Number = 6;
    static public final int Stream = 7;
    static public final int String = 8;

    protected int type = 0;

    /**
     * PdfDocument object belongs to. For direct objects it can be null.
     */
    protected PdfDocument pdfDocument = null;

    protected boolean flushed = false;
    protected int offset = 0;

    /**
     * If object is flushed the indirect reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    public PdfObject(int type) {
        this(null, type);
    }

    public PdfObject(PdfDocument doc, int type) {
        pdfDocument = doc;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * Flushes the object to the document.
     * @return
     * @throws IOException
     */
    public boolean flush() throws IOException {
        if (flushed)
            return true;
        getIndirectReference();
        PdfWriter writer = pdfDocument.getWriter();
        writer.add(indirectReference);
        offset = writer.getCurrentPos();
        writer.writeToBody(this);
        flushed = true;
        return flushed;
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public PdfIndirectReference getIndirectReference() {
        if (indirectReference == null) {
            if (pdfDocument != null) {
                indirectReference = pdfDocument.getNextIndirectReference(this);
            }
        }
        return indirectReference;
    }

    public void setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
    }

    public int getOffset() {
        return offset;
    }

}
