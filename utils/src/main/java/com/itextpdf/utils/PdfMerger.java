package com.itextpdf.utils;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.tagging.PdfStructTreeRoot;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PdfMerger {

    private PdfDocument pdfDocument;
    private ArrayList<PdfPage> pagesToCopy = new ArrayList<PdfPage>();

    /**
     * This class is used to merge a number of existing documents into one;
     * @param pdfDocument - the document into which source documents will be merged.
     */
    public PdfMerger(PdfDocument pdfDocument){
        this.pdfDocument = pdfDocument;
    }

    /**
     * This method adds pages from the source document to the List of pages which will be merged.
     * @param from - document, from which pages will be copied.
     * @param fromPage - start page in the range of pages to be copied.
     * @param toPage - end page in the range to be copied.
     * @throws PdfException
     */
    public void addPages(PdfDocument from, int fromPage, int toPage) {
        LinkedHashMap<PdfPage, PdfPage> page2page = new LinkedHashMap<PdfPage, PdfPage>();
        for (int pageNum = fromPage; pageNum <= toPage; pageNum++){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        createStructTreeRoot(from, page2page);
    }

    /**
     * This method adds pages from the source document to the List of pages which will be merged.
     * @param from - document, from which pages will be copied.
     * @param pages - List of numbers of pages which will be copied.
     * @throws PdfException
     */
    public void addPages(PdfDocument from, ArrayList<Integer> pages) {
        LinkedHashMap<PdfPage, PdfPage> page2page = new LinkedHashMap<PdfPage, PdfPage>();
        for (Integer pageNum : pages){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        createStructTreeRoot(from, page2page);
    }

    /**
     * This method gets all pages from the List of pages to be copied and merges them into one document.
     * @throws PdfException
     */
    public void merge() {
        for (PdfPage page : pagesToCopy){
            pdfDocument.addPage(page);
        }
    }

    /**
     * This method fills the List of pages to be copied with given page.
     * @param from - document, from which pages will be copied.
     * @param pageNum - number of page to be copied.
     * @param page2page - map, which contains original page as a key and new page of the new document as a value. This map is used to create StructTreeRoot in the new document.
     * @throws PdfException
     */
    private void fillListOfPagesToCopy(PdfDocument from, int pageNum, LinkedHashMap<PdfPage, PdfPage> page2page) {
        PdfPage originalPage = from.getPage(pageNum);
        PdfPage newPage = originalPage.copy(pdfDocument);
        page2page.put(originalPage, newPage);
        pagesToCopy.add(newPage);
    }

    /**
     * This method creates StructTreeRoot in the new document.
     * @param from - document, from which pages will be copied.
     * @param page2page - map, which contains original page as a key and new page of the new document as a value. This map is used to create StructTreeRoot in the new document.
     * @throws PdfException
     */
    private void createStructTreeRoot(PdfDocument from, LinkedHashMap<PdfPage, PdfPage> page2page) {
        PdfStructTreeRoot structTreeRoot = from.getStructTreeRoot();
        if (structTreeRoot != null)
            structTreeRoot.copyToDocument(pdfDocument, page2page);
    }
}
