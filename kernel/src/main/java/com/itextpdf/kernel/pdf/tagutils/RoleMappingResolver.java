package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;

class RoleMappingResolver implements IRoleMappingResolver {

    private static final long serialVersionUID = -8911597456631422956L;

    private PdfName currRole;
    private PdfDictionary roleMap;

    RoleMappingResolver(String role, PdfDocument document) {
        this.currRole = PdfStructTreeRoot.convertRoleToPdfName(role);
        this.roleMap = document.getStructTreeRoot().getRoleMap();
    }

    @Override
    public String getRole() {
        return currRole.getValue();
    }

    @Override
    public PdfNamespace getNamespace() {
        return null;
    }

    @Override
    public boolean currentRoleIsStandard() {
        return StandardNamespaces.roleBelongsToStandardNamespace(currRole.getValue(), StandardNamespaces.PDF_1_7);
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
