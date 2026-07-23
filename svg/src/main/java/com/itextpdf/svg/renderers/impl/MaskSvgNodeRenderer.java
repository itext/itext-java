/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfTransparencyGroup;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.INoDrawSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

import java.util.Map;

/**
 * Renderer for the {@code <mask>} tag.
 */
public class MaskSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements INoDrawSvgNodeRenderer {

    private static final LazyLogger LOGGER = new LazyLogger(MaskSvgNodeRenderer.class);

    private static final float PX_TO_PT = 0.75F;

    // Constants as defined in SVG specification: https://www.w3.org/TR/SVG11/masking.html#MaskElementXAttribute
    private static final String DEFAULT_MASK_X = "-10%";
    private static final String DEFAULT_MASK_Y = "-10%";
    private static final String DEFAULT_MASK_WIDTH = "120%";
    private static final String DEFAULT_MASK_HEIGHT = "120%";
    private static final double DEFAULT_MASK_OFFSET = -0.1;
    private static final double DEFAULT_MASK_DIMENSION = 1.2;

    /**
     * Creates a new {@link MaskSvgNodeRenderer} instance.
     */
    public MaskSvgNodeRenderer() {
        // Empty constructor
    }

    /**
     * Draws a renderer using this mask. The renderer's transform must already be applied to the current canvas.
     *
     * @param maskedRenderer renderer to be masked
     * @param context current draw context
     * @param maskId normalized id/reference value of the mask definition for cycle detection
     */
    void drawMaskedObject(AbstractSvgNodeRenderer maskedRenderer, SvgDrawContext context,
            String maskId) {
        Rectangle maskedBoundingBox = maskedRenderer.getObjectBoundingBox(context);
        Rectangle maskArea = calculateMaskArea(context, maskedBoundingBox);
        if (!isValidArea(maskArea)) {
            return;
        }

        PdfCanvas currentCanvas = context.getCurrentCanvas();

        PdfFormXObject maskForm = createMaskFormWithCycleProtection(maskArea, maskedBoundingBox, context, currentCanvas,
                maskId);
        if (maskForm == null) {
            return;
        }
        PdfFormXObject maskedContentForm = createMaskedContentForm(maskArea, maskedRenderer, context,
                currentCanvas);

        applySoftMaskAndDrawMaskedContent(currentCanvas, maskedContentForm, maskForm, maskArea);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISvgNodeRenderer createDeepCopy() {
        MaskSvgNodeRenderer copy = new MaskSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    /**
     * Mask renderers do not represent drawable graphics by themselves,
     * so they have no own object bounding box.
     *
     * @param context current draw context
     *
     * @return {@code null} always
     */
    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    /**
     * Mask elements are not rendered directly onto the current canvas.
     * They are applied through {@link #drawMaskedObject(AbstractSvgNodeRenderer, SvgDrawContext, String)}.
     *
     * @param context current draw context
     *
     * @throws UnsupportedOperationException always, because {@code <mask>} is a no-draw renderer
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgExceptionMessageConstant.DRAW_NO_DRAW);
    }

    private static PdfFormXObject createMaskedContentForm(Rectangle maskArea, AbstractSvgNodeRenderer maskedRenderer,
            SvgDrawContext context, PdfCanvas currentCanvas) {
        PdfFormXObject maskedContentForm = new PdfFormXObject(maskArea);
        PdfTransparencyGroup group = new PdfTransparencyGroup();
        group.setIsolated(true);
        maskedContentForm.setGroup(group);
        PdfCanvas maskedContentCanvas = new PdfCanvas(maskedContentForm, currentCanvas.getDocument());
        context.pushCanvas(maskedContentCanvas);
        try {
            maskedContentCanvas.rectangle(maskArea);
            maskedContentCanvas.clip();
            maskedContentCanvas.endPath();
            drawMaskedRendererCopy(maskedRenderer, context);
        } finally {
            context.popCanvas();
        }
        return maskedContentForm;
    }

    // Mask caching is not implemented because it would depend on
    // target bounding box and current viewport, even with userSpaceOnUse units.
    // It would make cache hits less likely and increase implementation complexity.
    private PdfFormXObject createMaskForm(Rectangle maskArea, Rectangle objectBoundingBox,
            SvgDrawContext context, PdfCanvas currentCanvas) {
        boolean objectBoundingBoxMaskContentUnits = isObjectBoundingBoxMaskContentUnits();
        if (objectBoundingBoxMaskContentUnits && !isValidArea(objectBoundingBox)) {
            return null;
        }

        PdfFormXObject maskForm = new PdfFormXObject(maskArea);
        PdfTransparencyGroup group = new PdfTransparencyGroup();
        group.setColorSpace(isAlphaMaskType() ? PdfName.DeviceRGB : PdfName.DeviceGray);
        maskForm.setGroup(group);
        PdfCanvas maskCanvas = new PdfCanvas(maskForm, currentCanvas.getDocument());
        context.pushCanvas(maskCanvas);
        context.pushMaskRenderingMode(!isAlphaMaskType());
        try {
            maskCanvas.rectangle(maskArea);
            maskCanvas.clip();
            maskCanvas.endPath();
            applyMaskContentUnitsTransform(maskCanvas, objectBoundingBox);
            for (ISvgNodeRenderer child : getChildren()) {
                maskCanvas.saveState();
                child.draw(context);
                maskCanvas.restoreState();
            }
        } finally {
            context.popMaskRenderingMode();
            context.popCanvas();
        }
        return maskForm;
    }

    private PdfFormXObject createMaskFormWithCycleProtection(Rectangle maskArea, Rectangle objectBoundingBox,
            SvgDrawContext context, PdfCanvas currentCanvas, String maskId) {
        if (!context.pushMaskId(maskId)) {
            return null;
        }
        try {
            return createMaskForm(maskArea, objectBoundingBox, context, currentCanvas);
        } finally {
            context.popMaskId();
        }
    }

    private static void drawMaskedRendererCopy(AbstractSvgNodeRenderer maskedRenderer, SvgDrawContext context) {
        AbstractSvgNodeRenderer drawableRenderer = (AbstractSvgNodeRenderer) maskedRenderer.createDeepCopy();
        drawableRenderer.setParent(maskedRenderer.getParent());
        Map<String, String> drawableStyles = drawableRenderer.getAttributeMapCopy();
        drawableStyles.remove(SvgConstants.Attributes.MASK);
        // The target transform has already been applied by AbstractSvgNodeRenderer.draw.
        drawableStyles.remove(SvgConstants.Attributes.TRANSFORM);
        drawableRenderer.setAttributesAndStyles(drawableStyles);
        drawableRenderer.draw(context);
    }

    /**
     * Maps mask child coordinates to the masked object's bounding box when
     * {@code maskContentUnits="objectBoundingBox"}. In this coordinate system, the origin is the bounding-box
     * origin and one unit represents its full width or height, rather than a user-space length.
     * Translation and scaling are therefore needed to align the mask content with the masked object.
     *
     * <p>Child renderers parse unitless lengths as pixels and convert them to points. Dividing the scale factors
     * by {@code PX_TO_PT} compensates for that conversion, so a unitless length of one spans the corresponding
     * bounding-box dimension. For {@code userSpaceOnUse} (the default), no transformation is needed.
     *
     * @param maskCanvas canvas on which the mask children will be drawn
     * @param objectBoundingBox masked object's bounding box; must be non-null with positive dimensions
     * when using {@code objectBoundingBox} content units
     */
    private void applyMaskContentUnitsTransform(PdfCanvas maskCanvas, Rectangle objectBoundingBox) {
        if (!isObjectBoundingBoxMaskContentUnits()) {
            return;
        }

        AffineTransform toObjectBoundingBox = new AffineTransform();
        toObjectBoundingBox.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
        toObjectBoundingBox.scale(objectBoundingBox.getWidth() / PX_TO_PT,
                objectBoundingBox.getHeight() / PX_TO_PT);
        maskCanvas.concatMatrix(toObjectBoundingBox);
    }

    private void applySoftMaskAndDrawMaskedContent(PdfCanvas currentCanvas, PdfFormXObject maskedContentForm,
            PdfFormXObject maskForm, Rectangle maskArea) {
        PdfExtGState extGState = createSoftMaskExtGState(maskForm);

        currentCanvas.saveState();
        currentCanvas.setExtGState(extGState);
        currentCanvas.addXObjectAt(maskedContentForm, maskArea.getX(), maskArea.getY());
        currentCanvas.restoreState();
    }

    private PdfExtGState createSoftMaskExtGState(PdfFormXObject maskForm) {
        PdfDictionary softMask = new PdfDictionary();
        softMask.put(PdfName.S, isAlphaMaskType() ? PdfName.Alpha : PdfName.Luminosity);
        softMask.put(PdfName.G, maskForm.getPdfObject());

        PdfExtGState extGState = new PdfExtGState();
        extGState.setSoftMask(softMask);
        return extGState;
    }

    private static boolean isValidArea(Rectangle area) {
        return area != null && area.getWidth() > 0 && area.getHeight() > 0;
    }

    private Rectangle calculateMaskArea(SvgDrawContext context, Rectangle objectBoundingBox) {
        if (isObjectBoundingBoxMaskUnits()) {
            if (objectBoundingBox == null || objectBoundingBox.getWidth() <= 0 || objectBoundingBox.getHeight() <= 0) {
                return null;
            }

            double xRel = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(SvgConstants.Attributes.X), DEFAULT_MASK_OFFSET);
            double yRel = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(SvgConstants.Attributes.Y), DEFAULT_MASK_OFFSET);
            double widthRel = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(SvgConstants.Attributes.WIDTH), DEFAULT_MASK_DIMENSION);
            double heightRel = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(SvgConstants.Attributes.HEIGHT), DEFAULT_MASK_DIMENSION);
            if (widthRel < 0 || heightRel < 0) {
                LOGGER.warn(() -> SvgLogMessageConstant.MASK_WIDTH_OR_HEIGHT_IS_NEGATIVE);
            }

            return new Rectangle(
                    (float) (objectBoundingBox.getX() + objectBoundingBox.getWidth() * xRel),
                    (float) (objectBoundingBox.getY() + objectBoundingBox.getHeight() * yRel),
                    (float) (objectBoundingBox.getWidth() * widthRel),
                    (float) (objectBoundingBox.getHeight() * heightRel));
        }

        float x = parseHorizontalLength(getAttributeOrDefault(SvgConstants.Attributes.X, DEFAULT_MASK_X), context);
        float y = parseVerticalLength(getAttributeOrDefault(SvgConstants.Attributes.Y, DEFAULT_MASK_Y), context);
        float width = parseHorizontalLength(getAttributeOrDefault(SvgConstants.Attributes.WIDTH, DEFAULT_MASK_WIDTH),
                context);
        float height = parseVerticalLength(getAttributeOrDefault(SvgConstants.Attributes.HEIGHT, DEFAULT_MASK_HEIGHT),
                context);
        if (width < 0 || height < 0) {
            LOGGER.warn(() -> SvgLogMessageConstant.MASK_WIDTH_OR_HEIGHT_IS_NEGATIVE);
        }
        return new Rectangle(x, y, width, height);
    }

    private boolean isAlphaMaskType() {
        String maskType = getAttribute(SvgConstants.Attributes.MASK_TYPE);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (maskType == null) {
            maskType = getAttribute(StringNormalizer.toLowerCase(SvgConstants.Attributes.MASK_TYPE));
        }
        return SvgConstants.Values.ALPHA.equalsIgnoreCase(maskType);
    }

    private boolean isObjectBoundingBoxMaskUnits() {
        String maskUnits = getAttribute(SvgConstants.Attributes.MASK_UNITS);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (maskUnits == null) {
            maskUnits = getAttribute(StringNormalizer.toLowerCase(SvgConstants.Attributes.MASK_UNITS));
        }
        return !SvgConstants.Values.USER_SPACE_ON_USE.equals(maskUnits);
    }

    private boolean isObjectBoundingBoxMaskContentUnits() {
        String maskContentUnits = getAttribute(SvgConstants.Attributes.MASK_CONTENT_UNITS);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (maskContentUnits == null) {
            maskContentUnits = getAttribute(StringNormalizer.toLowerCase(SvgConstants.Attributes.MASK_CONTENT_UNITS));
        }
        return SvgConstants.Values.OBJECT_BOUNDING_BOX.equals(maskContentUnits);
    }
}
