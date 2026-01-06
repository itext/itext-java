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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ClipPathSvgNodeRendererIntegrationTest extends SvgIntegrationTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/ClipPathTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/ClipPathTest/";

    private SvgConverterProperties properties;

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void emptyClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "emptyClipPath");
    }

    @Test
    public void invalidClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidClipPath");
    }

    @Test
    public void rectClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_rect_complex");
    }

    @Test
    public void rectClipPathSimpleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_rect_simple");
    }

    @Test
    public void circleClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_circle_complex");
    }

    @Test
    public void circleClipPathSimpleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_circle_simple");
    }

    @Test
    //TODO DEVSIX-4044 SVG: support outline CSS property
    public void multiClipPathComplexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_multi_complex");
    }

    @Test
    public void moveClipPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_move");
    }

    @Test
    public void moveClipPathRuleMultipleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_rule_multiple");
    }

    @Test
    public void simpleTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleTranslate");
    }

    @Test
    public void clipRule() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipRule");
    }

    @Test
    public void clipPathRuleParameterVsFillRule() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathRuleParameterVsFillRule");
    }

    @Test
    public void clipPathRuleEvenoddNonzero() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathRuleEvenoddNonzero");
    }

    @Test
    public void clipPathCss() throws IOException, InterruptedException {
        properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathCss", properties);
    }

    @Test
    //TODO DEVSIX-2946 Support clip-path CSS property
    public void clipPathCssProperty() throws IOException, InterruptedException {
        properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathCssProperty", properties);
    }

    @Test
    // TODO DEVSIX-2589 Support clip and overflow attribute for symbol
    public void clipPathRulesCombined() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathRulesCombined");
    }

    @Test
    // TODO: DEVSIX-3923 SVG: tags are processed in сase-insensitive way
    public void invalidClipPathTagTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clippath_invalid_tag");
    }

    @Test
    public void clipPathUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathUnits");
    }

    @Test
    public void clipPathUrlTopLevelTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathUrlTopLevel");
    }

    @Test
    public void clipPathUrl2ndLevelTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathUrl2ndLevel");
    }

    @Test
    public void clipPathUseTest() throws IOException, InterruptedException {
        // doesn't work in Chrome and Firefox too
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathUse");
    }

    @Test
    public void clipPathTextSimpleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathText");
    }

    @Test
    public void clipPathSimpleNestedTextTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathSimpleNestedTextTest");
    }

    @Test
    public void clipPathNestedTextTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathNestedTextTest");
    }

    @Test
    public void clipPathNestedTextImageTest() throws IOException, InterruptedException {
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathNestedTextImage", properties);
    }

    @Test
    public void clipPathComplexTest() throws IOException, InterruptedException {
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathComplex", properties);
    }

    @Test
    public void clipPathTranslateText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTranslateText");
    }

    @Test
    public void clipPathNegativeXTranslateText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathNegativeXTranslateText");
    }

    @Test
    public void clipPathNegativeDxText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathNegativeDxText");
    }

    @Test
    public void clipPathTextBoldTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextBold");
    }

    @Test
    public void clipPathTextItalicTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextItalic");
    }

    @Test
    public void clipPathTextItalicBoldTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextItalicBold");
    }

    @Test
    public void clipPathTextMultiObjectsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextMultiObjects");
    }

    @Test
    public void clipPathTextMultiObjects2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextMultiObjects2");
    }

    @Test
    public void clipPathTextMultiObjects3Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextMultiObjects3");
    }

    @Test
    public void clipPathTextLinearGradientTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextLinearGrad");
    }

    @Test
    public void clipPathTextPatternTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextPattern");
    }

    @Test
    public void clipPathTextImageTest() throws IOException, InterruptedException {
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathTextImage", properties);
    }

    @Test
    public void clipPathStrokeText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathStrokeText");
    }

    @Test
    public void clipPathOnlyStrokeText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathOnlyStrokeText");
    }

    @Test
    public void clipPathFillText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathFillText");
    }

    @Test
    public void clipPathUnderlineText() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "clipPathUnderlineText");
    }

    @Test
    public void notUsedClipPathOutsideDefsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "notUsedClipPathOutsideDefs");
    }
}
