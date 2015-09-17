package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Subsets a True Type font by removing the unneeded glyphs from the font.
 *
 * @author  Paulo Soares
 */
class TrueTypeFontSubset {

    static final String tableNamesSimple[] = {"cvt ", "fpgm", "glyf", "head",
            "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String tableNamesCmap[] = {"cmap", "cvt ", "fpgm", "glyf", "head",
            "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String tableNamesExtra[] = {"OS/2", "cmap", "cvt ", "fpgm", "glyf", "head",
            "hhea", "hmtx", "loca", "maxp", "name, prep"};
    static final int entrySelectors[] = {0,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,4};
    static final int TABLE_CHECKSUM = 0;
    static final int TABLE_OFFSET = 1;
    static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;

    static final int ARG_1_AND_2_ARE_WORDS = 1;
    static final int WE_HAVE_A_SCALE = 8;
    static final int MORE_COMPONENTS = 32;
    static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    static final int WE_HAVE_A_TWO_BY_TWO = 128;


    /**
     * Contains the location of the several tables. The key is the name of
     * the table and the value is an {@code int[3]} where position 0
     * is the checksum, position 1 is the offset from the start of the file
     * and position 2 is the length of the table.
     */
    protected HashMap<String, int[]> tableDirectory;
    /** The file in use. */
    protected RandomAccessFileOrArray rf;
    /** The file name. */
    protected String fileName;
    protected boolean includeCmap;
    protected boolean includeExtras;
    protected boolean locaShortTable;
    protected int[] locaTable;
    protected Set<Integer> glyphsUsed;
    protected ArrayList<Integer> glyphsInList;
    protected int tableGlyphOffset;
    protected int[] newLocaTable;
    protected byte[] newLocaTableOut;
    protected byte[] newGlyfTable;
    protected int glyfTableRealSize;
    protected int locaTableRealSize;
    protected byte[] outFont;
    protected int fontPtr;
    protected int directoryOffset;

    /**
     * Creates a new TrueTypeFontSubSet
     * @param directoryOffset The offset from the start of the file to the table directory
     * @param fileName the file name of the font
     * @param glyphsUsed the glyphs used
     * @param includeCmap {@code true} if the table cmap is to be included in the generated font
     */
    TrueTypeFontSubset(String fileName, RandomAccessFileOrArray rf, Set<Integer> glyphsUsed, int directoryOffset, boolean includeCmap, boolean includeExtras) {
        this.fileName = fileName;
        this.rf = rf;
        this.glyphsUsed = glyphsUsed;
        this.includeCmap = includeCmap;
        this.includeExtras = includeExtras;
        this.directoryOffset = directoryOffset;
        glyphsInList = new ArrayList<Integer>(glyphsUsed);
    }

    /**
     * Does the actual work of subsetting the font.
     * @throws IOException on error
     * @on error
     * @return the subset font
     */
    byte[] process() throws IOException {
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
            } catch (Exception ignore) { }
        }
    }

    protected void assembleFont() throws IOException {
        int[] tableLocation;
        int fullFontSize = 0;
        String[] tableNames;
        if (includeExtras) {
            tableNames = tableNamesExtra;
        } else {
            if (includeCmap) {
                tableNames = tableNamesCmap;
            } else {
                tableNames = tableNamesSimple;
            }
        }
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
        int ref = 16 * tablesUsed + 12;
        fullFontSize += ref;
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
            writeFontInt(ref);
            writeFontInt(len);
            ref += len + 3 & ~3;
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

    protected void createTableDirectory() throws IOException {
        tableDirectory = new HashMap<String, int[]>();
        rf.seek(directoryOffset);
        int id = rf.readInt();
        if (id != 0x00010000) {
            throw new PdfException("1.is.not.a.true.type.file").setMessageParams(fileName);
        }
        int num_tables = rf.readUnsignedShort();
        rf.skipBytes(6);
        for (int k = 0; k < num_tables; ++k) {
            String tag = readStandardString(4);
            int tableLocation[] = new int[3];
            tableLocation[TABLE_CHECKSUM] = rf.readInt();
            tableLocation[TABLE_OFFSET] = rf.readInt();
            tableLocation[TABLE_LENGTH] = rf.readInt();
            tableDirectory.put(tag, tableLocation);
        }
    }

    protected void readLoca() throws IOException {
        int[] tableLocation = tableDirectory.get("head");
        if (tableLocation == null) {
            throw new PdfException("table.1.does.not.exist.in.2", "head").setMessageParams(fileName);
        }
        rf.seek(tableLocation[TABLE_OFFSET] + HEAD_LOCA_FORMAT_OFFSET);
        locaShortTable = rf.readUnsignedShort() == 0;
        tableLocation = tableDirectory.get("loca");
        if (tableLocation == null) {
            throw new PdfException("table.1.does.not.exist.in.2", "loca").setMessageParams(fileName);
        }
        rf.seek(tableLocation[TABLE_OFFSET]);
        if (locaShortTable) {
            int entries = tableLocation[TABLE_LENGTH] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = rf.readUnsignedShort() * 2;
            }
        }
        else {
            int entries = tableLocation[TABLE_LENGTH] / 4;
            locaTable = new int[entries];
            for (int k = 0; k < entries; ++k) {
                locaTable[k] = rf.readInt();
            }
        }
    }

    protected void createNewGlyphTables() throws IOException {
        newLocaTable = new int[locaTable.length];
        int activeGlyphs[] = new int[glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; ++k) {
            activeGlyphs[k] = glyphsInList.get(k);
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

    protected void locaToBytes() {
        if (locaShortTable) {
            locaTableRealSize = newLocaTable.length * 2;
        } else {
            locaTableRealSize = newLocaTable.length * 4;
        }
        newLocaTableOut = new byte[locaTableRealSize + 3 & ~3];
        outFont = newLocaTableOut;
        fontPtr = 0;
        for (int k = 0; k < newLocaTable.length; ++k) {
            if (locaShortTable)
                writeFontShort(newLocaTable[k] / 2);
            else
                writeFontInt(newLocaTable[k]);
        }
    }

    protected void flatGlyphs() throws IOException {
        int[] tableLocation = tableDirectory.get("glyf");
        if (tableLocation == null)
            throw new PdfException("table.1.does.not.exist.in.2").setMessageParams("glyf", fileName);
        Integer glyph0 = 0;
        if (!glyphsUsed.contains(glyph0)) {
            glyphsUsed.add(glyph0);
            glyphsInList.add(glyph0);
        }
        tableGlyphOffset = tableLocation[TABLE_OFFSET];
        // Do not replace with foreach. ConcurrentModificationException will arise.
        for (int k = 0; k < glyphsInList.size(); ++k) {
            int glyph = glyphsInList.get(k);
            checkGlyphComposite(glyph);
        }
    }

    protected void checkGlyphComposite(int glyph) throws IOException {
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
        for(;;) {
            int flags = rf.readUnsignedShort();
            Integer cGlyph = rf.readUnsignedShort();
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
     * @param length the length of bytes to read
     * @return the {@code String} read
     * @throws IOException the font file could not be read
     */
    protected String readStandardString(int length) throws IOException {
        byte buf[] = new byte[length];
        rf.readFully(buf);
        try {
            return new String(buf, PdfEncodings.WINANSI);
        } catch (Exception e) {
            throw new PdfException("TrueType font", e);
        }
    }

    protected void writeFontShort(int n) {
        outFont[fontPtr++] = (byte)(n >> 8);
        outFont[fontPtr++] = (byte)n;
    }

    protected void writeFontInt(int n) {
        outFont[fontPtr++] = (byte)(n >> 24);
        outFont[fontPtr++] = (byte)(n >> 16);
        outFont[fontPtr++] = (byte)(n >> 8);
        outFont[fontPtr++] = (byte)n;
    }

    protected void writeFontString(String s) {
        byte b[] = PdfEncodings.convertToBytes(s, PdfEncodings.WINANSI);
        System.arraycopy(b, 0, outFont, fontPtr, b.length);
        fontPtr += b.length;
    }

    protected int calculateChecksum(byte b[]) {
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
