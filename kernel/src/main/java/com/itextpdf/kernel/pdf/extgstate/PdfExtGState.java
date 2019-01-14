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
package com.itextpdf.kernel.pdf.extgstate;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.function.PdfFunction;

/**
 * Graphics state parameter dictionary wrapper.
 * See ISO-320001, 8.4.5 Graphics State Parameter Dictionaries.
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
     * by existed {@link PdfDictionary} object
     *
     * @param pdfObject instance of graphics state parameter dictionary
     */
    public PdfExtGState(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Create default instance of graphics state parameter dictionary
     */
    public PdfExtGState() {
        this(new PdfDictionary());
    }

    /**
     * Gets line width value, {@code LW} key.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getLineWidth() {
        return getPdfObject().getAsFloat(PdfName.LW);
    }

    /**
     * Sets line width value, {@code LW} key.
     *
     * @param lineWidth a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setLineWidth(float lineWidth) {
        return put(PdfName.LW, new PdfNumber(lineWidth));
    }

    /**
     * Gets line gap style value, {@code LC} key.
     *
     * @return 0 - butt cap, 1 - round cap, 2 - projecting square cap.
     */
    public Integer getLineCapStyle() {
        return getPdfObject().getAsInt(PdfName.LC);
    }

    /**
     * Sets line gap style value, {@code LC} key.
     *
     * @param lineCapStyle 0 - butt cap, 1 - round cap, 2 - projecting square cap.
     * @return object itself.
     */
    public PdfExtGState setLineCapStyle(int lineCapStyle) {
        return put(PdfName.LC, new PdfNumber(lineCapStyle));
    }

    /**
     * Gets line join style value, {@code LJ} key.
     *
     * @return 0 - miter join (see also miter limit), 1 - round join, 2 - bevel join.
     */
    public Integer getLineJoinStyle() {
        return getPdfObject().getAsInt(PdfName.LJ);
    }

    /**
     * Sets line join style value, {@code LJ} key.
     *
     * @param lineJoinStyle 0 - miter join (see also miter limit), 1 - round join, 2 - bevel join.
     * @return object itself.
     */
    public PdfExtGState setLineJoinStyle(int lineJoinStyle) {
        return put(PdfName.LJ, new PdfNumber(lineJoinStyle));
    }

    /**
     * Gets miter limit value, {@code ML key}. See also line join style.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getMiterLimit() {
        return getPdfObject().getAsFloat(PdfName.ML);
    }

    /**
     * Sets miter limit value, {@code ML key}. See also line join style.
     *
     * @param miterLimit a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setMiterLimit(float miterLimit) {
        return put(PdfName.ML, new PdfNumber(miterLimit));
    }

    /**
     * Gets line dash pattern value, {@code D} key.
     *
     * @return a {@code PdfArray}, that represents line dash pattern.
     */
    public PdfArray getDashPattern() {
        return getPdfObject().getAsArray(PdfName.D);
    }

    /**
     * Sets line dash pattern value, {@code D} key.
     *
     * @param dashPattern a {@code PdfArray}, that represents line dash pattern.
     * @return object itself.
     */
    public PdfExtGState setDashPattern(PdfArray dashPattern) {
        return put(PdfName.D, dashPattern);
    }

    /**
     * Gets rendering intent value, {@code RI} key.
     * Valid values are: {@code AbsoluteColorimetric}, {@code RelativeColorimetric},
     * {@code Saturation}, {@code Perceptual}.
     *
     * @return a {@code PdfName} instance.
     */
    public PdfName getRenderingIntent() {
        return getPdfObject().getAsName(PdfName.RI);
    }

    /**
     * Sets rendering intent value, {@code RI} key.
     *
     * @param renderingIntent a {@code PdfName} instance, Valid values are: {@code AbsoluteColorimetric},
     *                        {@code RelativeColorimetric}, {@code Saturation}, {@code Perceptual}.
     * @return object itself.
     */
    public PdfExtGState setRenderingIntent(PdfName renderingIntent) {
        return put(PdfName.RI, renderingIntent);
    }

    /**
     * Get overprint flag value for <b>stroking</b> operations, {@code OP} key.
     *
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getStrokeOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.OP);
    }

    /**
     * Set overprint flag value for <b>stroking</b> operations, {@code OP} key.
     *
     * @param strokeOverPrintFlag {@code true}, for applying overprint for <b>stroking</b> operations.
     * @return object itself.
     */
    public PdfExtGState setStrokeOverPrintFlag(boolean strokeOverPrintFlag) {
        return put(PdfName.OP, PdfBoolean.valueOf(strokeOverPrintFlag));
    }

    /**
     * Get overprint flag value for <b>non-stroking</b> operations, {@code op} key.
     *
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getFillOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.op);
    }

    /**
     * Set overprint flag value for <b>non-stroking</b> operations, {@code op} key.
     *
     * @param fillOverprintFlag {@code true}, for applying overprint for <b>non-stroking</b> operations.
     * @return object itself.
     */
    public PdfExtGState setFillOverPrintFlag(boolean fillOverprintFlag) {
        return put(PdfName.op, PdfBoolean.valueOf(fillOverprintFlag));
    }

    /**
     * Get overprint control mode, {@code OPM} key.
     *
     * @return an {@code int} value if exist, otherwise {@code null}.
     */
    public Integer getOverprintMode() {
        return getPdfObject().getAsInt(PdfName.OPM);
    }

    /**
     * Set overprint control mode, {@code OPM} key.
     *
     * @param overprintMode an {@code int} value, see ISO-320001, 8.6.7 Overprint Control.
     * @return object itself.
     */
    public PdfExtGState setOverprintMode(int overprintMode) {
        return put(PdfName.OPM, new PdfNumber(overprintMode));
    }

    /**
     * Gets font and size, {@code Font} key.
     *
     * @return a {@link PdfArray} of the form {@code [font size]}, where
     * {@code font} shall be an indirect reference to a font dictionary and
     * {@code size} shall be a number expressed in text space units.
     */
    public PdfArray getFont() {
        return getPdfObject().getAsArray(PdfName.Font);
    }

    /**
     * Sets font and size, {@code Font} key.
     * NOTE: If you want add the font object which has just been created, make sure to register the font with
     * {@link PdfDocument#addFont(PdfFont)} method first.
     *
     * @param font a {@link PdfArray} of the form {@code [font size]}, where
     *             {@code font} shall be an indirect reference to a font dictionary and
     *             {@code size} shall be a number expressed in text space units.
     * @return object itself.
     */
    public PdfExtGState setFont(PdfArray font) {
        return put(PdfName.Font, font);
    }

    /**
     * Gets the black-generation function value, {@code BG}.
     *
     * @return a {@link PdfObject}, should be {@link PdfFunction}.
     */
    public PdfObject getBlackGenerationFunction() {
        return getPdfObject().get(PdfName.BG);
    }

    /**
     * Sets the black-generation function value, {@code BG}.
     *
     * @param blackGenerationFunction a {@link PdfObject}, shall be {@link PdfFunction}.
     * @return object itself.
     */
    public PdfExtGState setBlackGenerationFunction(PdfObject blackGenerationFunction) {
        return put(PdfName.BG, blackGenerationFunction);
    }

    /**
     * Gets the black-generation function value or {@code Default}, {@code BG2} key.
     *
     * @return a {@link PdfObject} value, should be either {@link PdfFunction} or {@link PdfName}.
     */
    public PdfObject getBlackGenerationFunction2() {
        return getPdfObject().get(PdfName.BG2);
    }

    /**
     * Sets the black-generation function value or {@code Default}, {@code BG2} key.
     * Note, if both {@code BG} and {@code BG2} are present in the same graphics state parameter dictionary,
     * {@code BG2} takes precedence.
     *
     * @param blackGenerationFunction2 a {@link PdfObject} value, shall be either {@link PdfFunction}
     *                                 or {@code Default}.
     * @return object itself.
     */
    public PdfExtGState setBlackGenerationFunction2(PdfObject blackGenerationFunction2) {
        return put(PdfName.BG2, blackGenerationFunction2);
    }

    /**
     * Gets the undercolor-removal function, {@code UCR} key.
     *
     * @return a {@link PdfObject}, should be {@link PdfFunction}.
     */
    public PdfObject getUndercolorRemovalFunction() {
        return getPdfObject().get(PdfName.UCR);
    }

    /**
     * Sets the undercolor-removal function, {@code UCR} key.
     *
     * @param undercolorRemovalFunction a {@link PdfObject}, shall be {@link PdfFunction}.
     * @return object itself.
     */
    public PdfExtGState setUndercolorRemovalFunction(PdfObject undercolorRemovalFunction) {
        return put(PdfName.UCR, undercolorRemovalFunction);
    }

    /**
     * Gets the undercolor-removal function value or {@code Default}, {@code UCR2} key.
     *
     * @return a {@link PdfObject} value, should be either {@link PdfFunction} or {@link PdfName}.
     */
    public PdfObject getUndercolorRemovalFunction2() {
        return getPdfObject().get(PdfName.UCR2);
    }

    /**
     * Sets the undercolor-removal function value or {@code Default}, {@code UCR2} key.
     * Note, if both {@code UCR} and {@code UCR2} are present in the same graphics state parameter dictionary,
     * {@code UCR2} takes precedence.
     *
     * @param undercolorRemovalFunction2 a {@link PdfObject} value, shall be either {@link PdfFunction}
     *                                   or {@code Default}.
     * @return object itself.
     */
    public PdfExtGState setUndercolorRemovalFunction2(PdfObject undercolorRemovalFunction2) {
        return put(PdfName.UCR2, undercolorRemovalFunction2);
    }

    /**
     * Gets the transfer function value, {@code TR} key.
     *
     * @return a {@link PdfObject}, should be either {@link PdfFunction},
     * {@link PdfArray} or {@link PdfName}.
     */
    public PdfObject getTransferFunction() {
        return getPdfObject().get(PdfName.TR);
    }

    /**
     * Sets the transfer function value, {@code TR} key.
     *
     * @param transferFunction a {@link PdfObject}, shall be either {@link PdfFunction},
     *                         {@link PdfArray} or {@link PdfName}.
     * @return object itself.
     */
    public PdfExtGState setTransferFunction(PdfObject transferFunction) {
        return put(PdfName.TR, transferFunction);
    }

    /**
     * Gets the transfer function value or {@code Default}, {@code TR2} key.
     *
     * @return a {@link PdfObject}, should be either {@link PdfFunction},
     * {@link PdfArray} or {@link PdfName}.
     */
    public PdfObject getTransferFunction2() {
        return getPdfObject().get(PdfName.TR2);
    }

    /**
     * Sets the transfer function value or {@code Default}, {@code TR2} key.
     * Note, if both {@code TR} and {@code TR2} are present in the same graphics state parameter dictionary,
     * {@code TR2} takes precedence.
     *
     * @param transferFunction2 a {@link PdfObject}, shall be either {@link PdfFunction},
     *                          {@link PdfArray}, {@link PdfName} or {@code Default}.
     * @return object itself.
     */
    public PdfExtGState setTransferFunction2(PdfObject transferFunction2) {
        return put(PdfName.TR2, transferFunction2);
    }

    /**
     * Gets the halftone dictionary, stream or {@code Default}, {@code HT} key.
     *
     * @return a {@link PdfObject}, should be either {@link PdfDictionary},
     * {@link PdfStream} or {@link PdfName}.
     */
    public PdfObject getHalftone() {
        return getPdfObject().get(PdfName.HT);
    }

    /**
     * Sets the halftone or {@code Default}, {@code HT} key.
     *
     * @param halftone a {@link PdfObject}, shall be either {@link PdfDictionary},
     *                 {@link PdfStream} or {@link PdfName}.
     * @return object itself.
     */
    public PdfExtGState setHalftone(PdfObject halftone) {
        return put(PdfName.HT, halftone);
    }

    /**
     * Gets the flatness tolerance value, {@code FL} key.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getFlatnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.FL);
    }

    /**
     * Sets the flatness tolerance value, {@code FL} key.
     *
     * @param flatnessTolerance a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setFlatnessTolerance(float flatnessTolerance) {
        return put(PdfName.FL, new PdfNumber(flatnessTolerance));
    }

    /**
     * Gets the smoothness tolerance value, {@code SM} key.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getSmothnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.SM);
    }

    /**
     * Sets the smoothness tolerance value, {@code SM} key.
     *
     * @param smoothnessTolerance a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setSmoothnessTolerance(float smoothnessTolerance) {
        return put(PdfName.SM, new PdfNumber(smoothnessTolerance));
    }

    /**
     * Gets value of an automatic stroke adjustment flag, {@code SA} key.
     *
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getAutomaticStrokeAdjustmentFlag() {
        return getPdfObject().getAsBool(PdfName.SA);
    }

    /**
     * Sets value of an automatic stroke adjustment flag, {@code SA} key.
     *
     * @param strokeAdjustment a {@code boolean} value.
     * @return object itself.
     */
    public PdfExtGState setAutomaticStrokeAdjustmentFlag(boolean strokeAdjustment) {
        return put(PdfName.SA, PdfBoolean.valueOf(strokeAdjustment));
    }

    /**
     * Gets the current blend mode for the transparent imaging model, {@code BM} key.
     *
     * @return a {@link PdfObject}, should be either {@link PdfName} or {@link PdfArray}. array is deprecated in PDF 2.0.
     */
    public PdfObject getBlendMode() {
        return getPdfObject().get(PdfName.BM);
    }

    /**
     * Sets the current blend mode for the transparent imaging model, {@code BM} key.
     *
     * @param blendMode a {@link PdfObject}, shall be either {@link PdfName} or {@link PdfArray}; array is deprecated in PDF 2.0.
     * @return object itself.
     */
    public PdfExtGState setBlendMode(PdfObject blendMode) {
        return put(PdfName.BM, blendMode);
    }

    /**
     * Gets the current soft mask, {@code SMask} key.
     *
     * @return a {@link PdfObject}, should be either {@link PdfName} or {@link PdfDictionary}.
     */
    public PdfObject getSoftMask() {
        return getPdfObject().get(PdfName.SMask);
    }

    /**
     * Sets the current soft mask, {@code SMask} key.
     *
     * @param sMask a {@link PdfObject}, shall be either {@link PdfName} or {@link PdfDictionary}.
     * @return object itself.
     */
    public PdfExtGState setSoftMask(PdfObject sMask) {
        return put(PdfName.SMask, sMask);
    }

    /**
     * Gets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>stroking</b> operations in the transparent imaging model, {@code CA} key.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getStrokeOpacity() {
        return getPdfObject().getAsFloat(PdfName.CA);
    }

    /**
     * Sets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>stroking</b> operations in the transparent imaging model, {@code CA} key.
     *
     * @param strokingAlphaConstant a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setStrokeOpacity(float strokingAlphaConstant) {
        return put(PdfName.CA, new PdfNumber(strokingAlphaConstant));
    }

    /**
     * Gets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>non-stroking</b> operations in the transparent imaging model, {@code ca} key.
     *
     * @return a {@code float} value if exist, otherwise {@code null}.
     */
    public Float getFillOpacity() {
        return getPdfObject().getAsFloat(PdfName.ca);
    }

    /**
     * Sets the current alpha constant, specifying the constant shape or constant opacity value
     * for <b>non-stroking</b> operations in the transparent imaging model, {@code ca} key.
     *
     * @param fillingAlphaConstant a {@code float} value.
     * @return object itself.
     */
    public PdfExtGState setFillOpacity(float fillingAlphaConstant) {
        return put(PdfName.ca, new PdfNumber(fillingAlphaConstant));
    }

    /**
     * Gets the alpha source flag ("alpha is shape"), specifying whether the current soft mask and alpha constant
     * shall be interpreted as shape values ({@code true}) or opacity values ({@code false}), {@code AIS} key.
     *
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getAlphaSourceFlag() {
        return getPdfObject().getAsBool(PdfName.AIS);
    }

    /**
     * Sets the alpha source flag ("alpha is shape"), specifying whether the current soft mask and alpha constant
     * shall be interpreted as shape values ({@code true}) or opacity values ({@code false}), {@code AIS} key.
     *
     * @param alphaSourceFlag if {@code true} - alpha as shape values, if {@code false} — as opacity values.
     * @return object itself.
     */
    public PdfExtGState setAlphaSourceFlag(boolean alphaSourceFlag) {
        return put(PdfName.AIS, PdfBoolean.valueOf(alphaSourceFlag));
    }

    /**
     * Gets the text knockout flag, which determine the behaviour of overlapping glyphs
     * within a text object in the transparent imaging model, {@code TK} key.
     *
     * @return a {@code boolean} value if exist, otherwise {@code null}.
     */
    public Boolean getTextKnockoutFlag() {
        return getPdfObject().getAsBool(PdfName.TK);
    }

    /**
     * Sets the text knockout flag, which determine the behaviour of overlapping glyphs
     * within a text object in the transparent imaging model, {@code TK} key.
     *
     * @param textKnockoutFlag {@code true} if enabled.
     * @return object itself.
     */
    public PdfExtGState setTextKnockoutFlag(boolean textKnockoutFlag) {
        return put(PdfName.TK, PdfBoolean.valueOf(textKnockoutFlag));
    }

    /**
     * PDF 2.0. This graphics state parameter controls whether black point
     * compensation is performed while doing CIE-based colour conversions.
     *
     * @param useBlackPointCompensation <code>true</code> to enable, <code>false</code> to disable
     * @return object itself
     */
    public PdfExtGState setUseBlackPointCompensation(boolean useBlackPointCompensation) {
        return put(PdfName.UseBlackPtComp, useBlackPointCompensation ? PdfName.ON : PdfName.OFF);
    }

    /**
     * PDF 2.0. Checks whether the black point compensation is performed while doing CIE-based colour conversions.
     *
     * @return <code>true</code> if black point compensation is used, <code>false</code> if it is not used, or
     * <code>null</code> is the value is set to Default, or not set at all
     */
    public Boolean isBlackPointCompensationUsed() {
        PdfName useBlackPointCompensation = getPdfObject().getAsName(PdfName.UseBlackPtComp);
        if (PdfName.ON.equals(useBlackPointCompensation)) {
            return true;
        } else if (PdfName.OFF.equals(useBlackPointCompensation)) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * PDF 2.0. Sets halftone origin
     *
     * @param x X location of the halftone origin in the current coordinate system
     * @param y Y location of the halftone origin in the current coordinate system
     * @return this {@link PdfExtGState} instance
     */
    public PdfExtGState setHalftoneOrigin(float x, float y) {
        PdfArray hto = new PdfArray();
        hto.add(new PdfNumber(x));
        hto.add(new PdfNumber(y));
        return put(PdfName.HTO, hto);
    }

    /**
     * PDF 2.0. Gets halftone origin
     *
     * @return an array of two values specifying X and Y values of the halftone origin in the current coordinate system,
     * respectively, or <code>null</code> if halftone origin is not specified
     */
    public float[] getHalftoneOrigin() {
        PdfArray hto = getPdfObject().getAsArray(PdfName.HTO);
        if (hto != null && hto.size() == 2 && hto.get(0).isNumber() && hto.get(1).isNumber()) {
            return new float[]{hto.getAsNumber(0).floatValue(), hto.getAsNumber(1).floatValue()};
        } else {
            return null;
        }
    }

    /**
     * Puts the value into Graphics state parameter dictionary and associates it with the specified key.
     * If the key is already present, it will override the old value with the specified one.
     *
     * @param key   key to insert or to override
     * @param value the value to associate with the specified key
     * @return object itself.
     */
    public PdfExtGState put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
