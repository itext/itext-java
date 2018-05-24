package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * Renderer implementing the use tag. This tag allows you to reuse previously defined elements.
 */
public class UseSvgNodeRenderer extends AbstractSvgNodeRenderer {
    @Override
    protected void doDraw(SvgDrawContext context) {
        if ( this.attributesAndStyles != null ) {
            String elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);

            if ( elementToReUse == null ) {
                elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);
            }

            if ( elementToReUse != null && !elementToReUse.isEmpty() ) {
                ISvgNodeRenderer namedObject = context.getNamedObject(normalizeName(elementToReUse));

                if ( namedObject != null ) {
                    PdfCanvas currentCanvas = context.getCurrentCanvas();

                    float x = 0f;
                    float y = 0f;

                    if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                        x = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.X));
                    }

                    if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                        y = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.Y));
                    }

                    if (x != 0 || y != 0) {
                        AffineTransform translation = AffineTransform.getTranslateInstance(x, y);
                        currentCanvas.concatMatrix(translation);
                    }

                    // setting the parent of the referenced element to this instance
                    namedObject.setParent(this);
                    namedObject.draw(context);
                    // unsetting the parent of the referenced element
                    namedObject.setParent(null);
                }
            }
        }
    }

    /**
     * The reference value will contain a hashtag character. This method will filter that value.
     *
     * @param name value to be filtered
     * @return filtered value
     */
    private String normalizeName(String name) {
        return name.replace("#", "").trim();
    }
}