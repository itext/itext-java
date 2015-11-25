package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;

import java.io.IOException;
import java.text.MessageFormat;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.itextpdf.basics.font.otf.FeatureRecord;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.basics.font.otf.GlyphSubstitutionTableReader;
import com.itextpdf.basics.font.otf.LanguageRecord;
import com.itextpdf.basics.font.otf.OpenTableLookup;
import com.itextpdf.basics.font.otf.OpenTypeGdefTableReader;
import com.itextpdf.basics.font.otf.ScriptRecord;
=======
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

>>>>>>> Initial Devanagari syllables clusterization. Shaping implementation prototyping
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueTypeFont extends FontProgram {

    private OpenTypeParser fontParser;

    private boolean isSymbol;

    protected int[][] bBoxes;

    protected boolean isVertical;

    private GlyphSubstitutionTableReader gsubTable;
    private OpenTypeGdefTableReader gdefTable;

    private boolean applyLigatures = false;
    private Character.UnicodeScript script = null;
    private String otfScriptTag = null;

    /**
     * The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative.
     */
    protected IntHashtable kerning = new IntHashtable();

    private byte[] fontStreamBytes;

    public TrueTypeFont(String path) throws IOException {
        fontParser = new OpenTypeParser(path);
        initializeFontProperties();
    }

    TrueTypeFont(String ttcPath, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttcPath, ttcIndex);
        initializeFontProperties();
    }

    TrueTypeFont(byte[] ttc, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttc, ttcIndex);
        initializeFontProperties();
    }

    public TrueTypeFont(byte[] ttf) throws IOException {
        fontParser = new OpenTypeParser(ttf);
        initializeFontProperties();
    }

    public boolean isApplyLigatures() {
        return applyLigatures;
    }

    public void setApplyLigatures(boolean applyLigatures) {
        this.applyLigatures = applyLigatures;
    }

    public void setScriptForOTF(Character.UnicodeScript script) {
        this.script = script;
        switch (script) {
            case LATIN:
                otfScriptTag = "latn";
                break;
            case ARABIC:
                otfScriptTag = "arab";
                break;
            case DEVANAGARI:
                otfScriptTag = "dev2"; // TODO there is also old-style "deva" implementation
                break;
            default:
                otfScriptTag = null;
                break;
        }
    }

    public String getOtfScript() {
        return otfScriptTag;
    }

    public boolean applyOtfScript(GlyphLine glyphLine) {
        if (otfScriptTag == null) {
            return false;
        }
        if (gsubTable != null) {
            LanguageRecord languageRecord = getLanguageRecord();
            if (languageRecord == null) {
                return false;
            }
            boolean transformed = false;

            if (Character.UnicodeScript.ARABIC.equals(script)) {
                List<OpenTableLookup> init = null;
                List<OpenTableLookup> medi = null;
                List<OpenTableLookup> fina = null;
                List<OpenTableLookup> rlig = null;
                for (int featureIndex : languageRecord.features) {
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
                        case "rlig":
                            rlig = gsubTable.getLookups(new FeatureRecord[]{feature});
                            break;
                    }
                }

                if (applyInitMediFinaShaping(glyphLine, init, medi, fina)) {
                    transformed = true;
                }
                if (applyRligFeature(glyphLine, rlig)) {
                    transformed = true;
                }
            } else if (Character.UnicodeScript.DEVANAGARI.equals(script)) {
                List<DevanagariCluster> clusters = splitDevanagariGlyphLineIntoClusters(glyphLine);

                if (clusters != null && clusters.size() > 0) {
                    for (DevanagariCluster cluster : clusters) {
                        // TODO reordering

                        Map<String, List<OpenTableLookup>> features = new LinkedHashMap<>();
                        for (int featureIndex : languageRecord.features) {
                            FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                            List<OpenTableLookup> lookups = gsubTable.getLookups(new FeatureRecord[]{feature});
                            features.put(feature.tag, lookups);
                        }

                        int start = glyphLine.start;
                        int end = glyphLine.end;

                        // TODO do it cluster by cluster. end might be shifted as a result of applying a feature to a cluster, so be careful.

                        // Localized forms
                        if (transform(features.get("locl"), glyphLine)) {
                            transformed = true;
                        }

                        // Basic Shaping forms
                        String[] basicShapingForms = new String[] {"nukt", "akhn", "rphf", "rkrf", "blwf", "half", "vatu", "cjct"};
                        for (String feature : basicShapingForms) {
                            if (transform(features.get(feature), glyphLine)) {
                                transformed = true;
                            }
                        }

                        // TODO final reordering

                        String[] presentationForms = new String[] {"pres", "abvs", "blws", "psts", "haln", "calt"};
                        for (String feature : presentationForms) {
                            if (transform(features.get(feature), glyphLine)) {
                                transformed = true;
                            }
                        }
                    }
                }
            }

            return transformed;
        }
        return false;
    }

    public boolean applyLigaFeature(GlyphLine glyphLine, boolean scriptSpecific) {
        if (gsubTable != null) {
            List<FeatureRecord> ligaFeatures = new ArrayList<>();

            if (scriptSpecific) {
                LanguageRecord languageRecord = getLanguageRecord();
                if (languageRecord == null) {
                    return false;
                }
                for (int featureIndex : languageRecord.features) {
                    FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                    if (feature.tag.equals("liga")) {
                        ligaFeatures.add(feature);
                    }
                }
            } else {
                for (FeatureRecord featureRecord : gsubTable.getFeatureRecords()) {
                    if (featureRecord.tag.equals("liga")) {
                        ligaFeatures.add(featureRecord);
                    }
                }
            }

            if (ligaFeatures.size() > 0) {
                boolean transformed = false;
                if (glyphLine != null) {
                    List<OpenTableLookup> lookups = gsubTable.getLookups(ligaFeatures.toArray(new FeatureRecord[ligaFeatures.size()]));
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

    @Override
    public GlyphLine createGlyphLine(String text) {
        ArrayList<Glyph> glyphs = new ArrayList<>(text.length());
        if (isFontSpecific()) {
            byte[] bytes = PdfEncodings.convertToBytes(text, "symboltt");
            for (byte b : bytes) {
                glyphs.add(getMetrics(b & 0xff));
            }
        } else {
            for (int k = 0; k < text.length(); ++k) {
                int val;
                if (Utilities.isSurrogatePair(text, k)) {
                    val = Utilities.convertToUtf32(text, k);
                    k++;
                } else {
                    val = text.charAt(k);
                }
                glyphs.add(getMetrics(val));
            }
        }
        return new GlyphLine(glyphs);
    }

    public GlyphLine createGlyphLine(char[] glyphs, Integer length) {
        ArrayList<Glyph> glyphLine = new ArrayList<>(length);
        for (int k = 0; k < length; k++) {
            int index = glyphs[k];
            Glyph glyph = getGlyphByCode(index);
            if (glyph == null) {
                glyph = getGlyphByCode(0);
            }
            glyphLine.add(glyph);
        }
        return new GlyphLine(glyphLine);
    }

    @Override
    public boolean hasKernPairs() {
        return kerning.size() > 0;
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first the first glyph
     * @param second the second glyph
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(Glyph first, Glyph second) {
        if (first == null || second == null) {
            return 0;
        }
        return kerning.get((first.index << 16) + second.index);
    }

    /**
     * Gets the glyph index and metrics for a character.
     *
     * @param charCode the character code
     * @return an {@code int} array with {glyph index, width}
     */
    //TODO remove this method. Generation of notdef glyphs should be done by PdfFont
    public Glyph getMetrics(int charCode) {
        Glyph result = unicodeToGlyph.get(charCode);
        // special use case for not found glyphs
        // in this case we should use .notdef glyph, but correct unicode value.
        if (result == null) {
            result = new Glyph(codeToGlyph.get(0), charCode);
            unicodeToGlyph.put(charCode, result);
        }
        return result;
    }

    public boolean isCff() {
        return fontParser.isCff();
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
            gsubTable = new GlyphSubstitutionTableReader(fontParser.raf, gsub[0], gdefTable, codeToGlyph);
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

        // font names group
        fontNames.setAllNames(fontParser.getAllNameEntries());
        fontNames.setFontName(fontParser.getPsFontName());
        fontNames.setFullName(fontNames.getNames(4));
        String[][] otfFamilyName = fontNames.getNames(16);
        if (otfFamilyName != null) {
            fontNames.setFamilyName(otfFamilyName);
        } else {
            fontNames.setFamilyName(fontNames.getNames(1));
        }
        String[][] subfamily = fontNames.getNames(2);
        if (subfamily != null) {
            fontNames.setStyle(subfamily[0][3]);
        }
        String[][] otfSubFamily = fontNames.getNames(17);
        if (otfFamilyName != null) {
            fontNames.setSubfamily(otfSubFamily);
        } else {
            fontNames.setSubfamily(subfamily);
        }
        String[][] cidName = fontNames.getNames(20);
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
        String[][] ttfVersion = fontNames.getNames(5);
        if (ttfVersion != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        String[][] ttfUniqueId = fontNames.getNames(3);
        if (ttfUniqueId != null) {
            fontIdentification.setTtfVersion(ttfUniqueId[0][3]);
        }
        fontIdentification.setPanose(os_2.panose);

        HashMap<Integer, int[]> cmap = getActiveCmap();
        int[] glyphWidths = fontParser.getGlyphWidthsByIndex();
        unicodeToGlyph = new LinkedHashMap<>(cmap.size());
        codeToGlyph = new LinkedHashMap<>(glyphWidths.length);
        for (Integer charCode : cmap.keySet()) {
            int index = cmap.get(charCode)[0];
            if (index >= glyphWidths.length) {
                Logger LOGGER = LoggerFactory.getLogger(TrueTypeFont.class);
                LOGGER.warn(MessageFormat.format(LogMessageConstant.FONT_HAS_INVALID_GLYPH, getFontNames().getFontName(), index));
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], charCode, bBoxes != null ? bBoxes[index] : null);
            unicodeToGlyph.put(charCode, glyph);
            codeToGlyph.put(index, glyph);
        }

        for (int index = 0; index < glyphWidths.length; index++) {
            if (codeToGlyph.containsKey(index)) {
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], null);
            codeToGlyph.put(index, glyph);
        }

        readGdefTable();
        readGsubTable();

        isVertical = false;
    }

    private List<Integer> splitArabicGlyphLineIntoWords(GlyphLine glyphLine, List<OpenTableLookup> medi, List<OpenTableLookup> fina) {
        List<Integer> words = new ArrayList<>(glyphLine.glyphs.size());
        boolean started = false;
        for (int i = 0; i < glyphLine.glyphs.size(); i++) {
            Glyph medialForm = transform(medi, glyphLine.glyphs.get(i));
            Glyph finalForm = transform(fina, glyphLine.glyphs.get(i));
            if (medialForm == null || (finalForm != null && medialForm.index == finalForm.index)) {
                if (started) {
                    // if the glyph has no fina form, it is not an arabic glyph
                    words.add(finalForm != null ? i + 1 : i);
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

    private boolean applyInitMediFinaShaping(GlyphLine glyphLine, List<OpenTableLookup> init, List<OpenTableLookup> medi, List<OpenTableLookup> fina) {
        if (init == null || medi == null || fina == null) {
            return false;
        }
        boolean transformed = false;
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
        return transformed;
    }

    private boolean applyRligFeature(GlyphLine glyphLine, List<OpenTableLookup> rlig) {
        boolean transformed = false;
        if (glyphLine != null && rlig != null) {
            for (OpenTableLookup lookup : rlig) {
                if (lookup != null && lookup.transformLine(glyphLine)) {
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    private Glyph transform(List<OpenTableLookup> feature, Glyph glyph) {
        if (feature != null) {
            for (OpenTableLookup lookup : feature) {
                if (lookup != null) {
                    if (lookup.hasSubstitution(glyph.index)) {
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
        }
        return null;
    }

    private boolean transform(List<OpenTableLookup> feature, GlyphLine glyphLine) {
        boolean transformed = false;
        if (feature != null) {
            for (OpenTableLookup lookup : feature) {
                if (lookup != null) {
                    if (lookup.transformLine(glyphLine)) {
                        transformed = true;
                    }
                }
            }
        }
        return transformed;
    }

    private LanguageRecord getLanguageRecord() {
        LanguageRecord languageRecord = null;
        if (otfScriptTag != null) {
            for (ScriptRecord record : gsubTable.getScriptRecords()) {
                if (otfScriptTag.equals(record.tag)) {
                    languageRecord = record.defaultLanguage;
                    break;
                }
            }
        }
        return languageRecord;
    }

    private List<DevanagariCluster> splitDevanagariGlyphLineIntoClusters(GlyphLine glyphLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            Glyph glyph = glyphLine.glyphs.get(i);
            sb.append(glyph.unicode == null ? 'X' : getDevanagariClass(glyph.unicode));
        }
        String classes = sb.toString();

        String regex = "(((C|R)N?((HZ?)|(ZH)))*(C|R)N?A?((HZ?)|(M*N?H?))?S?D?)|" +
                "((RH)?VN?((Z?H(C|R))|(Z(C|R)))?(M*N?H?)?S?(D{1,2})?)|" +
                "(X(RH)?BN?((Z?H(C|R))|(Z(C|R)))?(M*N?H?)?S?(D{1,2})?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("X" + classes);

        List<DevanagariCluster> clusters = new ArrayList<>();

        while (matcher.find()) {
            clusters.add(new DevanagariCluster(glyphLine, glyphLine.start + matcher.start() - 1, glyphLine.start + matcher.end() - 1, classes.substring(matcher.start() - 1, matcher.end() - 1)));
        }

        // TODO sorting and eliminating intersections?

        return clusters;
    }

    /**
     * Classifies each char according to its Devanagari class.
     * Classes are:
     * X - no indic char
     * C - Consonant
     * V - Independent vowel
     * N - Nukta
     * H - Halant/Virama
     * Z - ZWJ|ZWNJ
     * M - Dependent Vowel Signs (Matras)
     * S - Syllable modifier signs
     * D - Vedic
     * A - Anudatta (U+0952)
     * B - NO-BREAK SPACE
     * R - Ra
     */
    private char getDevanagariClass(int c) {
        if (c == '\u0952')
            return 'A';
        else if (c == '\u094d')
            return 'H';
        else if (c == '\u093c')
            return 'N';
        else if (c == '\u0930')
            return 'R';
        else if (c == '\u0951')
            return 'D';
        else if (c == '\u00a0')
            return 'B';
        else if (c == '\u200d' || c == '\u200c')
            return 'Z';
        else if ((c >= '\u0915' && c <= '\u0939') || (c >= '\u0958' && c <= '\u095f')
                || (c >= '\u0978' && c <= '\u097a'))
            return 'C';
        else if ((c >= '\u0904' && c <= '\u0914') || (c >= '\u0972' && c <= '\u0977'))
            return 'V';
        else if ((c >= '\u093a' && c <= '\u093b') || (c >= '\u093e' && c <= '\u094c')
                || (c >= '\u094e' && c <= '\u094f') || (c >= '\u0955' && c <= '\u0957'))
            return 'M';
        else if ((c >= '\u0900' && c <= '\u0903') || c == '\u093d')
            return 'S';
        else
            return 'X';
    }

    private static class DevanagariCluster {
        public GlyphLine glyphLine;
        public int start;
        public int end;
        public String classes;

        public DevanagariCluster(GlyphLine glyphLine, int start, int end, String classes) {
            this.glyphLine = glyphLine;
            this.start = start;
            this.end = end;
            this.classes = classes;
        }
    }

}
