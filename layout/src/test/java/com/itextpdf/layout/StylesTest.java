package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
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

        Paragraph p = new Paragraph("text");
        p.addStyle(myStyle);
        p.setFontColor(Color.GREEN);

        Assert.assertEquals(Color.GREEN, p.getRenderer().getProperty(Property.FONT_COLOR));
    }

    @Test
    public void stylesTest02() {
        Style myStyle = new Style();
        myStyle.setFontColor(Color.RED);

        Paragraph p = new Paragraph("text");
        p.addStyle(myStyle);

        Assert.assertEquals(Color.RED, p.getRenderer().getProperty(Property.FONT_COLOR));
    }

}
