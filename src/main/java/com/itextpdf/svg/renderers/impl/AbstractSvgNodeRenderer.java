package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public abstract class AbstractSvgNodeRenderer implements ISvgNodeRenderer {

    private ISvgNodeRenderer parent;
    private final List<ISvgNodeRenderer> children = new ArrayList<>();
    protected Map<String, String> attributesAndStyles;
    private boolean doFill = false;

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        this.parent = parent;
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return parent;
    }

    @Override
    public final void addChild(ISvgNodeRenderer child) {
        // final method, in order to disallow adding null
        if (child != null) {
            children.add(child);
        }
    }

    @Override
    public final List<ISvgNodeRenderer> getChildren() {
        // final method, in order to disallow modifying the List
        return Collections.unmodifiableList(children);
    }

    @Override
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {
        this.attributesAndStyles = attributesAndStyles;
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
            String transformString = this.attributesAndStyles.get(SvgTagConstants.TRANSFORM);

            if (transformString != null && !transformString.isEmpty()) {
                AffineTransform transformation = TransformUtils.parseTransform(transformString);
                currentCanvas.concatMatrix(transformation);
            }
        }

        preDraw(context);
        doDraw(context);
        postDraw(context);

        if (attributesAndStyles != null && attributesAndStyles.containsKey(SvgTagConstants.ID)) {
            context.addNamedObject(attributesAndStyles.get(SvgTagConstants.ID), this);
        }
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

            // fill
            {
                String fillRawValue = getAttribute(SvgTagConstants.FILL);

                this.doFill = !SvgTagConstants.NONE.equalsIgnoreCase(fillRawValue);

                if (doFill && canElementFill()) {
                    // todo RND-865 default style sheets
                    Color color = ColorConstants.BLACK;

                    if (fillRawValue != null) {
                        color = WebColors.getRGBColor(fillRawValue);
                    }

                    currentCanvas.setFillColor(color);
                }
            }

            // stroke
            {
                String strokeRawValue = getAttribute(SvgTagConstants.STROKE);
                DeviceRgb rgbColor = WebColors.getRGBColor(strokeRawValue);

                if (strokeRawValue != null && rgbColor != null) {
                    currentCanvas.setStrokeColor(rgbColor);

                    String strokeWidthRawValue = getAttribute(SvgTagConstants.STROKE_WIDTH);

                    float strokeWidth = 1f;

                    if ( strokeWidthRawValue != null ) {
                        strokeWidth = CssUtils.parseAbsoluteLength(strokeWidthRawValue);
                    }

                    currentCanvas.setLineWidth(strokeWidth);
                }
            }
        }
    }

    protected boolean canElementFill() {
        return true;
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
            if ( doFill && canElementFill() ) {
                String fillRuleRawValue = getAttribute(SvgTagConstants.FILL_RULE);

                if (SvgTagConstants.FILL_RULE_EVEN_ODD.equalsIgnoreCase(fillRuleRawValue)) {
                    // TODO RND-878
                    currentCanvas.eoFill();
                } else {
                    currentCanvas.fill();
                }
            }

            if (getAttribute(SvgTagConstants.STROKE) != null) {
                currentCanvas.stroke();
            }

            currentCanvas.closePath();
        }
    }

    /**
     * Draws this element to a canvas-like object maintained in the context.
     *
     * @param context the object that knows the place to draw this element and maintains its state
     */
    protected abstract void doDraw(SvgDrawContext context);

    @Override
    public String getAttribute(String key) {
        return attributesAndStyles.get(key);
    }
}
