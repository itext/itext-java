package com.itextpdf.basics.font;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Type1Font extends FontProgram {

    /**
     * Type 1 font parser.
     */
    private Type1Parser fontParser;

    /**
     * The character set of the font.
     */
    private String characterSet;

    /**
     * Represents the section CharMetrics in the AFM file. Each value of this array
     * contains a {@code Object[4]} with an Integer, Integer, String and int[].
     * This is the code, width, name and char bbox. The key is the name of the char
     * and also an Integer with the char number.
     */
    private HashMap<Object, Object[]> charMetrics = new HashMap<>();

    /**
     * Represents the section KernPairs in the AFM file. The key is the name of the first character
     * and the value is a {@code Object[]} with two elements for each kern pair. Position 0 is the name of
     * the second character and position 1 is the kerning distance. This is repeated for all the pairs.
     */
    private HashMap<String, Object[]> kernPairs = new HashMap<>();

    /**
     * Types of records in a PFB file. ASCII is 1 and BINARY is 2. They have to appear in the PFB file in this sequence.
     */
    private static final int[] PFB_TYPES = {1, 2, 1};

    private byte[] fontStreamBytes;
    private int[] fontStreamLengths;

    //TODO remove
    public Type1Font(String baseEncoding) throws IOException {
        boolean fontSpecific = true;
        if (encodingScheme.equals("AdobeStandardEncoding") || encodingScheme.equals("StandardEncoding")) {
            fontSpecific = false;
        }
        this.encoding = new FontEncoding(baseEncoding, fontSpecific);
    }

    public static Type1Font createStandardFont(String name, String encoding) throws IOException {
        if (FontConstants.BUILTIN_FONTS_14.contains(name)) {
            return createFont(name, encoding);
        } else {
            throw new PdfException("1.is.not.a.standard.type1.font").setMessageParams(name);
        }
    }

    public static Type1Font createFont(String metricsPath, String encoding) throws IOException {
        return new Type1Font(metricsPath, null, null, null, encoding);
    }

    public static Type1Font createFont(String metricsPath, String binaryPath, String encoding) throws IOException {
        return new Type1Font(metricsPath, binaryPath, null, null, encoding);
    }

    public static Type1Font createFont(byte[] metricsData, String encoding) throws IOException {
        return new Type1Font(null, null, metricsData, null, encoding);
    }

    public static Type1Font createFont(byte[] metricsData, byte[] binaryData, String encoding) throws IOException {
        return new Type1Font(null, null, metricsData, binaryData, encoding);
    }

    protected Type1Font(String metricsPath, String binaryPath, byte[] afm, byte[] pfb, String encoding) throws IOException {
        fontParser = new Type1Parser(metricsPath, binaryPath, afm, pfb);
        process(encoding);
    }

    public boolean isBuiltInFont() {
        return fontParser.isBuiltInFont();
    }

    @Override
    public int getPdfFontFlags() {
        int flags = 0;
        if (fontMetrics.isFixedPitch()) {
            flags |= 1;
        }
        flags |= getEncoding().isFontSpecific() ? 4 : 32;
        if (fontMetrics.getItalicAngle() < 0) {
            flags |= 64;
        }
        if (fontNames.getFontName().contains("Caps") || fontNames.getFontName().endsWith("SC")) {
            flags |= 131072;
        }
        if (fontNames.isBold() || fontNames.getFontWeight() > 500) {
            flags |= 262144;
        }
        return flags;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    /**
     * Checks if the font has any kerning pairs.
     *
     * @return {@code true} if the font has any kerning pairs.
     */
    @Override
    public boolean hasKernPairs() {
        return !kernPairs.isEmpty();
    }

    /**
     * Gets the kerning between two Unicode characters. The characters
     * are converted to names and this names are used to find the kerning
     * pairs in the {@code HashMap} {@code KernPairs}.
     *
     * @param char1 the first char
     * @param char2 the second char
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(int char1, int char2) {
        String first = AdobeGlyphList.unicodeToName(char1);
        if (first == null) {
            return 0;
        }
        String second = AdobeGlyphList.unicodeToName(char2);
        if (second == null) {
            return 0;
        }
        Object[] obj = kernPairs.get(first);
        if (obj == null) {
            return 0;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                return (Integer) obj[k + 1];
            }
        }
        return 0;
    }

    /**
     * Sets the kerning between two Unicode chars.
     *
     * @param char1 the first char.
     * @param char2 the second char.
     * @param kern  the kerning to apply in normalized 1000 units.
     * @return {@code true} if the kerning was applied, {@code false} otherwise.
     */
    public boolean setKerning(int char1, int char2, int kern) {
        String first = AdobeGlyphList.unicodeToName(char1);
        if (first == null) {
            return false;
        }
        String second = AdobeGlyphList.unicodeToName(char2);
        if (second == null)
            return false;
        Object[] obj = kernPairs.get(first);
        if (obj == null) {
            obj = new Object[]{second, kern};
            kernPairs.put(first, obj);
            return true;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                obj[k + 1] = kern;
                return true;
            }
        }
        int size = obj.length;
        Object[] obj2 = new Object[size + 2];
        System.arraycopy(obj, 0, obj2, 0, size);
        obj2[size] = second;
        obj2[size + 1] = kern;
        kernPairs.put(first, obj2);
        return true;
    }

    /**
     * Gets the width from the font according to the {@code name} or,
     * if the {@code name} is null, meaning it is a symbolic font,
     * the char {@code c}.
     *
     * @param c    the char if the font is symbolic
     * @param name the glyph name
     * @return the width of the char
     */
    @Override
    protected int getRawWidth(int c, String name) {
        Object[] metrics;
        if (name == null) { // font specific
            metrics = charMetrics.get(c);
        } else {
            if (name.equals(".notdef")) {
                return 0;
            }
            metrics = charMetrics.get(name);
        }
        if (metrics != null) {
            return (Integer) metrics[1];
        }
        return 0;
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        Object[] metrics;
        if (name == null) { // font specific
            metrics = charMetrics.get(Integer.valueOf(c));
        } else {
            if (name.equals(FontConstants.notdef)) {
                return null;
            }
            metrics = charMetrics.get(name);
        }
        if (metrics != null) {
            return (int[]) metrics[3];
        }
        return null;
    }

    /**
     * Gets the width of a {@code char} in normalized 1000 units.
     *
     * @param code the char code of glyph
     * @return the width in normalized 1000 units
     */
    @Override
    public int getWidth(int code) {
        return widths[code];
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     *
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        return encoding.convertToBytes(text);
    }

    public byte[] getFontStreamBytes() {
        if (fontParser.isBuiltInFont())
            return null;
        if (fontStreamBytes != null)
            return fontStreamBytes;
        RandomAccessFileOrArray raf = null;
        try {
            raf = fontParser.getPostscriptBinary();
            int fileLength = (int) raf.length();
            fontStreamBytes = new byte[fileLength - 18];
            fontStreamLengths = new int[3];
            int bytePtr = 0;
            for (int k = 0; k < 3; ++k) {
                if (raf.read() != 0x80) {
                    Logger logger = LoggerFactory.getLogger(Type1Font.class);
                    logger.error(LogMessageConstant.START_MARKER_MISSING_IN_PDB_FILE);
                    return null;
                }
                if (raf.read() != PFB_TYPES[k]) {
                    Logger logger = LoggerFactory.getLogger(Type1Font.class);
                    logger.error("incorrect.segment.type.in.pfb.file");
                    return null;
                }
                int size = raf.read();
                size += raf.read() << 8;
                size += raf.read() << 16;
                size += raf.read() << 24;
                fontStreamLengths[k] = size;
                while (size != 0) {
                    int got = raf.read(fontStreamBytes, bytePtr, size);
                    if (got < 0) {
                        Logger logger = LoggerFactory.getLogger(Type1Font.class);
                        logger.error("premature.end.in.pfb.file");
                        return null;
                    }
                    bytePtr += got;
                    size -= got;
                }
            }
            return fontStreamBytes;
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(Type1Font.class);
            logger.error("type1.font.file.exception");
            return null;
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public int[] getFontStreamLengths() {
        return fontStreamLengths;
    }

    protected void process(String baseEncoding) throws IOException {
        RandomAccessFileOrArray raf = fontParser.getMetricsFile();
        String line;
        boolean startKernPairs = false;
        label:
        while ((line = raf.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line, " ,\n\r\t\f");
            if (!tok.hasMoreTokens())
                continue;
            String ident = tok.nextToken();
            switch (ident) {
                case "FontName":
                    fontNames.setFontName(tok.nextToken("\u00ff").substring(1));
                    break;
                case "FullName":
                    String fullName = tok.nextToken("\u00ff").substring(1);
                    fontNames.setFullName(new String[][]{new String[]{"", "", "", fullName}});
                    break;
                case "FamilyName":
                    String familyName = tok.nextToken("\u00ff").substring(1);
                    fontNames.setFamilyName(new String[][]{new String[]{"", "", "", familyName}});
                    break;
                case "Weight":
                    fontNames.setWeight(FontNames.convertFontWeight(tok.nextToken("\u00ff").substring(1)));
                    break;
                case "ItalicAngle":
                    fontMetrics.setItalicAngle(Float.parseFloat(tok.nextToken()));
                    break;
                case "IsFixedPitch":
                    fontMetrics.setIsFixedPitch(tok.nextToken().equals("true"));
                    break;
                case "CharacterSet":
                    characterSet = tok.nextToken("\u00ff").substring(1);
                    break;
                case "FontBBox":
                    int llx = (int) Float.parseFloat(tok.nextToken());
                    int lly = (int) Float.parseFloat(tok.nextToken());
                    int urx = (int) Float.parseFloat(tok.nextToken());
                    int ury = (int) Float.parseFloat(tok.nextToken());
                    fontMetrics.getBbox().setBbox(llx, lly, urx, ury);
                    break;
                case "UnderlinePosition":
                    fontMetrics.setUnderlinePosition((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "UnderlineThickness":
                    fontMetrics.setUnderlineThickness((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "EncodingScheme":
                    encodingScheme = tok.nextToken("\u00ff").substring(1).trim();
                    break;
                case "CapHeight":
                    fontMetrics.setCapHeight((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "XHeight":
                    fontMetrics.setXHeight((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "Ascender":
                    fontMetrics.setTypoAscender((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "Descender":
                    fontMetrics.setTypoDescender((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "StdHW":
                    fontMetrics.setStemH((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "StdVW":
                    fontMetrics.setStemV((int) Float.parseFloat(tok.nextToken()));
                    break;
                case "StartCharMetrics":
                    startKernPairs = true;
                    break label;
            }
        }
        if (!startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new PdfException("missing.startcharmetrics.in.1").setMessageParams(metricsPath);
            } else {
                throw new PdfException("missing.startcharmetrics.in.the.metrics.file");
            }
        }
        while ((line = raf.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            String ident = tok.nextToken();
            if (ident.equals("EndCharMetrics")) {
                startKernPairs = false;
                break;
            }
            Integer C = -1;
            Integer WX = 250;
            String N = "";
            int[] B = null;
            tok = new StringTokenizer(line, ";");
            while (tok.hasMoreTokens()) {
                StringTokenizer tokc = new StringTokenizer(tok.nextToken());
                if (!tokc.hasMoreTokens()) {
                    continue;
                }
                ident = tokc.nextToken();
                switch (ident) {
                    case "C":
                        C = Integer.valueOf(tokc.nextToken());
                        break;
                    case "WX":
                        WX = (int) Float.parseFloat(tokc.nextToken());
                        break;
                    case "N":
                        N = tokc.nextToken();
                        break;
                    case "B":
                        B = new int[]{
                                Integer.parseInt(tokc.nextToken()),
                                Integer.parseInt(tokc.nextToken()),
                                Integer.parseInt(tokc.nextToken()),
                                Integer.parseInt(tokc.nextToken())
                        };
                        break;
                }
            }
            Object[] metrics = new Object[]{C, WX, N, B};
            if (C >= 0) {
                charMetrics.put(C, metrics);
            }
            charMetrics.put(N, metrics);
        }
        if (startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new PdfException("missing.endcharmetrics.in.1").setMessageParams(metricsPath);
            } else {
                throw new PdfException("missing.endcharmetrics.in.the.metrics.file");
            }
        }
        if (!charMetrics.containsKey("nonbreakingspace")) {
            Object[] space = charMetrics.get("space");
            if (space != null)
                charMetrics.put("nonbreakingspace", space);
        }
        boolean endOfMetrics = false;
        while ((line = raf.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line);
            if (!tok.hasMoreTokens()) {
                continue;
            }
            String ident = tok.nextToken();
            if (ident.equals("EndFontMetrics")) {
                endOfMetrics = true;
                break;
            } else if (ident.equals("StartKernPairs")) {
                startKernPairs = true;
                break;
            }
        }
        if (startKernPairs) {
            while ((line = raf.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(line);
                if (!tok.hasMoreTokens()) {
                    continue;
                }
                String ident = tok.nextToken();
                if (ident.equals("KPX")) {
                    String first = tok.nextToken();
                    String second = tok.nextToken();
                    Integer width = (int) Float.parseFloat(tok.nextToken());
                    Object[] relates = kernPairs.get(first);
                    if (relates == null) {
                        kernPairs.put(first, new Object[]{second, width});
                    } else {
                        int n = relates.length;
                        Object[] relates2 = new Object[n + 2];
                        System.arraycopy(relates, 0, relates2, 0, n);
                        relates2[n] = second;
                        relates2[n + 1] = width;
                        kernPairs.put(first, relates2);
                    }
                } else if (ident.equals("EndKernPairs")) {
                    startKernPairs = false;
                    break;
                }
            }
        } else if (!endOfMetrics) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new PdfException("missing.endfontmetrics.in.1").setMessageParams(metricsPath);
            } else {
                throw new PdfException("missing.endfontmetrics.in.the.metrics.file");
            }
        }

        if (startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new PdfException("missing.endkernpairs.in.1").setMessageParams(metricsPath);
            } else {
                throw new PdfException("missing.endkernpairs.in.the.metrics.file");
            }
        }
        raf.close();

        boolean fontSpecific = true;
        if (encodingScheme.equals("AdobeStandardEncoding") || encodingScheme.equals("StandardEncoding")) {
            fontSpecific = false;
        }
        encoding = new FontEncoding(baseEncoding, fontSpecific);
        if (encoding.hasSpecialEncoding()) {
            createSpecialEncoding();
        } else {
            createEncoding();
        }
    }
}
