package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;

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

    public Type1Font(String name, String encoding, byte[] afm, byte[] pfb) throws IOException {
        fontParser = new Type1Parser(name, afm, pfb);
        process(encoding);
    }

    public Type1Font(String baseEncoding) throws IOException {
        boolean fontSpecific = true;
        if (encodingScheme.equals("AdobeStandardEncoding") || encodingScheme.equals("StandardEncoding")) {
            fontSpecific = false;
        }
        this.encoding = new FontEncoding(baseEncoding, fontSpecific);
    }

    public Type1Font(String name, String encoding) throws IOException {
        this(name, encoding, null, null);
    }

    public boolean isBuiltInFont() {
        return fontParser.isBuiltInFont();
    }

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
            byte[] bytes = encoding.convertToBytes(ch);
            for (byte b : bytes) {
                total += widths[0xff & b];
            }
            return total;
        }
    }

    /**
     * Gets the width of a {@code String} in normalized 1000 units.
     *
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
        } else {
            byte bytes[] = encoding.convertToBytes(text);
            for (byte b : bytes) {
                total += widths[0xff & b];
            }
        }
        return total;
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
                    fontNames.setFamilyName(new String[][] {new String[]{"","","", familyName }});
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
                    B = new int[] {
                            Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken()),
                            Integer.parseInt(tokc.nextToken()) };
                }
            }
            Object[] metrics = new Object[]{C, WX, N, B};
            if (C >= 0) {
                charMetrics.put(C, metrics);
            }
            charMetrics.put(N, metrics);
        }
        if (startKernPairs) {
            throw new PdfException("missing.endcharmetrics.in.1").setMessageParams(fontParser.getName());
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
            throw new PdfException("missing.endfontmetrics.in.1").setMessageParams(fontParser.getName());
        }

        if (startKernPairs) {
            throw new PdfException("missing.endkernpairs.in.1").setMessageParams(fontParser.getName());
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
