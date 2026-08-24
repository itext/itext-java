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
package com.itextpdf.io.font;


/**
 * Holds the typographic metrics parsed from a font program.
 *
 * <p>
 * Unless stated otherwise, dimensional values are normalized to the PDF glyph space of 1000 units
 * per em when they are assigned by the font readers.
 */
public class FontMetrics {


    /**
     * Multiplier that converts values expressed in the source font's units per em to normalized
     * 1000-unit glyph space. Font readers update it after reading {@code head.unitsPerEm}.
     */
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


    /**
     * Gets the units per em value declared by the source font.
     *
     * @return source font design units in one em
     */
    public int getUnitsPerEm() {
        return unitsPerEm;
    }

    /**
     * Gets the number of glyphs declared by the font.
     *
     * @return glyph count
     */
    public int getNumberOfGlyphs() {
        return numOfGlyphs;
    }

    /**
     * Gets the glyph width array.
     *
     * @return glyph widths indexed by glyph ID
     */
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

    /**
     * Gets the height of lowercase flat characters above the baseline.
     *
     * @return x-height in normalized glyph units, or zero when not present
     */
    public int getXHeight() {
        return xHeight;
    }

    /**
     * Gets the PostScript italic angle.
     *
     * @return counterclockwise degrees from vertical; negative values lean right
     */
    public float getItalicAngle() {
        return italicAngle;
    }

    /**
     * Gets the font bounding box.
     *
     * @return array containing lower-left x/y and upper-right x/y in normalized glyph units
     */
    public int[] getBbox() {
        return bbox;
    }

    /**
     * Replaces the normalized font bounding box.
     *
     * @param llx lower-left x coordinate
     * @param lly lower-left y coordinate
     * @param urx upper-right x coordinate
     * @param ury upper-right y coordinate
     */
    public void setBbox(int llx, int lly, int urx, int ury){
        bbox[0] = llx;
        bbox[1] = lly;
        bbox[2] = urx;
        bbox[3] = ury;
    }

    /**
     * Gets the horizontal ascender.
     *
     * @return ascender in normalized glyph units
     */
    public int getAscender() {
        return ascender;
    }

    /**
     * Gets the horizontal descender.
     *
     * @return descender in normalized glyph units
     */
    public int getDescender() {
        return descender;
    }

    /**
     * Gets the line spacing.
     *
     * @return horizontal line gap in normalized glyph units
     */
    public int getLineGap() {
        return lineGap;
    }

    /**
     * Gets the Windows ascender metric.
     *
     * @return OS/2 {@code usWinAscent} in normalized glyph units
     */
    public int getWinAscender() {
        return winAscender;
    }

    /**
     * Gets the Windows descender metric.
     *
     * @return OS/2 {@code usWinDescent} in normalized glyph units
     */
    public int getWinDescender() {
        return winDescender;
    }

    /**
     * Gets the largest horizontal advance width in the font.
     *
     * @return maximum advance width in normalized glyph units
     */
    public int getAdvanceWidthMax() {
        return advanceWidthMax;
    }

    /**
     * Gets the underline center position relative to the baseline.
     *
     * @return normalized underline position, adjusted from the stored edge by half its thickness
     */
    public int getUnderlinePosition() {
        return underlinePosition - underlineThickness / 2;
    }

    /**
     * Gets the recommended underline thickness.
     *
     * @return stored thickness value; readers currently supply it in source font units
     */
    public int getUnderlineThickness() {
        return underlineThickness;
    }

    /**
     * Gets the strikeout position relative to the baseline.
     *
     * @return OS/2 strikeout position in normalized glyph units
     */
    public int getStrikeoutPosition() {
        return strikeoutPosition;
    }

    /**
     * Gets the strikeout thickness.
     *
     * @return OS/2 strikeout size in normalized glyph units
     */
    public int getStrikeoutSize() {
        return strikeoutSize;
    }

    /**
     * Gets the recommended vertical size for subscripts.
     *
     * @return subscript y-size in normalized glyph units
     */
    public int getSubscriptSize() {
        return subscriptSize;
    }

    /**
     * Gets the subscript baseline offset.
     *
     * @return subscript y-offset in normalized glyph units
     */
    public int getSubscriptOffset() {
        return subscriptOffset;
    }

    /**
     * Gets the recommended vertical size for superscripts.
     *
     * @return superscript y-size in source font units
     */
    public int getSuperscriptSize() {
        return superscriptSize;
    }

    /**
     * Gets the superscript baseline offset.
     *
     * @return superscript y-offset in normalized glyph units
     */
    public int getSuperscriptOffset() {
        return superscriptOffset;
    }

    /**
     * Gets the dominant vertical stem width.
     *
     * @return Type 1/CFF {@code StdVW} value, or the reader's fallback, in glyph units
     */
    public int getStemV() {
        return stemV;
    }

    /**
     * Gets the dominant horizontal stem width.
     *
     * @return Type 1/CFF {@code StdHW} value, or the reader's fallback, in glyph units
     */
    public int getStemH() {
        return stemH;
    }

    /**
     * Checks whether all glyphs use a common width.
     *
     * @return {@code true} when the font declares fixed pitch
     */
    public boolean isFixedPitch() {
        return isFixedPitch;
    }

    /**
     * Sets source units per em.
     *
     * <p>
     * It recalculates the normalization multiplier used by subsequent setters.
     *
     * @param unitsPerEm positive number of design units in an em
     */
    protected void setUnitsPerEm(int unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
        normalizationCoef =  (float) FontProgram.UNITS_NORMALIZATION / unitsPerEm;
    }

    /**
     * Converts and stores a font bounding box in source units.
     *
     * @param llx lower-left x coordinate in source units
     * @param lly lower-left y coordinate in source units
     * @param urx upper-right x coordinate in source units
     * @param ury upper-right y coordinate in source units
     */
    protected void updateBbox(float llx, float lly, float urx, float ury) {
        bbox[0] = (int) (llx * normalizationCoef);
        bbox[1] = (int) (lly * normalizationCoef);
        bbox[2] = (int) (urx * normalizationCoef);
        bbox[3] = (int) (ury * normalizationCoef);
    }

    /**
     * Sets the glyph count declared by the font.
     *
     * @param numOfGlyphs number of glyphs
     */
    protected void setNumberOfGlyphs(int numOfGlyphs) {
        this.numOfGlyphs = numOfGlyphs;
    }

    /**
     * Stores the glyph width table.
     *
     * @param glyphWidths width array indexed by glyph ID
     */
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

    /**
     * Sets the source font x-height.
     *
     * @param xHeight OS/2 {@code sxHeight} in source units
     */
    protected void setXHeight(int xHeight) {
        this.xHeight = (int) (xHeight * normalizationCoef);
    }

    /**
     * Sets the PostScript italic angle without unit normalization.
     *
     * @param italicAngle counterclockwise degrees from vertical
     */
    protected void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    /**
     * Sets the source horizontal ascender.
     *
     * @param ascender {@code hhea.Ascender} in source units
     */
    protected void setAscender(int ascender) {
        this.ascender = (int) (ascender * normalizationCoef);
    }

    /**
     * Sets the source horizontal descender.
     *
     * @param descender {@code hhea.Descender} in source units
     */
    protected void setDescender(int descender) {
        this.descender = (int) (descender * normalizationCoef);
    }

    /**
     * Sets the line space.
     *
     * @param lineGap {@code hhea.LineGap} in source units
     */
    protected void setLineGap(int lineGap) {
        this.lineGap = (int) (lineGap * normalizationCoef);
    }

    /**
     * Sets the source Windows ascender metric.
     *
     * @param winAscender OS/2 {@code usWinAscent} in source units
     */
    protected void setWinAscender(int winAscender) {
        this.winAscender = (int) (winAscender * normalizationCoef);
    }

    /**
     * Sets the source Windows descender metric.
     *
     * @param winDescender OS/2 {@code usWinDescent} in source units
     */
    protected void setWinDescender(int winDescender) {
        this.winDescender = (int) (winDescender * normalizationCoef);
    }

    /**
     * Sets the largest horizontal advance width declared by the source font.
     *
     * @param advanceWidthMax {@code hhea.advanceWidthMax} in source units
     */
    protected void setAdvanceWidthMax(int advanceWidthMax) {
        this.advanceWidthMax = (int) (advanceWidthMax * normalizationCoef);
    }

    /**
     * Sets the source underline position.
     *
     * @param underlinePosition {@code post.underlinePosition} in source units
     */
    protected void setUnderlinePosition(int underlinePosition) {
        this.underlinePosition = (int) (underlinePosition * normalizationCoef);
    }

    /**
     * Sets the underline thickness.
     *
     * @param underlineThickness {@code post.underlineThickness}; stored without normalization
     */
    protected void setUnderlineThickness(int underlineThickness) {
        this.underlineThickness = underlineThickness;
    }

    /**
     * Sets the source strikeout position.
     *
     * @param strikeoutPosition OS/2 {@code yStrikeoutPosition} in source units
     */
    protected void setStrikeoutPosition(int strikeoutPosition) {
        this.strikeoutPosition = (int) (strikeoutPosition * normalizationCoef);
    }

    /**
     * Sets the source strikeout thickness.
     *
     * @param strikeoutSize OS/2 {@code yStrikeoutSize} in source units
     */
    protected void setStrikeoutSize(int strikeoutSize) {
        this.strikeoutSize = (int) (strikeoutSize * normalizationCoef);
    }

    /**
     * Sets the source subscript vertical size.
     *
     * @param subscriptSize OS/2 {@code ySubscriptYSize} in source units
     */
    protected void setSubscriptSize(int subscriptSize) {
        this.subscriptSize = (int) (subscriptSize * normalizationCoef);
    }

    /**
     * Sets the source subscript vertical offset.
     *
     * @param subscriptOffset OS/2 {@code ySubscriptYOffset} in source units
     */
    protected void setSubscriptOffset(int subscriptOffset) {
        this.subscriptOffset = (int) (subscriptOffset * normalizationCoef);
    }

    /**
     * Sets the superscript vertical size.
     *
     * @param superscriptSize OS/2 {@code ySuperscriptYSize}; stored without normalization
     */
    protected void setSuperscriptSize(int superscriptSize) {
        this.superscriptSize = superscriptSize;
    }

    /**
     * Sets the source superscript vertical offset.
     *
     * @param superscriptOffset OS/2 {@code ySuperscriptYOffset} in source units
     */
    protected void setSuperscriptOffset(int superscriptOffset) {
        this.superscriptOffset = (int) (superscriptOffset * normalizationCoef);
    }

    /**
     * Sets the dominant vertical stem width.
     *
     * @param stemV Type 1/CFF {@code StdVW} value in glyph units
     */
    public void setStemV(int stemV) {
        this.stemV = stemV;
    }

    /**
     * Sets the dominant horizontal stem width.
     *
     * @param stemH Type 1/CFF {@code StdHW} value in glyph units
     */
    protected void setStemH(int stemH) {
        this.stemH = stemH;
    }

    /**
     * Sets whether the font declares a common width for all glyphs.
     *
     * @param isFixedPitch {@code true} for fixed pitch fonts
     */
    protected void setIsFixedPitch(boolean isFixedPitch) {
        this.isFixedPitch = isFixedPitch;
    }
}
