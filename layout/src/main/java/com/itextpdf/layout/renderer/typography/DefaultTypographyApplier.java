package com.itextpdf.layout.renderer.typography;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.renderer.LineRenderer.RendererGlyph;

import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultTypographyApplier extends AbstractTypographyApplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTypographyApplier.class);

    public DefaultTypographyApplier() {
    }

    @Override
    public boolean isPdfCalligraphInstance() {
        return false;
    }

    @Override
    public boolean applyOtfScript(TrueTypeFont font, GlyphLine glyphLine, UnicodeScript script, Object configurator,
            SequenceId id, IMetaInfo metaInfo) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.applyOtfScript(font, glyphLine, script, configurator, id, metaInfo);
    }

    @Override
    public Collection<UnicodeScript> getSupportedScripts() {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.getSupportedScripts();
    }

    @Override
    public Collection<UnicodeScript> getSupportedScripts(Object configurator) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.getSupportedScripts(configurator);
    }

    @Override
    public boolean applyKerning(FontProgram fontProgram, GlyphLine text, SequenceId sequenceId, IMetaInfo metaInfo) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.applyKerning(fontProgram, text, sequenceId, metaInfo);
    }

    @Override
    public byte[] getBidiLevels(BaseDirection baseDirection, int[] unicodeIds, SequenceId sequenceId,
            IMetaInfo metaInfo) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.getBidiLevels(baseDirection, unicodeIds, sequenceId, metaInfo);
    }

    @Override
    public int[] reorderLine(List<RendererGlyph> line, byte[] lineLevels, byte[] levels) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.reorderLine(line, lineLevels, levels);
    }

    @Override
    public List<Integer> getPossibleBreaks(String str) {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.getPossibleBreaks(str);
    }

    @Override
    public Map<String, byte[]> loadShippedFonts() throws IOException {
        LOGGER.warn(LogMessageConstant.TYPOGRAPHY_NOT_FOUND);
        return super.loadShippedFonts();
    }
}
