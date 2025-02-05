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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GhostScriptHelperUnitTest extends ExtendedITextTest {

    @Test
    public void verifyEmptyPageList() {
        String testPageList = "";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyNullPageList() {
        String testPageList = null;

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyPageListWithLeadingSpaces() {
        String testPageList = "     1";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyPageListWithTrailingSpaces() {
        String testPageList = "1     ";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyValidPageListWithSeveralPages() {
        String testPageList = "1,2,3";

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyValidPageListOfOnePage() {
        String testPageList = "2";

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    // It's worth mentioning that gs allows negative arguments: if one of the passed list numbers is negative,
    // then all the pages are processed. However, if "0" is passed, then no pages are processed.
    // Having said that, at iText level we're strict and do not allow such values.
    public void verifyPageListWithNegativePages() {
        String testPageList = "-2";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    // It's worth mentioning that gs allows negative arguments: if one of the passed list numbers is negative,
    // then all the pages are processed. However, if "0" is passed, then no pages are processed.
    // Having said that, at iText level we're strict and do not allow such values.
    public void verifyPageListWithSomeNegativePagesInTheMiddle() {
        String testPageList = "1,-2,3";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    // It's worth mentioning that gs allows negative arguments: if one of the passed list numbers is negative,
    // then all the pages are processed. However, if "0" is passed, then no pages are processed.
    // Having said that, at iText level we're strict and do not allow such values.
    public void verifyPageListWithSomeNegativePagesAtTheEnd() {
        String testPageList = "1,-2";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyPageListWithOnlyPageZero() {
        String testPageList = "0";

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyPageListWithOneOfPagesBeingZero() {
        String testPageList = "3,0,2";

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    // For gs the order doesn't play any role
    public void verifyValidPageListWithDescendingOrder() {
        String testPageList = "3,2,1";

        Assertions.assertTrue(GhostscriptHelper.validatePageList(testPageList));
    }

    @Test
    public void verifyTextInPageList() {
        String testPageList = "1,hello,2";

        Assertions.assertFalse(GhostscriptHelper.validatePageList(testPageList));
    }
}
