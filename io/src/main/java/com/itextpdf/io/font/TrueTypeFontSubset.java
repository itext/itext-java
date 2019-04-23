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
import com.itextpdf.io.source.RandomAccessFileOrArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Subsets a True Type font by removing the unneeded glyphs from the font.
 *
 * @author Paulo Soares
 */
class TrueTypeFontSubset {

    // If it's a regular font subset, we should not add `name` and `post`,
    // because information in these tables maybe irrelevant for a subset.
    private static final String[] TABLE_NAMES_SUBSET = {"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep",
            "cmap", "OS/2"};
    // In case ttc file with subset = false (#directoryOffset > 0) `name` and `post` shall be included,
    // because it's actually a full font.
    private static final String[] TABLE_NAMES = {"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep",
            "cmap", "OS/2", "name", "post"};
    private static final int[] entrySelectors = {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4};
    private static final int TABLE_CHECKSUM = 0;
    private static final int TABLE_OFFSET = 1;
    private static final int TABLE_LENGTH = 2;
    private static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    private static final int ARG_1_AND_2_ARE_WORDS = 1;
    private static final int WE_HAVE_A_SCALE = 8;
    private static final int MORE_COMPONENTS = 32;
    private static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    private static final int WE_HAVE_A_TWO_BY_TWO = 128;


    /**
     * Contains the location of the several tables. The key is the name of
     * the table and the value is an {@code int[3]} where position 0
     * is the checksum, position 1 is the offset from the start of the file
     * and position 2 is the length of the table.
     */
    private Map<String, int[]> tableDirectory;
    /**
     * The file in use.
     */
    protected RandomAccessFileOrArray rf;
    /**
     * The file name.
     */
    private String fileName;
    private  boolean locaShortTable;
    private  int[] locaTable;
    private  Set<Integer> glyphsUsed;
    private  List<Integer> glyphsInList;
    private  int tableGlyphOffset;
    private  int[] newLocaTable;
    private  byte[] newLocaTableOut;
    private  byte[] newGlyfTable;
    private  int glyfTableRealSize;
    private  int locaTableRealSize;
    private  byte[] outFont;
    private  int fontPtr;
    private  int directoryOffset;
    private final String[] tableNames;

    /**
     * Creates a new TrueTypeFontSubSet
     *
     * @param directoryOffset The offset from the start of the file to the table directory
     * @param fileName        the file name of the font
     * @param glyphsUsed      the glyphs used
     */
    TrueTypeFontSubset(String fileName, RandomAccessFileOrArray rf, Set<Integer> glyphsUsed, int directoryOffset, boolean subset) {
        this.fileName = fileName;
        this.rf = rf;
        this.glyphsUsed = new HashSet<>(glyphsUsed);
        this.directoryOffset = directoryOffset;
        // subset = false is possible with directoryOffset > 0, i.e. ttc font without subset.
        if (subset) {
            tableNames = TABLE_NAMES_SUBSET;
        } else {
            tableNames = TABLE_NAMES;
        }
        glyphsInList = new ArrayList<>(glyphsUsed);
    }

    /**
     * Does the actual work of subsetting the font.
     *
     * @return the subset font
     * @throws java.io.IOException on error
     */
    byte[] process() throws java.io.IOException {
        try {
            createTableDirectory();
            readLoca();
            flatGlyphs();
            createNewGlyphTables();
            locaToBytes();
            assembleFont();
            return outFont;
        } finally {
            try {
                rf.close();
            } catch (Exception ignore) {
            }
        }
    }

    private void assembleFont() throws java.io.IOException {
        int[] tableLocation;
        int fullFontSize = 0;
        int tablesUsed = 2;
        for (String name : tableNames) {
            if (name.equals("glyf") || name.equals("loca")) {
                continue;
            }
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            tablesUsed++;
            fullFontSize += tableLocation[TABLE_LENGTH] + 3 & ~3;
        }
        fullFontSize += newLocaTableOut.length;
        fullFontSize += newGlyfTable.length;
        int reference = 16 * tablesUsed + 12;
        fullFontSize += reference;
        outFont = new byte[fullFontSize];
        fontPtr = 0;
        writeFontInt(0x00010000);
        writeFontShort(tablesUsed);
        int selector = entrySelectors[tablesUsed];
        writeFontShort((1 << selector) * 16);
        writeFontShort(selector);
        writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (String name : tableNames) {
            int len;
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            writeFontString(name);
            switch (name) {
                case "glyf":
                    writeFontInt(calculateChecksum(newGlyfTable));
                    len = glyfTableRealSize;
                    break;
                case "loca":
                    writeFontInt(calculateChecksum(newLocaTableOut));
                    len = locaTableRealSize;
                    break;
                default:
                    writeFontInt(tableLocation[TABLE_CHECKSUM]);
                    len = tableLocation[TABLE_LENGTH];
                    break;
            }
            writeFontInt(reference);
            writeFontInt(len);
            reference += len + 3 & ~3;
        }
        for (String name : tableNames) {
            tableLocation = tableDirectory.get(name);
            if (tableLocation == null) {
                continue;
            }
            switch (name) {
                case "glyf":
                    System.arraycopy(newGlyfTable, 0, outFont, fontPtr, newGlyfTable.length);
                    fontPtr += newGlyfTable.length;
                    newGlyfTable = null;
                    break;
                case "loca":
                    System.arraycopy(newLocaTableOut, 0, outFont, fontPtr, newLocaTableOut.length);
                    fontPtr += newLocaTableOut.length;
                    newLocaTableOut = null;
                    break;
                default:
                    rf.seek(tableLocation[TABLE_OFFSET]);
                    rf.readFully(outFont, fontPtr, tableLocation[TABLE_LENGTH]);
                    fontPtr += tableLocation[TABLE_LENGTH] + 3 & ~3;
                    break;
            }
        }
    }

    private void createTableDirectory() throws java.io.IOException {
        tableDirectory = new HashMap<>();
        rf.seek(directoryOffset);
        int id = rf.readInt();
        if (id != 0x00010000) {
            throw new IOException(IOException.NotAtTrueTypeFile).setMessageParams(fileName);
        }
        int num_tables = rf.readUnsignedShort();
        rf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            String tag = readStandardString(4);
            int[] tableLocation = new int[3];
            tableLocation[TABLE_CHECKSUM] = rf.readInt();
            tableLocation[TABLE_OFFSET] = rf.readInt();
            tableLocation[TABLE_LENGTH] = rf.readInt();
            tableDirectory.put(tag, tableLocation);
        }
    }

    private void readLoca() throws java.io.IOException {
        int[] tableLocation = tableDirectory.get("head");
        if (tableLocation == null) {
            throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("head", fileName);
        }
        rf.seek(tableLocation[TABLE_OFFSET] + HEAD_LOCA_FORMAT_OFFSET);
        locaShortTable = rf.readUnsignedShort() == 0;
        tableLocation = tableDirectory.get("loca");
        if (tableLocation == null) {
            throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("loca", fileName);
        }
        rf.seek(tableLocation[TABLE_OFFSET]);
        if (locaShortTable) {
            int entries = tableLocation[TABLE_LENGTH] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = rf.readUnsignedShort() * 2;
            }
        } else {
            int entries = tableLocation[TABLE_LENGTH] / 4;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = rf.readInt();
            }
        }
    }

    private void createNewGlyphTables() throws java.io.IOException {
        newLocaTable = new int[locaTable.length];
        int[] activeGlyphs = new int[glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; ++k) {
            activeGlyphs[k] = (int) glyphsInList.get(k);
        }
        Arrays.sort(activeGlyphs);
        int glyfSize = 0;
        for (int glyph : activeGlyphs) {
            glyfSize += locaTable[glyph + 1] - locaTable[glyph];
        }
        glyfTableRealSize = glyfSize;
        glyfSize = glyfSize + 3 & ~3;
        newGlyfTable = new byte[glyfSize];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int k = 0; k < newLocaTable.length; ++k) {
            newLocaTable[k] = glyfPtr;
            if (listGlyf < activeGlyphs.length && activeGlyphs[listGlyf] == k) {
                ++listGlyf;
                newLocaTable[k] = glyfPtr;
                int start = locaTable[k];
                int len = locaTable[k + 1] - start;
                if (len > 0) {
                    rf.seek(tableGlyphOffset + start);
                    rf.readFully(newGlyfTable, glyfPtr, len);
                    glyfPtr += len;
                }
            }
        }
    }

    private void locaToBytes() {
        if (locaShortTable) {
            locaTableRealSize = newLocaTable.length * 2;
        } else {
            locaTableRealSize = newLocaTable.length * 4;
        }
        newLocaTableOut = new byte[locaTableRealSize + 3 & ~3];
        outFont = newLocaTableOut;
        fontPtr = 0;
        for (int location : newLocaTable) {
            if (locaShortTable)
                writeFontShort(location / 2);
            else
                writeFontInt(location);
        }
    }

    private void flatGlyphs() throws java.io.IOException {
        int[] tableLocation = tableDirectory.get("glyf");
        if (tableLocation == null)
            throw new IOException(IOException.TableDoesNotExistsIn).setMessageParams("glyf", fileName);
        int glyph0 = 0;
        if (!glyphsUsed.contains(glyph0)) {
            glyphsUsed.add(glyph0);
            glyphsInList.add(glyph0);
        }
        tableGlyphOffset = tableLocation[TABLE_OFFSET];
        // Do not replace with foreach. ConcurrentModificationException will arise.
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < glyphsInList.size(); i++) {
            checkGlyphComposite((int) glyphsInList.get(i));
        }
    }

    private void checkGlyphComposite(int glyph) throws java.io.IOException {
        int start = locaTable[glyph];
        if (start == locaTable[glyph + 1]) {// no contour
            return;
        }
        rf.seek(tableGlyphOffset + start);
        int numContours = rf.readShort();
        if (numContours >= 0) {
            return;
        }
        rf.skipBytes(8);
        for (; ; ) {
            int flags = rf.readUnsignedShort();
            int cGlyph = rf.readUnsignedShort();
            if (!glyphsUsed.contains(cGlyph)) {
                glyphsUsed.add(cGlyph);
                glyphsInList.add(cGlyph);
            }
            if ((flags & MORE_COMPONENTS) == 0) {
                return;
            }
            int skip;
            if ((flags & ARG_1_AND_2_ARE_WORDS) != 0) {
                skip = 4;
            } else {
                skip = 2;
            }
            if ((flags & WE_HAVE_A_SCALE) != 0) {
                skip += 2;
            } else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
                skip += 4;
            }
            if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
                skip += 8;
            }
            rf.skipBytes(skip);
        }
    }

    /**
     * Reads a {@code String} from the font file as bytes using the Cp1252 encoding.
     *
     * @param length the length of bytes to read
     * @return the {@code String} read
     * @throws java.io.IOException the font file could not be read
     */
    private String readStandardString(int length) throws java.io.IOException {
        byte[] buf = new byte[length];
        rf.readFully(buf);
        try {
            return new String(buf, PdfEncodings.WINANSI);
        } catch (Exception e) {
            throw new IOException("TrueType font", e);
        }
    }

    private void writeFontShort(int n) {
        outFont[fontPtr++] = (byte) (n >> 8);
        outFont[fontPtr++] = (byte) n;
    }

    private void writeFontInt(int n) {
        outFont[fontPtr++] = (byte) (n >> 24);
        outFont[fontPtr++] = (byte) (n >> 16);
        outFont[fontPtr++] = (byte) (n >> 8);
        outFont[fontPtr++] = (byte) n;
    }

    private void writeFontString(String s) {
        byte[] b = PdfEncodings.convertToBytes(s, PdfEncodings.WINANSI);
        System.arraycopy(b, 0, outFont, fontPtr, b.length);
        fontPtr += b.length;
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
}
