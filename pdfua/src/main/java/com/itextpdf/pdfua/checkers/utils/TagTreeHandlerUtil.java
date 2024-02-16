/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

/**
 * Utility class that contains utility methods used  when working with the TagTreeHandler
 */
public final class TagTreeHandlerUtil {

    private TagTreeHandlerUtil() {
        //Empty constructor.
    }

    /**
     * Gets the {@link PdfStructElem} if the element matches the provided role and the structureNode is indeed an
     * {@link PdfStructElem}
     *
     * @param role          The role that needs to be matched.
     * @param structureNode The structure node.
     *
     * @return The {@link PdfStructElem}  if the structure matches the role.
     */
    public static PdfStructElem getElementIfRoleMatches(PdfName role, IStructureNode structureNode) {
        if (structureNode == null) {
            return null;
        }
        if (!(structureNode instanceof PdfStructElem)) {
            return null;
        }
        if (!role.equals(structureNode.getRole())) {
            return null;
        }
        return (PdfStructElem) structureNode;
    }
}
