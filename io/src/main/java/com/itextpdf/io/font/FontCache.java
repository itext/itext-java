package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.io.font.cmap.AbstractCMap;
import com.itextpdf.io.font.cmap.CMapByteCid;
import com.itextpdf.io.font.cmap.CMapCidByte;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapLocationResource;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapUniCid;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class FontCache {

    /**
     * The path to the font resources.
     */
    public static final String CMAP_RESOURCE_PATH = FontConstants.RESOURCE_PATH + "cmap/";

    private static final Map<String, Map<String, Object>> allFonts = new HashMap<>();
    private static final Map<String, Set<String>> registryNames = new HashMap<>();

    private static final String CJK_REGISTRY_FILENAME = "cjk_registry.properties";
    private static final String FONTS_PROP = "fonts";
    private static final String REGISTRY_PROP = "Registry";
    private static final String W_PROP = "W";
    private static final String W2_PROP = "W2";

    private static Map<String, FontProgram> fontCache = new ConcurrentHashMap<>();

    static {
        try {
            loadRegistry();

            for (String font : registryNames.get(FONTS_PROP)) {
                allFonts.put(font, readFontProperties(font));
            }
        } catch (Exception ignored) {
            // TODO: add logger (?)
        }
    }

    /**
     * Checks if the font with the given name and encoding is one
     * of the predefined CID fonts.
     * @param fontName the font name.
     * @return {@code true} if it is CJKFont.
     */
    protected static boolean isPredefinedCidFont(String fontName) {
        if (!registryNames.containsKey(FONTS_PROP)) {
            return false;
        } else if (!registryNames.get(FONTS_PROP).contains(fontName)) {
            return false;
        }
        return true;
    }

    public static String getCompatibleCidFont(String cmap) {
        for (Map.Entry<String, Set<String>> e : registryNames.entrySet()) {
            if (e.getValue().contains(cmap)) {
                String registry = e.getKey();
                for (Map.Entry<String, Map<String, Object>> e1 : allFonts.entrySet()) {
                    if (registry.equals(e1.getValue().get(REGISTRY_PROP)))
                        return e1.getKey();
                }
            }
        }
        return null;
    }

    public static Set<String> getCompatibleCmaps(String fontName) {
        String registry = (String) FontCache.getAllFonts().get(fontName).get(REGISTRY_PROP);
        return registryNames.get(registry);
    }

    public static Map<String, Map<String, Object>> getAllFonts() {
        return allFonts;
    }

    public static Map<String, Set<String>> getRegistryNames() {
        return registryNames;
    }

    public static CMapCidUni getCid2UniCmap(String uniMap) {
        CMapCidUni cidUni = new CMapCidUni();
        return parseCmap(uniMap, cidUni);
    }

    public static CMapUniCid getUni2CidCmap(String uniMap) {
        CMapUniCid uniCid = new CMapUniCid();
        return parseCmap(uniMap, uniCid);
    }

    public static CMapByteCid getByte2CidCmap(String cmap) {
        CMapByteCid uniCid = new CMapByteCid();
        return parseCmap(cmap, uniCid);
    }

    public static CMapCidByte getCid2Byte(String cmap) {
        CMapCidByte cidByte = new CMapCidByte();
        return parseCmap(cmap, cidByte);
    }

    public static FontProgram getFont(String fontName) {
        String key = getFontCacheKey(fontName);
        return fontCache.get(key);
    }

    public static FontProgram saveFont(FontProgram font, String fontName) {
        // for most of the fonts we can retrieve encoding from FontProgram, but
        // for Cid there is no such possibility, since it is used in conjunction
        // with cmap to produce Type0 font, so I added fontName and encoding parameters
        // just for convenience.
        // TODO: probably it's better to declare saveFont(FontProgram) and saveFont(CidFont, encoding or cmap) in the future
        FontProgram fontFound = getFont(fontName);
        if (fontFound != null) {
            //TODO add close method
            //fontBuilt.close();
            return fontFound;
        }
        String key = getFontCacheKey(fontName);
        fontCache.put(key, font);
        return font;
    }

    private static void loadRegistry() throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(CMAP_RESOURCE_PATH + CJK_REGISTRY_FILENAME);
        try {
            Properties p = new Properties();
            p.load(resource);

            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                String value = (String) entry.getValue();
                String[] splitValue = value.split(" ");
                Set<String> set = new HashSet<>();

                for (String s : splitValue) {
                    if (!s.isEmpty()) {
                        set.add(s);
                    }
                }

                registryNames.put((String) entry.getKey(), set);
            }
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    private static Map<String, Object> readFontProperties(String name) throws java.io.IOException {
        InputStream resource = ResourceUtil.getResourceStream(CMAP_RESOURCE_PATH + name + ".properties");

        try {
            Properties p = new Properties();
            p.load(resource);

            Map<String, Object> fontProperties = new HashMap<>((Map) p);
            fontProperties.put(W_PROP, createMetric((String) fontProperties.get(W_PROP)));
            fontProperties.put(W2_PROP, createMetric((String) fontProperties.get(W2_PROP)));

            return fontProperties;
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }

    private static IntHashtable createMetric(String s) {
        IntHashtable h = new IntHashtable();
        StringTokenizer tk = new StringTokenizer(s);

        while (tk.hasMoreTokens()) {
            int n1 = Integer.parseInt(tk.nextToken());
            h.put(n1, Integer.parseInt(tk.nextToken()));
        }

        return h;
    }

    private static <T extends AbstractCMap> T parseCmap(String name, T cmap) {
        try {
            CMapParser.parseCid(name, cmap, new CMapLocationResource());
        } catch (java.io.IOException e) {
            throw new IOException(IOException.IoException, e);
        }
        return cmap;
    }

    private static String getFontCacheKey(String fontName) {
        return fontName;
    }
}
