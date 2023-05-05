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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer.Token;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;
import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ISvgNodeRenderer} abstract implementation.
 */
public abstract class AbstractSvgNodeRenderer implements ISvgNodeRenderer {

    // TODO (DEVSIX-3397) Add MarkerVertexType.MARKER_MID after ticket will be finished.
    private static final MarkerVertexType[] MARKER_VERTEX_TYPES = new MarkerVertexType[] {MarkerVertexType.MARKER_START,
            MarkerVertexType.MARKER_END};

    /**
     * Map that contains attributes and styles used for drawing operations.
     */
    protected Map<String, String> attributesAndStyles;

    boolean partOfClipPath;
    boolean doFill = false;
    boolean doStroke = false;

    private ISvgNodeRenderer parent;

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        this.parent = parent;
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return parent;
    }

    @Override
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {
        this.attributesAndStyles = attributesAndStyles;
    }

    @Override
    public String getAttribute(String key) {
        return attributesAndStyles.get(key);
    }

    /**
     * Retrieves the property value for a given key name or default if the property value is
     * {@code null} or missing.
     *
     * @param key          the name of the property to search for
     * @param defaultValue the default value to be returned if the property is
     *                     {@code null} or missing
     * @return the value for this key, or {@code defaultValue}
     */
    public String getAttributeOrDefault(String key, String defaultValue) {
        String rawValue = getAttribute(key);
        return rawValue != null ? rawValue : defaultValue;
    }

    @Override
    public void setAttribute(String key, String value) {
        if (this.attributesAndStyles == null) {
            this.attributesAndStyles = new HashMap<>();
        }

        this.attributesAndStyles.put(key, value);
    }

    @Override
    public Map<String, String> getAttributeMapCopy() {
        HashMap<String, String> copy = new HashMap<>();
        if (attributesAndStyles == null) {
            return copy;
        }
        copy.putAll(attributesAndStyles);
        return copy;
    }

    /**
     * Applies transformations set to this object, if any, and delegates the drawing of this element and its children
     * to the {@link #doDraw(SvgDrawContext) doDraw} method.
     *
     * @param context the object that knows the place to draw this element and maintains its state
     */
    @Override
    public final void draw(SvgDrawContext context) {
        PdfCanvas currentCanvas = context.getCurrentCanvas();

        if (this.attributesAndStyles != null) {
            String transformString = this.attributesAndStyles.get(SvgConstants.Attributes.TRANSFORM);

            if (transformString != null && !transformString.isEmpty()) {
                AffineTransform transformation = TransformUtils.parseTransform(transformString);
                if (!transformation.isIdentity()) {
                    currentCanvas.concatMatrix(transformation);
                }
            }

            if (attributesAndStyles.containsKey(SvgConstants.Attributes.ID)) {
                context.addUsedId(attributesAndStyles.get(SvgConstants.Attributes.ID));
            }
        }

        /* If a (non-empty) clipping path exists, drawing operations must be surrounded by q/Q operators
            and may have to be drawn multiple times
        */
        if (!drawInClipPath(context)) {
            preDraw(context);
            doDraw(context);
            postDraw(context);
        }

        if (attributesAndStyles.containsKey(SvgConstants.Attributes.ID)) {
            context.removeUsedId(attributesAndStyles.get(SvgConstants.Attributes.ID));
        }
    }

    /**
     * Method to see if a certain renderer can use fill.
     *
     * @return true if the renderer can use fill
     */
    protected boolean canElementFill() {
        return true;
    }

    /**
     * Method to see if the renderer can create a viewport
     *
     * @return true if the renderer can construct a viewport
     */
    public boolean canConstructViewPort() {
        return false;
    }


    /**
     * Return font-size of the current element
     *
     * @return absolute value of font-size
     */
    public float getCurrentFontSize() {
        return CssDimensionParsingUtils.parseAbsoluteFontSize(getAttribute(SvgConstants.Attributes.FONT_SIZE));
    }

    /**
     * Make a deep copy of the styles and attributes of this renderer
     * Helper method for deep copying logic
     *
     * @param deepCopy renderer to insert the deep copied attributes into
     */
    protected void deepCopyAttributesAndStyles(ISvgNodeRenderer deepCopy) {
        Map<String, String> stylesDeepCopy = new HashMap<>();
        if (this.attributesAndStyles != null) {
            stylesDeepCopy.putAll(this.attributesAndStyles);
            deepCopy.setAttributesAndStyles(stylesDeepCopy);
        }
    }

    /**
     * Draws this element to a canvas-like object maintained in the context.
     *
     * @param context the object that knows the place to draw this element and maintains its state
     */
    protected abstract void doDraw(SvgDrawContext context);

    /**
     * Calculate the transformation for the viewport based on the context. Only used by elements that can create
     * viewports
     *
     * @param context the SVG draw context
     * @return the transformation that needs to be applied to this renderer
     */
    AffineTransform calculateViewPortTranslation(SvgDrawContext context) {
        Rectangle viewPort = context.getCurrentViewPort();
        AffineTransform transform;
        transform = AffineTransform.getTranslateInstance(viewPort.getX(), viewPort.getY());
        return transform;
    }


    /**
     * Operations to be performed after drawing the element.
     * This includes filling, stroking.
     *
     * @param context the svg draw context
     */
    void postDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            // fill-rule
            if (partOfClipPath) {
                if (SvgConstants.Values.FILL_RULE_EVEN_ODD
                        .equalsIgnoreCase(this.getAttribute(SvgConstants.Attributes.CLIP_RULE))) {
                    currentCanvas.eoClip();
                } else {
                    currentCanvas.clip();
                }
                currentCanvas.endPath();
            } else if (!(this instanceof ISvgTextNodeRenderer)) {
                if (doFill && canElementFill()) {
                    String fillRuleRawValue = getAttribute(SvgConstants.Attributes.FILL_RULE);

                    if (SvgConstants.Values.FILL_RULE_EVEN_ODD.equalsIgnoreCase(fillRuleRawValue)) {
                        if (doStroke) {
                            currentCanvas.eoFillStroke();
                        } else {
                            currentCanvas.eoFill();
                        }
                    } else {
                        if (doStroke) {
                            currentCanvas.fillStroke();
                        } else {
                            currentCanvas.fill();
                        }
                    }
                } else if (doStroke) {
                    currentCanvas.stroke();
                } else {
                    currentCanvas.endPath();
                }
            }
            // Marker drawing
            if (this instanceof IMarkerCapable) {
                // TODO (DEVSIX-3397) add processing of 'marker' property (shorthand for a joint using of all other properties)
                for (MarkerVertexType markerVertexType : MARKER_VERTEX_TYPES) {
                    if (attributesAndStyles.containsKey(markerVertexType.toString())) {
                        currentCanvas.saveState();
                        ((IMarkerCapable) this).drawMarker(context, markerVertexType);
                        currentCanvas.restoreState();
                    }
                }
            }
        }
    }

    void setPartOfClipPath(boolean value) {
        partOfClipPath = value;
    }

    /**
     * Operations to perform before drawing an element.
     * This includes setting stroke color and width, fill color.
     *
     * @param context the svg draw context
     */
    void preDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            PdfCanvas currentCanvas = context.getCurrentCanvas();

            PdfExtGState opacityGraphicsState = new PdfExtGState();
            if (!partOfClipPath) {
                float generalOpacity = getOpacity();
                // fill
                {
                    String fillRawValue = getAttributeOrDefault(SvgConstants.Attributes.FILL, "black");
                    this.doFill = !SvgConstants.Values.NONE.equalsIgnoreCase(fillRawValue);

                    if (doFill && canElementFill()) {
                        float fillOpacity = getOpacityByAttributeName(
                                SvgConstants.Attributes.FILL_OPACITY, generalOpacity);

                        Color fillColor = null;
                        TransparentColor transparentColor = getColorFromAttributeValue(
                                context, fillRawValue, 0, fillOpacity);
                        if (transparentColor != null) {
                            fillColor = transparentColor.getColor();
                            fillOpacity = transparentColor.getOpacity();
                        }

                        if (!CssUtils.compareFloats(fillOpacity, 1f)) {
                            opacityGraphicsState.setFillOpacity(fillOpacity);
                        }

                        // set default if no color has been parsed
                        if (fillColor == null) {
                            fillColor = ColorConstants.BLACK;
                        }
                        currentCanvas.setFillColor(fillColor);
                    }
                }
                // stroke
                {
                    String strokeRawValue = getAttributeOrDefault(SvgConstants.Attributes.STROKE,
                            SvgConstants.Values.NONE);

                    if (!SvgConstants.Values.NONE.equalsIgnoreCase(strokeRawValue)) {
                        String strokeWidthRawValue = getAttribute(SvgConstants.Attributes.STROKE_WIDTH);

                        // 1 px = 0,75 pt
                        float strokeWidth = 0.75f;

                        if (strokeWidthRawValue != null) {
                            strokeWidth = CssDimensionParsingUtils.parseAbsoluteLength(strokeWidthRawValue);
                        }

                        float strokeOpacity = getOpacityByAttributeName(SvgConstants.Attributes.STROKE_OPACITY,
                                generalOpacity);

                        Color strokeColor = null;
                        TransparentColor transparentColor = getColorFromAttributeValue(
                                context, strokeRawValue, (float) ((double) strokeWidth / 2.0), strokeOpacity);
                        if (transparentColor != null) {
                            strokeColor = transparentColor.getColor();
                            strokeOpacity = transparentColor.getOpacity();
                        }

                        if (!CssUtils.compareFloats(strokeOpacity, 1f)) {
                            opacityGraphicsState.setStrokeOpacity(strokeOpacity);
                        }

                        // as default value for stroke is 'none' we should not set
                        // it in case when value obtaining fails
                        if (strokeColor != null) {
                            currentCanvas.setStrokeColor(strokeColor);
                        }

                        currentCanvas.setLineWidth(strokeWidth);

                        doStroke = true;
                    }
                }
                // opacity
                {
                    if (!opacityGraphicsState.getPdfObject().isEmpty()) {
                        currentCanvas.setExtGState(opacityGraphicsState);
                    }
                }
            }
        }
    }

    /**
     * Parse absolute length.
     * @param length {@link String} for parsing
     * @param percentRelativeValue the value on which percent length is based on
     * @param defaultValue default value if length is not recognized
     * @param context current {@link SvgDrawContext}
     * @return absolute value in points
     */
    protected float parseAbsoluteLength(String length, float percentRelativeValue, float defaultValue,
            SvgDrawContext context) {
        if (CssTypesValidationUtils.isPercentageValue(length)) {
            return CssDimensionParsingUtils.parseRelativeValue(length, percentRelativeValue);
        } else {
            final float em = getCurrentFontSize();
            final float rem = context.getCssContext().getRootFontSize();
            UnitValue unitValue = CssDimensionParsingUtils.parseLengthValueToPt(length, em, rem);
            if (unitValue != null && unitValue.isPointValue()) {
                return unitValue.getValue();
            } else {
                return defaultValue;
            }
        }
    }

    private TransparentColor getColorFromAttributeValue(SvgDrawContext context, String rawColorValue,
            float objectBoundingBoxMargin, float parentOpacity) {
        if (rawColorValue == null) {
            return null;
        }
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(rawColorValue);
        Token token = tokenizer.getNextValidToken();
        if (token == null) {
            return null;
        }
        String tokenValue = token.getValue();
        if (tokenValue.startsWith("url(#") && tokenValue.endsWith(")")) {
            Color resolvedColor = null;
            float resolvedOpacity = 1;
            final String normalizedName = tokenValue.substring(5, tokenValue.length() - 1).trim();
            final ISvgNodeRenderer colorRenderer = context.getNamedObject(normalizedName);
            if (colorRenderer instanceof ISvgPaintServer) {
                resolvedColor = ((ISvgPaintServer) colorRenderer).createColor(
                        context, getObjectBoundingBox(context), objectBoundingBoxMargin, parentOpacity);
            }
            if (resolvedColor != null) {
                return new TransparentColor(resolvedColor, resolvedOpacity);
            }
            token = tokenizer.getNextValidToken();
        }
        // may become null after function parsing and reading the 2nd token
        if (token != null) {
            String value = token.getValue();
            if (!SvgConstants.Values.NONE.equalsIgnoreCase(value)) {
                if (!CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.COLOR, value))) {
                    return new TransparentColor(new DeviceRgb(0.0f, 0.0f, 0.0f), 1.0f);
                }
                TransparentColor result = CssDimensionParsingUtils.parseColor(value);
                return new TransparentColor(result.getColor(), result.getOpacity() * parentOpacity);
            }
        }
        return null;
    }

    private float getOpacityByAttributeName(String attributeName, float generalOpacity) {
        float opacity = generalOpacity;

        String opacityValue = getAttribute(attributeName);
        if (opacityValue != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityValue)) {
            opacity *= Float.valueOf(opacityValue);
        }
        return opacity;
    }

    private boolean drawInClipPath(SvgDrawContext context) {
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.CLIP_PATH)) {
            String clipPathName = attributesAndStyles.get(SvgConstants.Attributes.CLIP_PATH);
            ISvgNodeRenderer template = context.getNamedObject(normalizeLocalUrlName(clipPathName));
            //Clone template to avoid muddying the state
            if (template instanceof ClipPathSvgNodeRenderer) {
                ClipPathSvgNodeRenderer clipPath = (ClipPathSvgNodeRenderer) template.createDeepCopy();
                // Resolve parent inheritance
                SvgNodeRendererInheritanceResolver.applyInheritanceToSubTree(this, clipPath, context.getCssContext());
                clipPath.setClippedRenderer(this);
                clipPath.draw(context);
                return !clipPath.getChildren().isEmpty();
            }
        }
        return false;
    }

    private String normalizeLocalUrlName(String name) {
        return name.replace("url(#", "").replace(")", "").trim();
    }

    private float getOpacity() {
        float result = 1f;

        String opacityValue = getAttribute(SvgConstants.Attributes.OPACITY);
        if (opacityValue != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityValue)) {
            result = Float.valueOf(opacityValue);
        }
        if (parent != null && parent instanceof AbstractSvgNodeRenderer) {
            result *= ((AbstractSvgNodeRenderer) parent).getOpacity();
        }

        return result;
    }
}
