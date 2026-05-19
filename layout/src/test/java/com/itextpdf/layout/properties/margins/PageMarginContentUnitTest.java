/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PageMarginContentUnitTest extends ExtendedITextTest {

    @Test
    public void staticMarginsConstructorTest() {
        PageMarginContent pageMarginContentTop = new PageMarginContent(MarginBoxName.TOP, 100);
        UnitValue topMarginHeight = ((Div) pageMarginContentTop.getContent()).getHeight();
        UnitValue topMarginWidth = ((Div) pageMarginContentTop.getContent()).getWidth();
        Assertions.assertEquals(100, topMarginHeight.getValue());
        Assertions.assertNull(topMarginWidth);

        PageMarginContent pageMarginContentBottom = new PageMarginContent(MarginBoxName.BOTTOM, 150);
        UnitValue bottomMarginHeight = ((Div) pageMarginContentBottom.getContent()).getHeight();
        UnitValue bottomMarginWidth = ((Div) pageMarginContentBottom.getContent()).getWidth();
        Assertions.assertEquals(150, bottomMarginHeight.getValue());
        Assertions.assertNull(bottomMarginWidth);

        PageMarginContent pageMarginContentLeft = new PageMarginContent(MarginBoxName.LEFT, 60);
        UnitValue leftMarginHeight = ((Div) pageMarginContentLeft.getContent()).getHeight();
        UnitValue leftMarginWidth = ((Div) pageMarginContentLeft.getContent()).getWidth();
        Assertions.assertNull(leftMarginHeight);
        Assertions.assertEquals(60, leftMarginWidth.getValue());

        PageMarginContent pageMarginContentRight = new PageMarginContent(MarginBoxName.RIGHT, 200);
        UnitValue rightMarginHeight = ((Div) pageMarginContentRight.getContent()).getHeight();
        UnitValue rightMarginWidth = ((Div) pageMarginContentRight.getContent()).getWidth();
        Assertions.assertNull(rightMarginHeight);
        Assertions.assertEquals(200, rightMarginWidth.getValue());
    }
}
