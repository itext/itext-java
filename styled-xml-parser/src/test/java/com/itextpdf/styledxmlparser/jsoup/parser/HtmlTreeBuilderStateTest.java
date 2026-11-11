/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
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
