package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;

import java.util.List;

public interface IPdfStructElem {

    public IPdfStructElem getParent() throws PdfException;

    public List<IPdfStructElem> getKids() throws PdfException;

}
