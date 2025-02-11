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
package com.itextpdf.layout.renderer.typography;

import com.itextpdf.commons.actions.AbstractITextEvent;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.renderer.LineRenderer;

import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractTypographyApplier extends AbstractITextEvent {

    protected AbstractTypographyApplier() {
        // do nothing
    }

    public abstract boolean isPdfCalligraphInstance();

    public Collection<UnicodeScript> getSupportedScripts() {
        return null;
    }

    public Collection<Character.UnicodeScript> getSupportedScripts(Object configurator) {
        return null;
    }

    public boolean applyOtfScript(TrueTypeFont font, GlyphLine glyphLine, Character.UnicodeScript script,
            Object configurator, SequenceId id, IMetaInfo metaInfo) {
        return false;
    }

    public boolean applyKerning(FontProgram fontProgram, GlyphLine text, SequenceId sequenceId, IMetaInfo metaInfo) {
        return false;
    }

    public byte[] getBidiLevels(BaseDirection baseDirection, int[] unicodeIds,
            SequenceId sequenceId, IMetaInfo metaInfo) {
        return null;
    }

    public int[] reorderLine(List<LineRenderer.RendererGlyph> line, byte[] lineLevels, byte[] levels) {
        return null;
    }

    public List<Integer> getPossibleBreaks(String str) {
        return null;
    }

    public Map<String, byte[]> loadShippedFonts() throws IOException {
        return null;
    }
}
