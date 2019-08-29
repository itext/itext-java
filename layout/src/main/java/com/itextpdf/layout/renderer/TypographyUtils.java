/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypographyUtils {

    private static final Logger logger = LoggerFactory.getLogger(TypographyUtils.class);

    private static final String TYPOGRAPHY_PACKAGE = "com.itextpdf.typography.";

    private static final String SHAPER = "shaping.Shaper";
    private static final String BIDI_CHARACTER_MAP = "bidi.BidiCharacterMap";
    private static final String BIDI_BRACKET_MAP = "bidi.BidiBracketMap";
    private static final String BIDI_ALGORITHM = "bidi.BidiAlgorithm";

    private static final String APPLY_OTF_SCRIPT = "applyOtfScript";
    private static final String APPLY_KERNING = "applyKerning";
    private static final String GET_SUPPORTED_SCRIPTS = "getSupportedScripts";

    private static final String GET_CHARACTER_TYPES = "getCharacterTypes";
    private static final String GET_BRACKET_TYPES = "getBracketTypes";
    private static final String GET_BRACKET_VALUES = "getBracketValues";
    private static final String GET_PAIRED_BRACKET = "getPairedBracket";
    private static final String GET_LEVELS = "getLevels";
    private static final String COMPUTE_REORDERING = "computeReordering";
    private static final String INVERSE_REORDERING = "inverseReordering";

    private static final Collection<Character.UnicodeScript> SUPPORTED_SCRIPTS;
    private static final boolean TYPOGRAPHY_MODULE_INITIALIZED;

    private static Map<String, Class<?>> cachedClasses = new HashMap<>();
    private static Map<TypographyMethodSignature, AccessibleObject> cachedMethods = new HashMap<>();

    private static final String typographyNotFoundException = "Cannot find pdfCalligraph module, which was implicitly required by one of the layout properties";

    static {
        boolean moduleFound = false;
        try {
            Class<?> type = getTypographyClass(TYPOGRAPHY_PACKAGE + SHAPER);
            if (type != null) {
                moduleFound = true;
            }
        } catch (ClassNotFoundException ignored) {
        }
        Collection<Character.UnicodeScript> supportedScripts = null;
        if (moduleFound) {
            try {
                supportedScripts = (Collection<Character.UnicodeScript>) callMethod(TYPOGRAPHY_PACKAGE + SHAPER, GET_SUPPORTED_SCRIPTS, new Class[]{});
            } catch (Exception e) {
                supportedScripts = null;
                logger.error(e.getMessage());
            }
        }
        moduleFound = supportedScripts != null;
        if (!moduleFound) {
            cachedClasses.clear();
            cachedMethods.clear();
        }
        TYPOGRAPHY_MODULE_INITIALIZED = moduleFound;
        SUPPORTED_SCRIPTS = supportedScripts;
    }

    private TypographyUtils() {
    }

    /**
     * Checks if layout module can access pdfCalligraph
     * @return <code>true</code> if layout can access pdfCalligraph and <code>false</code> otherwise
     */
    public static boolean isPdfCalligraphAvailable() {
        return TYPOGRAPHY_MODULE_INITIALIZED;
    }

    static void applyOtfScript(FontProgram fontProgram, GlyphLine text, Character.UnicodeScript script, Object typographyConfig) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
        } else {
            callMethod(TYPOGRAPHY_PACKAGE + SHAPER, APPLY_OTF_SCRIPT, new Class[]{TrueTypeFont.class, GlyphLine.class, Character.UnicodeScript.class, Object.class},
                    fontProgram, text, script, typographyConfig);
        }
    }

    static void applyKerning(FontProgram fontProgram, GlyphLine text) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
        } else {
            callMethod(TYPOGRAPHY_PACKAGE + SHAPER, APPLY_KERNING, new Class[]{FontProgram.class, GlyphLine.class},
                    fontProgram, text);
//            Shaper.applyKerning(fontProgram, text);
        }
    }

    static byte[] getBidiLevels(BaseDirection baseDirection, int[] unicodeIds) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
        } else {
            byte direction;
            switch (baseDirection) {
                case LEFT_TO_RIGHT:
                    direction = 0;
                    break;
                case RIGHT_TO_LEFT:
                    direction = 1;
                    break;
                case DEFAULT_BIDI:
                default:
                    direction = 2;
                    break;
            }

            int len = unicodeIds.length;
            byte[] types = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_CHARACTER_MAP, GET_CHARACTER_TYPES, new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
//            byte[] types = BidiCharacterMap.getCharacterTypes(unicodeIds, 0, len);
            byte[] pairTypes = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_BRACKET_MAP, GET_BRACKET_TYPES, new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
//            byte[] pairTypes = BidiBracketMap.getBracketTypes(unicodeIds, 0, len);
            int[] pairValues = (int[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_BRACKET_MAP, GET_BRACKET_VALUES, new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
//            int[] pairValues = BidiBracketMap.getBracketValues(unicodeIds, 0, len);
            Object bidiReorder = callConstructor(TYPOGRAPHY_PACKAGE + BIDI_ALGORITHM, new Class[]{byte[].class, byte[].class, int[].class, byte.class},
                    types, pairTypes, pairValues, direction);
//            BidiAlgorithm bidiReorder = new BidiAlgorithm(types, pairTypes, pairValues, direction);
            return (byte[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_ALGORITHM, GET_LEVELS, bidiReorder, new Class[]{int[].class},
                    new int[]{len});
//            return bidiReorder.getLevels(new int[]{len});
        }
        return null;
    }

    static int[] reorderLine(List<LineRenderer.RendererGlyph> line, byte[] lineLevels, byte[] levels) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
        } else {
            if (levels == null) {
                return null;
            }
            int[] reorder = (int[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_ALGORITHM, COMPUTE_REORDERING, new Class[]{byte[].class},
                    lineLevels);
//            int[] reorder = BidiAlgorithm.computeReordering(lineLevels);
            int[] inverseReorder = (int[]) callMethod(TYPOGRAPHY_PACKAGE + BIDI_ALGORITHM, INVERSE_REORDERING, new Class[] {int[].class}, reorder);
//            int[] inverseReorder = BidiAlgorithm.inverseReordering(reorder);
            List<LineRenderer.RendererGlyph> reorderedLine = new ArrayList<>(lineLevels.length);
            for (int i = 0; i < line.size(); i++) {
                reorderedLine.add(line.get(reorder[i]));

                // Mirror RTL glyphs
                if (levels[reorder[i]] % 2 == 1) {
                    if (reorderedLine.get(i).glyph.hasValidUnicode()) {
                        int unicode = reorderedLine.get(i).glyph.getUnicode();
                        int pairedBracket = (int) callMethod(TYPOGRAPHY_PACKAGE + BIDI_BRACKET_MAP, GET_PAIRED_BRACKET, new Class[]{int.class},
                                unicode);
//                        int pairedBracket = BidiBracketMap.getPairedBracket(reorderedLine.get(i).glyph.getUnicode());
                        if (pairedBracket != unicode) {
                            PdfFont font = reorderedLine.get(i).renderer.getPropertyAsFont(Property.FONT);
                            reorderedLine.set(i, new LineRenderer.RendererGlyph(font.getGlyph(pairedBracket), reorderedLine.get(i).renderer));
                        }
                    }
                }
            }

            // fix anchorDelta
            for (int i = 0; i < reorderedLine.size(); i++) {
                Glyph glyph = reorderedLine.get(i).glyph;
                if (glyph.hasPlacement()) {
                    int oldAnchor = reorder[i] + glyph.getAnchorDelta();
                    int newPos = inverseReorder[oldAnchor];
                    int newAnchorDelta = newPos - i;
                    glyph.setAnchorDelta((short) newAnchorDelta);
                }
            }

            line.clear();
            line.addAll(reorderedLine);
            return reorder;
        }
        return null;
    }

    static Collection<Character.UnicodeScript> getSupportedScripts() {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
            return null;
        } else {
            return SUPPORTED_SCRIPTS;
        }
    }

    static Collection<Character.UnicodeScript> getSupportedScripts(Object typographyConfig) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn(typographyNotFoundException);
            return null;
        } else {
            return (Collection<Character.UnicodeScript>) callMethod(TYPOGRAPHY_PACKAGE + SHAPER, GET_SUPPORTED_SCRIPTS, (Object) null, new Class[] {Object.class}, typographyConfig);
        }
    }

    private static Object callMethod(String className, String methodName, Class[] parameterTypes, Object... args) {
        return callMethod(className, methodName, (Object) null, parameterTypes, args);
    }

    private static Object callMethod(String className, String methodName, Object target, Class[] parameterTypes, Object... args) {
        try {
            Method method = findMethod(className, methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (NoSuchMethodException e) {
            logger.warn(MessageFormatUtil.format("Cannot find method {0} for class {1}", methodName, className));
        } catch (ClassNotFoundException e) {
            logger.warn(MessageFormatUtil.format("Cannot find class {0}", className));
        } catch (IllegalArgumentException e) {
            logger.warn(MessageFormatUtil.format("Illegal arguments passed to {0}#{1} method call: {2}", className, methodName, e.getMessage()));
        } catch (Exception e) {
            // Converting checked exceptions to unchecked RuntimeException (java-specific comment).
            //
            // If typography utils throws an exception at this point, we consider it as unrecoverable situation for
            // its callers (layouting methods). Presence of typography module in class path is checked before.
            // It's might be more suitable to wrap checked exceptions at a bit higher level, but we do it here for
            // the sake of convenience.
            //
            // The RuntimeException exception is used instead of, for example, PdfException, because failure here is
            // unexpected and is not connected to PDF documents processing.
            throw new RuntimeException(e.toString(), e);
        }
        return null;
    }

    private static Object callConstructor(String className, Class[] parameterTypes, Object... args) {
        try {
            Constructor<?> constructor = findConstructor(className, parameterTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            logger.warn(MessageFormatUtil.format("Cannot find constructor for class {0}", className));
        } catch (ClassNotFoundException e) {
            logger.warn(MessageFormatUtil.format("Cannot find class {0}", className));
        } catch (Exception exc) {
            // Converting checked exceptions to unchecked RuntimeException (java-specific comment).
            //
            // If typography utils throws an exception at this point, we consider it as unrecoverable situation for
            // its callers (layouting methods). Presence of typography module in class path is checked before.
            // It's might be more suitable to wrap checked exceptions at a bit higher level, but we do it here for
            // the sake of convenience.
            //
            // The RuntimeException exception is used instead of, for example, PdfException, because failure here is
            // unexpected and is not connected to PDF documents processing.
            throw new RuntimeException(exc.toString(), exc);
        }
        return null;
    }

    private static Method findMethod(String className, String methodName, Class[] parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
        TypographyMethodSignature tm = new TypographyMethodSignature(className, parameterTypes, methodName);
        Method m = (Method) cachedMethods.get(tm);
        if (m == null) {
            m = findClass(className).getMethod(methodName, parameterTypes);
            cachedMethods.put(tm, m);
        }
        return m;
    }

    private static Constructor<?> findConstructor(String className, Class[] parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
        TypographyMethodSignature tc = new TypographyMethodSignature(className, parameterTypes);
        Constructor<?> c = (Constructor<?>) cachedMethods.get(tc);
        if (c == null) {
            c = findClass(className).getConstructor(parameterTypes);
            cachedMethods.put(tc, c);
        }
        return c;
    }

    private static Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> c = cachedClasses.get(className);
        if (c == null) {
            c = getTypographyClass(className);
            cachedClasses.put(className, c);
        }
        return c;
    }

    private static Class<?> getTypographyClass(String typographyClassName) throws ClassNotFoundException {
        return Class.forName(typographyClassName);
    }

    private static class TypographyMethodSignature {
        protected final String className;
        protected Class[] parameterTypes;
        private final String methodName;

        TypographyMethodSignature(String className, Class[] parameterTypes) {
            this(className, parameterTypes, null);
        }

        TypographyMethodSignature(String className, Class[] parameterTypes, String methodName) {
            this.methodName = methodName;
            this.className = className;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypographyMethodSignature that = (TypographyMethodSignature) o;

            if (!className.equals(that.className)) return false;
            if (!Arrays.equals(parameterTypes, that.parameterTypes)) return false;
            return methodName != null ? methodName.equals(that.methodName) : that.methodName == null;

        }

        @Override
        public int hashCode() {
            int result = className.hashCode();
            result = 31 * result + Arrays.hashCode(parameterTypes);
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            return result;
        }
    }
}
