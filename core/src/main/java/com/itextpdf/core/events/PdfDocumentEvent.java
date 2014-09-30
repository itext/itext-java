package com.itextpdf.core.events;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;

/**
 * Event dispatched by PdfDocument
 */
public class PdfDocumentEvent extends Event {

    /**
     * Dispatched after document is created
     */
    final static public String OpenDocument = "OpenPdfDocument";

    /**
     * Dispatched before document is closed
     */
    final static public String CloseDocument = "ClosePdfDocument";

    /**
     * Dispatched after page is created
     */
    final static public String StartPage = "StartPdfPage";

    /**
     * Dispatched after page is inserted/added into document
     */
    final static public String InsertPage = "InsertPdfPage";

    /**
     * Dispatched after page is removed from document
     */
    final static public String RemovePage = "RemovePdfPage";

    /**
     * Dispatched before page is closed and written
     */
    final static public String EndPage = "EndPdfPage";

    protected PdfPage page;
    private PdfDocument document;

    public PdfDocumentEvent(String type, PdfDocument document) {
        super(type);
        this.document = document;
    }

    public PdfDocumentEvent(String type, PdfPage page) {
        super(type);
        this.page = page;
        this.document = page.getPdfObject().getDocument();
    }

    public PdfDocument getDocument() {
        return document;
    }

    public PdfPage getPage() {
        return page;
    }
}
