/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class DefaultSvgProcessorIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void DefaultBehaviourTest() throws IOException {
        String svgFile = sourceFolder + "RedCircle.svg";
        InputStream svg = new FileInputStream(svgFile);
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg, null);
        IBranchSvgNodeRenderer actual = (IBranchSvgNodeRenderer) new DefaultSvgProcessor().process(root, null).getRootRenderer();

        //Attribute comparison from the known RedCircle.svg
        Map<String, String> attrs = actual.getChildren().get(0).getAttributeMapCopy();

        Assert.assertEquals("Number of parsed attributes is wrong", 12, attrs.keySet().size());

        Assert.assertEquals("The stroke-opacity attribute doesn't correspond it's value", "1", attrs.get("stroke-opacity"));
        Assert.assertEquals("The stroke-width attribute doesn't correspond it's value", "1.76388889", attrs.get("stroke-width"));

        Assert.assertEquals("The id attribute doesn't correspond it's value", "path3699", attrs.get("id"));
        Assert.assertEquals("The stroke-dasharray attribute doesn't correspond it's value", "none", attrs.get("stroke-dasharray"));
    }

    @Test
    public void namedObjectRectangleTest() throws IOException {
        String svgFile = sourceFolder + "namedObjectRectangleTest.svg";
        InputStream svg = new FileInputStream(svgFile);
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg, null);
        ISvgProcessorResult processorResult = new DefaultSvgProcessor().process(root, null);
        Map<String, ISvgNodeRenderer> actual = processorResult.getNamedObjects();
        Assert.assertEquals(1, actual.size());
        Assert.assertTrue(actual.containsKey("MyRect"));
    }
}
