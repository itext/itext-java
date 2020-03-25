/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TransparentColor;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class StylesTest extends ExtendedITextTest {

    public static float EPS = 0.0001f;

    @Test
    public void stylesTest01() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text")
                .addStyle(myStyle)
                .setFontColor(ColorConstants.GREEN);

        Assert.assertEquals(ColorConstants.GREEN, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void stylesTest02() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assert.assertEquals(ColorConstants.RED, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void stylesTest03() {
        Style myStyle = new Style();
        myStyle.setFontColor(ColorConstants.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assert.assertEquals(ColorConstants.RED, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());

        Style myStyle2 = new Style();
        myStyle2.setFontColor(ColorConstants.GREEN);

        p.addStyle(myStyle2);

        Assert.assertEquals(ColorConstants.GREEN, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void getMarginsTest() {
        float expectedMarginTop = 92;
        float expectedMarginRight = 90;
        float expectedMarginBottom = 86;
        float expectedMarginLeft = 88;

        Style style = new Style();
        style.setMargins(expectedMarginTop, expectedMarginRight, expectedMarginBottom, expectedMarginLeft);

        Paragraph p = new Paragraph("Hello, iText!");
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMarginTop),
                p.<UnitValue>getProperty(Property.MARGIN_TOP));
        Assert.assertEquals(UnitValue.createPointValue(expectedMarginRight),
                p.<UnitValue>getProperty(Property.MARGIN_RIGHT));
        Assert.assertEquals(UnitValue.createPointValue(expectedMarginBottom),
                p.<UnitValue>getProperty(Property.MARGIN_BOTTOM));
        Assert.assertEquals(UnitValue.createPointValue(expectedMarginLeft),
                p.<UnitValue>getProperty(Property.MARGIN_LEFT));
    }

    @Test
    public void getMarginTopTest() {
        float expectedMarginTop = 92;

        Style style = new Style();
        style.setMarginTop(expectedMarginTop);

        Paragraph p = new Paragraph("Hello, iText!");
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMarginTop),
                p.<UnitValue>getProperty(Property.MARGIN_TOP));
    }

    @Test
    public void setVerticalAlignmentTest() {
        VerticalAlignment expectedAlignment = VerticalAlignment.MIDDLE;

        Style style = new Style();
        style.setVerticalAlignment(expectedAlignment);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(expectedAlignment, p.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT));
    }

    @Test
    public void setSpacingRatioTest() {
        float expectedSpacingRatio = 0.5f;

        Style style = new Style();
        style.setSpacingRatio(expectedSpacingRatio);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(expectedSpacingRatio, (float)p.<Float>getProperty(Property.SPACING_RATIO), EPS);
    }

    @Test
    public void keepTogetherTrueTest() {
        Style trueStyle = new Style();
        trueStyle.setKeepTogether(true);

        Paragraph p1 = new Paragraph();
        p1.addStyle(trueStyle);

        Assert.assertEquals(true, p1.isKeepTogether());
    }

    @Test
    public void keepTogetherFalseTest() {
        Style falseStyle = new Style();
        falseStyle.setKeepTogether(false);

        Paragraph p = new Paragraph();
        p.addStyle(falseStyle);

        Assert.assertEquals(false, p.<Boolean>getProperty(Property.KEEP_TOGETHER));
    }

    @Test
    public void setRotationAngleTest() {
        float expectedRotationAngle = 20f;

        Style style = new Style();
        style.setRotationAngle(expectedRotationAngle);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(expectedRotationAngle, (float)p.<Float>getProperty(Property.ROTATION_ANGLE), EPS);
    }

    @Test
    public void setWidthTest() {
        float expectedWidth = 100;

        Style style = new Style();
        style.setWidth(expectedWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedWidth), p.<UnitValue>getProperty(Property.WIDTH));
    }

    @Test
    public void setWidthUnitValueTest() {
        float expectedWidth = 100;

        Style style = new Style();
        style.setWidth(UnitValue.createPointValue(expectedWidth));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedWidth), p.<UnitValue>getProperty(Property.WIDTH));
    }

    @Test
    public void setAndGetHeightTest() {
        float expectedHeight = 100;

        Style style = new Style();
        style.setHeight(expectedHeight);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedHeight), p.<UnitValue>getProperty(Property.HEIGHT));
    }

    @Test
    public void setAndGetHeightUnitValueTest() {
        float expectedHeight = 100;

        Style style = new Style();
        style.setHeight(UnitValue.createPointValue(expectedHeight));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedHeight), p.<UnitValue>getProperty(Property.HEIGHT));
    }

    @Test
    public void setMaxHeightTest() {
        float expectedMaxHeight = 80;

        Style style = new Style();
        style.setMaxHeight(UnitValue.createPointValue(expectedMaxHeight));

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMaxHeight), p.<UnitValue>getProperty(Property.MAX_HEIGHT));
    }

    @Test
    public void setMinHeightTest() {
        float expectedMinHeight = 20;

        Style style = new Style();
        style.setMinHeight(expectedMinHeight);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMinHeight), p.<UnitValue>getProperty(Property.MIN_HEIGHT));
    }

    @Test
    public void setMaxWidthTest() {
        float expectedMaxWidth = 200;

        Style style = new Style();
        style.setMaxWidth(expectedMaxWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMaxWidth), p.<UnitValue>getProperty(Property.MAX_WIDTH));
    }

    @Test
    public void setMinWidthTest() {
        float expectedMinWidth = 20;

        Style style = new Style();
        style.setMinWidth(expectedMinWidth);

        Paragraph p = new Paragraph();
        p.addStyle(style);

        Assert.assertEquals(UnitValue.createPointValue(expectedMinWidth), p.<UnitValue>getProperty(Property.MIN_WIDTH));
    }
}
