package com.itextpdf.utils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfOutline;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
        if (pdfDocument.getWriter() != null) {
            throw new PdfException(PdfException.CannotSplitDocumentThatIsBeingWritten);
        }
        this.pdfDocument = pdfDocument;
    }

    /**
     * Splits the document basing on the given size.
     *
     * @param size <strog>Preferred</strog> size for splitting.
     * @return The documents which the source document was split into.
     *         Be warned that these documents are not closed.
     */
    public List<PdfDocument> splitBySize(long size) {
        List<PageRange> splitRanges = new ArrayList<PageRange>();
        int currentPage = 1;
        int numOfPages = pdfDocument.getNumOfPages();

        while (currentPage <= numOfPages) {
            PageRange nextRange = getNextRange(currentPage, numOfPages, size);
            splitRanges.add(nextRange);
            currentPage = nextRange.getAllPages().last() + 1;
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
     * @throws PdfException
     */
    public void splitByPageNumbers(List<Integer> pageNumbers, IDocumentReadyListener documentReady) {
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
    public List<PdfDocument> splitByPageNumbers(List<Integer> pageNumbers) {
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
    public void splitByPageCount(int pageCount, IDocumentReadyListener documentReady) {
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
    public List<PdfDocument> splitByPageCount(int pageCount) {
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
    public List<PdfDocument> extractPageRanges(List<PageRange> pageRanges) {
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
    public PdfDocument extractPageRange(PageRange pageRange) {
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

    private PdfDocument createPdfDocument(PageRange currentPageRange) {
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
    public List<PdfDocument> splitByOutlines(List<String> outlineTitles) {

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

    private PdfDocument splitByOutline(String outlineTitle) {

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

    private PdfPage getPageByOutline(int fromPage, PdfOutline outline) {
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
        long lengthWithoutXref = counter.getLength(null); // initialize with trailer length
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
        return 20l * (size + 1);
    }
}
