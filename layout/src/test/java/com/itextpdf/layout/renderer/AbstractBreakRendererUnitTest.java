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
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class AbstractBreakRendererUnitTest extends ExtendedITextTest {

    @Test
    public void hasOwnPropertyTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);

        Assertions.assertTrue(abstractBreakRenderer.hasOwnProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void hasPropertyFromItselfTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);

        Assertions.assertTrue(abstractBreakRenderer.hasProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void hasPropertyFromParentTest() {
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);
        AbstractBreakRenderer abstractBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        divRenderer.addChildRenderer(abstractBreakRenderer);

        Assertions.assertTrue(abstractBreakRenderer.hasProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void getPropertyFromItselfTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);

        Assertions.assertTrue(abstractBreakRenderer.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void getPropertyFromParentTest() {
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);
        AbstractBreakRenderer abstractBreakRenderer = new AreaBreakRenderer(new AreaBreak());
        divRenderer.addChildRenderer(abstractBreakRenderer);

        Assertions.assertTrue(abstractBreakRenderer.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void getPropertyNotFoundTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());

        Assertions.assertNull(abstractBreakRenderer.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void getPropertyWithDefaultArgumentNotUsedTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);

        Assertions.assertTrue(
                abstractBreakRenderer.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.FALSE));
    }

    @Test
    public void getPropertyWithDefaultArgumentUsedTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());

        Assertions.assertFalse(
                abstractBreakRenderer.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.FALSE));
    }

    @Test
    public void getOwnPropertyTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);

        Assertions.assertTrue(abstractBreakRenderer.<Boolean>getOwnProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }

    @Test
    public void deleteOwnPropertyTest() {
        AbstractBreakRenderer abstractBreakRenderer = new SectionBreakRenderer(new SectionBreak());
        abstractBreakRenderer.setProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS, Boolean.TRUE);
        abstractBreakRenderer.deleteOwnProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS);

        Assertions.assertNull(abstractBreakRenderer.<Boolean>getOwnProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS));
    }
}
