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
package com.itextpdf.svg.css;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class DefaultStyleInheritanceIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/DefaultInheritance/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/css/DefaultInheritance/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    //Css inheritance
    @Test
    public void simpleGroupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"simpleGroupInheritance");
    }
    //Inheritance in use tags
    @Test
    public void useFillInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"useFillInheritance");
    }
    //Inheritance and g-tags
    @Test
    public void groupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"groupInheritance");
    }

    @Test
    public void useInheritanceNotOverridingTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"useInheritanceNotOverriding");
    }

    @Test
    public void usePropertiesInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"usePropertiesInheritance");
    }

    @Test
    public void fillOpacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"fillOpacityInheritance");
    }

    @Test
    public void fillRuleInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"fillRuleInheritance");
    }

    @Test
    public void strokeInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeInheritance");
    }

    @Test
    public void strokeWidthInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeWidthInheritance");
    }

    @Test
    public void strokeOpacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeOpacityInheritance");
    }

    @Test
    //TODO DEVSIX-4857 support stroke-linecap attribute
    public void strokeLinecapInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeLinecapInheritance");
    }

    @Test
    public void strokeLinejoinInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeLinejoinInheritance");
    }

    @Test
    public void strokeDasharrayInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeDasharrayInheritance");
    }

    @Test
    public void strokeDashoffsetInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"strokeDashoffsetInheritance");
    }

    @Test
    public void opacityInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"opacityInheritance");
    }

    @Test
    //TODO DEVSIX-4114 support vertical text attribute
    public void textPropertiesInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"textPropertiesInheritance");
    }

    @Test
    public void nestedInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"nestedInheritance");
    }
}
