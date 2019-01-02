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
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class Type1Font extends FontProgram {

    private static final long serialVersionUID = -1078208220942939920L;

    private Type1Parser fontParser;

    private String characterSet;

    /**
     * Represents the section KernPairs in the AFM file.
     * Key is uni1 &lt;&lt; 32 + uni2. Value is kerning value.
     */
    private Map<Long, Integer> kernPairs = new HashMap<>();

    /**
     * Types of records in a PFB file. ASCII is 1 and BINARY is 2. They have to appear in the PFB file in this sequence.
     */
    private static final int[] PFB_TYPES = {1, 2, 1};

    private byte[] fontStreamBytes;
    private int[] fontStreamLengths;

    protected static Type1Font createStandardFont(String name) throws java.io.IOException {
        if (StandardFonts.isStandardFont(name)) {
            return new Type1Font(name, null, null, null);
        } else {
            throw new IOException("{0} is not a standard type1 font.").setMessageParams(name);
        }
    }

    protected Type1Font() {
        fontNames = new FontNames();
    }

    protected Type1Font(String metricsPath, String binaryPath, byte[] afm, byte[] pfb) throws java.io.IOException {
        this();

        fontParser = new Type1Parser(metricsPath, binaryPath, afm, pfb);
        process();
    }

    protected Type1Font(String baseFont) {
        this();
        getFontNames().setFontName(baseFont);
    }

    public boolean isBuiltInFont() {
        return fontParser != null && fontParser.isBuiltInFont();
    }

    @Override
    public int getPdfFontFlags() {
        int flags = 0;
        if (fontMetrics.isFixedPitch()) {
            flags |= 1;
        }
        flags |= isFontSpecific() ? 4 : 32;
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
        return kernPairs.size() > 0;
    }

    @Override
    public int getKerning(Glyph first, Glyph second) {
        if (first.hasValidUnicode() && second.hasValidUnicode()) {
            long record = ((long)first.getUnicode() << 32) + second.getUnicode();
            if (kernPairs.containsKey(record)) {
                return (int) kernPairs.get(record);
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Sets the kerning between two Unicode chars.
     *
     * @param first the first unicode char.
     * @param second the second unicode char.
     * @param kern  the kerning to apply in normalized 1000 units.
     * @return {@code true} if the kerning was applied, {@code false} otherwise.
     */
    public boolean setKerning(int first, int second, int kern) {
        long record = ((long)first << 32) + second;
        kernPairs.put(record, kern);
        return true;
    }

    /**
     * Find glyph by glyph name.
     * @param name Glyph name
     * @return Glyph instance if found, otherwise null.
     */
    public Glyph getGlyph(String name) {
        int unicode = AdobeGlyphList.nameToUnicode(name);
        if (unicode != -1) {
            return getGlyph((int) unicode);
        } else {
            return null;
        }
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
                    logger.error(LogMessageConstant.START_MARKER_MISSING_IN_PFB_FILE);
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

    public boolean isBuiltWith(String fontProgram) {
        return Objects.equals(fontParser.getAfmPath(), fontProgram);
    }

    protected void process() throws java.io.IOException {
        RandomAccessFileOrArray raf = fontParser.getMetricsFile();
        String line;
        boolean startKernPairs = false;
        while (!startKernPairs && (line = raf.readLine()) != null) {
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
                    fontNames.setFontWeight(FontWeights.fromType1FontWeight(tok.nextToken("\u00ff").substring(1)));
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
                    fontMetrics.setBbox(llx, lly, urx, ury);
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
                    break;
            }
        }
        if (!startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new IOException("startcharmetrics is missing in {0}.").setMessageParams(metricsPath);
            } else {
                throw new IOException("startcharmetrics is missing in the metrics file.");
            }
        }
        avgWidth = 0;
        int widthCount = 0;
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
            int C = -1;
            int WX = 250;
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
                        C = Integer.parseInt(tokc.nextToken());
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
            int unicode = AdobeGlyphList.nameToUnicode(N);
            Glyph glyph = new Glyph(C, WX, unicode, B);
            if (C >= 0) {
                codeToGlyph.put(C, glyph);
            }
            if (unicode != -1) {
                unicodeToGlyph.put(unicode, glyph);
            }
            avgWidth += WX;
            widthCount++;
        }
        if (widthCount != 0) {
            avgWidth /= widthCount;
        }
        if (startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new IOException("endcharmetrics is missing in {0}.").setMessageParams(metricsPath);
            } else {
                throw new IOException("endcharmetrics is missing in the metrics file.");
            }
        }

        // From AdobeGlyphList:
        // nonbreakingspace;00A0
        // space;0020
        if (!unicodeToGlyph.containsKey(0x00A0)) {
            Glyph space = unicodeToGlyph.get(0x0020);
            if (space != null) {
                unicodeToGlyph.put(0x00A0, new Glyph(space.getCode(), space.getWidth(), 0x00A0, space.getBbox()));
            }
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

                    int firstUni = AdobeGlyphList.nameToUnicode(first);
                    int secondUni = AdobeGlyphList.nameToUnicode(second);

                    if (firstUni != -1 && secondUni != -1) {
                        long record = ((long)firstUni << 32) + secondUni;
                        kernPairs.put(record, width);
                    }
                } else if (ident.equals("EndKernPairs")) {
                    startKernPairs = false;
                    break;
                }
            }
        } else if (!endOfMetrics) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new IOException("endfontmetrics is missing in {0}.").setMessageParams(metricsPath);
            } else {
                throw new IOException("endfontmetrics is missing in the metrics file.");
            }
        }

        if (startKernPairs) {
            String metricsPath = fontParser.getAfmPath();
            if (metricsPath != null) {
                throw new IOException("endkernpairs is missing in {0}.").setMessageParams(metricsPath);
            } else {
                throw new IOException("endkernpairs is missing in the metrics file.");
            }
        }
        raf.close();

        isFontSpecific = !(encodingScheme.equals("AdobeStandardEncoding") || encodingScheme.equals("StandardEncoding"));
    }
}
