package com.itextpdf.kernel.pdf;

import java.util.Set;

/**
 * Class which stores information about single PDF document revision.
 */
public class DocumentRevision {
    private final long eofOffset;
    private final Set<PdfIndirectReference> modifiedObjects;

    /**
     * Creates {@link DocumentRevision} from end-of-file byte position and a set of indirect references which were
     * modified in this document revision.
     *
     * @param eofOffset       end-of-file byte position
     * @param modifiedObjects {@link Set} of {@link PdfIndirectReference} objects which were modified
     */
    public DocumentRevision(long eofOffset, Set<PdfIndirectReference> modifiedObjects) {
        this.eofOffset = eofOffset;
        this.modifiedObjects = modifiedObjects;
    }

    /**
     * Gets end-of-file byte position.
     *
     * @return end-of-file byte position
     */
    public long getEofOffset() {
        return eofOffset;
    }

    /**
     * Gets objects which were modified in this document revision.
     *
     * @return {@link Set} of {@link PdfIndirectReference} objects which were modified
     */
    public Set<PdfIndirectReference> getModifiedObjects() {
        return modifiedObjects;
    }
}
