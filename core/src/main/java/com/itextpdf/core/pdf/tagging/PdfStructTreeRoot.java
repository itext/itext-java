package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> {

    public PdfStructTreeRoot(PdfDocument document) throws PdfException {
        this(new PdfDictionary(), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    public PdfStructTreeRoot(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfArray getKids() throws PdfException {
        PdfArray k = getPdfObject().getAsArray(PdfName.K);
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
        }
        return k;
    }

    public void addKid(PdfStructElem structElem) throws PdfException {
        addKid(structElem.getPdfObject());
    }

    public void addKid(PdfDictionary structElem) throws PdfException {
        getKids().add(structElem);
        if (PdfName.StructElem.equals(structElem.getAsName(PdfName.Type)))
            structElem.put(PdfName.P, getPdfObject());
    }

    @Override
    public void flush() throws PdfException {

        Map<PdfObject, PageAndNums> nums = new LinkedHashMap<PdfObject, PageAndNums>();
        for (int i = 1; i <= getDocument().getNumOfPages(); i++) {
            PdfPage page = getDocument().getPage(i);
            Integer structParentIndex = page.getStructParentIndex();
            if (structParentIndex != null)
                nums.put(page.getPdfObject(), new PageAndNums(page, new PdfArray()));
        }
        PdfArray kids = getKids();
        for (int i = 0; i < kids.size(); i++) {
            if (kids.get(i) instanceof PdfDictionary)
                fillNums(nums, (PdfDictionary)kids.get(i));
        }


        PdfDictionary parentTree = new PdfDictionary().makeIndirect(getDocument());
        getPdfObject().put(PdfName.ParentTree, parentTree);
        PdfArray numsArray = new PdfArray().makeIndirect(getDocument());
        for (Map.Entry<PdfObject, PageAndNums> entry : nums.entrySet()) {
            numsArray.add(new PdfNumber(entry.getValue().getPage().getStructParentIndex()));
            numsArray.add(entry.getValue().getNums().makeIndirect(getDocument()));
        }
        parentTree.put(PdfName.Nums, numsArray);

        getPdfObject().flush();
    }

    private void fillNums(Map<PdfObject, PageAndNums> nums, PdfDictionary structElem) throws PdfException {
        PdfObject pg = structElem.get(PdfName.Pg);
        PageAndNums pageAndNums = null;
        if (pg != null) {
            pageAndNums = nums.get(pg);
            if (pageAndNums != null) {
                pageAndNums.getNums().add(structElem);
            }
        }
        PdfObject kids = structElem.get(PdfName.K);
        if (kids instanceof PdfDictionary)
            fillNums(nums, (PdfDictionary)kids);
        else if (kids instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray)kids).size(); i++) {
                PdfDictionary kid = ((PdfArray)kids).getAsDictionary(i);
                if (kid != null) {
                    if (PdfName.MCR.equals(kid.get(PdfName.Type))) {
                        if (pageAndNums != null) {
                            pageAndNums.getNums().add(structElem);
                        }
                    } else {
                        fillNums(nums, kid);
                    }
                }
            }
        }
    }

    private static class PageAndNums {
        private PdfPage page;
        private PdfArray nums;

        private PageAndNums(PdfPage page, PdfArray nums) {
            this.page = page;
            this.nums = nums;
        }

        public PdfPage getPage() {
            return page;
        }

        public PdfArray getNums() {
            return nums;
        }
    }


}
