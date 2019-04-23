/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.SvgTextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;text&gt; and &lt;tspan&gt; tag.
 */
public class TextSvgBranchRenderer extends AbstractSvgNodeRenderer implements ISvgTextNodeRenderer {

    /**
     * Top level transformation to flip the y-axis results in the character glyphs being mirrored, this tf corrects for this behaviour
     */
    protected final static AffineTransform TEXTFLIP = new AffineTransform(1, 0, 0, -1, 0, 0);

    /**
     * Placeholder default font-size until DEVSIX-2607 is resolved
     */
    private final static float DEFAULT_FONT_SIZE = 12f;

    private final List<ISvgTextNodeRenderer> children = new ArrayList<>();
    protected boolean performRootTransformations;
    private PdfFont font;
    private float fontSize;

    private boolean moveResolved;
    private float xMove;
    private float yMove;

    private boolean posResolved;
    private float[] xPos;
    private float[] yPos;

    private boolean whiteSpaceProcessed = false;

    public TextSvgBranchRenderer() {
        performRootTransformations = true;
        moveResolved = false;
        posResolved = false;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        TextSvgBranchRenderer copy = new TextSvgBranchRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    public final void addChild(ISvgTextNodeRenderer child) {
        // final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    public final List<ISvgTextNodeRenderer> getChildren() {
        // final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    @Override
    public float getTextContentLength(float parentFontSize, PdfFont font) {
        return 0.0f; //Branch renderers do not contain any text themselves and do not contribute to the text length
    }

    @Override
    public float[] getRelativeTranslation() {
        if (!moveResolved) resolveTextMove();
        return new float[]{xMove, yMove};
    }

    @Override
    public boolean containsRelativeMove() {
        if (!moveResolved) resolveTextMove();
        boolean isNullMove = CssUtils.compareFloats(0f, xMove) && CssUtils.compareFloats(0f, yMove); // comparision to 0
        return !isNullMove;
    }

    @Override
    public boolean containsAbsolutePositionChange() {
        if (!posResolved) resolveTextPosition();
        return (xPos != null && xPos.length > 0) || (yPos != null && yPos.length > 0);
    }

    @Override
    public float[][] getAbsolutePositionChanges() {
        if (!posResolved) resolveTextPosition();
        return new float[][]{xPos, yPos};
    }

    public void markWhiteSpaceProcessed() {
        whiteSpaceProcessed = true;
    }

    /**
     * Method that will set properties to be inherited by this branch renderer's
     * children and will iterate over all children in order to draw them.
     *
     * @param context the object that knows the place to draw this element and
     *                maintains its state
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        if (getChildren().size() > 0) { // if branch has no children, don't do anything
            PdfCanvas currentCanvas = context.getCurrentCanvas();
            if (performRootTransformations) {
                currentCanvas.beginText();
                //Current transformation matrix results in the character glyphs being mirrored, correct with inverse tf
                AffineTransform rootTf;
                if (this.containsAbsolutePositionChange()) {
                    rootTf = getTextTransform(this.getAbsolutePositionChanges(), context);
                } else {
                    rootTf = new AffineTransform(TEXTFLIP);
                }
                currentCanvas.setTextMatrix(rootTf);
                //Reset context of text move
                context.resetTextMove();
                //Apply relative move
                if (this.containsRelativeMove()) {
                    float[] rootMove = this.getRelativeTranslation();
                    context.addTextMove(rootMove[0], -rootMove[1]); //-y to account for the text-matrix transform we do in the text root to account for the coordinates
                }
                //handle white-spaces
                if (!whiteSpaceProcessed) {
                    SvgTextUtil.processWhiteSpace(this, true);
                }
            }
            applyTextRenderingMode(currentCanvas);

            if (this.attributesAndStyles != null) {
                resolveFontSize();
                resolveFont(context);
                currentCanvas.setFontAndSize(font, fontSize);
                for (ISvgTextNodeRenderer c : children) {
                    float childLength = c.getTextContentLength(fontSize, font);
                    if (c.containsAbsolutePositionChange()) {
                        //TODO(DEVSIX-2507) support rotate and other attributes
                        float[][] absolutePositions = c.getAbsolutePositionChanges();
                        AffineTransform newTransform = getTextTransform(absolutePositions, context);
                        //overwrite the last transformation stored in the context
                        context.setLastTextTransform(newTransform);
                        //Apply transformation
                        currentCanvas.setTextMatrix(newTransform);
                        //Absolute position changes requires resetting the current text move in the context
                        context.resetTextMove();
                    }

                    //Handle Text-Anchor declarations
                    float textAnchorCorrection = getTextAnchorAlignmentCorrection(childLength);
                    if (!CssUtils.compareFloats(0f, textAnchorCorrection)) {
                        context.addTextMove(textAnchorCorrection, 0);
                    }
                    //Move needs to happen before the saving of the state in order for it to cascade beyond
                    if (c.containsRelativeMove()) {
                        float[] childMove = c.getRelativeTranslation();
                        context.addTextMove(childMove[0], -childMove[1]); //-y to account for the text-matrix transform we do in the text root to account for the coordinates
                    }
                    currentCanvas.saveState();
                    c.draw(context);

                    context.addTextMove(childLength, 0);
                    currentCanvas.restoreState();
                    //Restore transformation matrix
                    if (!context.getLastTextTransform().isIdentity()) {
                        currentCanvas.setTextMatrix(context.getLastTextTransform());
                    }

                }
                if (performRootTransformations) {
                    currentCanvas.endText();
                }
            }
        }
    }

    private void resolveTextMove() {
        if (this.attributesAndStyles != null) {
            String xRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.DX);
            String yRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.DY);

            List<String> xValuesList = SvgCssUtils.splitValueList(xRawValue);
            List<String> yValuesList = SvgCssUtils.splitValueList(yRawValue);

            xMove = 0f;
            yMove = 0f;

            if (!xValuesList.isEmpty()) {
                xMove = CssUtils.parseAbsoluteLength(xValuesList.get(0));
            }

            if (!yValuesList.isEmpty()) {
                yMove = CssUtils.parseAbsoluteLength(yValuesList.get(0));
            }
            moveResolved = true;
        }
    }

    private FontInfo resolveFontName(String fontFamily, String fontWeight, String fontStyle,
                                     FontProvider provider, FontSet tempFonts) {
        boolean isBold = fontWeight != null && fontWeight.equalsIgnoreCase(SvgConstants.Attributes.BOLD);
        boolean isItalic = fontStyle != null && fontStyle.equalsIgnoreCase(SvgConstants.Attributes.ITALIC);

        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        List<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(fontFamily);
        fontCharacteristics.setBoldFlag(isBold);
        fontCharacteristics.setItalicFlag(isItalic);

        return provider.getFontSelector(stringArrayList, fontCharacteristics, tempFonts).bestMatch();
    }

    private void resolveFont(SvgDrawContext context) {
        FontProvider provider = context.getFontProvider();
        FontSet tempFonts = context.getTempFonts();
        font = null;
        if (!provider.getFontSet().isEmpty() || (tempFonts != null && !tempFonts.isEmpty())) {
            String fontFamily = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_FAMILY);
            String fontWeight = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_WEIGHT);
            String fontStyle = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_STYLE);

            fontFamily = fontFamily != null ? fontFamily.trim() : "";
            FontInfo fontInfo = resolveFontName(fontFamily, fontWeight, fontStyle,
                    provider, tempFonts);
            font = provider.getPdfFont(fontInfo, tempFonts);
        }
        if (font == null) {
            try {
                // TODO (DEVSIX-2057)
                // TODO each call of createFont() create a new instance of PdfFont.
                // TODO FontProvider shall be used instead.
                font = PdfFontFactory.createFont();
            } catch (IOException e) {
                throw new SvgProcessingException(SvgLogMessageConstant.FONT_NOT_FOUND, e);
            }
        }
    }

    private void resolveFontSize() {
        //TODO (DEVSIX-2607) (re)move static variable
        fontSize = (float) SvgTextUtil.resolveFontSize(this, DEFAULT_FONT_SIZE);
    }

    private void resolveTextPosition() {
        if (this.attributesAndStyles != null) {
            String xRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.X);
            String yRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.Y);

            xPos = getPositionsFromString(xRawValue);
            yPos = getPositionsFromString(yRawValue);

            posResolved = true;
        }
    }

    private static float[] getPositionsFromString(String rawValuesString) {
        float[] result = null;
        List<String> valuesList = SvgCssUtils.splitValueList(rawValuesString);
        if (!valuesList.isEmpty()) {
            result = new float[valuesList.size()];
            for (int i = 0; i < valuesList.size(); i++) {
                result[i] = CssUtils.parseAbsoluteLength(valuesList.get(i));
            }
        }

        return result;
    }

    private static AffineTransform getTextTransform(float[][] absolutePositions, SvgDrawContext context) {
        AffineTransform tf = new AffineTransform();
        //If x is not specified, but y is, we need to correct for preceding text.
        if (absolutePositions[0] == null && absolutePositions[1] != null) {
            absolutePositions[0] = new float[]{context.getTextMove()[0]};
        }
        //If y is not present, we can replace it with a neutral transformation (0.0f)
        if (absolutePositions[1] == null) {
            absolutePositions[1] = new float[]{0.0f};
        }
        tf.concatenate(TEXTFLIP);
        tf.concatenate(AffineTransform.getTranslateInstance(absolutePositions[0][0], -absolutePositions[1][0]));

        return tf;
    }

    private void applyTextRenderingMode(PdfCanvas currentCanvas) {
        //Fill only is the default for text operation in PDF
        if (doStroke && doFill) {
            currentCanvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE); //Default for SVG
        } else {
            if (doStroke) {
                currentCanvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
            } else {
                currentCanvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
            }
        }
    }

    private void deepCopyChildren(TextSvgBranchRenderer deepCopy) {
        for (ISvgTextNodeRenderer child : children) {
            ISvgTextNodeRenderer newChild = (ISvgTextNodeRenderer) child.createDeepCopy();
            child.setParent(deepCopy);
            deepCopy.addChild(newChild);
        }
    }

    private float getTextAnchorAlignmentCorrection(float childContentLength) {
        // Resolve text anchor
        //TODO DEVSIX-2631 properly resolve text-anchor by taking entire line into account, not only children of the current TextSvgBranchRenderer
        float textAnchorXCorrection = 0.0f;
        if (this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_ANCHOR)) {
            String textAnchorValue = this.getAttribute(SvgConstants.Attributes.TEXT_ANCHOR);
            //Middle
            if (textAnchorValue.equals(SvgConstants.Values.TEXT_ANCHOR_MIDDLE)) {
                if (xPos != null && xPos.length > 0) {
                    textAnchorXCorrection -= childContentLength / 2;
                }
            }
            //End
            if (textAnchorValue.equals(SvgConstants.Values.TEXT_ANCHOR_END)) {
                if (xPos != null && xPos.length > 0) {
                    textAnchorXCorrection -= childContentLength;
                }
            }
        }
        return textAnchorXCorrection;
    }
}
