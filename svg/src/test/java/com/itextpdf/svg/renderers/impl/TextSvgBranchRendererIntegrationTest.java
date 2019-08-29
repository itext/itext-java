/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class TextSvgBranchRendererIntegrationTest extends SvgIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/TextSvgBranchRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/TextSvgBranchRendererIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void helloWorldTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world");
    }

    @Test
    public void tooLongTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "too_long");
    }

    @Test
    public void twoLinesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines");
    }

    @Test
    public void twoLinesNewlineTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines_newline");
    }

    @Test
    public void helloWorldScaleUpXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleUpX");
    }

    @Test
    public void helloWorldScaleUpYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleUpY");
    }

    @Test
    public void helloWorldScaleDownXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleDownX");
    }

    @Test
    public void helloWorldScaleDownYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleDownY");
    }

    @Test
    public void helloWorldTranslateTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_translate");
    }

    @Test
    public void helloWorldRotateTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_rotate");
    }

    @Test
    public void helloWorldSkewXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_skewX");
    }

    @Test
    public void helloWorldSkewYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_skewY");
    }

    @Test
    public void helloWorldCombinedTransformationsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_combinedTransformations");
    }

    @Test
    public void helloWorldFontSizeMissingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_fontSizeMissing");
    }

    //Absolute position
    //X
    @Test
    public void textAbsolutePositionpositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-positiveX");
    }

    @Test
    public void textAbsolutePositionnegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-negativeX");
    }

    @Test
    public void textAbsolutePositionzeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-zeroX");
    }

    @Test
    public void textAbsolutePositionInvalidXTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-invalidX");
    }

    //Y
    @Test
    public void textAbsolutePositionPositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-positiveY");
    }

    @Test
    public void textAbsolutePositionNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-negativeY");
    }

    @Test
    public void textAbsolutePositionZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-zeroY");
    }

    @Test
    public void textAbsolutePositionInvalidYTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-invalidY");
    }

    //Relative move
    //X
    @Test
    public void textRelativeMovePositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-positiveX");
    }

    @Test
    public void textRelativeMoveNegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-negativeX");
    }

    @Test
    public void textRelativeMoveZeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-zeroX");
    }

    @Test
    public void textRelativeMoveInvalidXTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-invalidX");
    }

    //Y
    @Test
    public void textRelativeMovePositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-positiveY");
    }

    @Test
    public void textRelativeMoveNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-negativeY");
    }

    @Test
    public void textRelativeMoveZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-zeroY");
    }

    @Test
    public void textRelativeMoveInvalidYTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-invalidY");
    }


}
