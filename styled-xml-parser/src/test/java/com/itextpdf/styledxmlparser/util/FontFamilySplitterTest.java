/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

@Tag("UnitTest")
public class FontFamilySplitterTest extends ExtendedITextTest {
    @Test
    //TODO DEVSIX-1130: Adapt after fix
    public void fontFamilySplitter() {
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
            List<String> fontFamily = FontFamilySplitterUtil.splitFontFamily(splitFontFamilies[i]);
            result.setLength(0);
            for (String ff: fontFamily) {
                result.append(ff).append("; ");
            }
            Assertions.assertEquals(splitFontFamilies[i+1],
                    result.length() > 2 ? result.substring(0, result.length() - 2) : "-");
        }
    }
}
