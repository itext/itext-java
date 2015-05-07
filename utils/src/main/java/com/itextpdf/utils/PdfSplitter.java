package com.itextpdf.utils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.*;

import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    private PdfDocument pdfDocument;

    /**
     * Creates a new instance of PdfSplitter class.
     *
     * @param pdfDocument the document to be split.
     */
    public PdfSplitter(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Splits the document by page numbers.
     *
     * @param pageNumbers   the numbers of pages from which another document is to be started.
     *                      If the first element is not 1, then 1 is implied (i.e. the first split document will start from page 1 in any case).
     * @param documentReady the event listener which is called when another document is ready.
     *                      You can close this document in this listener, for instance.
     * @throws PdfException
     */
    public void splitByPageNumbers(List<Integer> pageNumbers, IDocumentReadyListener documentReady) throws PdfException {
        int currentPageNumber = 1;

        for (int ind = 0; ind <= pageNumbers.size(); ind++) {
            int nextPageNumber = ind == pageNumbers.size() ? pdfDocument.getNumOfPages() + 1 : pageNumbers.get(ind);
            if (ind == 0 && nextPageNumber == 1)
                continue;

            PageRange currentPageRange = new PageRange().addPageSequence(currentPageNumber, nextPageNumber - 1);
            PdfDocument currentDocument = createPdfDocument(currentPageRange);
            pdfDocument.copyPages(currentPageNumber, nextPageNumber - 1, currentDocument);
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
     * @throws PdfException
     */
    public List<PdfDocument> splitByPageNumbers(List<Integer> pageNumbers) throws PdfException {
        final List<PdfDocument> splitDocuments = new ArrayList<PdfDocument>();

        splitByPageNumbers(pageNumbers, new IDocumentReadyListener() {
            @Override
            public void documentReady(PdfDocument pdfDocument, PageRange pageRange) {
                splitDocuments.add(pdfDocument);
            }
        });

        return splitDocuments;
    }

    /**
     * Splits a document into smaller documents with no more than @pageCount pages each.
     *
     * @param pageCount     the biggest possible number of pages in a split document.
     * @param documentReady the event listener which is called when another document is ready.
     *                      You can close this document in this listener, for instance.
     * @throws PdfException
     */
    public void splitByPageCount(int pageCount, IDocumentReadyListener documentReady) throws PdfException {
        for (int startPage = 1; startPage <= pdfDocument.getNumOfPages(); startPage += pageCount) {
            int endPage = Math.min(startPage + pageCount - 1, pdfDocument.getNumOfPages());

            PageRange currentPageRange = new PageRange().addPageSequence(startPage, endPage);
            PdfDocument currentDocument = createPdfDocument(currentPageRange);
            pdfDocument.copyPages(startPage, endPage, currentDocument);
            documentReady.documentReady(currentDocument, currentPageRange);
        }
    }

    /**
     * Splits a document into smaller documents with no more than @pageCount pages each.
     *
     * @param pageCount the biggest possible number of pages in a split document.
     * @return the list of resultant documents. By warned that they are not closed.
     * @throws PdfException
     */
    public List<PdfDocument> splitByPageCount(int pageCount) throws PdfException {
        final List<PdfDocument> splitDocuments = new ArrayList<PdfDocument>();

        splitByPageCount(pageCount, new IDocumentReadyListener() {
            @Override
            public void documentReady(PdfDocument pdfDocument, PageRange pageRange) {
                splitDocuments.add(pdfDocument);
            }
        });

        return splitDocuments;
    }

    /**
     * Extracts the specified page ranges from a document.
     *
     * @param pageRanges the list of page ranges for each of the resultant document.
     * @return the list of the resultant documents for each of the specified page range.
     * Be warned that these documents are not closed.
     * @throws PdfException
     */
    public List<PdfDocument> extractPageRanges(List<PageRange> pageRanges) throws PdfException {
        List<PdfDocument> splitDocuments = new ArrayList<PdfDocument>();

        for (PageRange currentPageRange : pageRanges) {
            PdfDocument currentPdfDocument = createPdfDocument(currentPageRange);
            splitDocuments.add(currentPdfDocument);
            pdfDocument.copyPages(currentPageRange.getAllPages(), currentPdfDocument);
        }

        return splitDocuments;
    }

    /**
     * Extracts the specified page ranges from a document.
     *
     * @param pageRange the page range to be extracted from the document.
     * @return the resultant document containing the pages specified by the provided page range.
     * Be warned that this document is not closed.
     * @throws PdfException
     */
    public PdfDocument extractPageRange(PageRange pageRange) throws PdfException {
        return extractPageRanges(Arrays.asList(pageRange)).get(0);
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * This method is called when another split document is to be created.
     * You can override this method and return your own {@see PdfWriter} depending on your needs.
     *
     * @param documentPageRange the page range of the original document to be included in the document being created now.
     * @return the PdfWriter instance for the document which is being created.
     */
    protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
        return new PdfWriter(new ByteArrayOutputStream());
    }

    private PdfDocument createPdfDocument(PageRange currentPageRange) throws PdfException {
        PdfDocument newDocument = new PdfDocument(getNextPdfWriter(currentPageRange));
        if (pdfDocument.isTagged())
            newDocument.setTagged();
        return newDocument;
    }

    public static interface IDocumentReadyListener {
        void documentReady(PdfDocument pdfDocument, PageRange pageRange);
    }

    public static class PageRange {

        private List<Integer> sequenceStarts = new ArrayList<Integer>();
        private List<Integer> sequenceEnds = new ArrayList<Integer>();

        public PageRange() {
        }

        /**
         * You can call specify the page range in a string form, for example: "1-12, 15, 45-66".
         *
         * @param pageRange the page range.
         */
        public PageRange(String pageRange) {
            pageRange = pageRange.replaceAll("\\s+", "");
            Pattern sequencePattern = Pattern.compile("(\\d+)-(\\d+)");
            Pattern singlePagePattern = Pattern.compile("(\\d+)");
            for (String pageRangePart : pageRange.split(",")) {
                Matcher matcher;
                if ((matcher = sequencePattern.matcher(pageRangePart)).matches()) {
                    sequenceStarts.add(Integer.parseInt(matcher.group(1)));
                    sequenceEnds.add(Integer.parseInt(matcher.group(2)));
                } else if ((matcher = singlePagePattern.matcher(pageRangePart)).matches()) {
                    int pageNumber = Integer.parseInt(matcher.group(1));
                    sequenceStarts.add(pageNumber);
                    sequenceEnds.add(pageNumber);
                }
            }
        }

        public PageRange addPageSequence(int startPageNumber, int endPageNumber) {
            sequenceStarts.add(startPageNumber);
            sequenceEnds.add(endPageNumber);
            return this;
        }

        public PageRange addSinglePage(int pageNumber) {
            sequenceStarts.add(pageNumber);
            sequenceEnds.add(pageNumber);
            return this;
        }

        public TreeSet<Integer> getAllPages() {
            TreeSet<Integer> allPages = new TreeSet<Integer>();
            for (int ind = 0; ind < sequenceStarts.size(); ind++) {
                for (int pageInRange = sequenceStarts.get(ind); pageInRange <= sequenceEnds.get(ind); pageInRange++) {
                    allPages.add(pageInRange);
                }
            }
            return allPages;
        }

        public boolean isPageInRange(int pageNumber) {
            for (int ind = 0; ind < sequenceStarts.size(); ind++) {
                if (sequenceStarts.get(ind) <= pageNumber && pageNumber <= sequenceEnds.get(ind))
                    return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageRange))
                return false;

            PageRange other = (PageRange) obj;
            return sequenceStarts.equals(other.sequenceStarts) && sequenceEnds.equals(other.sequenceEnds);
        }
    }

    /**
     * Split a document by outline title (bookmark name), find outline by name
     * and places the entire hierarchy in a separate document ( outlines and pages ) .
     *
     * @param outlineTitles list of outline titles .
     * @throws PdfException
     */
    public List<PdfDocument> splitByOutlines(List<String> outlineTitles) throws PdfException {

        if (outlineTitles == null || outlineTitles.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<PdfDocument> documentList = new ArrayList<PdfDocument>(outlineTitles.size());
        for (String title : outlineTitles) {
            PdfDocument document = splitByOutline(title);
            if (document != null) {
                documentList.add(document);
            }
        }

        return documentList;
    }

    public PdfDocument splitByOutline(String outlineTitle) throws PdfException {

        int startPage = -1;
        int endPage = -1;

        PdfDocument toDocument = createPdfDocument(null);

        int size = pdfDocument.getNumOfPages();
        for (int i = 1; i <= size; i++) {
            PdfPage pdfPage = pdfDocument.getPage(i);
            List<PdfOutline> outlineList = pdfPage.getOutlines(false);
            if (outlineList != null) {
                for (PdfOutline pdfOutline : outlineList) {
                    if (pdfOutline.getTitle().equals(outlineTitle)) {
                        startPage = pdfDocument.getPageNum(pdfPage);
                        pdfPage.getOutlines(false);
                        PdfOutline nextOutLine = getAbsoluteTreeNextOutline(pdfOutline);
                        if (nextOutLine != null) {
                            endPage = pdfDocument.getPageNum(getPageByOutline(i, nextOutLine)) - 1;
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
        pdfDocument.copyPages(startPage, endPage, toDocument);

        return toDocument;
    }

    public PdfPage getPageByOutline(int fromPage, PdfOutline outline) throws PdfException {
        int size = pdfDocument.getNumOfPages();
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
     * @throws PdfException
     */
    public PdfOutline getAbsoluteTreeNextOutline(PdfOutline outline) throws PdfException {

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
}
