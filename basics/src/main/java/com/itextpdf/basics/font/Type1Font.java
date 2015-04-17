package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Type1Font {

    /** Type 1 font parser. */
    private Type1Parser fontParser;

    /** The Postscript font name. */
    private String FontName;
    /** The full name of the font. */
    private String FullName;
    /** The family name of the font. */
    private String FamilyName;

    /** The font's encoding name. This encoding is 'StandardEncoding' or 'AdobeStandardEncoding' for a font
     * that can be totally encoded according to the characters names. For all other names the font is treated as symbolic. */
    private String EncodingScheme = "FontSpecific";
    /** Font encoding. */
    private FontEncoding encoding;
    /** Contains the smallest box enclosing the character contours. */
    private int[][] charBBoxes = new int[256][];
    /** Table of characters widths for this encoding. */
    private int[] widths = new int[256];
    /** The weight of the font: normal, bold, etc. */
    private String Weight = "";
    /** The italic angle of the font, usually 0.0 or negative. */
    private float ItalicAngle = 0.0f;
    /** {@code true} if all the characters have the same width. */
    private boolean IsFixedPitch = false;
    /** The character set of the font. */
    private String CharacterSet;
    /** The llx of the FontBox. */
    private int llx = -50;
    /** The lly of the FontBox. */
    private int lly = -200;
    /** The lurx of the FontBox. */
    private int urx = 1000;
    /** The ury of the FontBox. */
    private int ury = 900;
    /** The underline position. */
    private int UnderlinePosition = -100;
    /** The underline thickness. */
    private int UnderlineThickness = 50;
    /** A variable. */
    private int CapHeight = 700;
    /** A variable. */
    private int XHeight = 480;
    /** A variable. */
    private int Ascender = 800;
    /** A variable. */
    private int Descender = -200;
    /** A variable. */
    private int StdHW;
    /** A variable. */
    private int StdVW = 80;
    /** Represents the section CharMetrics in the AFM file. Each value of this array
     * contains a {@code Object[4]} with an Integer, Integer, String and int[].
     * This is the code, width, name and char bbox. The key is the name of the char
     * and also an Integer with the char number. */
    private HashMap<Object, Object[]> CharMetrics = new HashMap<Object, Object[]>();
    /** Represents the section KernPairs in the AFM file. The key is the name of the first character
     * and the value is a {@code Object[]} with two elements for each kern pair. Position 0 is the name of
     * the second character and position 1 is the kerning distance. This is repeated for all the pairs. */
    private HashMap<String, Object[]> KernPairs = new HashMap<String, Object[]>();

    /** Types of records in a PFB file. ASCII is 1 and BINARY is 2. They have to appear in the PFB file in this sequence. */
    private static final int PFB_TYPES[] = {1, 2, 1};

    private byte[] fontStreamBytes;
    private int[] fontStreamLengths;


    public Type1Font(String name, String encoding, byte[] afm, byte[] pfb) throws PdfException, IOException {
        fontParser = new Type1Parser(name, afm, pfb);
        process(encoding);
    }

    public Type1Font(String name, String encoding) throws PdfException, IOException {
        this(name, encoding, null, null);
    }

    public boolean isBuiltInFont() {
        return fontParser.isBuiltInFont();
    }

    public String getFontName() {
        return FontName;
    }

    public String getFullName() {
        return FullName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public FontEncoding getEncoding() {
        return encoding;
    }

    /**
     * Gets the font parameter identified by {@code key}. Valid values
     * for {@code key} are {@code ASCENT}, {@code CAPHEIGHT}, {@code DESCENT},
     * {@code ITALICANGLE}, {@code BBOXLLX}, {@code BBOXLLY}, {@code BBOXURX}
     * and {@code BBOXURY}.
     * @param key the parameter to be extracted.
     * @param fontSize the font size in points.
     * @return the parameter in points.
     */
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case FontConstants.AWT_ASCENT:
            case FontConstants.ASCENT:
                return Ascender * fontSize / 1000;
            case FontConstants.CAPHEIGHT:
                return CapHeight * fontSize / 1000;
            case FontConstants.AWT_DESCENT:
            case FontConstants.DESCENT:
                return Descender * fontSize / 1000;
            case FontConstants.ITALICANGLE:
                return ItalicAngle;
            case FontConstants.BBOXLLX:
                return llx * fontSize / 1000;
            case FontConstants.BBOXLLY:
                return lly * fontSize / 1000;
            case FontConstants.BBOXURX:
                return urx * fontSize / 1000;
            case FontConstants.BBOXURY:
                return ury * fontSize / 1000;
            case FontConstants.AWT_LEADING:
                return 0;
            case FontConstants.AWT_MAXADVANCE:
                return (urx - llx) * fontSize / 1000;
            case FontConstants.UNDERLINE_POSITION:
                return UnderlinePosition * fontSize / 1000;
            case FontConstants.UNDERLINE_THICKNESS:
                return UnderlineThickness * fontSize / 1000;
        }
        return 0;
    }

    /**
     * Sets the font parameter identified by {@code key}. Valid values
     * for {@code key} are {@code ASCENT}, {@code AWT_ASCENT}, {@code CAPHEIGHT},
     * {@code DESCENT}, {@code AWT_DESCENT},
     * {@code ITALICANGLE}, {@code BBOXLLX}, {@code BBOXLLY}, {@code BBOXURX}
     * and {@code BBOXURY}.
     * @param key the parameter to be updated.
     * @param value the parameter value.
     */
    public void setFontDescriptor(int key, float value) {
        switch (key) {
            case FontConstants.AWT_ASCENT:
            case FontConstants.ASCENT:
                Ascender = (int)value;
                break;
            case FontConstants.AWT_DESCENT:
            case FontConstants.DESCENT:
                Descender = (int)value;
                break;
            default:
                break;
        }
    }

    public int getLlx() {
        return llx;
    }

    public int getLly() {
        return lly;
    }

    public int getUrx() {
        return urx;
    }

    public int getUry() {
        return ury;
    }

    public int getUnderlinePosition() {
        return UnderlinePosition;
    }

    public int getUnderlineThickness() {
        return UnderlineThickness;
    }

    public int getCapHeight() {
        return CapHeight;
    }

    public int getXHeight() {
        return XHeight;
    }

    public int getAscender() {
        return Ascender;
    }

    public int getDescender() {
        return Descender;
    }

    public int getStdHW() {
        return StdHW;
    }

    public int getStdVW() {
        return StdVW;
    }

    public boolean isFixedPitch() {
        return IsFixedPitch;
    }

    public float getItalicAngle() {
        return ItalicAngle;
    }

    public String getWeight() {
        return Weight;
    }

    public String getCharacterSet() {
        return CharacterSet;
    }

    /**
     * Checks if the font has any kerning pairs.
     * @return {@code true} if the font has any kerning pairs.
     */
    public boolean hasKernPairs() {
        return !KernPairs.isEmpty();
    }

    /**
     * Gets the kerning between two Unicode characters. The characters
     * are converted to names and this names are used to find the kerning
     * pairs in the {@code HashMap} {@code KernPairs}.
     * @param char1 the first char
     * @param char2 the second char
     * @return the kerning to be applied
     */
    public int getKerning(int char1, int char2) {
        String first = AdobeGlyphList.unicodeToName(char1);
        if (first == null) {
            return 0;
        }
        String second = AdobeGlyphList.unicodeToName(char2);
        if (second == null) {
            return 0;
        }
        Object obj[] = KernPairs.get(first);
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
     * @param char1 the first char.
     * @param char2 the second char.
     * @param kern the kerning to apply in normalized 1000 units.
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
        Object obj[] = KernPairs.get(first);
        if (obj == null) {
            obj = new Object[]{second, kern};
            KernPairs.put(first, obj);
            return true;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                obj[k + 1] = kern;
                return true;
            }
        }
        int size = obj.length;
        Object obj2[] = new Object[size + 2];
        System.arraycopy(obj, 0, obj2, 0, size);
        obj2[size] = second;
        obj2[size + 1] = kern;
        KernPairs.put(first, obj2);
        return true;
    }

    /**
     * Gets the width from the font according to the {@code name} or,
     * if the {@code name} is null, meaning it is a symbolic font,
     * the char {@code c}.
     * @param c the char if the font is symbolic
     * @param name the glyph name
     * @return the width of the char
     */
    public int getRawWidth(int c, String name) {
        Object metrics[];
        if (name == null) { // font specific
            metrics = CharMetrics.get(c);
        } else {
            if (name.equals(".notdef")) {
                return 0;
            }
            metrics = CharMetrics.get(name);
        }
        if (metrics != null) {
            return (Integer) metrics[1];
        }
        return 0;
    }

    public int[] getRawCharBBox(int c, String name) {
        Object metrics[];
        if (name == null) { // font specific
            metrics = CharMetrics.get(Integer.valueOf(c));
        } else {
            if (name.equals(FontConstants.notdef)) {
                return null;
            }
            metrics = CharMetrics.get(name);
        }
        if (metrics != null) {
            return (int[]) metrics[3];
        }
        return null;
    }

    /**
     * Gets the width of a {@code char} in normalized 1000 units.
     * @param ch the unicode {@code char} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(int ch) {
        if (encoding.isFastWinansi()) {
            if (ch < 128 || ch >= 160 && ch <= 255) {
                return widths[ch];
            } else {
                return widths[PdfEncodings.winansi.get(ch)];
            }
        } else {
            int total = 0;
            byte bytes[] = encoding.convertToBytes(ch);
            for (byte b : bytes) {
                total += widths[0xff & b];
            }
            return total;
        }
    }

    /**
     * Gets table of characters widths for this simple font encoding.
     */
    public int[] getRawWidths() {
        return widths;
    }

    /**
     * Gets the width of a {@code String} in normalized 1000 units.
     * @param text the {@code String} to get the width of
     * @return the width in normalized 1000 units
     */
    public int getWidth(String text) {
        int total = 0;
        if (encoding.isFastWinansi()) {
            int len = text.length();
            for (int k = 0; k < len; ++k) {
                char char1 = text.charAt(k);
                if (char1 < 128 || char1 >= 160 && char1 <= 255) {
                    total += widths[char1];
                } else {
                    total += widths[PdfEncodings.winansi.get(char1)];
                }
            }
            return total;
        }
        else {
            byte bytes[] = encoding.convertToBytes(text);
            for (byte b : bytes) {
                total += widths[0xff & b];
            }
        }
        return total;
    }

    /**
     * Gets the descent of a <CODE>String</CODE> in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     * @param text the <CODE>String</CODE> to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(String text) {
        int min = 0;
        char chars[] = text.toCharArray();
        for (char ch : chars) {
            int bbox[] = getCharBBox(ch);
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            }
        }
        return min;
    }

    /**
     * Gets the ascent of a <CODE>String</CODE> in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     * @param text the <CODE>String</CODE> to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(String text) {
        int max = 0;
        char chars[] = text.toCharArray();
        for (char ch : chars) {
            int bbox[] = getCharBBox(ch);
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            }
        }
        return max;
    }

    public int[] getCharBBox(char ch) {
        byte b[] = encoding.convertToBytes(ch);
        if (b.length == 0) {
            return null;
        } else {
            return charBBoxes[b[0] & 0xff];
        }
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        return encoding.convertToBytes(text);
    }

    public byte[] getFontStreamBytes() throws PdfException {
        if (fontParser.isBuiltInFont())
            return null;
        if (fontStreamBytes != null)
            return fontStreamBytes;
        RandomAccessFileOrArray raf = null;
        try {
            raf = fontParser.getPostscriptBinary();
            int fileLength = (int)raf.length();
            fontStreamBytes = new byte[fileLength - 18];
            fontStreamLengths = new int[3];
            int bytePtr = 0;
            for (int k = 0; k < 3; ++k) {
                if (raf.read() != 0x80) {
                    throw new PdfException("start.marker.missing.in.1").setMessageParams(fontParser.getPfbName());
                }
                if (raf.read() != PFB_TYPES[k])
                    throw new PdfException("incorrect.segment.type.in.1").setMessageParams(fontParser.getPfbName());
                int size = raf.read();
                size += raf.read() << 8;
                size += raf.read() << 16;
                size += raf.read() << 24;
                fontStreamLengths[k] = size;
                while (size != 0) {
                    int got = raf.read(fontStreamBytes, bytePtr, size);
                    if (got < 0) {
                        throw new PdfException("premature.end.in.1").setMessageParams(fontParser.getPfbName());
                    }
                    bytePtr += got;
                    size -= got;
                }
            }
            return fontStreamBytes;
        } catch (Exception e) {
            throw new PdfException("type1.font.file.exception", e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception ignored) { }
            }
        }
    }

    public int[] getFontStreamLengths() {
        return fontStreamLengths;
    }

    protected void process(String baseEncoding) throws PdfException, IOException {
        RandomAccessFileOrArray raf = fontParser.getMetricsFile();
        String line;
        boolean startKernPairs = false;
        while ((line = raf.readLine()) != null)
        {
            StringTokenizer tok = new StringTokenizer(line, " ,\n\r\t\f");
            if (!tok.hasMoreTokens())
                continue;
            String ident = tok.nextToken();
            if (ident.equals("FontName")) {
                FontName = tok.nextToken("\u00ff").substring(1);
            } else if (ident.equals("FullName")) {
                FullName = tok.nextToken("\u00ff").substring(1);
            } else if (ident.equals("FamilyName")) {
                FamilyName = tok.nextToken("\u00ff").substring(1);
            } else if (ident.equals("Weight")) {
                Weight = tok.nextToken("\u00ff").substring(1);
            } else if (ident.equals("ItalicAngle")) {
                ItalicAngle = Float.parseFloat(tok.nextToken());
            } else if (ident.equals("IsFixedPitch")) {
                IsFixedPitch = tok.nextToken().equals("true");
            } else if (ident.equals("CharacterSet")) {
                CharacterSet = tok.nextToken("\u00ff").substring(1);
            } else if (ident.equals("FontBBox")) {
                llx = (int)Float.parseFloat(tok.nextToken());
                lly = (int)Float.parseFloat(tok.nextToken());
                urx = (int)Float.parseFloat(tok.nextToken());
                ury = (int)Float.parseFloat(tok.nextToken());
            } else if (ident.equals("UnderlinePosition")) {
                UnderlinePosition = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("UnderlineThickness")) {
                UnderlineThickness = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("EncodingScheme")) {
                EncodingScheme = tok.nextToken("\u00ff").substring(1).trim();
            } else if (ident.equals("CapHeight")) {
                CapHeight = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("XHeight")) {
                XHeight = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("Ascender")) {
                Ascender = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("Descender")) {
                Descender = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("StdHW")) {
                StdHW = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("StdVW")) {
                StdVW = (int) Float.parseFloat(tok.nextToken());
            } else if (ident.equals("StartCharMetrics")) {
                startKernPairs = true;
                break;
            }
        }
        if (!startKernPairs) {
            throw new PdfException("missing.startcharmetrics.in.1").setMessageParams(fontParser.getName());
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
                if (ident.equals("C")) {
                    C = Integer.valueOf(tokc.nextToken());
                } else if (ident.equals("WX")) {
                    WX = (int) Float.parseFloat(tokc.nextToken());
                } else if (ident.equals("N")) {
                    N = tokc.nextToken();
                } else if (ident.equals("B")) {
                    B = new int[]{Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken())};
                }
            }
            Object[] metrics = new Object[]{C, WX, N, B};
            if (C >= 0) {
                CharMetrics.put(C, metrics);
            }
            CharMetrics.put(N, metrics);
        }
        if (startKernPairs) {
            throw new PdfException("missing.endcharmetrics.in.1").setMessageParams(fontParser.getName());
        }
        if (!CharMetrics.containsKey("nonbreakingspace")) {
            Object[] space = CharMetrics.get("space");
            if (space != null)
                CharMetrics.put("nonbreakingspace", space);
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
                    Object relates[] = KernPairs.get(first);
                    if (relates == null) {
                        KernPairs.put(first, new Object[]{second, width});
                    } else {
                        int n = relates.length;
                        Object relates2[] = new Object[n + 2];
                        System.arraycopy(relates, 0, relates2, 0, n);
                        relates2[n] = second;
                        relates2[n + 1] = width;
                        KernPairs.put(first, relates2);
                    }
                } else if (ident.equals("EndKernPairs")) {
                    startKernPairs = false;
                    break;
                }
            }
        } else if (!endOfMetrics) {
            throw new PdfException("missing.endfontmetrics.in.1").setMessageParams(fontParser.getName());
        }

        if (startKernPairs) {
            throw new PdfException("missing.endkernpairs.in.1").setMessageParams(fontParser.getName());
        }
        raf.close();

        boolean fontSpecific = true;
        if (EncodingScheme.equals("AdobeStandardEncoding") || EncodingScheme.equals("StandardEncoding")) {
            fontSpecific = false;
        }
        encoding = new FontEncoding(baseEncoding, fontSpecific);
        if (encoding.hasSpecialEncoding()) {
            createSpecialEncoding();
        } else {
            createEncoding();
        }
    }

    /**
     * Creates the {@code widths} and the {@code differences} arrays.
     */
    protected void createEncoding() throws PdfException {
        if (encoding.isFontSpecific()) {
            for (int k = 0; k < 256; ++k) {
                widths[k] = getRawWidth(k, null);
                charBBoxes[k] = getRawCharBBox(k, null);
            }
        } else {
            String s;
            String name;
            char ch;
            byte b[] = new byte[1];

            for (int k = 0; k < 256; ++k) {
                b[0] = (byte)k;
                s = PdfEncodings.convertToString(b, encoding.getBaseEncoding());
                if (s.length() > 0) {
                    ch = s.charAt(0);
                } else {
                    ch = '?';
                }
                name = AdobeGlyphList.unicodeToName(ch);
                if (name == null) {
                    name = FontConstants.notdef;
                }
                encoding.setDifferences(k, name);
                encoding.setUnicodeDifferences(k, ch);
                widths[k] = getRawWidth(ch, name);
                charBBoxes[k] = getRawCharBBox(ch, name);
            }
        }
    }

    /**
     * Creates the {@code widths} and the {@code differences} arrays in case special user map-encoding.
     * Encoding starts with '# simple …' or '# full …'.
     */
    protected void createSpecialEncoding() {
        StringTokenizer tok = new StringTokenizer(encoding.getBaseEncoding().substring(1), " ,\t\n\r\f");
        if (tok.nextToken().equals("full")) {
            while (tok.hasMoreTokens()) {
                String order = tok.nextToken();
                String name = tok.nextToken();
                char uni = (char)Integer.parseInt(tok.nextToken(), 16);
                int orderK;
                if (order.startsWith("'")) {
                    orderK = order.charAt(1);
                } else {
                    orderK = Integer.parseInt(order);
                }
                orderK %= 256;
                encoding.getSpecialMap().put(uni, orderK);
                encoding.setDifferences(orderK, name);
                encoding.setUnicodeDifferences(orderK, uni);

                widths[orderK] = getRawWidth(uni, name);
                charBBoxes[orderK] = getRawCharBBox(uni, name);
            }
        } else {
            int k = 0;
            if (tok.hasMoreTokens()) {
                k = Integer.parseInt(tok.nextToken());
            }
            while (tok.hasMoreTokens() && k < 256) {
                String hex = tok.nextToken();
                int uni = Integer.parseInt(hex, 16) % 0x10000;
                String name = AdobeGlyphList.unicodeToName(uni);
                if (name != null) {
                    encoding.getSpecialMap().put(uni, k);
                    encoding.setDifferences(k, name);
                    encoding.setUnicodeDifferences(k, (char)uni);

                    widths[k] = getRawWidth(uni, name);
                    charBBoxes[k] = getRawCharBBox(uni, name);
                    ++k;
                }
            }
        }
        encoding.fillEmptyDifferences();
    }
}
