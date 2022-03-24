/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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


import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PreserveAspectRatioSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void aspectRatioPreservationMidXMidYMeetMinimalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "MidXMidYMeetMinimalTest");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectDefaultAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"PreserveAspectDefaultAll");
    }

    @Test
    public void viewBoxWithoutSetPreserveAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatio");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 19)
    })
    public void differentAspectRatiosTest() throws IOException, InterruptedException {
        //TODO: update cmp_ when DEVSIX-2250 fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "differentAspectRatios");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectDefaultAllGroup() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectDefaultAllGroup");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMin() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMin");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectAll");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMetricDimensionsMin() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMetricDimensionsMin");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMetricDimensionsAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMetricDimensionsAll");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMidMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMidMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMaxMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMaxMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMidYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMidYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMidYMaxMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMidYMaxMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMaxYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMaxYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMaxYMidMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMaxYMidMeetScaling");
    }

    @Test
    public void viewBoxTranslationTestInnerZeroCoordinatesViewBox() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "InnerZeroCoordinatesViewBox");
    }

    @Test
    public void viewBoxTranslationTestOuterZeroCoordinatesViewBox() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "OuterZeroCoordinatesViewBox");
    }

    @Test
    public void viewBoxTranslationTestMultipleViewBoxes() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "MultipleViewBoxes");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed 
    public void svgTranslationYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMinMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMidMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMaxMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMinMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMidMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMaxMeet");
    }
}
