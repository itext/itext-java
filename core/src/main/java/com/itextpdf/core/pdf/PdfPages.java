package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

/**
 * {@code PdfPages} is the PDF Pages-object.
 * The Pages of a document are accessible through a tree of nodes known as the Pages tree.
 * This tree defines the ordering of the pages in the document.
 *
 * {@see PdfPage}
 */
public class PdfPages extends PdfDictionary {
    private PdfArray kids;
    private int pagesCount;

    /**
     * Create new instance of {@code PdfPages}
     * @param doc {@see PdfDocument}
     */
    public PdfPages(PdfDocument doc) {
        super();
        makeIndirect(doc);
        kids = new PdfArray();
        put(PdfName.Type, PdfName.Pages);
        put(PdfName.Kids, kids);
    }

    /**
     * Add {@see PdfPage} or {@code PdfPages} to the list of kids of this {code PdfPages}.
     * @param page {@see PdfPage} or {@see PdfPages} to add
     */
    public void addPage(PdfObject page) {
        if (page instanceof PdfPage)
            pagesCount++;
        else if (page instanceof PdfPages)
            pagesCount += ((PdfPages)page).getPagesCount();
        kids.add(page);
    }

    /**
     * Add {@see PdfPage} or {@code PdfPages} to the list of kids of this {code PdfPages}.
     * @param page {@see PdfPage} or {@see PdfPages} to add
     */

    /**
     * Insert {@see PdfPage} into specific one-based position.
     *
     * @param index one-base index of the page
     * @param page {@see PdfPage}
     */
    public void insertPage(int index, PdfPage page) {
        pagesCount++;
        kids.add(index, page);
    }

    /**
     * Gets kids of this {@code PdfPages}.
     *
     * @return not-null {@see PdfArray} if {@code PdfPages} is not flushed,
     * otherwise returns {@code null}.
     */
    protected PdfArray getKids() {
        return kids;
    }

    /**
     * Returns the total number of {@see PdfPage}, including sub {@code PdfPages}.
     *
     * @return the number of {@see PdfPage} in this {@code PdfPages}
     */
    protected int getPagesCount() {
        return pagesCount;
    }

    @Override
    public void flush() throws IOException, PdfException {
        put(PdfName.Count, new PdfNumber(pagesCount));
        kids = null;
        super.flush();
    }

}
