package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.*;

import java.util.List;

/**
 * Represents Marked Content Reference (MCR) object wrapper.
 */
abstract public class PdfMcr<T extends PdfObject> extends PdfObjectWrapper implements IPdfTag {

    protected PdfStructElem parent;

    public PdfMcr(T pdfObject, PdfStructElem parent) {
        super(pdfObject);
        this.parent = parent;
    }

//    public PdfMcr(PdfPage page, PdfStructElem parent) {
//        super(new PdfDictionary());
//        getPdfObject().put(PdfName.Type, PdfName.MCR);
//        getPdfObject().put(PdfName.Pg, page.getPdfObject());
//        getPdfObject().put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
//        this.parent = parent;
//    }
//
//    public PdfMcr(PdfDictionary pdfObject, PdfStructElem parent) {
//        super(pdfObject);
//        this.parent = parent;
//    }

    @Override
    abstract public Integer getMcid();// {
//        return getPdfObject().getAsNumber(PdfName.MCID).getIntValue();
//    }

    @Override
    public PdfName getRole() {
        return parent.getRole();
    }

    @Override
    public IPdfStructElem getParent() {
        return parent;
    }

    @Override
    public List<IPdfStructElem> getKids() {
        return null;
    }

//    @Override
//    public Integer getStructParentIndex() {
//        Integer structParentIndex = 0;
//        PdfDictionary page = getPageObject();
//        if (page != null) {
//            PdfNumber spi = page.getAsNumber(PdfName.StructParents);
//            if (spi != null)
//                structParentIndex = spi.getIntValue();
//        }
//        return structParentIndex;
//    }

}
