package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IPdfStructElem {

    public PdfStructTreeRoot(PdfDocument document) throws PdfException {
        this(new PdfDictionary(), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    public PdfStructTreeRoot(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
//        PdfDictionary parentTree = getPdfObject().getAsDictionary(PdfName.ParentTree);
//        if (parentTree == null) {
//            parentTree = new PdfDictionary().makeIndirect(document);
//            getPdfObject().put(PdfName.ParentTree, parentTree);
//        }
    }

    public void addKid(PdfStructElem structElem) throws PdfException {
        addKidObject(structElem.getPdfObject());
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        return null;
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        final PdfObject k = getPdfObject().get(PdfName.K);
        if (k instanceof PdfDictionary && PdfStructElem.isStructElem((PdfDictionary) k)) {
            return new ArrayList<IPdfStructElem>() {{
                add(new PdfStructElem((PdfDictionary) k, getDocument()));
            }};
        } else if (k instanceof PdfArray) {
            List<IPdfStructElem> kids = new ArrayList<IPdfStructElem>();
            PdfArray kArr = (PdfArray) k;
            for (int i = 0; i < kArr.size(); i++) {
                PdfObject o = kArr.get(i);
                if (o instanceof PdfDictionary) {
                    PdfDictionary d = (PdfDictionary) o;
                    if (PdfStructElem.isStructElem(d)) {
                        kids.add(new PdfStructElem(d, getDocument()));
                    }
                }
            }
            return kids;
        }
        return null;
    }

    public PdfArray getKidsObject() throws PdfException {
        PdfArray k = getPdfObject().getAsArray(PdfName.K);
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
        }
        return k;
    }

    public void addKidObject(PdfDictionary structElem) throws PdfException {
        getKidsObject().add(structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }

    @Override
    public void flush() throws PdfException {
        super.flush();
    }

    public PdfDictionary getRoleMap() throws PdfException {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
        }
        return roleMap;
    }

    public PdfDictionary getParentTreeObject() throws PdfException {
        PdfDictionary parentTree = getPdfObject().getAsDictionary(PdfName.ParentTree);
        if (parentTree == null) {
            parentTree = new PdfDictionary();
            if (getDocument().getWriter() != null)
                parentTree.makeIndirect(getDocument());
            getPdfObject().put(PdfName.ParentTree, parentTree);
        }
        return parentTree;
    }

    public PdfArray getNums() throws PdfException {
        PdfArray nums = getParentTreeObject().getAsArray(PdfName.Nums);
        if (nums == null) {
            nums = new PdfArray();
            if (getDocument().getWriter() != null)
                nums.makeIndirect(getDocument());
            getParentTreeObject().put(PdfName.Nums, nums);
        }
        return nums;
    }

    public int flattenNums() throws PdfException {
        PdfArray kids = getParentTreeObject().getAsArray(PdfName.Kids);
        if (kids != null) {
            PdfArray nums = new PdfArray();
            for (int i = 0; i < kids.size(); i++) {
                PdfObject o = kids.get(i);
                if (o instanceof PdfDictionary) {
                    PdfArray numsLocal = ((PdfDictionary) o).getAsArray(PdfName.Nums);
                    if (numsLocal != null) {
                        nums.addAll(numsLocal);
                    }
                } else {
                    LoggerFactory.getLogger(this.getClass()).warn("Suspicious nums element in StructParentTree", o);
                }
            }
            getParentTreeObject().remove(PdfName.Kids);
            getParentTreeObject().put(PdfName.Nums, nums);

        }

        int maxStructParentIndex = 0;
        PdfArray nums = getParentTreeObject().getAsArray(PdfName.Nums);
        for (int i = 0; i < nums.size(); i++) {
            PdfNumber n = nums.getAsNumber(i);
            if (n != null && n.getIntValue() > maxStructParentIndex)
                maxStructParentIndex = n.getIntValue();
        }
        return maxStructParentIndex;

    }

    public PdfArray getNumsBranch(Integer structParentIndex) throws PdfException {
        if (structParentIndex == null)
            return null;
        PdfArray nums = getNums();
        PdfArray numsBranch = null;
        for (int i = 0; i < nums.size() / 2; i++) {
            PdfNumber spi = nums.getAsNumber(i * 2);
            if (spi != null && structParentIndex.equals(spi.getIntValue())) {
                PdfObject branchEntry = nums.get(i * 2 + 1);
                return ((PdfArray) branchEntry);
            }
        }
        numsBranch = new PdfArray();
        if (getDocument().getWriter() != null)
            numsBranch.makeIndirect(getDocument());
        nums.add(new PdfNumber(structParentIndex));
        nums.add(numsBranch);
        return numsBranch;
    }

}
