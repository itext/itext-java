package com.itextpdf.io.font;

public class FontMetrics {

    protected float normalizationCoef = 1f;

    // head.unitsPerEm
    private int unitsPerEm = 1000;
    // maxp.numGlyphs
    private int maxGlyphId;
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

    public int getMaxGlyphId() {
        return maxGlyphId;
    }

    public int[] getGlyphWidths() {
        return glyphWidths;
    }

    public int getTypoAscender() {
        return typoAscender;
    }

    public int getTypoDescender() {
        return typoDescender;
    }

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
        normalizationCoef = (float) FontProgram.UNITS_NORMALIZATION / unitsPerEm;
    }

    protected void updateBbox(float llx, float lly, float urx, float ury) {
        bbox[0] = (int) (llx * normalizationCoef);
        bbox[1] = (int) (lly * normalizationCoef);
        bbox[2] = (int) (urx * normalizationCoef);
        bbox[3] = (int) (ury * normalizationCoef);
    }

    protected void setMaxGlyphId(int maxGlyphId) {
        this.maxGlyphId = maxGlyphId;
    }

    protected void setGlyphWidths(int[] glyphWidths) {
        this.glyphWidths = glyphWidths;
    }

    //todo change to protected!
    public void setTypoAscender(int typoAscender) {
        this.typoAscender = (int) (typoAscender * normalizationCoef);
    }

    //todo change to protected!
    public void setTypoDescender(int typoDesctender) {
        this.typoDescender = (int) (typoDesctender * normalizationCoef);
    }

    //todo change to protected!
    public void setCapHeight(int capHeight) {
        this.capHeight = (int) (capHeight * normalizationCoef);
    }

    //todo change to protected!
    public void setXHeight(int xHeight) {
        this.xHeight = (int) (xHeight * normalizationCoef);
    }

    //todo change to protected!
    public void setItalicAngle(float italicAngle) {
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

    //todo change to protected!
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
