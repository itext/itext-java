package com.itextpdf.basics.font.otf;

import java.io.IOException;

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
    
    protected void readSubTables() throws IOException {
		for (int subTableLocation : subTableLocations) {
			readSubTable(subTableLocation);
		}
    }
    
    protected abstract void readSubTable(int subTableLocation) throws IOException; 
    
    public abstract boolean transformOne(GlyphLine line);
    
    public boolean transformLine(GlyphLine line) {
        boolean changed = false;
        line.idx = line.start;
        while (line.idx < line.end && line.idx >= line.start) {
            changed = transformOne(line) || changed;
        }
        return changed;
    }
    
    public void NextGlyph(GlyphIndexer indexer) {
        indexer.glyph = null;
        indexer.idx++;
        while (indexer.idx < indexer.line.end) {
            Glyph g = indexer.line.glyphs.get(indexer.idx);
            if (!openReader.IsSkip(g.index, lookupFlag)) {
                indexer.glyph = g;
                break;
            }
        }
    }
    
    public void PreviousGlyph(GlyphIndexer indexer) {
        indexer.glyph = null;
        indexer.idx--;
        while (indexer.idx >= indexer.line.start) {
            Glyph g = indexer.line.glyphs.get(indexer.idx);
            if (!openReader.IsSkip(g.index, lookupFlag)) {
                indexer.glyph = g;
                break;
            }
        }
    }
    
    public static class GlyphIndexer {
        public GlyphLine line;
        public Glyph glyph;
        public int idx;
    }

    public boolean hasSubstitution(int index) {
        return false;
    }
}
