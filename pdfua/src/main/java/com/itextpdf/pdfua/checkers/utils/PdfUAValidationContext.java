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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;

/**
 * This class keeps track of useful information when validating a PdfUaDocument.
 * It also contains some useful utility functions that help with PDF UA validation.
 */
public class PdfUAValidationContext {

    private final PdfDocument pdfDocument;

    /**
     * Creates a new instance of {@link PdfUAValidationContext}.
     *
     * @param pdfDocument the {@link PdfDocument} instance that is being validated
     */
    public PdfUAValidationContext(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    /**
     * Resolves the node's role to a standard role.
     *
     * @param node The node you want to resolve the standard role for.
     *
     * @return The role.
     */
    public String resolveToStandardRole(IStructureNode node) {
        if (node == null) {
            return null;
        }
        PdfName originalRole = node.getRole();
        if (originalRole == null) {
            return null;
        }
        PdfNamespace namespace = node instanceof PdfStructElem ? ((PdfStructElem) node).getNamespace() : null;

        return resolveToStandardRole(originalRole.getValue(), namespace);
    }

    /**
     * Resolves the role to a standard role.
     *
     * @param role the role you want to resolve the standard role for
     *
     * @return resolved role
     */
    public String resolveToStandardRole(String role) {
        return resolveToStandardRole(role, null);
    }

    /**
     * Resolves the role to a standard role.
     *
     * @param role the role you want to resolve the standard role for
     * @param namespace namespace where role is defined
     *
     * @return resolved role
     */
    public String resolveToStandardRole(String role, PdfNamespace namespace) {
        if (role == null) {
            return null;
        }
        IRoleMappingResolver resolver = pdfDocument.getTagStructureContext()
                .resolveMappingToStandardOrDomainSpecificRole(role, namespace);
        if (resolver == null) {
            return role;
        }
        return resolver.getRole();
    }

    /**
     * Checks if a {@link IStructureNode} resolved role's is equal to the provided role.
     * <p>
     * Note: This  method will not check recursive mapping. So either the node's role is the provided role,
     * or the standard role is the provided role. So we do not take into account the roles in between the mappings.
     *
     * @param role          The role we want to check against.
     * @param structureNode The structure node we want to check.
     *
     * @return The {@link PdfStructElem} if the role matches.
     */
    public PdfStructElem getElementIfRoleMatches(PdfName role, IStructureNode structureNode) {
        if (structureNode == null) {
            return null;
        }
        if (!(structureNode instanceof PdfStructElem)) {
            return null;
        }
        // We can get away with the short code without resolving it. Because we have checks in place
        // that would catch remapped standard roles and cyclic roles.
        if (role.equals(structureNode.getRole()) || role.getValue().equals(resolveToStandardRole(structureNode))) {
            return (PdfStructElem) structureNode;
        }
        return null;
    }

    /**
     * Retrieves object reference instance by provided structure parent index.
     *
     * @param i index of the structure parent
     * @param pageDict {@link PdfDictionary} of the page that {@link PdfObjRef} belong to
     *
     * @return {@link PdfObjRef} instance
     */
    public PdfObjRef findObjRefByStructParentIndex(int i, PdfDictionary pageDict) {
        return pdfDocument.getStructTreeRoot().findObjRefByStructParentIndex(pageDict, i);
    }

    /**
     * Retrieves the PDF/UA conformance of the {@link PdfDocument}.
     *
     * @return {@link PdfUAConformance} value
     */
    public PdfUAConformance getUAConformance() {
        return this.pdfDocument.getConformance().getUAConformance();
    }
}
