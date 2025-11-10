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
package com.itextpdf.svg.css;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class VisibilityTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/VisibilityTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/VisibilityTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void visibilityDiffValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibility");
    }

    @Test
    public void visibilityDiffValuesTest2() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibility2");
    }

    @Test
    public void visibilityHiddenInLinearGradientTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInLinearGradient");
    }

    @Test
    public void visibilityHiddenInMarkerTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInMarker");
    }

    @Test
    public void visibilityHiddenInGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInG");
    }

    @Test
    public void visibilityHiddenInUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInUse");
    }

    @Test
    public void visibilityHiddenInDefsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInDefs");
    }

    @Test
    public void visibilityHiddenInPatternTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInPattern");
    }

    @Test
    public void visibilityHiddenInClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInClipPath");
    }

    @Test
    public void visibilityHiddenInClipPathWithUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInClipPathWithUse");
    }

    @Test
    public void visibilityHiddenInSymbolTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"visibilityHiddenInSymbol");
    }

    @Test
    public void displayNoneInLinearGradientTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInLinearGradient");
    }

    @Test
    public void displayNoneInMarkerTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInMarker");
    }

    @Test
    public void displayNoneInGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInG");
    }

    @Test
    public void displayNoneInG2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInG2");
    }

    @Test
    public void displayNoneInUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInUse");
    }

    @Test
    public void displayNoneInDefsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInDefs");
    }

    @Test
    public void displayNoneInPatternTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInPattern");
    }

    @Test
    public void displayNoneInSymbolTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInSymbol");
    }

    @Test
    public void displayNoneInClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInClipPath");
    }

    @Test
    public void displayNoneInClipPathWithUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"displayNoneInClipPathWithUse");
    }
}
