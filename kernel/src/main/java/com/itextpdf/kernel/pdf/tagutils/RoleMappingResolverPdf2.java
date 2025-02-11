/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
        boolean stdRole17 = StandardNamespaces.PDF_1_7.equals(currNamespace.getNamespaceName())
                && StandardNamespaces.roleBelongsToStandardNamespace(roleStrVal, StandardNamespaces.PDF_1_7);
        boolean stdRole20 = StandardNamespaces.PDF_2_0.equals(currNamespace.getNamespaceName())
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
