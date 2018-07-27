package com.itextpdf.svg.renderers.impl;


import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgMathUtils;

/**
 * Renderer implementing the use tag. This tag allows you to reuse previously defined elements.
 */
public class UseSvgNodeRenderer extends AbstractSvgNodeRenderer {
    @Override
    protected void doDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            String elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);

            if (elementToReUse == null) {
                elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);
            }

            if (elementToReUse != null && !elementToReUse.isEmpty() && isValidHref(elementToReUse)) {
                String normalizedName = normalizeName(elementToReUse);
                if (!context.isIdUsedByUseTagBefore(normalizedName)) {
                    ISvgNodeRenderer template = context.getNamedObject(normalizedName);
                    //Clone template
                    ISvgNodeRenderer namedObject = template.createDeepCopy();
                    //Resolve parent inheritance
                    SvgNodeRendererInheritanceResolver iresolver = new SvgNodeRendererInheritanceResolver();
                    iresolver.applyInheritanceToSubTree(this,namedObject);

                    if (namedObject != null) {
                        PdfCanvas currentCanvas = context.getCurrentCanvas();

                        float x = 0f;
                        float y = 0f;

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                            x = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.X));
                        }

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                            y = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.Y));
                        }

                        if (!SvgMathUtils.compareFloats(x,0) || !SvgMathUtils.compareFloats(y,0)) {
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

    private boolean isValidHref(String name) {
        return name.startsWith("#");
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        UseSvgNodeRenderer copy = new UseSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}