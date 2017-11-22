package com.itextpdf.kernel.pdf.canvas.parser.listener;

import java.util.Comparator;

class TextChunkLocationBasedComparator implements Comparator<TextChunk> {
    private Comparator<ITextChunkLocation> locationComparator;

    public TextChunkLocationBasedComparator(Comparator<ITextChunkLocation> locationComparator) {
        this.locationComparator = locationComparator;
    }

    @Override
    public int compare(TextChunk o1, TextChunk o2) {
        return locationComparator.compare(o1.location, o2.location);
    }
}
