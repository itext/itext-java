package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;

import java.util.List;

/**
 * Represents Marked Content Reference (MCR) object wrapper.
 */
abstract public class PdfMcr<T extends PdfObject> extends PdfObjectWrapper implements IPdfStructElem {

    protected PdfStructElem parent;

    public PdfMcr(T pdfObject, PdfStructElem parent) {
        super(pdfObject);
        this.parent = parent;
    }

    public abstract Integer getMcid();

    public abstract PdfDictionary getPageObject();

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

}
