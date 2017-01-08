package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TransparentColor;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class StylesTest extends ExtendedITextTest {

    @Test
    public void stylesTest01() {
        Style myStyle = new Style();
        myStyle.setFontColor(Color.RED);

        Paragraph p = new Paragraph("text")
                .addStyle(myStyle)
                .setFontColor(Color.GREEN);

        Assert.assertEquals(Color.GREEN, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void stylesTest02() {
        Style myStyle = new Style();
        myStyle.setFontColor(Color.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assert.assertEquals(Color.RED, p.getRenderer().<TransparentColor>getProperty(Property.FONT_COLOR).getColor());
    }

    @Test
    public void stylesTest03() {
        Style myStyle = new Style();
        myStyle.setFontColor(Color.RED);

        Paragraph p = new Paragraph("text").addStyle(myStyle);

        Assert.assertEquals(Color.RED, p.getRenderer().<Color>getProperty(Property.FONT_COLOR));

        Style myStyle2 = new Style();
        myStyle2.setFontColor(Color.GREEN);

        p.addStyle(myStyle2);

        Assert.assertEquals(Color.GREEN, p.getRenderer().<Color>getProperty(Property.FONT_COLOR));
    }

}
