/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/

package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PortUtilTest extends ExtendedITextTest {
    @Test
    public void trimControlCodesTest() {
        for (int i = 0; i < ' ' + 1; ++i) {
            String str = new String(new char[]{(char) i});
            Assert.assertTrue(PortUtil.trimControlCodes(str).isEmpty());
        }
    }
}
