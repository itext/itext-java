package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.FileUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reusable font set for FontProgram related data.
 *
 * @see FontProvider
 */
public class FontSet {
    //"fontName+encoding" or "hash(fontProgram)+encoding" as key
    private static Map<String, FontProgramInfo> fontInfoCache = new ConcurrentHashMap<>();
    private Set<FontProgramInfo> fonts = new HashSet<>();
    private Map<FontProgramInfo, FontProgram> fontPrograms = new HashMap<>();
    private Map<FontSelectorKey, FontSelector> fontSelectorCache = new HashMap<>();

    public int addDirectory(String dir, boolean scanSubdirectories) {
        int count = 0;
        String[] files = FileUtil.listFilesInDirectory(dir, scanSubdirectories);
        if (files == null)
            return 0;
        for (String file : files) {
            try {
                String suffix = file.length() < 4 ? null : file.substring(file.length() - 4).toLowerCase();
                if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                    // Add only Type 1 fonts with matching .pfb files.
                    String pfb = file.substring(0, file.length() - 4) + ".pfb";
                    if (FileUtil.fileExists(pfb)) {
                        addFont(file, null);
                        count++;
                    }
                } else if (".ttf".equals(suffix) || ".otf".equals(suffix) || ".ttc".equals(suffix)) {
                    addFont(file, null);
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    public int addDirectory(String dir) {
        return addDirectory(dir, false);
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @return false, if fontProgram is null, otherwise true.
     */
    public boolean addFont(FontProgram fontProgram, String encoding) {
        if (fontProgram == null) {
            return false;
        }

        FontProgramInfo fontInfo = FontProgramInfo.create(fontProgram, encoding);
        addFontInfo(fontInfo);
        fontPrograms.put(fontInfo, fontProgram);
        return true;
    }

    public boolean addFont(String fontProgram, String encoding) {
        return addFont(fontProgram, null, encoding);
    }

    public boolean addFont(byte[] fontProgram, String encoding) {
        return addFont(null, fontProgram, encoding);
    }

    public void addFont(String fontProgram) {
        addFont(fontProgram, null);
    }

    public void addFont(FontProgram fontProgram) {
        addFont(fontProgram, null);
    }

    public void addFont(byte[] fontProgram) {
        addFont(fontProgram, null);
    }

    public Set<FontProgramInfo> getFonts() {
        return fonts;
    }

    protected boolean addFont(String fontName, byte[] fontProgram, String encoding) {
        if (fontName == null && fontProgram == null) {
            return false;
        }
        String fontInfoKey = calculateFontProgramInfoKey(fontName, fontProgram, encoding);
        FontProgramInfo fontInfo;
        if (fontInfoCache.containsKey(fontInfoKey)) {
            fontInfo = fontInfoCache.get(fontInfoKey);
        } else {
            fontInfo = FontProgramInfo.create(fontName, fontProgram, encoding);
            if (fontInfo != null) {
                fontInfoCache.put(fontInfoKey, fontInfo);
            } else {
                return false;
            }
        }
        addFontInfo(fontInfo);
        return true;
    }

    Map<FontProgramInfo, FontProgram> getFontPrograms() {
        return fontPrograms;
    }

    Map<FontSelectorKey, FontSelector> getFontSelectorCache() {
        return fontSelectorCache;
    }

    private String calculateFontProgramInfoKey(String fontName, byte[] fontProgram, String encoding) {
        String key;
        if (fontName != null) {
            key = fontName;
        } else {
            key = Integer.toHexString(ArrayUtil.hashCode(fontProgram));
        }
        return key + encoding;
    }

    private void addFontInfo(FontProgramInfo fontInfo) {
        fonts.add(fontInfo);
        fontSelectorCache.clear();
    }
}
