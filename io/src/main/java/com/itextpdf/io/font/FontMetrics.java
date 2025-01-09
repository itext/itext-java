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
package com.itextpdf.io.font;


public class FontMetrics {


    protected float normalizationCoef = 1f;

    // head.unitsPerEm
    private int unitsPerEm = FontProgram.UNITS_NORMALIZATION;
    // maxp.numGlyphs
    private int numOfGlyphs;
    // hmtx
    private int[] glyphWidths;
    // os_2.sTypoAscender * normalization
    private int typoAscender = 800;
    // os_2.sTypoDescender * normalization
    private int typoDescender = -200;
    // os_2.sCapHeight * normalization
    private int capHeight = 700;
    // os_2.sxHeight * normalization
    private int xHeight = 0;
    // post.italicAngle
    private float italicAngle = 0;
    // llx: head.xMin * normalization; lly: head.yMin * normalization
    // urx: head.xMax * normalization; ury: head.yMax * normalization
    private int[] bbox = new int[]{-50, -200, 1000, 900};
    // hhea.Ascender * normalization
    private int ascender;
    // hhea.Descender * normalization
    private int descender;
    // hhea.LineGap * normaliztion (leading)
    private int lineGap;
    // os_2.winAscender * normalization
    private int winAscender;
    // os_2.winDescender * normalization
    private int winDescender;
    // hhea.advanceWidthMax * normalization
    private int advanceWidthMax;
    // (post.underlinePosition - post.underlineThickness / 2) * normalization
    private int underlinePosition = -100;
    // post.underlineThickness * normalization
    private int underlineThickness = 50;
    // os_2.yStrikeoutPosition * normalization
    private int strikeoutPosition;
    // os_2.yStrikeoutSize * normalization
    private int strikeoutSize;
    // os_2.ySubscriptYSize * normalization
    private int subscriptSize;
    // -os_2.ySubscriptYOffset * normalization
    private int subscriptOffset;
    // os_2.ySuperscriptYSize * normalization
    private int superscriptSize;
    // os_2.ySuperscriptYOffset * normalization
    private int superscriptOffset;
    // in type1/cff it is stdVW
    private int stemV = 80;
    // in type1/cff it is stdHW
    private int stemH = 0;
    // post.isFixedPitch (monospaced)
    private boolean isFixedPitch;


    public int getUnitsPerEm() {
        return unitsPerEm;
    }

    public int getNumberOfGlyphs() {
        return numOfGlyphs;
    }

    public int[] getGlyphWidths() {
        return glyphWidths;
    }

    /**
     * Gets typo (a.k.a. sTypo or OS/2) vertical metric corresponding to ascender.
     *
     * <p>
     * Typo vertical metrics are the primary source for iText ascender/descender calculations.
     *
     * @return typo ascender value in normalized 1000-units
     */
    public int getTypoAscender() {
        return typoAscender;
    }

    /**
     * Gets typo (a.k.a. sTypo or OS/2) vertical metric corresponding to descender.
     *
     * <p>
     * Typo vertical metrics are the primary source for iText ascender/descender calculations.
     *
     * @return typo descender value in normalized 1000-units
     */
    public int getTypoDescender() {
        return typoDescender;
    }

    /**
     * Gets the capital letters height.
     *
     * <p>
     * This property defines the vertical coordinate of the top of flat capital letters,
     * measured from the baseline.
     *
     * @return cap height in 1000-units
     */
    public int getCapHeight() {
        return capHeight;
    }

    public int getXHeight() {
        return xHeight;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public int[] getBbox() {
        return bbox;
    }

    public void setBbox(int llx, int lly, int urx, int ury){
        bbox[0] = llx;
        bbox[1] = lly;
        bbox[2] = urx;
        bbox[3] = ury;
    }

    public int getAscender() {
        return ascender;
    }

    public int getDescender() {
        return descender;
    }

    public int getLineGap() {
        return lineGap;
    }

    public int getWinAscender() {
        return winAscender;
    }

    public int getWinDescender() {
        return winDescender;
    }

    public int getAdvanceWidthMax() {
        return advanceWidthMax;
    }

    public int getUnderlinePosition() {
        return underlinePosition - underlineThickness / 2;
    }

    public int getUnderlineThickness() {
        return underlineThickness;
    }

    public int getStrikeoutPosition() {
        return strikeoutPosition;
    }

    public int getStrikeoutSize() {
        return strikeoutSize;
    }

    public int getSubscriptSize() {
        return subscriptSize;
    }

    public int getSubscriptOffset() {
        return subscriptOffset;
    }

    public int getSuperscriptSize() {
        return superscriptSize;
    }

    public int getSuperscriptOffset() {
        return superscriptOffset;
    }

    public int getStemV() {
        return stemV;
    }

    public int getStemH() {
        return stemH;
    }

    public boolean isFixedPitch() {
        return isFixedPitch;
    }

    protected void setUnitsPerEm(int unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
        normalizationCoef =  (float) FontProgram.UNITS_NORMALIZATION / unitsPerEm;
    }

    protected void updateBbox(float llx, float lly, float urx, float ury) {
        bbox[0] = (int) (llx * normalizationCoef);
        bbox[1] = (int) (lly * normalizationCoef);
        bbox[2] = (int) (urx * normalizationCoef);
        bbox[3] = (int) (ury * normalizationCoef);
    }

    protected void setNumberOfGlyphs(int numOfGlyphs) {
        this.numOfGlyphs = numOfGlyphs;
    }

    protected void setGlyphWidths(int[] glyphWidths) {
        this.glyphWidths = glyphWidths;
    }

    /**
     * Sets typo (a.k.a. sTypo or OS/2) vertical metric corresponding to ascender.
     *
     * <p>
     * Typo vertical metrics are the primary source for iText ascender/descender calculations.
     *
     * @param typoAscender typo ascender value in normalized 1000-units
     */
    protected void setTypoAscender(int typoAscender) {
        this.typoAscender = (int) (typoAscender * normalizationCoef);
    }

    /**
     * Sets typo (a.k.a. sTypo or OS/2) vertical metric corresponding to descender.
     *
     * <p>
     * Typo vertical metrics are the primary source for iText ascender/descender calculations.
     *
     * @param typoDescender typo descender value in normalized 1000-units
     */
    protected void setTypoDescender(int typoDescender) {
        this.typoDescender = (int) (typoDescender * normalizationCoef);
    }

    /**
     * Sets the capital letters height.
     *
     * <p>
     * This property defines the vertical coordinate of the top of flat capital letters,
     * measured from the baseline.
     *
     * @param capHeight cap height in 1000-units
     */
    protected void setCapHeight(int capHeight) {
        this.capHeight = (int) (capHeight * normalizationCoef);
    }

    protected void setXHeight(int xHeight) {
        this.xHeight = (int) (xHeight * normalizationCoef);
    }

    protected void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    protected void setAscender(int ascender) {
        this.ascender = (int) (ascender * normalizationCoef);
    }

    protected void setDescender(int descender) {
        this.descender = (int) (descender * normalizationCoef);
    }

    protected void setLineGap(int lineGap) {
        this.lineGap = (int) (lineGap * normalizationCoef);
    }

    protected void setWinAscender(int winAscender) {
        this.winAscender = (int) (winAscender * normalizationCoef);
    }

    protected void setWinDescender(int winDescender) {
        this.winDescender = (int) (winDescender * normalizationCoef);
    }

    protected void setAdvanceWidthMax(int advanceWidthMax) {
        this.advanceWidthMax = (int) (advanceWidthMax * normalizationCoef);
    }

    protected void setUnderlinePosition(int underlinePosition) {
        this.underlinePosition = (int) (underlinePosition * normalizationCoef);
    }

    protected void setUnderlineThickness(int underineThickness) {
        this.underlineThickness = underineThickness;
    }

    protected void setStrikeoutPosition(int strikeoutPosition) {
        this.strikeoutPosition = (int) (strikeoutPosition * normalizationCoef);
    }

    protected void setStrikeoutSize(int strikeoutSize) {
        this.strikeoutSize = (int) (strikeoutSize * normalizationCoef);
    }

    protected void setSubscriptSize(int subscriptSize) {
        this.subscriptSize = (int) (subscriptSize * normalizationCoef);
    }

    protected void setSubscriptOffset(int subscriptOffset) {
        this.subscriptOffset = (int) (subscriptOffset * normalizationCoef);
    }

    protected void setSuperscriptSize(int superscriptSize) {
        this.superscriptSize = superscriptSize;
    }

    protected void setSuperscriptOffset(int superscriptOffset) {
        this.superscriptOffset = (int) (superscriptOffset * normalizationCoef);
    }

    public void setStemV(int stemV) {
        this.stemV = stemV;
    }

    protected void setStemH(int stemH) {
        this.stemH = stemH;
    }

    protected void setIsFixedPitch(boolean isFixedPitch) {
        this.isFixedPitch = isFixedPitch;
    }
}
