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
        // not really needed, but just in case
        if (first == second) return 0;

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
