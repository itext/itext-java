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
package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ImagePdfBytesInfoTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/xobject"
            + "/ImagePdfBytesInfoTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/xobject"
            + "/ImagePdfBytesInfoTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void indexedColorSpace2BpcTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "indexedCalRGB2bpc.pdf"))) {
            int pngColorType = getPngColorTypeFromObject(pdfDoc, 1, "Im1");
            Assertions.assertEquals(3, pngColorType);
        }
    }

    @Test
    public void indexedColorSpace8BpcTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "indexedCalRGB8bpc.pdf"))) {
            int pngColorType = getPngColorTypeFromObject(pdfDoc, 1, "Im1");
            Assertions.assertEquals(3, pngColorType);
        }
    }

    @Test
    public void indexedColorSpace16BpcTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "indexedCalRGB16bpc.pdf"))) {
            int pngColorType = getPngColorTypeFromObject(pdfDoc, 1, "Im1");
            Assertions.assertEquals(3, pngColorType);
        }
    }

    @Test
    public void calRgb16bpcTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "calRgb16bpc.pdf"))) {
            int pngColorType = getPngColorTypeFromObject(pdfDoc, 1, "Im0");
            Assertions.assertEquals(2, pngColorType);
        }
    }

    @Test
    public void calGrayTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "calgray.pdf"))) {
            int pngColorType = getPngColorTypeFromObject(pdfDoc, 1, "Im0");
            Assertions.assertEquals(0, pngColorType);
        }
    }

    @Test
    public void negativeNTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "negativeN.pdf"))) {
            PdfImageXObject img = getPdfImageCObject(pdfDoc, 1, "Im1");
            ImagePdfBytesInfo imagePdfBytesInfo = new ImagePdfBytesInfo((int)img.getWidth(), (int)img.getHeight(),
                    img.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue(),
                    img.getPdfObject().get(PdfName.ColorSpace),
                    img.getPdfObject().getAsArray(PdfName.Decode));

            int pngColorType = imagePdfBytesInfo.getPngColorType();
            Assertions.assertEquals(-1, pngColorType);

            Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                    () -> imagePdfBytesInfo.decodeTiffAndPngBytes(img.getImageBytes()));
            Assertions.assertEquals("N value -1 is not supported.", e.getMessage());
        }
    }

    @Test
    public void undefinedCSArrayTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "undefinedInCSArray.pdf"))) {
            PdfImageXObject img = getPdfImageCObject(pdfDoc, 1, "Im1");
            ImagePdfBytesInfo imagePdfBytesInfo = new ImagePdfBytesInfo((int)img.getWidth(), (int)img.getHeight(),
                    img.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue(),
                    img.getPdfObject().get(PdfName.ColorSpace),
                    img.getPdfObject().getAsArray(PdfName.Decode));

            int pngColorType = imagePdfBytesInfo.getPngColorType();
            Assertions.assertEquals(-1, pngColorType);

            Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class,
                    () -> imagePdfBytesInfo.decodeTiffAndPngBytes(img.getImageBytes()));
            Assertions.assertEquals("The color space /Undefined is not supported.", e.getMessage());
        }
    }

    private int getPngColorTypeFromObject(PdfDocument pdfDocument, int pageNum, String objectId) {
        PdfImageXObject img = getPdfImageCObject(pdfDocument, pageNum, objectId);
        ImagePdfBytesInfo imagePdfBytesInfo = new ImagePdfBytesInfo((int)img.getWidth(), (int)img.getHeight(),
                img.getPdfObject().getAsNumber(PdfName.BitsPerComponent).intValue(),
                img.getPdfObject().get(PdfName.ColorSpace),
                img.getPdfObject().getAsArray(PdfName.Decode));
        return imagePdfBytesInfo.getPngColorType();
    }

    private PdfImageXObject getPdfImageCObject(PdfDocument pdfDoc, int pageNum, String objectId) {
        PdfResources resources = pdfDoc.getPage(pageNum).getResources();
        PdfDictionary xobjects = resources.getResource(PdfName.XObject);
        PdfObject obj = xobjects.get(new PdfName(objectId));

        return new PdfImageXObject((PdfStream) obj);
    }
}
