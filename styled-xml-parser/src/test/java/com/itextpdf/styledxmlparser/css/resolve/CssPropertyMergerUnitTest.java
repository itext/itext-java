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
package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssPropertyMergerUnitTest extends ExtendedITextTest {

    @Test
    public void mergeTextDecorationSimpleTest(){
        String firstValue="underline";
        String secondValue="strikethrough bold";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationNormalizeFirstTest(){
        String firstValue="   underline  ";
        String secondValue="strikethrough bold";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);

    }

    @Test
    public void mergeTextDecorationNormalizeSecondTest(){
        String firstValue="underline";
        String secondValue="strikethrough     bold   ";

        String expected="underline strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationFirstNullTest(){
        String firstValue=null;
        String secondValue="strikethrough bold";

        String expected="strikethrough bold";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationSecondNullTest(){
        String firstValue="underline";
        String secondValue=null;

        String expected="underline";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationBothNullTest(){
        String firstValue=null;
        String secondValue=null;

        String expected=null;
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }


    @Test
    public void mergeTextDecorationEmpyInputsTest(){
        String firstValue="";
        String secondValue="";

        String expected="none";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationSecondInputContainsNoneTest(){
        String firstValue="underline";
        String secondValue="none strikethrough";

        String expected="underline";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationFirstInputNoneTest(){
        String firstValue="underline none";
        String secondValue="strikethrough";

        String expected="strikethrough";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void mergeTextDecorationBothInputsNoneTest(){
        String firstValue="underline none";
        String secondValue="strikethrough none";

        String expected="none";
        String actual = CssPropertyMerger.mergeTextDecoration(firstValue,secondValue);

        Assertions.assertEquals(expected,actual);
    }

}
