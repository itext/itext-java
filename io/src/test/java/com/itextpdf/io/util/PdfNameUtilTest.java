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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfNameUtilTest extends ExtendedITextTest {
    @Test
    public void decodeNameTest(){
        // /#C3#9Cberschrift_1
        byte[] name1Content = new byte[] {35, 67, 51, 35, 57, 67, 98, 101, 114, 115, 99, 104, 114, 105, 102, 116, 95, 49};
        Assertions.assertEquals("Ã\u009Cberschrift_1", PdfNameUtil.decodeName(name1Content));
        // /TOC-1
        byte[] name2Content = new byte[] {84, 79, 67, 45, 49};
        Assertions.assertEquals("TOC-1", PdfNameUtil.decodeName(name2Content));
        // /NormalParagraphStyle
        byte[] name3Content = new byte[] {78, 111, 114, 109, 97, 108, 80, 97, 114, 97, 103, 114, 97, 112, 104, 83, 116, 121, 108, 101};
        Assertions.assertEquals("NormalParagraphStyle", PdfNameUtil.decodeName(name3Content));
    }
}
