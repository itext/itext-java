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


import com.itextpdf.layout.font.FontFamilySplitter;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(UnitTest.class)
public class FontFamilySplitterTest {
    @Test
    public void fontFamilySplitter() throws Exception {
        String fontFamilies =
                        "'Puritan'\n" +
                        "Puritan\n" +

                        "'Pur itan'\n" +
                        "Pur itan\n" +

                        "'Pur it an'\n" +
                        "Pur it an\n" +

                        "   \"Puritan\"\n" +
                        "Puritan\n" +

                        "  \"Pur itan\"\n" +
                        "Pur itan\n" +

                        "\"Pur it an\"\n" +
                        "Pur it an\n" +

                        "FreeSans\n" +
                        "FreeSans\n" +

                        "'Puritan', FreeSans\n" +
                        "Puritan; FreeSans\n" +

                        "'Pur itan' , FreeSans\n" +
                        "Pur itan; FreeSans\n" +

                        "   'Pur it an'  ,  FreeSans   \n" +
                        "Pur it an; FreeSans\n" +

                        "\"Puritan\", FreeSans\n" +
                        "Puritan; FreeSans\n" +

                        "\"Pur itan\", FreeSans\n" +
                        "Pur itan; FreeSans\n" +

                        "\"Pur it an\", FreeSans\n" +
                        "Pur it an; FreeSans\n" +

                        "\"Puritan\"\n" +
                        "Puritan\n" +

                        "'Free Sans',\n"+
                        "Free Sans\n"+

                        "'Free-Sans',\n"+
                        "Free-Sans\n"+

                        "  'Free-Sans' , Puritan\n"+
                        "Free-Sans; Puritan\n"+

                        "  \"Free-Sans\" , Puritan\n"+
                        "Free-Sans; Puritan\n"+

                        "  Free-Sans , Puritan\n"+
                        "Free-Sans; Puritan\n"+

                        "  Free-Sans\n"+
                        "Free-Sans\n"+

                        "\"Puritan\", Free Sans\n" +
                        "Puritan\n" +

                        "\"Puritan 2.0\"\n" +
                        "-\n" +

                        "'Puritan' FreeSans\n" +
                        "-\n" +

                        "Pur itan\n" +
                        "-\n" +

                        "Pur it an\"\n" +
                        "-\n" +

                        "\"Free Sans\n" +
                        "-\n" +

                        "Pur it an'\n" +
                        "-\n" +

                        "'Free Sans\n"+
                        "-";


        String[] splitFontFamilies = fontFamilies.split("\n");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < splitFontFamilies.length; i+=2) {
            List<String> fontFamily = FontFamilySplitter.splitFontFamily(splitFontFamilies[i]);
            result.setLength(0);
            for (String ff: fontFamily) {
                result.append(ff).append("; ");
            }
            Assert.assertEquals(splitFontFamilies[i+1],
                    result.length() > 2 ? result.substring(0, result.length() - 2) : "-");
        }
    }
}
