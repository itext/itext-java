package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.PdfPage;

public interface IPdfPageExtraCopier {

    void copy(PdfPage fromPage, PdfPage toPage);
}
