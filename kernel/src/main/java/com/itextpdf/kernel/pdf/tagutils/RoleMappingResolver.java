package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;

class RoleMappingResolver implements IRoleMappingResolver {
    private PdfName currRole;
    private PdfDictionary roleMap;

    RoleMappingResolver(PdfName currRole, PdfDocument document) {
        this.currRole = currRole;
        this.roleMap = document.getStructTreeRoot().getRoleMap();
    }

    @Override
    public PdfName getRole() {
        return currRole;
    }

    @Override
    public PdfNamespace getNamespace() {
        return null;
    }

    @Override
    public boolean currentRoleIsStandard() {
        return StandardStructureNamespace.roleBelongsToStandardNamespace(currRole, StandardStructureNamespace._1_7);
    }

    @Override
    public boolean currentRoleShallBeMappedToStandard() {
        return !currentRoleIsStandard();
    }

    @Override
    public boolean resolveNextMapping() {
        PdfName mappedRole = roleMap.getAsName(currRole);
        if (mappedRole == null) {
            return false;
        }
        currRole = mappedRole;
        return true;
    }
}
