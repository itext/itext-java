package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfInkAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PageResizerUnitTest extends ExtendedITextTest {
    @Test
    public void resizeAppearanceStreamsNullAPTest() {
        PdfAnnotation annotation = new PdfInkAnnotation(new Rectangle(50.0f, 50.0f));
        PageResizer.resizeAppearanceStreams(annotation, null);
        Assertions.assertNull(annotation.getAppearanceDictionary());
    }

    @Test
    public void scalePageBoxNullPageSizeTest() {
        Rectangle originalPageSize = null;
        PageSize newPageSize = new PageSize(25.0f, 25.0f);
        Rectangle box = new Rectangle(10.0f, 10.0f);

        Rectangle scaled = PageResizer.scalePageBox(originalPageSize, newPageSize, box);
        Assertions.assertEquals(box, scaled);
    }

    @Test
    public void scalePageBoxNullNewPageSizeTest() {
        Rectangle originalPageSize = new Rectangle(50.0f, 50.0f);
        PageSize newPageSize = null;
        Rectangle box = new Rectangle(10.0f, 10.0f);

        Rectangle scaled = PageResizer.scalePageBox(originalPageSize, newPageSize, box);
        Assertions.assertEquals(box, scaled);
    }

    @Test
    public void scalePageBoxNullBoxTest() {
        Rectangle originalPageSize = new Rectangle(50.0f, 50.0f);
        PageSize newPageSize = new PageSize(25.0f, 25.0f);
        Rectangle box = null;

        Rectangle scaled = PageResizer.scalePageBox(originalPageSize, newPageSize, box);
        Assertions.assertEquals(box, scaled);
    }

    @Test
    public void scalePageBoxZeroHeightTest() {
        Rectangle originalPageSize = new Rectangle(50.0f, 0.0f);
        PageSize newPageSize = new PageSize(25.0f, 25.0f);
        Rectangle box = new Rectangle(10.0f, 10.0f);

        Rectangle scaled = PageResizer.scalePageBox(originalPageSize, newPageSize, box);
        Assertions.assertEquals(box, scaled);
    }

    @Test
    public void scalePageBoxZeroWidthTest() {
        Rectangle originalPageSize = new Rectangle(0.0f, 50.0f);
        PageSize newPageSize = new PageSize(25.0f, 25.0f);
        Rectangle box = new Rectangle(10.0f, 10.0f);

        Rectangle scaled = PageResizer.scalePageBox(originalPageSize, newPageSize, box);
        Assertions.assertEquals(box, scaled);
    }

    @Test
    public void scaleDaStringSimpleScaleTest() {
        String input = "/Helv 12 Tf";
        double scale = 0.5;
        String expected = "/Helv 6 Tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringMixedOperatorsAndColorTest() {
        String input = "1 0 0 rg /F1 10 Tf 14 TL";
        double scale = 2;
        String expected = "1 0 0 rg /F1 20 Tf 28 TL";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringEdgeNumericFormsTest() {
        Assertions.assertEquals("-1 Ts", PageResizer.scaleDaString("-.5 Ts", 2.0));
        Assertions.assertEquals("1 Ts", PageResizer.scaleDaString(".5 Ts", 2.0));
        Assertions.assertEquals("0.5 Tc", PageResizer.scaleDaString("5.0000 Tc", 0.1));
        Assertions.assertEquals("1 TL", PageResizer.scaleDaString("1e-1 TL", 10.0));
    }

    @Test
    public void scaleDaStringMultipleOperatorGroupsTest() {
        String input = "/F1 10 Tf 5 Tc 2.5 Tw 10 TL /F2 20 Tf -2 Ts";
        double scale = 0.5;
        String expected = "/F1 5 Tf 2.5 Tc 1.25 Tw 5 TL /F2 10 Tf -1 Ts";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringNoOpsTest() {
        double scale = 2.0;
        // Operator with no operands should not change.
        Assertions.assertEquals("Tf", PageResizer.scaleDaString("Tf", scale));
        //Operator with non-numeric operand should not change.
        Assertions.assertEquals("/F1 Tf", PageResizer.scaleDaString("/F1 Tf", scale));
        //String with no operators should not change.
        Assertions.assertEquals("foo bar baz", PageResizer.scaleDaString("foo bar baz", scale));
        //Malformed operator sequence should not change unpredictably.
        Assertions.assertEquals("/Helv Tf 12", PageResizer.scaleDaString("/Helv Tf 12", scale));
        //Whitespace-only string should result in empty.
        Assertions.assertEquals("", PageResizer.scaleDaString("", scale));
        //Numbers without operators should not be scaled.
        Assertions.assertEquals("1 2 3", PageResizer.scaleDaString("1 2 3", scale));
    }

    @Test
    public void scaleDaStringWhitespaceNormalizationTest() {
        String input = "  /Helv   12 \t Tf  ";
        double scale = 0.5;
        String expected = "/Helv 6 Tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale), "Whitespace should be normalized.");
    }

    @Test
    public void scaleDaStringWithIdentityScaleTest() {
        String input = "/Helv 12.5 Tf";
        double scale = 1.0;
        String expected = "/Helv 12.5 Tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringIgnoreOtherOperatorsTest() {
        String input = "100 Tz 12 Tf";
        double scale = 2.0;
        String expected = "100 Tz 24 Tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringNullInputTest() {
        Assertions.assertNull(PageResizer.scaleDaString(null, 2.0));
    }

    @Test
    public void scaleDaStringOperatorCaseSensitivityTest() {
        String input = "/Helv 12 tf";
        double scale = 2.0;
        String expected = "/Helv 12 tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void scaleDaStringSmallResultingValueTest() {
        String input = "0.0001 Tf";
        double scale = 0.1;
        String expected = "0 Tf";
        Assertions.assertEquals(expected, PageResizer.scaleDaString(input, scale));
    }

    @Test
    public void resizePageWithZeroSizeTest() {
        PageResizer resizer = new PageResizer(new PageSize(0.0F, 0.0F), PageResizer.ResizeType.DEFAULT);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> resizer.resize(null));
        String expectedMessage = MessageFormatUtil.format(KernelExceptionMessageConstant
                .CANNOT_RESIZE_PAGE_WITH_NEGATIVE_OR_INFINITE_SCALE, new PageSize(0.0F, 0.0F));
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
