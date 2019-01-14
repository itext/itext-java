/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms.util;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.nio.charset.StandardCharsets;

/**
 * Utility class to draw form fields {@link com.itextpdf.forms.fields.PdfFormField}.
 */
public class DrawingUtil {
    protected static final String check = "0.8 0 0 0.8 0.3 0.5 cm 0 0 m\n" +
            "0.066 -0.026 l\n" +
            "0.137 -0.15 l\n" +
            "0.259 0.081 0.46 0.391 0.553 0.461 c\n" +
            "0.604 0.489 l\n" +
            "0.703 0.492 l\n" +
            "0.543 0.312 0.255 -0.205 0.154 -0.439 c\n" +
            "0.069 -0.399 l\n" +
            "0.035 -0.293 -0.039 -0.136 -0.091 -0.057 c\n" +
            "h\n" +
            "f\n";

    protected static final String circle = "1 0 0 1 0.86 0.5 cm 0 0 m\n" +
            "0 0.204 -0.166 0.371 -0.371 0.371 c\n" +
            "-0.575 0.371 -0.741 0.204 -0.741 0 c\n" +
            "-0.741 -0.204 -0.575 -0.371 -0.371 -0.371 c\n" +
            "-0.166 -0.371 0 -0.204 0 0 c\n" +
            "f\n";


    protected static final String cross = "1 0 0 1 0.80 0.8 cm 0 0 m\n" +
            "-0.172 -0.027 l\n" +
            "-0.332 -0.184 l\n" +
            "-0.443 -0.019 l\n" +
            "-0.475 -0.009 l\n" +
            "-0.568 -0.168 l\n" +
            "-0.453 -0.324 l\n" +
            "-0.58 -0.497 l\n" +
            "-0.59 -0.641 l\n" +
            "-0.549 -0.627 l\n" +
            "-0.543 -0.612 -0.457 -0.519 -0.365 -0.419 c\n" +
            "-0.163 -0.572 l\n" +
            "-0.011 -0.536 l\n" +
            "-0.004 -0.507 l\n" +
            "-0.117 -0.441 l\n" +
            "-0.246 -0.294 l\n" +
            "-0.132 -0.181 l\n" +
            "0.031 -0.04 l\n" +
            "h\n" +
            "f\n";

    protected static final String diamond = "1 0 0 1 0.5 0.12 cm 0 0 m\n" +
            "0.376 0.376 l\n" +
            "0 0.751 l\n" +
            "-0.376 0.376 l\n" +
            "h\n" +
            "f\n";

    protected static final String square = "1 0 0 1 0.835 0.835 cm 0 0 -0.669 -0.67 re\n" +
            "f\n";

    protected static final String star = "0.95 0 0 0.95 0.85 0.6 cm 0 0 m\n" +
            "-0.291 0 l\n" +
            "-0.381 0.277 l\n" +
            "-0.47 0 l\n" +
            "-0.761 0 l\n" +
            "-0.526 -0.171 l\n" +
            "-0.616 -0.448 l\n" +
            "-0.381 -0.277 l\n" +
            "-0.145 -0.448 l\n" +
            "-0.236 -0.171 l\n" +
            "h\n" +
            "f\n";

    private static void drawPdfAAppearanceString(PdfCanvas canvas, float width, float height, float moveX, float moveY, String appearanceString) {
        canvas.saveState();
        canvas.resetFillColorRgb();
        canvas.concatMatrix(width, 0, 0, height, moveX, moveY);
        canvas.getContentStream().getOutputStream().writeBytes(appearanceString.getBytes(StandardCharsets.ISO_8859_1));
        canvas.restoreState();
    }

    public static void drawPdfACheck(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, check);
    }

    public static void drawPdfACheck(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, check);
    }

    public static void drawPdfACircle(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, circle);
    }

    public static void drawPdfACircle(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, circle);
    }

    public static void drawPdfACross(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, cross);
    }

    public static void drawPdfACross(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, cross);
    }

    public static void drawPdfADiamond(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, diamond);
    }

    public static void drawPdfADiamond(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, diamond);
    }

    public static void drawPdfASquare(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, square);
    }

    public static void drawPdfASquare(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, square);
    }

    public static void drawPdfAStar(PdfCanvas canvas, float width, float height, float moveX, float moveY) {
        drawPdfAAppearanceString(canvas, width, height, moveX, moveY, star);
    }

    public static void drawPdfAStar(PdfCanvas canvas, float width, float height) {
        drawPdfAAppearanceString(canvas, width, height, 0, 0, star);
    }

    public static void drawCross(PdfCanvas canvas, float width, float height, float borderWidth) {
        float offset = borderWidth * 2;
        canvas.
                moveTo((width - height) / 2 + offset, height - offset).
                lineTo((width + height) / 2 - offset, offset).
                moveTo((width + height) / 2 - offset, height - offset).
                lineTo((width - height) / 2 + offset, offset).
                stroke();
    }

    public static void drawCircle(PdfCanvas canvas, float centerX, float centerY, float radius) {
        canvas
                .circle(centerX, centerY, radius)
                .fill();
    }
}
