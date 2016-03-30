package com.itextpdf.io.font.otf;

/**
 *
 * @author psoares
 */
public abstract class OpenTableLookup {

    protected int lookupFlag;
    protected int[] subTableLocations;
    protected OpenTypeFontTableReader openReader;
    
    protected OpenTableLookup(OpenTypeFontTableReader openReader, int lookupFlag, int[] subTableLocations) {
        this.lookupFlag = lookupFlag;
        this.subTableLocations = subTableLocations;
        this.openReader = openReader;
    }
    
    public int getLookupFlag() {
        return lookupFlag;
    }
    
    protected void readSubTables() throws java.io.IOException {
		for (int subTableLocation : subTableLocations) {
			readSubTable(subTableLocation);
		}
    }
    
    protected abstract void readSubTable(int subTableLocation) throws java.io.IOException; 
    
    public abstract boolean transformOne(GlyphLine line);
    
    public boolean transformLine(GlyphLine line) {
        boolean changed = false;
        line.idx = line.start;
        while (line.idx < line.end && line.idx >= line.start) {
            changed = transformOne(line) || changed;
        }
        return changed;
    }
    
    public static class GlyphIndexer {
        public GlyphLine line;
        public Glyph glyph;
        public int idx;

        public void nextGlyph(OpenTypeFontTableReader openReader, int lookupFlag) {
            glyph = null;
            while (++idx < line.end) {
                Glyph g = line.get(idx);
                if (!openReader.isSkip(g.getCode(), lookupFlag)) {
                    glyph = g;
                    break;
                }
            }
        }

        public void previousGlyph(OpenTypeFontTableReader openReader, int lookupFlag) {
            glyph = null;
            while (--idx >= line.start) {
                Glyph g = line.get(idx);
                if (!openReader.isSkip(g.getCode(), lookupFlag)) {
                    glyph = g;
                    break;
                }
            }
        }
    }

    public boolean hasSubstitution(int index) {
        return false;
    }
}
