package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;

/**
 * This class acts as a default implementation of IPdfTextLocation
 */
public class DefaultPdfTextLocation implements IPdfTextLocation {

    private int pageNr;
    private Rectangle rectangle;
    private String text;

    public DefaultPdfTextLocation(int pageNr, Rectangle rect, String text) {
        this.pageNr = pageNr;
        this.rectangle = rect;
        this.text = text;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }

    public DefaultPdfTextLocation setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    public DefaultPdfTextLocation setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int getPageNumber() {
        return pageNr;
    }

    public DefaultPdfTextLocation setPageNr(int pageNr) {
        this.pageNr = pageNr;
        return this;
    }
}
