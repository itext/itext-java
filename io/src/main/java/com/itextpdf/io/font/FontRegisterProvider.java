package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.FileUtils;
import com.itextpdf.io.util.Utilities;
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
     * This is a map of postscriptfontnames of True Type fonts and the path of their ttf- or ttc-file.
     */
    private final Map<String, String> trueTypeFonts = new HashMap<String, String>();

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
     * This is the default encoding to use.
     */
    public String defaultEncoding = PdfEncodings.WINANSI;

    /**
     * This is the default value of the <VAR>embedded</VAR> variable.
     */
    public boolean defaultEmbedding = false;

    /**
     * Creates new FontRegisterProvider
     */
    public FontRegisterProvider() {
        trueTypeFonts.put(FontConstants.COURIER.toLowerCase(), FontConstants.COURIER);
        trueTypeFonts.put(FontConstants.COURIER_BOLD.toLowerCase(), FontConstants.COURIER_BOLD);
        trueTypeFonts.put(FontConstants.COURIER_OBLIQUE.toLowerCase(), FontConstants.COURIER_OBLIQUE);
        trueTypeFonts.put(FontConstants.COURIER_BOLDOBLIQUE.toLowerCase(), FontConstants.COURIER_BOLDOBLIQUE);
        trueTypeFonts.put(FontConstants.HELVETICA.toLowerCase(), FontConstants.HELVETICA);
        trueTypeFonts.put(FontConstants.HELVETICA_BOLD.toLowerCase(), FontConstants.HELVETICA_BOLD);
        trueTypeFonts.put(FontConstants.HELVETICA_OBLIQUE.toLowerCase(), FontConstants.HELVETICA_OBLIQUE);
        trueTypeFonts.put(FontConstants.HELVETICA_BOLDOBLIQUE.toLowerCase(), FontConstants.HELVETICA_BOLDOBLIQUE);
        trueTypeFonts.put(FontConstants.SYMBOL.toLowerCase(), FontConstants.SYMBOL);
        trueTypeFonts.put(FontConstants.TIMES_ROMAN.toLowerCase(), FontConstants.TIMES_ROMAN);
        trueTypeFonts.put(FontConstants.TIMES_BOLD.toLowerCase(), FontConstants.TIMES_BOLD);
        trueTypeFonts.put(FontConstants.TIMES_ITALIC.toLowerCase(), FontConstants.TIMES_ITALIC);
        trueTypeFonts.put(FontConstants.TIMES_BOLDITALIC.toLowerCase(), FontConstants.TIMES_BOLDITALIC);
        trueTypeFonts.put(FontConstants.ZAPFDINGBATS.toLowerCase(), FontConstants.ZAPFDINGBATS);

        List<String> tmp;
        tmp = new ArrayList();
        tmp.add(FontConstants.COURIER);
        tmp.add(FontConstants.COURIER_BOLD);
        tmp.add(FontConstants.COURIER_OBLIQUE);
        tmp.add(FontConstants.COURIER_BOLDOBLIQUE);
        fontFamilies.put(FontConstants.COURIER.toLowerCase(), tmp);
        tmp = new ArrayList();
        tmp.add(FontConstants.HELVETICA);
        tmp.add(FontConstants.HELVETICA_BOLD);
        tmp.add(FontConstants.HELVETICA_OBLIQUE);
        tmp.add(FontConstants.HELVETICA_BOLDOBLIQUE);
        fontFamilies.put(FontConstants.HELVETICA.toLowerCase(), tmp);
        tmp = new ArrayList();
        tmp.add(FontConstants.SYMBOL);
        fontFamilies.put(FontConstants.SYMBOL.toLowerCase(), tmp);
        tmp = new ArrayList();
        tmp.add(FontConstants.TIMES_ROMAN);
        tmp.add(FontConstants.TIMES_BOLD);
        tmp.add(FontConstants.TIMES_ITALIC);
        tmp.add(FontConstants.TIMES_BOLDITALIC);
        fontFamilies.put(FontConstants.TIMES.toLowerCase(), tmp);
        fontFamilies.put(FontConstants.TIMES_ROMAN.toLowerCase(), tmp);
        tmp = new ArrayList();
        tmp.add(FontConstants.ZAPFDINGBATS);
        fontFamilies.put(FontConstants.ZAPFDINGBATS.toLowerCase(), tmp);
    }

    /**
     * Constructs a <CODE>Font</CODE>-object.
     *
     * @param fontName the name of the font
     * @param style    the style of this font
     * @return the Font constructed based on the parameters
     */
    public FontProgram getFont(final String fontName, final int style) throws java.io.IOException {
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
    public FontProgram getFont(String fontName, int style, boolean cached) throws java.io.IOException {
        if (fontName == null)
            return null;
        String lowerCaseFontName = fontName.toLowerCase();
        List<String> tmp = fontFamilies.get(lowerCaseFontName);
        if (tmp != null) {
            synchronized (tmp) {
                // some bugs were fixed here by Daniel Marczisovszky
                int s = style == FontConstants.UNDEFINED ? FontConstants.NORMAL : style;
                int fs = FontConstants.NORMAL;
                boolean found = false;
                for (String f : tmp) {
                    String lcf = f.toLowerCase();
                    fs = FontConstants.NORMAL;
                    if (lcf.indexOf("bold") != -1) fs |= FontConstants.BOLD;
                    if (lcf.indexOf("italic") != -1 || lcf.indexOf("oblique") != -1) fs |= FontConstants.ITALIC;
                    if ((s & FontConstants.BOLDITALIC) == fs) {
                        fontName = f;
                        found = true;
                        break;
                    }
                }
                if (style != FontConstants.UNDEFINED && found) {
                    style &= ~fs;
                }
            }
        }
        FontProgram fontProgram = getFontProgram(fontName, cached);

        return fontProgram;

    }

    protected FontProgram getFontProgram(String fontName, boolean cached) throws java.io.IOException {
        FontProgram fontProgram = null;
        fontName = trueTypeFonts.get(fontName.toLowerCase());
        // the font is not registered as truetype font
        if (fontName != null) {
            fontProgram = FontFactory.createFont(fontName, cached);
        }
        if (fontProgram == null) {
            try {
                // the font is a type 1 font or CJK font
                fontProgram = FontFactory.createFont(fontName, cached);
            } catch (IOException e) {
            }
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
    public void registerFamily(final String familyName, final String fullName, final String path) {
        if (path != null)
            trueTypeFonts.put(fullName, path);
        List<String> tmp;
        synchronized (fontFamilies) {
            tmp = fontFamilies.get(familyName);
            if (tmp == null) {
                tmp = new ArrayList();
                fontFamilies.put(familyName, tmp);
            }
        }
        synchronized (tmp) {
            if (!tmp.contains(fullName)) {
                int fullNameLength = fullName.length();
                boolean inserted = false;
                for (int j = 0; j < tmp.size(); ++j) {
                    if (tmp.get(j).length() >= fullNameLength) {
                        tmp.add(j, fullName);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    tmp.add(fullName);
                    String newFullName = fullName.toLowerCase();
                    if (newFullName.endsWith("regular")) {
                        //remove "regular" at the end of the font name
                        newFullName = newFullName.substring(0, newFullName.length() - 7).trim();
                        //insert this font name at the first position for higher priority
                        tmp.add(0, fullName.substring(0, newFullName.length()));
                    }
                }
            }
        }
    }

    /**
     * Register a ttf- or a ttc-file.
     *
     * @param path the path to a ttf- or ttc-file
     */

    public void register(final String path) {
        register(path, null);
    }

    /**
     * Register a font file and use an alias for the font contained in it.
     *
     * @param path  the path to a font file
     * @param alias the alias you want to use for the font
     */

    public void register(final String path, final String alias) {
        try {
            if (path.toLowerCase().endsWith(".ttf") || path.toLowerCase().endsWith(".otf") || path.toLowerCase().indexOf(".ttc,") > 0) {
                FontProgram fontProgram = FontFactory.createFont(path);
                Object allNames[] = new Object[]{fontProgram.getFontNames().getFontName(), fontProgram.getFontNames().getFamilyName(), fontProgram.getFontNames().getFullName()};
                trueTypeFonts.put(((String) allNames[0]).toLowerCase(), path);
                if (alias != null) {
                    String lcAlias = alias.toLowerCase();
                    trueTypeFonts.put(lcAlias, path);
                    if (lcAlias.endsWith("regular")) {
                        //do this job to give higher priority to regular fonts in comparison with light, narrow, etc
                        saveCopyOfRegularFont(lcAlias, path);
                    }
                }
                // register all the font names with all the locales
                String[][] names = (String[][]) allNames[2]; //full name
                for (String[] name : names) {
                    String lcName = name[3].toLowerCase();
                    trueTypeFonts.put(lcName, path);
                    if (lcName.endsWith("regular")) {
                        //do this job to give higher priority to regular fonts in comparison with light, narrow, etc
                        saveCopyOfRegularFont(lcName, path);
                    }
                }
                String fullName = null;
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
                                registerFamily(familyName, fullName, null);
                                break;
                            }
                        }
                    }
                }
            } else if (path.toLowerCase().endsWith(".ttc")) {
                if (alias != null) {
                    LOGGER.error("You can't define an alias for a true type collection.");
                }
                TrueTypeCollection ttc = new TrueTypeCollection(path, PdfEncodings.WINANSI);
                for (int i = 0; i < ttc.getTTCSize(); i++) {
                    register(path + "," + i);
                }
            } else if (path.toLowerCase().endsWith(".afm") || path.toLowerCase().endsWith(".pfm")) {
                FontProgram fontProgram = FontFactory.createFont(path, false);
                String fullName = fontProgram.getFontNames().getFullName()[0][3].toLowerCase();
                String familyName = fontProgram.getFontNames().getFamilyName()[0][3].toLowerCase();
                String psName =fontProgram.getFontNames().getFontName().toLowerCase();
                registerFamily(familyName, fullName, null);
                trueTypeFonts.put(psName, path);
                trueTypeFonts.put(fullName, path);
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Registered %s", path));
            }
        } catch (java.io.IOException e){
            throw new IOException(e);
        }
    }

    // remove regular and correct last symbol
    // do this job to give higher priority to regular fonts in comparison with light, narrow, etc
    // Don't use this method for not regular fonts!
    protected boolean saveCopyOfRegularFont(String regularFontName, String path) {
        //remove "regular" at the end of the font name
        String alias = regularFontName.substring(0, regularFontName.length() - 7).trim();
        if (!trueTypeFonts.containsKey(alias)) {
            trueTypeFonts.put(alias, path);
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
    public int registerDirectory(final String dir) {
        return registerDirectory(dir, false);
    }

    /**
     * Register all the fonts in a directory and possibly its subdirectories.
     *
     * @param dir                the directory
     * @param scanSubdirectories recursively scan subdirectories if <code>true</true>
     * @return the number of fonts registered
     */
    public int registerDirectory(final String dir, final boolean scanSubdirectories) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Registering directory %s, looking for fonts", dir));
        }
        int count = 0;
        try {
            String[] files = FileUtils.getDirectoryList(dir);
            if (files == null)
                return 0;
            for (String file : files) {
                try {
                    if (FileUtils.isDirectory(file)) {
                        if (scanSubdirectories) {
                            count += registerDirectory(file, true);
                        }
                    } else {
                        String suffix = file.length() < 4 ? null : file.substring(file.length() - 4).toLowerCase();
                        if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                            /* Only register Type 1 fonts with matching .pfb files */
                            String pfb = file.substring(0, file.length() - 4) + ".pfb";
                            if (FileUtils.fileExists(pfb)) {
                                register(file, null);
                                ++count;
                            }
                        } else if (".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix)) {
                            register(file, null);
                            ++count;
                        }
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
    public int registerSystemDirectories() {
        int count = 0;
        String[] withSubDirs = {
                FileUtils.getFontsDir(),
                "/usr/share/X11/fonts",
                "/usr/X/lib/X11/fonts",
                "/usr/openwin/lib/X11/fonts",
                "/usr/share/fonts",
                "/usr/X11R6/lib/X11/fonts"
        };
        for (String directory : withSubDirs) {
            count += registerDirectory(directory, true);
        }

        String[] withoutSubDirs = {
                "/Library/Fonts",
                "/System/Library/Fonts"
        };
        for (String directory : withoutSubDirs) {
            count += registerDirectory(directory, false);
        }

        return count;
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered fonts
     */

    public Set<String> getRegisteredFonts() {
        return trueTypeFonts.keySet();
    }

    /**
     * Gets a set of registered font names.
     *
     * @return a set of registered font families
     */

    public Set<String> getRegisteredFamilies() {
        return fontFamilies.keySet();
    }

    /**
     * Checks if a certain font is registered.
     * @param fontname the name of the font that has to be checked.
     * @return true if the font is found
     */
    public boolean isRegistered(final String fontname) {
        return trueTypeFonts.containsKey(fontname.toLowerCase());
    }
}
