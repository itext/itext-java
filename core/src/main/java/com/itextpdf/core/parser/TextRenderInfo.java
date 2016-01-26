package com.itextpdf.core.parser;

import com.itextpdf.core.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides information and calculations needed by render listeners
 * to display/evaluate text render operations.
 * <br><br>
 * This is passed between the {@link PdfContentStreamProcessor} and
 * {@link EventListener} objects as text rendering operations are
 * discovered
 */
public class TextRenderInfo implements EventData {

    private final PdfString string;
    private String text = null;
    private final Matrix textToUserSpaceTransformMatrix;
    private final GraphicsState gs;
    private Float unscaledWidth = null;
    private double[] fontMatrix = null;
    /**
     * Array containing marked content info for the text.
     * @since 5.0.2
     */
    private final Collection<MarkedContentInfo> markedContentInfos;

    /**
     * Creates a new TextRenderInfo object
     * @param string the PDF string that should be displayed
     * @param gs the graphics state (note: at this time, this is not immutable, so don't cache it)
     * @param textMatrix the text matrix at the time of the render operation
     * @param markedContentInfo the marked content sequence, if available
     */
    TextRenderInfo(PdfString string, GraphicsState gs, Matrix textMatrix, Collection<MarkedContentInfo> markedContentInfo) {
        this.string = string;
        this.textToUserSpaceTransformMatrix = textMatrix.multiply(gs.getCtm());
        this.gs = gs;
        this.markedContentInfos = new ArrayList<>(markedContentInfo);
        this.fontMatrix = gs.getFont().getFontMatrix();
    }

    /**
     * Used for creating sub-TextRenderInfos for each individual character
     * @param parent the parent TextRenderInfo
     * @param string the content of a TextRenderInfo
     * @param horizontalOffset the unscaled horizontal offset of the character that this TextRenderInfo represents
     * @since 5.3.3
     */
    private TextRenderInfo(TextRenderInfo parent, PdfString string, float horizontalOffset){
        this.string = string;
        this.textToUserSpaceTransformMatrix = new Matrix(horizontalOffset, 0).multiply(parent.textToUserSpaceTransformMatrix);
        this.gs = parent.gs;
        this.markedContentInfos = parent.markedContentInfos;
        this.fontMatrix = gs.getFont().getFontMatrix();
    }

    /**
     * @return the text to render
     */
    public String getText(){
        if (text == null)
            text = gs.getFont().decode(string);
        return text;
    }

    /**
     * @return original PDF string
     */
    public PdfString getPdfString() { return string; }

    /**
     * Checks if the text belongs to a marked content sequence
     * with a given mcid.
     * @param mcid a marked content id
     * @return true if the text is marked with this id
     * @since 5.0.2
     */
    public boolean hasMcid(int mcid) {
        return hasMcid(mcid, false);
    }

    /**
     * Checks if the text belongs to a marked content sequence
     * with a given mcid.
     * @param mcid a marked content id
     * @param checkTheTopmostLevelOnly indicates whether to check the topmost level of marked content stack only
     * @return true if the text is marked with this id
     * @since 5.3.5
     */
    public boolean hasMcid(int mcid, boolean checkTheTopmostLevelOnly) {
        if (checkTheTopmostLevelOnly) {
            if (markedContentInfos instanceof List) {
                Integer infoMcid = getMcid();
                return (infoMcid != null) && infoMcid == mcid;
            }
        } else {
            for (MarkedContentInfo info : markedContentInfos) {
                if (info.hasMcid())
                    if(info.getMcid() == mcid)
                        return true;
            }
        }
        return false;
    }

    /**
     * @return the marked content associated with the TextRenderInfo instance.
     */
    public Integer getMcid() {
        if (markedContentInfos instanceof List) {
            List<MarkedContentInfo> mci = (List<MarkedContentInfo>)markedContentInfos;
            MarkedContentInfo info = mci.size() > 0 ? mci.get(mci.size() - 1) : null;
            return (info != null && info.hasMcid()) ? info.getMcid() : null;
        }
        return null;
    }

    /**
     * Gets the baseline for the text (i.e. the line that the text 'sits' on)
     * This value includes the Rise of the draw operation - see {@link #getRise()} for the amount added by Rise
     * @return the baseline line segment
     * @since 5.0.2
     */
    public LineSegment getBaseline(){
        return getUnscaledBaselineWithOffset(0 + gs.getRise()).transformBy(textToUserSpaceTransformMatrix);
    }

    public LineSegment getUnscaledBaseline() {
        return getUnscaledBaselineWithOffset(0 + gs.getRise());
    }

    /**
     * Gets the ascentline for the text (i.e. the line that represents the topmost extent that a string of the current font could have)
     * This value includes the Rise of the draw operation - see {@link #getRise()} for the amount added by Rise
     * @return the ascentline line segment
     * @since 5.0.2
     */
    public LineSegment getAscentLine(){
        float ascent = gs.getFont().getFontProgram().getFontMetrics().getTypoAscender() * gs.getFontSize() / 1000f;
        return getUnscaledBaselineWithOffset(ascent + gs.getRise()).transformBy(textToUserSpaceTransformMatrix);
    }

    /**
     * Gets the descentline for the text (i.e. the line that represents the bottom most extent that a string of the current font could have).
     * This value includes the Rise of the draw operation - see {@link #getRise()} for the amount added by Rise
     * @return the descentline line segment
     * @since 5.0.2
     */
    public LineSegment getDescentLine(){
        // per getFontDescription() API, descent is returned as a negative number, so we apply that as a normal vertical offset
        float descent = gs.getFont().getFontProgram().getFontMetrics().getTypoDescender() * gs.getFontSize() / 1000f;
        return getUnscaledBaselineWithOffset(descent + gs.getRise()).transformBy(textToUserSpaceTransformMatrix);
    }

    /**
     * Getter for the font
     * @return the font
     * @since iText 5.0.2
     */
    public PdfFont getFont() {
        return gs.getFont();
    }

    /**
     * The rise represents how far above the nominal baseline the text should be rendered.  The {@link #getBaseline()}, {@link #getAscentLine()} and {@link #getDescentLine()} methods already include Rise.
     * This method is exposed to allow listeners to determine if an explicit rise was involved in the computation of the baseline (this might be useful, for example, for identifying superscript rendering)
     * @return The Rise for the text draw operation, in user space units (Ts value, scaled to user space)
     * @since 5.3.3
     */
    public float getRise(){
        if (gs.getRise() == 0) return 0; // optimize the common case

        return convertHeightFromTextSpaceToUserSpace(gs.getRise());
    }

    /**
     * Provides detail useful if a listener needs access to the position of each individual glyph in the text render operation
     * @return  A list of {@link TextRenderInfo} objects that represent each glyph used in the draw operation. The next effect is if there was a separate Tj opertion for each character in the rendered string
     * @since   5.3.3
     */
    public List<TextRenderInfo> getCharacterRenderInfos(){
        List<TextRenderInfo> rslt = new ArrayList<>(string.getValue().length());
        PdfString[] strings = splitString(string);
        float totalWidth = 0;
        for (PdfString str : strings) {
            float[] widthAndWordSpacing = getWidthAndWordSpacing(str, true);
            TextRenderInfo subInfo = new TextRenderInfo(this, str, totalWidth);
            rslt.add(subInfo);
            totalWidth += (widthAndWordSpacing[0] * gs.getFontSize() + gs.getCharacterSpacing() + widthAndWordSpacing[1]) * gs.getHorizontalScaling();
        }
        for (TextRenderInfo tri : rslt)
            tri.getUnscaledWidth();
        return rslt;
    }

    /**
     * @return The width, in user space units, of a single space character in the current font
     */
    public float getSingleSpaceWidth(){
        return convertWidthFromTextSpaceToUserSpace(getUnscaledFontSpaceWidth());
    }

    /**
     * @return the text render mode that should be used for the text.  From the
     * PDF specification, this means:
     * <ul>
     *   <li>0 = Fill text</li>
     *   <li>1 = Stroke text</li>
     *   <li>2 = Fill, then stroke text</li>
     *   <li>3 = Invisible</li>
     *   <li>4 = Fill text and add to path for clipping</li>
     *   <li>5 = Stroke text and add to path for clipping</li>
     *   <li>6 = Fill, then stroke text and add to path for clipping</li>
     *   <li>7 = Add text to padd for clipping</li>
     * </ul>
     * @since iText 5.0.1
     */
    public int getTextRenderMode(){
        return gs.getRenderMode();
    }

    /**
     * @return the current fill color.
     */
    public Color getFillColor() {
        return gs.getFillColor();
    }

    /**
     * @return the current stroke color.
     */
    public Color getStrokeColor() {
        return gs.getStrokeColor();
    }

    /**
     * Gets /ActualText tag entry value if this text chunk is marked content.
     * @return /ActualText value
     */
    protected String getActualText() {
        String lastActualText = null;
        if (markedContentInfos != null) {
            for (MarkedContentInfo info : markedContentInfos) {
                if (PdfName.Span.equals(info.getTag())) {
                    lastActualText = info.getActualText();
                }
            }
        }
        return lastActualText;
    }

    /**
     * @return the unscaled (i.e. in Text space) width of the text
     */
    float getUnscaledWidth(){
        if (unscaledWidth == null)
            unscaledWidth = getPdfStringWidth(string, false);
        return unscaledWidth;
    }

    private LineSegment getUnscaledBaselineWithOffset(float yOffset){
        // we need to correct the width so we don't have an extra character and word spaces at the end.  The extra character and word spaces
        // are important for tracking relative text coordinate systems, but should not be part of the baseline
        String unicodeStr = string.toUnicodeString();

        float correctedUnscaledWidth = getUnscaledWidth() - (gs.getCharacterSpacing() +
                (unicodeStr.length() > 0 && unicodeStr.charAt(unicodeStr.length() - 1) == ' ' ? gs.getWordSpacing() : 0)) * gs.getHorizontalScaling();

        return new LineSegment(new Vector(0, yOffset, 1), new Vector(correctedUnscaledWidth, yOffset, 1));
    }

    /**
     *
     * @param width the width, in text space
     * @return the width in user space
     * @since 5.3.3
     */
    private float convertWidthFromTextSpaceToUserSpace(float width){
        LineSegment textSpace = new LineSegment(new Vector(0, 0, 1), new Vector(width, 0, 1));
        LineSegment userSpace = textSpace.transformBy(textToUserSpaceTransformMatrix);
        return userSpace.getLength();
    }

    /**
     *
     * @param height the height, in text space
     * @return the height in user space
     * @since 5.3.3
     */
    private float convertHeightFromTextSpaceToUserSpace(float height){
        LineSegment textSpace = new LineSegment(new Vector(0, 0, 1), new Vector(0, height, 1));
        LineSegment userSpace = textSpace.transformBy(textToUserSpaceTransformMatrix);
        return userSpace.getLength();
    }

    /**
     * Calculates the width of a space character.  If the font does not define
     * a width for a standard space character \u0020, we also attempt to use
     * the width of \u00A0 (a non-breaking space in many fonts)
     * @return the width of a single space character in text space units
     */
    private float getUnscaledFontSpaceWidth(){
        char charToUse = ' ';
        if (gs.getFont().getWidth(charToUse) == 0)
            charToUse = '\u00A0';
        return getStringWidth(String.valueOf(charToUse));
    }

    /**
     * Gets the width of a String in text space units
     * @param string    the string that needs measuring
     * @return          the width of a String in text space units
     */
    private float getStringWidth(String string){
        float totalWidth = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            float w = gs.getFont().getWidth(c) / 1000.0f;
            float wordSpacing = c == 32 ? gs.getWordSpacing() : 0f;
            totalWidth += (w * gs.getFontSize() + gs.getCharacterSpacing() + wordSpacing) * gs.getHorizontalScaling();
        }
        return totalWidth;
    }

    /**
     * Gets the width of a PDF string in text space units
     * @param string        the string that needs measuring
     * @return  the width of a String in text space units
     */
    private float getPdfStringWidth(PdfString string, boolean singleCharString){
        if (singleCharString) {
            float[] widthAndWordSpacing = getWidthAndWordSpacing(string, singleCharString);
            return (widthAndWordSpacing[0] * gs.getFontSize() + gs.getCharacterSpacing() + widthAndWordSpacing[1]) * gs.getHorizontalScaling();
        } else {
            float totalWidth = 0;
            for (PdfString str : splitString(string)) {
                totalWidth += getPdfStringWidth(str, true);
            }
            return totalWidth;
        }
    }

    /**
     * Calculates width and word spacing of a single character PDF string.
     * @param string            a character to calculate width.
     * @param singleCharString  true if PDF string represents single character, false otherwise.
     * @return                  array of 2 items: first item is a character width, second item is a calculated word spacing.
     */
    private float[] getWidthAndWordSpacing(PdfString string, boolean singleCharString) {
        if (!singleCharString)
            throw new UnsupportedOperationException();
        float[] result = new float[2];
        result[0] = (float)((gs.getFont().getContentWidth(string) * fontMatrix[0]));
        result[1] = " ".equals(string.getValue()) ? gs.getWordSpacing() : 0;
        return result;
    }

    /**
     * Converts a single character string to char code.
     *
     * @param string single character string to convert to.
     * @return char code.
     */
    private int getCharCode(String string) {
        try {
            byte[] b = string.getBytes("UTF-16BE");
            int value = 0;
            for (int i = 0; i < b.length - 1; i++) {
                value += b[i] & 0xff;
                value <<= 8;
            }
            if (b.length > 0) {
                value += b[b.length - 1] & 0xff;
            }
            return value;
        } catch (UnsupportedEncodingException e) {
        }
        return 0;
    }

    /**
     * Split PDF string into array of single character PDF strings.
     * @param string    PDF string to be splitted.
     * @return          splitted PDF string.
     */
    private PdfString[] splitString(PdfString string) {
        List<PdfString> strings = new ArrayList<>();
        String stringValue = string.getValue();
        for (int i = 0; i < stringValue.length(); i++) {
            PdfString newString = new PdfString(stringValue.substring(i, i + 1), string.getEncoding());
            String text = gs.getFont().decode(newString);
            if (text.length() == 0 && i < stringValue.length() - 1) {
                newString = new PdfString(stringValue.substring(i, i + 2), string.getEncoding());
                i++;
            }
            strings.add(newString);
        }
        return strings.toArray(new PdfString[strings.size()]);
    }
}