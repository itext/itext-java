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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.IBeforeTextRestoreExecutor;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TextAnchor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.SvgStrokeParameterConverter.PdfLineDashParameters;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.SvgTextProperties;
import com.itextpdf.svg.utils.SvgTextUtil;
import com.itextpdf.svg.utils.TextRectangle;

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
    @Deprecated
    protected boolean performRootTransformations;

    private Paragraph paragraph;
    private Rectangle objectBoundingBox;

    private boolean moveResolved;
    private float xMove;
    private float yMove;

    private boolean posResolved;
    private float[] xPos;
    private float[] yPos;

    private boolean whiteSpaceProcessed = false;

    /**
     * Creates new {@link TextSvgBranchRenderer} instance.
     */
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

    /**
     * Adds a child to the current {@link TextSvgBranchRenderer} renderer.
     *
     * @param child {@link ISvgTextNodeRenderer} child to add
     */
    public final void addChild(ISvgTextNodeRenderer child) {
        // Final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    /**
     * Retrieves a list of all children of the current {@link TextSvgBranchRenderer} renderer.
     *
     * @return a list of {@link ISvgTextNodeRenderer} children
     */
    public final List<ISvgTextNodeRenderer> getChildren() {
        // Final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    @Override
    @Deprecated
    public float getTextContentLength(float parentFontSize, PdfFont font) {
        return 0.0f; // Branch renderers do not contain any text themselves and do not contribute to the text length
    }

    @Override
    @Deprecated
    public float[] getRelativeTranslation() {
        return getRelativeTranslation(new SvgDrawContext(null, null));
    }

    /**
     * Gets relative translation of the current &lt;text&gt; or &lt;tspan&gt; element.
     *
     * @param context current {@link SvgDrawContext}
     *
     * @return float array that contains relative {@code dx} and {@code dy} translations
     */
    public float[] getRelativeTranslation(SvgDrawContext context) {
        if (!moveResolved) {
            resolveRelativeTextMove(context);
        }
        return new float[]{xMove, yMove};
    }

    @Override
    @Deprecated
    public boolean containsRelativeMove() {
        return containsRelativeMove(new SvgDrawContext(null, null));
    }

    /**
     * Checks whether current &lt;text&gt; or &lt;tspan&gt; element contains relative position change.
     *
     * @param context current {@link SvgDrawContext}
     *
     * @return {@code true} is current element contains relative position, {@code false} otherwise
     */
    public boolean containsRelativeMove(SvgDrawContext context) {
        if (!moveResolved) {
            resolveRelativeTextMove(context);
        }
        boolean isNullMove = CssUtils.compareFloats(0f, xMove) && CssUtils.compareFloats(0f, yMove); // comparison to 0
        return !isNullMove;
    }

    @Override
    public boolean containsAbsolutePositionChange() {
        return containsAbsolutePositionChange(new SvgDrawContext(null, null));
    }

    /**
     * Checks whether current &lt;text&gt; or &lt;tspan&gt; element contains absolute position attributes.
     *
     * @param context current {@link SvgDrawContext}
     *
     * @return {@code true} is current element contains absolute position, {@code false} otherwise
     */
    public boolean containsAbsolutePositionChange(SvgDrawContext context) {
        if (!posResolved) {
            resolveAbsoluteTextPosition(context);
        }
        return (xPos != null && xPos.length > 0) || (yPos != null && yPos.length > 0);
    }

    @Override
    public float[][] getAbsolutePositionChanges() {
        return getAbsolutePositionChanges(new SvgDrawContext(null, null));
    }

    /**
     * Gets absolute position of the current &lt;text&gt; or &lt;tspan&gt; element.
     *
     * @param context current {@link SvgDrawContext}
     *
     * @return float array that contains absolute {@code x} and {@code y} positions as either single item arrays
     * or null if attribute is not present
     */
    public float[][] getAbsolutePositionChanges(SvgDrawContext context) {
        if (!posResolved) {
            resolveAbsoluteTextPosition(context);
        }
        return new float[][]{xPos, yPos};
    }

    /**
     * Marks white-space property as processed.
     */
    public void markWhiteSpaceProcessed() {
        whiteSpaceProcessed = true;
    }

    @Override
    public TextRectangle getTextRectangle(SvgDrawContext context, Point startPoint) {
        if (this.attributesAndStyles == null) {
            return null;
        }
        startPoint = getStartPoint(context, startPoint);
        Rectangle commonRect = null;
        Rectangle textChunkRect = null;
        List<ISvgTextNodeRenderer> children = new ArrayList<>();
        collectChildren(children);
        float rootX = (float) startPoint.getX();
        String textAnchorValue = this.getAttribute(SvgConstants.Attributes.TEXT_ANCHOR);
        // We resolve absolutely positioned text chunks similar to doDraw method, but here we are interested only in
        // building of properly positioned rectangles without any drawing or visual properties applying.
        for (ISvgTextNodeRenderer child : children) {
            if (child instanceof TextSvgBranchRenderer) {
                TextSvgBranchRenderer childText = (TextSvgBranchRenderer) child;
                startPoint = childText.getStartPoint(context, startPoint);
                if (childText.containsAbsolutePositionChange(context) && textChunkRect != null) {
                    commonRect = getCommonRectangleWithAnchor(commonRect, textChunkRect, rootX, textAnchorValue);
                    // Start new text chunk.
                    textChunkRect = null;
                    textAnchorValue = childText.getAttribute(SvgConstants.Attributes.TEXT_ANCHOR);
                    rootX = (float) startPoint.getX();
                }
            } else {
                TextRectangle rectangle = child.getTextRectangle(context, startPoint);
                startPoint = rectangle.getTextBaseLineRightPoint();
                textChunkRect = Rectangle.getCommonRectangle(textChunkRect, rectangle);
            }
        }
        if (textChunkRect != null) {
            commonRect = getCommonRectangleWithAnchor(commonRect, textChunkRect, rootX, textAnchorValue);
            return new TextRectangle(commonRect.getX(), commonRect.getY(),
                    commonRect.getWidth(), commonRect.getHeight(), (float) startPoint.getY());
        }
        return null;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        if (getParent() instanceof TextSvgBranchRenderer) {
            return getParent().getObjectBoundingBox(context);
        }
        if (objectBoundingBox == null) {
            // Handle white-spaces
            if (!whiteSpaceProcessed) {
                SvgTextUtil.processWhiteSpace(this, true, context);
            }
            objectBoundingBox = getTextRectangle(context, null);
        }
        return objectBoundingBox;
    }

    @Override
    void preDraw(SvgDrawContext context) {
        super.preDraw(context);
        SvgTextUtil.applyTextDecoration(this, doFill, doStroke, context);
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
        if (getChildren().isEmpty() || this.attributesAndStyles == null) {
            return;
        }
        // Handle white-spaces
        if (!whiteSpaceProcessed) {
            SvgTextUtil.processWhiteSpace(this, true, context);
        }

        this.paragraph = new Paragraph();
        this.paragraph.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
        this.paragraph.setProperty(Property.RENDERING_MODE, RenderingMode.SVG_MODE);
        this.paragraph.setProperty(Property.NO_SOFT_WRAP_INLINE, true);
        if (getParentClipPath() != null) {
            this.paragraph.setProperty(Property.BEFORE_TEXT_RESTORE_EXECUTOR,
                    new ClippedElementDrawer(getParentClipPath(), context));
        }
        this.paragraph.setMargin(0);
        String direction = this.attributesAndStyles.get(SvgConstants.Attributes.DIRECTION);
        boolean isRtl = "rtl".equals(direction);
        if (isRtl) {
            paragraph.setProperty(Property.BASE_DIRECTION, BaseDirection.RIGHT_TO_LEFT);
        } else {
            paragraph.setProperty(Property.BASE_DIRECTION, BaseDirection.LEFT_TO_RIGHT);
        }
        applyTextRenderingMode(paragraph);
        applyFontProperties(paragraph, context);
        // We resolve and draw absolutely positioned text chunks similar to getTextRectangle method. We are interested
        // not only in building of properly positioned rectangles, but also in drawing and text properties applying.
        startNewTextChunk(context, TEXTFLIP);
        
        performDrawing(context);
        drawLastTextChunk(context);
        context.setSvgTextProperties(new SvgTextProperties());
    }

    void applyFontProperties(IElement element, SvgDrawContext context) {
        element.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(getCurrentFontSize(context)));

        FontProvider provider = context.getFontProvider();
        element.setProperty(Property.FONT_PROVIDER, provider);

        FontSet tempFonts = context.getTempFonts();
        element.setProperty(Property.FONT_SET, tempFonts);

        String fontFamily = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_FAMILY);
        String fontWeight = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_WEIGHT);
        String fontStyle = this.attributesAndStyles.get(SvgConstants.Attributes.FONT_STYLE);
        element.setProperty(Property.FONT, new String[]{fontFamily == null ? "" : fontFamily.trim()});
        element.setProperty(Property.FONT_WEIGHT, fontWeight);
        element.setProperty(Property.FONT_STYLE, fontStyle);
    }

    void applyTextRenderingMode(IElement element) {
        if (getParentClipPath() != null) {
            element.setProperty(Property.TEXT_RENDERING_MODE, TextRenderingMode.CLIP);
        } else {
            // Fill only is the default for text operation in PDF
            if (doStroke && doFill) {
                // Default for SVG
                element.setProperty(Property.TEXT_RENDERING_MODE, TextRenderingMode.FILL_STROKE);
            } else if (doStroke) {
                element.setProperty(Property.TEXT_RENDERING_MODE, TextRenderingMode.STROKE);
            } else {
                element.setProperty(Property.TEXT_RENDERING_MODE, TextRenderingMode.FILL);
            }
        }
    }

    void addTextChild(Text text, SvgDrawContext drawContext) {
        if (getParent() instanceof TextSvgBranchRenderer) {
            ((TextSvgBranchRenderer) getParent()).addTextChild(text, drawContext);
            return;
        }

        text.setProperty(Property.POSITION, LayoutPosition.RELATIVE);
        text.setProperty(Property.LEFT, drawContext.getRelativePosition()[0]);
        text.setProperty(Property.BOTTOM, drawContext.getRelativePosition()[1]);
        paragraph.add(text);
    }

    void performDrawing(SvgDrawContext context) {
        if (this.containsAbsolutePositionChange(context)) {
            drawLastTextChunk(context);
            // TODO: DEVSIX-2507 support rotate and other attributes
            float[][] absolutePositions = this.getAbsolutePositionChanges(context);
            AffineTransform newTransform = getTextTransform(absolutePositions, context);
            startNewTextChunk(context, newTransform);
        }
        if (this.containsRelativeMove(context)) {
            float[] rootMove = this.getRelativeTranslation(context);
            context.addTextMove(rootMove[0], rootMove[1]);
            context.moveRelativePosition(rootMove[0], rootMove[1]);
        }
        for (ISvgTextNodeRenderer child : children) {
            SvgTextProperties textProperties = new SvgTextProperties(context.getSvgTextProperties());
            child.setParent(this);
            child.draw(context);
            context.setSvgTextProperties(textProperties);
        }
    }

    private void startNewTextChunk(SvgDrawContext context, AffineTransform newTransform) {
        applyTextAnchor();
        context.setRootTransform(newTransform);
        context.resetTextMove();
        context.resetRelativePosition();
    }

    private void drawLastTextChunk(SvgDrawContext context) {
        if (getParent() instanceof TextSvgBranchRenderer) {
            ((TextSvgBranchRenderer) getParent()).drawLastTextChunk(context);
            return;
        }
        if (paragraph.getChildren().isEmpty()) {
            return;
        }
        ParagraphRenderer paragraphRenderer = new ParagraphRenderer(paragraph);
        paragraph.setNextRenderer(paragraphRenderer);
        try (Canvas canvas = new Canvas(context.getCurrentCanvas(), new Rectangle(
                (float) context.getRootTransform().getTranslateX(),
                (float) context.getRootTransform().getTranslateY(), 1e6f, 0))) {
            canvas.add(paragraph);
        }
        float textLength = paragraphRenderer.getLines().get(0).getOccupiedAreaBBox().getWidth();
        context.addTextMove(textLength, 0);
        paragraph.getChildren().clear();
    }

    @Override
    void applyFillAndStrokeProperties(FillProperties fillProperties, StrokeProperties strokeProperties,
            SvgDrawContext context) {
        if (fillProperties != null) {
            context.getSvgTextProperties().setFillColor(fillProperties.getColor());
            if (!CssUtils.compareFloats(fillProperties.getOpacity(), 1f)) {
                context.getSvgTextProperties().setFillOpacity(fillProperties.getOpacity());
            }
        }
        if (strokeProperties != null) {
            if (strokeProperties.getLineDashParameters() != null) {
                PdfLineDashParameters lineDashParameters = strokeProperties.getLineDashParameters();
                context.getSvgTextProperties().setDashPattern(lineDashParameters.getDashArray(),
                        lineDashParameters.getDashPhase());
            }
            if (strokeProperties.getColor() != null) {
                context.getSvgTextProperties().setStrokeColor(strokeProperties.getColor());
            }
            context.getSvgTextProperties().setLineWidth(strokeProperties.getWidth());
            if (!CssUtils.compareFloats(strokeProperties.getOpacity(), 1f)) {
                context.getSvgTextProperties().setStrokeOpacity(strokeProperties.getOpacity());
            }
        }
    }

    private void resolveRelativeTextMove(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            String xRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.DX);
            String yRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.DY);

            List<String> xValuesList = SvgCssUtils.splitValueList(xRawValue);
            List<String> yValuesList = SvgCssUtils.splitValueList(yRawValue);

            xMove = 0f;
            yMove = 0f;

            if (!xValuesList.isEmpty()) {
                xMove = parseHorizontalLength(xValuesList.get(0), context);
            }

            if (!yValuesList.isEmpty()) {
                yMove = parseVerticalLength(yValuesList.get(0), context);
            }
            moveResolved = true;
        }
    }

    private void resolveAbsoluteTextPosition(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            String xRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.X);
            String yRawValue = this.attributesAndStyles.get(SvgConstants.Attributes.Y);

            xPos = getPositionsFromString(xRawValue, context, true);
            yPos = getPositionsFromString(yRawValue, context, false);

            posResolved = true;
        }
    }

    private float[] getPositionsFromString(String rawValuesString, SvgDrawContext context, boolean isHorizontal) {
        float[] result = null;
        List<String> valuesList = SvgCssUtils.splitValueList(rawValuesString);
        if (!valuesList.isEmpty()) {
            result = new float[valuesList.size()];
            for (int i = 0; i < valuesList.size(); i++) {
                String value = valuesList.get(i);
                if (CssDimensionParsingUtils.determinePositionBetweenValueAndUnit(value) == 0) {
                    throw new StyledXMLParserException(MessageFormatUtil.format(StyledXMLParserException.NAN, value));
                }
                if (isHorizontal) {
                    result[i] = parseHorizontalLength(value, context);
                } else {
                    result[i] = parseVerticalLength(value, context);
                }
            }
        }

        return result;
    }

    private static AffineTransform getTextTransform(float[][] absolutePositions, SvgDrawContext context) {
        AffineTransform tf = new AffineTransform();
        // If x is not specified, but y is, we need to correct for preceding text.
        if (absolutePositions[0] == null && absolutePositions[1] != null) {
            absolutePositions[0] =
                    new float[]{(float) context.getRootTransform().getTranslateX() + context.getTextMove()[0]};
        }
        // If y is not present, we should take the last text y
        if (absolutePositions[1] == null) {
            absolutePositions[1] =
                    new float[]{(float) context.getRootTransform().getTranslateY() + context.getTextMove()[1]};
        }
        tf.concatenate(TEXTFLIP);
        tf.concatenate(AffineTransform.getTranslateInstance(absolutePositions[0][0], -absolutePositions[1][0]));

        return tf;
    }

    /**
     * Adjust absolutely positioned text chunk (shift it to the start of view port, apply text anchor) and
     * merge it with the common text rectangle.
     *
     * @param commonRect rectangle for the whole text tag
     * @param textChunkRect rectangle for the last absolutely positioned text chunk
     * @param absoluteX last absolute x position
     * @param textAnchorValue text anchor for the last text chunk
     *
     * @return merged common text rectangle
     */
    private static Rectangle getCommonRectangleWithAnchor(Rectangle commonRect, Rectangle textChunkRect, float absoluteX,
                                                          String textAnchorValue) {
        textChunkRect.moveRight(absoluteX - textChunkRect.getX());
        if (SvgConstants.Values.TEXT_ANCHOR_MIDDLE.equals(textAnchorValue)) {
            textChunkRect.moveRight(-textChunkRect.getWidth() / 2);
        }
        if (SvgConstants.Values.TEXT_ANCHOR_END.equals(textAnchorValue)) {
            textChunkRect.moveRight(-textChunkRect.getWidth());
        }
        return Rectangle.getCommonRectangle(commonRect, textChunkRect);
    }

    private void deepCopyChildren(TextSvgBranchRenderer deepCopy) {
        for (ISvgTextNodeRenderer child : children) {
            ISvgTextNodeRenderer newChild = (ISvgTextNodeRenderer) child.createDeepCopy();
            newChild.setParent(deepCopy);
            deepCopy.addChild(newChild);
        }
    }

    private void applyTextAnchor() {
        if (this.attributesAndStyles != null &&
                (this.attributesAndStyles.containsKey(SvgConstants.Attributes.TEXT_ANCHOR) ||
                        this.attributesAndStyles.containsKey(SvgConstants.Attributes.DIRECTION))) {
            String textAnchorValue = getAttributeOrDefault(SvgConstants.Attributes.TEXT_ANCHOR,
                    SvgConstants.Values.TEXT_ANCHOR_START);
            String direction = this.attributesAndStyles.get(SvgConstants.Attributes.DIRECTION);
            boolean isRtl = "rtl".equals(direction);
            applyTextAnchor(textAnchorValue, isRtl);
        }
    }

    private void applyTextAnchor(String textAnchorValue, boolean isRtl) {
        if (getParent() instanceof TextSvgBranchRenderer) {
            ((TextSvgBranchRenderer) getParent()).applyTextAnchor(textAnchorValue, isRtl);
            return;
        }
        if (SvgConstants.Values.TEXT_ANCHOR_MIDDLE.equals(textAnchorValue)) {
            paragraph.setProperty(Property.TEXT_ANCHOR, TextAnchor.MIDDLE);
            return;
        }
        if (SvgConstants.Values.TEXT_ANCHOR_END.equals(textAnchorValue) && !isRtl ||
                !SvgConstants.Values.TEXT_ANCHOR_END.equals(textAnchorValue) && isRtl) {
            paragraph.setProperty(Property.TEXT_ANCHOR, TextAnchor.END);
            return;
        }
        paragraph.setProperty(Property.TEXT_ANCHOR, TextAnchor.START);
    }

    private Point getStartPoint(SvgDrawContext context, Point basePoint) {
        double x = 0;
        double y = 0;
        float[][] absolutePosition = getAbsolutePositionChanges(context);
        if (absolutePosition[0] != null) {
            x = absolutePosition[0][0];
        } else if (basePoint != null) {
            x = basePoint.getX();
        }
        if (absolutePosition[1] != null) {
            y = absolutePosition[1][0];
        } else if (basePoint != null) {
            y = basePoint.getY();
        }
        basePoint = new Point(x, y);
        basePoint.move(getRelativeTranslation(context)[0], getRelativeTranslation(context)[1]);
        return basePoint;
    }

    private void collectChildren(List<ISvgTextNodeRenderer> children) {
        for (ISvgTextNodeRenderer child : getChildren()) {
            children.add(child);
            if (child instanceof TextSvgBranchRenderer) {
                ((TextSvgBranchRenderer) child).collectChildren(children);
            }
        }
    }

    private static final class ClippedElementDrawer implements IBeforeTextRestoreExecutor {
        private final ClipPathSvgNodeRenderer clipPathSvgNodeRenderer;
        private final SvgDrawContext context;

        public ClippedElementDrawer(ClipPathSvgNodeRenderer clipPathSvgNodeRenderer, SvgDrawContext context) {
            this.clipPathSvgNodeRenderer = clipPathSvgNodeRenderer;
            this.context = context;
        }

        @Override
        public void execute() {
            clipPathSvgNodeRenderer.drawClippedRenderer(context);
        }
    }
}
