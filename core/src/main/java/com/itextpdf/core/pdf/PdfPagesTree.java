package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.util.ArrayList;

/**
 * Algorithm for construction {@see PdfPages} tree
 */
class PdfPagesTree {

    private final int leafSize = 10;
    private ArrayList<PdfDictionary> pages;
    private ArrayList<PdfDictionary> parents;
    private PdfDocument pdfDocument;

    /**
     * Create PdfPages tree.
     *
     * @param pdfDocument {@see PdfDocument}
     */
    public PdfPagesTree(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
        pages = new ArrayList<PdfDictionary>();
        parents = new ArrayList<PdfDictionary>();
        parents.add(createNewPdfPages());
    }

    /**
     * Appends the specified {@see PdfPage} to the end of this tree.
     *
     * @param page {@see PdfPage}
     */
    public void addPage(PdfPage page) {
        PdfDictionary currentPdfPages = parents.get(parents.size() - 1);
        PdfNumber pagesCount = (PdfNumber)currentPdfPages.get(PdfName.Count);
        if (pagesCount.getIntValue() % leafSize == 0 && pages.size() != 0) {
            currentPdfPages = createNewPdfPages();
            pagesCount = (PdfNumber)currentPdfPages.get(PdfName.Count);
            parents.add(currentPdfPages);
        }
        PdfArray pdfPagesKids = (PdfArray)currentPdfPages.get(PdfName.Kids);
        pdfPagesKids.add(page.getPdfObject());
        pagesCount.increment();
        page.getPdfObject().put(PdfName.Parent, currentPdfPages);
        pages.add(page.getPdfObject());
    }

    /**
     * Returns the {@see PdfPage} at the specified position in this list.
     *
     * @param  pageNum one-based index of the element to return
     * @return the {@see PdfPage} at the specified position in this list
     */
    public PdfPage getPage(int pageNum) {
        return new PdfPage(pages.get(pageNum - 1), pdfDocument);
    }

    /**
     * Gets total number of @see PdfPages.
     * @return total number of pages
     */
    public int getNumOfPages() {
        return pages.size();
    }

    /**
     * Returns the index of the first occurrence of the specified page
     * in this tree, or 0 if this tree does not contain the page.
     */
    public int getPageNum(PdfPage page) {
        return pages.indexOf(page.getPdfObject()) + 1;
    }

    /**
     * Insert {@see PdfPage} into specific one-based position.
     *
     * @param index one-base index of the page
     * @param page {@see PdfPage}
     * @throws PdfException in case {@code page} has been flushed
     */
    public void insertPage(int index, PdfPage page) throws PdfException {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        --index;
        if (index > pages.size())
            throw new IndexOutOfBoundsException("index");
        if (index == pages.size()) {
            addPage(page);
            return;
        }
        PdfDictionary parent = findPageParent(pages.get(index));
        PdfArray parentKids = (PdfArray)parent.get(PdfName.Kids);
        int subIndex = parentKids.indexOf(pages.get(index));
        PdfNumber parentCount = (PdfNumber)parent.get(PdfName.Count);
        //balancing between nearby PdfPages
        if (subIndex == 0 && index != 0) {
            PdfDictionary prevParent = (PdfDictionary)pages.get(index - 1).get(PdfName.Parent);
            PdfNumber prevParentCount = (PdfNumber)prevParent.get(PdfName.Count);
            if (prevParentCount.getIntValue() < parentCount.getIntValue()) {
                parent = prevParent;
                parentKids = (PdfArray)prevParent.get(PdfName.Kids);
                parentCount = prevParentCount;
                subIndex = prevParentCount.getIntValue();
            }
        } else if (subIndex == parentCount.getIntValue()) {
            PdfDictionary nextParent = (PdfDictionary)pages.get(index + 1).get(PdfName.Parent);
            PdfNumber nextParentCount = (PdfNumber)nextParent.get(PdfName.Count);
            if (nextParentCount.getIntValue() < parentCount.getIntValue()) {
                parent = nextParent;
                parentKids = (PdfArray)nextParent.get(PdfName.Kids);
                parentCount = nextParentCount;
                subIndex = 0;
            }
        }
        parentCount.increment();
        parentKids.add(subIndex, page.getPdfObject());
        page.getPdfObject().put(PdfName.Parent, parent);
        pages.add(index, page.getPdfObject());
    }

    /**
     * Removes the page at the specified position in this tree.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param pageNum the one-based index of the PdfPage to be removed
     * @return the page that was removed from the list
     */
    public PdfPage removePage(int pageNum) throws PdfException {
        --pageNum;
        PdfDictionary page = pages.get(pageNum);
        internalRemovePage(pageNum, page);
        return new PdfPage(page, pdfDocument);
    }

    /**
     * Removes the first occurrence of the specified page from this list,
     * if it is present. Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param page page to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified page
     */
    public boolean removePage(PdfPage page) throws PdfException {
        int pageNum = getPageNum(page);
        if (pageNum < 1)
            return false;
        internalRemovePage(pageNum - 1, page.getPdfObject());
        return true;
    }

    /**
     * Generate PdfPages tree.
     *
     * @return root {@see PdfPages}
     * @throws PdfException in case empty document
     */
    protected  PdfObject generateTree() throws PdfException {
        if (pages.isEmpty())
            throw new PdfException(PdfException.DocumentHasNoPages);
        while (parents.size() != 1) {
            ArrayList<PdfDictionary> nextParents = new ArrayList<PdfDictionary>();
            //dynamicLeafSize helps to avoid PdfPages leaf with only one page
            int dynamicLeafSize = leafSize;
            PdfDictionary current = null;
            for (int i = 0; i < parents.size(); i++) {
                PdfDictionary page = parents.get(i);
                int pageCount = ((PdfNumber)page.get(PdfName.Count)).getIntValue();
                if (i % dynamicLeafSize == 0) {
                    if (pageCount < 2) {
                        dynamicLeafSize++;
                    } else {
                        current = createNewPdfPages();
                        nextParents.add(current);
                        dynamicLeafSize = leafSize;
                    }
                }
                page.put(PdfName.Parent, current);
                PdfArray currentKids = (PdfArray)current.get(PdfName.Kids);
                currentKids.add(page);
                PdfNumber currentCount = (PdfNumber)current.get(PdfName.Count);
                currentCount.setValue(currentCount.getIntValue()+pageCount);
            }
            parents = nextParents;
        }
        return parents.get(0);
    }

    private PdfDictionary createNewPdfPages() {
        PdfDictionary pdfPages = new PdfDictionary();
        pdfPages.put(PdfName.Type, PdfName.Pages);
        pdfPages.makeIndirect(pdfDocument);
        pdfPages.put(PdfName.Kids, new PdfArray());
        pdfPages.put(PdfName.Count, new PdfNumber(0));
        return pdfPages;
    }

    private void internalRemovePage(int pageNum, PdfDictionary page) throws PdfException {
        PdfDictionary parent = findPageParent(page);
        PdfArray parentKids = (PdfArray)parent.get(PdfName.Kids);
        parentKids.remove(page);
        ((PdfNumber)parent.get(PdfName.Count)).decrement();
        pages.remove(pageNum);
    }

    private PdfDictionary findPageParent(PdfDictionary page) {
        for (PdfDictionary pdfPages: parents) {
            PdfArray pdfPagesKids = (PdfArray)pdfPages.get(PdfName.Kids);
            if (pdfPagesKids.contains(page))
                return pdfPages;
        }
        return null;
    }
}
