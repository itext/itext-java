/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Algorithm for construction {@link PdfPages} tree
 */
class PdfPagesTree {

    static final int DEFAULT_LEAF_SIZE = 10;

    private final int leafSize = DEFAULT_LEAF_SIZE;

    private ISimpleList<PdfIndirectReference> pageRefs;
    private List<PdfPages> parents;
    private ISimpleList<PdfPage> pages;
    private final PdfDocument document;
    private boolean generated = false;
    private PdfPages root;

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfPagesTree.class);

    /**
     * Creates a PdfPages tree.
     *
     * @param pdfCatalog a {@link PdfCatalog} which will be used to create the tree
     */
    public PdfPagesTree(PdfCatalog pdfCatalog) {
        this.document = pdfCatalog.getDocument();
        this.parents = new ArrayList<>();
        IPageTreeListFactory pageTreeFactory = document.getDiContainer().getInstance(IPageTreeListFactory.class);
        if (pdfCatalog.getPdfObject().containsKey(PdfName.Pages)) {
            PdfDictionary pages = pdfCatalog.getPdfObject().getAsDictionary(PdfName.Pages);
            if (pages == null) {
                throw new PdfException(
                        KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE_PAGES_MUST_BE_PDF_DICTIONARY);
            }
            this.pages = pageTreeFactory.<PdfPage>createList(pages);
            this.pageRefs = pageTreeFactory.<PdfIndirectReference>createList(pages);
            this.root = new PdfPages(0, Integer.MAX_VALUE, pages, null);
            parents.add(this.root);
            for (int i = 0; i < this.root.getCount(); i++) {
                this.pageRefs.add(null);
                this.pages.add(null);
            }
        } else {
            this.root = null;
            this.parents.add(new PdfPages(0, this.document));
            this.pages = pageTreeFactory.<PdfPage>createList(null);
            this.pageRefs = pageTreeFactory.<PdfIndirectReference>createList(null);
        }
        //in read mode we will create PdfPages from 0 to Count
        // and reserve null indexes for pageRefs and pages.
    }

    /**
     * Returns the {@link PdfPage} at the specified position in this list.
     *
     * @param pageNum one-based index of the element to return
     * @return the {@link PdfPage} at the specified position in this list
     */
    public PdfPage getPage(int pageNum) {
        if (pageNum < 1 || pageNum > getNumberOfPages()) {
            throw new IndexOutOfBoundsException(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS, pageNum));
        }
        --pageNum;
        PdfPage pdfPage = pages.get(pageNum);
        if (pdfPage == null) {
            loadPage(pageNum);
            if (pageRefs.get(pageNum) != null) {
                int parentIndex = findPageParent(pageNum);
                PdfObject pageObject = pageRefs.get(pageNum).getRefersTo();
                if (pageObject instanceof PdfDictionary) {
                    pdfPage = document.getPageFactory().createPdfPage((PdfDictionary) pageObject);
                    pdfPage.parentPages = parents.get(parentIndex);
                } else {
                    LOGGER.error(
                            MessageFormatUtil.format(IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE,
                                    pageNum + 1));
                }
            } else {
                LOGGER.error(MessageFormatUtil.format(IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE,
                        pageNum + 1));
            }
            pages.set(pageNum, pdfPage);
        }
        if (pdfPage == null) {
            throw new PdfException(
                    MessageFormatUtil.format(IoLogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE,
                            pageNum + 1));
        }
        return pdfPage;
    }

    /**
     * Returns the {@link PdfPage} by page's PdfDictionary.
     *
     * @param pageDictionary page's PdfDictionary
     * @return the {@code PdfPage} object, that wraps {@code pageDictionary}.
     */
    public PdfPage getPage(PdfDictionary pageDictionary) {
        int pageNum = getPageNumber(pageDictionary);
        if (pageNum > 0) {
            return getPage(pageNum);
        }

        return null;
    }

    /**
     * Gets total number of @see PdfPages.
     *
     * @return total number of pages
     */
    public int getNumberOfPages() {
        return pageRefs.size();
    }

    /**
     * Returns the index of the first occurrence of the specified page
     * in this tree, or 0 if this tree does not contain the page.
     */
    public int getPageNumber(PdfPage page) {
        return pages.indexOf(page) + 1;
    }

    /**
     * Returns the index of the first occurrence of the page in this tree
     * specified by it's PdfDictionary, or 0 if this tree does not contain the page.
     */
    public int getPageNumber(PdfDictionary pageDictionary) {
        int pageNum = pageRefs.indexOf(pageDictionary.getIndirectReference());
        if (pageNum >= 0) {
            return pageNum + 1;
        }
        for (int i = 0; i < pageRefs.size(); i++) {
            if (pageRefs.get(i) == null) {
                loadPage(i);
            }
            if (pageRefs.get(i).equals(pageDictionary.getIndirectReference())) {
                return i + 1;
            }
        }

        return 0;
    }

    /**
     * Appends the specified {@link PdfPage} to the end of this tree.
     *
     * @param pdfPage a {@link PdfPage} to be added
     */
    public void addPage(PdfPage pdfPage) {
        PdfPages pdfPages;
        if (root != null) {
            // in this case we save tree structure

            if (pageRefs.size() == 0) {
                pdfPages = root;
            } else {
                loadPage(pageRefs.size() - 1);
                pdfPages = parents.get(parents.size() - 1);
            }
        } else {
            pdfPages = parents.get(parents.size() - 1);
            if (pdfPages.getCount() % leafSize == 0 && pageRefs.size() > 0) {
                pdfPages = new PdfPages(pdfPages.getFrom() + pdfPages.getCount(), document);
                parents.add(pdfPages);
            }
        }

        pdfPage.makeIndirect(document);
        pdfPages.addPage(pdfPage.getPdfObject());
        pdfPage.parentPages = pdfPages;
        pageRefs.add(pdfPage.getPdfObject().getIndirectReference());
        pages.add(pdfPage);
    }

    /**
     * Inserts {@link PdfPage} into specific one-based position.
     *
     * @param index   one-base index of the page
     * @param pdfPage {@link PdfPage} to insert.
     */
    public void addPage(int index, PdfPage pdfPage) {
        --index;
        if (index > pageRefs.size()) {
            throw new IndexOutOfBoundsException("index");
        }
        if (index == pageRefs.size()) {
            addPage(pdfPage);
            return;
        }
        loadPage(index);
        pdfPage.makeIndirect(document);
        int parentIndex = findPageParent(index);
        PdfPages parentPages = parents.get(parentIndex);
        parentPages.addPage(index, pdfPage);
        pdfPage.parentPages = parentPages;
        correctPdfPagesFromProperty(parentIndex + 1, +1);
        pageRefs.add(index, pdfPage.getPdfObject().getIndirectReference());
        pages.add(index, pdfPage);
    }

    /**
     * Removes the page at the specified position in this tree.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param pageNum the one-based index of the PdfPage to be removed
     * @return the page that was removed from the list
     */
    public PdfPage removePage(int pageNum) {
        PdfPage pdfPage = getPage(pageNum);
        if (pdfPage.isFlushed()) {
            LOGGER.warn(IoLogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED);
        }
        if (internalRemovePage(--pageNum)) {
            return pdfPage;
        } else {
            return null;
        }
    }

    void releasePage(int pageNumber) {
        --pageNumber;
        if (pageRefs.get(pageNumber) != null && !pageRefs.get(pageNumber).checkState(PdfObject.FLUSHED)
                && !pageRefs.get(pageNumber).checkState(PdfObject.MODIFIED)
                && (pageRefs.get(pageNumber).getOffset() > 0 || pageRefs.get(pageNumber).getIndex() >= 0)) {
            pages.set(pageNumber, null);
        }
    }

    /**
     * Generate PdfPages tree.
     *
     * @return root {@link PdfPages}
     * @throws PdfException in case empty document
     */
    protected PdfObject generateTree() {
        if (pageRefs.size() == 0) {
            LOGGER.info(IoLogMessageConstant.ATTEMPT_TO_GENERATE_PDF_PAGES_TREE_WITHOUT_ANY_PAGES);
            document.addNewPage();
        }
        if (generated) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_PAGES_TREE_COULD_BE_GENERATED_ONLY_ONCE);
        }

        if (root == null) {
            while (parents.size() != 1) {
                List<PdfPages> nextParents = new ArrayList<>();
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
                            current = new PdfPages(-1, document);
                            nextParents.add(current);
                            dynamicLeafSize = leafSize;
                        }
                    }
                    current.addPages(pages);
                }
                parents = nextParents;
            }
            root = parents.get(0);
        }
        generated = true;
        return root.getPdfObject();
    }

    protected void clearPageRefs() {
        pageRefs = null;
        pages = null;
    }

    protected List<PdfPages> getParents() {
        return parents;
    }

    protected PdfPages getRoot() {
        return root;
    }

    protected PdfPages findPageParent(PdfPage pdfPage) {
        int pageNum = getPageNumber(pdfPage) - 1;
        int parentIndex = findPageParent(pageNum);
        return parents.get(parentIndex);
    }

    private void loadPage(int pageNum) {
        loadPage(pageNum, new HashSet<>());
    }

    /**
     * Load page from pages tree node structure
     *
     * @param pageNum          page number to load
     * @param processedParents set with already processed parents object reference numbers
     *                         if this method was called recursively to avoid infinite recursion.
     */
    private void loadPage(int pageNum, Set<PdfIndirectReference> processedParents) {
        PdfIndirectReference targetPage = pageRefs.get(pageNum);
        if (targetPage != null) {
            return;
        }

        //if we go here, we have to split PdfPages that contains pageNum
        int parentIndex = findPageParent(pageNum);
        PdfPages parent = parents.get(parentIndex);
        PdfIndirectReference parentIndirectReference = parent.getPdfObject().getIndirectReference();
        if (parentIndirectReference != null) {
            if (processedParents.contains(parentIndirectReference)) {
                throw new PdfException(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE)
                        .setMessageParams(pageNum + 1);
            } else {
                processedParents.add(parentIndirectReference);
            }
        }
        PdfArray kids = parent.getKids();
        if (kids == null) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE).setMessageParams(pageNum + 1);
        }
        int kidsCount = parent.getCount();

        // we should handle separated pages, it means every PdfArray kids must contain either PdfPage or PdfPages,
        // mix of PdfPage and PdfPages not allowed.
        boolean findPdfPages = false;

        // NOTE optimization? when we already found needed index
        for (int i = 0; i < kids.size(); i++) {
            PdfDictionary page = kids.getAsDictionary(i);

            // null values not allowed in pages tree.
            if (page == null) {
                throw new PdfException(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE)
                        .setMessageParams(pageNum + 1);
            }
            PdfObject pageKids = page.get(PdfName.Kids);
            if (pageKids != null) {
                if (pageKids.isArray()) {
                    findPdfPages = true;
                } else {
                    // kids must be of type array
                    throw new PdfException(KernelExceptionMessageConstant.INVALID_PAGE_STRUCTURE)
                            .setMessageParams(pageNum + 1);
                }
            }
            if (document.getReader().isMemorySavingMode() && !findPdfPages && parent.getFrom() + i != pageNum) {
                page.release();
            }
        }
        if (findPdfPages) {

            // handle mix of PdfPage and PdfPages.
            // handle count property!
            List<PdfPages> newParents = new ArrayList<>(kids.size());
            PdfPages lastPdfPages = null;
            for (int i = 0; i < kids.size() && kidsCount > 0; i++) {
                /*
                 * We don't release pdfPagesObject in the end of each loop because we enter this for-cycle only when
                 * parent has PdfPages kids.
                 * If all of the kids are PdfPages, then there's nothing to release, because we don't release
                 * PdfPages at this point.
                 * If there are kids that are instances of PdfPage, then there's no sense in releasing them:
                 * in this case ParentTreeStructure is being rebuilt by inserting an intermediate PdfPages between
                 * the parent and a PdfPage,
                 * thus modifying the page object by resetting its parent, thus making it impossible to release the
                 * object.
                 */
                PdfDictionary pdfPagesObject = kids.getAsDictionary(i);
                if (pdfPagesObject.getAsArray(PdfName.Kids) == null) {
                    // pdfPagesObject is PdfPage

                    // possible if only first kid is PdfPage
                    if (lastPdfPages == null) {

                        lastPdfPages = new PdfPages(parent.getFrom(), document, parent);
                        kids.set(i, lastPdfPages.getPdfObject());
                        newParents.add(lastPdfPages);
                    } else {

                        // Only remove from kids if we did not replace the entry with new PdfPages
                        kids.remove(i);
                        i--;
                    }

                    // decrement count first so that page is not counted twice when moved to lastPdfPages
                    parent.decrementCount();
                    lastPdfPages.addPage(pdfPagesObject);
                    kidsCount--;
                } else {
                    // pdfPagesObject is PdfPages

                    int from = lastPdfPages == null
                            ? parent.getFrom()
                            : lastPdfPages.getFrom() + lastPdfPages.getCount();
                    lastPdfPages = new PdfPages(from, kidsCount, pdfPagesObject, parent);
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
            loadPage(pageNum, processedParents);
        } else {
            int from = parent.getFrom();

            // NOTE optimization? when we already found needed index
            final int pageCount = Math.min(parent.getCount(), kids.size());
            for (int i = 0; i < pageCount; i++) {
                PdfObject kid = kids.get(i, false);
                if (kid instanceof PdfIndirectReference) {
                    pageRefs.set(from + i, (PdfIndirectReference) kid);
                } else {
                    pageRefs.set(from + i, kid.getIndirectReference());
                }

            }
        }
    }

    // zero-based index
    private boolean internalRemovePage(int pageNum) {
        int parentIndex = findPageParent(pageNum);
        PdfPages pdfPages = parents.get(parentIndex);
        if (pdfPages.removePage(pageNum)) {
            if (pdfPages.getCount() == 0) {
                parents.remove(parentIndex);
                pdfPages.removeFromParent();
                --parentIndex;
            }
            if (parents.size() == 0) {
                root = null;
                parents.add(new PdfPages(0, document));
            } else {
                correctPdfPagesFromProperty(parentIndex + 1, -1);
            }
            pageRefs.remove(pageNum);
            pages.remove(pageNum);
            return true;
        } else {
            return false;
        }
    }

    // zero-based index
    private int findPageParent(int pageNum) {
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
}

