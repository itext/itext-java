/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.image;

/**
 * Stores the CIE {@code x}/{@code y} chromaticities declared by a PNG image.
 */
public class PngChromaticities {
    private float xW;
    private float yW;
    private float xR;
    private float yR;
    private float xG;
    private float yG;
    private float xB;
    private float yB;

    /**
     * Creates PNG chromaticities for the white point and RGB primaries.
     *
     * @param xW white-point x chromaticity
     * @param yW white-point y chromaticity
     * @param xR red-primary x chromaticity
     * @param yR red-primary y chromaticity
     * @param xG green-primary x chromaticity
     * @param yG green-primary y chromaticity
     * @param xB blue-primary x chromaticity
     * @param yB blue-primary y chromaticity
     */
    public PngChromaticities(float xW, float yW, float xR, float yR, float xG, float yG, float xB, float yB) {
        this.xW = xW;
        this.yW = yW;
        this.xR = xR;
        this.yR = yR;
        this.xG = xG;
        this.yG = yG;
        this.xB = xB;
        this.yB = yB;
    }

    /** Gets the white-point x chromaticity.
     *
     * @return white-point x chromaticity
     */
    public float getXW() {
        return xW;
    }

    /** Gets the white-point y chromaticity.
     *
     * @return white-point y chromaticity
     */
    public float getYW() {
        return yW;
    }

    /** Gets the red-primary x chromaticity.
     *
     * @return red-primary x chromaticity
     */
    public float getXR() {
        return xR;
    }

    /** Gets the red-primary y chromaticity.
     *
     * @return red-primary y chromaticity
     */
    public float getYR() {
        return yR;
    }

    /** Gets the green-primary x chromaticity.
     *
     * @return green-primary x chromaticity
     */
    public float getXG() {
        return xG;
    }

    /** Gets the green-primary y chromaticity.
     *
     * @return green-primary y chromaticity
     */
    public float getYG() {
        return yG;
    }

    /** Gets the blue-primary x chromaticity.
     *
     * @return blue-primary x chromaticity
     */
    public float getXB() {
        return xB;
    }

    /** Gets the blue-primary y chromaticity.
     *
     * @return blue-primary y chromaticity
     */
    public float getYB() {
        return yB;
    }
}
