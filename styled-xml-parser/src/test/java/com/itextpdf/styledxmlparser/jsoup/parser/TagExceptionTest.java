/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 Tag tests.
 @author Jonathan Hedley, jonathan@hedley.net */
@Category(UnitTest.class)
public class TagExceptionTest extends ExtendedITextTest {
    @Test(expected = IllegalArgumentException.class) public void valueOfChecksNotNull() {
        Tag.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class) public void valueOfChecksNotEmpty() {
        Tag.valueOf(" ");
    }
}
