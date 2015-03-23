package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfName;

public interface IPdfTag extends IPdfStructElem {

    public Integer getMcid() throws PdfException;

//    public Integer getStructParentIndex() throws PdfException;

}
