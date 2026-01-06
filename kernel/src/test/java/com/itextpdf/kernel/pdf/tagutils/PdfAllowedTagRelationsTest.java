/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("UnitTest")
class PdfAllowedTagRelationsTest extends ExtendedITextTest {

    @Test
    public void isRelationAllowed() {
        PdfAllowedTagRelations pdfAllowedTagRelations = new PdfAllowedTagRelations();
        assertFalse(pdfAllowedTagRelations.isRelationAllowed(StandardRoles.P, StandardRoles.P));
        assertFalse(pdfAllowedTagRelations.isRelationAllowed(StandardRoles.P, StandardRoles.DIV));
        assertFalse(pdfAllowedTagRelations.isRelationAllowed(StandardRoles.DOCUMENT, StandardRoles.SPAN));
        assertFalse(pdfAllowedTagRelations.isRelationAllowed(StandardRoles.FORM, StandardRoles.SPAN));
        assertFalse(pdfAllowedTagRelations.isRelationAllowed(StandardRoles.FORM, StandardRoles.H1));
    }

    @Test
    public void isContentAllowedInRole() {
        PdfAllowedTagRelations pdfAllowedTagRelations = new PdfAllowedTagRelations();
        Assertions.assertTrue(pdfAllowedTagRelations.isContentAllowedInRole(StandardRoles.H1));
        Assertions.assertTrue(pdfAllowedTagRelations.isContentAllowedInRole(StandardRoles.P));
        Assertions.assertTrue(pdfAllowedTagRelations.isContentAllowedInRole(StandardRoles.SPAN));
        Assertions.assertTrue(pdfAllowedTagRelations.isContentAllowedInRole(StandardRoles.LBL));
        Assertions.assertFalse(pdfAllowedTagRelations.isContentAllowedInRole(StandardRoles.DIV));
    }

    @Test
    public void normalizeRole() {
        PdfAllowedTagRelations pdfAllowedTagRelations = new PdfAllowedTagRelations();
        Assertions.assertEquals(StandardRoles.P, pdfAllowedTagRelations.normalizeRole(StandardRoles.P));
        Assertions.assertEquals(PdfAllowedTagRelations.NUMBERED_HEADER,
                pdfAllowedTagRelations.normalizeRole(StandardRoles.H1));
        Assertions.assertEquals(PdfAllowedTagRelations.NUMBERED_HEADER,
                pdfAllowedTagRelations.normalizeRole(StandardRoles.H2));
    }
}
