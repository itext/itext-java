package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.PdfName;

import java.util.List;

public interface IPdfStructElem {

    public IPdfStructElem getParent();

    public List<IPdfStructElem> getKids();

    public PdfName getRole();


}
