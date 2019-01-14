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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ISvgNodeRenderer} abstract implementation.
 */
public abstract class AbstractSvgNodeRenderer implements ISvgNodeRenderer {
    private ISvgNodeRenderer parent;

    /**
     * Map that contains attributes and styles used for drawing operations
     */
    protected Map<String, String> attributesAndStyles;

    private boolean doFill = false;
    private boolean doStroke = false;
    boolean partOfClipPath;

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

    private boolean drawInClipPath(SvgDrawContext context) {
        if (attributesAndStyles.containsKey(SvgConstants.Attributes.CLIP_PATH)) {
            String clipPathName = attributesAndStyles.get(SvgConstants.Attributes.CLIP_PATH);
            ISvgNodeRenderer template = context.getNamedObject(normalizeName(clipPathName));
            //Clone template to avoid muddying the state
            if (template instanceof ClipPathSvgNodeRenderer) {
                ClipPathSvgNodeRenderer clipPath = (ClipPathSvgNodeRenderer) template.createDeepCopy();
                clipPath.setClippedRenderer(this);
                clipPath.draw(context);
                return !clipPath.getChildren().isEmpty();
            }
        }
        return false;
    }

    private String normalizeName(String name) {
        return name.replace("url(#", "").replace(")", "").trim();
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

            if (!partOfClipPath) {
                // fill
                {
                    String fillRawValue = getAttribute(SvgConstants.Attributes.FILL);

                    this.doFill = !SvgConstants.Values.NONE.equalsIgnoreCase(fillRawValue);

                    if (doFill && canElementFill()) {
                        Color color = ColorConstants.BLACK;

                        if (fillRawValue != null) {
                            color = WebColors.getRGBColor(fillRawValue);
                        }

                        currentCanvas.setFillColor(color);
                    }
                }

                // stroke
                {
                    String strokeRawValue = getAttribute(SvgConstants.Attributes.STROKE);
                    if (!SvgConstants.Values.NONE.equalsIgnoreCase(strokeRawValue)) {
                        DeviceRgb rgbColor = WebColors.getRGBColor(strokeRawValue);

                        if (strokeRawValue != null && rgbColor != null) {
                            currentCanvas.setStrokeColor(rgbColor);

                            String strokeWidthRawValue = getAttribute(SvgConstants.Attributes.STROKE_WIDTH);

                            float strokeWidth = 1f;

                            if (strokeWidthRawValue != null) {
                                strokeWidth = CssUtils.parseAbsoluteLength(strokeWidthRawValue);
                            }

                            currentCanvas.setLineWidth(strokeWidth);
                            doStroke = true;
                        }
                    }
                }
                // opacity
                {
                    String opacityValue = getAttribute(SvgConstants.Attributes.FILL_OPACITY);
                    if (opacityValue != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityValue)) {
                        PdfExtGState gs1 = new PdfExtGState();
                        gs1.setFillOpacity(Float.valueOf(opacityValue));
                        currentCanvas.setExtGState(gs1);
                    }
                }
            }
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
     * Calculate the transformation for the viewport based on the context. Only used by elements that can create viewports
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
                if (SvgConstants.Values.FILL_RULE_EVEN_ODD.equalsIgnoreCase(this.getAttribute(SvgConstants.Attributes.CLIP_RULE))) {
                    currentCanvas.eoClip();
                } else {
                    currentCanvas.clip();
                }
                currentCanvas.newPath();
            } else {
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
                }
                currentCanvas.closePath(); // TODO: see if this is necessary DEVSIX-2583
            }
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

    @Override
    public void setAttribute(String key, String value) {
        if (this.attributesAndStyles == null) {
            this.attributesAndStyles = new HashMap<>();
        }

        this.attributesAndStyles.put(key, value);
    }

    @Override
    public Map<String, String> getAttributeMapCopy(){
        HashMap<String, String> copy = new HashMap<>();
        if(attributesAndStyles == null){
            return copy;
        }
        copy.putAll(attributesAndStyles);
        return copy;
    }

    @Override
    public boolean equals(Object other){
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        AbstractSvgNodeRenderer oar = (AbstractSvgNodeRenderer)other;
        //Compare attribute and style map
        boolean  attributesAndStylesEqual = true;
        if (attributesAndStyles != null && oar.attributesAndStyles!= null){
            attributesAndStylesEqual &= (attributesAndStyles.size() == oar.attributesAndStyles.size());
            for (Map.Entry<String, String> kvp :attributesAndStyles.entrySet()) {
                String value = oar.attributesAndStyles.get(kvp.getKey());
                if(value==null || !kvp.getValue().equals(value)){
                    return false;
                }
            }
        }else{
            attributesAndStylesEqual = (attributesAndStyles==null && oar.attributesAndStyles==null);
        }
        return attributesAndStylesEqual && doFill==oar.doFill && doStroke == oar.doStroke;
    }

    @Override
    public int hashCode(){
        //No particular reasoning behind this hashing
        int hash = 112;
        hash = hash *3+attributesAndStyles.hashCode();
        return hash;
    }
    /**
     * Make a deep copy of the styles and attributes of this renderer
     * Helper method for deep copying logic
     * @param deepCopy renderer to insert the deep copied attributes into
     */
    protected void deepCopyAttributesAndStyles(ISvgNodeRenderer deepCopy){
        Map<String,String> stylesDeepCopy = new HashMap<>();
        if(this.attributesAndStyles != null) {
            stylesDeepCopy.putAll(this.attributesAndStyles);
            deepCopy.setAttributesAndStyles(stylesDeepCopy);
        }
    }

    void setPartOfClipPath(boolean value) {
        partOfClipPath = value;
    }
}
