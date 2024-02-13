package com.itextpdf.kernel.utils.checkers;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnitTest.class)
public class FontCheckUtilTest extends ExtendedITextTest {

    @Test
    public void checkFontAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Assert.assertTrue(FontCheckUtil.doesFontContainAllUsedGlyphs("123", font));
    }


    @Test
    public void checkFontNotAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Assert.assertFalse(FontCheckUtil.doesFontContainAllUsedGlyphs("⫊", font));
    }
}