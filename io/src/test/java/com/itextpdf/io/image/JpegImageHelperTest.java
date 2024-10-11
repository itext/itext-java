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
package com.itextpdf.io.image;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class JpegImageHelperTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/image/";

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DURING_CONSTRUCTION_OF_ICC_PROFILE_ERROR_OCCURRED, logLevel = LogLevelConstants.ERROR)
    })
    public void attemptToSetInvalidIccProfileToImageTest() throws IOException {
        try (InputStream fis = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "WP_20140410_001.jpg")) {

            ImageData img = ImageDataFactory.createJpeg(StreamUtil.inputStreamToArray(fis));
            int size = 100;
            // Instantiate new byte[size][] instead new byte[size][size] necessary for autoporting
            byte[][] icc = new byte[size][];
            for (int i = 0; i < size; i++) {
                icc[i] = new byte[size];
                for (int j = 0; j < size; j++) {
                    icc[i][j] = (byte) j;
                }
            }
            AssertUtil.doesNotThrow(() -> JpegImageHelper.attemptToSetIccProfileToImage(icc, img));
        }
    }

    @Test
    public void attemptToSetNullIccProfileToImageTest() throws IOException {
        try (InputStream fis = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "WP_20140410_001.jpg")) {
            byte[][] icc = new byte[][] {null, null};
            ImageData img = ImageDataFactory.createJpeg(StreamUtil.inputStreamToArray(fis));
            AssertUtil.doesNotThrow(() -> JpegImageHelper.attemptToSetIccProfileToImage(icc, img));
        }
    }
}
