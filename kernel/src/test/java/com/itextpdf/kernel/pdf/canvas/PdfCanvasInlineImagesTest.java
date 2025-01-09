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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfCanvasInlineImagesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasInlineImagesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasInlineImagesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest01() throws IOException, InterruptedException {
        String filename = "inlineImages01.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "Desert.jpg"), new Rectangle(36, 700, 100, 75), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "bulb.gif"), new Rectangle(36, 600, 100, 100), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "smpl.bmp"), new Rectangle(36, 500, 100, 100), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "itext.png"), new Rectangle(36, 460, 100, 14.16f), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "0047478.jpg"), new Rectangle(36, 300, 100, 141.41f), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "bee.jp2"), new Rectangle(36, 200, 60, 76.34f), true);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "amb.jb2"), new Rectangle(36, 30, 100, 150), true);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = IoLogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest02() throws IOException, InterruptedException {
        String filename = "inlineImages02.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        InputStream stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "Desert.jpg"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle( 36, 700, 100, 75), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "bulb.gif"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 600, 100, 100), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "smpl.bmp"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 500, 100, 100), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "itext.png"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 460, 100, 14.16f), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "0047478.jpg"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 300, 100, 141.41f), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "bee.jp2"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 200, 60, 76.34f), true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "amb.jb2"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(baos.toByteArray()), new Rectangle(36, 30, 100, 150), true);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void inlineImagesTest03() throws IOException, InterruptedException {
        String filename = "inlineImages03.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0))
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "bulb.gif"), new Rectangle(36, 600, 100, 100), true);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void inlineImagesPngTest() throws IOException, InterruptedException {
        String filename = "inlineImagePng.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "5.png"), new Rectangle(36, 700, 100, 100), true);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void inlineImagesPngErrorWhileOpenTest() throws IOException, InterruptedException {
        String filename = "inlineImagePngErrorWhileOpen.pdf";
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImageFittedIntoRectangle(ImageDataFactory.create(sourceFolder + "3.png"), new Rectangle(36, 700, 100, 100), true);

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }
}
