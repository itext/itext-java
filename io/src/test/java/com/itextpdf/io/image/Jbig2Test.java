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
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class Jbig2Test extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/Jbig2Test/";

    @Test
    public void testReadingJbigFromBytes() throws IOException {
        try (InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "image.jb2")) {
            byte[] inputImage = StreamUtil.inputStreamToArray(is);
            ImageData imageData = ImageDataFactory.createJbig2(inputImage, 1);
            Assertions.assertEquals(100, (int)imageData.getHeight());
            Assertions.assertEquals(100, (int)imageData.getWidth());
        }
    }

    @Test
    public void testReadingJbigFromUrl() throws IOException {
        ImageData imageData = ImageDataFactory.createJbig2(UrlUtil.toURL(SOURCE_FOLDER + "image.jb2"), 1);
        Assertions.assertEquals("JBIG2Decode", imageData.getFilter());
        Assertions.assertEquals(1, imageData.getBpc());
    }

    @Test
    public void testCreatingJbigFromCommonMethodByUrl() throws IOException {
        ImageData imageData = ImageDataFactory.create(UrlUtil.toURL(SOURCE_FOLDER + "image.jb2"));
        Assertions.assertTrue(imageData instanceof Jbig2ImageData);
        Assertions.assertEquals(1, ((Jbig2ImageData) imageData).getPage());
    }

    @Test
    public void testCreatingJbigFromCommonMethodByUrlAndBytesProducesSameResult() throws IOException {
        String imageFilePath = SOURCE_FOLDER + "image.jb2";
        ImageData imageDataFromUrl = ImageDataFactory.create(UrlUtil.toURL(imageFilePath));
        try (InputStream fis = FileUtil.getInputStreamForFile(imageFilePath)) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(fis);
            ImageData imageDataFromBytes = ImageDataFactory.create(imageBytes);
            Assertions.assertArrayEquals(imageDataFromBytes.getData(), imageDataFromUrl.getData());
        }
    }

}
