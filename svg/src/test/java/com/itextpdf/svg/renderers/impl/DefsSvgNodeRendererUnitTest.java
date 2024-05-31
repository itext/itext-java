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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefsSvgNodeRendererUnitTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/DefsSvgNodeRendererTest/";

    @Test
    public void processDefsNoChildrenTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(sourceFolder + "onlyDefsWithNoChildren.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);

        Assert.assertTrue(result.getNamedObjects().isEmpty());
    }

    @Test
    public void processDefsOneChildTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(sourceFolder + "onlyDefsWithOneChild.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);

        Assert.assertTrue(result.getNamedObjects().get("circle1") instanceof CircleSvgNodeRenderer);
    }

    @Test
    public void processDefsMultipleChildrenTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(sourceFolder + "onlyDefsWithMultipleChildren.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);

        Assert.assertTrue(result.getNamedObjects().get("circle1") instanceof CircleSvgNodeRenderer);
        Assert.assertTrue(result.getNamedObjects().get("line1") instanceof LineSvgNodeRenderer);
        Assert.assertTrue(result.getNamedObjects().get("rect1") instanceof RectangleSvgNodeRenderer);
    }

    @Test
    public void processDefsParentShouldBeNullTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(sourceFolder + "onlyDefsWithOneChild.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);

        Assert.assertNull(result.getNamedObjects().get("circle1").getParent());
    }

    @Test
    public void noObjectBoundingBoxTest() {
        DefsSvgNodeRenderer renderer = new DefsSvgNodeRenderer();
        Assert.assertNull(renderer.getObjectBoundingBox(null));
    }
}
