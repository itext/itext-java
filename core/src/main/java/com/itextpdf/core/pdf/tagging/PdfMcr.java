package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

import java.util.List;

/**
 * Represents Marked Content Reference (MCR) object wrapper.
 */
public class PdfMcr extends PdfObjectWrapper<PdfDictionary> implements IPdfTag {

    protected PdfStructElem parent;

    public PdfMcr(PdfDocument document, PdfPage page, PdfStructElem parent) throws PdfException {
        super(new PdfDictionary(), document);
        getPdfObject().put(PdfName.Type, PdfName.MCR);
        getPdfObject().put(PdfName.Pg, page.getPdfObject());
        getPdfObject().put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
        this.parent = parent;
    }

    public PdfMcr(PdfPage page, PdfStructElem parent) throws PdfException {
        this(null, page, parent);
    }

    public PdfMcr(PdfDictionary pdfObject, PdfStructElem parent) throws PdfException {
        this(pdfObject, null, parent);
    }

    public PdfMcr(PdfDictionary pdfObject, PdfDocument pdfDocument, PdfStructElem parent) throws PdfException {
        super(pdfObject, pdfDocument);
        this.parent = parent;
    }

    @Override
    public Integer getMcid() throws PdfException {
        return getPdfObject().getAsNumber(PdfName.MCID).getIntValue();
    }

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
        return parent.getStructParentIndex();
    }
}
