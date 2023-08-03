/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class DefaultStyleInheritanceIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/DefaultInheritance/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/css/DefaultInheritance/";

    @BeforeClass
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

}
