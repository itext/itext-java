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
package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * If you are using True Type fonts, you can declare the paths of the different ttf- and ttc-files
 * to this class first and then create fonts in your code using one of the getFont method
 * without having to enter a path as parameter.
 */
class FontRegisterProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FontRegisterProvider.class);
    /**
     * This is a map of postscriptfontnames of fonts and the path of their font file.
     */
    private final Map<String, String> fontNames = new HashMap<>();

    private static String[] TTFamilyOrder = {
            "3", "1", "1033",
            "3", "0", "1033",
            "1", "0", "0",
            "0", "3", "0"
    };

    /**
     * This is a map of fontfamilies.
     */
    private final Map<String, List<String>> fontFamilies = new HashMap<>();

    /**
     * Creates new FontRegisterProvider
     */
    FontRegisterProvider() {
        registerStandardFonts();
        registerStandardFontFamilies();
    }

    /**
     * Constructs a <CODE>Font</CODE>-object.
     *
     * @param fontName the name of the font
     * @param style    the style of this font
     * @return the Font constructed based on the parameters
     */
    FontProgram getFont(String fontName, int style) throws java.io.IOException {
        return getFont(fontName, style, true);
    }


    /**
     * Constructs a <CODE>Font</CODE>-object.
     *
     * @param fontName the name of the font
     * @param style    the style of this font
     * @param cached   true if the font comes from the cache or is added to
     *                 the cache if new, false if the font is always created new
     * @return the Font constructed based on the parameters
     */
    FontProgram getFont(String fontName, int style, boolean cached) throws java.io.IOException {
        if (fontName == null)
            return null;
        String lowerCaseFontName = fontName.toLowerCase();
        List<String> family = !lowerCaseFontName.equalsIgnoreCase(StandardFonts.TIMES_ROMAN) ?
                fontFamilies.get(lowerCaseFontName) : fontFamilies.get(StandardFontFamilies.TIMES.toLowerCase());
        if (family != null) {
            synchronized (family) {
                // some bugs were fixed here by Daniel Marczisovszky
                int s = style == FontStyles.UNDEFINED ? FontStyles.NORMAL : style;
                for (String f : family) {
                    String lcf = f.toLowerCase();
                    int fs = FontStyles.NORMAL;
                    if (lcf.contains("bold")) fs |= FontStyles.BOLD;
                    if (lcf.contains("italic") || lcf.contains("oblique")) fs |= FontStyles.ITALIC;
                    if ((s & FontStyles.BOLDITALIC) == fs) {
                        fontName = f;
                        break;
                    }
                }
            }
        }
        return getFontProgram(fontName, cached);
    }

    protected void registerStandardFonts() {
        fontNames.put(StandardFonts.COURIER.toLowerCase(), StandardFonts.COURIER);
        fontNames.put(StandardFonts.COURIER_BOLD.toLowerCase(), StandardFonts.COURIER_BOLD);
        fontNames.put(StandardFonts.COURIER_OBLIQUE.toLowerCase(), StandardFonts.COURIER_OBLIQUE);
        fontNames.put(StandardFonts.COURIER_BOLDOBLIQUE.toLowerCase(), StandardFonts.COURIER_BOLDOBLIQUE);
        fontNames.put(StandardFonts.HELVETICA.toLowerCase(), StandardFonts.HELVETICA);
        fontNames.put(StandardFonts.HELVETICA_BOLD.toLowerCase(), StandardFonts.HELVETICA_BOLD);
        fontNames.put(StandardFonts.HELVETICA_OBLIQUE.toLowerCase(), StandardFonts.HELVETICA_OBLIQUE);
        fontNames.put(StandardFonts.HELVETICA_BOLDOBLIQUE.toLowerCase(), StandardFonts.HELVETICA_BOLDOBLIQUE);
        fontNames.put(StandardFonts.SYMBOL.toLowerCase(), StandardFonts.SYMBOL);
        fontNames.put(StandardFonts.TIMES_ROMAN.toLowerCase(), StandardFonts.TIMES_ROMAN);
        fontNames.put(StandardFonts.TIMES_BOLD.toLowerCase(), StandardFonts.TIMES_BOLD);
        fontNames.put(StandardFonts.TIMES_ITALIC.toLowerCase(), StandardFonts.TIMES_ITALIC);
        fontNames.put(StandardFonts.TIMES_BOLDITALIC.toLowerCase(), StandardFonts.TIMES_BOLDITALIC);
        fontNames.put(StandardFonts.ZAPFDINGBATS.toLowerCase(), StandardFonts.ZAPFDINGBATS);
    }

    protected void registerStandardFontFamilies() {
        List<String> family;
        family = new ArrayList<>();
        family.add(StandardFonts.COURIER);
        family.add(StandardFonts.COURIER_BOLD);
        family.add(StandardFonts.COURIER_OBLIQUE);
        family.add(StandardFonts.COURIER_BOLDOBLIQUE);
        fontFamilies.put(StandardFontFamilies.COURIER.toLowerCase(), family);
        family = new ArrayList<>();
        family.add(StandardFonts.HELVETICA);
        family.add(StandardFonts.HELVETICA_BOLD);
        family.add(StandardFonts.HELVETICA_OBLIQUE);
        family.add(StandardFonts.HELVETICA_BOLDOBLIQUE);
        fontFamilies.put(StandardFontFamilies.HELVETICA.toLowerCase(), family);
        family = new ArrayList<>();
        family.add(StandardFonts.SYMBOL);
        fontFamilies.put(StandardFontFamilies.SYMBOL.toLowerCase(), family);
        family = new ArrayList<>();
        family.add(StandardFonts.TIMES_ROMAN);
        family.add(StandardFonts.TIMES_BOLD);
        family.add(StandardFonts.TIMES_ITALIC);
        family.add(StandardFonts.TIMES_BOLDITALIC);
        fontFamilies.put(StandardFontFamilies.TIMES.toLowerCase(), family);
        family = new ArrayList<>();
        family.add(StandardFonts.ZAPFDINGBATS);
        fontFamilies.put(StandardFontFamilies.ZAPFDINGBATS.toLowerCase(), family);
    }

    protected FontProgram getFontProgram(String fontName, boolean cached) throws java.io.IOException {
        FontProgram fontProgram = null;
        fontName = fontNames.get(fontName.toLowerCase());
        if (fontName != null) {
            fontProgram = FontProgramFactory.createFont(fontName, cached);
        }
        return fontProgram;
    }

    /**
     * Register a font by giving explicitly the font family and name.
     *
     * @param familyName the font family
     * @param fullName   the font name
     * @param path       the font path
     */
    void registerFontFamily(String familyName, String fullName, String path) {
        if (path != null)
            fontNames.put(fullName, path);
        List<String> family;
        synchronized (fontFamilies) {
            family = fontFamilies.get(familyName);
            if (family == null) {
                family = new ArrayList<>();
                fontFamilies.put(familyName, family);
            }
        }
        synchronized (family) {
            if (!family.contains(fullName)) {
                int fullNameLength = fullName.length();
                boolean inserted = false;
                for (int j = 0; j < family.size(); ++j) {
                    if (family.get(j).length() >= fullNameLength) {
                        family.add(j, fullName);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    family.add(fullName);
                    String newFullName = fullName.toLowerCase();
                    if (newFullName.endsWith("regular")) {
                        //remove "regular" at the end of the font name
                        newFullName = newFullName.substring(0, newFullName.length() - 7).trim();
                        //insert this font name at the first position for higher priority
                        family.add(0, fullName.substring(0, newFullName.length()));
                    }
                }
            }
        }
    }

    /**
     * Register a font file, either .ttf or .otf, .afm or a font from TrueType Collection.
     * If a TrueType Collection is registered, an additional index of the font program can be specified
     *
     * @param path the path to a ttf- or ttc-file
     */
    void registerFont(String path) {
        registerFont(path, null);
    }

    /**
     * Register a font file and use an alias for the font contained in it.
     *
     * @param path  the path to a font file
     * @param alias the alias you want to use for the font
     */
    void registerFont(String path, String alias) {
        try {
            if (path.toLowerCase().endsWith(".ttf") || path.toLowerCase().endsWith(".otf") || path.toLowerCase().indexOf(".ttc,") > 0) {
                FontProgram fontProgram = FontProgramFactory.createFont(path);
                Object[] allNames = new Object[]{fontProgram.getFontNames().getFontName(), fontProgram.getFontNames().getFamilyName(), fontProgram.getFontNames().getFullName()};
                fontNames.put(((String) allNames[0]).toLowerCase(), path);
                if (alias != null) {
                    String lcAlias = alias.toLowerCase();
                    fontNames.put(lcAlias, path);
                    if (lcAlias.endsWith("regular")) {
                        //do this job to give higher priority to regular fonts in comparison with light, narrow, etc
                        saveCopyOfRegularFont(lcAlias, path);
                    }
                }
                // register all the font names with all the locales
                String[][] names = (String[][]) allNames[2]; //full name
                for (String[] name : names) {
                    String lcName = name[3].toLowerCase();
                    fontNames.put(lcName, path);
                    if (lcName.endsWith("regular")) {
                        //do this job to give higher priority to regular fonts in comparison with light, narrow, etc
                        saveCopyOfRegularFont(lcName, path);
                    }
                }
                String fullName;
                String familyName = null;
                names = (String[][]) allNames[1]; //family name
                for (int k = 0; k < TTFamilyOrder.length; k += 3) {
                    for (String[] name : names) {
                        if (TTFamilyOrder[k].equals(name[0]) && TTFamilyOrder[k + 1].equals(name[1]) && TTFamilyOrder[k + 2].equals(name[2])) {
                            familyName = name[3].toLowerCase();
                            k = TTFamilyOrder.length;
                            break;
                        }
                    }
                }
                if (familyName != null) {
                    String lastName = "";
                    names = (String[][]) allNames[2]; //full name
                    for (String[] name : names) {
                        for (int k = 0; k < TTFamilyOrder.length; k += 3) {
                            if (TTFamilyOrder[k].equals(name[0]) && TTFamilyOrder[k + 1].equals(name[1]) && TTFamilyOrder[k + 2].equals(name[2])) {
                                fullName = name[3];
                                if (fullName.equals(lastName))
                                    continue;
                                lastName = fullName;
                                registerFontFamily(familyName, fullName, null);
                                break;
                            }
                        }
                    }
                }
            } else if (path.toLowerCase().endsWith(".ttc")) {
                TrueTypeCollection ttc = new TrueTypeCollection(path);
                for (int i = 0; i < ttc.getTTCSize(); i++) {
                    String fullPath = path + "," + i;
                    if (alias != null) {
                        registerFont(fullPath, alias + "," + i);
                    } else {
                        registerFont(fullPath);
                    }
                }
            } else if (path.toLowerCase().endsWith(".afm") || path.toLowerCase().endsWith(".pfm")) {
                FontProgram fontProgram = FontProgramFactory.createFont(path);
                String fullName = fontProgram.getFontNames().getFullName()[0][3].toLowerCase();
                String familyName = fontProgram.getFontNames().getFamilyName()[0][3].toLowerCase();
                String psName = fontProgram.getFontNames().getFontName().toLowerCase();
                registerFontFamily(familyName, fullName, null);
                fontNames.put(psName, path);
                fontNames.put(fullName, path);
            }
            LOGGER.trace(MessageFormatUtil.format("Registered {0}", path));
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    // remove regular and correct last symbol
    // do this job to give higher priority to regular fonts in comparison with light, narrow, etc
    // Don't use this method for not regular fonts!
    boolean saveCopyOfRegularFont(String regularFontName, String path) {
        //remove "regular" at the end of the font name
        String alias = regularFontName.substring(0, regularFontName.length() - 7).trim();
        if (!fontNames.containsKey(alias)) {
            fontNames.put(alias, path);
            return true;
        }
        return false;
    }

    /**
     * Register all the fonts in a directory.
     *
     * @param dir the directory
     * @return the number of fonts registered
     */
    int registerFontDirectory(String dir) {
        return registerFontDirectory(dir, false);
    }

    /**
     * Register all the fonts in a directory and possibly its subdirectories.
     *
     * @param dir                the directory
     * @param scanSubdirectories recursively scan subdirectories if <code>true</true>
     * @return the number of fonts registered
     */
    int registerFontDirectory(String dir, boolean scanSubdirectories) {
        LOGGER.debug(MessageFormatUtil.format("Registering directory {0}, looking for fonts", dir));
        int count = 0;
        try {
            String[] files = FileUtil.listFilesInDirectory(dir, scanSubdirectories);
            if (files == null)
                return 0;
            for (String file : files) {
                try {
                    String suffix = file.length() < 4 ? null : file.substring(file.length() - 4).toLowerCase();
                    if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                        /* Only register Type 1 fonts with matching .pfb files */
                        String pfb = file.substring(0, file.length() - 4) + ".pfb";
                        if (FileUtil.fileExists(pfb)) {
                            registerFont(file, null);
                            ++count;
                        }
                    } else if (".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix)) {
                        registerFont(file, null);
                        ++count;
                    }
                } catch (Exception e) {
                    //empty on purpose
                }
            }
        } catch (Exception e) {
            //empty on purpose
        }
        return count;
    }

    /**
     * Register fonts in some probable directories. It usually works in Windows,
     * Linux and Solaris.
     *
     * @return the number of fonts registered
     */
    int registerSystemFontDirectories() {
        int count = 0;
        String[] withSubDirs = {
                FileUtil.getFontsDir(),
                "/usr/share/X11/fonts",
                "/usr/X/lib/X11/fonts",
                "/usr/openwin/lib/X11/fonts",
                "/usr/share/fonts",
                "/usr/X11R6/lib/X11/fonts"
        };
        for (String directory : withSubDirs) {
            count += registerFontDirectory(directory, true);
        }

        String[] withoutSubDirs = {
                "/Library/Fonts",
                "/System/Library/Fonts"
        };
        for (String directory : withoutSubDirs) {
            count += registerFontDirectory(directory, false);
        }

        return count;
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered fonts
     */
    Set<String> getRegisteredFonts() {
        return fontNames.keySet();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered font families
     */
    Set<String> getRegisteredFontFamilies() {
        return fontFamilies.keySet();
    }

    /**
     * Checks if a certain font is registered.
     *
     * @param fontname the name of the font that has to be checked.
     * @return true if the font is found
     */
    boolean isRegisteredFont(String fontname) {
        return fontNames.containsKey(fontname.toLowerCase());
    }

    public void clearRegisteredFonts() {
        fontNames.clear();
        registerStandardFonts();
    }

    public void clearRegisteredFontFamilies() {
        fontFamilies.clear();
        registerStandardFontFamilies();
    }
}
