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
package com.itextpdf.svg.processors;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgIntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class DefaultSvgProcessorIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";

    @Test
    public void DefaultBehaviourTest() throws IOException {
        String svgFile = sourceFolder + "RedCircle.svg";
        InputStream svg = FileUtil.getInputStreamForFile(svgFile);
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg, null);
        IBranchSvgNodeRenderer actual = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root, null).getRootRenderer();

        //Attribute comparison from the known RedCircle.svg
        Map<String, String> attrs = actual.getChildren().get(0).getAttributeMapCopy();

        Assertions.assertEquals(12, attrs.keySet().size(), "Number of parsed attributes is wrong");

        Assertions.assertEquals("1", attrs.get("stroke-opacity"), "The stroke-opacity attribute doesn't correspond it's value");
        Assertions.assertEquals("1.76388889", attrs.get("stroke-width"), "The stroke-width attribute doesn't correspond it's value");

        Assertions.assertEquals("path3699", attrs.get("id"), "The id attribute doesn't correspond it's value");
        Assertions.assertEquals("none", attrs.get("stroke-dasharray"), "The stroke-dasharray attribute doesn't correspond it's value");
    }

    @Test
    public void namedObjectRectangleTest() throws IOException {
        String svgFile = sourceFolder + "namedObjectRectangleTest.svg";
        InputStream svg = FileUtil.getInputStreamForFile(svgFile);
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg, null);
        ISvgProcessorResult processorResult = new DefaultSvgProcessor().process(root, null);
        Map<String, ISvgNodeRenderer> actual = processorResult.getNamedObjects();
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.containsKey("MyRect"));
    }
}
