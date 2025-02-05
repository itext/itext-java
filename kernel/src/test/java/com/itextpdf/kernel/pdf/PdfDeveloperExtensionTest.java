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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;

@Tag("IntegrationTest")
public class PdfDeveloperExtensionTest extends ExtendedITextTest {


    public static final PdfDeveloperExtension SIMPLE_EXTENSION_L3 =
            new PdfDeveloperExtension(new PdfName("Test"), PdfName.Pdf_Version_1_7, 3);

    public static final PdfDeveloperExtension SIMPLE_EXTENSION_L5 =
            new PdfDeveloperExtension(new PdfName("Test"), PdfName.Pdf_Version_1_7, 5);

    public static final PdfDeveloperExtension MULTI_EXTENSION_1 = new PdfDeveloperExtension(
            new PdfName("Test"), PdfName.Pdf_Version_2_0, 1, "https://example.com",
            ":2022", true
    );

    public static final PdfDeveloperExtension MULTI_EXTENSION_2 = new PdfDeveloperExtension(
            new PdfName("Test"), PdfName.Pdf_Version_2_0, 2, "https://example.com",
            ":2022", true
    );

    @Test
    public void addSingleValuedExtensionTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L3);
        }

        assertSimpleExtension(
                baos.toByteArray(),
                SIMPLE_EXTENSION_L3.getPrefix(), SIMPLE_EXTENSION_L3.getExtensionLevel()
        );
    }

    @Test
    public void addSingleValuedExtensionOverrideTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L3);
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L5);
        }

        assertSimpleExtension(
                baos.toByteArray(),
                SIMPLE_EXTENSION_L5.getPrefix(), SIMPLE_EXTENSION_L5.getExtensionLevel()
        );
    }

    @Test
    public void addSingleValuedExtensionNoOverrideTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L5);
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L3);
        }

        assertSimpleExtension(
                baos.toByteArray(),
                SIMPLE_EXTENSION_L5.getPrefix(), SIMPLE_EXTENSION_L5.getExtensionLevel()
        );
    }

    @Test
    public void addMultivaluedExtensionTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_1);
        }

        assertMultiExtension(
                baos.toByteArray(),
                MULTI_EXTENSION_1.getPrefix(),
                Collections.singletonList(MULTI_EXTENSION_1.getExtensionLevel())
        );
    }

    @Test
    public void addMultivaluedExtensionNoDuplicateTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_1);
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_1);
        }

        assertMultiExtension(
                baos.toByteArray(),
                MULTI_EXTENSION_1.getPrefix(),
                Collections.singletonList(MULTI_EXTENSION_1.getExtensionLevel())
        );
    }

    @Test
    public void addMultivaluedExtensionNoOverrideTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_1);
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_2);
        }

        assertMultiExtension(
                baos.toByteArray(),
                MULTI_EXTENSION_1.getPrefix(),
                Arrays.asList(MULTI_EXTENSION_1.getExtensionLevel(), MULTI_EXTENSION_2.getExtensionLevel())
        );
    }

    @Test
    public void removeSingleValuedExtensionTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(SIMPLE_EXTENSION_L5);
            pdfDoc.getCatalog().removeDeveloperExtension(SIMPLE_EXTENSION_L5);
        }

        assertNoExtensionWithPrefix(
                baos.toByteArray(),
                SIMPLE_EXTENSION_L5.getPrefix()
        );
    }

    @Test
    public void removeMultivaluedExtensionTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos))) {
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_1);
            pdfDoc.getCatalog().addDeveloperExtension(MULTI_EXTENSION_2);
            pdfDoc.getCatalog().removeDeveloperExtension(MULTI_EXTENSION_2);
        }

        assertMultiExtension(
                baos.toByteArray(),
                MULTI_EXTENSION_1.getPrefix(),
                Arrays.asList(MULTI_EXTENSION_1.getExtensionLevel())
        );
    }

    private void assertSimpleExtension(byte[] docData, PdfName prefix, int expectedLevel) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(docData)))) {
            PdfDictionary extDict = pdfDoc.getCatalog().getPdfObject()
                    .getAsDictionary(PdfName.Extensions)
                    .getAsDictionary(prefix);
            Assertions.assertEquals(expectedLevel, extDict.getAsNumber(PdfName.ExtensionLevel).intValue());
        }
    }

    private void assertNoExtensionWithPrefix(byte[] docData, PdfName prefix) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(docData)))) {
            PdfDictionary extDict = pdfDoc.getCatalog().getPdfObject()
                    .getAsDictionary(PdfName.Extensions)
                    .getAsDictionary(prefix);
            Assertions.assertNull(extDict);
        }
    }

    private void assertMultiExtension(byte[] docData, PdfName prefix, Collection<Integer> expectedLevels) throws IOException {
        Set <Integer> seen = new HashSet<>();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(docData)))) {
            PdfArray exts = pdfDoc.getCatalog().getPdfObject()
                    .getAsDictionary(PdfName.Extensions)
                    .getAsArray(prefix);
            for (int i = 0; i < exts.size(); i++) {
                int level = exts
                        .getAsDictionary(i)
                        .getAsInt(PdfName.ExtensionLevel).intValue();
                Assertions.assertTrue(expectedLevels.contains(level), "Level " + level + " is not in expected level list");
                Assertions.assertFalse(seen.contains(level), "Level " + level + " appears multiple times");
                seen.add(level);
            }
        }
    }
}
