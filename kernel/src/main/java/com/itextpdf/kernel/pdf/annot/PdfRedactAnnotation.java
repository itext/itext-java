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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;

public class PdfRedactAnnotation extends PdfMarkupAnnotation {

	public PdfRedactAnnotation(Rectangle rect) {
        super(rect);
    }

    /**
     * Instantiates a new {@link PdfRedactAnnotation} instance based on {@link PdfDictionary}
     * instance, that represents existing annotation object in the document.
     *
     * @param pdfObject the {@link PdfDictionary} representing annotation object
     * @see PdfAnnotation#makeAnnotation(PdfObject)
     */
    protected PdfRedactAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Redact;
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, "Variable Text".
     * @return a {@link PdfString} that specifies the default appearance, or null if default appereance is not specified.
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * The default appearance string that shall be used in formatting the text. See ISO-32001 12.7.3.3, "Variable Text".
     * @param appearanceString a {@link PdfString} that specifies the default appearance.
     * @return this {@link PdfMarkupAnnotation} instance.+
     */
    public PdfRedactAnnotation setDefaultAppearance(PdfString appearanceString) {
        return (PdfRedactAnnotation) put(PdfName.DA, appearanceString);
    }

    public PdfRedactAnnotation setDefaultAppearance(AnnotationDefaultAppearance da) {
        return setDefaultAppearance(da.toPdfString());
    }

    public PdfRedactAnnotation setOverlayText(PdfString text){
        return (PdfRedactAnnotation) put(PdfName.OverlayText, text);
    }

    public PdfString getOverlayText() {
        return getPdfObject().getAsString(PdfName.OverlayText);
    }

    public PdfRedactAnnotation setRedactRolloverAppearance(PdfStream stream) {
        return (PdfRedactAnnotation) put(PdfName.RO, stream);
    }

    public PdfStream getRedactRolloverAppearance() {
        return getPdfObject().getAsStream(PdfName.RO);
    }

    public PdfRedactAnnotation setRepeat(PdfBoolean repeat) {
        return (PdfRedactAnnotation) put(PdfName.Repeat, repeat);
    }

    public PdfBoolean getRepeat() {
        return getPdfObject().getAsBoolean(PdfName.Repeat);
    }

    /**
     * An array of 8 × n numbers specifying the coordinates of n quadrilaterals in default user space.
     * Quadrilaterals are used to define the content region that is intended to be removed for a redaction annotation.
     *
     * @return an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     */
    public PdfArray getQuadPoints() {
        return getPdfObject().getAsArray(PdfName.QuadPoints);
    }

    /**
     * Sets n quadrilaterals in default user space by passing an {@link PdfArray} of 8 × n numbers.
     * Quadrilaterals are used to define the content region that is intended to be removed for a redaction annotation.
     *
     * @param quadPoints an {@link PdfArray} of 8 × n numbers specifying the coordinates of n quadrilaterals.
     * @return this {@link PdfRedactAnnotation} instance.
     */
    public PdfRedactAnnotation setQuadPoints(PdfArray quadPoints) {
        return (PdfRedactAnnotation) put(PdfName.QuadPoints, quadPoints);
    }

    /**
     * The interior color which is used to fill the redacted region after the affected content has been removed.
     *
     * @return {@link Color} of either {@link DeviceGray}, {@link DeviceRgb} or {@link DeviceCmyk} type which defines
     * interior color of the annotation, or null if interior color is not specified.
     */
    public Color getInteriorColor() {
        return InteriorColorUtil.parseInteriorColor(getPdfObject().getAsArray(PdfName.IC));
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color which
     * is used to fill the redacted region after the affected content has been removed.
     *
     * @param interiorColor a {@link PdfArray} of numbers in the range 0.0 to 1.0. The number of array elements determines
     *                      the colour space in which the colour is defined: 0 - No colour, transparent; 1 - DeviceGray,
     *                      3 - DeviceRGB, 4 - DeviceCMYK. For the {@link PdfRedactAnnotation} number of elements shall be
     *                      equal to 3 (which defines DeviceRGB colour space).
     * @return this {@link PdfRedactAnnotation} instance.
     */
    public PdfRedactAnnotation setInteriorColor(PdfArray interiorColor) {
        return (PdfRedactAnnotation) put(PdfName.IC, interiorColor);
    }

    /**
     * An array of numbers in the range 0.0 to 1.0 specifying the interior color which
     * is used to fill the redacted region after the affected content has been removed.
     *
     * @param interiorColor an array of floats in the range 0.0 to 1.0.
     * @return this {@link PdfRedactAnnotation} instance.
     */
    public PdfRedactAnnotation setInteriorColor(float[] interiorColor) {
        return setInteriorColor(new PdfArray(interiorColor));
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @return a code specifying the form of quadding (justification), returns the default value if not explicitly specified.
     */
    public int getJustification() {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.intValue();
    }

    /**
     * A code specifying the form of quadding (justification) that is used in displaying the annotation's text:
     * 0 - Left-justified, 1 - Centered, 2 - Right-justified. Default value: 0 (left-justified).
     * @param justification a code specifying the form of quadding (justification).
     * @return this {@link PdfRedactAnnotation} instance.
     */
    public PdfRedactAnnotation setJustification(int justification) {
        return (PdfRedactAnnotation) put(PdfName.Q, new PdfNumber(justification));
    }
}
