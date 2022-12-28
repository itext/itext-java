/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AccessibilityPropertiesTest extends ExtendedITextTest {

    @Test
    public void setAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assert.assertNotNull(properties.setRole(StandardRoles.DIV));
        Assert.assertNotNull(properties.setLanguage("EN-GB"));
        Assert.assertNotNull(properties.setActualText("actualText"));
        Assert.assertNotNull(properties.setAlternateDescription("Description"));
        Assert.assertNotNull(properties.setExpansion("expansion"));
        Assert.assertNotNull(properties.setPhoneme("phoneme"));
        Assert.assertNotNull(properties.setPhoneticAlphabet("Phonetic Alphabet"));
        Assert.assertNotNull(properties.setNamespace(new PdfNamespace("Namespace")));
        Assert.assertNotNull(properties.getRefsList());
        Assert.assertNotNull(properties.clearRefs());
        Assert.assertNotNull(properties.addAttributes(new PdfStructureAttributes("attributes")));
        Assert.assertNotNull(properties.addAttributes(0, new PdfStructureAttributes("attributes")));
        Assert.assertNotNull(properties.clearAttributes());
        Assert.assertNotNull(properties.getAttributesList());
        Assert.assertNotNull(properties.addRef(new TagTreePointer(createTestDocument())));
    }

    @Test
    public void getAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assert.assertNull(properties.getRole());
        Assert.assertNull(properties.getLanguage());
        Assert.assertNull(properties.getActualText());
        Assert.assertNull(properties.getAlternateDescription());
        Assert.assertNull(properties.getExpansion());
        Assert.assertNull(properties.getPhoneme());
        Assert.assertNull(properties.getPhoneticAlphabet());
        Assert.assertNull(properties.getNamespace());
    }

    private static PdfDocument createTestDocument() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.setTagged();
        return pdfDoc;
    }
}
