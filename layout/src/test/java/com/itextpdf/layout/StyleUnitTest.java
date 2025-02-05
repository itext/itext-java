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
package com.itextpdf.layout;

import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class StyleUnitTest extends ExtendedITextTest {

    public static float EPS = 0.0001f;

    @Test
    public void setAndGetMarginsTest() {
        float expectedMarginTop = 92;
        float expectedMarginRight = 90;
        float expectedMarginBottom = 86;
        float expectedMarginLeft = 88;

        Style style = new Style();

        Assertions.assertNull(style.getMarginTop());
        Assertions.assertNull(style.getMarginRight());
        Assertions.assertNull(style.getMarginBottom());
        Assertions.assertNull(style.getMarginLeft());
        
        style.setMargins(expectedMarginTop, expectedMarginRight, expectedMarginBottom, expectedMarginLeft);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginTop),
                style.getMarginTop());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginRight),
                style.getMarginRight());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginBottom),
                style.getMarginBottom());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginLeft),
                style.getMarginLeft());
    }

    @Test
    public void setMarginTest() {
        float expectedMargin = 90;

        Style style = new Style();

        Assertions.assertNull(style.getMarginTop());
        Assertions.assertNull(style.getMarginRight());
        Assertions.assertNull(style.getMarginBottom());
        Assertions.assertNull(style.getMarginLeft());

        style.setMargin(expectedMargin);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMargin), style.getMarginTop());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMargin), style.getMarginRight());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMargin), style.getMarginBottom());
        Assertions.assertEquals(UnitValue.createPointValue(expectedMargin), style.getMarginLeft());
    }

    @Test
    public void getMarginLeftTest() {
        float expLeftMargin = 88;

        Style style = new Style();

        Assertions.assertNull(style.getMarginLeft());

        style.setMarginLeft(expLeftMargin);

        Assertions.assertEquals(UnitValue.createPointValue(expLeftMargin), style.getMarginLeft());
    }

    @Test
    public void getMarginRightTest() {
        float expRightMargin = 90;

        Style style = new Style();

        Assertions.assertNull(style.getMarginRight());

        style.setMarginRight(expRightMargin);

        Assertions.assertEquals(UnitValue.createPointValue(expRightMargin), style.getMarginRight());
    }

    @Test
    public void getMarginTopTest() {
        float expTopMargin = 92;

        Style style = new Style();

        Assertions.assertNull(style.getMarginTop());

        style.setMarginTop(expTopMargin);

        Assertions.assertEquals(UnitValue.createPointValue(expTopMargin), style.getMarginTop());
    }

    @Test
    public void getMarginBottomTest() {
        float expBottomMargin = 86;

        Style style = new Style();

        Assertions.assertNull(style.getMarginTop());

        style.setMarginBottom(expBottomMargin);

        Assertions.assertEquals(UnitValue.createPointValue(expBottomMargin), style.getMarginBottom());
    }

    @Test
    public void getPaddingLeftTest() {
        float expLeftPadding = 6;

        Style style = new Style();

        Assertions.assertNull(style.getPaddingLeft());

        style.setPaddingLeft(expLeftPadding);

        Assertions.assertEquals(UnitValue.createPointValue(expLeftPadding), style.getPaddingLeft());
    }

    @Test
    public void getPaddingRightTest() {
        float expRightPadding = 8;

        Style style = new Style();

        Assertions.assertNull(style.getPaddingRight());

        style.setPaddingRight(expRightPadding);

        Assertions.assertEquals(UnitValue.createPointValue(expRightPadding), style.getPaddingRight());
    }

    @Test
    public void getPaddingTopTest() {
        float expTopPadding = 10;

        Style style = new Style();

        Assertions.assertNull(style.getPaddingTop());

        style.setPaddingTop(expTopPadding);

        Assertions.assertEquals(UnitValue.createPointValue(expTopPadding), style.getPaddingTop());
    }

    @Test
    public void getPaddingBottomTest() {
        float expBottomPadding = 5;

        Style style = new Style();

        Assertions.assertNull(style.getPaddingBottom());

        style.setPaddingBottom(expBottomPadding);

        Assertions.assertEquals(UnitValue.createPointValue(expBottomPadding), style.getPaddingBottom());
    }

    @Test
    public void setPaddingTest() {
        float expPadding = 10;
        Style style = new Style();

        Assertions.assertNull(style.getPaddingTop());
        Assertions.assertNull(style.getPaddingRight());
        Assertions.assertNull(style.getPaddingBottom());
        Assertions.assertNull(style.getPaddingLeft());

        style.setPadding(expPadding);

        Assertions.assertEquals(UnitValue.createPointValue(expPadding), style.getPaddingTop());
        Assertions.assertEquals(UnitValue.createPointValue(expPadding), style.getPaddingRight());
        Assertions.assertEquals(UnitValue.createPointValue(expPadding), style.getPaddingBottom());
        Assertions.assertEquals(UnitValue.createPointValue(expPadding), style.getPaddingLeft());
    }

    @Test
    public void setPaddingsTest() {
        float expPaddingTop = 10;
        float expPaddingRight = 8;
        float expPaddingBottom = 5;
        float expPaddingLeft = 6;

        Style style = new Style();

        Assertions.assertNull(style.getPaddingTop());
        Assertions.assertNull(style.getPaddingRight());
        Assertions.assertNull(style.getPaddingBottom());
        Assertions.assertNull(style.getPaddingLeft());

        style.setPaddings(expPaddingTop, expPaddingRight, expPaddingBottom, expPaddingLeft);

        Assertions.assertEquals(UnitValue.createPointValue(expPaddingLeft), style.getPaddingLeft());
        Assertions.assertEquals(UnitValue.createPointValue(expPaddingBottom), style.getPaddingBottom());
        Assertions.assertEquals(UnitValue.createPointValue(expPaddingTop), style.getPaddingTop());
        Assertions.assertEquals(UnitValue.createPointValue(expPaddingRight), style.getPaddingRight());
    }

    @Test
    public void setVerticalAlignmentMiddleTest() {
        VerticalAlignment expectedAlignment = VerticalAlignment.MIDDLE;

        Style style = new Style();

        Assertions.assertNull(style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));

        style.setVerticalAlignment(expectedAlignment);

        Assertions.assertEquals(expectedAlignment, style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));
    }

    @Test
    public void setVerticalAlignmentTopTest() {
        VerticalAlignment expectedAlignment = VerticalAlignment.TOP;

        Style style = new Style();

        Assertions.assertNull(style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));

        style.setVerticalAlignment(expectedAlignment);

        Assertions.assertEquals(expectedAlignment, style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));
    }

    @Test
    public void setVerticalAlignmentBottomTest() {
        VerticalAlignment expectedAlignment = VerticalAlignment.BOTTOM;

        Style style = new Style();

        Assertions.assertNull(style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));

        style.setVerticalAlignment(expectedAlignment);

        Assertions.assertEquals(expectedAlignment, style.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));
    }

    @Test
    public void setSpacingRatioTest() {
        float expectedSpacingRatio = 0.5f;

        Style style = new Style();

        Assertions.assertNull(style.<Float>getProperty(Property.SPACING_RATIO));

        style.setSpacingRatio(expectedSpacingRatio);

        Assertions.assertEquals(expectedSpacingRatio, (float)style.<Float>getProperty(Property.SPACING_RATIO), EPS);
    }

    @Test
    public void setKeepTogetherTrueTest() {
        Style style = new Style();

        Assertions.assertNull(style.<Boolean>getProperty(Property.KEEP_TOGETHER));

        style.setKeepTogether(true);

        Assertions.assertEquals(true, style.<Boolean>getProperty(Property.KEEP_TOGETHER));
    }

    @Test
    public void setKeepTogetherFalseTest() {
        Style style = new Style();

        Assertions.assertNull(style.<Boolean>getProperty(Property.KEEP_TOGETHER));

        style.setKeepTogether(false);

        Assertions.assertEquals(false, style.<Boolean>getProperty(Property.KEEP_TOGETHER));
    }

    @Test
    public void isKeepTogetherTest() {
        Style style = new Style();

        Assertions.assertNull(style.<Boolean>getProperty(Property.KEEP_TOGETHER));

        style.setKeepTogether(true);

        Assertions.assertEquals(true, style.isKeepTogether());
    }

    @Test
    public void setRotationAngleFloatTest() {
        float expectedRotationAngle = 20f;

        Style style = new Style();

        Assertions.assertNull(style.<Float>getProperty(Property.ROTATION_ANGLE));

        style.setRotationAngle(expectedRotationAngle);

        Assertions.assertEquals(expectedRotationAngle, (float)style.<Float>getProperty(Property.ROTATION_ANGLE), EPS);
    }

    @Test
    public void setRotationAngleDoubleTest() {
        double expectedRotationAngle = 20.0;

        Style style = new Style();

        Assertions.assertNull(style.<Float>getProperty(Property.ROTATION_ANGLE));

        style.setRotationAngle(expectedRotationAngle);

        Assertions.assertEquals(expectedRotationAngle, (float)style.<Float>getProperty(Property.ROTATION_ANGLE), EPS);
    }

    @Test
    public void setAndGetWidthTest() {
        float expectedWidth = 100;

        Style style = new Style();

        Assertions.assertNull(style.getWidth());

        style.setWidth(expectedWidth);

        Assertions.assertEquals(UnitValue.createPointValue(expectedWidth), style.getWidth());
    }

    @Test
    public void setAndGetWidthUnitValueTest() {
        float expectedWidth = 50;

        Style style = new Style();

        Assertions.assertNull(style.getWidth());

        style.setWidth(UnitValue.createPointValue(expectedWidth));

        Assertions.assertEquals(UnitValue.createPointValue(expectedWidth), style.getWidth());
    }

    @Test
    public void setAndGetHeightTest() {
        float expectedHeight = 100;

        Style style = new Style();

        Assertions.assertNull(style.getHeight());

        style.setHeight(expectedHeight);

        Assertions.assertEquals(UnitValue.createPointValue(expectedHeight), style.getHeight());
    }

    @Test
    public void setAndGetHeightUnitValueTest() {
        float expectedHeight = 50;

        Style style = new Style();

        Assertions.assertNull(style.getHeight());

        style.setHeight(UnitValue.createPointValue(expectedHeight));

        Assertions.assertEquals(UnitValue.createPointValue(expectedHeight), style.getHeight());
    }

    @Test
    public void setMaxHeightTest() {
        float expectedMaxHeight = 80;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MAX_HEIGHT));

        style.setMaxHeight(expectedMaxHeight);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxHeight), style.<UnitValue>getProperty(Property.MAX_HEIGHT));
    }

    @Test
    public void setMaxHeightUnitValueTest() {
        float expectedMaxHeight = 50;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MAX_HEIGHT));

        style.setMaxHeight(UnitValue.createPointValue(expectedMaxHeight));

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxHeight), style.<UnitValue>getProperty(Property.MAX_HEIGHT));
    }

    @Test
    public void setMinHeightTest() {
        float expectedMinHeight = 50;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MIN_HEIGHT));

        style.setMinHeight(expectedMinHeight);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinHeight), style.<UnitValue>getProperty(Property.MIN_HEIGHT));
    }

    @Test
    public void setMinHeightUnitValueTest() {
        float expectedMinHeight = 30;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MIN_HEIGHT));

        style.setMinHeight(UnitValue.createPointValue(expectedMinHeight));

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinHeight), style.<UnitValue>getProperty(Property.MIN_HEIGHT));
    }

    @Test
    public void setMaxWidthTest() {
        float expectedMaxWidth = 200;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MAX_WIDTH));

        style.setMaxWidth(expectedMaxWidth);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxWidth), style.<UnitValue>getProperty(Property.MAX_WIDTH));
    }

    @Test
    public void setMaxWidthUnitValueTest() {
        float expectedMaxWidth = 150;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MAX_WIDTH));

        style.setMaxWidth(UnitValue.createPointValue(expectedMaxWidth));

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxWidth), style.<UnitValue>getProperty(Property.MAX_WIDTH));
    }

    @Test
    public void setMinWidthTest() {
        float expectedMinWidth = 50;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MIN_WIDTH));

        style.setMinWidth(expectedMinWidth);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinWidth), style.<UnitValue>getProperty(Property.MIN_WIDTH));
    }

    @Test
    public void setMinWidthUnitValueTest() {
        float expectedMinWidth = 30;

        Style style = new Style();

        Assertions.assertNull(style.<UnitValue>getProperty(Property.MIN_WIDTH));

        style.setMinWidth(UnitValue.createPointValue(expectedMinWidth));

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinWidth), style.<UnitValue>getProperty(Property.MIN_WIDTH));
    }
}
