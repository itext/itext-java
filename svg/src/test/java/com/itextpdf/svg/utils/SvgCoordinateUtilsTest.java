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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgCoordinateUtilsTest extends ExtendedITextTest {

    private final static double DELTA = 0.0000001;

    private final static Rectangle VIEW_BOX = new Rectangle(20F, 20F, 20F, 20F);
    private final static Rectangle VIEW_PORT_HORIZONTAL = new Rectangle(60F, 40F, 100F, 60F);
    private final static Rectangle VIEW_PORT_VERTICAL = new Rectangle(60F, 40F, 60F, 100F);

    @Test
    public void calculateAngleBetweenTwoVectors45degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, 1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }

    @Test
    public void calculateAngleBetweenTwoVectors45degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(1, -1, 0);
        double expected = Math.PI / 4;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }

    @Test
    public void calculateAngleBetweenTwoVectors135degInverseTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, -1, 0);
        double expected = (Math.PI - Math.PI / 4);
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }


    @Test
    public void calculateAngleBetweenTwoVectors90degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(0, 1, 0);
        double expected = Math.PI / 2;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }

    @Test
    public void calculateAngleBetweenTwoVectors180degTest() {
        Vector vectorA = new Vector(1, 0, 0);
        Vector vectorB = new Vector(-1, 0, 0);
        double expected = Math.PI;
        double actual = SvgCoordinateUtils.calculateAngleBetweenTwoVectors(vectorA, vectorB);
        Assert.assertEquals(expected, actual, DELTA);
    }

    @Test
    public void getCoordinateForUserSpaceOnUseDefaultTest() {
        double defaultValue = 244.0;
        double result = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                "random", defaultValue, 0, 0, 0, 0);
        Assert.assertEquals(defaultValue, result, DELTA);
    }

    @Test
    public void getCoordinateForUserSpaceOnUsePercentTest() {
        double result = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                "20%", 0, 10, 20, 0, 0);
        Assert.assertEquals(14.0, result, DELTA);
    }

    @Test
    public void getCoordinateForUserSpaceOnUsePxTest() {
        double result = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                "20px", 0, 0, 0, 0, 0);
        Assert.assertEquals(15.0, result, DELTA);
    }

    @Test
    public void getCoordinateForUserSpaceOnUseEmTest() {
        double result = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                "14em", 0, 0, 0, 10, 18);
        Assert.assertEquals(140.0, result, DELTA);
    }

    @Test
    public void getCoordinateForUserSpaceOnUseRemTest() {
        double result = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                "14rem", 0, 0, 0, 10, 18);
        Assert.assertEquals(252.0, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxPercentTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("20%", 0);
        Assert.assertEquals(0.2, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxNumericFloatingValueTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("1234.3", 0);
        Assert.assertEquals(1234.3, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxNumericIntegerValueTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("1234", 0);
        Assert.assertEquals(1234.0, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxMetricFloatingValueTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("12.3px", 0);
        Assert.assertEquals(12.3, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxMetricIntegerValueTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("12px", 0);
        Assert.assertEquals(12.0, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxRelativeValueTest() {
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("12.3em", 0);
        Assert.assertEquals(12.3, result, DELTA);
    }

    @Test
    public void getCoordinateForObjectBoundingBoxDefaultTest() {
        double defaultValue = 20.0;
        double result = SvgCoordinateUtils.getCoordinateForObjectBoundingBox("random", defaultValue);
        Assert.assertEquals(defaultValue, result, DELTA);
    }

    @Test
    public void applyViewBoxViewBoxIsNullTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(null, new Rectangle(10F, 10F), null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT, e.getMessage());
    }

    @Test
    public void applyViewBoxViewBoxWidthIsZeroTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(new Rectangle(0F, 10F), new Rectangle(10F, 10F), null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT, e.getMessage());
    }

    @Test
    public void applyViewBoxViewBoxHeightIsZeroTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(new Rectangle(10F, 0F), new Rectangle(10F, 10F), null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT, e.getMessage());
    }

    @Test
    public void applyViewBoxViewBoxWidthIsNegativeTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(new Rectangle(-10F, 10F), new Rectangle(10F, 10F), null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT, e.getMessage());
    }

    @Test
    public void applyViewBoxViewBoxHeightIsNegativeTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(new Rectangle(10F, -10F), new Rectangle(10F, 10F), null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.VIEWBOX_IS_INCORRECT, e.getMessage());
    }

    @Test
    public void applyViewBoxCurrentViewPortIsNullTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(new Rectangle(10F, 10F), null, null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.CURRENT_VIEWPORT_IS_NULL, e.getMessage());
    }

    @Test
    public void applyViewBoxAllNullTest() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> SvgCoordinateUtils.applyViewBox(null, null, null, null)
        );
        Assert.assertEquals(SvgExceptionMessageConstant.CURRENT_VIEWPORT_IS_NULL, e.getMessage());
    }

    @Test
    public void applyViewBoxCurrentViewPortZeroWidthHeightTest() {
        Rectangle currentViewPort = new Rectangle(50F, 50F, 0F, 0F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, currentViewPort, null, null);
        Assert.assertTrue(currentViewPort.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxCurrentViewPortNegativeWidthHeightTest() {
        Rectangle currentViewPort = new Rectangle(50F, 50F, -100F, -60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, currentViewPort, null, null);
        Assert.assertTrue(new Rectangle(50F, 70F, -100F, -100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxAlignIsNullSliceTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, null, Values.SLICE);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxAlignIsNullMeetTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, null, Values.MEET);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxAlignIsNullIncorrectMeetOrSliceTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils.applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, null, "jklsdj");
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxMeetOrSliceIsNullXMaxYMaxTest() {
        Rectangle assertRect = new Rectangle(100F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMAX, null);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxMeetOrSliceIsNullXMinYMinTest() {
        Rectangle assertRect = new Rectangle(60F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMIN, null);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxMeetOrSliceIsNullIncorrectAlignTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, "ahfdfs", null);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxIncorrectAlignMeetTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, "ahfdfs", Values.MEET);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxIncorrectAlignSliceTest() {
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, "ahfdfs", Values.SLICE);
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxNoneNullTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.NONE, null);
        Assert.assertNotSame(VIEW_PORT_HORIZONTAL, appliedViewBox);
        Assert.assertTrue(VIEW_PORT_HORIZONTAL.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxNoneMeetTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.NONE, Values.MEET);
        Assert.assertNotSame(VIEW_PORT_HORIZONTAL, appliedViewBox);
        Assert.assertTrue(VIEW_PORT_HORIZONTAL.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxNoneSliceTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.NONE, Values.SLICE);
        Assert.assertNotSame(VIEW_PORT_HORIZONTAL, appliedViewBox);
        Assert.assertTrue(VIEW_PORT_HORIZONTAL.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxNoneMeetOrSliceIsIncorrectTest() {
        //xMidYMid will be processed  cause meetOrSlice is incorrect
        Rectangle assertRect = new Rectangle(80F, 40F, 60F, 60F);
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.NONE, "fhakljs");
        Assert.assertTrue(assertRect.equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMinMeetHorizontalViewPortTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMinSliceHorizontalViewPortTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMinMeetVerticalViewPortTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMinSliceVerticalViewPortTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMidMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMidSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 20F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMidMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 60F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMidSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }


    @Test
    public void applyViewBoxXMinYMaxMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMaxSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMIN_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 0F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMaxMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 80F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMinYMaxSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMIN_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMinMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(80F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMinSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMinMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMinSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(40F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMidMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(80F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMidSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 20F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMidMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 60F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMidSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(40F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMaxMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(80F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMaxSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMID_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 0F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMaxMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 80F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMidYMaxSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMID_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(40F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMinMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(100F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMinSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMinMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMIN, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMinSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMIN, Values.SLICE);
        Assert.assertTrue(new Rectangle(20F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMidMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(100F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMidSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 20F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMidMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMID, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 60F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMidSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMID, Values.SLICE);
        Assert.assertTrue(new Rectangle(20F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMaxMeetHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(100F, 40F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMaxSliceHorizontalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_HORIZONTAL, Values.XMAX_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(60F, 0F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMaxMeetVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMAX, Values.MEET);
        Assert.assertTrue(new Rectangle(60F, 80F, 60F, 60F).equalsWithEpsilon(appliedViewBox));
    }

    @Test
    public void applyViewBoxXMaxYMaxSliceVerticalTest() {
        Rectangle appliedViewBox = SvgCoordinateUtils
                .applyViewBox(VIEW_BOX, VIEW_PORT_VERTICAL, Values.XMAX_YMAX, Values.SLICE);
        Assert.assertTrue(new Rectangle(20F, 40F, 100F, 100F).equalsWithEpsilon(appliedViewBox));
    }
}
