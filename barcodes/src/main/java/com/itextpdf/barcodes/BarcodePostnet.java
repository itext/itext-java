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
package com.itextpdf.barcodes;


import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * POSTNET (Postal Numeric Encoding Technique) is a barcode symbology used by the United States Postal Service to assist
 * in directing mail. The ZIP Code or ZIP+4 code is encoded in half- and full-height bars.[1] Most often, the delivery
 * point is added, usually being the last two digits of the address or PO box number.
 */
public class BarcodePostnet extends Barcode1D {

    public static final int TYPE_POSTNET = 1;
    public static final int TYPE_PLANET = 2;

    /**
     * The bars for each character.
     */
    private static final byte[][] BARS =
            {
                    {1, 1, 0, 0, 0},
                    {0, 0, 0, 1, 1},
                    {0, 0, 1, 0, 1},
                    {0, 0, 1, 1, 0},
                    {0, 1, 0, 0, 1},
                    {0, 1, 0, 1, 0},
                    {0, 1, 1, 0, 0},
                    {1, 0, 0, 0, 1},
                    {1, 0, 0, 1, 0},
                    {1, 0, 1, 0, 0}
            };

    /**
     * Creates new {@link  BarcodePostnet} instance.
     *
     * @param document The document
     */
    public BarcodePostnet(PdfDocument document) {
        super(document);
        // distance between bars
        n = 72f / 22f;
        // bar width
        x = 0.02f * 72f;
        // height of the tall bars
        barHeight = 0.125f * 72f;
        // height of the short bars
        size = 0.05f * 72f;
        // type of code
        codeType = TYPE_POSTNET;
    }

    /** Creates the bars for Postnet.
     * @param text the code to be created without checksum
     * @return the bars
     */
    public static byte[] getBarsPostnet(String text) {
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            int n = text.charAt(k) - '0';
            total += n;
        }
        text += (char)(((10 - (total % 10)) % 10) + '0');
        byte[] bars = new byte[text.length() * 5 + 2];
        bars[0] = 1;
        bars[bars.length - 1] = 1;
        for (int k = 0; k < text.length(); ++k) {
            int c = text.charAt(k) - '0';
            System.arraycopy(BARS[c], 0, bars, k * 5 + 1, 5);
        }
        return bars;
    }

    @Override
    public Rectangle getBarcodeSize() {
        float width = ((code.length() + 1) * 5 + 1) * n + x;
        return new Rectangle(width, barHeight);
    }

    @Override
    public void fitWidth(float width) {
        byte[] bars = getBarsPostnet(code);
        float currentWidth = getBarcodeSize().getWidth();
        x *= width / currentWidth;
        n = (width - x) / (bars.length - 1);
    }

    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor) {
        if (barColor != null)
            canvas.setFillColor(barColor);
        byte[] bars = getBarsPostnet(code);
        byte flip = 1;
        if (codeType == TYPE_PLANET) {
            flip = 0;
            bars[0] = 0;
            bars[bars.length - 1] = 0;
        }
        float startX = 0;
        for (int k = 0; k < bars.length; ++k) {
            canvas.rectangle(startX, 0, x - inkSpreading, bars[k] == flip ? barHeight : size);
            startX += n;
        }
        canvas.fill();
        return getBarcodeSize();
    }

}
