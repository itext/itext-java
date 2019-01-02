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

import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CircleNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/CircleSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/CircleSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "basicCircle");
    }

    @Test
    public void circleCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleCxCyAbsent");
    }

    @Test
    public void circleCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleCxAbsent");
    }

    @Test
    public void circleCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleCxNegative");
    }

    @Test
    public void circleCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleCyAbsent");
    }

    @Test
    public void circleCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleCyNegative");
    }

    @Test
    public void circleRAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleRAbsent");
    }

    @Test
    public void circleRNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleRNegative");
    }

    @Test
    public void circleTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleTranslated");
    }

    @Test
    public void circleRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleRotated");
    }

    @Test
    public void circleScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleScaledUp");
    }

    @Test
    public void circleScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleScaledDown");
    }

    @Test
    public void circleScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleScaledXY");
    }

    @Test
    public void circleSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleSkewX");
    }

    @Test
    public void circleSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompareVisually(sourceFolder, destinationFolder, "circleSkewY");
    }
}
