package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.util.ArrayList;


/**
 * Algorithm for construction {@see PdfPages} tree
 */
class PdfPagesTree {
    private final int leafSize = 10;

    private ArrayList<PdfDictionary> pageRefs;
    private ArrayList<PdfPages> parents;
    private ArrayList<PdfPage> pages;
    private PdfDocument pdfDocument;

    /**
     * Create PdfPages tree.
     *
     * @param pdfCatalog {@see PdfCatalog}
     */
    public PdfPagesTree(PdfCatalog pdfCatalog) throws PdfException {
        this.pdfDocument = pdfCatalog.getDocument();
        this.pageRefs = new ArrayList<PdfDictionary>();
        this.parents = new ArrayList<PdfPages>();
        this.pages = new ArrayList<PdfPage>();
        if (pdfCatalog.getPdfObject().containsKey(PdfName.Pages)) {
            PdfDictionary pages = pdfCatalog.getPdfObject().getAsDictionary(PdfName.Pages);
            if (pages == null)
                throw new PdfException(PdfException.InvalidPageStructurePagesPagesMustBePdfDictionary);
            PdfPages pdfPages = new PdfPages(0, pages, Integer.MAX_VALUE);
            for (int i = 0; i < pdfPages.getCount(); i++) {
                this.pageRefs.add(null);
                this.pages.add(null);
            }
            parents.add(pdfPages);
        } else {
            this.parents.add(new PdfPages(0, this.pdfDocument));
        }
        //in read mode we will create PdfPages from 0 to Count
        // and reserve null indexes for pageRefs and pages.
    }

    /**
     * Appends the specified {@see PdfPage} to the end of this tree.
     *
     * @param page {@see PdfPage}
     */
    public void addPage(PdfPage page) throws PdfException {
        PdfPages pdfPages = parents.get(parents.size() - 1);
        if (pdfPages.getCount() % leafSize == 0 && pageRefs.size() != 0) {
            pdfPages = new PdfPages(pdfPages.getFrom() + pdfPages.getCount(), pdfDocument);
            parents.add(pdfPages);
        }
        page.makeIndirect(pdfDocument);
        pdfPages.addPage(page.getPdfObject());
        pageRefs.add(page.getPdfObject());
        pages.add(page);
    }

    /**
     * Returns the {@see PdfPage} at the specified position in this list.
     *
     * @param  pageNum one-based index of the element to return
     * @return the {@see PdfPage} at the specified position in this list
     */
    public PdfPage getPage(int pageNum) throws PdfException {
        --pageNum;
        PdfPage pdfPage = pages.get(pageNum);
        if (pdfPage == null) {
            loadPage(pageNum);
            pdfPage = new PdfPage(pageRefs.get(pageNum), pdfDocument);
            pages.set(pageNum, pdfPage);
        }
        return pdfPage;
    }

    /**
     * Gets total number of @see PdfPages.
     * @return total number of pages
     */
    public int getNumOfPages() {
        return pageRefs.size();
    }

    /**
     * Returns the index of the first occurrence of the specified page
     * in this tree, or 0 if this tree does not contain the page.
     */
    public int getPageNum(PdfPage page) {
        return pages.indexOf(page) + 1;
    }

    /**
     * Insert {@see PdfPage} into specific one-based position.
     *
     * @param index one-base index of the page
     * @param page {@see PdfPage}
     * @throws PdfException in case {@code page} has been flushed
     */
    public void addPage(int index, PdfPage page) throws PdfException {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        --index;
        if (index > pageRefs.size())
            throw new IndexOutOfBoundsException("index");
        if (index == pageRefs.size()) {
            addPage(page);
            return;
        }
        loadPage(index);
        page.makeIndirect(pdfDocument);
        int parentIndex = findPageParent(index);
        parents.get(parentIndex).addPage(index, page);
        correctPdfPagesFromProperty(parentIndex + 1, +1);
        pageRefs.add(index, page.getPdfObject());
        pages.add(index, page);
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
        PdfPage pdfPage = getPage(pageNum);
        if (internalRemovePage(--pageNum)) {
            if (!pdfPage.getPdfObject().isFlushed()) {
                pdfPage.getPdfObject().remove(PdfName.Parent);
            }
            pdfPage.getPdfObject().getIndirectReference().setFree();
            return pdfPage;
        } else {
            return null;
        }
    }

    /**
     * Removes the first occurrence of the specified page from this list,
     * if it is present. Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param pdfPage page to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified page
     */
    public boolean removePage(PdfPage pdfPage) throws PdfException {
        int pageNum = getPageNum(pdfPage) - 1;
        if (pageNum < 0)
            return false;
        if (!pdfPage.getPdfObject().isFlushed()) {
            pdfPage.getPdfObject().remove(PdfName.Parent);
        }
        pdfPage.getPdfObject().getIndirectReference().setFree();
        internalRemovePage(pageNum);
        return true;
    }

    /**
     * Generate PdfPages tree.
     *
     * @return root {@see PdfPages}
     * @throws PdfException in case empty document
     */
    protected  PdfObject generateTree() throws PdfException {
        if (pageRefs.isEmpty())
            throw new PdfException(PdfException.DocumentHasNoPages);
        for (int  i = 0; i < pageRefs.size(); i++) {
            pageRefs.set(i, null);
            pages.set(i, null);
        }

        while (parents.size() != 1) {
            ArrayList<PdfPages> nextParents = new ArrayList<PdfPages>();
            //dynamicLeafSize helps to avoid PdfPages leaf with only one page
            int dynamicLeafSize = leafSize;
            PdfPages current = null;
            for (int i = 0; i < parents.size(); i++) {
                PdfPages pages = parents.get(i);
                int pageCount = pages.getCount();
                if (i % dynamicLeafSize == 0) {
                    if (pageCount <= 1) {
                        dynamicLeafSize++;
                    } else {
                        current = new PdfPages(-1, pdfDocument);
                        nextParents.add(current);
                        dynamicLeafSize = leafSize;
                    }
                }
                current.addPages(pages);
            }
            parents = nextParents;
        }
        return parents.get(0).getPdfObject();
    }

    private void loadPage(int pageNum) throws PdfException {
        PdfDictionary targetPage = pageRefs.get(pageNum);
        if (targetPage != null)
            return;
        //if we go here, we have to split PdfPages that contains pageNum
        int parentIndex = findPageParent(pageNum);
        PdfPages parent = parents.get(parentIndex);
        PdfArray kids = parent.getKids();
        if (kids == null) {
            throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum+1);
        }
        int kidsCount = parent.getCount();
        // we should handle separated pages, it means every PdfArray kids must contain either PdfPage or PdfPages,
        // mix of PdfPage and PdfPages not allowed.
        boolean findPdfPages = false;
        // NOTE optimization? when we already found needed index
        for (int i = 0; i < kids.size(); i++) {
            PdfDictionary page = kids.getAsDictionary(i);
            if (page == null) {                                             // null values not allowed in pages tree.
                throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum+1);
            }
            PdfObject pageKids = page.get(PdfName.Kids);
            if (pageKids != null) {
                if (pageKids.getType() == PdfObject.Array) {
                    findPdfPages = true;
                } else {                                                    // kids must be of type array
                    throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum+1);
                }
            }
        }
        if (findPdfPages) {
            // handle mix of PdfPage and PdfPages.
            // handle count property!
            ArrayList<PdfPages> newParents = new ArrayList<PdfPages>(kids.size());
            PdfPages lastPdfPages = null;
            for (int i = 0; i < kids.size() && kidsCount > 0; i++) {
                PdfDictionary pdfPagesObject = kids.getAsDictionary(i);
                if (pdfPagesObject.getAsArray(PdfName.Kids) == null) {      // pdfPagesObject is PdfPage
                    if (lastPdfPages == null) {                             // possible if only first kid is PdfPage
                        lastPdfPages = new PdfPages(parent.getFrom(), pdfDocument);
                        newParents.add(lastPdfPages);
                    }
                    lastPdfPages.addPage(pdfPagesObject);
                    kidsCount--;
                } else {                                                    // pdfPagesObject is PdfPages
                    int from = lastPdfPages == null
                            ? parent.getFrom()
                            : lastPdfPages.getFrom() + lastPdfPages.getCount();
                    lastPdfPages = new PdfPages(from, pdfPagesObject, kidsCount);
                    newParents.add(lastPdfPages);
                    kidsCount -= lastPdfPages.getCount();
                }
            }
            parents.remove(parentIndex);
            for (int i = newParents.size() - 1; i >= 0; i--) {
                parents.add(parentIndex, newParents.get(i));
            }
            // recursive call, to load needed pageRef.
            // NOTE optimization? add to loadPage startParentIndex.
            loadPage(pageNum);
        } else {
            int from = parent.getFrom();
            // Possible exception in case kids.getSize() < parent.getCount().
            // In any case parent.getCount() has higher priority.
            // NOTE optimization? when we already found needed index
            for (int i = 0; i < parent.getCount(); i++) {
                pageRefs.set(from + i, kids.getAsDictionary(i));
            }
        }
    }

    // zero-based index
    private boolean internalRemovePage(int pageNum) throws PdfException {
        int parentIndex = findPageParent(pageNum);
        PdfPages pdfPages = parents.get(parentIndex);
        if (pdfPages.remove(pageNum)) {
            if (pdfPages.getCount() == 0 && parents.size() > 1) {
                parents.remove(parentIndex);
                --parentIndex;
            }
            correctPdfPagesFromProperty(parentIndex + 1, -1);
            pageRefs.remove(pageNum);
            pages.remove(pageNum);
            return true;
        } else {
            return false;
        }
    }

    // zero-based index
    private int findPageParent(int pageNum) throws PdfException {
        int low = 0;
        int high = parents.size() - 1;
        while (low != high) {
            int middle = (low + high + 1) / 2;
            if (parents.get(middle).compareTo(pageNum) > 0) {
                high = middle - 1;
            } else {
                low = middle;
            }
        }
        return low;
    }

    private void correctPdfPagesFromProperty(int index, int correction) {
        for (int i = index; i < parents.size(); i++) {
            if (parents.get(i) != null) {
                parents.get(i).correctFrom(correction);
            }
        }
    }

    protected int verifyIntegrity() {
        int from = 0;
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getFrom() != from)
                return i;
            from = parents.get(i).getFrom()+parents.get(i).getCount();
        }
        return -1;
    }
}
