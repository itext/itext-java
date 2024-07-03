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
package com.itextpdf.kernel.font;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfType3FontTest extends ExtendedITextTest {
    private static final float EPS = 1e-4f;

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void addDifferentGlyphsInConstructorTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(new PdfName("space"), new PdfStream());
        charProcs.put(new PdfName("A"), new PdfStream());
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        dictionary.put(PdfName.ToUnicode, PdfName.IdentityH);
        dictionary.put(PdfName.Encoding, new PdfName("zapfdingbatsencoding"));
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };
        Assertions.assertNotNull(type3Font.getFontProgram());
        int spaceGlyphCode = 32;
        Glyph glyph = type3Font.getFontProgram().getGlyph(spaceGlyphCode);
        Assertions.assertEquals(new Glyph(spaceGlyphCode, 0, new char[]{' '}), glyph);

        int AGlyphCode = 65;
        glyph = type3Font.getFontProgram().getGlyph(AGlyphCode);
        Assertions.assertEquals(new Glyph(AGlyphCode, 0, new char[] {'A'}), glyph);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void addAlreadyExistingGlyphTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        charProcs.put(new PdfName("A"), new PdfStream());
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };

        Type3Glyph type3Glyph = type3Font.addGlyph('A', 1, 2, 3, 5, 8);
        Assertions.assertEquals(0, type3Glyph.getWx(), EPS);
        Assertions.assertEquals(0, type3Glyph.getLlx(), EPS);
        Assertions.assertEquals(0, type3Glyph.getLly(), EPS);
        Assertions.assertEquals(0, type3Glyph.getUrx(), EPS);
        Assertions.assertEquals(0, type3Glyph.getUry(), EPS);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void setFontStretchTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        String fontStretch = "test";
        type3Font.setFontStretch(fontStretch);
        Assertions.assertNotNull(type3Font.fontProgram);
        Assertions.assertNotNull(type3Font.fontProgram.getFontNames());
        Assertions.assertEquals(fontStretch, type3Font.fontProgram.getFontNames().getFontStretch());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void setPdfFontFlagsTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        int randomTestFontFlagsValue = 5;
        type3Font.setPdfFontFlags(randomTestFontFlagsValue);
        Assertions.assertNotNull(type3Font.fontProgram);
        Assertions.assertEquals(randomTestFontFlagsValue, type3Font.fontProgram.getPdfFontFlags());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void glyphWithUnicodeBiggerThan32CannotBeEncodedTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary);

        int cannotEncodeAndAUnicodeBiggerThan32TestValue = 333;
        Assertions.assertNull(type3Font.getGlyph(cannotEncodeAndAUnicodeBiggerThan32TestValue));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void containsGlyphTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };

        Assertions.assertFalse(type3Font.containsGlyph(333));
        Assertions.assertFalse(type3Font.containsGlyph(-5));
        Assertions.assertFalse(type3Font.containsGlyph(32));
        type3Font.addGlyph(' ', 0, 0, 0, 1, 1);
        Assertions.assertTrue(type3Font.containsGlyph(32));
        type3Font.addGlyph('A', 0, 0, 0, 0, 0);
        Assertions.assertTrue(type3Font.containsGlyph(65));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void flushExceptionTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfType3Font type3Font = new DisableEnsureUnderlyingObjectHasIndirectReference(dictionary);

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> type3Font.flush()
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.NO_GLYPHS_DEFINED_FOR_TYPE_3_FONT, e.getMessage());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void fillFontDescriptorTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfDictionary fontDescriptor = new PdfDictionary();
        String fontStretch = "test";
        fontDescriptor.put(PdfName.FontStretch, new PdfName(fontStretch));
        dictionary.put(PdfName.FontDescriptor, fontDescriptor);
        PdfType3Font type3Font = new PdfType3Font(dictionary) {
            @Override
            protected PdfDocument getDocument() {
                return null;
            }
        };
        Assertions.assertNotNull(type3Font.fontProgram);
        Assertions.assertNotNull(type3Font.fontProgram.getFontNames());
        Assertions.assertEquals(fontStretch, type3Font.fontProgram.getFontNames().getFontStretch());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void noCharProcsTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        dictionary.put(PdfName.Widths, new PdfArray());

        AssertUtil.doesNotThrow(() -> new PdfType3Font(dictionary));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void noEncodingTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());

        AssertUtil.doesNotThrow(() -> new PdfType3Font(dictionary));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE)})
    public void noDifferenceTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        PdfDictionary charProcs = new PdfDictionary();
        dictionary.put(PdfName.CharProcs, charProcs);
        dictionary.put(PdfName.Widths, new PdfArray());
        PdfDictionary encoding = new PdfDictionary();
        dictionary.put(PdfName.Encoding, encoding);

        AssertUtil.doesNotThrow(() -> new PdfType3Font(dictionary));
    }

    @Test
    public void missingFontMatrixTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Widths, new PdfArray());
        dictionary.put(PdfName.ToUnicode, PdfName.IdentityH);
        dictionary.put(PdfName.Encoding, new PdfName("zapfdingbatsencoding"));

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> new PdfType3Font(dictionary)
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.MISSING_REQUIRED_FIELD_IN_FONT_DICTIONARY, PdfName.FontMatrix), e.getMessage());
    }

    @Test
    public void missingWidthsTest() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.FontMatrix, new PdfArray());
        dictionary.put(PdfName.ToUnicode, PdfName.IdentityH);
        dictionary.put(PdfName.Encoding, new PdfName("zapfdingbatsencoding"));

        Exception e = Assertions.assertThrows(PdfException.class,
                () -> new PdfType3Font(dictionary)
        );
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.MISSING_REQUIRED_FIELD_IN_FONT_DICTIONARY, PdfName.Widths), e.getMessage());
    }

    @Test
    public void noCharProcGlyphForDifferenceTest() {
        PdfDictionary font = new PdfDictionary();
        font.put(PdfName.FontMatrix, new PdfArray());
        font.put(PdfName.Widths, new PdfArray());
        font.put(PdfName.CharProcs, new PdfDictionary());

        PdfDictionary encoding = new PdfDictionary();
        PdfArray differences = new PdfArray();
        differences.add(0, new PdfNumber(65));
        differences.add(1, new PdfName("A"));
        encoding.put(PdfName.Differences, differences);
        font.put(PdfName.Encoding, encoding);

        AssertUtil.doesNotThrow(() -> new PdfType3Font(font));
    }

    private class DisableEnsureUnderlyingObjectHasIndirectReference extends PdfType3Font {

        DisableEnsureUnderlyingObjectHasIndirectReference(PdfDictionary fontDictionary) {
            super(fontDictionary);
        }

        @Override
        protected void ensureUnderlyingObjectHasIndirectReference() {
        }
    }
}
