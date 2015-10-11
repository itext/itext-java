package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.PdfDictionary;

public interface IPdfTag extends IPdfStructElem {

    Integer getMcid();

    PdfDictionary getPageObject();

//    public Integer getStructParentIndex();

}
