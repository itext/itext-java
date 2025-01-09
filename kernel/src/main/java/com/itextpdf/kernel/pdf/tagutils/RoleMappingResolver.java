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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;

class RoleMappingResolver implements IRoleMappingResolver {


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
