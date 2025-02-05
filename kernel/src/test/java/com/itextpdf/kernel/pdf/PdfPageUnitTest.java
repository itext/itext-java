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
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static com.itextpdf.kernel.pdf.PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS;

@Tag("UnitTest")
public class PdfPageUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfPageUnitTest/";

    @Test
    public void cannotGetMcidIfDocIsNotTagged() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfPage.getNextMcid());
        Assertions.assertEquals(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT, exception.getMessage());
    }

    @Test
    public void cannotSetPageLabelIfFirstPageLessThanOneTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfPage.setPageLabel(DECIMAL_ARABIC_NUMERALS, "test_prefix", 0));
        Assertions.assertEquals(
                KernelExceptionMessageConstant.IN_A_PAGE_LABEL_THE_PAGE_NUMBERS_MUST_BE_GREATER_OR_EQUAL_TO_1,
                exception.getMessage());
    }

    @Test
    public void cannotFlushTagsIfNoTagStructureIsPresentTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage pdfPage = pdfDoc.addNewPage();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> pdfPage.tryFlushPageTags());
        Assertions.assertEquals(KernelExceptionMessageConstant.TAG_STRUCTURE_FLUSHING_FAILED_IT_MIGHT_BE_CORRUPTED,
                exception.getMessage());
    }

    @Test
    public void mediaBoxAttributeIsNotPresentTest() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "mediaBoxAttributeIsNotPresentTest.pdf"));
        PdfObject mediaBox = pdfDoc.getPage(1).getPdfObject().get(PdfName.MediaBox);
        Assertions.assertNull(mediaBox);
        PdfPage page = pdfDoc.getPage(1);
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> page.getMediaBox());
        Assertions.assertEquals(KernelExceptionMessageConstant.CANNOT_RETRIEVE_MEDIA_BOX_ATTRIBUTE, exception.getMessage());
    }
}
