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

    public PdfMerger(PdfDocument pdfDocument){
        this.pdfDocument = pdfDocument;
    }

    public void addPages(PdfDocument from, int fromPage, int toPage) throws PdfException {
        LinkedHashMap<PdfPage, PdfPage> page2page = new LinkedHashMap<PdfPage, PdfPage>();
        for (int pageNum = fromPage; pageNum <= toPage; pageNum++){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        createStructTreeRoot(from, page2page);
    }

    public void addPages(PdfDocument from, ArrayList<Integer> pages) throws PdfException {
        LinkedHashMap<PdfPage, PdfPage> page2page = new LinkedHashMap<PdfPage, PdfPage>();
        for (Integer pageNum : pages){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        createStructTreeRoot(from, page2page);
    }

    public void merge() throws PdfException {
        for (PdfPage page : pagesToCopy){
            pdfDocument.addPage(page);
        }
    }

    private void fillListOfPagesToCopy(PdfDocument from, int pageNum, LinkedHashMap<PdfPage, PdfPage> page2page) throws PdfException {
        PdfPage originalPage = from.getPage(pageNum);
        PdfPage newPage = originalPage.copy(pdfDocument);
        page2page.put(originalPage, newPage);
        pagesToCopy.add(newPage);
    }

    private void createStructTreeRoot(PdfDocument from, LinkedHashMap<PdfPage, PdfPage> page2page) throws PdfException {
        PdfStructTreeRoot structTreeRoot = from.getStructTreeRoot();
        if (structTreeRoot != null)
            structTreeRoot.copyToDocument(pdfDocument, page2page);
    }
}
