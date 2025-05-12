/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PortUtilTest extends ExtendedITextTest {
    @Test
    public void trimControlCodesTest() {
        for (int i = 0; i < ' ' + 1; ++i) {
            String str = new String(new char[]{(char) i});
            Assertions.assertTrue(PortUtil.trimControlCodes(str).isEmpty());
        }
    }
}
