package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfName;

import java.util.List;

public interface IPdfStructElem {

    public IPdfStructElem getParent() throws PdfException;

    public List<IPdfStructElem> getKids() throws PdfException;

    public PdfName getRole() throws PdfException;


}
