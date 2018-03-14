package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
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
    public void setAttributesAndStyles(Map<String,String> attributesAndStyles){
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
        if ( this.attributesAndStyles != null ) {
            String transformString = this.attributesAndStyles.get(SvgTagConstants.TRANSFORM);

            if (transformString != null && ! transformString.isEmpty()) {
                AffineTransform transformation = TransformUtils.parseTransform(transformString);
                context.getCurrentCanvas().concatMatrix(transformation);
            }
        }

        doDraw(context);
    }

    /**
     * Draws this element to a canvas-like object maintained in the context.
     *
     * @param context the object that knows the place to draw this element and maintains its state
     */
    protected abstract void doDraw(SvgDrawContext context);
}
