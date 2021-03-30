/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class GifTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/GifTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void gifImageTest() throws IOException, java.io.IOException {
        try (FileInputStream file = new FileInputStream(sourceFolder + "WP_20140410_001.gif")) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            ImageData img = ImageDataFactory.createGif(fileContent).getFrames().get(0);
            Assert.assertTrue(img.isRawImage());
            Assert.assertEquals(ImageType.GIF, img.getOriginalType());
        }
    }

    @Test
    public void gifImageFrameOutOfBoundsTest() throws java.io.IOException {
        junitExpectedException.expect(IOException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(IOException.CannotFind1Frame, 2));
        ImageDataFactory.createGifFrame(UrlUtil.toURL(sourceFolder + "image-2frames.gif"), 3);
    }

    @Test
    public void gifImageSpecificFrameTest() throws IOException, java.io.IOException {
        String imageFilePath = sourceFolder + "image-2frames.gif";
        try (FileInputStream file = new FileInputStream(imageFilePath)) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            ImageData img = ImageDataFactory.createGifFrame(fileContent, 2);
            Assert.assertEquals(100, (int)img.getWidth());
            Assert.assertEquals(100, (int)img.getHeight());

            ImageData imgFromUrl = ImageDataFactory.createGifFrame(UrlUtil.toURL(imageFilePath), 2);
            Assert.assertArrayEquals(img.getData(), imgFromUrl.getData());
        }
    }

    @Test
    public void gifImageReadingAllFramesTest() throws IOException, java.io.IOException {
        String imageFilePath = sourceFolder + "image-2frames.gif";
        try (FileInputStream file = new FileInputStream(imageFilePath)) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            List<ImageData> frames = ImageDataFactory.createGifFrames(fileContent);
            Assert.assertEquals(2, frames.size());
            Assert.assertNotEquals(frames.get(0).getData(), frames.get(1).getData());

            List<ImageData> framesFromUrl = ImageDataFactory.createGifFrames(UrlUtil.toURL(imageFilePath));
            Assert.assertArrayEquals(frames.get(0).getData(), framesFromUrl.get(0).getData());
            Assert.assertArrayEquals(frames.get(1).getData(), framesFromUrl.get(1).getData());
        }
    }
}
