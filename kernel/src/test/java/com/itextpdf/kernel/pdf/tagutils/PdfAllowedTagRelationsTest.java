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
    public void isContentObjectAllowedInRole() {
        PdfAllowedTagRelations pdfAllowedTagRelations = new PdfAllowedTagRelations();
        Assertions.assertFalse(pdfAllowedTagRelations.isContentObjectAllowedInRole(StandardRoles.H1));
        Assertions.assertFalse(pdfAllowedTagRelations.isContentObjectAllowedInRole(StandardRoles.P));
        Assertions.assertFalse(pdfAllowedTagRelations.isContentObjectAllowedInRole(StandardRoles.SPAN));
        Assertions.assertFalse(pdfAllowedTagRelations.isContentObjectAllowedInRole(StandardRoles.LBL));

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