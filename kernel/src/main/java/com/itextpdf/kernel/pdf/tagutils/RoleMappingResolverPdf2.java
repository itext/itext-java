package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;


class RoleMappingResolverPdf2 implements IRoleMappingResolver {

    private static final long serialVersionUID = -564649110244365255L;

    private PdfName currRole;
    private PdfNamespace currNamespace;

    private PdfNamespace defaultNamespace;


    RoleMappingResolverPdf2(String role, PdfNamespace namespace, PdfDocument document) {
        this.currRole = PdfStructTreeRoot.convertRoleToPdfName(role);
        this.currNamespace = namespace;

        String defaultNsName = StandardNamespaces.getDefault();
        PdfDictionary defaultNsRoleMap = document.getStructTreeRoot().getRoleMap();
        this.defaultNamespace = new PdfNamespace(defaultNsName).setNamespaceRoleMap(defaultNsRoleMap);

        if (currNamespace == null) {
            currNamespace = defaultNamespace;
        }
    }

    public String getRole() {
        return currRole.getValue();
    }

    public PdfNamespace getNamespace() {
        return currNamespace;
    }

    public boolean currentRoleIsStandard() {
        String roleStrVal = currRole.getValue();
        boolean stdRole17 = currNamespace.getNamespaceName().equals(StandardNamespaces.PDF_1_7)
                && StandardNamespaces.roleBelongsToStandardNamespace(roleStrVal, StandardNamespaces.PDF_1_7);
        boolean stdRole20 = currNamespace.getNamespaceName().equals(StandardNamespaces.PDF_2_0)
                && StandardNamespaces.roleBelongsToStandardNamespace(roleStrVal, StandardNamespaces.PDF_2_0);
        return stdRole17 || stdRole20;
    }

    public boolean currentRoleShallBeMappedToStandard() {
        return !currentRoleIsStandard() && !StandardNamespaces.isKnownDomainSpecificNamespace(currNamespace);
    }

    public boolean resolveNextMapping() {
        PdfObject mapping = null;
        PdfDictionary currNsRoleMap = currNamespace.getNamespaceRoleMap();
        if (currNsRoleMap != null) {
            mapping = currNsRoleMap.get(currRole);
        }

        if (mapping == null) {
            return false;
        }

        boolean mappingWasResolved = false;
        if (mapping.isName()) {
            currRole = (PdfName) mapping;
            currNamespace = defaultNamespace;
            mappingWasResolved = true;
        } else if (mapping.isArray()) {
            PdfName mappedRole = null;
            PdfDictionary mappedNsDict = null;

            PdfArray mappingArr = (PdfArray) mapping;
            if (mappingArr.size() > 1) {
                mappedRole = mappingArr.getAsName(0);
                mappedNsDict = mappingArr.getAsDictionary(1);
            }
            mappingWasResolved = mappedRole != null && mappedNsDict != null;
            if (mappingWasResolved) {
                currRole = mappedRole;
                currNamespace = new PdfNamespace(mappedNsDict);
            }
        }
        return mappingWasResolved;
    }
}
