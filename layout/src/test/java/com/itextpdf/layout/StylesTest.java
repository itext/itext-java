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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class StylesTest extends ExtendedITextTest {

    public static float EPS = 0.0001f;

    @Test
    public void copyConstructorTest() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Style copiedStyle = new Style(myStyle);
        Assertions.assertEquals(ColorConstants.RED,
                copiedStyle.<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void addingStyleBeforeSettingPropertyTest() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text")
                .addStyle(myStyle)
                .setFontColor(ColorConstants.GREEN);

        Assertions.assertEquals(ColorConstants.GREEN,
                p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void addingStyleAfterSettingPropertyTest() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text")
                .setFontColor(ColorConstants.GREEN)
                .addStyle(myStyle);

        Assertions.assertEquals(ColorConstants.GREEN,
                p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void addingStyleTest() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assertions.assertEquals(ColorConstants.RED,
                p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void addingSeveralStyleTest() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assertions.assertEquals(ColorConstants.RED,
                p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());

        Style myStyle2 = new Style();
        myStyle2.setFontColor(ColorConstants.GREEN);

        p.addStyle(myStyle2);

        Assertions.assertEquals(ColorConstants.GREEN,
                p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void addNullAsStyleTest() {
        Paragraph p = new Paragraph("text");

        Assertions.assertThrows(IllegalArgumentException.class, () -> p.addStyle(null));
    }

    @Test
    public void setMarginsViaStyleTest() {
        float expectedMarginTop = 92;
        float expectedMarginRight = 90;
        float expectedMarginBottom = 86;
        float expectedMarginLeft = 88;

        Style style = new Style();
        style.setMargins(expectedMarginTop, expectedMarginRight, expectedMarginBottom, expectedMarginLeft);

        Paragraph p = new Paragraph("Hello, iText!");
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginTop),
                p.<UnitValue>getProperty(Property.MARGIN_TOP));
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginRight),
                p.<UnitValue>getProperty(Property.MARGIN_RIGHT));
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginBottom),
                p.<UnitValue>getProperty(Property.MARGIN_BOTTOM));
        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginLeft),
                p.<UnitValue>getProperty(Property.MARGIN_LEFT));
    }

    @Test
    public void setMarginTopViaStyleTest() {
        float expectedMarginTop = 92;

        Style style = new Style();
        style.setMarginTop(expectedMarginTop);

        Paragraph p = new Paragraph("Hello, iText!");
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMarginTop),
                p.<UnitValue>getProperty(Property.MARGIN_TOP));
    }

    @Test
    public void setVerticalAlignmentViaStyleTest() {
        VerticalAlignment expectedAlignment = VerticalAlignment.MIDDLE;

        Style style = new Style();
        style.setVerticalAlignment(expectedAlignment);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(expectedAlignment, p.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));
    }

    @Test
    public void setSpacingRatioViaStyleTest() {
        float expectedSpacingRatio = 0.5f;

        Style style = new Style();
        style.setSpacingRatio(expectedSpacingRatio);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(expectedSpacingRatio, (float) p.<Float>getProperty(Property.SPACING_RATIO), EPS);
    }

    @Test
    public void setKeepTogetherTrueViaStyleTest() {
        Style trueStyle = new Style();
        trueStyle.setKeepTogether(true);

        Paragraph p1 = new Paragraph();
        p1.addStyle(trueStyle);

        Assertions.assertEquals(true, p1.isKeepTogether());
    }

    @Test
    public void setKeepTogetherFalseViaStyleTest() {
        Style falseStyle = new Style();
        falseStyle.setKeepTogether(false);

        Paragraph p = new Paragraph();
        p.addStyle(falseStyle);

        Assertions.assertEquals(false, p.isKeepTogether());
    }

    @Test
    public void setRotationAngleViaStyleTest() {
        float expectedRotationAngle = 20f;

        Style style = new Style();
        style.setRotationAngle(expectedRotationAngle);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(expectedRotationAngle, (float) p.<Float>getProperty(Property.ROTATION_ANGLE), EPS);
    }

    @Test
    public void setWidthViaStyleTest() {
        float expectedWidth = 100;

        Style style = new Style();
        style.setWidth(expectedWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedWidth), p.<UnitValue>getProperty(Property.WIDTH));
    }

    @Test
    public void setWidthInUnitValueViaStyleTest() {
        float expectedWidth = 100;

        Style style = new Style();
        style.setWidth(UnitValue.createPointValue(expectedWidth));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedWidth), p.<UnitValue>getProperty(Property.WIDTH));
    }

    @Test
    public void setHeightViaStyleTest() {
        float expectedHeight = 100;

        Style style = new Style();
        style.setHeight(expectedHeight);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedHeight), p.<UnitValue>getProperty(Property.HEIGHT));
    }

    @Test
    public void setHeightInUnitValueViaStyleTest() {
        float expectedHeight = 100;

        Style style = new Style();
        style.setHeight(UnitValue.createPointValue(expectedHeight));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedHeight), p.<UnitValue>getProperty(Property.HEIGHT));
    }

    @Test
    public void setMaxHeightViaStyleTest() {
        float expectedMaxHeight = 80;

        Style style = new Style();
        style.setMaxHeight(UnitValue.createPointValue(expectedMaxHeight));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxHeight),
                p.<UnitValue>getProperty(Property.MAX_HEIGHT));
    }

    @Test
    public void setMinHeightViaStyleTest() {
        float expectedMinHeight = 20;

        Style style = new Style();
        style.setMinHeight(expectedMinHeight);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinHeight),
                p.<UnitValue>getProperty(Property.MIN_HEIGHT));
    }

    @Test
    public void setMaxWidthViaStyleTest() {
        float expectedMaxWidth = 200;

        Style style = new Style();
        style.setMaxWidth(expectedMaxWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMaxWidth), p.<UnitValue>getProperty(Property.MAX_WIDTH));
    }

    @Test
    public void setMinWidthViaStyleTest() {
        float expectedMinWidth = 20;

        Style style = new Style();
        style.setMinWidth(expectedMinWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assertions.assertEquals(UnitValue.createPointValue(expectedMinWidth), p.<UnitValue>getProperty(Property.MIN_WIDTH));
    }
}
