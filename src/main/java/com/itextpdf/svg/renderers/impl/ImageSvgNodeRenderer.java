package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;


/**
 * Responsible for drawing Images to the canvas.
 * Referenced SVG images aren't supported yet. TODO RND-984
 */
public class ImageSvgNodeRenderer extends AbstractSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        ResourceResolver resourceResolver = context.getResourceResolver();

        if ( resourceResolver != null && this.attributesAndStyles != null ) {
            PdfImageXObject xObject = resourceResolver.retrieveImage(this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF));

            if (xObject != null) {
                PdfCanvas currentCanvas = context.getCurrentCanvas();

                float x = 0;
                float y = 0;

                if (attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                    x = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.X));
                }

                if (attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                    y = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.Y));
                }

                float width = 0;

                if (attributesAndStyles.containsKey(SvgConstants.Attributes.WIDTH)) {
                    width = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.WIDTH));
                }

                float height = 0;

                if (attributesAndStyles.containsKey(SvgConstants.Attributes.HEIGHT)) {
                    height = CssUtils.parseAbsoluteLength(attributesAndStyles.get(SvgConstants.Attributes.HEIGHT));
                }

                if (attributesAndStyles.containsKey(SvgConstants.Attributes.PRESERVE_ASPECT_RATIO)) {
                    // TODO RND-876
                }

                float v = y + height;

                currentCanvas.addXObject(xObject, width, 0, 0, -height, x, v);
            }
        }
    }
}