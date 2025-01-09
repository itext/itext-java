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
package com.itextpdf.io.image;

import java.net.URL;

public class PngImageData extends RawImageData {

    private byte[] colorPalette;
    private int colorType;
    private float gamma = 1f;
    private PngChromaticities pngChromaticities;

    protected PngImageData(byte[] bytes) {
        super(bytes, ImageType.PNG);
    }

    protected PngImageData(URL url) {
        super(url, ImageType.PNG);
    }

    public byte[] getColorPalette() {
        return colorPalette;
    }

    public void setColorPalette(byte[] colorPalette) {
        this.colorPalette = colorPalette;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public boolean isHasCHRM() {
        return this.pngChromaticities != null;
    }

    public PngChromaticities getPngChromaticities() {
        return pngChromaticities;
    }

    public void setPngChromaticities(PngChromaticities pngChromaticities) {
        this.pngChromaticities = pngChromaticities;
    }

    public int getColorType() {
        return colorType;
    }

    public void setColorType(int colorType) {
        this.colorType = colorType;
    }

    public boolean isIndexed() {
        return this.colorType == 3;
    }

    public boolean isGrayscaleImage() {
        return (this.colorType & 2) == 0;
    }
}
