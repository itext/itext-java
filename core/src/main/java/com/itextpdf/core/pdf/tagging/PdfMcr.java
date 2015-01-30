package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
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

//    public PdfMcr(PdfPage page, PdfStructElem parent) throws PdfException {
//        super(new PdfDictionary());
//        getPdfObject().put(PdfName.Type, PdfName.MCR);
//        getPdfObject().put(PdfName.Pg, page.getPdfObject());
//        getPdfObject().put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
//        this.parent = parent;
//    }
//
//    public PdfMcr(PdfDictionary pdfObject, PdfStructElem parent) throws PdfException {
//        super(pdfObject);
//        this.parent = parent;
//    }

    @Override
    abstract public Integer getMcid() throws PdfException;// {
//        return getPdfObject().getAsNumber(PdfName.MCID).getIntValue();
//    }

    @Override
    public PdfName getRole() throws PdfException {
        return parent.getRole();
    }

    @Override
    public IPdfStructElem getParent() throws PdfException {
        return parent;
    }

    @Override
    public List<IPdfStructElem> getKids() throws PdfException {
        return null;
    }

    @Override
    public Integer getStructParentIndex() throws PdfException {
        Integer structParentIndex = 0;
        PdfDictionary page = getPageObject();
        if (page != null) {
            PdfNumber spi = page.getAsNumber(PdfName.StructParents);
            if (spi != null)
                structParentIndex = spi.getIntValue();
        }
        return structParentIndex;
    }

    protected abstract PdfDictionary getPageObject() throws PdfException;

}
