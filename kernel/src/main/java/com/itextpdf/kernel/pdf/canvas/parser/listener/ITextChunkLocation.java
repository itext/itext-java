package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Vector;

public interface ITextChunkLocation {
    float distParallelEnd();

    float distParallelStart();

    int distPerpendicular();

    float getCharSpaceWidth();

    Vector getEndLocation();

    Vector getStartLocation();

    int orientationMagnitude();

    boolean sameLine(ITextChunkLocation as);

    float distanceFromEndOf(ITextChunkLocation other);

    boolean isAtWordBoundary(ITextChunkLocation previous);
}
