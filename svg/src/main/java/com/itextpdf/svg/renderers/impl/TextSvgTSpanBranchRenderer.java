/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

public class TextSvgTSpanBranchRenderer extends TextSvgBranchRenderer {

    private static final float EPS = 0.0001f;

    public TextSvgTSpanBranchRenderer() {
        this.performRootTransformations = false;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return getParent().getObjectBoundingBox(context);
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        TextSvgBranchRenderer copy = new TextSvgTSpanBranchRenderer();
        fillCopy(copy);
        return copy;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        if (getChildren().size() > 0) { // if branch has no children, don't do anything
            PdfCanvas currentCanvas = context.getCurrentCanvas();
            if (this.attributesAndStyles != null) {
                for (ISvgTextNodeRenderer c : getChildren()) {

                    applyTextRenderingMode(currentCanvas);
                    resolveFont(context);
                    currentCanvas.setFontAndSize(getFont(), getCurrentFontSize());

                    final float childLength = c.getTextContentLength(getCurrentFontSize(), getFont());
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
                        context.setPreviousElementTextMove(null);
                    }

                    // Handle Text-Anchor declarations
                    float textAnchorCorrection = getTextAnchorAlignmentCorrection(childLength);
                    if (!CssUtils.compareFloats(0f, textAnchorCorrection)) {
                        context.addTextMove(textAnchorCorrection, 0);
                    }
                    // Move needs to happen before the saving of the state in order for it to cascade beyond
                    if (c.containsRelativeMove()) {
                        float[] childMove = c.getRelativeTranslation();
                        //-y to account for the text-matrix transform we do in the text root to account
                        // for the coordinates
                        context.addTextMove(childMove[0], -childMove[1]);
                        context.setPreviousElementTextMove(
                                new float[] {context.getPreviousElementTextMove()[0] + childMove[0],
                                context.getPreviousElementTextMove()[1] - childMove[1]});
                    }

                    CanvasGraphicsState savedState = new CanvasGraphicsState(currentCanvas.getGraphicsState());
                    c.draw(context);
                    applyGSDifference(currentCanvas, savedState);
                    context.addTextMove(childLength, 0);

                    if (!floatsAreEqual(childLength, 0)) {
                        context.setPreviousElementTextMove(new float[]{childLength, 0});
                    }
                }
            }
        }
    }

    // This method is used to follow q/Q store/restore approach. If some graphics characteristics
    // have been updated while processing this renderer's children, they are restored.
    void applyGSDifference(PdfCanvas currentCanvas, CanvasGraphicsState savedGs) {
        CanvasGraphicsState newGs = currentCanvas.getGraphicsState();
        if (!floatsAreEqual(savedGs.getCharSpacing(), newGs.getCharSpacing())) {
            currentCanvas.setCharacterSpacing(savedGs.getCharSpacing());
        }
        if (savedGs.getFillColor() != newGs.getFillColor()) {
            currentCanvas.setFillColor(savedGs.getFillColor());
        }
        if (savedGs.getFont() != newGs.getFont() || !floatsAreEqual(savedGs.getFontSize(), newGs.getFontSize())) {
            currentCanvas.setFontAndSize(savedGs.getFont(), savedGs.getFontSize());
        }
        if (!floatsAreEqual(savedGs.getLineWidth(), newGs.getLineWidth())) {
            currentCanvas.setLineWidth(savedGs.getLineWidth());
        }
        if (savedGs.getStrokeColor() != newGs.getStrokeColor()) {
            currentCanvas.setStrokeColor(savedGs.getStrokeColor());
        }
        if (savedGs.getTextRenderingMode() != newGs.getTextRenderingMode()) {
            currentCanvas.setTextRenderingMode(savedGs.getTextRenderingMode());
        }

        // Only the next extended options are set in svg
        if (!floatsAreEqual(savedGs.getFillOpacity(), newGs.getFillOpacity())
                || !floatsAreEqual(savedGs.getStrokeOpacity(), newGs.getStrokeOpacity())) {
            PdfExtGState extGState = new PdfExtGState();
            extGState.setFillOpacity(savedGs.getFillOpacity());
            extGState.setStrokeOpacity(savedGs.getStrokeOpacity());
            currentCanvas.setExtGState(extGState);
        }
    }

    private static boolean floatsAreEqual(float first, float second) {
        return Math.abs(first - second) < EPS;
    }
}
