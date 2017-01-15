package com.itextpdf.layout;


import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.reflect.Method;
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
                        "\"Puritan\", Free Sans\n" +
                        "Puritan\n" +
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
                        "-\n";


        String[] splitFontFamilies = fontFamilies.split("\n");

        Method splitFontFamily = Class.forName("com.itextpdf.layout.font.FontFamilySplitter").getDeclaredMethod("splitFontFamily", String.class);
        splitFontFamily.setAccessible(true);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < splitFontFamilies.length; i+=2) {
            List<String> fontFamily = (List<String>) splitFontFamily.invoke(null, splitFontFamilies[i]);
            result.setLength(0);
            for (String ff: fontFamily) {
                result.append(ff).append("; ");
            }
            Assert.assertEquals(splitFontFamilies[i+1],
                    result.length() > 2 ? result.substring(0, result.length() - 2) : "-");
        }
    }
}
