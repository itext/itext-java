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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.renderer.typography.AbstractTypographyApplier;
import com.itextpdf.layout.renderer.typography.DefaultTypographyApplier;

import java.io.IOException;
import java.lang.Character.UnicodeScript;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class TypographyUtils {

    private static final String TYPOGRAPHY_PACKAGE = "com.itextpdf.typography.";
    private static final String TYPOGRAPHY_APPLIER = "shaping.TypographyApplier";

    private static final String TYPOGRAPHY_APPLIER_INITIALIZE = "registerForLayout";

    private static AbstractTypographyApplier applierInstance;

    static {
        try {
            Class<?> type = getTypographyClass(TYPOGRAPHY_PACKAGE + TYPOGRAPHY_APPLIER);
            if (type != null) {
                Method method = type.getMethod(TYPOGRAPHY_APPLIER_INITIALIZE, new Class[] {});
                if (method != null) {
                    method.invoke(null, new Object[] {});
                }
            }
        } catch (Exception ignored) {
            // do nothing
        }
        if (applierInstance == null) {
            setTypographyApplierInstance(new DefaultTypographyApplier());
        }
    }

    private TypographyUtils() {
    }

    /**
     * Set {@link AbstractTypographyApplier} instance to use.
     *
     * @param newInstance the instance to set
     */
    public static void setTypographyApplierInstance(AbstractTypographyApplier newInstance) {
        applierInstance = newInstance;
    }

    /**
     * Checks if layout module can access pdfCalligraph
     * @return <code>true</code> if layout can access pdfCalligraph and <code>false</code> otherwise
     */
    public static boolean isPdfCalligraphAvailable() {
        return applierInstance.isPdfCalligraphInstance();
    }

    public static Collection<Character.UnicodeScript> getSupportedScripts() {
        return applierInstance.getSupportedScripts();
    }

    public static Collection<Character.UnicodeScript> getSupportedScripts(Object typographyConfig) {
        return applierInstance.getSupportedScripts(typographyConfig);
    }

    public static Map<String, byte[]> loadShippedFonts() throws IOException {
        return applierInstance.loadShippedFonts();
    }

    static void applyOtfScript(FontProgram fontProgram, GlyphLine text, UnicodeScript script, Object typographyConfig,
            SequenceId sequenceId, IMetaInfo metaInfo) {
        applierInstance.applyOtfScript((TrueTypeFont) fontProgram, text, script, typographyConfig,
                sequenceId, metaInfo);
    }

    static void applyKerning(FontProgram fontProgram, GlyphLine text, SequenceId sequenceId, IMetaInfo metaInfo) {
        applierInstance.applyKerning(fontProgram, text, sequenceId, metaInfo);
    }

    static byte[] getBidiLevels(BaseDirection baseDirection, int[] unicodeIds,
            SequenceId sequenceId, IMetaInfo metaInfo) {
        return applierInstance.getBidiLevels(baseDirection, unicodeIds, sequenceId, metaInfo);
    }

    static int[] reorderLine(List<LineRenderer.RendererGlyph> line, byte[] lineLevels, byte[] levels) {
        return applierInstance.reorderLine(line, lineLevels, levels);
    }

    static List<Integer> getPossibleBreaks(String str) {
        return applierInstance.getPossibleBreaks(str);
    }

    private static Class<?> getTypographyClass(String typographyClassName) throws ClassNotFoundException {
        return Class.forName(typographyClassName);
    }
}
