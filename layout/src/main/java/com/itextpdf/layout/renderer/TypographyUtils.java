/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TypographyUtils {

    private static final Logger logger = LoggerFactory.getLogger(TypographyUtils.class);
    private static final String TYPOGRAPHY_PACKAGE = "com.itextpdf.typography.";
    private static final boolean TYPOGRAPHY_MODULE_INITIALIZED = checkTypographyModulePresence();

    static void applyOtfScript(FontProgram fontProgram, GlyphLine text, Character.UnicodeScript script) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn("Cannot find advanced typography module, which was implicitly required by one of the layout properties");
        } else {
            callMethod(TYPOGRAPHY_PACKAGE + "shaping.Shaper", "applyOtfScript", new Class[]{TrueTypeFont.class, GlyphLine.class, Character.UnicodeScript.class},
                    fontProgram, text, script);
            //Shaper.applyOtfScript((TrueTypeFont)font.getFontProgram(), text, script);
        }
    }

    static void applyKerning(FontProgram fontProgram, GlyphLine text) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn("Cannot find advanced typography module, which was implicitly required by one of the layout properties");
        } else {
            callMethod(TYPOGRAPHY_PACKAGE + "shaping.Shaper", "applyKerning", new Class[]{FontProgram.class, GlyphLine.class},
                    fontProgram, text);
            //Shaper.applyKerning(font.getFontProgram(), text);
        }
    }

    static byte[] getBidiLevels(Property.BaseDirection baseDirection, int[] unicodeIds) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn("Cannot find advanced typography module, which was implicitly required by one of the layout properties");
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
            byte[] types = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiCharacterMap", "getCharacterTypes", new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
            //byte[] types = BidiCharacterMap.getCharacterTypes(unicodeIds, 0, text.end - text.start;
            byte[] pairTypes = (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getBracketTypes", new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
            //byte[] pairTypes = BidiBracketMap.getBracketTypes(unicodeIds, 0, text.end - text.start);
            int[] pairValues = (int[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getBracketValues", new Class[]{int[].class, int.class, int.class},
                    unicodeIds, 0, len);
            //int[] pairValues = BidiBracketMap.getBracketValues(unicodeIds, 0, text.end - text.start);
            Object bidiReorder = callConstructor(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", new Class[]{byte[].class, byte[].class, int[].class, byte.class},
                    types, pairTypes, pairValues, direction);
            //BidiAlgorithm bidiReorder = new BidiAlgorithm(types, pairTypes, pairValues, direction);
            return (byte[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", "getLevels", bidiReorder, new Class[]{int[].class},
                    new int[]{len});
            //levels = bidiReorder.getLevels(new int[]{text.end - text.start});
        }
        return null;
    }

    static List<LineRenderer.RendererGlyph> reorderLine(List<LineRenderer.RendererGlyph> line, byte[] lineLevels, byte[] levels) {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn("Cannot find advanced typography module, which was implicitly required by one of the layout properties");
        } else {
            if (levels == null) {
                return null;
            }
            int[] reorder = (int[]) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiAlgorithm", "computeReordering", new Class[]{byte[].class},
                    lineLevels);
            //int[] reorder = BidiAlgorithm.computeReordering(lineLevels);
            List<LineRenderer.RendererGlyph> reorderedLine = new ArrayList<>(lineLevels.length);
            for (int i = 0; i < line.size(); i++) {
                reorderedLine.add(line.get(reorder[i]));

                // Mirror RTL glyphs
                if (levels[reorder[i]] % 2 == 1) {
                    if (reorderedLine.get(i).glyph.hasValidUnicode()) {
                        int pairedBracket = (int) callMethod(TYPOGRAPHY_PACKAGE + "bidi.BidiBracketMap", "getPairedBracket", new Class[]{int.class},
                                reorderedLine.get(i).glyph.getUnicode());
                        PdfFont font = reorderedLine.get(i).renderer.getPropertyAsFont(Property.FONT);
                        //BidiBracketMap.getPairedBracket(reorderedLine.get(i).getUnicode())
                        reorderedLine.set(i, new LineRenderer.RendererGlyph(font.getGlyph(pairedBracket), reorderedLine.get(i).renderer));
                    }
                }
            }
            return reorderedLine;
        }
        return null;
    }

    static Collection<Character.UnicodeScript> getSupportedScripts() {
        if (!TYPOGRAPHY_MODULE_INITIALIZED) {
            logger.warn("Cannot find advanced typography module, which was implicitly required by one of the layout properties");
            return null;
        } else {
            return (Collection<Character.UnicodeScript>)callMethod(TYPOGRAPHY_PACKAGE + "shaping.Shaper", "getSupportedScripts", new Class[] {});
        }
    }

    static boolean isTypographyModuleInitialized() {
        return TYPOGRAPHY_MODULE_INITIALIZED;
    }

    private static boolean checkTypographyModulePresence() {
        boolean moduleFound = false;
        try {
            Class.forName("com.itextpdf.typography.shaping.Shaper");
            moduleFound = true;
        } catch (ClassNotFoundException ignored) {
        }
        return moduleFound;
    }

    private static Object callMethod(String className, String methodName, Class[] parameterTypes, Object... args) {
        return callMethod(className, methodName, null, parameterTypes, args);
    }

    private static Object callMethod(String className, String methodName, Object target, Class[] parameterTypes, Object... args) {
        try {
            Method method = Class.forName(className).getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (NoSuchMethodException e) {
            logger.warn(MessageFormat.format("Cannot find method {0} for class {1}", methodName, className));
        } catch (ClassNotFoundException e) {
            logger.warn(MessageFormat.format("Cannot find class {0}", className));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static Object callConstructor(String className, Class[] parameterTypes, Object... args) {
        Constructor constructor = null;
        try {
            constructor = Class.forName(className).getConstructor(parameterTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            logger.warn(MessageFormat.format("Cannot find constructor for class {0}", className));
        } catch (ClassNotFoundException e) {
            logger.warn(MessageFormat.format("Cannot find class {0}", className));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
