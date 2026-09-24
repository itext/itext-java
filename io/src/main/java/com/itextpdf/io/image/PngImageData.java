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

import java.net.URL;

/**
 * Image data and PNG-specific color information.
 */
public class PngImageData extends RawImageData {

    private byte[] colorPalette;
    private int colorType;
    private float gamma = 1f;
    private PngChromaticities pngChromaticities;

    /**
     * Creates PNG image data from encoded bytes.
     *
     * @param bytes encoded PNG bytes; the array is retained
     */
    protected PngImageData(byte[] bytes) {
        super(bytes, ImageType.PNG);
    }

    /**
     * Creates PNG image data to be loaded from a URL.
     *
     * @param url source URL, not {@code null}
     */
    protected PngImageData(URL url) {
        super(url, ImageType.PNG);
    }

    /**
     * Gets the indexed-color palette.
     *
     * @return retained PNG palette bytes, or {@code null}
     */
    public byte[] getColorPalette() {
        return colorPalette;
    }

    /**
     * Sets the indexed-color palette.
     *
     * @param colorPalette PNG palette bytes to retain, or {@code null}
     */
    public void setColorPalette(byte[] colorPalette) {
        this.colorPalette = colorPalette;
    }

    /**
     * Gets the PNG gamma value.
     *
     * @return gamma value
     */
    public float getGamma() {
        return gamma;
    }

    /**
     * Sets the PNG gamma value.
     *
     * @param gamma gamma value
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    /**
     * Checks whether PNG chromaticity data is available.
     *
     * @return {@code true} when chromaticity data is present
     */
    public boolean isHasCHRM() {
        return this.pngChromaticities != null;
    }

    /**
     * Gets PNG chromaticity data.
     *
     * @return chromaticity data, or {@code null}
     */
    public PngChromaticities getPngChromaticities() {
        return pngChromaticities;
    }

    /**
     * Sets PNG chromaticity data.
     *
     * @param pngChromaticities chromaticity data, or {@code null}
     */
    public void setPngChromaticities(PngChromaticities pngChromaticities) {
        this.pngChromaticities = pngChromaticities;
    }

    /**
     * Gets the PNG color type.
     *
     * @return PNG color-type value
     */
    public int getColorType() {
        return colorType;
    }

    /**
     * Sets the PNG color type.
     *
     * @param colorType PNG color-type value
     */
    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    /**
     * Checks whether the PNG uses indexed color.
     *
     * @return {@code true} for color type {@code 3}
     */
    public boolean isIndexed() {
        return this.colorType == 3;
    }

    /**
     * Checks whether the PNG color type has no color components.
     *
     * @return {@code true} for grayscale color types
     */
    public boolean isGrayscaleImage() {
        return (this.colorType & 2) == 0;
    }
}
