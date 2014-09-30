package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.util.ArrayList;

/**
 * Algorithm for construction {@see PdfPages} tree
 */
class PdfPagesTree {

    private final int leafSize = 10;
    private ArrayList<PdfPage> pages;
    private ArrayList<PdfPages> parents;
    private PdfDocument doc;

    /**
     * Create PdfPages tree.
     *
     * @param doc {@see PdfDocument}
     */
    public PdfPagesTree(PdfDocument doc) {
        this.doc = doc;
        pages = new ArrayList<PdfPage>();
        parents = new ArrayList<PdfPages>();
        parents.add(new PdfPages(doc));
    }

    /**
     * Appends the specified {@see PdfPage} to the end of this tree.
     *
     * @param page {@see PdfPage}
     */
    public void addPage(PdfPage page) {
        PdfPages current = parents.get(parents.size() - 1);
        if (current.getPagesCount() % leafSize == 0 && pages.size() != 0) {
            parents.add(current = new PdfPages(doc));
        }
        current.addPage(page);
        page.put(PdfName.Parent, current);
        pages.add(page);
    }

    /**
     * Returns the {@see PdfPage} at the specified position in this list.
     *
     * @param  pageNum one-based index of the element to return
     * @return the {@see PdfPage} at the specified position in this list
     */
    public PdfPage getPage(int pageNum) {
        return pages.get(pageNum - 1);
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
        return pages.indexOf(page) + 1;
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
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted);
        --index;
        if (index > pages.size())
            throw new IndexOutOfBoundsException("index");
        if (index == pages.size()) {
            addPage(page);
            return;
        }
        PdfPages parent = findPageParent(pages.get(index));
        int subIndex = parent.getKids().indexOf(pages.get(index));
        //balancing between nearby PdfPages
        if (subIndex == 0 && index != 0) {
            PdfPages prevParent = (PdfPages)pages.get(index - 1).get(PdfName.Parent);
            if (prevParent.getPagesCount() < parent.getPagesCount()) {
                parent = prevParent;
                subIndex = prevParent.getPagesCount();
            }
        } else if (subIndex == parent.getPagesCount()) {
            PdfPages nextParent = (PdfPages)pages.get(index + 1).get(PdfName.Parent);
            if (nextParent.getPagesCount() < parent.getPagesCount()) {
                parent = nextParent;
                subIndex = 0;
            }
        }
        parent.insertPage(subIndex, page);
        page.put(PdfName.Parent, parent);
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
        --pageNum;
        PdfPage page = pages.get(pageNum);
        internalRemovePage(pageNum, page);
        return page;
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
        internalRemovePage(pageNum - 1, page);
        return true;
    }

    private void internalRemovePage(int pageNum, PdfPage page) throws PdfException {
        //TODO log removing of flushed page
        PdfPages parent = findPageParent(page);
        parent.getKids().remove(page);
        pages.remove(pageNum);
    }

    private PdfPages findPageParent(PdfPage page) {
        for (PdfPages parent: parents) {
            if (parent.getKids().contains(page))
                return parent;
        }
        return null;
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
            ArrayList<PdfPages> nextParents = new ArrayList<PdfPages>();
            //dynamicLeafSize helps to avoid PdfPages leaf with only one page
            int dynamicLeafSize = leafSize;
            PdfPages current = null;
            for (int i = 0; i < parents.size(); i++) {
                PdfPages page = parents.get(i);
                if (i % dynamicLeafSize == 0) {
                    if (page.getPagesCount() < 2) {
                        dynamicLeafSize++;
                    } else {
                        current = new PdfPages(doc);
                        nextParents.add(current);
                        dynamicLeafSize = leafSize;
                    }
                }
                page.put(PdfName.Parent, current);
                current.addPage(page);
            }
            parents = nextParents;
        }
        return parents.get(0);
    }
}
