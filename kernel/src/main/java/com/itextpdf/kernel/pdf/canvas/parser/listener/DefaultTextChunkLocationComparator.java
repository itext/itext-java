package com.itextpdf.kernel.pdf.canvas.parser.listener;

import java.util.Comparator;

class DefaultTextChunkLocationComparator implements Comparator<ITextChunkLocation> {
    private boolean leftToRight = true;

    public DefaultTextChunkLocationComparator() {
        this(true);
    }

    public DefaultTextChunkLocationComparator(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }

    @Override
    public int compare(ITextChunkLocation first, ITextChunkLocation second) {
        if (first == second) return 0; // not really needed, but just in case

        int result;
        result = Integer.compare(first.orientationMagnitude(), second.orientationMagnitude());
        if (result != 0) {
            return result;
        }

        int distPerpendicularDiff = first.distPerpendicular() - second.distPerpendicular();
        if (distPerpendicularDiff != 0) {
            return distPerpendicularDiff;
        }

        return leftToRight ? Float.compare(first.distParallelStart(), second.distParallelStart()) :
                -Float.compare(first.distParallelEnd(), second.distParallelEnd());
    }
}
