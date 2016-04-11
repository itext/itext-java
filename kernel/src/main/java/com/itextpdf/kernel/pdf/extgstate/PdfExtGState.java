/*
    $Id$

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

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 5205219918362853395L;
	
    /**
     * Blend mode constants
     */
    public static PdfName BM_NORMAL = PdfName.Normal;
    public static PdfName BM_MULTIPLY = PdfName.Multiply;
    public static PdfName BM_SCREEN = PdfName.Screen;
    public static PdfName BM_OVERLAY = PdfName.Overlay;
    public static PdfName BM_DARKEN = PdfName.Darken;
    public static PdfName BM_LIGHTEN = PdfName.Lighten;
    public static PdfName BM_COLOR_DODGE = PdfName.ColorDodge;
    public static PdfName BM_COLOR_BURN = PdfName.ColorBurn;
    public static PdfName BM_HARD_LIGHT = PdfName.HardLight;
    public static PdfName BM_SOFT_LIGHT = PdfName.SoftLight;
    public static PdfName BM_DIFFERENCE = PdfName.Difference;
    public static PdfName BM_EXCLUSION = PdfName.Exclusion;

    public static PdfName BM_HUE = PdfName.Hue;
    public static PdfName BM_SATURATION = PdfName.Saturation;
    public static PdfName BM_COLOR = PdfName.Color;
    public static PdfName BM_LUMINOSITY = PdfName.Luminosity;

    public PdfExtGState(PdfDictionary pdfObject) {
        super(pdfObject);
        markObjectAsIndirect(getPdfObject());
    }

    public PdfExtGState() {
        this(new PdfDictionary());
    }

    public Float getLineWidth() {
        return getPdfObject().getAsFloat(PdfName.LW);
    }

    public PdfExtGState setLineWidth(float lineWidth) {
        return put(PdfName.LW, new PdfNumber(lineWidth));
    }


    public Integer getLineCapStyle() {
        return getPdfObject().getAsInt(PdfName.LC);
    }

    public PdfExtGState setLineCapStryle(int lineCapStyle) {
        return put(PdfName.LC, new PdfNumber(lineCapStyle));
    }


    public Integer getLineJoinStyle() {
        return getPdfObject().getAsInt(PdfName.LJ);
    }

    public PdfExtGState setLineJoinStyle(int lineJoinStyle) {
        return put(PdfName.LJ, new PdfNumber(lineJoinStyle));
    }


    public Float getMiterLimit() {
        return getPdfObject().getAsFloat(PdfName.ML);
    }

    public PdfExtGState setMiterLimit(float miterLimit) {
        return put(PdfName.ML, new PdfNumber(miterLimit));
    }


    public PdfArray getDashPattern() {
        return getPdfObject().getAsArray(PdfName.D);
    }

    public PdfExtGState setDashPattern(PdfArray dashPattern) {
        return put(PdfName.D, dashPattern);
    }


    public PdfName getRenderingIntent() {
        return getPdfObject().getAsName(PdfName.RI);
    }

    public PdfExtGState setRenderingIntent(PdfName renderingIntent) {
        return put(PdfName.RI, renderingIntent);
    }


    public Integer getOverprintMode() {
        return getPdfObject().getAsInt(PdfName.OPM);
    }

    public PdfExtGState setOverprintMode(int overprintMode) {
        return put(PdfName.OPM, new PdfNumber(overprintMode));
    }


    public Boolean getFillOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.op);
    }

    public PdfExtGState setFillOverPrintFlag(boolean fillOverprintFlag) {
        return put(PdfName.op, new PdfBoolean(fillOverprintFlag));
    }


    public Boolean getStrokeOverprintFlag() {
        return getPdfObject().getAsBool(PdfName.OP);
    }

    public PdfExtGState setStrokeOverPrintFlag(boolean strokeOverPrintFlag) {
        return put(PdfName.OP, new PdfBoolean(strokeOverPrintFlag));
    }


    public PdfArray getFont() {
        return getPdfObject().getAsArray(PdfName.Font);
    }

    public PdfExtGState setFont(PdfArray font) {
        return put(PdfName.Font, font);
    }

    public PdfObject getBlackGenerationFunction() {
        return getPdfObject().get(PdfName.BG);
    }

    public PdfExtGState setBlackGenerationFunction(PdfObject blackGenerationFunction) {
        return put(PdfName.BG, blackGenerationFunction);
    }

    public PdfObject getBlackGenerationFunction2() {
        return getPdfObject().get(PdfName.BG2);
    }

    public PdfExtGState setBlackGenerationFunction2(PdfObject blackGenerationFunction2) {
        return put(PdfName.BG2, blackGenerationFunction2);
    }

    public PdfObject getUndercolorRemovalFunction() {
        return getPdfObject().get(PdfName.UCR);
    }

    public PdfExtGState setUndercolorRemovalFunction(PdfObject undercolorRemovalFunction) {
        return put(PdfName.UCR, undercolorRemovalFunction);
    }

    public PdfObject getUndercolorRemovalFunction2() {
        return getPdfObject().get(PdfName.UCR2);
    }

    public PdfExtGState setUndercolorRemovalFunction2(PdfObject undercolorRemovalFunction2) {
        return put(PdfName.UCR2, undercolorRemovalFunction2);
    }


    public PdfObject getTransferFunction() {
        return getPdfObject().get(PdfName.TR);
    }

    public PdfExtGState setTransferFunction(PdfObject transferFunction) {
        return put(PdfName.TR, transferFunction);
    }


    public PdfObject getTransferFunction2() {
        return getPdfObject().get(PdfName.TR2);
    }

    public PdfExtGState setTransferFunction2(PdfObject transferFunction) {
        return put(PdfName.TR2, transferFunction);
    }


    public PdfObject getHalftone() {
        return getPdfObject().get(PdfName.HT);
    }

    public PdfExtGState setHalftone(PdfObject halftone) {
        return put(PdfName.HT, halftone);
    }


    public PdfObject getHTP() {
        return getPdfObject().get(PdfName.HTP);
    }

    public PdfExtGState setHTP(PdfObject htp) {
        return put(PdfName.HTP, htp);
    }


    public Float getFlatnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.FT);
    }

    public PdfExtGState setFlatnessTolerance(float flatnessTolerance) {
        return put(PdfName.FT, new PdfNumber(flatnessTolerance));
    }


    public Float getSmothnessTolerance() {
        return getPdfObject().getAsFloat(PdfName.SM);
    }

    public PdfExtGState setSmoothnessTolerance(float smoothnessTolerance) {
        return put(PdfName.SM, new PdfNumber(smoothnessTolerance));
    }


    public Boolean getAutomaticStrokeAdjustmentFlag() {
        return getPdfObject().getAsBool(PdfName.SA);
    }

    public PdfExtGState setAutomaticStrokeAdjustmentFlag(boolean strokeAdjustment) {
        return put(PdfName.SA, new PdfBoolean(strokeAdjustment));
    }


    public PdfObject getBlendMode() {
        return getPdfObject().get(PdfName.BM);
    }

    public PdfExtGState setBlendMode(PdfObject blendMode) {
        return put(PdfName.BM, blendMode);
    }


    public PdfObject getSoftMask() {
        return getPdfObject().get(PdfName.SMask);
    }

    public PdfExtGState setSoftMask(PdfObject sMask) {
        return put(PdfName.SMask, sMask);
    }


    public Float getStrokeOpacity() {
        return getPdfObject().getAsFloat(PdfName.CA);
    }

    public PdfExtGState setStrokeOpacity(float strokingAlphaConstant) {
        return put(PdfName.CA, new PdfNumber(strokingAlphaConstant));
    }


    public Float getFillOpacity() {
        return getPdfObject().getAsFloat(PdfName.ca);
    }

    public PdfExtGState setFillOpacity(float fillingAlphaConstant) {
        return put(PdfName.ca, new PdfNumber(fillingAlphaConstant));
    }


    public Boolean getAlphaSourceFlag() {
        return getPdfObject().getAsBool(PdfName.AIS);
    }

    public PdfExtGState setAlphaSourceFlag(boolean alphaSourceFlag) {
        return put(PdfName.AIS, new PdfBoolean(alphaSourceFlag));
    }


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
