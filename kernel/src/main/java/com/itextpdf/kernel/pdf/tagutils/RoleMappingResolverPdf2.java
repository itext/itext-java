/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
