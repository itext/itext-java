package com.itextpdf.kernel.pdf.canvas.parser.listener;

/**
 * Represents a chunk of text, it's orientation, and location relative to the orientation vector
 */
public class TextChunk {
    /**
     * the text of the chunk
     */
    protected final String text;
    protected final ITextChunkLocation location;

    public TextChunk(String string, ITextChunkLocation loc) {
        this.text = string;
        this.location = loc;
    }

    /**
     * @return the text captured by this chunk
     */
    public String getText() {
        return text;
    }

    public ITextChunkLocation getLocation() {
        return location;
    }

    void printDiagnostics() {
        System.out.println("Text (@" + location.getStartLocation() + " -> " + location.getEndLocation() + "): " + text);
        System.out.println("orientationMagnitude: " + location.orientationMagnitude());
        System.out.println("distPerpendicular: " + location.distPerpendicular());
        System.out.println("distParallel: " + location.distParallelStart());
    }

    boolean sameLine(TextChunk lastChunk) {
        return getLocation().sameLine(lastChunk.getLocation());
    }
}
