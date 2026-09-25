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
package com.itextpdf.layout.element;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.FontKerning;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAnchor;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer.RendererGlyph;
import com.itextpdf.layout.renderer.TypographyUtils;
import com.itextpdf.layout.renderer.typography.AbstractTypographyApplier;
import com.itextpdf.layout.renderer.typography.DefaultTypographyApplier;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

@Tag("UnitTest")
public class VerticalParagraphTest extends ExtendedITextTest {

    private static final String CJK_FONT =
            "./src/test/resources/com/itextpdf/layout/fonts/BioRhymeExpanded-Regular.ttf";
    @AfterEach
    public void cleanup() {
        TypographyUtils.setTypographyApplierInstance(new DefaultTypographyApplier());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY,
            logLevel = LogLevelConstants.WARN)})
    public void settingUnsupportedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        Leading leading = new Leading(Leading.MULTIPLIED, 3f);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.FLOAT,
                com.itextpdf.layout.properties.FloatPropertyValue.LEFT);
        verticalParagraph.setProperty(com.itextpdf.layout.properties.Property.LEADING,
                leading);

        verticalParagraph.getRenderer().setParent(new TestRenderer());
        // Keep sonar happy; the real test is through the log messages, so we just assert true here.
        Assertions.assertEquals(leading, verticalParagraph.<Leading>getProperty(Property.LEADING));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY,
            logLevel = LogLevelConstants.WARN),})
    public void unsupportedInheritedPropertiesMustLogWarning() {
        VerticalParagraph verticalParagraph = new VerticalParagraph(false);
        TestRenderer parentRenderer = new TestRenderer();
        parentRenderer.setProperty(Property.TEXT_ANCHOR, TextAnchor.END);
        IRenderer verticalParagraphRenderer = verticalParagraph.getRenderer();
        verticalParagraphRenderer.setParent(parentRenderer);

        // Actual test is done through the log messages, so we just assert here to keep sonar happy.
        Assertions.assertNull(verticalParagraph.<TextAnchor>getProperty(Property.TEXT_ANCHOR));
    }

    @ResourceLock(value = Resources.GLOBAL)
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING))
    public void verticalTextShouldUseDefaultTypographyApplierWhenTypographyAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(CJK_FONT);
        TestTypographyApplier testApplier = new TestTypographyApplier(true);
        TypographyUtils.setTypographyApplierInstance(testApplier);
        Document dummyDocument = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        VerticalParagraph p = new VerticalParagraph("Hello world!", false);
        p.add("\u0E2D\u0E32\u0E01\u0E32\u0E28");
        p.setFont(font);
        p.setProperty(Property.TYPOGRAPHY_CONFIG, Boolean.TRUE);
        p.setProperty(Property.FONT_KERNING, FontKerning.YES);
        dummyDocument.add(p);
        dummyDocument.close();
        Assertions.assertFalse(testApplier.called, "Default typography applier should be used for vertical text");
    }

    @ResourceLock(value = Resources.GLOBAL)
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING))
    public void verticalTextShouldUseDefaultTypographyApplierWhenTypographyNotAvailable() throws IOException {
        PdfFont font = PdfFontFactory.createFont(CJK_FONT);
        TestTypographyApplier testApplier = new TestTypographyApplier(false);
        TypographyUtils.setTypographyApplierInstance(testApplier);
        Document dummyDocument = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
        VerticalParagraph p = new VerticalParagraph("Hello world!", false);
        p.add("\u0E2D\u0E32\u0E01\u0E32\u0E28");
        p.setFont(font);
        p.setProperty(Property.TYPOGRAPHY_CONFIG, Boolean.TRUE);
        p.setProperty(Property.FONT_KERNING, FontKerning.YES);
        dummyDocument.add(p);
        dummyDocument.close();
        Assertions.assertFalse(testApplier.called, "Default typography applier should be used for vertical text");
    }

    private static class TestRenderer extends AbstractRenderer {

        public TestRenderer() {
            super();
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            return null;
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }

    private static class TestTypographyApplier extends AbstractTypographyApplier {

        public final boolean emulatePdfCalligraphInstance;
        public boolean called = false;

        public TestTypographyApplier(boolean emulatePdfCalligraphInstance) {
            super();
            this.emulatePdfCalligraphInstance = emulatePdfCalligraphInstance;
        }

        @Override
        public boolean isPdfCalligraphInstance() {
            //This one is not counted
            return emulatePdfCalligraphInstance;
        }

        @Override
        public Collection<UnicodeScript> getSupportedScripts() {
            called = true;
            return Collections.<UnicodeScript>emptyList();
        }

        @Override
        public Collection<UnicodeScript> getSupportedScripts(Object configurator) {
            called = true;
            return super.getSupportedScripts(configurator);
        }

        @Override
        public boolean applyOtfScript(TrueTypeFont font, GlyphLine glyphLine, UnicodeScript script, Object configurator,
                SequenceId id, IMetaInfo metaInfo) {
            called = true;
            return super.applyOtfScript(font, glyphLine, script, configurator, id, metaInfo);
        }

        @Override
        public boolean applyKerning(FontProgram fontProgram, GlyphLine text, SequenceId sequenceId,
                IMetaInfo metaInfo) {
            called = true;
            return super.applyKerning(fontProgram, text, sequenceId, metaInfo);
        }

        @Override
        public byte[] getBidiLevels(BaseDirection baseDirection, int[] unicodeIds, SequenceId sequenceId,
                IMetaInfo metaInfo) {
            called = true;
            return super.getBidiLevels(baseDirection, unicodeIds, sequenceId, metaInfo);
        }

        @Override
        public int[] reorderLine(java.util.List<RendererGlyph> line, byte[] lineLevels, byte[] levels) {
            called = true;
            return super.reorderLine(line, lineLevels, levels);
        }

        @Override
        public List<Integer> getPossibleBreaks(String str) {
            called = true;
            return super.getPossibleBreaks(str);
        }

        @Override
        public Map<String, byte[]> loadShippedFonts() throws IOException {
            called = true;
            return super.loadShippedFonts();
        }
    }
}
