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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

import java.util.Arrays;

/**
 * This class is designed for internal usage. <br>
 * Use <code>PdfExtGState</code> class and <code>PdfCanvas#setExtGState()</code> method for setting extended graphics properties.
 */
public class CanvasGraphicsState {

    /**
     * The current transformation matrix, which maps positions from user coordinates to device coordinates.
     * <p>
     * We use an identity matrix as a default value, but in spec a default value is:
     * "a matrix that transforms default user coordinates to device coordinates".
     */
    private Matrix ctm = new Matrix();

    // color
    private Color strokeColor = DeviceGray.BLACK;
    private Color fillColor = DeviceGray.BLACK;

    // text state
    private float charSpacing = 0f;
    private float wordSpacing = 0f;
    // horizontal scaling
    private float scale = 100f;
    private float leading = 0f;
    private PdfFont font;
    private float fontSize;
    private int textRenderingMode = PdfCanvasConstants.TextRenderingMode.FILL;
    private float textRise = 0f;
    private boolean textKnockout = true;

    private float lineWidth = 1f;
    private int lineCapStyle = PdfCanvasConstants.LineCapStyle.BUTT;
    private int lineJoinStyle = PdfCanvasConstants.LineJoinStyle.MITER;
    private float miterLimit = 10f;

    /**
     * A description of the dash pattern to be used when paths are stroked. Default value is solid line.
     * <p>
     * The line dash pattern is expressed as an array of the form [ dashArray dashPhase ],
     * where dashArray is itself an array and dashPhase is an integer.
     * <p>
     * An empty dash array (first element in the array) and zero phase (second element in the array)
     * can be used to restore the dash pattern to a solid line.
     */
    private PdfArray dashPattern = new PdfArray(Arrays.asList(new PdfObject[]{new PdfArray(), new PdfNumber(0)}));

    private PdfName renderingIntent = PdfName.RelativeColorimetric;
    private boolean automaticStrokeAdjustment = false;
    private PdfObject blendMode = PdfName.Normal;
    private PdfObject softMask = PdfName.None;

    // alpha constant
    private float strokeAlpha = 1f;
    private float fillAlpha = 1f;
    // alpha source
    private boolean alphaIsShape = false;

    private boolean strokeOverprint = false;
    private boolean fillOverprint = false;
    private int overprintMode = 0;
    private PdfObject blackGenerationFunction;
    private PdfObject blackGenerationFunction2;
    private PdfObject underColorRemovalFunction;
    private PdfObject underColorRemovalFunction2;
    private PdfObject transferFunction;
    private PdfObject transferFunction2;
    private PdfObject halftone;
    private float flatnessTolerance = 1f;
    private Float smoothnessTolerance;
    private PdfObject htp;

    /**
     * Internal empty and default constructor.
     */
    protected CanvasGraphicsState() {

    }

    /**
     * Copy constructor.
     *
     * @param source the Graphics State to copy from
     */
    public CanvasGraphicsState(CanvasGraphicsState source) {
        copyFrom(source);
    }

    /**
     * Updates this object with the values from a dictionary.
     *
     * @param extGState the dictionary containing source parameters
     */
    public void updateFromExtGState(PdfDictionary extGState) {
        updateFromExtGState(new PdfExtGState(extGState), extGState.getIndirectReference() == null ? null : extGState.getIndirectReference().getDocument());
    }

    /**
     * @return current transformation matrix.
     */
    public Matrix getCtm() {
        return ctm;
    }

    /**
     * Updates current transformation matrix.
     * The third column will always be [0 0 1]
     *
     * @param a element at (1,1) of the transformation matrix
     * @param b element at (1,2) of the transformation matrix
     * @param c element at (2,1) of the transformation matrix
     * @param d element at (2,2) of the transformation matrix
     * @param e element at (3,1) of the transformation matrix
     * @param f element at (3,2) of the transformation matrix
     */
    public void updateCtm(float a, float b, float c, float d, float e, float f) {
        updateCtm(new Matrix(a, b, c, d, e, f));
    }

    /**
     * Updates current transformation matrix.
     *
     * @param newCtm new current transformation matrix.
     */
    public void updateCtm(Matrix newCtm) {
        ctm = newCtm.multiply(ctm);
    }

    /**
     * Gets the current fill color.
     *
     * @return The canvas graphics state fill {@link Color color}
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets the current fill {@link Color color}.
     *
     * @param fillColor The new fill color.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Gets the current stroke color.
     *
     * @return The canvas graphics state stroke {@link Color color}
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Sets the current stroke {@link Color color}.
     *
     * @param strokeColor The new stroke color.
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Gets the current line width.
     *
     * @return The canvas graphics state line width.
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     *  Sets the current line width.
     *
     * @param lineWidth The new line width.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }


    /**
     * Gets the current line cap style, see ISO-320001, 8.4.3.3 Line Cap Style.
     *
     * @return The current cap style.
     * @see PdfCanvas#setLineCapStyle(int) for more info.
     */
    public int getLineCapStyle() {
        return lineCapStyle;
    }

    /**
     * Sets the current line cap style, see ISO-320001, 8.4.3.3 Line Cap Style.
     *
     * @param lineCapStyle The new cap style value.
     * @see PdfCanvas#setLineCapStyle(int) for more info.
     */
    public void setLineCapStyle(int lineCapStyle) {
        this.lineCapStyle = lineCapStyle;
    }

    /**
     * Gets the current line join style, see ISO-320001, 8.4.3.4 Line Join Style.
     *
     * @return The current line join style.
     * @see PdfCanvas#setLineJoinStyle(int) for more info.
     */
    public int getLineJoinStyle() {
        return lineJoinStyle;
    }

    /**
     * Sets the current line join style, see ISO-320001, 8.4.3.4 Line Join Style.
     *
     * @param lineJoinStyle The new line join style value.
     * @see PdfCanvas#setLineJoinStyle(int) for more info.
     */
    public void setLineJoinStyle(int lineJoinStyle) {
        this.lineJoinStyle = lineJoinStyle;
    }

    /**
     * Gets the current miter limit, see ISO-320001, 8.4.3.5 Miter Limit.
     *
     * @return The current miter limit.
     * @see PdfCanvas#setMiterLimit(float) for more info.
     */
    public float getMiterLimit() {
        return miterLimit;
    }

    /**
     * Sets the current miter limit, see ISO-320001, 8.4.3.5 Miter Limit.
     *
     * @param miterLimit The new miter limit value.
     * @see PdfCanvas#setMiterLimit(float) for more info.
     */
    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }


    /**
     * Gets line dash pattern value, {@code D} key, see ISO-320001, 8.4.3.6 Line Dash Pattern,
     * {@link com.itextpdf.kernel.pdf.extgstate.PdfExtGState#setDashPattern }.
     *
     * @return a {@code PdfArray}, that represents line dash pattern.
     */
    public PdfArray getDashPattern() {
        return dashPattern;
    }

    /**
     * Sets line dash pattern value, {@code D} key, see ISO-320001, 8.4.3.6 Line Dash Pattern,
     * {@link com.itextpdf.kernel.pdf.extgstate.PdfExtGState#setDashPattern }.
     *
     * @param dashPattern a {@code PdfArray}, that represents line dash pattern.
     */
    public void setDashPattern(PdfArray dashPattern) {
        this.dashPattern = dashPattern;
    }

    /**
     * Gets the rendering intent, see {@link PdfExtGState#getRenderingIntent()}.
     *
     * @return the rendering intent name.
     */
    public PdfName getRenderingIntent() {
        return renderingIntent;
    }
    /**
     * Sets the rendering intent, see {@link PdfExtGState#getRenderingIntent()}.
     *
     * @param renderingIntent the rendering intent name.
     */
    public void setRenderingIntent(PdfName renderingIntent) {
        this.renderingIntent = renderingIntent;
    }

    /**
     * Gets the font size.
     *
     * @return The current font size.
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size.
     *
     * @param fontSize The new font size.
     */

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Gets the current {@link PdfFont}.
     *
     * @return The current {@link PdfFont}.
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Sets the current {@link PdfFont}.
     *
     * @param font The new {@link PdfFont}.
     */
    public void setFont(PdfFont font) {
        this.font = font;
    }

    /**
     * Gets the current Text Rendering Mode, see ISO-320001, 9.3.6 Text Rendering Mode,
     * {@link PdfCanvas#setTextRenderingMode}.
     *
     * @return The current text rendering mode.
     */
    public int getTextRenderingMode() {
        return textRenderingMode;
    }

    /**
     * Sets the current Text Rendering Mode, see ISO-320001, 9.3.6 Text Rendering Mode,
     * {@link PdfCanvas#setTextRenderingMode}.
     *
     * @param textRenderingMode The new text rendering mode.
     */
    public void setTextRenderingMode(int textRenderingMode) {
        this.textRenderingMode = textRenderingMode;
    }

    /**
     * Get the current Text Rise, see ISO-320001, 9.3.7 Text Rise,
     * {@link PdfCanvas#setTextRise}.
     *
     * @return The current text rise.
     */
    public float getTextRise() {
        return textRise;
    }

    /**
     * Set the current Text Rise, see ISO-320001, 9.3.7 Text Rise
     * {@link PdfCanvas#setTextRise}.
     *
     * @param textRise The new text rise value.
     */
    public void setTextRise(float textRise) {
        this.textRise = textRise;
    }

    /**
     * Gets the current Flatness Tolerance, see ISO-320001, 10.6.2 Flatness Tolerance,
     * {@link PdfCanvas#setFlatnessTolerance(float)}.
     *
     * @return The current flatness tolerance.
     */
    public float getFlatnessTolerance() {
        return flatnessTolerance;
    }

    /**
     * Sets the current Flatness Tolerance, see ISO-320001, 10.6.2 Flatness Tolerance,
     * {@link PdfCanvas#setFlatnessTolerance(float)}.
     *
     * @param flatnessTolerance The new flatness tolerance value.
     */
    public void setFlatnessTolerance(float flatnessTolerance) {
        this.flatnessTolerance = flatnessTolerance;
    }

    /**
     * Sets the Word Spacing, see ISO-320001, 9.3.3 Word Spacing,
     * {@link PdfCanvas#setWordSpacing(float)}.
     *
     * @param wordSpacing The new word spacing value.
     */
    public void setWordSpacing(float wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    /**
     * Gets the current Word Spacing, see ISO-320001, 9.3.3 Word Spacing,
     * {@link PdfCanvas#setWordSpacing(float)}
     *
     * @return The current word spacing
     */
    public float getWordSpacing() {
        return wordSpacing;
    }

    /**
     * Sets the Character Spacing, see ISO-320001, 9.3.2 Character Spacing,
     * {@link PdfCanvas#setCharacterSpacing(float)}
     *
     * @param characterSpacing The new character spacing value.
     */
    public void setCharSpacing(float characterSpacing) {
        this.charSpacing = characterSpacing;
    }

    /**
     * Gets the current Character Spacing, see ISO-320001, 9.3.2 Character Spacing,
     * {@link PdfCanvas#setCharacterSpacing(float)}.
     *
     * @return The current character spacing value.
     */
    public float getCharSpacing() {
        return charSpacing;
    }

    /**
     * Gets the current Leading, see ISO-320001, 9.3.5 Leading,
     * {@link PdfCanvas#setLeading(float)}.
     *
     * @return The current leading value.
     */
    public float getLeading() {
        return leading;
    }

    /**
     * Sets the  Leading, see ISO-320001, 9.3.5 Leading,
     * {@link PdfCanvas#setLeading(float)}.
     *
     * @param leading The new leading value.
     */
    public void setLeading(float leading) {
        this.leading = leading;
    }

    /**
     * Gets the current Horizontal Scaling percentage, see ISO-320001, 9.3.4 Horizontal Scaling.
     * {@link PdfCanvas#setHorizontalScaling(float)}.
     *
     * @return The current horizontal scaling factor.
     */
    public float getHorizontalScaling() {
        return scale;
    }

    /**
     * Sets the Horizontal Scaling percentage for text, see ISO-320001, 9.3.4 Horizontal Scaling,
     * {@link PdfCanvas#setHorizontalScaling(float)}.
     *
     * @param scale The new horizontal scaling factor.
     */
    public void setHorizontalScaling(float scale) {
        this.scale = scale;
    }

    /**
     * Get the Stroke Overprint flag, see ISO 32000-1, 8.6.7 Overprint Control
     * and 11.7.4.5 Summary of Overprinting Behaviour, {@link PdfExtGState#getStrokeOverprintFlag()}.
     *
     * @return The current stroke overprint flag.
     */
    public boolean getStrokeOverprint() {
        return strokeOverprint;
    }

    /**
     * Get the Fill Overprint flag, see ISO 32000-1, 8.6.7 Overprint Control
     * and 11.7.4.5 Summary of Overprinting Behaviour, {@link PdfExtGState#getFillOverprintFlag()}.
     *
     * @return The current stroke overprint flag.
     */
    public boolean getFillOverprint() {
        return fillOverprint;
    }

    /**
     * Get the Overprint Mode, see ISO 32000-1, 8.6.7 Overprint Control
     * and 11.7.4.5 Summary of Overprinting Behaviour, {@link PdfExtGState#getOverprintMode()}.
     *
     * @return The current overprint mode.
     */

    public int getOverprintMode() {
        return overprintMode;
    }

    /**
     * Gets the current Black-generation function, see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getBlackGenerationFunction()}.
     *
     * @return the current black-generation function.
     */
    public PdfObject getBlackGenerationFunction() {
        return blackGenerationFunction;
    }

    /**
     * Gets the current overruling Black-generation function,
     * see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getBlackGenerationFunction2()}.
     *
     * @return the current overruling black-generation function.
     */
    public PdfObject getBlackGenerationFunction2() {
        return blackGenerationFunction2;
    }

    /**
     * Gets the current Undercolor-removal function,
     * see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary
     * {@link PdfExtGState#getUndercolorRemovalFunction()}.
     *
     * @return the current black-generation function.
     */
    public PdfObject getUnderColorRemovalFunction() {
        return underColorRemovalFunction;
    }

    /**
     * Gets the current overruling Undercolor-removal function,
     * see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getUndercolorRemovalFunction2()}.
     *
     * @return the current undercolor-removal function.
     */
    public PdfObject getUnderColorRemovalFunction2() {
        return underColorRemovalFunction2;
    }

    /**
     * Gets the current Transfer function,
     * see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getTransferFunction()}.
     *
     * @return the current transfer function.
     */
    public PdfObject getTransferFunction() {
        return transferFunction;
    }

    /**
     * Gets the current overruling transer function,
     * see ISO32000-1, 11.7.5.3 Rendering Intent and Colour Conversions and
     * Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getTransferFunction2()}.
     *
     * @return the current overruling transer function.
     */
    public PdfObject getTransferFunction2() {
        return transferFunction2;
    }

    /**
     * Gets the current halftone ,
     * see ISO32000-1, 10.5 Halftones and Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getHalftone()}.
     *
     * @return the current halftone.
     */
    public PdfObject getHalftone() {
        return halftone;
    }


    /**
     * Gets the current Smoothness Tolerance,
     * see ISO32000-1, 10.6.3 Smoothness Tolerance and Table 58 – Entries in a Graphics State Parameter Dictionary,
     * {@link PdfExtGState#getSmothnessTolerance()}.
     *
     * @return the current smoothness tolerance function.
     */
    public Float getSmoothnessTolerance() {
        return smoothnessTolerance;
    }

    /**
     * Gets the current Apply Automatic Stroke Adjustment flag, see ISO 32000-1, 10.6.5 Automatic Stroke Adjustment,
     * {@link PdfExtGState#getAutomaticStrokeAdjustmentFlag()}.
     *
     * @return The current automatic stroke adjustment flag.
     */
    public boolean getAutomaticStrokeAdjustment() {
        return automaticStrokeAdjustment;
    }

    /**
     * Gets the current Blend Mode, see ISO 32000-1, 11.3.5 Blend Mode and
     * 11.6.3 Specifying Blending Colour Space and Blend Mode,
     * {@link PdfExtGState#getBlendMode()}.
     *
     * @return The current blend mode.
     */
    public PdfObject getBlendMode() {
        return blendMode;
    }

    /**
     * Gets the current Soft Mask, see ISO 32000-1, 11.3.7.2 Source Shape and Opacity,
     * 11.6.4.3 Mask Shape and Opacity and 11.6.5.2 Soft-Mask Dictionaries,
     * {@link PdfExtGState#getSoftMask()}.
     *
     * @return The current soft mask.
     */
    public PdfObject getSoftMask() {
        return softMask;
    }

    /**
     * Gets the current Stroke Opacity value, see ISO 32000-1, 11.3.7.2 Source Shape and Opacity
     * and 11.6.4.4 Constant Shape and Opacity, {@link PdfExtGState#getStrokeOpacity()}.
     *
     * @return the current stroke opacity value.
     */
    public float getStrokeOpacity() {
        return strokeAlpha;
    }

    /**
     * Gets the current Fill Opacity value, see ISO 32000-1, 11.3.7.2 Source Shape and Opacity
     * and 11.6.4.4 Constant Shape and Opacity, {@link PdfExtGState#getFillOpacity()}.
     *
     * @return the current fill opacity value.
     */
    public float getFillOpacity() {
        return fillAlpha;
    }

    /**
     * Gets the current Alpha is shape flag, see ISO 32000-1, 11.3.7.2 Source Shape and Opacity and
     * 11.6.4.3 Mask Shape and Opacity, {@link PdfExtGState#getAlphaSourceFlag()} .
     *
     * @return The current alpha is shape flag.
     */
    public boolean getAlphaIsShape() {
        return alphaIsShape;
    }

    /**
     * Gets the current Text Knockout flag, see ISO 32000-1, 9.3.8 Text Knockout,
     * {@link PdfExtGState#getTextKnockoutFlag()}.
     *
     * @return The current text knockout flag.
     */
    public boolean getTextKnockout() {
        return textKnockout;
    }

    /**
     * Gets the current Halftone Phase, see Portable Document Format Reference Manual Version 1.2,
     * 7.12 Extended graphics states and PostScript Language Reference Manual, Second Edition,
     * 7.3.3, Halftone Phase.
     *
     * @return the current halftone phase.
     */
    public PdfObject getHTP() {
        return htp;
    }

    /**
     * Updates current graphic state with values from extended graphic state dictionary.
     *
     * @param extGState the wrapper around the extended graphic state dictionary
     */
    public void updateFromExtGState(PdfExtGState extGState) {
        updateFromExtGState(extGState, null);
    }

    /**
     * Updates current graphic state with values from extended graphic state dictionary.
     *
     * @param extGState the wrapper around the extended graphic state dictionary
     * @param pdfDocument the document to retrieve fonts from. Needed when the newly created fonts are used
     */
    void updateFromExtGState(PdfExtGState extGState, PdfDocument pdfDocument) {
        Float lw = extGState.getLineWidth();
        if (lw != null)
            lineWidth = (float) lw;
        Integer lc = extGState.getLineCapStyle();
        if (lc != null)
            lineCapStyle = (int) lc;
        Integer lj = extGState.getLineJoinStyle();
        if (lj != null)
            lineJoinStyle = (int) lj;
        Float ml = extGState.getMiterLimit();
        if (ml != null)
            miterLimit = (float) ml;
        PdfArray d = extGState.getDashPattern();
        if (d != null)
            dashPattern = d;
        PdfName ri = extGState.getRenderingIntent();
        if (ri != null)
            renderingIntent = ri;
        Boolean op = extGState.getStrokeOverprintFlag();
        if (op != null)
            strokeOverprint = (boolean) op;
        op = extGState.getFillOverprintFlag();
        if (op != null)
            fillOverprint = (boolean) op;
        Integer opm = extGState.getOverprintMode();
        if (opm != null)
            overprintMode = (int) opm;
        PdfArray fnt = extGState.getFont();
        if (fnt != null) {
            PdfDictionary fontDictionary = fnt.getAsDictionary(0);
            if (this.font == null || this.font.getPdfObject() != fontDictionary) {
                this.font = pdfDocument.getFont(fontDictionary);
            }
            PdfNumber fntSz = fnt.getAsNumber(1);
            if (fntSz != null)
                this.fontSize = fntSz.floatValue();
        }
        PdfObject bg = extGState.getBlackGenerationFunction();
        if (bg != null)
            blackGenerationFunction = bg;
        PdfObject bg2 = extGState.getBlackGenerationFunction2();
        if (bg2 != null)
            blackGenerationFunction2 = bg2;
        PdfObject ucr = extGState.getUndercolorRemovalFunction();
        if (ucr != null)
            underColorRemovalFunction = ucr;
        PdfObject ucr2 = extGState.getUndercolorRemovalFunction2();
        if (ucr2 != null)
            underColorRemovalFunction2 = ucr2;
        PdfObject tr = extGState.getTransferFunction();
        if (tr != null)
            transferFunction = tr;
        PdfObject tr2 = extGState.getTransferFunction2();
        if (tr2 != null)
            transferFunction2 = tr2;
        PdfObject ht = extGState.getHalftone();
        if (ht != null)
            halftone = ht;
        PdfObject local_htp = extGState.getPdfObject().get(PdfName.HTP);
        if (local_htp != null)
            this.htp = local_htp;
        Float fl = extGState.getFlatnessTolerance();
        if (fl != null)
            flatnessTolerance = (float) fl;
        Float sm = extGState.getSmothnessTolerance();
        if (sm != null)
            smoothnessTolerance = sm;
        Boolean sa = extGState.getAutomaticStrokeAdjustmentFlag();
        if (sa != null)
            automaticStrokeAdjustment = (boolean) sa;
        PdfObject bm = extGState.getBlendMode();
        if (bm != null)
            blendMode = bm;
        PdfObject sMask = extGState.getSoftMask();
        if (sMask != null)
            softMask = sMask;
        Float ca = extGState.getStrokeOpacity();
        if (ca != null)
            strokeAlpha = (float) ca;
        ca = extGState.getFillOpacity();
        if (ca != null)
            fillAlpha = (float) ca;
        Boolean ais = extGState.getAlphaSourceFlag();
        if (ais != null)
            alphaIsShape = (boolean) ais;
        Boolean tk = extGState.getTextKnockoutFlag();
        if (tk != null)
            textKnockout = (boolean) tk;
    }

    private void copyFrom(CanvasGraphicsState source) {
        this.ctm = source.ctm;
        this.strokeColor = source.strokeColor;
        this.fillColor = source.fillColor;
        this.charSpacing = source.charSpacing;
        this.wordSpacing = source.wordSpacing;
        this.scale = source.scale;
        this.leading = source.leading;
        this.font = source.font;
        this.fontSize = source.fontSize;
        this.textRenderingMode = source.textRenderingMode;
        this.textRise = source.textRise;
        this.textKnockout = source.textKnockout;
        this.lineWidth = source.lineWidth;
        this.lineCapStyle = source.lineCapStyle;
        this.lineJoinStyle = source.lineJoinStyle;
        this.miterLimit = source.miterLimit;
        this.dashPattern = source.dashPattern;
        this.renderingIntent = source.renderingIntent;
        this.automaticStrokeAdjustment = source.automaticStrokeAdjustment;
        this.blendMode = source.blendMode;
        this.softMask = source.softMask;
        this.strokeAlpha = source.strokeAlpha;
        this.fillAlpha = source.fillAlpha;
        this.alphaIsShape = source.alphaIsShape;
        this.strokeOverprint = source.strokeOverprint;
        this.fillOverprint = source.fillOverprint;
        this.overprintMode = source.overprintMode;
        this.blackGenerationFunction = source.blackGenerationFunction;
        this.blackGenerationFunction2 = source.blackGenerationFunction2;
        this.underColorRemovalFunction = source.underColorRemovalFunction;
        this.underColorRemovalFunction2 = source.underColorRemovalFunction2;
        this.transferFunction = source.transferFunction;
        this.transferFunction2 = source.transferFunction2;
        this.halftone = source.halftone;
        this.flatnessTolerance = source.flatnessTolerance;
        this.smoothnessTolerance = source.smoothnessTolerance;
        this.htp = source.htp;
    }
}
