/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.renderer.typography;

import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class DefaultTypographyApplierTest extends ExtendedITextTest {

    public static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    private static DefaultTypographyApplier sut;
    private static TrueTypeFont freeSansBold;
    private static TrueTypeFont notoSansGujaratiRegular;
    private static FontProgram helvetica;
    private static TrueTypeFont puritanRegular;
    private static TrueTypeFont notoSansRegular;

    @BeforeAll
    public static void setUp() throws IOException {
        sut = new DefaultTypographyApplier();
        freeSansBold = (TrueTypeFont) FontProgramFactory
                .createFont(FONT_FOLDER + "FreeSansBold.ttf");
        notoSansGujaratiRegular = (TrueTypeFont) FontProgramFactory
                .createFont(FONT_FOLDER + "NotoSansGujarati-Regular.ttf");
        notoSansRegular = (TrueTypeFont) FontProgramFactory
                .createFont(FONT_FOLDER + "NotoSans-Regular.ttf");

        puritanRegular = (TrueTypeFont) FontProgramFactory
                .createFont(FONT_FOLDER + "Puritan-Regular.otf");
        helvetica = FontProgramFactory
                .createFont();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_INFO, count = 1, logLevel =
                    LogLevelConstants.INFO)
    })
    public void testApplyOtfShouldIssueInfoForAffectingFeatures() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansRegular.getGlyph(84), notoSansRegular.getGlyph(101),
                        notoSansRegular.getGlyph(115), notoSansRegular.getGlyph(116)));
        SequenceId id = new SequenceId();
        Assertions.assertDoesNotThrow(() ->
                sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, id, null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_INFO, count = 1, logLevel =
                    LogLevelConstants.INFO)
    })
    public void testApplyOtfShouldIssueInfoOncePerDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansRegular.getGlyph(84), notoSansRegular.getGlyph(101),
                        notoSansRegular.getGlyph(115), notoSansRegular.getGlyph(116)));
        SequenceId id = new SequenceId();Assertions.assertDoesNotThrow(() -> {
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, id, null);
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, id, null);
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, id, null);
        });
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_INFO, count = 3, logLevel =
                    LogLevelConstants.INFO)
    })
    public void testApplyOtfShouldIssueInfoOnceForEachDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansRegular.getGlyph(84), notoSansRegular.getGlyph(101),
                        notoSansRegular.getGlyph(115), notoSansRegular.getGlyph(116)));
        Assertions.assertDoesNotThrow(() -> {
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, new SequenceId(), null);
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, new SequenceId(), null);
            sut.applyOtfScript(notoSansRegular, glyphLine, UnicodeScript.LATIN, null, new SequenceId(), null);
        });
    }

    @Test
    public void isPdfCalligraphInstance() {
        Assertions.assertFalse(sut.isPdfCalligraphInstance());
    }

    @Test
    public void applyOtfShouldNotIssueWarningForCyrillicScript() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(freeSansBold.getGlyph(84), freeSansBold.getGlyph(101), freeSansBold.getGlyph(115),
                        freeSansBold.getGlyph(116)));
        SequenceId id = new SequenceId();
        Assertions.assertDoesNotThrow(() ->
                sut.applyOtfScript(freeSansBold, glyphLine, UnicodeScript.CYRILLIC, null, id, null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 1, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyOtfShouldIssueWarningForGujaratiAndSupportingfontScript() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansGujaratiRegular.getGlyph(84), notoSansGujaratiRegular.getGlyph(101),
                        notoSansGujaratiRegular.getGlyph(115), notoSansGujaratiRegular.getGlyph(116)));
        SequenceId id = new SequenceId();
        Assertions.assertDoesNotThrow(() ->
                sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 1, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyOtfShouldIssueWarningOncePerDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansGujaratiRegular.getGlyph(84), notoSansGujaratiRegular.getGlyph(101),
                        notoSansGujaratiRegular.getGlyph(115), notoSansGujaratiRegular.getGlyph(116)));
        SequenceId id = new SequenceId();
        Assertions.assertDoesNotThrow(() -> {
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
        });
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 3, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyOtfShouldIssueWarningForEachDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansGujaratiRegular.getGlyph(84), notoSansGujaratiRegular.getGlyph(101),
                        notoSansGujaratiRegular.getGlyph(115), notoSansGujaratiRegular.getGlyph(116)));
        Assertions.assertDoesNotThrow(() -> {
            SequenceId id = new SequenceId();
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
            id = new SequenceId();
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
            id = new SequenceId();
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
        });
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 3, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyOtfShouldIssueWarningForEachScript() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(notoSansGujaratiRegular.getGlyph(84), notoSansGujaratiRegular.getGlyph(101),
                        notoSansGujaratiRegular.getGlyph(115), notoSansGujaratiRegular.getGlyph(116)));
        Assertions.assertDoesNotThrow(() -> {
            SequenceId id = new SequenceId();
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.GUJARATI, null, id, null);
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.THAI, null, id, null);
            sut.applyOtfScript(notoSansGujaratiRegular, glyphLine, UnicodeScript.HEBREW, null, id, null);
        });
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 1)
    })
    public void applyKerningShouldIssueWarningIfFontSupport() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(helvetica.getGlyph(84), helvetica.getGlyph(101), helvetica.getGlyph(115),
                        helvetica.getGlyph(116)));
        Assertions.assertDoesNotThrow(() ->
                sut.applyKerning(helvetica, glyphLine, new SequenceId(), null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 3, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyKerningShouldIssueWarningPerDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(helvetica.getGlyph(84), helvetica.getGlyph(101), helvetica.getGlyph(115),
                        helvetica.getGlyph(116)));
        Assertions.assertDoesNotThrow(() -> {
            sut.applyKerning(helvetica, glyphLine, new SequenceId(), null);
            sut.applyKerning(helvetica, glyphLine, new SequenceId(), null);
            sut.applyKerning(helvetica, glyphLine, new SequenceId(), null);
        });
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, count = 1, logLevel =
                    LogLevelConstants.WARN)
    })
    public void applyKerningShouldLogOncePerDocument() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(helvetica.getGlyph(84), helvetica.getGlyph(101), helvetica.getGlyph(115),
                        helvetica.getGlyph(116)));
        Assertions.assertDoesNotThrow(() -> {
            SequenceId id = new SequenceId();
            sut.applyKerning(helvetica, glyphLine, id, null);
            sut.applyKerning(helvetica, glyphLine, id, null);
            sut.applyKerning(helvetica, glyphLine, id, null);
        });
    }

    @Test
    public void applyKerningShouldNotIssueWarningIfNoFontSupport() {
        GlyphLine glyphLine = new GlyphLine(
                Arrays.asList(puritanRegular.getGlyph(84), puritanRegular.getGlyph(101),
                        puritanRegular.getGlyph(115), puritanRegular.getGlyph(116)));
        Assertions.assertDoesNotThrow(() ->
                sut.applyKerning(puritanRegular, glyphLine, new SequenceId(), null));
    }
}