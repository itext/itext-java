/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

    @Test
    public void scaleRcStringWithNullOrEmptyReturnsAsIs() {
        Assertions.assertNull(PageResizer.scaleRcString(null, 2.0));
        Assertions.assertEquals("", PageResizer.scaleRcString("", 2.0));
        Assertions.assertEquals("   ", PageResizer.scaleRcString("   ", 2.0));
    }

    @Test
    public void scaleRcStringWithIdentityScaleReturnsAsIs() {
        String input = "font-size: 12.5pt; width: 100px;";
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 1.0));
    }

    @Test
    public void scaleRcStringWithNonScalablePropertiesReturnsAsIs() {
        String input = "color: red; font-weight: bold;";
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 2.0));
        String input2 = "margin: 10pt 5pt; padding: 10px;";
        Assertions.assertEquals(input2, PageResizer.scaleRcString(input2, 2.0), "Shorthand properties should be ignored.");
    }

    @Test
    public void scaleRcStringScalesValuesWithAbsoluteUnits() {
        String input = "font-size: 12pt;";
        String expected = "font-size: 6pt;";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 0.5));

        String multiInput = "width: 1in; height: 2pc; margin-left: 3cm; margin-top: 4mm; border-top-width: 5px;";
        String multiExpected = "width: 0.5in; height: 1pc; margin-left: 1.5cm; margin-top: 2mm; border-top-width: 2.5px;";
        Assertions.assertEquals(multiExpected, PageResizer.scaleRcString(multiInput, 0.5));
    }

    @Test
    public void scaleRcStringWithRelativeOrNoUnitsReturnsAsIs() {
        String input = "font-size: 1.5em; width: 100%; line-height: 150%;";
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 2.0));
        String noUnitInput = "font-size: 12";
        Assertions.assertEquals(noUnitInput, PageResizer.scaleRcString(noUnitInput, 2.0));
    }

    @Test
    public void scaleRcStringHandlesVariousNumberFormats() {
        Assertions.assertEquals("font-size: 20pt;", PageResizer.scaleRcString("font-size: 10pt;", 2.0), "Integer");
        Assertions.assertEquals("font-size: 21pt;", PageResizer.scaleRcString("font-size: 10.5pt;", 2.0), "Decimal");
        Assertions.assertEquals("font-size: 1pt;", PageResizer.scaleRcString("font-size: .5pt;", 2.0), "Leading dot decimal");
        Assertions.assertEquals("width: 50px;", PageResizer.scaleRcString("width: 1e2px;", 0.5), "Scientific notation");
        Assertions.assertEquals("margin-left: -20px;", PageResizer.scaleRcString("margin-left: -10px;", 2.0), "Negative value");
    }

    @Test
    public void scaleRcStringHandlesVariedWhitespace() {
        String input = "font-size:  12pt ; width:50px";
        String expected = "font-size:  24pt ; width:100px";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 2.0));
    }

    @Test
    public void scaleRcStringWithZeroScale_scalesToZero() {
        String input = "font-size: 12pt; width: 100px;";
        String expected = "font-size: 0pt; width: 0px;";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 0.0));
    }

    @Test
    public void scaleRcStringHandlesSmallResultingValues() {
        Assertions.assertEquals("font-size: 0.0001pt;", PageResizer.scaleRcString("font-size: 0.001pt;", 0.1));
        Assertions.assertEquals("font-size: 0pt;", PageResizer.scaleRcString("font-size: 0.001pt;", 0.01));
    }

    @Test
    public void scaleRcStringWithComplexRichTextScalesCorrectly() {
        String rc = "body{font-family:helvetica,sans-serif;font-size:12.0pt;line-height:14.0pt;margin-top:2.0mm;"
                + "margin-bottom:2.0mm;text-align:left;}p{margin-top:0.0pt;margin-bottom:0.0pt;}";
        String expected = "body{font-family:helvetica,sans-serif;font-size:18pt;line-height:21pt;margin-top:3mm;"
                + "margin-bottom:3mm;text-align:left;}p{margin-top:0pt;margin-bottom:0pt;}";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(rc, 1.5));
    }

    @Test
    public void scaleRcStringDoesNotScaleRelativeUnitsOnScalableProperties() {
        // em, rem, % should not be scaled
        String input = "font-size: 1.5em; line-height: 150%; letter-spacing: 0.5rem;";
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 2.0));
    }

    @Test
    public void scaleRcStringPreservesWhitespaceBetweenNumberAndUnit() {
        String input = "font-size: 12 pt; width:  10  px; height: 5   cm;";
        String expected = "font-size: 24 pt; width:  20  px; height: 10   cm;";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 2.0));
    }

    @Test
    public void scaleRcStringPropertyNameCaseSensitivity_notScaled() {
        // CSS in XHTML is case-sensitive here; property name with different case should not match
        String input = "Font-Size: 12pt; WIDTH: 100px;";
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 2.0));
    }

    @Test
    public void scaleRcStringIgnoresShorthandBorderWidth() {
        String input = "border: 5px;";
        // Shorthand property is not in the scalable list, should remain unchanged
        Assertions.assertEquals(input, PageResizer.scaleRcString(input, 3.0));
    }

    @Test
    public void scaleRcStringScientificNotationNegativeExponent() {
        Assertions.assertEquals("width: 0.1in;", PageResizer.scaleRcString("width: 1e-2in;", 10.0));
    }

    @Test
    public void scaleRcStringMixedAbsoluteAndRelativeValues() {
        String input = "font-size: 10pt; line-height: 120%; width: 2cm;";
        String expected = "font-size: 20pt; line-height: 120%; width: 4cm;";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 2.0));
    }

    @Test
    public void scaleRcAcrobatExample() {
        String input = "<?xml version=\"1.0\"?><body xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\" xfa:spec=\"2.0.2\"><p style=\"font-size:12pt;padding-left:5pt;padding-top:2pt;margin-bottom:6pt;line-height:15pt;text-align:left;color:#000000;\">This is the first paragraph. It has a specific<span style=\"font-weight:bold; color:#FF0000;\">font size and line height.</span></p><p style=\"font-size:12pt;margin-top:0pt;text-indent:24pt;color:#333333;\">This is the second paragraph. Notice the <b>text-indent</b> property, which creates the indentation.</p></body>";
        String expected = "<?xml version=\"1.0\"?><body xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\" xfa:spec=\"2.0.2\"><p style=\"font-size:6pt;padding-left:2.5pt;padding-top:1pt;margin-bottom:3pt;line-height:7.5pt;text-align:left;color:#000000;\">This is the first paragraph. It has a specific<span style=\"font-weight:bold; color:#FF0000;\">font size and line height.</span></p><p style=\"font-size:6pt;margin-top:0pt;text-indent:12pt;color:#333333;\">This is the second paragraph. Notice the <b>text-indent</b> property, which creates the indentation.</p></body>";
        Assertions.assertEquals(expected, PageResizer.scaleRcString(input, 0.5));
    }

    @Test
    public void scaleRcStringPreservesWhitespaceBetweenPropertyAndValue() {
        // Test various whitespace patterns are preserved
        String input1 = "font-size:  12pt;";
        String expected1 = "font-size:  24pt;";
        Assertions.assertEquals(expected1, PageResizer.scaleRcString(input1, 2.0),
                "Multiple spaces after colon should be preserved");

        String input2 = "font-size:\t10pt;";
        String expected2 = "font-size:\t20pt;";
        Assertions.assertEquals(expected2, PageResizer.scaleRcString(input2, 2.0),
                "Tab after colon should be preserved");

        String input3 = "font-size: 10pt; width:  20px;";
        String expected3 = "font-size: 20pt; width:  40px;";
        Assertions.assertEquals(expected3, PageResizer.scaleRcString(input3, 2.0),
                "Different whitespace patterns should be preserved independently");

        String input4 = "font-size:10pt;";
        String expected4 = "font-size:20pt;";
        Assertions.assertEquals(expected4, PageResizer.scaleRcString(input4, 2.0),
                "No space after colon should be preserved");

        String input5 = "margin-left: 5 pt;";
        String expected5 = "margin-left: 10 pt;";
        Assertions.assertEquals(expected5, PageResizer.scaleRcString(input5, 2.0),
                "Space between number and unit should be preserved");
    }

    @Test
    public void scaleRcStringWordBoundaryCheck() {
        String input1 = "font-size-large: 10pt;";
        Assertions.assertEquals(input1, PageResizer.scaleRcString(input1, 2.0),
                "Property 'font-size-large' should not be treated as 'font-size'");

        String input2 = "width2: 20px;";
        Assertions.assertEquals(input2, PageResizer.scaleRcString(input2, 2.0),
                "Property 'width2' should not be treated as 'width'");

        String input3 = "my-height: 30pt;";
        Assertions.assertEquals(input3, PageResizer.scaleRcString(input3, 2.0),
                "Property 'my-height' should not be treated as 'height'");

        String input4 = "margin-top-extra: 15px;";
        Assertions.assertEquals(input4, PageResizer.scaleRcString(input4, 2.0),
                "Property 'margin-top-extra' should not be treated as 'margin-top'");

        String input5 = "font-size: 12pt; custom-font-size: 10pt;";
        String expected5 = "font-size: 24pt; custom-font-size: 10pt;";
        Assertions.assertEquals(expected5, PageResizer.scaleRcString(input5, 2.0),
                "Valid 'font-size' should be scaled, but 'custom-font-size' should not");

        String input6 = "width: 100px";
        String expected6 = "width: 200px";
        Assertions.assertEquals(expected6, PageResizer.scaleRcString(input6, 2.0),
                "Property 'width' at end of string should be scaled");

        String input7 = "font-size: 10pt; font-size-custom: 20pt; height: 50px; height123: 30px;";
        String expected7 = "font-size: 20pt; font-size-custom: 20pt; height: 100px; height123: 30px;";
        Assertions.assertEquals(expected7, PageResizer.scaleRcString(input7, 2.0),
                "Only exact property name matches should be scaled");
    }

    @Test
    public void scaleRcStringWithMalformedNumberReturnsAsIs() {
        // A single dot is not a valid number.
        String input1 = "font-size: .pt;";
        Assertions.assertEquals(input1, PageResizer.scaleRcString(input1, 2.0));

        // A single sign is not a valid number.
        String input2 = "font-size: +pt;";
        Assertions.assertEquals(input2, PageResizer.scaleRcString(input2, 2.0));

        // A dot after a number and sign is not valid.
        String input3 = "font-size: -.pt;";
        Assertions.assertEquals(input3, PageResizer.scaleRcString(input3, 2.0));

        // Exponent without mantissa
        String input4 = "font-size: E1pt;";
        Assertions.assertEquals(input4, PageResizer.scaleRcString(input4, 2.0));

        // Number with exponent but no digits in exponent
        String input5 = "font-size: 1Ept;";
        Assertions.assertEquals(input5, PageResizer.scaleRcString(input5, 2.0));
    }

    @Test
    public void scaleRcStringIgnoresComments() {
        String htmlComment = "font-size: 20pt; <!-- font-size: 10pt; -->";
        String expectedHtml = "font-size: 40pt; <!-- font-size: 20pt; -->";
        Assertions.assertEquals(expectedHtml, PageResizer.scaleRcString(htmlComment, 2.0));

        String blockComment = "font-size: 20pt; /** font-size: 10pt; **/";
        String expectedBlock = "font-size: 40pt; /** font-size: 20pt; **/";
        Assertions.assertEquals(expectedBlock, PageResizer.scaleRcString(blockComment, 2.0));

        String combined = "<!-- width: 50px; --> width: 100px; /** height: 40pt; */ height: 80pt;";
        String expectedCombined = "<!-- width: 100px; --> width: 200px; /** height: 80pt; */ height: 160pt;";
        Assertions.assertEquals(expectedCombined, PageResizer.scaleRcString(combined, 2.0));
    }

}
