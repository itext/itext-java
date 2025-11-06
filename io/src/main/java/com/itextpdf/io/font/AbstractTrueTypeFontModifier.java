/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.font;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The abstract class which provides a functionality to modify TrueType fonts with returning raw data of modified font.
 */
abstract class AbstractTrueTypeFontModifier {
    // If it's a regular font subset, we should not add `name` and `post`,
    // because information in these tables maybe irrelevant for a subset.
    private static final String[] TABLE_NAMES_SUBSET =
            {"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep", "cmap", "OS/2"};
    // In case ttc (true type collection) file with subset = false (#directoryOffset > 0)
    // `name` and `post` shall be included, because it's actually a full font.
    private static final String[] TABLE_NAMES =
            {"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep", "cmap", "OS/2", "name", "post"};
    private static final int[] ENTRY_SELECTORS = {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4};
    private static final int TABLE_CHECKSUM = 0;
    private static final int TABLE_OFFSET = 1;
    private static final int TABLE_LENGTH = 2;
    private static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    /**
     * Contains the location of the several tables. The key is the name of the table and the
     * value is an {@code int[3]} where position 0 is the checksum, position 1 is the offset
     * from the start of the file and position 2 is the length of the table.
     *
     * <p>
     * The length of the table can be any. But when writing data, it should be aligned to 4 bytes. Say, if table length
     * is 54 we should write 2 extra zeroes at the end, and it should be taken into account for calculating the start
     * offset of the next table (but not the length of the current table).
     */
    protected Map<String, int[]> tableDirectory;
    /**
     * Contains glyph data from `glyf` table. The key is the GID (glyph ID) and the value is a raw data of glyph.
     */
    protected Map<Integer, byte[]> glyphDataMap;
    /**
     * Contains font tables which have been changed during the modification. The key is the name of table which has
     * been changed and the value is raw data of the modified table.
     */
    protected final Map<String, byte[]> modifiedTables = new HashMap<>();

    /**
     * Font raw data on which new modified font will be built.
     */
    protected RandomAccessFileOrArray raf;
    /**
     * Table directory offset which corresponds to font's raw data from {@link #raf}.
     */
    protected int directoryOffset;
    /**
     * the name of font which will be modified
     */
    protected final String fontName;

    protected Map<Integer, byte[]> horizontalMetricMap;
    protected int numberOfHMetrics;

    private FontRawData outFont;
    private final String[] tableNames;

    /**
     * Instantiates a new {@link AbstractTrueTypeFontModifier} instance.
     *
     * @param fontName the name of font which will be modified
     * @param subsetTables whether subset tables (remove `name` and `post` tables) or not. It's used in case of ttc
     *                     (true type collection) font where single "full" font is needed. Despite the value of that
     *                     flag, only used glyphs will be left in the font
     */
    AbstractTrueTypeFontModifier(String fontName, boolean subsetTables) {
        // subset = false is possible with directoryOffset > 0, i.e. ttc font without subset.
        if (subsetTables) {
            tableNames = TABLE_NAMES_SUBSET;
        } else {
            tableNames = TABLE_NAMES;
        }
        this.fontName = fontName;
    }

    Tuple2<Integer, byte[]> process() throws java.io.IOException {
        try {
            createTableDirectory();
            final int numberOfGlyphs = mergeTables();
            assembleFont();
            return new Tuple2<>(numberOfGlyphs, outFont.getData());
        } finally {
            try {
                raf.close();
            } catch (Exception ignore) {
            }
        }
    }

    abstract int mergeTables() throws java.io.IOException;

    protected int createModifiedTables() throws java.io.IOException {
        int[] activeGlyphs = new int[glyphDataMap.size()];
        int i = 0;
        int glyfSize = 0;
        for (Map.Entry<Integer, byte[]> entry : glyphDataMap.entrySet()) {
            activeGlyphs[i++] = entry.getKey();
            glyfSize += entry.getValue().length;
        }
        Arrays.sort(activeGlyphs);
        final int maxGlyphId = activeGlyphs[activeGlyphs.length - 1];

        // Update loca and glyf tables

        // If the biggest used GID is X, size of loca should be X + 1 (array index starts from 0),
        // plus one extra entry to get size of X element (loca[X + 1] - loca[X]), it's why 2 added
        final int locaSize = maxGlyphId + 2;
        final boolean isLocaShortTable = isLocaShortTable();
        final int newLocaTableSize = isLocaShortTable ? locaSize * 2 : locaSize * 4;
        byte[] newLoca = new byte[newLocaTableSize];
        byte[] newGlyf = new byte[glyfSize];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int k = 0; k < locaSize; ++k) {
            writeToLoca(newLoca, k, glyfPtr, isLocaShortTable);

            if (listGlyf < activeGlyphs.length && activeGlyphs[listGlyf] == k) {
                ++listGlyf;

                byte[] glyphData = glyphDataMap.get(k);
                System.arraycopy(glyphData, 0, newGlyf, glyfPtr, glyphData.length);
                glyfPtr += glyphData.length;
            }
        }
        modifiedTables.put("glyf", newGlyf);
        modifiedTables.put("loca", newLoca);

        // Update maxp table
        int[] tableLocation = tableDirectory.get("maxp");
        raf.seek(tableLocation[TABLE_OFFSET]);
        byte[] maxp = new byte[tableLocation[TABLE_LENGTH]];
        raf.read(maxp);
        writeShortToTable(maxp, 2, maxGlyphId + 1);
        modifiedTables.put("maxp", maxp);

        // Merging vertical fonts aren't supported yet, it's why vmtx and vhea tables ignored

        // Update hhea table
        // numberOfHMetrics can't be increased because there are no advanced width data. So only
        // numberOfHMetrics decreasing is allowed. See createNewHorizontalMetricsTable method for more info.
        if (numberOfHMetrics > maxGlyphId + 1) {
            int[] hheaTableLocation = tableDirectory.get("hhea");
            raf.seek(hheaTableLocation[TABLE_OFFSET]);
            byte[] hhea = new byte[hheaTableLocation[TABLE_LENGTH]];
            raf.read(hhea);
            writeShortToTable(hhea, 17, maxGlyphId + 1);
            modifiedTables.put("hhea", hhea);
        }

        // Update hmtx table
        byte[] newHtmx = createNewHorizontalMetricsTable(maxGlyphId);
        modifiedTables.put("hmtx", newHtmx);

        return maxGlyphId + 1;
    }

    /**
     * Creates new hmtx table.
     *
     * <p>
     * hmtx is a "hybrid" table, it has maxp.numGlyphs indices, for (indices <= hhea.numberOfHMetrics) 4 bytes (advanced
     * width + left side bearing), for (hhea.numberOfHMetrics < indices <= maxp.numGlyphs) 2 bytes (left side bearing).
     *
     * @param maxGlyphId the max glyph id
     * @return raw data of new hmtx table
     */
    private byte[] createNewHorizontalMetricsTable(int maxGlyphId) throws java.io.IOException {
        int[] tableLocation = tableDirectory.get("hmtx");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        raf.seek(tableLocation[TABLE_OFFSET]);
        for (int k = 0; k < numberOfHMetrics; ++k) {
            if (k > maxGlyphId) {
                break;
            }
            if (horizontalMetricMap.containsKey(k)) {
                raf.skipBytes(4);
                baos.write(horizontalMetricMap.get(k));
            } else {
                baos.write(raf.readByte());
                baos.write(raf.readByte());
                baos.write(raf.readByte());
                baos.write(raf.readByte());
            }
        }

        // The 2nd part of hmtx. Only left side bearings
        for (int k = numberOfHMetrics; k <= maxGlyphId; ++k) {
            if (horizontalMetricMap.containsKey(k)) {
                baos.write(horizontalMetricMap.get(k));
            } else {
                baos.write(new byte[] {0, 0});
            }
        }
        return baos.toByteArray();
    }

    private void createTableDirectory() throws java.io.IOException {
        tableDirectory = new HashMap<>();
        raf.seek(directoryOffset);
        int id = raf.readInt();
        if (id != 0x00010000) {
            throw new IOException(IoExceptionMessageConstant.NOT_AT_TRUE_TYPE_FILE).setMessageParams(fontName);
        }
        int numTables = raf.readUnsignedShort();
        raf.skipBytes(6);
        for (int k = 0; k < numTables; ++k) {
            String tag = readTag();
            int[] tableLocation = new int[3];
            tableLocation[TABLE_CHECKSUM] = raf.readInt();
            tableLocation[TABLE_OFFSET] = raf.readInt();
            tableLocation[TABLE_LENGTH] = raf.readInt();
            tableDirectory.put(tag, tableLocation);
        }
    }

    private boolean isLocaShortTable() throws java.io.IOException {
        int[] tableLocation = tableDirectory.get("head");
        if (tableLocation == null) {
            throw new IOException(IoExceptionMessageConstant.TABLE_DOES_NOT_EXISTS_IN).setMessageParams("head",
                    fontName);
        }
        raf.seek(tableLocation[TABLE_OFFSET] + HEAD_LOCA_FORMAT_OFFSET);
        return raf.readUnsignedShort() == 0;
    }

    private void assembleFont() throws java.io.IOException {
        int[] tableLocation;
        // Calculate size of the out font
        int fullFontSize = 0;
        int tablesUsed = modifiedTables.size();
        for (String name : tableNames) {
            if (modifiedTables.containsKey(name)) {
                continue;
            }
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            tablesUsed++;
            // + 3 & ~3 here and further is to align to 4 bytes
            fullFontSize += tableLocation[TABLE_LENGTH] + 3 & ~3;
        }
        for (byte[] table : modifiedTables.values()) {
            fullFontSize += table.length + 3 & ~3;
        }
        int reference = 16 * tablesUsed + 12;
        fullFontSize += reference;
        outFont = new FontRawData(fullFontSize);
        // Write font headers + tables directory
        outFont.writeFontInt(0x00010000);
        outFont.writeFontShort(tablesUsed);
        int selector = ENTRY_SELECTORS[tablesUsed];
        outFont.writeFontShort((1 << selector) * 16);
        outFont.writeFontShort(selector);
        outFont.writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (String name : tableNames) {
            int len;
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            outFont.writeFontString(name);
            if (modifiedTables.containsKey(name)) {
                byte[] table = modifiedTables.get(name);
                outFont.writeFontInt(calculateChecksum(table));
                len = table.length;
            } else {
                outFont.writeFontInt(tableLocation[TABLE_CHECKSUM]);
                len = tableLocation[TABLE_LENGTH];
            }
            outFont.writeFontInt(reference);
            outFont.writeFontInt(len);
            reference += len + 3 & ~3;
        }
        // Write tables data
        for (String name : tableNames) {
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            if (modifiedTables.containsKey(name)) {
                byte[] table = modifiedTables.get(name);
                outFont.writeFontTable(table);
            } else {
                raf.seek(tableLocation[TABLE_OFFSET]);
                outFont.writeFontTable(raf, tableLocation[TABLE_LENGTH]);
            }
        }
    }

    private String readTag() throws java.io.IOException {
        byte[] buf = new byte[4];
        raf.readFully(buf);
        try {
            return new String(buf, PdfEncodings.WINANSI);
        } catch (Exception e) {
            throw new IOException("TrueType font", e);
        }
    }

    private static void writeToLoca(byte[] loca, int index, int location, boolean isLocaShortTable) {
        if (isLocaShortTable) {
            index *= 2;
            location /= 2;
            loca[index] = (byte) (location >> 8);
            loca[index + 1] = (byte) location;
        }
        else {
            index *= 4;
            loca[index] = (byte) (location >> 24);
            loca[index + 1] = (byte) (location >> 16);
            loca[index + 2] = (byte) (location >> 8);
            loca[index + 3] = (byte) location;
        }
    }

    private static void writeShortToTable(byte[] table, int index, int data) {
        // 2 bytes per field
        index *= 2;
        table[index] = (byte) (data >> 8);
        table[index + 1] = (byte) data;
    }

    private int calculateChecksum(byte[] b) {
        int len = b.length / 4;
        int v0 = 0;
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int ptr = 0;
        for (int k = 0; k < len; ++k) {
            v3 += b[ptr++] & 0xff;
            v2 += b[ptr++] & 0xff;
            v1 += b[ptr++] & 0xff;
            v0 += b[ptr++] & 0xff;
        }
        return v0 + (v1 << 8) + (v2 << 16) + (v3 << 24);
    }

    private static class FontRawData {
        private final byte[] data;
        private int ptr;

        FontRawData(int size) {
            this.data = new  byte[size];
            this.ptr = 0;
        }

        public byte[] getData() {
            return data;
        }

        void writeFontTable(RandomAccessFileOrArray raf, int tableLength) throws java.io.IOException {
            raf.readFully(data, ptr, tableLength);
            ptr += tableLength + 3 & ~3;
        }

        void writeFontTable(byte[] tableData) {
            System.arraycopy(tableData, 0, data, ptr, tableData.length);
            ptr += tableData.length + 3 & ~3;
        }

        void writeFontShort(int n) {
            data[ptr++] = (byte) (n >> 8);
            data[ptr++] = (byte) n;
        }

        void writeFontInt(int n) {
            data[ptr++] = (byte) (n >> 24);
            data[ptr++] = (byte) (n >> 16);
            data[ptr++] = (byte) (n >> 8);
            data[ptr++] = (byte) n;
        }

        void writeFontString(String s) {
            byte[] b = PdfEncodings.convertToBytes(s, PdfEncodings.WINANSI);
            System.arraycopy(b, 0, data, ptr, b.length);
            ptr += b.length;
        }
    }
}
