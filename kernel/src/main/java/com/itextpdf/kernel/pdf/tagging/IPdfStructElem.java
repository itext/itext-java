package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfName;

import java.util.List;

public interface IPdfStructElem {

    public IPdfStructElem getParent();

    public List<IPdfStructElem> getKids();

    public PdfName getRole();


}
