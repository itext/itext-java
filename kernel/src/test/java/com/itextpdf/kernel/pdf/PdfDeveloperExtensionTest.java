/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class PdfDeveloperExtensionTest {


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

    private void assertSimpleExtension(byte[] docData, PdfName prefix, int expectedLevel) throws IOException {

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(docData)))) {
            PdfDictionary extDict = pdfDoc.getCatalog().getPdfObject()
                    .getAsDictionary(PdfName.Extensions)
                    .getAsDictionary(prefix);
            assertEquals(expectedLevel, extDict.getAsNumber(PdfName.ExtensionLevel).intValue());
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
                assertTrue("Level " + level + " is not in expected level list", expectedLevels.contains(level));
                assertFalse("Level " + level + " appears multiple times", seen.contains(level));
                seen.add(level);
            }
        }
    }
}
