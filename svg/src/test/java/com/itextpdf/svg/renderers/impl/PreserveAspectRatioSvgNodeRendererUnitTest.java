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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Tag("UnitTest")
public class PreserveAspectRatioSvgNodeRendererUnitTest extends ExtendedITextTest {

    private static final Rectangle VIEWPORT_VALUE = PageSize.DEFAULT;
    private static final Rectangle VIEWBOX_VALUE = new Rectangle(0, 0, 300, 400);

    @Test
    public void retrieveAlignAndMeetXMinYMinMeet() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetXMinYMinSlice() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetXMinYMinNone() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, "");

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetEmptyMeet() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", meet);

        //should fail, because align attribute must be present
        Assertions.assertFalse(Arrays.equals(cmpAlignAndMeet, outAlignAndMeet));
    }

    @Test
    public void retrieveAlignAndMeetEmptySlice() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", meet);

        //should fail, because align attribute must be present
        Assertions.assertFalse(Arrays.equals(cmpAlignAndMeet, outAlignAndMeet));
    }

    @Test
    public void retrieveAlignAndMeetNoneMeet() {
        String align = SvgConstants.Values.NONE;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetNoneSlice() {
        String align = SvgConstants.Values.NONE;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetAllDefault() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", "");

        Assertions.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    private String[] retrieveAlignAndMeet(String align, String meet) {
        AbstractBranchSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();
        Map<String, String> attributesAndStyles = new HashMap<>();
        if (!"".equals(align) || !"".equals(meet)) {
            attributesAndStyles.put(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO, align + " " + meet);
        }
        renderer.setAttributesAndStyles(attributesAndStyles);
        return renderer.retrieveAlignAndMeet();
    }
}
