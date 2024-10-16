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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AccessibilityPropertiesTest extends ExtendedITextTest {

    @Test
    public void setAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assertions.assertNotNull(properties.setRole(StandardRoles.DIV));
        Assertions.assertNotNull(properties.setLanguage("EN-GB"));
        Assertions.assertNotNull(properties.setActualText("actualText"));
        Assertions.assertNotNull(properties.setAlternateDescription("Description"));
        Assertions.assertNotNull(properties.setExpansion("expansion"));
        Assertions.assertNotNull(properties.setPhoneme("phoneme"));
        Assertions.assertNotNull(properties.setPhoneticAlphabet("Phonetic Alphabet"));
        Assertions.assertNotNull(properties.setNamespace(new PdfNamespace("Namespace")));
        Assertions.assertNotNull(properties.getRefsList());
        Assertions.assertNotNull(properties.clearRefs());
        Assertions.assertNotNull(properties.addAttributes(new PdfStructureAttributes("attributes")));
        Assertions.assertNotNull(properties.addAttributes(0, new PdfStructureAttributes("attributes")));
        Assertions.assertNotNull(properties.clearAttributes());
        Assertions.assertNotNull(properties.getAttributesList());
        Assertions.assertNotNull(properties.addRef(new TagTreePointer(createTestDocument())));
    }

    @Test
    public void getAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assertions.assertNull(properties.getRole());
        Assertions.assertNull(properties.getLanguage());
        Assertions.assertNull(properties.getActualText());
        Assertions.assertNull(properties.getAlternateDescription());
        Assertions.assertNull(properties.getExpansion());
        Assertions.assertNull(properties.getPhoneme());
        Assertions.assertNull(properties.getPhoneticAlphabet());
        Assertions.assertNull(properties.getNamespace());
    }

    private static PdfDocument createTestDocument() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.setTagged();
        return pdfDoc;
    }
}
