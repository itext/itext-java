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
package com.itextpdf.kernel.pdf;

/**
 * Represents the line dash pattern. The line dash pattern shall control the pattern
 * of dashes and gaps used to stroke paths. It shall be specified by a dash, gap and
 * a dash phase.
 */
public class PdfDashPattern {

    /** This is the length of a dash. */
    private float dash = -1;

    /** This is the length of a gap. */
    private float gap = -1;

    /** This is the phase. */
    private float phase = -1;

    /**
     * Creates a new line dash pattern.
     */
    public PdfDashPattern(){
    }

    /**
     * Creates a new line dash pattern.
     *
     * @param dash length of dash
     */
    public PdfDashPattern(float dash) {
        this.dash = dash;
    }

    /**
     * Creates a new line dash pattern.
     *
     * @param dash length of dash
     * @param gap length of gap
     */
    public PdfDashPattern(float dash, float gap) {
        this.dash = dash;
        this.gap = gap;
    }

    /**
     * Creates a new line dash pattern.
     *
     * @param dash length of dash
     * @param gap length of gap
     * @param phase this is the phase
     */
    public PdfDashPattern(float dash, float gap, float phase) {
        this(dash, gap);
        this.phase = phase;
    }

    /**
     * Gets dash of PdfDashPattern.
     *
     * @return float value.
     */
    public float getDash() {
        return dash;
    }

    /**
     * Gets gap of PdfDashPattern.
     *
     * @return float value.
     */
    public float getGap() {
        return gap;
    }

    /**
     * Gets phase of PdfDashPattern.
     *
     * @return float value.
     */
    public float getPhase() {
        return phase;
    }
}
