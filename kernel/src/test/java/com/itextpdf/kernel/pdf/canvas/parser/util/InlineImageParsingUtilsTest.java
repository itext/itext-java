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
package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.parser.util.InlineImageParsingUtils.InlineImageParseException;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Tag("UnitTest")
public class InlineImageParsingUtilsTest extends ExtendedITextTest {

    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/InlineImageParsingUtilsTest/";

    @Test
    public void iccBasedCsTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();
        PdfArray array = new PdfArray();
        array.add(colorSpace);
        PdfStream stream = new PdfStream();
        stream.put(PdfName.N, new PdfNumber(4));
        array.add(stream);
        dictionary.put(colorSpace, array);

        Assertions.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void indexedCsTest() {
        PdfName colorSpace = PdfName.Indexed;

        PdfDictionary dictionary = new PdfDictionary();
        PdfArray array = new PdfArray();
        array.add(colorSpace);
        dictionary.put(colorSpace, array);

        Assertions.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void csInDictAsNameTest() {
        PdfName colorSpace = PdfName.ICCBased;

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(colorSpace, PdfName.DeviceCMYK);

        Assertions.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
    }

    @Test
    public void csInDictAsNameNullTest() {
        PdfName colorSpace = PdfName.ICCBased;
        PdfDictionary dictionary = new PdfDictionary();
        Exception exception = Assertions.assertThrows(InlineImageParseException.class,
                () -> InlineImageParsingUtils.getComponentsPerPixel(colorSpace, dictionary));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNEXPECTED_COLOR_SPACE, "/ICCBased"),
                exception.getMessage());
    }

    @Test
    public void nullCsTest() {
        Assertions.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(null, null));
    }

    @Test
    public void deviceGrayCsTest() {
        PdfName colorSpace = PdfName.DeviceGray;
        Assertions.assertEquals(1, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }

    @Test
    public void deviceRGBCsTest() {
        PdfName colorSpace = PdfName.DeviceRGB;
        Assertions.assertEquals(3, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }

    @Test
    public void deviceCMYKCsTest() {
        PdfName colorSpace = PdfName.DeviceCMYK;
        Assertions.assertEquals(4, InlineImageParsingUtils.getComponentsPerPixel(colorSpace, null));
    }

    @Test
    public void parseLargeImageWithEndMarkerInDataTest() throws IOException {
        PdfTokenizer tokenizer = new PdfTokenizer(
                new RandomAccessFileOrArray(
                new RandomAccessSourceFactory().createSource(
                        Files.readAllBytes(Paths.get(RESOURCE_FOLDER + "img.dat")))
                ));
        PdfCanvasParser ps = new PdfCanvasParser(tokenizer, new PdfResources());
        List<PdfObject> objects = ps.parse(null);
        Assertions.assertEquals(2, objects.size());
        Assertions.assertTrue(objects.get(0) instanceof PdfStream);
        Assertions.assertEquals(new PdfLiteral("EI"), objects.get(1));
        //Getting encoded bytes of an image, can't use PdfStream#getBytes() here because it decodes an image
        byte[] image = ((ByteArrayOutputStream) ((PdfStream)objects.get(0)).getOutputStream().getOutputStream()).toByteArray();
        byte[] cmpImage = Files.readAllBytes(Paths.get(RESOURCE_FOLDER, "cmp_img.dat"));
        Assertions.assertArrayEquals(cmpImage, image);
    }

    @Test
    public void binaryDataProbationTest() throws IOException {
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI Q", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI EMC", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI  S", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI  EMC", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI \000Q", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI Q                             ", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI EMC                           ", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI                               ", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI                               Q ", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI                               EMC ", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI ", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI QEI", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/ EI ", "inline image data");
        // 2nd EI is taken into account
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/ EI DDDEI ", "inline image dat`ûGÔn");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI SEI Q", "inline image data");

        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI \u0000", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI \u007f", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI \u0000pdf", "inline image data");
        testInlineImage("ID\nBl7a$DIjr)D..'g+Cno&@/EI \u0000pdf\u0000\u0000\u0000", "inline image data");
    }

    private void testInlineImage(String imgData, String cmpImgData) throws IOException {
        String data = "BI\n" +
                "/Width 10\n" +
                "/Height 10\n" +
                "/BitsPerComponent 8\n" +
                "/ColorSpace /DeviceRGB\n" +
                "/Filter [/ASCII85Decode]\n" + imgData;
        PdfTokenizer tokenizer = new PdfTokenizer(
                new RandomAccessFileOrArray(
                        new RandomAccessSourceFactory().createSource(data.getBytes(StandardCharsets.ISO_8859_1))
                )
        );
        PdfCanvasParser ps = new PdfCanvasParser(tokenizer, new PdfResources());
        List<PdfObject> objects = ps.parse(null);
        Assertions.assertEquals(2, objects.size());
        Assertions.assertTrue(objects.get(0) instanceof PdfStream);
        Assertions.assertEquals(new PdfLiteral("EI"), objects.get(1));
        String image = new String(((PdfStream)objects.get(0)).getBytes(), StandardCharsets.ISO_8859_1);
        Assertions.assertEquals(image, cmpImgData);
    }
}
