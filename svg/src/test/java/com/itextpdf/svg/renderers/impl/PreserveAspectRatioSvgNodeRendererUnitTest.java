/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Category(UnitTest.class)
public class PreserveAspectRatioSvgNodeRendererUnitTest {

    private static final Rectangle VIEWPORT_VALUE = PageSize.Default;
    private static final float[] VIEWBOX_VALUES = new float[]{0, 0, 300, 400};

    @Test
    public void processAspectRatioPositionDefault() {
        //default aspect ration is xMidYMid
        String alignValue = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(147.5, 221);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionNone() {
        String alignValue = SvgConstants.Values.NONE;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(0, 0);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMinYMin() {
        String alignValue = SvgConstants.Values.XMIN_YMIN;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(0, 0);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMinYMid() {
        String alignValue = SvgConstants.Values.XMIN_YMID;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(0, 221);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMinYMax() {
        String alignValue = SvgConstants.Values.XMIN_YMAX;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(0, 442);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMidYMin() {
        String alignValue = SvgConstants.Values.XMID_YMIN;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(147.5, 0);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMidYMax() {
        String alignValue = SvgConstants.Values.XMID_YMAX;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(147.5, 442);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMaxYMin() {
        String alignValue = SvgConstants.Values.XMAX_YMIN;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(295, 0);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMaxYMid() {
        String alignValue = SvgConstants.Values.XMAX_YMID;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(295, 221);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void processAspectRatioPositionXMaxYMax() {
        String alignValue = SvgConstants.Values.XMAX_YMAX;
        AffineTransform cmpTransform = new AffineTransform();
        cmpTransform.translate(295, 442);

        processAspectRatioPositionAndCompare(alignValue, cmpTransform);
    }

    @Test
    public void retrieveAlignAndMeetXMinYMinMeet() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetXMinYMinSlice() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetXMinYMinNone() {
        String align = SvgConstants.Values.XMIN_YMIN;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, "");

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetEmptyMeet() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", meet);

        //should fail, because align attribute must be present
        Assert.assertFalse(Arrays.equals(cmpAlignAndMeet, outAlignAndMeet));
    }

    @Test
    public void retrieveAlignAndMeetEmptySlice() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", meet);

        //should fail, because align attribute must be present
        Assert.assertFalse(Arrays.equals(cmpAlignAndMeet, outAlignAndMeet));
    }

    @Test
    public void retrieveAlignAndMeetNoneMeet() {
        String align = SvgConstants.Values.NONE;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetNoneSlice() {
        String align = SvgConstants.Values.NONE;
        String meet = SvgConstants.Values.SLICE;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet(align, meet);

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    @Test
    public void retrieveAlignAndMeetAllDefault() {
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;
        String meet = SvgConstants.Values.MEET;
        String[] cmpAlignAndMeet = new String[]{align, meet};
        String[] outAlignAndMeet = retrieveAlignAndMeet("", "");

        Assert.assertArrayEquals(cmpAlignAndMeet, outAlignAndMeet);
    }

    private void processAspectRatioPositionAndCompare(String alignValue, AffineTransform cmpTransform) {
        SvgDrawContext context = new SvgDrawContext(null, null);

        // topmost viewport has default page size values for bounding rectangle
        context.addViewPort(VIEWPORT_VALUE);

        float[] viewboxValues = VIEWBOX_VALUES;
        float scaleWidth = 1.0f;
        float scaleHeight = 1.0f;

        AbstractBranchSvgNodeRenderer renderer = new SvgTagSvgNodeRenderer();
        Map<String, String> attributesAndStyles = new HashMap<>();
        renderer.setAttributesAndStyles(attributesAndStyles);

        AffineTransform outTransform = renderer.processAspectRatioPosition(context, viewboxValues, alignValue, scaleWidth, scaleHeight);

        Assert.assertTrue(cmpTransform.equals(outTransform));
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
