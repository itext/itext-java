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
    }

    public PdfStructElem addKid(PdfStructElem structElem) throws PdfException {
        return addKid(-1, structElem);
    }

    public PdfStructElem addKid(int index, PdfStructElem structElem) throws PdfException {
        addKidObject(index, structElem.getPdfObject());
        return structElem;
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        return null;
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        PdfObject k = getPdfObject().get(PdfName.K);
        List<IPdfStructElem> kids = new ArrayList<IPdfStructElem>();
        switch (k.getType()) {
            case PdfObject.Dictionary:
                PdfDictionary d = (PdfDictionary) k;
                if (PdfStructElem.isStructElem(d))
                    kids.add(new PdfStructElem(d));
                break;
            case PdfObject.Array:
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    PdfObject o = a.get(i);
                    switch (o.getType()) {
                        case PdfObject.Dictionary:
                            d = a.getAsDictionary(i);
                            if (d != null) {
                                if (PdfStructElem.isStructElem(d))
                                    kids.add(new PdfStructElem(d));
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return kids;
    }

    public PdfArray getKidsObject() throws PdfException {
        PdfArray k = getPdfObject().getAsArray(PdfName.K);
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
        }
        return k;
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

    public int getStructParentIndex() throws PdfException {
        PdfArray nums = null;
        PdfArray kids = getParentTreeObject().getAsArray(PdfName.Kids);
        if (kids != null) {
            nums = new PdfArray();
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
        }

        int maxStructParentIndex = 0;
        if (nums == null)
            nums = getParentTreeObject().getAsArray(PdfName.Nums);
        for (int i = 0; i < nums.size(); i++) {
            PdfNumber n = nums.getAsNumber(i);
            if (n != null && n.getIntValue() > maxStructParentIndex)
                maxStructParentIndex = n.getIntValue();
        }
        return maxStructParentIndex;

    }

    @Override
    public PdfName getRole() throws PdfException {
        return null;
    }

    @Override
    public void flush() throws PdfException {
        PdfArray nums = new PdfArray();
        for (int i = 0; i < getDocument().getNumOfPages(); i++) {
            PdfPage page = getDocument().getPage(i + 1);
            List<IPdfTag> tags = page.getPageTags();
            PdfArray numsBranch = new PdfArray();
            if (getDocument().getWriter() != null)
                numsBranch.makeIndirect(getDocument());
            for (IPdfTag tag : tags) {
                numsBranch.add(((PdfStructElem) tag.getParent()).getPdfObject());
            }
            nums.add(new PdfNumber(i));
            nums.add(numsBranch);
        }
        getParentTreeObject().remove(PdfName.Kids);
        getParentTreeObject().put(PdfName.Nums, nums);
        super.flush();
    }

    private void addKidObject(PdfDictionary structElem) throws PdfException {
        getKidsObject().add(-1, structElem);
    }

    private void addKidObject(int index, PdfDictionary structElem) throws PdfException {
        if (index == -1)
            getKidsObject().add(structElem);
        else
            getKidsObject().add(index, structElem);
        if (PdfStructElem.isStructElem(structElem))
            structElem.put(PdfName.P, getPdfObject());
    }


}
