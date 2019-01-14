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
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.IntHashtable;

import java.io.Closeable;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class OpenTypeParser implements Serializable, Closeable {

    private static final long serialVersionUID = 3399061674525229738L;

    private static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    /**
     * The components of table 'head'.
     */
    static class HeaderTable implements Serializable {
        private static final long serialVersionUID = 5849907401352439751L;
        int flags;
        int unitsPerEm;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        int macStyle;
    }

    /**
     * The components of table 'hhea'.
     */
    static class HorizontalHeader implements Serializable {
        private static final long serialVersionUID = -6857266170153679811L;
        short Ascender;
        short Descender;
        short LineGap;
        int advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        int numberOfHMetrics;
    }

    /**
     * The components of table 'OS/2'.
     */
    static class WindowsMetrics implements Serializable{
        private static final long serialVersionUID = -9117114979326346658L;
        short xAvgCharWidth;
        int usWeightClass;
        int usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubscriptXOffset;
        short ySubscriptYOffset;
        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperscriptXOffset;
        short ySuperscriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        byte[] panose = new byte[10];
        byte[] achVendID = new byte[4];
        int fsSelection;
        int usFirstCharIndex;
        int usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        int usWinAscent;
        int usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        int sxHeight;
        int sCapHeight;
    }

    static class PostTable implements Serializable {
        private static final long serialVersionUID = 5735677308357646890L;
        /**
         * The italic angle. It is usually extracted from the 'post' table or in it's
         * absence with the code:
         * <PRE>
         * {@code -Math.atan2(hhea.caretSlopeRun, hhea.caretSlopeRise) * 180 / Math.PI}
         * </PRE>
         */
        float italicAngle;
        int underlinePosition;
        int underlineThickness;
        /**
         * <CODE>true</CODE> if all the glyphs have the same width.
         */
        boolean isFixedPitch;
    }

    static class CmapTable implements Serializable {
        private static final long serialVersionUID = 8923883989692194983L;
        /**
         * The map containing the code information for the table 'cmap', encoding 1.0.
         * The key is the code and the value is an {@code int[2]} where position 0
         * is the glyph number and position 1 is the glyph width normalized to 1000 units.
         * @see TrueTypeFont#UNITS_NORMALIZATION
         */
        Map<Integer, int[]> cmap10;
        /**
         * The map containing the code information for the table 'cmap', encoding 3.1 in Unicode.
         * The key is the code and the value is an {@code int[2]} where position 0
         * is the glyph number and position 1 is the glyph width normalized to 1000 units.
         * @see TrueTypeFont#UNITS_NORMALIZATION
         */
        Map<Integer, int[]> cmap31;
        Map<Integer, int[]> cmapExt;
        boolean fontSpecific = false;
    }

    /** The file name. */
    protected String fileName;
    /**
     * The file in use.
     */
    protected RandomAccessFileOrArray raf;
    /**
     * The index for the TTC font. It is -1 {@code int} for a TTF file.
     */
    protected int ttcIndex = -1;
    /**
     * The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font.
     */
    protected int directoryOffset;
    /**
     * The font name. This name is usually extracted from the table 'name' with the 'Name ID' 6.
     */
    protected String fontName;
    /**
     * All the names of the Names-Table.
     */
    protected Map<Integer, List<String[]>> allNameEntries;

    /**
     * Indicate, that the font contains 'CFF ' table.
     */
    protected boolean cff = false;
    /**
     * Offset to 'CFF ' table.
     */
    protected int cffOffset;
    /**
     * Length of 'CFF ' table.
     */
    protected int cffLength;

    private int[] glyphWidthsByIndex;

    protected HeaderTable head;
    protected HorizontalHeader hhea;
    protected WindowsMetrics os_2;
    protected PostTable post;
    protected CmapTable cmaps;

    /**
     * Contains the location of the several tables. The key is the name of
     * the table and the value is an <CODE>int[2]</CODE> where position 0
     * is the offset from the start of the file and position 1 is the length
     * of the table.
     */
    protected Map<String, int[]> tables;

    public OpenTypeParser(byte[] ttf) throws java.io.IOException {
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(ttf));
        initializeSfntTables();
    }

    public OpenTypeParser(byte[] ttc, int ttcIndex) throws java.io.IOException {
        this.ttcIndex = ttcIndex;
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(ttc));
        initializeSfntTables();
    }

    public OpenTypeParser(String ttcPath, int ttcIndex) throws java.io.IOException {
        this.ttcIndex = ttcIndex;
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(ttcPath));
        initializeSfntTables();
    }

    public OpenTypeParser(String name) throws java.io.IOException {
        String ttcName = getTTCName(name);
        this.fileName = ttcName;
        if (ttcName.length() < name.length()) {
            ttcIndex = Integer.parseInt(name.substring(ttcName.length() + 1));
        }
        raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(fileName));
        initializeSfntTables();
    }

    /**
     * Gets the Postscript font name.
     */
    public String getPsFontName() {
        if (fontName == null) {
            List<String[]> names = allNameEntries.get(6);
            if (names != null && names.size() > 0) {
                fontName = names.get(0)[3];
            } else {
                fontName = new File(fileName).getName().replace(' ', '-');
            }
        }
        return fontName;
    }

    public Map<Integer, List<String[]>> getAllNameEntries() {
        return allNameEntries;
    }

    public PostTable getPostTable() {
        return post;
    }

    public WindowsMetrics getOs_2Table() {
        return os_2;
    }

    public HorizontalHeader getHheaTable() {
        return hhea;
    }

    public HeaderTable getHeadTable() {
        return head;
    }

    public CmapTable getCmapTable() {
        return cmaps;
    }

    public int[] getGlyphWidthsByIndex() {
        return glyphWidthsByIndex;
    }

    public FontNames getFontNames() {
        FontNames fontNames = new FontNames();
        fontNames.setAllNames(getAllNameEntries());
        fontNames.setFontName(getPsFontName());
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
        fontNames.setFontWeight(os_2.usWeightClass);
        fontNames.setFontStretch(FontStretches.fromOpenTypeWidthClass(os_2.usWidthClass));
        fontNames.setMacStyle(head.macStyle);
        fontNames.setAllowEmbedding(os_2.fsType != 2);
        return fontNames;
    }

    public boolean isCff() {
        return cff;
    }

    public byte[] getFullFont() throws java.io.IOException {
        RandomAccessFileOrArray rf2 = null;
        try {
            rf2 = raf.createView();
            byte[] b = new byte[(int) rf2.length()];
            rf2.readFully(b);
            return b;
        } finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * If this font file is using the Compact Font File Format, then this method
     * will return the raw bytes needed for the font stream. If this method is
     * ever made public: make sure to add a test if (cff == true).
     *
     * @return a byte array
     */
    public byte[] readCffFont() throws java.io.IOException {
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
        } finally {
            try {
                if (rf2 != null) {
                    rf2.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) throws java.io.IOException {
        TrueTypeFontSubset sb = new TrueTypeFontSubset(fileName,
                raf.createView(), glyphs, directoryOffset, true, !subset);
        return sb.process();
    }

    @Override
    public void close() throws java.io.IOException {
        if (raf != null) {
            raf.close();
        }
        raf = null;
    }

    private void initializeSfntTables() throws java.io.IOException {
        tables = new LinkedHashMap<>();
        if (ttcIndex >= 0) {
            int dirIdx = ttcIndex;
            if (dirIdx < 0) {
                if (fileName != null) {
                    throw new IOException("The font index for {0} must be positive.").setMessageParams(fileName);
                } else {
                    throw new IOException("The font index must be positive.");
                }
            }
            String mainTag = readStandardString(4);
            if (!mainTag.equals("ttcf")) {
                if (fileName != null) {
                    throw new IOException("{0} is not a valid ttc file.").setMessageParams(fileName);
                } else {
                    throw new IOException("Not a valid ttc file.");
                }
            }
            raf.skipBytes(4);
            int dirCount = raf.readInt();
            if (dirIdx >= dirCount) {
                if (fileName != null) {
                    throw new IOException("The font index for {0} must be between 0 and {1}. It is {2}.")
                            .setMessageParams(fileName, dirCount - 1, dirIdx);
                } else {
                    throw new IOException("The font index must be between 0 and {0}. It is {1}.")
                            .setMessageParams(dirCount - 1, dirIdx);
                }
            }
            raf.skipBytes(dirIdx * 4);
            directoryOffset = raf.readInt();
        }
        raf.seek(directoryOffset);
        int ttId = raf.readInt();
        if (ttId != 0x00010000 && ttId != 0x4F54544F) {
            if (fileName != null) {
                throw new IOException("{0} is not a valid ttf or otf file.").setMessageParams(fileName);
            } else {
                throw new IOException("Not a valid ttf or otf file.");
            }
        }
        int num_tables = raf.readUnsignedShort();
        raf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            String tag = readStandardString(4);
            raf.skipBytes(4);
            int[] table_location = new int[2];
            table_location[0] = raf.readInt();
            table_location[1] = raf.readInt();
            tables.put(tag, table_location);
        }
    }

    /**
     * Reads the font data.
     * @param all if true, all tables will be read, otherwise only 'head', 'name', and 'os/2'.
     */
    protected void loadTables(boolean all) throws java.io.IOException {
        readNameTable();
        readHeadTable();
        readOs_2Table();
        readPostTable();
        if (all) {
            checkCff();
            readHheaTable();
            readGlyphWidths();
            readCmapTable();
        }
    }

    /**
     * Gets the name from a composed TTC file name.
     * If I have for input "myfont.ttc,2" the return will
     * be "myfont.ttc".
     *
     * @param name the full name
     * @return the simple file name
     */
    protected static String getTTCName(String name) {
        if (name == null) {
            return null;
        }
        int idx = name.toLowerCase().indexOf(".ttc,");
        if (idx < 0)
            return name;
        else
            return name.substring(0, idx + 4);
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
     * Reads the glyphs widths. The widths are extracted from the table 'hmtx'.
     * The glyphs are normalized to 1000 units (TrueTypeFont.UNITS_NORMALIZATION).
     * Depends from {@code hhea.numberOfHMetrics} property, {@see HorizontalHeader} and
     * {@code head.unitsPerEm} property, {@see HeaderTable}.
     *
     * @throws java.io.IOException the font file could not be read.
     */
    protected void readGlyphWidths() throws java.io.IOException {
        int numberOfHMetrics = hhea.numberOfHMetrics;
        int unitsPerEm = head.unitsPerEm;
        int table_location[];
        table_location = tables.get("hmtx");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("hmtx", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("hmtx");
            }
        }
        glyphWidthsByIndex = new int[readNumGlyphs()];
        raf.seek(table_location[0]);
        for (int k = 0; k < numberOfHMetrics; ++k) {
            glyphWidthsByIndex[k] = raf.readUnsignedShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm;
            @SuppressWarnings("unused")
            int leftSideBearing = raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm;
        }
        // If the font is monospaced, only one entry need be in the array, but that entry is required.
        // The last entry applies to all subsequent glyphs.
        if (numberOfHMetrics > 0) {
            for (int k = numberOfHMetrics; k < glyphWidthsByIndex.length; k++) {
                glyphWidthsByIndex[k] = glyphWidthsByIndex[numberOfHMetrics - 1];
            }
        }
    }

    /**
     * Reads the kerning information from the 'kern' table.
     *
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see HeaderTable}.
     * @throws java.io.IOException the font file could not be read
     */
    protected IntHashtable readKerning(int unitsPerEm) throws java.io.IOException {
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
                    int value = raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm;
                    kerning.put(pair, value);
                }
            }
        }
        return kerning;
    }

    /**
     * Read the glyf bboxes from 'glyf' table.
     *
     * @param unitsPerEm {@code head.unitsPerEm} property, {@see HeaderTable}.
     * @throws IOException the font is invalid.
     * @throws java.io.IOException  the font file could not be read.
     */
    protected int[][] readBbox(int unitsPerEm) throws java.io.IOException {
        int tableLocation[];
        tableLocation = tables.get("head");
        if (tableLocation == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("head", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("head");
            }
        }
        raf.seek(tableLocation[0] + HEAD_LOCA_FORMAT_OFFSET);
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
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("glyf", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("glyf");
            }
        }
        int tableGlyphOffset = tableLocation[0];
        int[][] bboxes = new int[locaTable.length - 1][];
        for (int glyph = 0; glyph < locaTable.length - 1; ++glyph) {
            int start = locaTable[glyph];
            if (start != locaTable[glyph + 1]) {
                raf.seek(tableGlyphOffset + start + 2);
                bboxes[glyph] = new int[]{
                        raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm,
                        raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm,
                        raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm,
                        raf.readShort() * TrueTypeFont.UNITS_NORMALIZATION / unitsPerEm
                };
            }
        }
        return bboxes;
    }

    protected int readNumGlyphs() throws java.io.IOException {
        int[] table_location = tables.get("maxp");
        if (table_location == null) {
            return 65536;
        } else {
            raf.seek(table_location[0] + 4);
            return raf.readUnsignedShort();
        }
    }

    /**
     * Extracts the names of the font in all the languages available.
     *
     * @throws IOException on error
     * @throws java.io.IOException  on error
     */
    private void readNameTable() throws java.io.IOException {
        int[] table_location = tables.get("name");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("name", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("name");
            }
        }
        allNameEntries = new LinkedHashMap<>();
        raf.seek(table_location[0] + 2);
        int numRecords = raf.readUnsignedShort();
        int startOfStorage = raf.readUnsignedShort();
        for (int k = 0; k < numRecords; ++k) {
            int platformID = raf.readUnsignedShort();
            int platformEncodingID = raf.readUnsignedShort();
            int languageID = raf.readUnsignedShort();
            int nameID = raf.readUnsignedShort();
            int length = raf.readUnsignedShort();
            int offset = raf.readUnsignedShort();
            List<String[]> names;
            if (allNameEntries.containsKey(nameID)) {
                names = allNameEntries.get(nameID);
            } else {
                allNameEntries.put(nameID, names = new ArrayList<>());
            }
            int pos = (int) raf.getPosition();
            raf.seek(table_location[0] + startOfStorage + offset);
            String name;
            if (platformID == 0 || platformID == 3 || platformID == 2 && platformEncodingID == 1) {
                name = readUnicodeString(length);
            } else {
                name = readStandardString(length);
            }
            names.add(new String[]{
                    Integer.toString(platformID),
                    Integer.toString(platformEncodingID),
                    Integer.toString(languageID),
                    name
            });
            raf.seek(pos);
        }
    }

    /**
     * Read horizontal header, table 'hhea'.
     *
     * @throws IOException the font is invalid.
     * @throws java.io.IOException  the font file could not be read.
     */
    private void readHheaTable() throws java.io.IOException {
        int[] table_location = tables.get("hhea");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("hhea", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("hhea");
            }
        }
        raf.seek(table_location[0] + 4);
        hhea = new HorizontalHeader();
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
    }

    /**
     * Read font header, table 'head'.
     *
     * @throws IOException the font is invalid.
     * @throws java.io.IOException  the font file could not be read.
     */
    private void readHeadTable() throws java.io.IOException {
        int[] table_location = tables.get("head");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("head", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("head");
            }
        }
        raf.seek(table_location[0] + 16);
        head = new HeaderTable();
        head.flags = raf.readUnsignedShort();
        head.unitsPerEm = raf.readUnsignedShort();
        raf.skipBytes(16);
        head.xMin = raf.readShort();
        head.yMin = raf.readShort();
        head.xMax = raf.readShort();
        head.yMax = raf.readShort();
        head.macStyle = raf.readUnsignedShort();
    }

    /**
     * Reads the windows metrics table. The metrics are extracted from the table 'OS/2'.
     * Depends from {@code head.unitsPerEm} property, {@see HeaderTable}.
     *
     * @throws IOException the font is invalid.
     * @throws java.io.IOException  the font file could not be read.
     */
    private void readOs_2Table() throws java.io.IOException {
        int[] table_location = tables.get("OS/2");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("os/2", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("os/2");
            }
        }
        os_2 = new WindowsMetrics();
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
        if (os_2.usWinDescent > 0) {
            os_2.usWinDescent = (short) -os_2.usWinDescent;
        }
        os_2.ulCodePageRange1 = 0;
        os_2.ulCodePageRange2 = 0;
        if (version > 0) {
            os_2.ulCodePageRange1 = raf.readInt();
            os_2.ulCodePageRange2 = raf.readInt();
        }
        if (version > 1) {
            // todo os_2.sxHeight = raf.readShort();
            raf.skipBytes(2);
            os_2.sCapHeight = raf.readShort();
        } else {
            os_2.sCapHeight = (int) (0.7 * head.unitsPerEm);
        }
    }

    private void readPostTable() throws java.io.IOException {
        int[] table_location = tables.get("post");
        if (table_location != null) {
            raf.seek(table_location[0] + 4);
            short mantissa = raf.readShort();
            int fraction = raf.readUnsignedShort();
            post = new PostTable();
            post.italicAngle = (float) (mantissa + fraction / 16384.0d);
            post.underlinePosition = raf.readShort();
            post.underlineThickness = raf.readShort();
            post.isFixedPitch = raf.readInt() != 0;
        } else {
            post = new OpenTypeParser.PostTable();
            post.italicAngle = (float) (-Math.atan2(hhea.caretSlopeRun, hhea.caretSlopeRise) * 180 / Math.PI);
        }
    }

    /**
     * Reads the several maps from the table 'cmap'. The maps of interest are 1.0 for symbolic
     * fonts and 3.1 for all others. A symbolic font is defined as having the map 3.0.
     * Depends from {@code readGlyphWidths()}.
     *
     * @throws java.io.IOException the font file could not be read
     */
    private void readCmapTable() throws java.io.IOException {
        int[] table_location = tables.get("cmap");
        if (table_location == null) {
            if (fileName != null) {
                throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("cmap", fileName);
            } else {
                throw new IOException(IOException.TableDoesNotExist).setMessageParams("cmap");
            }
        }
        raf.seek(table_location[0]);
        raf.skipBytes(2);
        int num_tables = raf.readUnsignedShort();
        int map10 = 0;
        int map31 = 0;
        int map30 = 0;
        int mapExt = 0;
        cmaps = new CmapTable();
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
                    cmaps.cmap10 = readFormat4(false);
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
                cmaps.cmap31 = readFormat4(false);
            }
        }
        if (map30 > 0) {
            raf.seek(table_location[0] + map30);
            int format = raf.readUnsignedShort();
            if (format == 4) {
                cmaps.cmap10 = readFormat4(cmaps.fontSpecific);
            } else {
                cmaps.fontSpecific = false;
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
                    cmaps.cmapExt = readFormat4(false);
                    break;
                case 6:
                    cmaps.cmapExt = readFormat6();
                    break;
                case 12:
                    cmaps.cmapExt = readFormat12();
                    break;
            }
        }
    }

    /**
     * Reads a <CODE>String</CODE> from the font file as bytes using the Cp1252
     * encoding.
     *
     * @param length the length of bytes to read
     * @return the <CODE>String</CODE> read
     * @throws java.io.IOException the font file could not be read
     */
    private String readStandardString(int length) throws java.io.IOException {
        return raf.readString(length, PdfEncodings.WINANSI);
    }

    /**
     * Reads a Unicode <CODE>String</CODE> from the font file. Each character is represented by two bytes.
     *
     * @param length the length of bytes to read. The <CODE>String</CODE> will have <CODE>length</CODE>/2 characters.
     * @return the <CODE>String</CODE> read.
     * @throws java.io.IOException the font file could not be read.
     */
    private String readUnicodeString(int length) throws java.io.IOException {
        StringBuilder buf = new StringBuilder();
        length /= 2;
        for (int k = 0; k < length; ++k) {
            buf.append(raf.readChar());
        }
        return buf.toString();
    }

    /**
     * Gets a glyph width.
     *
     * @param glyph the glyph to get the width of
     * @return the width of the glyph in normalized 1000 units (TrueTypeFont.UNITS_NORMALIZATION)
     */
    protected int getGlyphWidth(int glyph) {
        if (glyph >= glyphWidthsByIndex.length)
            glyph = glyphWidthsByIndex.length - 1;
        return glyphWidthsByIndex[glyph];
    }

    /**
     * The information in the maps of the table 'cmap' is coded in several formats.
     * Format 0 is the Apple standard character to glyph index mapping table.
     *
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws java.io.IOException the font file could not be read
     */
    private Map<Integer, int[]> readFormat0() throws java.io.IOException {
        Map<Integer, int[]> h = new LinkedHashMap<>();
        raf.skipBytes(4);
        for (int k = 0; k < 256; ++k) {
            int[] r = new int[2];
            r[0] = raf.readUnsignedByte();
            r[1] = getGlyphWidth(r[0]);
            h.put(k, r);
        }
        return h;
    }

    /**
     * The information in the maps of the table 'cmap' is coded in several formats.
     * Format 4 is the Microsoft standard character to glyph index mapping table.
     *
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws java.io.IOException the font file could not be read
     */
    private Map<Integer, int[]> readFormat4(boolean fontSpecific) throws java.io.IOException {
        Map<Integer, int[]> h = new LinkedHashMap<>();
        int table_lenght = raf.readUnsignedShort();
        raf.skipBytes(2);
        int segCount = raf.readUnsignedShort() / 2;
        raf.skipBytes(6);
        int[] endCount = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            endCount[k] = raf.readUnsignedShort();
        }
        raf.skipBytes(2);
        int[] startCount = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            startCount[k] = raf.readUnsignedShort();
        }
        int[] idDelta = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            idDelta[k] = raf.readUnsignedShort();
        }
        int[] idRO = new int[segCount];
        for (int k = 0; k < segCount; ++k) {
            idRO[k] = raf.readUnsignedShort();
        }
        int[] glyphId = new int[table_lenght / 2 - 8 - segCount * 4];
        for (int k = 0; k < glyphId.length; ++k) {
            glyphId[k] = raf.readUnsignedShort();
        }
        for (int k = 0; k < segCount; ++k) {
            int glyph;
            for (int j = startCount[k]; j <= endCount[k] && j != 0xFFFF; ++j) {
                if (idRO[k] == 0) {
                    glyph = j + idDelta[k] & 0xFFFF;
                } else {
                    int idx = k + idRO[k] / 2 - segCount + j - startCount[k];
                    if (idx >= glyphId.length)
                        continue;
                    glyph = glyphId[idx] + idDelta[k] & 0xFFFF;
                }
                int[] r = new int[2];
                r[0] = glyph;
                r[1] = getGlyphWidth(r[0]);

                // (j & 0xff00) == 0xf000) means, that it is private area of unicode
                // So, in case symbol font (cmap 3/0) we add both char codes:
                // j & 0xff and j. It will simplify unicode conversion in TrueTypeFont
                if (fontSpecific && ((j & 0xff00) == 0xf000)) {
                    h.put(j & 0xff, r);
                }
                h.put(j, r);
            }
        }
        return h;
    }

    /**
     * The information in the maps of the table 'cmap' is coded in several formats.
     * Format 6 is a trimmed table mapping. It is similar to format 0 but can have
     * less than 256 entries.
     *
     * @return a <CODE>HashMap</CODE> representing this map
     * @throws java.io.IOException the font file could not be read
     */
    private Map<Integer, int[]> readFormat6() throws java.io.IOException {
        Map<Integer, int[]> h = new LinkedHashMap<>();
        raf.skipBytes(4);
        int start_code = raf.readUnsignedShort();
        int code_count = raf.readUnsignedShort();
        for (int k = 0; k < code_count; ++k) {
            int[] r = new int[2];
            r[0] = raf.readUnsignedShort();
            r[1] = getGlyphWidth(r[0]);
            h.put(k + start_code, r);
        }
        return h;
    }

    private Map<Integer, int[]> readFormat12() throws java.io.IOException {
        Map<Integer, int[]> h = new LinkedHashMap<>();
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
