package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;

public interface IRoleMappingResolver {

    PdfName getRole();
    PdfNamespace getNamespace();
    boolean currentRoleIsStandard();
    boolean currentRoleShallBeMappedToStandard();
    boolean resolveNextMapping();
}
