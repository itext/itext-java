/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

}
