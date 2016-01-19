package com.itextpdf.core.events;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;

/**
 * Event dispatched by PdfDocument.
 */
public class PdfDocumentEvent extends Event {

    /**
     * Dispatched after page is created.
     */
    final static public String START_PAGE = "StartPdfPage";

    /**
     * Dispatched after page is inserted/added into document.
     */
    final static public String INSERT_PAGE = "InsertPdfPage";

    /**
     * Dispatched after page is removed from document.
     */
    final static public String REMOVE_PAGE = "RemovePdfPage";

    /**
     * Dispatched before page is closed and written.
     */
    final static public String END_PAGE = "EndPdfPage";

    /**
     * The PdfPage associated with this event.
     */
    protected PdfPage page;

    /**
     * The PdfDocument associated with this event.
     */
    private PdfDocument document;

    /**
     * Creates a PdfDocumentEvent.
     *
     * @param type type of the event that fired this event
     * @param document document that fired this event
     */
    public PdfDocumentEvent(String type, PdfDocument document) {
        super(type);
        this.document = document;
    }

    /**
     * Creates a PdfDocumentEvent.
     *
     * @param type type of the event that fired this event
     * @param page page that fired this event
     */
    public PdfDocumentEvent(String type, PdfPage page) {
        super(type);
        this.page = page;
        this.document = page.getPdfObject().getDocument();
    }

    /**
     * Returns the PdfDocument associated with this event.
     *
     * @return the PdfDocument associated with this event
     */
    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Returns the PdfPage associated with this event. Warning: this can be null.
     *
     * @return the PdfPage associated with this event
     */
    public PdfPage getPage() {
        return page;
    }
}
