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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.parser.HtmlTreeBuilderState.Constants;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("UnitTest")
public class HtmlTreeBuilderStateTest extends ExtendedITextTest {
    static List<Object[]> findConstantArrays(Class aClass) throws IllegalAccessException {
        ArrayList<Object[]> array = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();

        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && field.getType().isArray()) {
                array.add((Object[]) field.get(null));
            }
        }

        return array;
    }

    static void ensureSorted(List<Object[]> constants) {
        for (Object[] array : constants) {
            Object[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            Assertions.assertArrayEquals(array, copy);
        }
    }

    @Test
    public void ensureArraysAreSorted() throws IllegalAccessException {
        List<Object[]> constants = findConstantArrays(Constants.class);
        ensureSorted(constants);
        Assertions.assertEquals(38, constants.size());
    }


    @Test
    public void nestedAnchorElements01() {
        String html = "<html>\n" +
                "  <body>\n" +
                "    <a href='#1'>\n" +
                "        <div>\n" +
                "          <a href='#2'>child</a>\n" +
                "        </div>\n" +
                "    </a>\n" +
                "  </body>\n" +
                "</html>";
        String s = Jsoup.parse(html).toString();
        Assertions.assertEquals("<html> \n" +
                " <head></head>\n" +
                " <body> <a href=\"#1\"> </a>\n" +
                "  <div>\n" +
                "   <a href=\"#1\"> </a><a href=\"#2\">child</a> \n" +
                "  </div>   \n" +
                " </body>\n" +
                "</html>", s);
    }

    @Test
    public void nestedAnchorElements02() {
        String html = "<html>\n" +
                "  <body>\n" +
                "    <a href='#1'>\n" +
                "      <div>\n" +
                "        <div>\n" +
                "          <a href='#2'>child</a>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </a>\n" +
                "  </body>\n" +
                "</html>";
        String s = Jsoup.parse(html).toString();
        Assertions.assertEquals("<html> \n" +
                " <head></head>\n" +
                " <body> <a href=\"#1\"> </a>\n" +
                "  <div>\n" +
                "   <a href=\"#1\"> </a>\n" +
                "   <div>\n" +
                "    <a href=\"#1\"> </a><a href=\"#2\">child</a> \n" +
                "   </div> \n" +
                "  </div>   \n" +
                " </body>\n" +
                "</html>", s);
    }

}
