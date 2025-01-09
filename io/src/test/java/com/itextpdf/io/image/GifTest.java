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
package com.itextpdf.io.image;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GifTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/io/image/GifTest/";

    @Test
    public void gifImageTest() throws IOException, java.io.IOException {
        try (InputStream file = FileUtil.getInputStreamForFile(sourceFolder + "WP_20140410_001.gif")) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            ImageData img = ImageDataFactory.createGif(fileContent).getFrames().get(0);
            Assertions.assertTrue(img.isRawImage());
            Assertions.assertEquals(ImageType.GIF, img.getOriginalType());
        }
    }

    @Test
    public void gifImageFrameOutOfBoundsTest() throws java.io.IOException {
        Exception e = Assertions.assertThrows(IOException.class,
                () -> ImageDataFactory.createGifFrame(UrlUtil.toURL(sourceFolder + "image-2frames.gif"), 3));
        Assertions.assertEquals(MessageFormatUtil.format(IoExceptionMessageConstant.CANNOT_FIND_FRAME, 2), e.getMessage());
    }

    @Test
    public void gifImageSpecificFrameTest() throws IOException, java.io.IOException {
        String imageFilePath = sourceFolder + "image-2frames.gif";
        try (InputStream file = FileUtil.getInputStreamForFile(imageFilePath)) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            ImageData img = ImageDataFactory.createGifFrame(fileContent, 2);
            Assertions.assertEquals(100, (int)img.getWidth());
            Assertions.assertEquals(100, (int)img.getHeight());

            ImageData imgFromUrl = ImageDataFactory.createGifFrame(UrlUtil.toURL(imageFilePath), 2);
            Assertions.assertArrayEquals(img.getData(), imgFromUrl.getData());
        }
    }

    @Test
    public void gifImageReadingAllFramesTest() throws IOException, java.io.IOException {
        String imageFilePath = sourceFolder + "image-2frames.gif";
        try (InputStream file = FileUtil.getInputStreamForFile(imageFilePath)) {
            byte[] fileContent = StreamUtil.inputStreamToArray(file);
            List<ImageData> frames = ImageDataFactory.createGifFrames(fileContent);
            Assertions.assertEquals(2, frames.size());
            Assertions.assertNotEquals(frames.get(0).getData(), frames.get(1).getData());

            List<ImageData> framesFromUrl = ImageDataFactory.createGifFrames(UrlUtil.toURL(imageFilePath));
            Assertions.assertArrayEquals(frames.get(0).getData(), framesFromUrl.get(0).getData());
            Assertions.assertArrayEquals(frames.get(1).getData(), framesFromUrl.get(1).getData());
        }
    }
}
