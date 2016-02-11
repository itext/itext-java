package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: make PdfMerger use PdfDocument#copyPagesTo to avoid code duplication as copyLinkAnnotations method
 */
public class PdfMerger {

    private PdfDocument pdfDocument;
    private List<PdfPage> pagesToCopy = new ArrayList<>();

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
        Map<PdfPage, PdfPage> page2page = new LinkedHashMap<>();
        for (int pageNum = fromPage; pageNum <= toPage; pageNum++){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        copyLinkAnnotations(from, page2page);
        createStructTreeRoot(from, page2page);
    }

    /**
     * This method adds pages from the source document to the List of pages which will be merged.
     * @param from - document, from which pages will be copied.
     * @param pages - List of numbers of pages which will be copied.
     * @throws PdfException
     */
    public void addPages(PdfDocument from, List<Integer> pages) {
        Map<PdfPage, PdfPage> page2page = new LinkedHashMap<>();
        for (Integer pageNum : pages){
            fillListOfPagesToCopy(from, pageNum, page2page);
        }
        copyLinkAnnotations(from, page2page);
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
    private void fillListOfPagesToCopy(PdfDocument from, int pageNum, Map<PdfPage, PdfPage> page2page) {
        PdfPage originalPage = from.getPage(pageNum);
        PdfPage newPage = originalPage.copyTo(pdfDocument);
        page2page.put(originalPage, newPage);
        pagesToCopy.add(newPage);
    }

    /**
     * This method creates StructTreeRoot in the new document.
     * @param from - document, from which pages will be copied.
     * @param page2page - map, which contains original page as a key and new page of the new document as a value. This map is used to create StructTreeRoot in the new document.
     * @throws PdfException
     */
    private void createStructTreeRoot(PdfDocument from, Map<PdfPage, PdfPage> page2page) {
        PdfStructTreeRoot structTreeRoot = from.getStructTreeRoot();
        if (structTreeRoot != null)
            structTreeRoot.copyTo(pdfDocument, page2page);
    }

    private void copyLinkAnnotations(PdfDocument fromDocument, Map<PdfPage, PdfPage> page2page) {
        List<PdfName> excludedKeys = new ArrayList<>();
        excludedKeys.add(PdfName.Dest);
        // It's important not to copy P key, as if the annotation won't be added to the page, P key could be used to identify this case
        excludedKeys.add(PdfName.P);
        for (Map.Entry<PdfPage, PdfPage> entry : page2page.entrySet()) {
            for (PdfAnnotation annot : entry.getKey().getAnnotations()) {
                PdfDestination d = null;

                if (annot.getSubtype().equals(PdfName.Link)) {
                    PdfObject dest = ((PdfLinkAnnotation) annot).getDestinationObject();

                    if (dest != null) {
                        d = transformToExplicitDestination(fromDocument, dest, page2page);
                    }

                    boolean hasGoToAction = false;
                    PdfDictionary a = annot.getAction();
                    if (a != null && PdfName.GoTo.equals(a.get(PdfName.S))) {
                        if (d == null) {
                            d = transformToExplicitDestination(fromDocument, a.get(PdfName.D), page2page);
                        }
                        hasGoToAction = true;
                    }

                    if (d != null ||  a != null && !hasGoToAction) {
                        PdfLinkAnnotation newAnnot = PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(pdfDocument, excludedKeys, false));
                        newAnnot.setDestination(d);
                        if (hasGoToAction) {
                            newAnnot.remove(PdfName.A);
                        }
                        page2page.get(entry.getKey()).addAnnotation(-1, newAnnot, false);
                    }
                }
            }
        }
    }

    private PdfDestination transformToExplicitDestination(PdfDocument fromDocument, PdfObject dest, Map<PdfPage, PdfPage> page2page) {
        PdfDestination d = null;
        if (dest.isArray()) {
            PdfObject pageObject = ((PdfArray)dest).get(0);
            for (PdfPage oldPage : page2page.keySet()) {
                if (oldPage.getPdfObject() == pageObject) {
                    PdfArray array = new PdfArray((PdfArray)dest);
                    array.set(0, page2page.get(oldPage).getPdfObject());
                    d = new PdfExplicitDestination(array);
                }
            }
        } else if (dest.isString()) {
            PdfArray array = (PdfArray) fromDocument.getCatalog().getNamedDestinations().get(((PdfString) dest).toUnicodeString());
            if (array != null) {
                PdfObject pageObject = array.get(0);
                for (PdfPage oldPage : page2page.keySet()) {
                    if (oldPage.getPdfObject() == pageObject) {
                        array.set(0, page2page.get(oldPage).getPdfObject());
                        d = new PdfExplicitDestination(array);
                    }
                }
            }
        }

        return d;
    }
}
