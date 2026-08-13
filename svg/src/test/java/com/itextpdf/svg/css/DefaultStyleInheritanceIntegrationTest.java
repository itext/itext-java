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
package com.itextpdf.svg.css;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class DefaultStyleInheritanceIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/DefaultInheritance/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/css/DefaultInheritance/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    //Css inheritance
    @Test
    public void simpleGroupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"simpleGroupInheritance");
    }
    //Inheritance in use tags
    @Test
    public void useFillInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"useFillInheritance");
    }
    //Inheritance and g-tags
    @Test
    public void groupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"groupInheritance");
    }

    @Test
    public void useInheritanceNotOverridingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"useInheritanceNotOverriding");
    }

    @Test
    public void usePropertiesInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"usePropertiesInheritance");
    }

    @Test
    public void fillOpacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"fillOpacityInheritance");
    }

    @Test
    public void fillRuleInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"fillRuleInheritance");
    }

    @Test
    public void strokeInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeInheritance");
    }

    @Test
    public void strokeWidthInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeWidthInheritance");
    }

    @Test
    public void strokeOpacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeOpacityInheritance");
    }

    @Test
    public void strokeLinecapInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeLinecapInheritance");
    }

    @Test
    public void strokeLinejoinInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeLinejoinInheritance");
    }

    @Test
    public void strokeDasharrayInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeDasharrayInheritance");
    }

    @Test
    public void strokeDashoffsetInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"strokeDashoffsetInheritance");
    }

    @Test
    public void opacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"opacityInheritance");
    }

    @Test
    //TODO DEVSIX-4114 support vertical text attribute
    public void textPropertiesInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"textPropertiesInheritance");
    }

    @Test
    public void nestedInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"nestedInheritance");
    }
}
