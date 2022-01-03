/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
