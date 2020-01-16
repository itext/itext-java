/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
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
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfCanvasInlineImagesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/PdfCanvasInlineImagesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/PdfCanvasInlineImagesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest01() throws IOException, InterruptedException {
        String filename = "inlineImages01.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImage(ImageDataFactory.create(sourceFolder + "Desert.jpg"), 36, 700, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "bulb.gif"), 36, 600, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "smpl.bmp"), 36, 500, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "itext.png"), 36, 460, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "0047478.jpg"), 36, 300, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "map.jp2"), 36, 200, 100, true);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "amb.jb2"), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_HAS_MASK),
            @LogMessage(messageTemplate = LogMessageConstant.IMAGE_SIZE_CANNOT_BE_MORE_4KB)
    })
    public void inlineImagesTest02() throws IOException, InterruptedException {
        String filename = "inlineImages02.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        InputStream stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "Desert.jpg"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 700, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "bulb.gif"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 600, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "smpl.bmp"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 500, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "itext.png"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 460, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "0047478.jpg"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 300, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "map.jp2"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 200, 100, true);
        stream = UrlUtil.openStream(UrlUtil.toURL(sourceFolder + "amb.jb2"));

        baos = new ByteArrayOutputStream();
        StreamUtil.transferBytes(stream, baos);
        canvas.addImage(ImageDataFactory.create(baos.toByteArray()), 36, 30, 100, true);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    public void inlineImagesTest03() throws IOException, InterruptedException {
        String filename = "inlineImages03.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0))
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.addImage(ImageDataFactory.create(sourceFolder + "bulb.gif"), 36, 600, 100, true);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    //TODO update cmp-files after DEVSIX-3564 will be fixed
    public void inlineImagesPngTest() throws IOException, InterruptedException {
        String filename = "inlineImagePng.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "5.png"), 36, 700, 100, true);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }

    @Test
    //TODO update cmp-files after DEVSIX-3564 will be fixed
    public void inlineImagesPngErrorWhileOpenTest() throws IOException, InterruptedException {
        String filename = "inlineImagePngErrorWhileOpen.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationFolder + filename));

        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.addImage(ImageDataFactory.create(sourceFolder + "3.png"), 36, 700, 100, true);

        document.close();

        Assert.assertNull(new CompareTool()
                .compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder));
    }
}
