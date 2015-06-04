package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

//TODO see TtfUnicodeWriter
class OpenTypeParser {

    /** The components of table 'head'. */
    protected static class FontHeader {
        /** A variable. */
        int flags;
        /** A variable. */
        int unitsPerEm;
        /** A variable. */
        short xMin;
        /** A variable. */
        short yMin;
        /** A variable. */
        short xMax;
        /** A variable. */
        short yMax;
        /** A variable. */
        int macStyle;
    }

    /** The components of table 'hhea'. */
    protected static class HorizontalHeader {
        /** A variable. */
        short Ascender;
        /** A variable. */
        short Descender;
        /** A variable. */
        short LineGap;
        /** A variable. */
        int advanceWidthMax;
        /** A variable. */
        short minLeftSideBearing;
        /** A variable. */
        short minRightSideBearing;
        /** A variable. */
        short xMaxExtent;
        /** A variable. */
        short caretSlopeRise;
        /** A variable. */
        short caretSlopeRun;
        /** A variable. */
        int numberOfHMetrics;
    }

    /** The components of table 'OS/2'. */
    protected static class WindowsMetrics {
        /** A variable. */
        short xAvgCharWidth;
        /** A variable. */
        int usWeightClass;
        /** A variable. */
        int usWidthClass;
        /** A variable. */
        short fsType;
        /** A variable. */
        short ySubscriptXSize;
        /** A variable. */
        short ySubscriptYSize;
        /** A variable. */
        short ySubscriptXOffset;
        /** A variable. */
        short ySubscriptYOffset;
        /** A variable. */
        short ySuperscriptXSize;
        /** A variable. */
        short ySuperscriptYSize;
        /** A variable. */
        short ySuperscriptXOffset;
        /** A variable. */
        short ySuperscriptYOffset;
        /** A variable. */
        short yStrikeoutSize;
        /** A variable. */
        short yStrikeoutPosition;
        /** A variable. */
        short sFamilyClass;
        /** A variable. */
        byte panose[] = new byte[10];
        /** A variable. */
        byte achVendID[] = new byte[4];
        /** A variable. */
        int fsSelection;
        /** A variable. */
        int usFirstCharIndex;
        /** A variable. */
        int usLastCharIndex;
        /** A variable. */
        short sTypoAscender;
        /** A variable. */
        short sTypoDescender;
        /** A variable. */
        short sTypoLineGap;
        /** A variable. */
        int usWinAscent;
        /** A variable. */
        int usWinDescent;
        /** A variable. */
        int ulCodePageRange1;
        /** A variable. */
        int ulCodePageRange2;
        /** A variable. */
        int sCapHeight;
    }

    protected static class PostTable {
        /** The italic angle. It is usually extracted from the 'post' table or in it's
         * absence with the code:
         * <PRE>
         * {@code -Math.atan2(hhea.caretSlopeRun, hhea.caretSlopeRise) * 180 / Math.PI}
         * </PRE> */
        double italicAngle;
        int underlinePosition;
        int underlineThickness;
        /** <CODE>true</CODE> if all the glyphs have the same width. */
        boolean isFixedPitch;
    }

    protected static class Cmaps {
        /** The map containing the code information for the table 'cmap', encoding 1.0.
         * The key is the code and the value is an {@code int[2]} where position 0
         * is the glyph number and position 1 is the glyph width normalized to 1000 units. */
        HashMap<Integer, int[]> cmap10;
        /** The map containing the code information for the table 'cmap', encoding 3.1 in Unicode.
         * The key is the code and the value is an {@code int[2]} where position 0
         * is the glyph number and position 1 is the glyph width normalized to 1000 units. */
        HashMap<Integer, int[]> cmap31;
        HashMap<Integer, int[]> cmapExt;
        boolean fontSpecific = false;
    }

    /** The file name. */
    protected String fileName;
    /** The file in use. */
    protected RandomAccessFileOrArray raf;
    /** The index for the TTC font. It is an empty {@code String} for a TTF file. */
    protected String ttcIndex;
    /** The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font. */
    protected int directoryOffset;
    /** The font name. This name is usually extracted from the table 'name' with the 'Name ID' 6. */
    protected String fontName;
    /** The full name of the font. */
    protected String[][] fullName;
    /** All the names of the Names-Table. */
    protected String[][] allNameEntries;
    /** The family name of the font. */
    protected String[][] familyName;
    /** The style modifier. */
    protected String style = "";

    /** Indicate, that the font contains 'CFF ' table. */
    protected boolean cff = false;
    /** Offset to 'CFF ' table. */
    protected int cffOffset;
    /** Length of 'CFF ' table. */
    protected int cffLength;

    private int[] glyphWidthsByIndex;

    /**Contains the location of the several tables. The key is the name of
     * the table and the value is an <CODE>int[2]</CODE> where position 0
     * is the offset from the start of the file and position 1 is the length
     * of the table. */
    protected HashMap<String, int[]> tables;

    public OpenTypeParser(String name, byte[] ttf) throws IOException {
        String nameBase = FontProgram.getBaseName(name);
        String ttcName = getTTCName(nameBase);
        if (nameBase.length() < name.length()) {
            style = name.substring(nameBase.length());
        }
        this.fileName = ttcName;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length())
            ttcIndex = nameBase.substring(ttcName.length() + 1);

        if (ttf == null) {
            raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(fileName));
        } else {
            raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(ttf));
        }
        process();
    }

    public String getFontName() {
        return fontName;
    }

    public String[][] getFullName() {
        return fullName;
    }

    public String[][] getAllNameEntries() {
        return allNameEntries;
    }

    public String[][] getFamilyName() {
        return familyName;
    }

    public String getStyle() {
        return style;
    }

    public boolean isCff() {
        return cff;
    }

    public byte[] getFullFont() throws IOException {
        RandomAccessFileOrArray rf2 = null;
        try {
            rf2 = raf.createView();
            byte b[] = new byte[(int)rf2.length()];
            rf2.readFully(b);
            return b;
        }
        finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            } catch (Exception ignored) { }
        }
    }

    /**
     * If this font file is using the Compact Font File Format, then this method
     * will return the raw bytes needed for the font stream. If this method is
     * ever made public: make sure to add a test if (cff == true).
     * @return	a byte array
     * @since	2.1.3
     */
    public byte[] readCffFont() throws IOException {
        if (!isCff()) {
            return null;
        }
        RandomAccessFileOrArray rf2 = null;
        try {
            rf2 = raf.createView();
            rf2.seek(cffOffset);
            byte[] cff = new byte[cffLength];
            rf2.readFully(cff);
            return cff;
        }
        finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            } catch (Exception ignored) { }
        }
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) throws IOException {
        TrueTypeFontSubset sb = new TrueTypeFontSubset(fileName,
                raf.createView(), glyphs, directoryOffset, true, !subset);
        return sb.process();
    }

    /** Reads the font data. */
    protected void process() throws IOException {
        tables = new HashMap<String, int[]>();
        if (ttcIndex.length() > 0) {
            int dirIdx = Integer.parseInt(ttcIndex);
            if (dirIdx < 0) {
                throw new PdfRuntimeException("the.font.index.for.1.must.be.positive").setMessageParams(fileName);
            }
            String mainTag = readStandardString(4);
            if (!mainTag.equals("ttcf")) {
                throw new PdfRuntimeException("1.is.not.a.valid.ttc.file").setMessageParams(fileName);
            }
            raf.skipBytes(4);
            int dirCount = raf.readInt();
            if (dirIdx >= dirCount) {
                throw new PdfRuntimeException("the.font.index.for.1.must.be.between.0.and.2.it.was.3")
                        .setMessageParams(fileName, String.valueOf(dirCount - 1), String.valueOf(dirIdx));
            }
            raf.skipBytes(dirIdx * 4);
            directoryOffset = raf.readInt();
        }
        raf.seek(directoryOffset);
        int ttId = raf.readInt();
        if (ttId != 0x00010000 && ttId != 0x4F54544F) {
            throw new PdfRuntimeException("1.is.not.a.valid.ttf.or.otf.file").setMessageParams(fileName);
        }
        int num_tables = raf.readUnsignedShort();
        raf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            String tag = readStandardString(4);
            raf.skipBytes(4);
            int table_location[] = new int[2];
            table_location[0] = raf.readInt();
            table_location[1] = raf.readInt();
            tables.put(tag, table_location);
        }
        checkCff();
        fontName = getBaseFont();
        fullName = getNames(4); //full name
        familyName = getNames(1); //family name
        allNameEntries = getAllNames();
    }

    /**
     * Gets the name from a composed TTC file name.
     * If I have for input "myfont.ttc,2" the return will
     * be "myfont.ttc".
     * @param name the full name
     * @return the simple file name
     */
    protected static String getTTCName(String name) {
        int idx = name.toLowerCase().indexOf(".ttc,");
        if (idx < 0)
            return name;
        else
            return name.substring(0, idx + 4);
    }

    /** Reads a <CODE>String</CODE> from the font file as bytes using the Cp1252
     *  encoding.
     * @param length the length of bytes to read
     * @return the <CODE>String</CODE> read
     * @throws IOException the font file could not be read
     */
    protected String readStandardString(int length) throws IOException {
        return raf.readString(length, PdfEncodings.WINANSI);
    }

    protected void checkCff() {
        int table_location[];
        table_location = tables.get("CFF ");
        if (table_location != null) {
            cff = true;
            cffOffset = table_location[0];
            cffLength = table_location[1];
        }
    }

    /**
     * Read font header, table 'head'.
     * @throws PdfRuntimeException the font is invalid.
     * @throws IOException the font file could not be read.
     */
    protected FontHeader readHeadTable() throws IOException {
        int table_location[];
        table_location = tables.get("head");
        if (table_location == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("head", fileName + style);
        }
        raf.seek(table_location[0] + 16);
        FontHeader head = new FontHeader();
        head.flags = raf.readUnsignedShort();
        head.unitsPerEm = raf.readUnsignedShort();
        raf.skipBytes(16);
        head.xMin = raf.readShort();
        head.yMin = raf.readShort();
        head.xMax = raf.readShort();
        head.yMax = raf.readShort();
        head.macStyle = raf.readUnsignedShort();
        return head;
    }

    /**
     * Read horizontal header, table 'hhea'.
     * @throws PdfRuntimeException the font is invalid.
     * @throws IOException the font file could not be read.
     */
    protected HorizontalHeader readHheaTable() throws IOException {
        int table_location[];
        table_location = tables.get("hhea");
        if (table_location == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("hhea", fileName + style);
        }
        raf.seek(table_location[0] + 4);
        HorizontalHeader hhea = new HorizontalHeader();
        hhea.Ascender = raf.readShort();
        hhea.Descender = raf.readShort();
        hhea.LineGap = raf.readShort();
        hhea.advanceWidthMax = raf.readUnsignedShort();
        hhea.minLeftSideBearing = raf.readShort();
        hhea.minRightSideBearing = raf.readShort();
        hhea.xMaxExtent = raf.readShort();
        hhea.caretSlopeRise = raf.readShort();
        hhea.caretSlopeRun = raf.readShort();
        raf.skipBytes(12);
        hhea.numberOfHMetrics = raf.readUnsignedShort();
        return hhea;
    }

    /**
     * Reads the windows metrics table. The metrics are extracted from the table 'OS/2'.
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see FontHeader}.
     * @throws PdfRuntimeException the font is invalid.
     * @throws IOException the font file could not be read.
     */
    protected WindowsMetrics readOs2Table(int unitsPerEm) throws IOException {
        int table_location[];
        table_location = tables.get("OS/2");
        if (table_location == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("OS/2", fileName + style);
        }
        WindowsMetrics os_2 = new WindowsMetrics();
        raf.seek(table_location[0]);
        int version = raf.readUnsignedShort();
        os_2.xAvgCharWidth = raf.readShort();
        os_2.usWeightClass = raf.readUnsignedShort();
        os_2.usWidthClass = raf.readUnsignedShort();
        os_2.fsType = raf.readShort();
        os_2.ySubscriptXSize = raf.readShort();
        os_2.ySubscriptYSize = raf.readShort();
        os_2.ySubscriptXOffset = raf.readShort();
        os_2.ySubscriptYOffset = raf.readShort();
        os_2.ySuperscriptXSize = raf.readShort();
        os_2.ySuperscriptYSize = raf.readShort();
        os_2.ySuperscriptXOffset = raf.readShort();
        os_2.ySuperscriptYOffset = raf.readShort();
        os_2.yStrikeoutSize = raf.readShort();
        os_2.yStrikeoutPosition = raf.readShort();
        os_2.sFamilyClass = raf.readShort();
        raf.readFully(os_2.panose);
        raf.skipBytes(16);
        raf.readFully(os_2.achVendID);
        os_2.fsSelection = raf.readUnsignedShort();
        os_2.usFirstCharIndex = raf.readUnsignedShort();
        os_2.usLastCharIndex = raf.readUnsignedShort();
        os_2.sTypoAscender = raf.readShort();
        os_2.sTypoDescender = raf.readShort();
        if (os_2.sTypoDescender > 0) {
            os_2.sTypoDescender = (short) -os_2.sTypoDescender;
        }
        os_2.sTypoLineGap = raf.readShort();
        os_2.usWinAscent = raf.readUnsignedShort();
        os_2.usWinDescent = raf.readUnsignedShort();
        os_2.ulCodePageRange1 = 0;
        os_2.ulCodePageRange2 = 0;
        if (version > 0) {
            os_2.ulCodePageRange1 = raf.readInt();
            os_2.ulCodePageRange2 = raf.readInt();
        }
        if (version > 1) {
            raf.skipBytes(2);
            os_2.sCapHeight = raf.readShort();
        } else {
            os_2.sCapHeight = (int) (0.7 * unitsPerEm);
        }
        return os_2;
    }

    /**
     * Reads the glyphs widths. The widths are extracted from the table 'hmtx'.
     * The glyphs are normalized to 1000 units.
     * @param numberOfHMetrics {@code hhea.numberOfHMetrics} property, {@see HorizontalHeader}.
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see FontHeader}.
     * @throws PdfRuntimeException the font is invalid.
     * @throws IOException the font file could not be read.
     */
    protected int[] readGlyphWidths(int numberOfHMetrics, int unitsPerEm) throws IOException {
        int table_location[];
        table_location = tables.get("hmtx");
        if (table_location == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("hmtx", fileName + style);
        }
        raf.seek(table_location[0]);
        glyphWidthsByIndex = new int[numberOfHMetrics];
        for (int k = 0; k < numberOfHMetrics; ++k) {
            glyphWidthsByIndex[k] = raf.readUnsignedShort() * 1000 / unitsPerEm;
            @SuppressWarnings("unused")
            int leftSideBearing = raf.readShort() * 1000 / unitsPerEm;
        }
        return glyphWidthsByIndex;
    }

    /**
     * Reads the kerning information from the 'kern' table.
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see FontHeader}.
     * @throws IOException the font file could not be read
     */
    protected IntHashtable readKerning(int unitsPerEm) throws IOException {
        int table_location[];
        table_location = tables.get("kern");
        IntHashtable kerning = new IntHashtable();
        if (table_location == null) {
            return kerning;
        }
        raf.seek(table_location[0] + 2);
        int nTables = raf.readUnsignedShort();
        int checkpoint = table_location[0] + 4;
        int length = 0;
        for (int k = 0; k < nTables; k++) {
            checkpoint += length;
            raf.seek(checkpoint);
            raf.skipBytes(2);
            length = raf.readUnsignedShort();
            int coverage = raf.readUnsignedShort();
            if ((coverage & 0xfff7) == 0x0001) {
                int nPairs = raf.readUnsignedShort();
                raf.skipBytes(6);
                for (int j = 0; j < nPairs; ++j) {
                    int pair = raf.readInt();
                    int value = raf.readShort() * 1000 / unitsPerEm;
                    kerning.put(pair, value);
                }
            }
        }
        return kerning;
    }

    /**
     * Read the glyf bboxes from 'glyf' table.
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see FontHeader}.
     * @throws PdfRuntimeException the font is invalid.
     * @throws IOException the font file could not be read.
     */
    protected int[][] readBbox(int unitsPerEm) throws IOException {
        int tableLocation[];
        tableLocation = tables.get("head");
        if (tableLocation == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("head", fileName + style);
        }
        raf.seek(tableLocation[0] + FontConstants.HEAD_LOCA_FORMAT_OFFSET);
        boolean locaShortTable = raf.readUnsignedShort() == 0;
        tableLocation = tables.get("loca");
        if (tableLocation == null) {
            return null;
        }
        raf.seek(tableLocation[0]);
        int locaTable[];
        if (locaShortTable) {
            int entries = tableLocation[1] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = raf.readUnsignedShort() * 2;
            }
        } else {
            int entries = tableLocation[1] / 4;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = raf.readInt();
            }
        }
        tableLocation = tables.get("glyf");
        if (tableLocation == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("glyf", fileName + style);
        }
        int tableGlyphOffset = tableLocation[0];
        int[][] bboxes = new int[locaTable.length - 1][];
        for (int glyph = 0; glyph < locaTable.length - 1; ++glyph) {
            int start = locaTable[glyph];
            if (start != locaTable[glyph + 1]) {
                raf.seek(tableGlyphOffset + start + 2);
                bboxes[glyph] = new int[] {
                        raf.readShort() * 1000 / unitsPerEm,
                        raf.readShort() * 1000 / unitsPerEm,
                        raf.readShort() * 1000 / unitsPerEm,
                        raf.readShort() * 1000 / unitsPerEm
                };
            }
        }
        return bboxes;
    }

    protected PostTable readPostTable() throws IOException {
        int[] table_location = tables.get("post");
        if (table_location != null) {
            raf.seek(table_location[0] + 4);
            short mantissa = raf.readShort();
            int fraction = raf.readUnsignedShort();
            PostTable post = new PostTable();
            post.italicAngle = mantissa + fraction / 16384.0d;
            post.underlinePosition = raf.readShort();
            post.underlineThickness = raf.readShort();
            post.isFixedPitch = raf.readInt() != 0;
            return post;
        } else {
            return null;
        }
    }

    protected int readMaxGlyphId() throws IOException {
        int[] table_location = tables.get("maxp");
        if (table_location == null) {
            return 65536;
        } else {
            raf.seek(table_location[0] + 4);
            return raf.readUnsignedShort();
        }
    }

    /** Reads the several maps from the table 'cmap'. The maps of interest are 1.0 for symbolic
     *  fonts and 3.1 for all others. A symbolic font is defined as having the map 3.0.
     * @throws PdfRuntimeException the font is invalid
     * @throws IOException the font file could not be read
     */
    protected Cmaps readCMaps() throws IOException {
        int table_location[];
        table_location = tables.get("cmap");
        if (table_location == null)
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("cmap", fileName + style);
        raf.seek(table_location[0]);
        raf.skipBytes(2);
        int num_tables = raf.readUnsignedShort();
        int map10 = 0;
        int map31 = 0;
        int map30 = 0;
        int mapExt = 0;
        Cmaps cmaps = new Cmaps();
        for (int k = 0; k < num_tables; ++k) {
            int platId = raf.readUnsignedShort();
            int platSpecId = raf.readUnsignedShort();
            int offset = raf.readInt();
            if (platId == 3 && platSpecId == 0) {
                cmaps.fontSpecific = true;
                map30 = offset;
            } else if (platId == 3 && platSpecId == 1) {
                map31 = offset;
            } else if (platId == 3 && platSpecId == 10) {
                mapExt = offset;
            } else if (platId == 1 && platSpecId == 0) {
                map10 = offset;
            }
        }
        if (map10 > 0) {
            raf.seek(table_location[0] + map10);
            int format = raf.readUnsignedShort();
            switch (format) {
                case 0:
                    cmaps.cmap10 = readFormat0();
                    break;
                case 4:
                    cmaps.cmap10 = readFormat4(cmaps.fontSpecific);
                    break;
                case 6:
                    cmaps.cmap10 = readFormat6();
                    break;
            }
        }
        if (map31 > 0) {
            raf.seek(table_location[0] + map31);
            int format = raf.readUnsignedShort();
            if (format == 4) {
                cmaps.cmap31 = readFormat4(cmaps.fontSpecific);
            }
        }
        if (map30 > 0) {
            raf.seek(table_location[0] + map30);
            int format = raf.readUnsignedShort();
            if (format == 4) {
                cmaps.cmap10 = readFormat4(cmaps.fontSpecific);
            }
        }
        if (mapExt > 0) {
            raf.seek(table_location[0] + mapExt);
            int format = raf.readUnsignedShort();
            switch (format) {
                case 0:
                    cmaps.cmapExt = readFormat0();
                    break;
                case 4:
                    cmaps.cmapExt = readFormat4(cmaps.fontSpecific);
                    break;
                case 6:
                    cmaps.cmapExt = readFormat6();
                    break;
                case 12:
                    cmaps.cmapExt = readFormat12();
                    break;
            }
        }
        return cmaps;
    }

    /**
     * Gets the Postscript font name.
     * @throws PdfRuntimeException the font is invalid
     * @throws IOException the font file could not be read
     * @return the Postscript font name
     */
    private String getBaseFont() throws IOException {
        int table_location[];
        table_location = tables.get("name");
        if (table_location == null) {
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("name", fileName + style);
        }
        raf.seek(table_location[0] + 2);
        int numRecords = raf.readUnsignedShort();
        int startOfStorage = raf.readUnsignedShort();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = raf.readUnsignedShort();
            @SuppressWarnings("unused")
            int platformEncodingID = raf.readUnsignedShort();
            @SuppressWarnings("unused")
            int languageID = raf.readUnsignedShort();
            int nameID = raf.readUnsignedShort();
            int length = raf.readUnsignedShort();
            int offset = raf.readUnsignedShort();
            if (nameID == 6) {
                raf.seek(table_location[0] + startOfStorage + offset);
                if (platformID == 0 || platformID == 3) {
                    return readUnicodeString(length);
                } else {
                    return readStandardString(length);
                }
            }
        }
        File file = new File(fileName);
        return file.getName().replace(' ', '-');
    }

    /** Extracts the names of the font in all the languages available.
     * @param id the name id to retrieve
     * @throws PdfRuntimeException on error
     * @throws IOException on error
     */
    private String[][] getNames(int id) throws IOException {
        int table_location[];
        table_location = tables.get("name");
        if (table_location == null)
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("name", fileName + style);
        raf.seek(table_location[0] + 2);
        int numRecords = raf.readUnsignedShort();
        int startOfStorage = raf.readUnsignedShort();
        ArrayList<String[]> names = new ArrayList<String[]>();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = raf.readUnsignedShort();
            int platformEncodingID = raf.readUnsignedShort();
            int languageID = raf.readUnsignedShort();
            int nameID = raf.readUnsignedShort();
            int length = raf.readUnsignedShort();
            int offset = raf.readUnsignedShort();
            if (nameID == id) {
                int pos = (int)raf.getPosition();
                raf.seek(table_location[0] + startOfStorage + offset);
                String name;
                if (platformID == 0 || platformID == 3 || platformID == 2 && platformEncodingID == 1){
                    name = readUnicodeString(length);
                }
                else {
                    name = readStandardString(length);
                }
                names.add(new String[]{String.valueOf(platformID),
                        String.valueOf(platformEncodingID), String.valueOf(languageID), name});
                raf.seek(pos);
            }
        }
        String thisName[][] = new String[names.size()][];
        for (int k = 0; k < names.size(); ++k)
            thisName[k] = names.get(k);
        return thisName;
    }

    /** Extracts all the names of the names-Table
     * @throws PdfRuntimeException on error
     * @throws IOException on error
     */
    private String[][] getAllNames() throws IOException {
        int table_location[];
        table_location = tables.get("name");
        if (table_location == null)
            throw new PdfRuntimeException("table.1.does.not.exist.in.2").setMessageParams("name", fileName + style);
        raf.seek(table_location[0] + 2);
        int numRecords = raf.readUnsignedShort();
        int startOfStorage = raf.readUnsignedShort();
        ArrayList<String[]> names = new ArrayList<String[]>();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = raf.readUnsignedShort();
            int platformEncodingID = raf.readUnsignedShort();
            int languageID = raf.readUnsignedShort();
            int nameID = raf.readUnsignedShort();
            int length = raf.readUnsignedShort();
            int offset = raf.readUnsignedShort();
            int pos = (int)raf.getPosition();
            raf.seek(table_location[0] + startOfStorage + offset);
            String name;
            if (platformID == 0 || platformID == 3 || platformID == 2 && platformEncodingID == 1){
                name = readUnicodeString(length);
            }
            else {
                name = readStandardString(length);
            }
            names.add(new String[]{String.valueOf(nameID), String.valueOf(platformID),
                    String.valueOf(platformEncodingID), String.valueOf(languageID), name});
            raf.seek(pos);
        }
        String thisName[][] = new String[names.size()][];
        for (int k = 0; k < names.size(); ++k)
            thisName[k] = names.get(k);
        return thisName;
    }

    /**
     * Reads a Unicode <CODE>String</CODE> from the font file. Each character is represented by two bytes.
     * @param length the length of bytes to read. The <CODE>String</CODE> will have <CODE>length</CODE>/2 characters.
     * @return the <CODE>String</CODE> read.
     * @throws IOException the font file could not be read.
     */
    private String readUnicodeString(int length) throws IOException {
        StringBuilder buf = new StringBuilder();
        length /= 2;
        for (int k = 0; k < length; ++k) {
            buf.append(raf.readChar());
        }
        return buf.toString();
    }

    /** Gets a glyph width.
     * @param glyph the glyph to get the width of
     * @return the width of the glyph in normalized 1000 units
     */
    protected int getGlyphWidth(int glyph) {
        if (glyph >= glyphWidthsByIndex.length)
            glyph = glyphWidthsByIndex.length - 1;
        return glyphWidthsByIndex[glyph];
    }

    /** The information in the maps of the table 'cmap' is coded in several formats.
     *  Format 0 is the Apple standard character to glyph index mapping table.
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws IOException the font file could not be read
     */
    private HashMap<Integer, int[]> readFormat0() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        raf.skipBytes(4);
        for (int k = 0; k < 256; ++k) {
            int r[] = new int[2];
            r[0] = raf.readUnsignedByte();
            r[1] = getGlyphWidth(r[0]);
            h.put(k, r);
        }
        return h;
    }

    /** The information in the maps of the table 'cmap' is coded in several formats.
     *  Format 4 is the Microsoft standard character to glyph index mapping table.
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws IOException the font file could not be read
     */
    private HashMap<Integer, int[]> readFormat4(boolean fontSpecific) throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        int table_lenght = raf.readUnsignedShort();
        raf.skipBytes(2);
        int segCount = raf.readUnsignedShort() / 2;
        raf.skipBytes(6);
        int endCount[] = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            endCount[k] = raf.readUnsignedShort();
        }
        raf.skipBytes(2);
        int startCount[] = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            startCount[k] = raf.readUnsignedShort();
        }
        int idDelta[] = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            idDelta[k] = raf.readUnsignedShort();
        }
        int idRO[] = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            idRO[k] = raf.readUnsignedShort();
        }
        int glyphId[] = new int[table_lenght / 2 - 8 - segCount * 4];
        for (int k = 0; k < glyphId.length; ++k) {
            glyphId[k] = raf.readUnsignedShort();
        }
        for (int k = 0; k < segCount; ++k) {
            int glyph;
            for (int j = startCount[k]; j <= endCount[k] && j != 0xFFFF; ++j) {
                if (idRO[k] == 0) {
                    glyph = j + idDelta[k] & 0xFFFF;
                }
                else {
                    int idx = k + idRO[k] / 2 - segCount + j - startCount[k];
                    if (idx >= glyphId.length)
                        continue;
                    glyph = glyphId[idx] + idDelta[k] & 0xFFFF;
                }
                int r[] = new int[2];
                r[0] = glyph;
                r[1] = getGlyphWidth(r[0]);
                h.put(fontSpecific ? ((j & 0xff00) == 0xf000 ? j & 0xff : j) : j, r);
            }
        }
        return h;
    }

    /** The information in the maps of the table 'cmap' is coded in several formats.
     *  Format 6 is a trimmed table mapping. It is similar to format 0 but can have
     *  less than 256 entries.
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws IOException the font file could not be read
     */
    private HashMap<Integer, int[]> readFormat6() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        raf.skipBytes(4);
        int start_code = raf.readUnsignedShort();
        int code_count = raf.readUnsignedShort();
        for (int k = 0; k < code_count; ++k) {
            int r[] = new int[2];
            r[0] = raf.readUnsignedShort();
            r[1] = getGlyphWidth(r[0]);
            h.put(k + start_code, r);
        }
        return h;
    }

    private HashMap<Integer, int[]> readFormat12() throws IOException {
        HashMap<Integer, int[]> h = new HashMap<Integer, int[]>();
        raf.skipBytes(2);
        @SuppressWarnings("unused")
        int table_length = raf.readInt();
        raf.skipBytes(4);
        int nGroups = raf.readInt();
        for (int k = 0; k < nGroups; k++) {
            int startCharCode = raf.readInt();
            int endCharCode = raf.readInt();
            int startGlyphID = raf.readInt();
            for (int i = startCharCode; i <= endCharCode; i++) {
                int[] r = new int[2];
                r[0] = startGlyphID;
                r[1] = getGlyphWidth(r[0]);
                h.put(i, r);
                startGlyphID++;
            }
        }
        return h;
    }
}
