package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;

/**
 * Instances of this interface represent a piece of text,
 * somewhere on a page in a pdf document.
 */
public interface IPdfTextLocation {

    /**
     * Get the visual rectangle in which the text is located
     *
     * @return
     */
    Rectangle getRectangle();

    /**
     * Get the text
     *
     * @return
     */
    String getText();

    /**
     * Get the page number of the page on which the text is located
     *
     * @return the page number, or 0 if no page number was set
     */
    int getPageNumber();

}
