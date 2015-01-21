package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

/**
 * Represents Marked Content Reference (MCR) object wrapper.
 */
public class PdfMcr extends PdfObjectWrapper<PdfDictionary> implements IPdfTag {

    protected PdfName role;

    public PdfMcr(PdfDictionary pdfObject, PdfName role) {
        super(pdfObject);
        this.role = role;
    }

    public PdfMcr(PdfDictionary pdfObject, PdfDocument pdfDocument, PdfName role) throws PdfException {
        super(pdfObject, pdfDocument);
        this.role = role;
    }

    public PdfMcr(PdfPage page, PdfName role) {
        this(new PdfDictionary(), role);
        getPdfObject().put(PdfName.Type, PdfName.MCR);
        getPdfObject().put(PdfName.Pg, page.getPdfObject());
        getPdfObject().put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
        this.role = role;
    }

    @Override
    public Integer getMcid() throws PdfException {
        return getPdfObject().getAsNumber(PdfName.MCID).getIntValue();
    }

    @Override
    public PdfName getRole() throws PdfException {
        return role;
    }

}
