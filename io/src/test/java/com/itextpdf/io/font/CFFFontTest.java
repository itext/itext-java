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
package com.itextpdf.io.font;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CFFFontTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/sharedFontsResourceFiles/";

    @Test
    public void seekTest() throws IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory()
                .createBestSource(SOURCE_FOLDER + "NotoSansCJKjp-Bold.otf"));

        int offsetToCff = 259880;
        int cffLength = 16023217;
        byte[] cff = new byte[cffLength];
        try {
            raf.seek(offsetToCff);
            raf.readFully(cff);
        } finally {
            raf.close();
        }
        CFFFont cffFont = new CFFFont(cff);

        cffFont.seek(0);
        // Get int (bin 0000 0001 0000 0000  0000 0100 0000 0011)
        Assertions.assertEquals(16778243, cffFont.getInt());
        cffFont.seek(0);
        // Gets the first short (bin 0000 0001 0000 0000)
        Assertions.assertEquals(256, cffFont.getShort());
        cffFont.seek(2);
        // Gets the second short (bin 0000 0100 0000 0011)
        Assertions.assertEquals(1027, cffFont.getShort());
    }

    @Test
    public void getPositionTest() throws IOException {
        RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory()
                .createBestSource(SOURCE_FOLDER + "NotoSansCJKjp-Bold.otf"));

        int offsetToCff = 259880;
        int cffLength = 16023217;
        byte[] cff = new byte[cffLength];
        try {
            raf.seek(offsetToCff);
            raf.readFully(cff);
        } finally {
            raf.close();
        }
        CFFFont cffFont = new CFFFont(cff);


        cffFont.seek(0);
        Assertions.assertEquals(0, cffFont.getPosition());
        cffFont.seek(16);
        Assertions.assertEquals(16, cffFont.getPosition());
    }
}
