package com.itextpdf.utils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSplitter {

    private PdfDocument pdfDocument;

    public PdfSplitter(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

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

    public void splitByPageCount(int pageCount, IDocumentReadyListener documentReady) throws PdfException {
        for (int startPage = 1; startPage <= pdfDocument.getNumOfPages(); startPage += pageCount) {
            int endPage = Math.min(startPage + pageCount - 1, pdfDocument.getNumOfPages());

            PageRange currentPageRange = new PageRange().addPageSequence(startPage, endPage);
            PdfDocument currentDocument = createPdfDocument(currentPageRange);
            pdfDocument.copyPages(startPage, endPage, currentDocument);
            documentReady.documentReady(currentDocument, currentPageRange);
        }
    }

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

    public List<PdfDocument> extractPageRanges(List<PageRange> pageRanges) throws PdfException {
        List<PdfDocument> splitDocuments = new ArrayList<PdfDocument>();

        for (PageRange currentPageRange : pageRanges) {
            PdfDocument currentPdfDocument = createPdfDocument(currentPageRange);
            splitDocuments.add(currentPdfDocument);
            pdfDocument.copyPages(currentPageRange.getAllPages(), currentPdfDocument);
        }

        return splitDocuments;
    }

    public PdfDocument extractPageRange(PageRange pageRange) throws PdfException {
        return extractPageRanges(Arrays.asList(pageRange)).get(0);
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

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

        public PageRange(String pageRange) {
            pageRange = pageRange.replaceAll("\\s+","");
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

        public Set<Integer> getAllPages() {
            Set<Integer> allPages = new HashSet<Integer>();
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
}
