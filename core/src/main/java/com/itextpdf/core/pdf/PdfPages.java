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
public class PdfPages extends PdfObjectWrapper<PdfDictionary> {

    private PdfArray kids;
    private PdfNumber pageCount;

    public PdfPages(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Create new instance of {@code PdfPages}
     * @param pdfDocument {@see PdfDocument}
     */
    public PdfPages(PdfDocument pdfDocument) {
        super(new PdfDictionary(), pdfDocument);
        pdfObject.put(PdfName.Type, PdfName.Pages);
        kids = new PdfArray();
        pdfObject.put(PdfName.Kids, kids);
        pageCount = new PdfNumber(0);
        pdfObject.put(PdfName.Count, pageCount);
    }

    /**
     * Add {@see PdfPage} or {@code PdfPages} to the list of kids of this {code PdfPages}.
     * @param page {@see PdfPage} or {@see PdfPages} to add
     */
    public void addPage(PdfObjectWrapper page) {
        if (page instanceof PdfPage)
            pageCount.setValue(pageCount.getIntValue() + 1);
        else if (page instanceof PdfPages)
            pageCount.setValue(pageCount.getIntValue() + ((PdfPages)page).getPageCount());
        kids.add(page.getPdfObject());
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
        pageCount.setValue(pageCount.getIntValue() + 1);
        kids.add(index, page.getPdfObject());
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
    protected int getPageCount() {
        return pageCount.getIntValue();
    }
}
