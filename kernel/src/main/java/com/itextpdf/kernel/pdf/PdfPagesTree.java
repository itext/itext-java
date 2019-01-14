/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm for construction {@link PdfPages} tree
 */
class PdfPagesTree implements Serializable {

    private static final long serialVersionUID = 4189501363348296036L;

    private final int leafSize = 10;

    private List<PdfDictionary> pageRefs;
    private List<PdfPages> parents;
    private List<PdfPage> pages;
    private PdfDocument document;
    private boolean generated = false;
    private PdfPages root;

    /**
     * Create PdfPages tree.
     *
     * @param pdfCatalog {@see PdfCatalog}
     */
    public PdfPagesTree(PdfCatalog pdfCatalog) {
        this.document = pdfCatalog.getDocument();
        this.pageRefs = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.pages = new ArrayList<>();
        if (pdfCatalog.getPdfObject().containsKey(PdfName.Pages)) {
            PdfDictionary pages = pdfCatalog.getPdfObject().getAsDictionary(PdfName.Pages);
            if (pages == null)
                throw new PdfException(PdfException.InvalidPageStructurePagesPagesMustBePdfDictionary);
            this.root = new PdfPages(0, Integer.MAX_VALUE, pages, null);
            parents.add(this.root);
            for (int i = 0; i < this.root.getCount(); i++) {
                this.pageRefs.add(null);
                this.pages.add(null);
            }
        } else {
            this.root = null;
            this.parents.add(new PdfPages(0, this.document));
        }
        //in read mode we will create PdfPages from 0 to Count
        // and reserve null indexes for pageRefs and pages.
    }

    /**
     * Returns the {@see PdfPage} at the specified position in this list.
     *
     * @param pageNum one-based index of the element to return
     * @return the {@see PdfPage} at the specified position in this list
     */
    public PdfPage getPage(int pageNum) {
        if (pageNum < 1 || pageNum > getNumberOfPages()) {
            throw new IndexOutOfBoundsException(MessageFormatUtil.format(PdfException.RequestedPageNumberIsOutOfBounds, pageNum));
        }
        --pageNum;
        PdfPage pdfPage = pages.get(pageNum);
        if (pdfPage == null) {
            loadPage(pageNum);
            if (pageRefs.get(pageNum) != null) {
                int parentIndex = findPageParent(pageNum);
                pdfPage = new PdfPage(pageRefs.get(pageNum));
                pdfPage.parentPages = parents.get(parentIndex);
            } else {
                LoggerFactory.getLogger(getClass()).error(MessageFormatUtil.format(LogMessageConstant.PAGE_TREE_IS_BROKEN_FAILED_TO_RETRIEVE_PAGE, pageNum + 1));
            }
            pages.set(pageNum, pdfPage);
        }
        return pdfPage;
    }

    /**
     * Returns the {@see PdfPage} by page's PdfDictionary.
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
        int pageNum = pageRefs.indexOf(pageDictionary);
        if (pageNum >= 0) {
            return pageNum + 1;
        }
        for (int i = 0; i < pageRefs.size(); i++) {
            if (pageRefs.get(i) == null) {
                loadPage(i);
            }
            if (pageRefs.get(i).equals(pageDictionary)) {
                return i + 1;
            }
        }

        return 0;
    }

    /**
     * Appends the specified {@see PdfPage} to the end of this tree.
     *
     * @param pdfPage {@see PdfPage}
     */
    public void addPage(PdfPage pdfPage) {
        PdfPages pdfPages;
        if (root != null) { // in this case we save tree structure
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
        pageRefs.add(pdfPage.getPdfObject());
        pages.add(pdfPage);
    }

    /**
     * Insert {@see PdfPage} into specific one-based position.
     *
     * @param index   one-base index of the page
     * @param pdfPage {@link PdfPage} to insert.
     */
    public void addPage(int index, PdfPage pdfPage) {
        --index;
        if (index > pageRefs.size())
            throw new IndexOutOfBoundsException("index");
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
        pageRefs.add(index, pdfPage.getPdfObject());
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
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED);
        }
        if (internalRemovePage(--pageNum)) {
            return pdfPage;
        } else {
            return null;
        }
    }

    /**
     * Generate PdfPages tree.
     *
     * @return root {@link PdfPages}
     * @throws PdfException in case empty document
     */
    protected PdfObject generateTree() {
        if (pageRefs.size() == 0)
            throw new PdfException(PdfException.DocumentHasNoPages);
        if (generated)
            throw new PdfException(PdfException.PdfPagesTreeCouldBeGeneratedOnlyOnce);

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
                    assert current != null;
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
        PdfDictionary targetPage = pageRefs.get(pageNum);
        if (targetPage != null)
            return;
        //if we go here, we have to split PdfPages that contains pageNum
        int parentIndex = findPageParent(pageNum);
        PdfPages parent = parents.get(parentIndex);
        PdfArray kids = parent.getKids();
        if (kids == null) {
            throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum + 1);
        }
        int kidsCount = parent.getCount();
        // we should handle separated pages, it means every PdfArray kids must contain either PdfPage or PdfPages,
        // mix of PdfPage and PdfPages not allowed.
        boolean findPdfPages = false;
        // NOTE optimization? when we already found needed index
        for (int i = 0; i < kids.size(); i++) {
            PdfDictionary page = kids.getAsDictionary(i);
            if (page == null) {                                             // null values not allowed in pages tree.
                throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum + 1);
            }
            PdfObject pageKids = page.get(PdfName.Kids);
            if (pageKids != null) {
                if (pageKids.isArray()) {
                    findPdfPages = true;
                } else {                                                    // kids must be of type array
                    throw new PdfException(PdfException.InvalidPageStructure1).setMessageParams(pageNum + 1);
                }
            }
        }
        if (findPdfPages) {
            // handle mix of PdfPage and PdfPages.
            // handle count property!
            List<PdfPages> newParents = new ArrayList<>(kids.size());
            PdfPages lastPdfPages = null;
            for (int i = 0; i < kids.size() && kidsCount > 0; i++) {
                PdfDictionary pdfPagesObject = kids.getAsDictionary(i);
                if (pdfPagesObject.getAsArray(PdfName.Kids) == null) {      // pdfPagesObject is PdfPage
                    if (lastPdfPages == null) {                             // possible if only first kid is PdfPage
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
                } else {                                                    // pdfPagesObject is PdfPages
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
