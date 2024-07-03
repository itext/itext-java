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
package com.itextpdf.barcodes;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BarcodePDF417UnitTest extends ExtendedITextTest {

    @Test
    public void barcode417CodeRowsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeRows(150);

        Assertions.assertEquals(150, barcode.getCodeRows());
    }

    @Test
    public void barcode417CodeColumnsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeColumns(150);

        Assertions.assertEquals(150, barcode.getCodeColumns());
    }

    @Test
    public void barcode417CodeWordsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setLenCodewords(150);

        Assertions.assertEquals(150, barcode.getLenCodewords());
    }

    @Test
    public void barcode417ErrorLevelTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setErrorLevel(3);

        Assertions.assertEquals(3, barcode.getErrorLevel());
    }

    @Test
    public void barcode417GetCodeWordsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();

        Assertions.assertEquals(928, barcode.getCodewords().length);
    }

    @Test
    public void barcode417OptionsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setOptions(100);

        Assertions.assertEquals(100, barcode.getOptions());
    }

    @Test
    public void barcode417MaxSquareTest() {
        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);

        Assertions.assertEquals(928, barcode.getMaxSquare());
    }
}
