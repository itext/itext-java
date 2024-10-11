/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.SvgTextUtil;
import com.itextpdf.svg.utils.TextRectangle;

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

    private final List<ISvgTextNodeRenderer> children = new ArrayList<>();
    protected boolean performRootTransformations;
    private PdfFont font;

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
        fillCopy(copy);
        return copy;
    }

    void fillCopy(TextSvgBranchRenderer copy) {
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
    }

    public final void addChild(ISvgTextNodeRenderer child) {
        // Final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    public final List<ISvgTextNodeRenderer> getChildren() {
        // Final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    @Override
    public float getTextContentLength(float parentFontSize, PdfFont font) {
        return 0.0f; // Branch renderers do not contain any text themselves and do not contribute to the text length
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

    @Override
    public TextRectangle getTextRectangle(SvgDrawContext context, Point basePoint) {
        if (this.attributesAndStyles != null) {
            resolveFont(context);
            double x = 0, y = 0;
            if (getAbsolutePositionChanges()[0] != null) {
                x = getAbsolutePositionChanges()[0][0];
            } else if (basePoint != null) {
                x = basePoint.getX();
            }
            if (getAbsolutePositionChanges()[1] != null) {
                y = getAbsolutePositionChanges()[1][0];
            } else if (basePoint != null) {
                y = basePoint.getY();
            }
            basePoint = new Point(x, y);
            basePoint.move(getRelativeTranslation()[0], getRelativeTranslation()[1]);
            Rectangle commonRect = null;
            for (ISvgTextNodeRenderer child : getChildren()) {
                if (child != null) {
                    TextRectangle rectangle = child
                            .getTextRectangle(context, basePoint);
                    basePoint = rectangle.getTextBaseLineRightPoint();
                    commonRect = Rectangle.getCommonRectangle(commonRect, rectangle);
                }
            }
            if (commonRect != null) {
                return new TextRectangle(commonRect.getX(), commonRect.getY(), commonRect.getWidth(),
                        commonRect.getHeight(), (float) basePoint.getY());
            }
        }
        return null;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return getTextRectangle(context, null);
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
            context.resetTextMove();
            context.setLastTextTransform(null);
            context.setRootTransform(null);
            if (this.attributesAndStyles != null) {
                for (ISvgTextNodeRenderer c : children) {
                    currentCanvas.saveState();
                    currentCanvas.beginText();

                    performRootTransformations(currentCanvas, context);

                    applyTextRenderingMode(currentCanvas);
                    resolveFont(context);
                    currentCanvas.setFontAndSize(font, getCurrentFontSize());

                    final float childLength = c.getTextContentLength(getCurrentFontSize(), font);
                    if (c.containsAbsolutePositionChange()) {
                        // TODO: DEVSIX-2507 support rotate and other attributes
                        float[][] absolutePositions = c.getAbsolutePositionChanges();
                        AffineTransform newTransform = getTextTransform(absolutePositions, context);
                        // Overwrite the last transformation stored in the context
                        context.setLastTextTransform(newTransform);
                        // Apply transformation
                        currentCanvas.setTextMatrix(newTransform);
                        // Absolute position changes requires resetting the current text move in the context
                        context.resetTextMove();
                    } else if (c instanceof TextLeafSvgNodeRenderer &&
                            !context.getLastTextTransform().isIdentity()) {
                        currentCanvas.setTextMatrix(context.getLastTextTransform());
                    } else {
                        // If we don't update the matrix, we should set root matrix as the last text matrix
                        context.setLastTextTransform(context.getRootTransform());
                    }

                    // Handle Text-Anchor declarations
                    float textAnchorCorrection = getTextAnchorAlignmentCorrection(childLength);
                    if (!CssUtils.compareFloats(0f, textAnchorCorrection)) {
                        context.addTextMove(textAnchorCorrection, 0);
                    }
                    // Move needs to happen before the saving of the state in order for it to cascade beyond
                    if (c.containsRelativeMove()) {
                        float[] childMove = c.getRelativeTranslation();
                        context.addTextMove(childMove[0], -childMove[1]); //-y to account for the text-matrix transform we do in the text root to account for the coordinates
                    }

                    c.draw(context);

                    context.addTextMove(childLength, 0);

                    context.setPreviousElementTextMove(null);

                    currentCanvas.endText();
                    currentCanvas.restoreState();
                }
            }
        }
    }

    void performRootTransformations(PdfCanvas currentCanvas, SvgDrawContext context) {
        // Current transformation matrix results in the character glyphs being mirrored, correct with inverse tf
        AffineTransform rootTf;
        if (this.containsAbsolutePositionChange()) {
            rootTf = getTextTransform(this.getAbsolutePositionChanges(), context);
        } else {
            rootTf = new AffineTransform(TEXTFLIP);
        }
        context.setRootTransform(rootTf);
        currentCanvas.setTextMatrix(rootTf);
        // Apply relative move
        if (this.containsRelativeMove()) {
            float[] rootMove = this.getRelativeTranslation();
            //-y to account for the text-matrix transform we do in the text root to account for the coordinates
            context.addTextMove(rootMove[0], -rootMove[1]);
        }
        // Handle white-spaces
        if (!whiteSpaceProcessed) {
            SvgTextUtil.processWhiteSpace(this, true);
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
                xMove = CssDimensionParsingUtils.parseAbsoluteLength(xValuesList.get(0));
            }

            if (!yValuesList.isEmpty()) {
                yMove = CssDimensionParsingUtils.parseAbsoluteLength(yValuesList.get(0));
            }
            moveResolved = true;
        }
    }

    private FontInfo resolveFontName(String fontFamily, String fontWeight, String fontStyle,
                                     FontProvider provider, FontSet tempFonts) {
        final boolean isBold = SvgConstants.Attributes.BOLD.equalsIgnoreCase(fontWeight);
        final boolean isItalic = SvgConstants.Attributes.ITALIC.equalsIgnoreCase(fontStyle);

        FontCharacteristics fontCharacteristics = new FontCharacteristics();
        List<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(fontFamily);
        fontCharacteristics.setBoldFlag(isBold);
        fontCharacteristics.setItalicFlag(isItalic);

        return provider.getFontSelector(stringArrayList, fontCharacteristics, tempFonts).bestMatch();
    }

    void resolveFont(SvgDrawContext context) {
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
                // TODO: DEVSIX-2057 each call of createFont() create a new instance of PdfFont.
                // FontProvider shall be used instead.
                font = PdfFontFactory.createFont();
            } catch (IOException e) {
                throw new SvgProcessingException(SvgExceptionMessageConstant.FONT_NOT_FOUND, e);
            }
        }
    }

    /**
     * Return the font used in this text element.
     * Note that font should already be resolved with {@link TextSvgBranchRenderer#resolveFont}.
     *
     * @return font of the current text element
     */
    PdfFont getFont() {
        return font;
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
                result[i] = CssDimensionParsingUtils.parseAbsoluteLength(valuesList.get(i));
            }
        }

        return result;
    }

    static AffineTransform getTextTransform(float[][] absolutePositions, SvgDrawContext context) {
        AffineTransform tf = new AffineTransform();
        // If x is not specified, but y is, we need to correct for preceding text.
        if (absolutePositions[0] == null && absolutePositions[1] != null) {
            absolutePositions[0] =
                    new float[]{context.getTextMove()[0] + (float)context.getLastTextTransform().getTranslateX()};
        }
        // If y is not present, we should take the last text y
        if (absolutePositions[1] == null) {
            absolutePositions[1] = new float[]{(float)context.getLastTextTransform().getTranslateY()};
        }
        tf.concatenate(TEXTFLIP);
        tf.concatenate(AffineTransform.getTranslateInstance(absolutePositions[0][0], -absolutePositions[1][0]));

        return tf;
    }

    void applyTextRenderingMode(PdfCanvas currentCanvas) {
        // Fill only is the default for text operation in PDF
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

    float getTextAnchorAlignmentCorrection(float childContentLength) {
        // Resolve text anchor
        // TODO DEVSIX-2631 properly resolve text-anchor by taking entire line into account, not only children of the current TextSvgBranchRenderer
        float textAnchorXCorrection = 0.0f;
        if (this.attributesAndStyles != null && this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_ANCHOR)) {
            String textAnchorValue = this.getAttribute(SvgConstants.Attributes.TEXT_ANCHOR);
            // Middle
            if (SvgConstants.Values.TEXT_ANCHOR_MIDDLE.equals(textAnchorValue)) {
                if (xPos != null && xPos.length > 0) {
                    textAnchorXCorrection -= childContentLength / 2;
                }
            }
            // End
            if (SvgConstants.Values.TEXT_ANCHOR_END.equals(textAnchorValue)) {
                if (xPos != null && xPos.length > 0) {
                    textAnchorXCorrection -= childContentLength;
                }
            }
        }
        return textAnchorXCorrection;
    }
}
