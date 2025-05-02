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

import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.properties.TransparentColor;
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
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.css.SvgStrokeParameterConverter;
import com.itextpdf.svg.css.SvgStrokeParameterConverter.PdfLineDashParameters;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ISvgNodeRenderer} abstract implementation.
 */
public abstract class AbstractSvgNodeRenderer implements ISvgNodeRenderer {

    private static final MarkerVertexType[] MARKER_VERTEX_TYPES = new MarkerVertexType[] {MarkerVertexType.MARKER_START,
            MarkerVertexType.MARKER_MID, MarkerVertexType.MARKER_END};

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSvgNodeRenderer.class);

    /**
     * Map that contains attributes and styles used for drawing operations.
     */
    protected Map<String, String> attributesAndStyles;

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
            if (isHidden()) {
                return;
            }

            String transformString = this.attributesAndStyles.get(SvgConstants.Attributes.TRANSFORM);

            if (transformString != null && !transformString.isEmpty()) {
                AffineTransform transformation = TransformUtils.parseTransform(transformString);
                if (!transformation.isIdentity()) {
                    currentCanvas.concatMatrix(transformation);
                    if (getParentClipPath() != null) {
                        context.getClippingElementTransform().concatenate(transformation);
                    }
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
     * Return font-size of the current element in px.
     * <p>
     * This method is deprecated in favour of {@link AbstractSvgNodeRenderer#getCurrentFontSize(SvgDrawContext)} because
     * current one can't support relative values (em, rem) and those can't be resolved without {@link SvgDrawContext}.
     *
     * @return absolute value of font-size
     */
    @Deprecated
    public float getCurrentFontSize() {
        return getCurrentFontSize(new SvgDrawContext(null, null));
    }

    /**
     * Return font-size of the current element in px.
     *
     * @param context draw context from which root font size can be extracted
     *
     * @return absolute value of font-size
     */
    public float getCurrentFontSize(SvgDrawContext context) {
        String fontSizeAttribute = getAttribute(SvgConstants.Attributes.FONT_SIZE);
        if (CssTypesValidationUtils.isRemValue(fontSizeAttribute)) {
            return CssDimensionParsingUtils.parseRelativeValue(fontSizeAttribute, context.getCssContext().getRootFontSize());
        }
        if (CssTypesValidationUtils.isEmValue(fontSizeAttribute) && getParent() != null
                && getParent() instanceof AbstractSvgNodeRenderer) {
            return CssDimensionParsingUtils.parseRelativeValue(fontSizeAttribute,
                    ((AbstractSvgNodeRenderer) getParent()).getCurrentFontSize(context));
        }
        return CssDimensionParsingUtils.parseAbsoluteFontSize(fontSizeAttribute);
    }

    /**
     * Gets the viewbox from the first parent element which can define it.
     * <p>
     * See <a href="https://svgwg.org/svg2-draft/coords.html#EstablishingANewSVGViewport">SVG specification</a>
     * to find which elements can define a viewbox.
     *
     * @param context draw context from which fallback viewbox can be extracted
     *
     * @return the viewbox or {@code null} if the element doesn't have parent which can define the viewbox
     */
    public Rectangle getCurrentViewBox(SvgDrawContext context) {
        // According to https://svgwg.org/svg2-draft/coords.html#EstablishingANewSVGViewport: "For historical reasons,
        // the ‘pattern’ and ‘marker’ elements do not create a new viewport, despite accepting a ‘viewBox’ attribute".
        // So get viewbox only from symbol and svg elements
        if (this instanceof AbstractContainerSvgNodeRenderer) {
            float[] viewBoxValues = SvgCssUtils.parseViewBox(this);
            if (viewBoxValues == null || viewBoxValues.length < SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
                Rectangle currentViewPort = context.getCurrentViewPort();
                viewBoxValues = new float[]{0, 0, currentViewPort.getWidth(), currentViewPort.getHeight()};
            }
            return new Rectangle(viewBoxValues[0], viewBoxValues[1], viewBoxValues[2], viewBoxValues[3]);
        } else if (getParent() instanceof AbstractSvgNodeRenderer) {
            return ((AbstractSvgNodeRenderer) getParent()).getCurrentViewBox(context);
        } else {
            // From iText this line isn't reachable, in custom renderer tree fallback to context's view port
            return context.getCurrentViewPort();
        }
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

    String[] retrieveAlignAndMeet() {
        String meetOrSlice = SvgConstants.Values.MEET;
        String align = SvgConstants.Values.DEFAULT_ASPECT_RATIO;

        String preserveAspectRatioValue = this.attributesAndStyles.get(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (preserveAspectRatioValue == null) {
            preserveAspectRatioValue =
                    this.attributesAndStyles.get(StringNormalizer.toLowerCase(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO));
        }

        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO) ||
                this.attributesAndStyles.containsKey(StringNormalizer.toLowerCase(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO))) {
            List<String> aspectRatioValuesSplitValues = SvgCssUtils.splitValueList(preserveAspectRatioValue);

            align = StringNormalizer.toLowerCase(aspectRatioValuesSplitValues.get(0));
            if (aspectRatioValuesSplitValues.size() > 1) {
                meetOrSlice = StringNormalizer.toLowerCase(aspectRatioValuesSplitValues.get(1));
            }
        }

        if (this instanceof MarkerSvgNodeRenderer && !SvgConstants.Values.NONE.equals(align)
                && SvgConstants.Values.MEET.equals(meetOrSlice)) {
            // Browsers do not correctly display markers with 'meet' option in the preserveAspectRatio attribute.
            // The Chrome, IE, and Firefox browsers set the align value to 'xMinYMin' regardless of the actual align.
            align = SvgConstants.Values.XMIN_YMIN;
        }

        return new String[] {align, meetOrSlice};
    }

    /**
     * Check if this renderer should draw the element based on its attributes (e.g. visibility/display)
     *
     * @return true if element won't be drawn, false otherwise
     */
    protected boolean isHidden() {
        return CommonCssConstants.NONE.equals(this.attributesAndStyles.get(CommonCssConstants.DISPLAY))
                || CommonCssConstants.HIDDEN.equals(this.attributesAndStyles.get(CommonCssConstants.VISIBILITY));
    }

    /**
     * Gets parent {@link ClipPathSvgNodeRenderer} if it exists or {@code null} otherwise.
     *
     * @return the parent {@link ClipPathSvgNodeRenderer} or {@code null} otherwise
     */
    ClipPathSvgNodeRenderer getParentClipPath() {
        if (this instanceof ClipPathSvgNodeRenderer) {
            return (ClipPathSvgNodeRenderer) this;
        }
        if (getParent() == null) {
            return null;
        }
        if (getParent() instanceof AbstractSvgNodeRenderer) {
            return ((AbstractSvgNodeRenderer) getParent()).getParentClipPath();
        }
        return null;
    }

    /**
     * Applies non-scaling-stroke vector-effect to this renderer by concatenating all transformations applied
     * from the top level of the svg to the current one, inverting it and applying to the current canvas.
     *
     * @param context the SVG draw context
     *
     * @return the transformation that was inverted and applied to this renderer
     * to achieve non-scaling-stroke vector-effect
     */
    AffineTransform applyNonScalingStrokeTransform(SvgDrawContext context) {
        AffineTransform transform = null;
        boolean isNonScalingStroke = doStroke && SvgConstants.Values.NONE_SCALING_STROKE.equals(
                getAttribute(SvgConstants.Attributes.VECTOR_EFFECT));
        if (isNonScalingStroke) {
            transform = context.getConcatenatedTransform();
            try {
                context.getCurrentCanvas().concatMatrix(transform.createInverse());
            } catch (NoninvertibleTransformException e) {
                LOGGER.warn(SvgLogMessageConstant.NON_INVERTIBLE_TRANSFORMATION_MATRIX_FOR_NON_SCALING_STROKE);
                transform = null;
            }
        }
        return transform;
    }

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

            if (this instanceof ISvgTextNodeRenderer) {
                // Text rendering is performed via layout, which takes the responsibility for clipping, filling, stroking
                return;
            }

            // fill-rule
            if (getParentClipPath() == null) {
                if (doFill && canElementFill()) {
                    String fillRuleRawValue = getAttribute(SvgConstants.Attributes.FILL_RULE);
                    doStrokeOrFill(fillRuleRawValue, currentCanvas);
                } else if (doStroke) {
                    currentCanvas.stroke();
                } else {
                    currentCanvas.endPath();
                }
            } else {
                if (SvgConstants.Values.FILL_RULE_EVEN_ODD
                        .equalsIgnoreCase(this.getAttribute(SvgConstants.Attributes.CLIP_RULE))) {
                    currentCanvas.eoClip();
                } else {
                    currentCanvas.clip();
                }
                currentCanvas.endPath();
            }

            // Marker drawing
            if (this instanceof IMarkerCapable) {
                for (MarkerVertexType markerVertexType : MARKER_VERTEX_TYPES) {
                    if (attributesAndStyles.containsKey(markerVertexType.toString())) {
                        ((IMarkerCapable) this).drawMarker(context, markerVertexType);
                    }
                }
            }
        }
    }

    /**
     * Do stroke or fill based on {@code doFill/doStroke} fields.
     *
     * @param fillRuleRawValue fill rule attribute value.
     * @param currentCanvas current canvas to draw on.
     */
    void doStrokeOrFill(String fillRuleRawValue, PdfCanvas currentCanvas) {
        if (SvgConstants.Values.FILL_RULE_EVEN_ODD.equalsIgnoreCase(fillRuleRawValue)) {
            if (doStroke) {
                currentCanvas.eoFillStroke();
            } else {
                currentCanvas.eoFill();
            }
        } else {
            if (doStroke) {
                // TODO DEVSIX-8854 Draw SVG elements with transparent stroke in 2 steps
                currentCanvas.fillStroke();
            } else {
                currentCanvas.fill();
            }
        }
    }

    /**
     * Operations to perform before drawing an element.
     * This includes setting stroke color and width, fill color.
     *
     * @param context the svg draw context
     */
    void preDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null && getParentClipPath() == null) {
            FillProperties fillProperties = calculateFillProperties(context);
            StrokeProperties strokeProperties = calculateStrokeProperties(context);
            applyFillAndStrokeProperties(fillProperties, strokeProperties, context);
        }
    }

    void applyFillAndStrokeProperties(FillProperties fillProperties, StrokeProperties strokeProperties,
            SvgDrawContext context) {
        PdfExtGState opacityGraphicsState = new PdfExtGState();
        PdfCanvas currentCanvas = context.getCurrentCanvas();
        if (fillProperties != null) {
            currentCanvas.setFillColor(fillProperties.getColor());
            if (!CssUtils.compareFloats(fillProperties.getOpacity(), 1f)) {
                opacityGraphicsState.setFillOpacity(fillProperties.getOpacity());
            }
        }

        if (strokeProperties != null) {
            if (strokeProperties.getLineDashParameters() != null) {
                SvgStrokeParameterConverter.PdfLineDashParameters lineDashParameters = strokeProperties.getLineDashParameters();
                currentCanvas.setLineDash(lineDashParameters.getDashArray(), lineDashParameters.getDashPhase());
            }
            // As default value for stroke is 'none' we should not set it in case value obtaining fails
            if (strokeProperties.getColor() != null) {
                currentCanvas.setStrokeColor(strokeProperties.getColor());
            }
            currentCanvas.setLineWidth(strokeProperties.getWidth());

            if (!CssUtils.compareFloats(strokeProperties.getOpacity(), 1f)) {
                // TODO DEVSIX-8854 Draw SVG elements with transparent stroke in 2 steps
                opacityGraphicsState.setStrokeOpacity(strokeProperties.getOpacity());
            }
        }

        if (!opacityGraphicsState.getPdfObject().isEmpty()) {
            currentCanvas.setExtGState(opacityGraphicsState);
        }
    }

    /**
     * Parse x-axis length value.
     * If this method is called and there is no view port in {@link SvgDrawContext}, a default current viewport
     * will be created. This can happen if svg is created manually
     * (not through {@link com.itextpdf.svg.element.SvgImage} or {@link com.itextpdf.svg.xobject.SvgImageXObject})
     * and don't have {@link PdfRootSvgNodeRenderer} as its parent.
     *
     * @param length {@link String} length for parsing
     * @param context current {@link SvgDrawContext} instance
     * @return absolute length in points
     */
    protected float parseHorizontalLength(String length, SvgDrawContext context) {
        return SvgCssUtils.parseAbsoluteHorizontalLength(this, length, 0.0F, context);
    }

    /**
     * Parse y-axis length value.
     * If this method is called and there is no view port in {@link SvgDrawContext}, a default current viewport
     * will be created. This can happen if svg is created manually
     * (not through {@link com.itextpdf.svg.element.SvgImage} or {@link com.itextpdf.svg.xobject.SvgImageXObject})
     * and don't have {@link PdfRootSvgNodeRenderer} as its parent.
     *
     * @param length {@link String} length for parsing
     * @param context current {@link SvgDrawContext} instance
     * @return absolute length in points
     */
    protected float parseVerticalLength(String length, SvgDrawContext context) {
        return SvgCssUtils.parseAbsoluteVerticalLength(this, length, 0.0F, context);
    }

    /**
     * Parse length attributes.
     * <p>
     * This method is deprecated and
     * {@link SvgCssUtils#parseAbsoluteLength(AbstractSvgNodeRenderer, String, float, float, SvgDrawContext)} should
     * be used instead.
     *
     * @param length {@link String} for parsing
     * @param percentBaseValue the value on which percent length is based on
     * @param defaultValue default value if length is not recognized
     * @param context current {@link SvgDrawContext}
     * @return absolute value in points
     */
    @Deprecated
    protected float parseAbsoluteLength(String length, float percentBaseValue, float defaultValue,
            SvgDrawContext context) {
        return SvgCssUtils.parseAbsoluteLength(this, length, percentBaseValue, defaultValue, context);
    }

    private TransparentColor getColorFromAttributeValue(SvgDrawContext context, String rawColorValue,
            float objectBoundingBoxMargin, float parentOpacity) {
        if (rawColorValue == null) {
            return null;
        }
        if (CommonCssConstants.CURRENTCOLOR.equals(StringNormalizer.toLowerCase(rawColorValue))) {
            rawColorValue = getAttributeOrDefault(CommonCssConstants.COLOR, "black");
        }
        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(rawColorValue);
        Token token = tokenizer.getNextValidToken();
        if (token == null) {
            return null;
        }
        String tokenValue = token.getValue();
        boolean isUrlInvalid = false;
        if (tokenValue.startsWith("url(") && tokenValue.endsWith(")")) {
            String normalizedName = tokenValue.substring(4, tokenValue.length() - 1).trim();
            normalizedName = CssUtils.extractUnquotedString(normalizedName);
            if (normalizedName.startsWith("#")) {
                Color resolvedColor = null;
                float resolvedOpacity = 1;
                normalizedName = normalizedName.substring(1);
                ISvgNodeRenderer colorRenderer = context.getNamedObject(normalizedName);
                if (colorRenderer instanceof AbstractSvgNodeRenderer
                        && ((AbstractSvgNodeRenderer)colorRenderer).isHidden()) {
                    colorRenderer = null;
                }
                if (colorRenderer instanceof ISvgPaintServer) {
                    if (colorRenderer.getParent() == null) {
                        colorRenderer.setParent(this);
                    }
                    resolvedColor = ((ISvgPaintServer) colorRenderer).createColor(
                            context, getObjectBoundingBox(context), objectBoundingBoxMargin, parentOpacity);
                }
                if (resolvedColor != null) {
                    return new TransparentColor(resolvedColor, resolvedOpacity);
                }
                if (colorRenderer == null) {
                    isUrlInvalid = true;
                }
            } else {
                //we don't support those, but we need to make fill transparent anyway in such a case
                isUrlInvalid = true;
                //try to get next token to work the same as for the local url values
            }
            token = tokenizer.getNextValidToken();
        } else if (tokenValue.startsWith("url(") && !tokenValue.contains(" ")) {
            //for cases like url\([\w\d]+ browser treats them as urls, but url\([\w\d]+\s[\w\d]+ are not
            isUrlInvalid = true;
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
        if (isUrlInvalid) {
            return new TransparentColor(ColorConstants.BLACK, 0.0F);
        }
        return null;
    }

    private float getOpacityByAttributeName(String attributeName, float generalOpacity) {
        float opacity = generalOpacity;

        String opacityStr = getAttribute(attributeName);
        if (opacityStr != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityStr)) {
            float opacityValue;
            if (CssTypesValidationUtils.isPercentageValue(opacityStr)) {
                opacityValue = CssDimensionParsingUtils.parseRelativeValue(opacityStr, 1f);
            } else if (CssTypesValidationUtils.isNumber(opacityStr)) {
                opacityValue = (float) CssDimensionParsingUtils.parseFloat(opacityStr);
            } else {
                opacityValue = 1;
            }
            opacity *= opacityValue;
        }
        return opacity;
    }

    private boolean drawInClipPath(SvgDrawContext context) {
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.CLIP_PATH)) {
            String clipPathName = attributesAndStyles.get(SvgConstants.Attributes.CLIP_PATH);
            ISvgNodeRenderer template = context.getNamedObject(normalizeLocalUrlName(clipPathName));
            if (template instanceof ClipPathSvgNodeRenderer) {
                // Clone template to avoid muddying the state
                ClipPathSvgNodeRenderer clipPath = (ClipPathSvgNodeRenderer) template.createDeepCopy();
                if (clipPath.isHidden()) {
                    return false;
                }
                // Resolve parent inheritance
                SvgNodeRendererInheritanceResolver.applyInheritanceToSubTree(this, clipPath, context.getCssContext());
                clipPath.setClippedRenderer(this);
                clipPath.draw(context);
                return true;
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

    private FillProperties calculateFillProperties(SvgDrawContext context) {
        float generalOpacity = getOpacity();
        String fillRawValue = getAttributeOrDefault(Attributes.FILL, "black");
        this.doFill = !SvgConstants.Values.NONE.equalsIgnoreCase(fillRawValue);

        if (doFill && canElementFill()) {
            float fillOpacity = getOpacityByAttributeName(Attributes.FILL_OPACITY, generalOpacity);
            Color fillColor = null;

            TransparentColor transparentColor = getColorFromAttributeValue(context, fillRawValue, 0, fillOpacity);
            if (transparentColor != null) {
                fillColor = transparentColor.getColor();
                fillOpacity = transparentColor.getOpacity();
            }

            // set default if no color has been parsed
            if (fillColor == null) {
                fillColor = ColorConstants.BLACK;
            }
            return new FillProperties(fillOpacity, fillColor);
        }
        return null;
    }

    private StrokeProperties calculateStrokeProperties(SvgDrawContext context) {
        String strokeRawValue = getAttributeOrDefault(SvgConstants.Attributes.STROKE, SvgConstants.Values.NONE);
        if (!SvgConstants.Values.NONE.equalsIgnoreCase(strokeRawValue)) {
            String strokeWidthRawValue = getAttribute(SvgConstants.Attributes.STROKE_WIDTH);

            float strokeWidth = -1;
            if (strokeWidthRawValue != null) {
                strokeWidth = parseHorizontalLength(strokeWidthRawValue, context);
            }

            if (strokeWidth < 0) {
                // Default: 1 px = 0,75 pt
                strokeWidth = 0.75f;
            }

            float generalOpacity = getOpacity();
            float strokeOpacity = getOpacityByAttributeName(SvgConstants.Attributes.STROKE_OPACITY, generalOpacity);

            Color strokeColor = null;
            TransparentColor transparentColor = getColorFromAttributeValue(
                    context, strokeRawValue, (float) ((double) strokeWidth / 2.0), strokeOpacity);
            if (transparentColor != null) {
                strokeColor = transparentColor.getColor();
                strokeOpacity = transparentColor.getOpacity();
            }

            String strokeDashArrayRawValue = getAttribute(Attributes.STROKE_DASHARRAY);
            String strokeDashOffsetRawValue = getAttribute(Attributes.STROKE_DASHOFFSET);
            SvgStrokeParameterConverter.PdfLineDashParameters lineDashParameters =
                    SvgStrokeParameterConverter.convertStrokeDashParameters(strokeDashArrayRawValue,
                            strokeDashOffsetRawValue, getCurrentFontSize(context), context);

            if (strokeWidth > 0) {
                doStroke = true;
                return new StrokeProperties(strokeColor, strokeWidth, strokeOpacity, lineDashParameters);
            }
        }
        return null;
    }

    static final class FillProperties {
        final float opacity;
        final Color color;

        public FillProperties(float opacity, Color color) {
            this.opacity = opacity;
            this.color = color;
        }

        public float getOpacity() {
            return opacity;
        }

        public Color getColor() {
            return color;
        }
    }

    static final class StrokeProperties {
        final Color color;
        final float width;
        final float opacity;
        final SvgStrokeParameterConverter.PdfLineDashParameters lineDashParameters;

        public StrokeProperties(Color color, float width, float opacity,
                SvgStrokeParameterConverter.PdfLineDashParameters lineDashParameters) {
            this.color = color;
            this.width = width;
            this.opacity = opacity;
            this.lineDashParameters = lineDashParameters;
        }

        public Color getColor() {
            return color;
        }

        public float getWidth() {
            return width;
        }

        public float getOpacity() {
            return opacity;
        }

        public PdfLineDashParameters getLineDashParameters() {
            return lineDashParameters;
        }
    }
}
