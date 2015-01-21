package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfName;

public interface IPdfTag {

    public PdfName getRole() throws PdfException;

    public Integer getMcid() throws PdfException;

}
