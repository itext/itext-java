/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PdfSplitter {

    private PdfDocument pdfDocument;
    private boolean preserveTagged;
    private boolean preserveOutlines;
    private IMetaInfo metaInfo;

    /**
     * Creates a new instance of PdfSplitter class.
     *
     * @param pdfDocument the document to be split.
     */
    public PdfSplitter(PdfDocument pdfDocument) {
        if (pdfDocument.getWriter() != null) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_SPLIT_DOCUMENT_THAT_IS_BEING_WRITTEN);
        }
        this.pdfDocument = pdfDocument;
        this.preserveTagged = true;
        this.preserveOutlines = true;
    }

    /**
     * Sets the {@link IMetaInfo} that will be used during {@link PdfDocument} creation.
     *
     * @param metaInfo meta info to set
     */
    public void setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * If original document is tagged, then by default all resultant document will also be tagged.
     * This could be changed with this flag - if set to false, resultant documents will be not tagged, even if
     * original document is tagged.
     *
     * @param preserveTagged defines whether the resultant documents need to be tagged
     */
    public void setPreserveTagged(boolean preserveTagged) {
        this.preserveTagged = preserveTagged;
    }

    /**
     * If original document has outlines, then by default all resultant document will also have outlines.
     * This could be changed with this flag - if set to false, resultant documents won't contain outlines, even if
     * original document had them.
     *
     * @param preserveOutlines defines whether the resultant documents will preserve outlines or not
     */
    public void setPreserveOutlines(boolean preserveOutlines) {
        this.preserveOutlines = preserveOutlines;
    }

    /**
     * Splits the document basing on the given size specified in bytes.
     *
     * @param size <strong>Preferred</strong> size specified in bytes for splitting.
     *
     * @return The documents which the source document was split into.
     * Be warned that these documents are not closed.
     */
    public List<PdfDocument> splitBySize(long size) {
        List<PageRange> splitRanges = new ArrayList<>();
        int currentPage = 1;
        int numOfPages = pdfDocument.getNumberOfPages();

        while (currentPage <= numOfPages) {
            PageRange nextRange = getNextRange(currentPage, numOfPages, size);
            splitRanges.add(nextRange);
            List<Integer> allPages = nextRange.getQualifyingPageNums(numOfPages);
            currentPage = (int) allPages.get(allPages.size() - 1) + 1;
        }

        return extractPageRanges(splitRanges);
    }

    /**
     * Splits the document by page numbers.
     *
     * @param pageNumbers   the numbers of pages from which another document is to be started.
     *                      If the first element is not 1, then 1 is implied (i.e. the first split document will start from page 1 in any case).
     * @param documentReady the event listener which is called when another document is ready.
     *                      You can close this document in this listener, for instance.
     */
    public void splitByPageNumbers(List<Integer> pageNumbers, IDocumentReadyListener documentReady) {
        int currentPageNumber = 1;

        for (int ind = 0; ind <= pageNumbers.size(); ind++) {
            int nextPageNumber = ind == pageNumbers.size() ? pdfDocument.getNumberOfPages() + 1 : (int) pageNumbers.get(ind);
            if (ind == 0 && nextPageNumber == 1)
                continue;

            PageRange currentPageRange = new PageRange().addPageSequence(currentPageNumber, nextPageNumber - 1);
            PdfDocument currentDocument = createPdfDocument(currentPageRange);
            pdfDocument.copyPagesTo(currentPageNumber, nextPageNumber - 1, currentDocument);
            documentReady.documentReady(currentDocument, currentPageRange);

            currentPageNumber = nextPageNumber;
        }
    }

    /**
     * Splits the document by page numbers.
     *
     * @param pageNumbers the numbers of pages from which another document is to be started.
     *                    If the first element is not 1, then 1 is implied (i.e. the first split document will start from page 1 in any case).
     * @return the list of resultant documents. By warned that they are not closed.
     */
    public List<PdfDocument> splitByPageNumbers(List<Integer> pageNumbers) {
        final List<PdfDocument> splitDocuments = new ArrayList<>();

        splitByPageNumbers(pageNumbers, new SplitReadyListener(splitDocuments));

        return splitDocuments;
    }

    /**
     * Splits a document into smaller documents with no more than @pageCount pages each.
     *
     * @param pageCount     the biggest possible number of pages in a split document.
     * @param documentReady the event listener which is called when another document is ready.
     *                      You can close this document in this listener, for instance.
     */
    public void splitByPageCount(int pageCount, IDocumentReadyListener documentReady) {
        for (int startPage = 1; startPage <= pdfDocument.getNumberOfPages(); startPage += pageCount) {
            int endPage = Math.min(startPage + pageCount - 1, pdfDocument.getNumberOfPages());

            PageRange currentPageRange = new PageRange().addPageSequence(startPage, endPage);
            PdfDocument currentDocument = createPdfDocument(currentPageRange);
            pdfDocument.copyPagesTo(startPage, endPage, currentDocument);
            documentReady.documentReady(currentDocument, currentPageRange);
        }
    }

    /**
     * Splits a document into smaller documents with no more than @pageCount pages each.
     *
     * @param pageCount the biggest possible number of pages in a split document.
     *
     * @return the list of resultant documents. By warned that they are not closed.
     */
    public List<PdfDocument> splitByPageCount(int pageCount) {
        final List<PdfDocument> splitDocuments = new ArrayList<>();

        splitByPageCount(pageCount, new SplitReadyListener(splitDocuments));

        return splitDocuments;
    }

    /**
     * Extracts the specified page ranges from a document.
     *
     * @param pageRanges the list of page ranges for each of the resultant document.
     *
     * @return the list of the resultant documents for each of the specified page range.
     * Be warned that these documents are not closed.
     */
    public List<PdfDocument> extractPageRanges(List<PageRange> pageRanges) {
        List<PdfDocument> splitDocuments = new ArrayList<>();

        for (PageRange currentPageRange : pageRanges) {
            PdfDocument currentPdfDocument = createPdfDocument(currentPageRange);
            splitDocuments.add(currentPdfDocument);
            pdfDocument.copyPagesTo(currentPageRange.getQualifyingPageNums(pdfDocument.getNumberOfPages()), currentPdfDocument);
        }

        return splitDocuments;
    }

    /**
     * Extracts the specified page ranges from a document.
     *
     * @param pageRange the page range to be extracted from the document.
     *
     * @return the resultant document containing the pages specified by the provided page range.
     * Be warned that this document is not closed.
     */
    public PdfDocument extractPageRange(PageRange pageRange) {
        return extractPageRanges(Collections.singletonList(pageRange)).get(0);
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * This method is called when another split document is to be created.
     * You can override this method and return your own {@link PdfWriter} depending on your needs.
     *
     * @param documentPageRange the page range of the original document to be included
     *                          in the document being created now.
     *
     * @return the PdfWriter instance for the document which is being created.
     */
    protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
        return new PdfWriter(new ByteArrayOutputStream());
    }

    private PdfDocument createPdfDocument(PageRange currentPageRange) {
        PdfDocument newDocument = new PdfDocument(getNextPdfWriter(currentPageRange), new DocumentProperties().setEventCountingMetaInfo(metaInfo));
        if (pdfDocument.isTagged() && preserveTagged)
            newDocument.setTagged();
        if (pdfDocument.hasOutlines() && preserveOutlines)
            newDocument.initializeOutlines();
        return newDocument;
    }

    public interface IDocumentReadyListener {
        void documentReady(PdfDocument pdfDocument, PageRange pageRange);
    }

    /**
     * Split a document by outline title (bookmark name), find outline by name
     * and places the entire hierarchy in a separate document ( outlines and pages ) .
     *
     * @param outlineTitles list of outline titles .
     *
     * @return Collection of {@link PdfDocument} which contains split parts of a document
     */
    public List<PdfDocument> splitByOutlines(List<String> outlineTitles) {
        if (outlineTitles == null || outlineTitles.size() == 0) {
            return Collections.<PdfDocument>emptyList();
        }

        List<PdfDocument> documentList = new ArrayList<>(outlineTitles.size());
        for (String title : outlineTitles) {
            PdfDocument document = splitByOutline(title);
            if (document != null) {
                documentList.add(document);
            }
        }

        return documentList;
    }

    private PdfDocument splitByOutline(String outlineTitle) {

        int startPage = -1;
        int endPage = -1;

        PdfDocument toDocument = createPdfDocument(null);

        int size = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= size; i++) {
            PdfPage pdfPage = pdfDocument.getPage(i);
            List<PdfOutline> outlineList = pdfPage.getOutlines(false);
            if (outlineList != null) {
                for (PdfOutline pdfOutline : outlineList) {
                    if (pdfOutline.getTitle().equals(outlineTitle)) {
                        startPage = pdfDocument.getPageNumber(pdfPage);
                        PdfOutline nextOutLine = getAbsoluteTreeNextOutline(pdfOutline);
                        if (nextOutLine != null) {
                            endPage = pdfDocument.getPageNumber(getPageByOutline(i, nextOutLine)) - 1;
                        } else {
                            endPage = size;
                        }
                        // fix case: if two sequential bookmark point to one page
                        if (startPage - endPage == 1) {
                            endPage = startPage;
                        }
                        break;
                    }
                }
            }
        }

        if (startPage == -1 || endPage == -1) {
            return null;
        }
        pdfDocument.copyPagesTo(startPage, endPage, toDocument);

        return toDocument;
    }

    private PdfPage getPageByOutline(int fromPage, PdfOutline outline) {
        int size = pdfDocument.getNumberOfPages();
        for (int i = fromPage; i <= size; i++) {
            PdfPage pdfPage = pdfDocument.getPage(i);
            List<PdfOutline> outlineList = pdfPage.getOutlines(false);
            if (outlineList != null) {
                for (PdfOutline pdfOutline : outlineList) {
                    if (pdfOutline.equals(outline)) {
                        return pdfPage;
                    }
                }
            }
        }
        return null;
    }

    /**
     * the next element in the entire hierarchy
     *
     * @param outline *
     */
    private PdfOutline getAbsoluteTreeNextOutline(PdfOutline outline) {

        PdfObject nextPdfObject = outline.getContent().get(PdfName.Next);
        PdfOutline nextPdfOutline = null;

        if (outline.getParent() != null && nextPdfObject != null) {
            for (PdfOutline pdfOutline : outline.getParent().getAllChildren()) {
                if (pdfOutline.getContent().getIndirectReference().equals(nextPdfObject.getIndirectReference())) {
                    nextPdfOutline = pdfOutline;
                    break;
                }
            }
        }
        if (nextPdfOutline == null && outline.getParent() != null) {
            nextPdfOutline = getAbsoluteTreeNextOutline(outline.getParent());
        }
        return nextPdfOutline;
    }

    private PageRange getNextRange(int startPage, int endPage, long size) {
        PdfResourceCounter counter = new PdfResourceCounter(pdfDocument.getTrailer());
        Map<Integer, PdfObject> resources = counter.getResources();
        // initialize with trailer length
        long lengthWithoutXref = counter.getLength(null);
        int currentPage = startPage;
        boolean oversized = false;

        do {
            PdfPage page = pdfDocument.getPage(currentPage++);
            counter = new PdfResourceCounter(page.getPdfObject());
            lengthWithoutXref += counter.getLength(resources);
            resources.putAll(counter.getResources());

            if (lengthWithoutXref + xrefLength(resources.size()) > size) {
                oversized = true;
            }
        } while (currentPage <= endPage && !oversized);

        // true if at least the first page to be copied didn't cause the oversize
        if (oversized && (currentPage - 1) != startPage) {
            // we shouldn't copy previous page because it caused
            // the oversize and it isn't the first page to be copied
            --currentPage;
        }

        return new PageRange().addPageSequence(startPage, currentPage - 1);
    }

    private long xrefLength(int size) {
        return 20L * (size + 1);
    }

    private static final class SplitReadyListener implements IDocumentReadyListener {

        private List<PdfDocument> splitDocuments;

        public SplitReadyListener(List<PdfDocument> splitDocuments) {
            this.splitDocuments = splitDocuments;
        }

        @Override
        public void documentReady(PdfDocument pdfDocument, PageRange pageRange) {
            splitDocuments.add(pdfDocument);
        }
    }
}
