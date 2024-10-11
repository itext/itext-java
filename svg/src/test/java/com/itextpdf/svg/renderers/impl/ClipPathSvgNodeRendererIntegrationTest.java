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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class ClipPathSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/ClipPathTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/ClipPathTest/";

    private SvgConverterProperties properties;

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void rectClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_rect_complex");
    }

    @Test
    public void rectClipPathSimpleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_rect_simple");
    }

    @Test
    public void circleClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_circle_complex");
    }

    @Test
    public void circleClipPathSimpleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_circle_simple");
    }

    @Test
    //TODO: update cmp file after DEVSIX-4044 will be fixed
    public void multiClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_multi_complex");
    }

    @Test
    public void moveClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_move");
    }

    @Test
    public void moveClipPathRuleMultipleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_rule_multiple");
    }

    @Test
    public void clipRule() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipRule");
    }

    @Test
    public void clipPathRuleParameterVsFillRule() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipPathRuleParameterVsFillRule");
    }

    @Test
    public void clipPathRuleEvenoddNonzero() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipPathRuleEvenoddNonzero");
    }

    @Test
    //TODO: update after DEVSIX-2827
    public void clipPathCss() throws IOException, InterruptedException {
        properties = new SvgConverterProperties().setBaseUri(sourceFolder);
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipPathCss", properties);
    }

    @Test
    public void clipPathCssProperty() throws IOException, InterruptedException {
        properties = new SvgConverterProperties().setBaseUri(sourceFolder);
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipPathCssProperty", properties);
    }

    @Test
    //TODO: update after DEVSIX-2377
    public void clipPathRulesCombined() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clipPathRulesCombined");
    }

    @Test
    // TODO: DEVSIX-3923 update cmp_ after fix
    public void invalidClipPathTagTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "clippath_invalid_tag");
    }
}
