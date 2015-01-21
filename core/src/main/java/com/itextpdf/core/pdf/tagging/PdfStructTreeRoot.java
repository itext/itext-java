package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

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
        Map<PdfObject, Object[]> page2Nums = new LinkedHashMap<PdfObject, Object[]>();
        for (int i = 1; i <= getDocument().getNumOfPages(); i++) {
            PdfPage page = getDocument().getPage(i);
            Integer structParentIndex = page.getStructParentIndex();
            if (structParentIndex != null)
                page2Nums.put(page.getPdfObject(), new Object[]{page, new PdfArray()});
        }
        PdfArray kids = getKids();
        for (int i = 0; i < kids.size(); i++) {
            if (kids.get(i) instanceof PdfDictionary)
                fillNums(page2Nums, (PdfDictionary) kids.get(i));
        }
        PdfDictionary parentTree = new PdfDictionary().makeIndirect(getDocument());
        getPdfObject().put(PdfName.ParentTree, parentTree);
        PdfArray nums = new PdfArray().makeIndirect(getDocument());
        for (Map.Entry<PdfObject, Object[]> entry : page2Nums.entrySet()) {
            nums.add(new PdfNumber(((PdfPage) entry.getValue()[0]).getStructParentIndex()));
            nums.add(((PdfArray) entry.getValue()[1]).makeIndirect(getDocument()));
        }
        parentTree.put(PdfName.Nums, nums);
        getPdfObject().flush();
    }

    public PdfDictionary getRoleMap() throws PdfException {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
        }
        return roleMap;
    }

    private void fillNums(Map<PdfObject, Object[]> nums, PdfDictionary structElem) throws PdfException {
        PdfObject pg = structElem.get(PdfName.Pg);
        Object[] num = null;
        if (pg != null) {
            num = nums.get(pg);
            if (num != null) {
                ((PdfArray) num[1]).add(structElem);
            }
        }
        PdfObject kids = structElem.get(PdfName.K);
        if (kids instanceof PdfDictionary)
            fillNums(nums, (PdfDictionary) kids);
        else if (kids instanceof PdfArray) {
            for (int i = 0; i < ((PdfArray) kids).size(); i++) {
                PdfDictionary kid = ((PdfArray) kids).getAsDictionary(i);
                if (kid != null) {
                    if (PdfName.MCR.equals(kid.get(PdfName.Type))) {
                        if (num != null) {
                            ((PdfArray) num[1]).add(structElem);
                        }
                    } else {
                        fillNums(nums, kid);
                    }
                }
            }
        }
    }

}
