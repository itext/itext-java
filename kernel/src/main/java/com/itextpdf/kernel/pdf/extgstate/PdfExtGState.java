/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf.extgstate;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.function.PdfFunction;

/**
 * Graphics state parameter dictionary wrapper
 */
public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 5205219918362853395L;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_NORMAL = PdfName.Normal;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_MULTIPLY = PdfName.Multiply;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_SCREEN = PdfName.Screen;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_OVERLAY = PdfName.Overlay;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_DARKEN = PdfName.Darken;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_LIGHTEN = PdfName.Lighten;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_COLOR_DODGE = PdfName.ColorDodge;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_COLOR_BURN = PdfName.ColorBurn;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_HARD_LIGHT = PdfName.HardLight;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_SOFT_LIGHT = PdfName.SoftLight;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_DIFFERENCE = PdfName.Difference;
    /**
     * Standard separable blend mode. See ISO-320001, table 136
     */
    public static PdfName BM_EXCLUSION = PdfName.Exclusion;

    /**
     * Standard nonseparable blend mode. See ISO-320001, table 137
     */
    public static PdfName BM_HUE = PdfName.Hue;
    /**
     * Standard nonseparable blend mode. See ISO-320001, table 137
     */
    public static PdfName BM_SATURATION = PdfName.Saturation;
    /**
     * Standard nonseparable blend mode. See ISO-320001, table 137
     */
    public static PdfName BM_COLOR = PdfName.Color;
    /**
     * Standard nonseparable blend mode. See ISO-320001, table 137
     */
    public static PdfName BM_LUMINOSITY = PdfName.Luminosity;

    /**
     * Create instance of graphics state parameter dictionary wrapper
     * by existed {@see PdfDictionary} object
     * @param pdfObject instance of graphics state parameter dictionary
     */
    public PdfExtGState(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    /**
     * Create default instance of graphics state parameter dictionary
     */
    public PdfExtGState() {
        this(new PdfDictionary());
    }

    /**
     * Gets line width value, {@code LW} key.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getLineWidth() {
        return getPdfObject().getAsFloat(PdfName.LW);
    }

    public PdfExtGState setLineWidth(float lineWidth) {
        return put(PdfName.LW, new PdfNumber(lineWidth));
    }

    /**
     * Gets line gap style value, {@code LC} key.
     * @return 0 - butt cap, 1 - round cap, 2 - projecting square cap.
     */
    public Integer getLineCapStyle() {
        return getPdfObject().getAsInt(PdfName.LC);
    }

    public PdfExtGState setLineCapStyle(int lineCapStyle) {
        return put(PdfName.LC, new PdfNumber(lineCapStyle));
    }

    /**
     * Gets line join style value, {@code LJ} key.
     * @return 0 - miter join (see also miter limit), 1 - round join, 2 - bevel join.
     */
    public Integer getLineJoinStyle() {
        return getPdfObject().getAsInt(PdfName.LJ);
    }

    public PdfExtGState setLineJoinStyle(int lineJoinStyle) {
        return put(PdfName.LJ, new PdfNumber(lineJoinStyle));
    }

    /**
     * Gets miter limit value, {@code ML key}. See also line join style.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getMiterLimit() {
        return getPdfObject().getAsFloat(PdfName.ML);
    }

    public PdfExtGState setMiterLimit(float miterLimit) {
        return put(PdfName.ML, new PdfNumber(miterLimit));
    }

    /**
     * Gets line dash pattern value, {@code D} key.
     * @return a {@code PdfArray}, that represents line dash pattern.
     */
    public PdfArray getDashPattern() {
        return getPdfObject().getAsArray(PdfName.D);
    }

    public PdfExtGState setDashPattern(PdfArray dashPattern) {
        return put(PdfName.D, dashPattern);
    }

    /**
     * Gets rendering intent value, {@code RI} key.
     * Valid values are: {@code AbsoluteColorimetric}, {@code RelativeColorimetric},
     * {@code Saturation}, {@code Perceptual}.
     * @return a {@code PdfName} instance.
     */
    public PdfName getRenderingIntent() {
        return getPdfObject().getAsName(PdfName.RI);
    }

    public PdfExtGState setRenderingIntent(PdfName renderingIntent) {
        return put(PdfName.RI, renderingIntent);
    }

    /**
     * Get overprint flag value for stroke operations, {@code OP} key.
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getStrokeOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.OP);
    }

    public PdfExtGState setStrokeOverPrintFlag(boolean strokeOverPrintFlag) {
        return put(PdfName.OP, new PdfBoolean(strokeOverPrintFlag));
    }

    /**
     * Gets overprint flag value, {@code op} key.
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getFillOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.op);
    }

    public PdfExtGState setFillOverPrintFlag(boolean fillOverprintFlag) {
        return put(PdfName.op, new PdfBoolean(fillOverprintFlag));
    }

    /**
     * Get overprint control mode, {@code OPM} key.
     * @return a {@code int} value if exist, otherwise {@code null}.
     */
    public Integer getOverprintMode() {
        return getPdfObject().getAsInt(PdfName.OPM);
    }

    public PdfExtGState setOverprintMode(int overprintMode) {
        return put(PdfName.OPM, new PdfNumber(overprintMode));
    }

    /**
     * Gets font, {@code Font} key.
     * @return a {@see PdfFont} instance.
     */
    public PdfArray getFont() {
        return getPdfObject().getAsArray(PdfName.Font);
    }

    public PdfExtGState setFont(PdfArray font) {
        return put(PdfName.Font, font);
    }

    /**
     * Gets the black-generation function value, {@code BG}.
     * @return a {@link PdfObject}, represents {@link PdfFunction}.
     */
    public PdfObject getBlackGenerationFunction() {
        return getPdfObject().get(PdfName.BG);
    }

    public PdfExtGState setBlackGenerationFunction(PdfObject blackGenerationFunction) {
        return put(PdfName.BG, blackGenerationFunction);
    }

    /**
     * Gets the black-generation function value or {@code Default}, {@code BG2} key.
     * @return {@link PdfObject} value, represents either {@link PdfFunction} or {@link PdfName}.
     */
    public PdfObject getBlackGenerationFunction2() {
        return getPdfObject().get(PdfName.BG2);
    }

    /**
     * Note, if both {@code BG} and {@code BG2} are present in the same graphics state parameter dictionary,
     * {@code BG2} takes precedence.
     * @param blackGenerationFunction2
     * @return
     */
    public PdfExtGState setBlackGenerationFunction2(PdfObject blackGenerationFunction2) {
        return put(PdfName.BG2, blackGenerationFunction2);
    }

    /**
     * Gets the undercolor-removal function, {@code UCR} key.
     * @return a {@link PdfObject}, represents {@link PdfFunction}.
     */
    public PdfObject getUndercolorRemovalFunction() {
        return getPdfObject().get(PdfName.UCR);
    }

    public PdfExtGState setUndercolorRemovalFunction(PdfObject undercolorRemovalFunction) {
        return put(PdfName.UCR, undercolorRemovalFunction);
    }

    /**
     * Gets the undercolor-removal function value or {@code Default}, {@code UCR2} key.
     * @return {@link PdfObject} value, represents either {@link PdfFunction} or {@link PdfName}.
     */
    public PdfObject getUndercolorRemovalFunction2() {
        return getPdfObject().get(PdfName.UCR2);
    }

    /**
     * Note, if both {@code UCR} and {@code UCR2} are present in the same graphics state parameter dictionary,
     * {@code UCR2} takes precedence.
     * @param undercolorRemovalFunction2
     * @return
     */
    public PdfExtGState setUndercolorRemovalFunction2(PdfObject undercolorRemovalFunction2) {
        return put(PdfName.UCR2, undercolorRemovalFunction2);
    }

    /**
     * Gets the transfer function value, {@code TR} key.
     * @return a {@link PdfObject}, represents either {@link PdfFunction},
     * {@link PdfArray} or {@link PdfName}.
     */
    public PdfObject getTransferFunction() {
        return getPdfObject().get(PdfName.TR);
    }

    public PdfExtGState setTransferFunction(PdfObject transferFunction) {
        return put(PdfName.TR, transferFunction);
    }

    /**
     * Gets the transfer function value or {@code Default}, {@code TR2} key.
     * @return a {@link PdfObject}, represents either {@link PdfFunction},
     * {@link PdfArray} or {@link PdfName}.
     */
    public PdfObject getTransferFunction2() {
        return getPdfObject().get(PdfName.TR2);
    }

    /**
     * Note, if both {@code TR} and {@code TR2} are present in the same graphics state parameter dictionary,
     * {@code TR2} takes precedence.
     * @param transferFunction
     * @return
     */
    public PdfExtGState setTransferFunction2(PdfObject transferFunction) {
        return put(PdfName.TR2, transferFunction);
    }

    /**
     * Gets the halftone dictionary or stream or {@code Default}, {@code HT} key.
     * @return a {@link PdfObject}, represents either {@link PdfDictionary},
     * {@link PdfStream} or {@link PdfName}.
     */
    public PdfObject getHalftone() {
        return getPdfObject().get(PdfName.HT);
    }

    public PdfExtGState setHalftone(PdfObject halftone) {
        return put(PdfName.HT, halftone);
    }

    /**
     * Gets {@code HTP} key.
     */
    @Deprecated
    public PdfObject getHTP() {
        return getPdfObject().get(PdfName.HTP);
    }

    @Deprecated
    public PdfExtGState setHTP(PdfObject htp) {
        return put(PdfName.HTP, htp);
    }

    /**
     * Gets the flatness tolerance value, {@code FL} key.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getFlatnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.FL);
    }

    public PdfExtGState setFlatnessTolerance(float flatnessTolerance) {
        return put(PdfName.FL, new PdfNumber(flatnessTolerance));
    }

    /**
     * Gets the smoothness tolerance value, {@code SM} key.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getSmothnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.SM);
    }

    public PdfExtGState setSmoothnessTolerance(float smoothnessTolerance) {
        return put(PdfName.SM, new PdfNumber(smoothnessTolerance));
    }

    /**
     * Gets value of an automatic stroke adjustment flag, {@code SA} key.
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getAutomaticStrokeAdjustmentFlag() {
        return getPdfObject().getAsBool(PdfName.SA);
    }

    public PdfExtGState setAutomaticStrokeAdjustmentFlag(boolean strokeAdjustment) {
        return put(PdfName.SA, new PdfBoolean(strokeAdjustment));
    }

    /**
     * Gets the current blend mode for the transparent imaging model, {@code BM} key.
     * @return a {@link PdfObject}, represents either {@link PdfName} or {@link PdfArray}.
     */
    public PdfObject getBlendMode() {
        return getPdfObject().get(PdfName.BM);
    }

    public PdfExtGState setBlendMode(PdfObject blendMode) {
        return put(PdfName.BM, blendMode);
    }

    /**
     * Gets the current soft mask, {@code SMask} key.
     * @return a {@link PdfObject}, represents either {@link PdfName} or {@link PdfDictionary}.
     */
    public PdfObject getSoftMask() {
        return getPdfObject().get(PdfName.SMask);
    }

    public PdfExtGState setSoftMask(PdfObject sMask) {
        return put(PdfName.SMask, sMask);
    }

    /**
     * Gets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>stroking</b> operations in the transparent imaging model, {@code CA} key.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getStrokeOpacity() {
        return getPdfObject().getAsFloat(PdfName.CA);
    }

    public PdfExtGState setStrokeOpacity(float strokingAlphaConstant) {
        return put(PdfName.CA, new PdfNumber(strokingAlphaConstant));
    }

    /**
     * Gets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>nonstroking</b> operations in the transparent imaging model, {@code ca} key.
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getFillOpacity() {
        return getPdfObject().getAsFloat(PdfName.ca);
    }

    public PdfExtGState setFillOpacity(float fillingAlphaConstant) {
        return put(PdfName.ca, new PdfNumber(fillingAlphaConstant));
    }

    /**
     * Gets the alpha source flag (“alpha is shape”), specifying whether the current soft mask and alpha constant
     * shall be interpreted as shape values ({@code true}) or opacity values ({@code false}), {@code AIS} key.
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getAlphaSourceFlag() {
        return getPdfObject().getAsBool(PdfName.AIS);
    }

    public PdfExtGState setAlphaSourceFlag(boolean alphaSourceFlag) {
        return put(PdfName.AIS, new PdfBoolean(alphaSourceFlag));
    }

    /**
     * Gets the text knockout flag, which determine the behaviour of overlapping glyphs
     * within a text object in the transparent imaging model, {@code TK} key.
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getTextKnockoutFlag() {
        return getPdfObject().getAsBool(PdfName.TK);
    }

    public PdfExtGState setTextKnockoutFlag(boolean textKnockoutFlag) {
        return put(PdfName.TK, new PdfBoolean(textKnockoutFlag));
    }

    public PdfExtGState put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
