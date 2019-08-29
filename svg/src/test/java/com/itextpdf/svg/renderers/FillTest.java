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
package com.itextpdf.svg.renderers;

import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class FillTest extends SvgIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/FillTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/FillTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void normalRectangleFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "normalRectangleFill");
    }

    @Test
    public void multipleNormalRectangleFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleNormalRectangleFill");
    }

    @Test
    public void noRectangleFillColorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noRectangleFillColor");
    }

    @Test
    public void eoFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill");
    }

    /* This test should fail when RND-910 is resolved*/
    @Test
    public void eoFillTest01() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill01");
    }

    @Test
    public void eoFillTest02() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill02");
    }

    @Test
    public void eoFillTest03() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill03");
    }

    @Test
    public void  multipleObjectsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleObjectsTest");
    }

    @Test
    public void eoFillStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofillstroke");
    }

    /* This test should fail when RND-1031 is resolved*/
    @Test
    public void nonZeroFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nonzerofill");
    }

    @Test
    public void opacityFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacityfill");
    }

    /* This test should fail when RND-1108 is resolved*/
    @Test
    public void eofillUnsuportedAtributeTest() throws IOException, InterruptedException {
        junitExpectedException.expect(SvgProcessingException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofillUnsuportedAtributeTest");
    }

    @Test
    public void pathVerticalLineFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathVerticalLineFillTest");
    }

    @Test
    public void pathHorizontalLineFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHorizontalLineFillTest");
    }
}
