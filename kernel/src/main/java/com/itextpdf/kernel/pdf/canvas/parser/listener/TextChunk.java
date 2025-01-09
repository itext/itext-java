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
