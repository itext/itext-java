package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.otf.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueTypeFont extends FontProgram {

    private OpenTypeParser fontParser;

    private boolean isUnicode;
    private boolean isSymbol;

    protected int[][] bBoxes;

    protected HashMap<Integer, List<String[]>> allNames;

    //TODO doublicated with PdfType0Font.isVertical.
    protected boolean isVertical;

    private GlyphSubstitutionTableReader gsubTable;
    private OpenTypeGdefTableReader gdefTable;

    private boolean applyLigatures = false;
    private String otfScript = null;

    Map<Integer, Glyph> indexGlyphMap;
    Map<Integer, Glyph> codeGlyphMap;


    /**
     * The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative.
     */
    protected IntHashtable kerning = new IntHashtable();

    // TODO Duplicated with FontEncoding.baseEncoding.
    protected String baseEncoding;

    private byte[] fontStreamBytes;

    public TrueTypeFont(String path, String baseEncoding) throws IOException {
        fontParser = new OpenTypeParser(path);
        this.baseEncoding = baseEncoding;
        initializeFontProperties();
    }

    TrueTypeFont(String ttcPath, String baseEncoding, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttcPath, ttcIndex);
        this.baseEncoding = baseEncoding;
        initializeFontProperties();
    }

    TrueTypeFont(byte[] ttc, String baseEncoding, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttc, ttcIndex);
        this.baseEncoding = baseEncoding;
        initializeFontProperties();
    }

    public TrueTypeFont(byte[] ttf, String baseEncoding) throws IOException {
        fontParser = new OpenTypeParser(ttf);
        this.baseEncoding = baseEncoding;
        initializeFontProperties();
    }

    public TrueTypeFont(String encoding) {
        this.baseEncoding = encoding;
        if (this.baseEncoding.equals(PdfEncodings.IDENTITY_H) || this.baseEncoding.equals(PdfEncodings.IDENTITY_V)) {
            isUnicode = true;
            isVertical = this.baseEncoding.endsWith("V");
        } else {
            this.encoding = new FontEncoding(encoding, true);
        }
    }

    public boolean isApplyLigatures() {
        return applyLigatures;
    }

    public void setApplyLigatures(boolean applyLigatures) {
        this.applyLigatures = applyLigatures;
    }

    public void setScriptForOTF(Character.UnicodeScript script){
        switch (script) {
            case LATIN:
                otfScript = "latn";
                break;
            case ARABIC:
                otfScript = "arab";
                break;
            default:
                otfScript = null;
                break;
        }
    }

    public String getOtfScript() {
        return otfScript;
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     *
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        if (isUnicode) {
            char[] glyphs;
            Glyph glyph;
            int i = 0;
            if (isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                glyphs = new char[b.length];
                for (int k = 0; k < b.length; ++k) {
                    glyph = getMetrics(b[k] & 0xff);
                    if (glyph == null) {
                        continue;
                    }
                    glyphs[i++] = (char) glyph.index;
                }
            } else {
                glyphs = new char[text.length()];
                for (int k = 0; k < text.length(); ++k) {
                    int val;
                    if (Utilities.isSurrogatePair(text, k)) {
                        val = Utilities.convertToUtf32(text, k);
                        k++;
                    } else {
                        val = text.charAt(k);
                    }
                    glyph = getMetrics(val);
                    if (glyph == null) {
                        continue;
                    }
                    glyphs[i++] = (char) glyph.index;
                }
            }
            String s = new String(glyphs, 0, i);
            try {
                return s.getBytes("UnicodeBigUnmarked");
            } catch (UnsupportedEncodingException e) {
                throw new PdfException("TrueTypeFont", e);
            }
        } else {
            return encoding.convertToBytes(text);
        }
    }

    public boolean applyOtfScript(GlyphLine glyphLine) {
        if (otfScript == null) {
            return false;
        }
        if (gsubTable != null) {
            LanguageRecord languageRecord = null;
            for (ScriptRecord record: gsubTable.getScriptRecords()) {
                if (otfScript.equals(record.tag)) {
                    languageRecord = record.defaultLanguage;
                    break;
                }
            }
            if (languageRecord == null) {
                return false;
            }
            boolean transformed = false;
            List<OpenTableLookup> init = null;
            List<OpenTableLookup> medi = null;
            List<OpenTableLookup> fina = null;
            for (int featureIndex: languageRecord.features) {
                FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                switch (feature.tag) {
                    case "init":
                        init = gsubTable.getLookups(new FeatureRecord[]{feature});
                        break;
                    case "medi":
                        medi = gsubTable.getLookups(new FeatureRecord[]{feature});
                        break;
                    case "fina":
                        fina = gsubTable.getLookups(new FeatureRecord[]{feature});
                        break;
                }
            }
            if (init == null || medi == null || fina == null) {
                return false;
            }
            List<Integer> words = splitArabicGlyphLineIntoWords(glyphLine, medi, fina);
            for (OpenTableLookup lookup : init) {
                if (lookup != null) {
                    for (int i = 0; i < words.size(); i += 2) {
                        if (words.get(i) + 1 != words.get(i + 1)) {
                            glyphLine.idx = words.get(i);
                            if (lookup.transformOne(glyphLine)) {
                                transformed = true;
                            }
                        }
                    }
                }
            }
            for (OpenTableLookup lookup : medi) {
                if (lookup != null) {
                    for (int i = 0; i < words.size(); i += 2) {
                        for (int k = words.get(i) + 1; k < words.get(i + 1) - 1; k++) {
                            glyphLine.idx = k;
                            if (lookup.transformOne(glyphLine)) {
                                transformed = true;
                            }
                        }
                    }
                }
            }
            for (OpenTableLookup lookup : fina) {
                if (lookup != null) {
                    for (int i = 0; i < words.size(); i += 2) {
                        if (words.get(i) + 1 != words.get(i + 1)) {
                            glyphLine.idx = words.get(i + 1) - 1;
                            if (lookup.transformOne(glyphLine)) {
                                transformed = true;
                            }
                        }
                    }
                }
            }
            glyphLine.idx = 0;
            return transformed;
        }
        return false;
    }

    public boolean applyLigaFeature(GlyphLine glyphLine) {
        if (gsubTable != null) {
            List<FeatureRecord> features = gsubTable.getFeatureRecords();
            FeatureRecord liga = null;
            for (FeatureRecord featureRecord : features) {
                if (featureRecord.tag.equals("liga")) {
                    liga = featureRecord;
                    break;
                }
            }
            if (liga != null) {
                boolean transformed = false;
                if (glyphLine != null) {
                    List<OpenTableLookup> lookups = gsubTable.getLookups(new FeatureRecord[]{liga});
                    for (OpenTableLookup lookup : lookups) {
                        if (lookup != null && lookup.transformLine(glyphLine)) {
                            transformed = true;
                        }
                        glyphLine.idx = 0;
                    }
                }
                return transformed;
            }
        }
        return false;
    }

    public GlyphLine createGlyphLine(char[] glyphs, Integer length) {
        GlyphLine glyphLine = new GlyphLine();
        glyphLine.glyphs = new ArrayList<>(length);
        glyphLine.start = 0;
        glyphLine.end = length;
        glyphLine.idx = 0;
        for (int k = 0; k < length; k++) {
            //glyphCode, glyphWidth, String.valueOf(c), false
            int index = glyphs[k];
            Integer ch = gsubTable.getGlyphToCharacter(index);
            Glyph glyph = getGlyph(index);
            glyph.chars = String.valueOf((char) (int) ch);
            glyphLine.glyphs.add(glyph);
        }
        return glyphLine;
    }

    /**
     * Get glyph's width.
     *
     * @param code char code.
     * @return Gets width in normalized 1000 units.
     */
    @Override
    public int getWidth(int code) {
        if (isUnicode) {
            if (isVertical) {
                return FontProgram.DEFAULT_WIDTH;
// TODO I guess, this code is redundant, because font parser save both code code & 0xff values,
// TODO while parsing cmap and in case symbol cmap
//            } else if (isFontSpecific()) {
//                if ((code & 0xff00) == 0 || (code & 0xff00) == 0xf000) {
//                    return getRawWidth(code & 0xff, null);
//                } else {
//                    return 0;
//                }
            } else {
                return getRawWidth(code, null);
            }
        } else {
            return widths[code];
        }
    }

    public Glyph getGlyph(int index) {
        return indexGlyphMap.get(index);
    }

    /**
     * Get glyph's bbox.
     *
     * @param code char code, depends from implementation.
     * @return Gets bbox in normalized 1000 units.
     */
    @Override
    public int[] getCharBBox(int code) {
        if (isUnicode) {
            if (isVertical) {
                return null;
// TODO I guess, this code is redundant, because font parser save both code code & 0xff values,
// TODO while parsing cmapand in case symbol cmap
//            } else if (isFontSpecific()) {
//                if ((code & 0xff00) == 0 || (code & 0xff00) == 0xf000) {
//                    return getRawCharBBox(code & 0xff, null);
//                } else {
//                    return null;
//                }
            } else {
                return getRawCharBBox(code, null);
            }
        } else {
            return charBBoxes[code];
        }
    }


    public Integer getUnicodeChar(int index) {
        return indexGlyphMap.get(index).unicode;
    }

    @Override
    public boolean hasKernPairs() {
        return kerning.size() > 0;
    }

    /**
     * Gets the kerning between two Unicode chars.
     *
     * @param char1 the first char
     * @param char2 the second char
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(int char1, int char2) {
        Glyph glyph = getMetrics(char1);
        if (glyph == null)
            return 0;
        int c1 = glyph.index;
        glyph = getMetrics(char2);
        if (glyph == null)
            return 0;
        int c2 = glyph.index;
        return kerning.get((c1 << 16) + c2);
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param glyph1 the first glyph
     * @param glyph1 the second glyph
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        if (glyph1 == null || glyph2 == null) {
            return 0;
        }
        return kerning.get((glyph1.index << 16) + glyph2.index);
    }

    /**
     * Gets the glyph index and metrics for a character.
     *
     * @param ch the character code
     * @return an {@code int} array with {glyph index, width}
     */
    public Glyph getMetrics(int ch) {
// TODO I guess, this code is redundant, because font parser save both code code & 0xff values,
// TODO while parsing cmapand in case symbol cmap
// if fontSpecific is true with cmap(3,0)
//        if (isFontSpecific()) {
//            if ((ch & 0xffffff00) == 0 || (ch & 0xffffff00) == 0xf000) {
//                return codeGlyphMap.get(ch & 0xff);
//            } else {
//                return null;
//            }
//        }
        Glyph result = codeGlyphMap.get(ch);
        if (result == null) {
            result = new Glyph(indexGlyphMap.get(0), ch);
        }
        return result;
    }

    public boolean isCff() {
        return fontParser.isCff();
    }

    public HashMap<Integer, List<String[]>> getAllNames() {
        return allNames;
    }

    public HashMap<Integer, int[]> getActiveCmap() {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        if (cmaps.cmapExt != null) {
            return cmaps.cmapExt;
        } else if (!cmaps.fontSpecific && cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else if (cmaps.fontSpecific && cmaps.cmap10 != null) {
            return cmaps.cmap10;
        } else if (cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else {
            return cmaps.cmap10;
        }
    }

    public byte[] getFontStreamBytes() {
        if (fontStreamBytes != null)
            return fontStreamBytes;
        try {
            if (fontParser.isCff()) {
                fontStreamBytes = fontParser.readCffFont();
            } else {
                fontStreamBytes = fontParser.getFullFont();
            }
        } catch (IOException e) {
            fontStreamBytes = null;
            throw new PdfException(PdfException.IoException, e);
        }
        return fontStreamBytes;
    }

    @Override
    public int getPdfFontFlags() {
        int flags = 0;
        if (fontMetrics.isFixedPitch()) {
            flags |= 1;
        }
        flags |= isFontSpecific() ? 4 : 32;
        if (fontNames.isItalic()) {
            flags |= 64;
        }
        if (fontNames.isBold() || fontNames.getFontWeight() > 500) {
            flags |= 262144;
        }
        return flags;
    }

    public boolean isFontSpecific() {
        return isSymbol;
    }

    /**
     * Gets table of characters widths for this simple font encoding.
     */
    public int[] getRawWidths() {
        return widths;
    }

    /**
     * The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font.
     */
    public int getDirectoryOffset() {
        return fontParser.directoryOffset;
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) {
        try {
            return fontParser.getSubset(glyphs, subset);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
    }

    /**
     * Gets the width from the font according to the unicode char {@code c}.
     * If the {@code name} is null it's a symbolic font.
     *
     * @param c    the unicode char
     * @param name not used in {@code TrueTypeFont}.
     * @return the width of the char
     */
    @Override
    protected int getRawWidth(int c, String name) {
        Glyph glyph = getMetrics(c);
        if (glyph == null) {
            return 0;
        }
        return glyph.width;
    }

    @Override
    protected int[] getRawCharBBox(int ch, String name) {
        Glyph glyph = codeGlyphMap.get(ch);
        if (glyph == null || bBoxes == null) {
            return null;
        }
        return bBoxes[glyph.index];
    }

    /**
     * Extracts the names of the font in all the languages available.
     *
     * @param id the name id to retrieve
     * @return not empty {@code String[][]} if any names exists, otherwise {@code null}.
     */
    protected String[][] getNames(int id) throws IOException {
        List<String[]> names = allNames.get(id);
        return names != null && names.size() > 0 ? listToArray(names) : null;
    }

    protected void readGdefTable() throws IOException {
        int[] gdef = fontParser.tables.get("GDEF");
        if (gdef != null) {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, gdef[0]);
        } else {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, 0);
        }
    }

    protected void readGsubTable() throws IOException {
        int[] gsub = fontParser.tables.get("GSUB");
        if (gsub != null) {
            gsubTable = new GlyphSubstitutionTableReader(fontParser.raf, gsub[0], gdefTable, indexGlyphMap);
        }
    }

    private void initializeFontProperties() throws IOException {

        // initialize sfnt tables
        OpenTypeParser.HeaderTable head = fontParser.getHeadTable();
        OpenTypeParser.HorizontalHeader hhea = fontParser.getHheaTable();
        OpenTypeParser.WindowsMetrics os_2 = fontParser.getOs_2Table();
        OpenTypeParser.PostTable post = fontParser.getPostTable();
        isSymbol = fontParser.getCmapTable().fontSpecific;
        kerning = fontParser.readKerning(head.unitsPerEm);
        bBoxes = fontParser.readBbox(head.unitsPerEm);
        allNames = fontParser.getAllNameEntries();

        // font names group
        fontNames.setFontName(fontParser.getPsFontName());
        fontNames.setFullName(getNames(4));
        String[][] otfFamilyName = getNames(16);
        if (otfFamilyName != null) {
            fontNames.setFamilyName(otfFamilyName);
        } else {
            fontNames.setFamilyName(getNames(1));
        }
        String[][] subfamily = getNames(2);
        if (subfamily != null) {
            fontNames.setStyle(subfamily[0][3]);
        }
        String[][] otfSubFamily = getNames(17);
        if (otfFamilyName != null) {
            fontNames.setSubfamily(otfSubFamily);
        } else {
            fontNames.setSubfamily(subfamily);
        }
        String[][] cidName = getNames(20);
        if (cidName != null) {
            fontNames.setCidFontName(cidName[0][3]);
        }
        fontNames.setWeight(os_2.usWeightClass);
        fontNames.setWidth(os_2.usWidthClass);
        fontNames.setMacStyle(head.macStyle);
        fontNames.setAllowEmbedding(os_2.fsType != 2);

        // font metrics group
        fontMetrics.setUnitsPerEm(head.unitsPerEm);
        fontMetrics.updateBbox(head.xMin, head.yMin, head.xMax, head.yMax);
        fontMetrics.setMaxGlyphId(fontParser.readMaxGlyphId());
        fontMetrics.setGlyphWidths(fontParser.getGlyphWidthsByIndex());
        fontMetrics.setTypoAscender(os_2.sTypoAscender);
        fontMetrics.setTypoDescender(os_2.sTypoDescender);
        fontMetrics.setCapHeight(os_2.sCapHeight);
        fontMetrics.setXHeight(os_2.sxHeight);
        fontMetrics.setItalicAngle(post.italicAngle);
        fontMetrics.setAscender(hhea.Ascender);
        fontMetrics.setDescender(hhea.Descender);
        fontMetrics.setLineGap(hhea.LineGap);
        fontMetrics.setWinAscender(os_2.usWinAscent);
        fontMetrics.setWinDescender(os_2.usWinDescent);
        fontMetrics.setAdvanceWidthMax(hhea.advanceWidthMax);
        fontMetrics.setUnderlinePosition((post.underlinePosition - post.underlineThickness) / 2);
        fontMetrics.setUnderlineThickness(post.underlineThickness);
        fontMetrics.setStrikeoutPosition(os_2.yStrikeoutPosition);
        fontMetrics.setStrikeoutSize(os_2.yStrikeoutSize);
        fontMetrics.setSubscriptOffset(-os_2.ySubscriptYOffset);
        fontMetrics.setSubscriptSize(os_2.ySubscriptYSize);
        fontMetrics.setSuperscriptOffset(os_2.ySuperscriptYOffset);
        fontMetrics.setSuperscriptSize(os_2.ySuperscriptYSize);
        fontMetrics.setIsFixedPitch(post.isFixedPitch);

        // font identification group
        String[][] ttfVersion = getNames(5);
        if (ttfVersion != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        String[][] ttfUniqueId = getNames(3);
        if (ttfUniqueId != null) {
            fontIdentification.setTtfVersion(ttfUniqueId[0][3]);
        }
        fontIdentification.setPanose(os_2.panose);

        HashMap<Integer, int[]> cmap = getActiveCmap();
        int[] glyphWidths = fontParser.getGlyphWidthsByIndex();
        codeGlyphMap = new HashMap<>(cmap.size());
        indexGlyphMap = new HashMap<>(glyphWidths.length);
        for (Integer charCode : cmap.keySet()) {
            int index = cmap.get(charCode)[0];
            if (index >= glyphWidths.length) {
                Logger LOGGER = LoggerFactory.getLogger(TrueTypeFont.class);
                LOGGER.warn(String.format(String.format("Font %s has invalid glyph: %%d", getFontNames().getFontName()), index));
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], charCode);
            codeGlyphMap.put(charCode, glyph);
            indexGlyphMap.put(index, glyph);
        }

        for (int index = 0; index < glyphWidths.length; index++) {
            if (indexGlyphMap.containsKey(index)) {
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], null);
            indexGlyphMap.put(index, glyph);
        }

        readGdefTable();
        readGsubTable();

        if (this.baseEncoding.equals(PdfEncodings.IDENTITY_H) || this.baseEncoding.equals(PdfEncodings.IDENTITY_V)) {
            isUnicode = true;
            isVertical = this.baseEncoding.endsWith("V");
        } else {
            isUnicode = false;
            encoding = new FontEncoding(this.baseEncoding, isSymbol);
            if (encoding.hasSpecialEncoding()) {
                createSpecialEncoding();
            } else {
                createEncoding();
            }
        }
    }

    private String[][] listToArray(List<String[]> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private List<Integer> splitArabicGlyphLineIntoWords(GlyphLine glyphLine, List<OpenTableLookup> medi, List<OpenTableLookup> fina) {
        List<Integer> words = new ArrayList<>(glyphLine.glyphs.size());
        boolean started = false;
        for (int i = 0; i < glyphLine.glyphs.size(); i++) {
            if (!hasSubstitution(medi, glyphLine.glyphs.get(i).index) || isSameSubstitution(medi, fina, glyphLine.glyphs.get(i))) {
                if (started) {
                    // if the glyph has no fina form, it is not an arabic glyph
                    boolean hasFinaForm = hasSubstitution(fina, glyphLine.glyphs.get(i).index);
                    words.add(hasFinaForm ? i + 1 : i);
                    started = false;
                }
            } else {
                if (!started) {
                    words.add(i);
                    started = true;
                }
            }
        }
        if (words.size() % 2 != 0) {
            words.add(glyphLine.glyphs.size());
        }
        return words;
    }

    private boolean isSameSubstitution(List<OpenTableLookup> feature1, List<OpenTableLookup> feature2, Glyph glyph) {
        Glyph t1 = transform(feature1, glyph);
        Glyph t2 = transform(feature2, glyph);
        return t1 == t2 || t1 != null && t1.equals(t2);
    }

    private boolean hasSubstitution(List<OpenTableLookup> feature, int index) {
        for (OpenTableLookup lookup : feature) {
            if (lookup != null) {
                if (((GsubLookupFormat1)lookup).hasSubstitution(index)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Glyph transform(List<OpenTableLookup> feature, Glyph glyph) {
        for (OpenTableLookup lookup : feature) {
            if (lookup != null) {
                if (((GsubLookupFormat1)lookup).hasSubstitution(glyph.index)) {
                    GlyphLine gl = new GlyphLine();
                    gl.start = 0;
                    gl.end = 1;
                    gl.idx = 0;
                    gl.glyphs = Arrays.asList(glyph);
                    lookup.transformOne(gl);
                    return gl.glyphs.get(0);
                }
            }
        }
        return null;
    }
}
