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

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RawImageHelperTest extends ExtendedITextTest {

    @Test
    public void oneBitBlackPixelsTest() {
        PngImageData pngImageData1 = new PngImageData(new byte[1]);
        pngImageData1.setTypeCcitt(256);
        pngImageData1.setColorEncodingComponentsNumber(RawImageData.CCITT_BLACKIS1);
        RawImageHelper.updateImageAttributes(pngImageData1, null);

        Boolean blackIs1 = (Boolean) pngImageData1.getDecodeParms().get("BlackIs1");
        Assertions.assertTrue(blackIs1, "CCITT_BLACKIS1 is false.");
    }

    @Test
    public void extraZeroBitsBeforeEncodedLineTest() {
        PngImageData pngImageData1 = new PngImageData(new byte[1]);
        pngImageData1.setTypeCcitt(256);
        pngImageData1.setColorEncodingComponentsNumber(RawImageData.CCITT_ENCODEDBYTEALIGN);
        RawImageHelper.updateImageAttributes(pngImageData1, null);

        Boolean blackIs1 = (Boolean) pngImageData1.getDecodeParms().get("EncodedByteAlign");
        Assertions.assertTrue(blackIs1, "CCITT_ENCODEDBYTEALIGN is false.");
    }

    @Test
    public void endOfLineBitsPresentTest() {
        PngImageData pngImageData1 = new PngImageData(new byte[1]);
        pngImageData1.setTypeCcitt(256);
        pngImageData1.setColorEncodingComponentsNumber(RawImageData.CCITT_ENDOFLINE);
        RawImageHelper.updateImageAttributes(pngImageData1, null);

        Boolean blackIs1 = (Boolean) pngImageData1.getDecodeParms().get("EndOfLine");
        Assertions.assertTrue(blackIs1, "CCITT_ENDOFLINE is false.");
    }

    @Test
    public void endOfBlockPatternFalseTest() {
        PngImageData pngImageData1 = new PngImageData(new byte[1]);
        pngImageData1.setTypeCcitt(256);
        pngImageData1.setColorEncodingComponentsNumber(RawImageData.CCITT_ENDOFBLOCK);
        RawImageHelper.updateImageAttributes(pngImageData1, null);

        Boolean blackIs1 = (Boolean) pngImageData1.getDecodeParms().get("EndOfBlock");
        Assertions.assertFalse(blackIs1, "CCITT_ENDOFBLOCK is true.");
    }
}
