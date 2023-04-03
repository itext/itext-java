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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@link BlockRenderer} for form fields.
 */
public abstract class AbstractFormFieldRenderer extends BlockRenderer {

    /**
     * The flat renderer.
     */
    protected IRenderer flatRenderer;

    /**
     * Creates a new {@link AbstractFormFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    AbstractFormFieldRenderer(IFormField modelElement) {
        super(modelElement);
    }

    /**
     * Checks if form fields need to be flattened.
     *
     * @return true, if fields need to be flattened
     */
    public boolean isFlatten() {
        Boolean flatten = getPropertyAsBoolean(FormProperty.FORM_FIELD_FLATTEN);
        return flatten == null ?
                (boolean) modelElement.<Boolean>getDefaultProperty(FormProperty.FORM_FIELD_FLATTEN) : (boolean) flatten;
    }

    /**
     * Gets the default value of the form field.
     *
     * @return the default value of the form field
     */
    public String getDefaultValue() {
        String defaultValue = this.<String>getProperty(FormProperty.FORM_FIELD_VALUE);
        return defaultValue == null ?
                modelElement.<String>getDefaultProperty(FormProperty.FORM_FIELD_VALUE) : defaultValue;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.BlockRenderer#layout(com.itextpdf.layout.layout.LayoutContext)
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        childRenderers.clear();
        flatRenderer = null;

        float parentWidth = layoutContext.getArea().getBBox().getWidth();
        float parentHeight = layoutContext.getArea().getBBox().getHeight();

        IRenderer renderer = createFlatRenderer();
        renderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        renderer.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        addChild(renderer);

        Rectangle bBox = layoutContext.getArea().getBBox().clone().moveDown(INF - parentHeight).setHeight(INF);
        layoutContext.getArea().setBBox(bBox);
        // A workaround for the issue that super.layout clears Property.FORCED_PLACEMENT,
        // but we need it later in this function
        final boolean isForcedPlacement = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        LayoutResult result = super.layout(layoutContext);

        if (childRenderers.isEmpty()) {
            LoggerFactory.getLogger(getClass()).error(FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD);
            occupiedArea.getBBox().setWidth(0).setHeight(0);
        } else {
            flatRenderer = childRenderers.get(0);
            processLangAttribute();
            childRenderers.clear();
            childRenderers.add(flatRenderer);
            adjustFieldLayout(layoutContext);
            if (isLayoutBasedOnFlatRenderer()) {
                Rectangle fBox = flatRenderer.getOccupiedArea().getBBox();
                occupiedArea.getBBox().setX(fBox.getX()).setY(fBox.getY()).setWidth(fBox.getWidth())
                        .setHeight(fBox.getHeight());
                applyPaddings(occupiedArea.getBBox(), true);
                applyBorderBox(occupiedArea.getBBox(), true);
                applyMargins(occupiedArea.getBBox(), true);
            } else if (isForcedPlacement) {
                // This block of code appeared here because of
                // TODO DEVSIX-5042 HEIGHT property is ignored when FORCED_PLACEMENT is true
                // Height is wrong for the flat renderer and we adjust it here
                Rectangle fBox = getOccupiedArea().getBBox();
                LayoutArea childOccupiedArea = flatRenderer.getOccupiedArea();
                childOccupiedArea.getBBox().setY(fBox.getY()).setHeight(fBox.getHeight());
            }
        }
        if (!isForcedPlacement && !isRendererFit(parentWidth, parentHeight)) {
            occupiedArea.getBBox().setWidth(0).setHeight(0);
            return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this)
                    .setMinMaxWidth(new MinMaxWidth());
        }
        if (result.getStatus() != LayoutResult.FULL || !isRendererFit(parentWidth, parentHeight)) {
            LoggerFactory.getLogger(getClass()).warn(FormsLogMessageConstants.INPUT_FIELD_DOES_NOT_FIT);
        }
        return new MinMaxWidthLayoutResult(LayoutResult.FULL, occupiedArea, this, null)
                .setMinMaxWidth(
                        new MinMaxWidth(occupiedArea.getBBox().getWidth(), occupiedArea.getBBox().getWidth(), 0));
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.BlockRenderer#draw(com.itextpdf.layout.renderer.DrawContext)
     */
    @Override
    public void draw(DrawContext drawContext) {
        if (flatRenderer != null) {
            super.draw(drawContext);
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.AbstractRenderer#drawChildren(com.itextpdf.layout.renderer.DrawContext)
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        drawContext.getCanvas().saveState();
        boolean flatten = isFlatten();
        if (flatten) {
            drawContext.getCanvas().rectangle(applyBorderBox(occupiedArea.getBBox(), false)).clip().endPath();
            flatRenderer.draw(drawContext);
        } else {
            applyAcroField(drawContext);
        }
        drawContext.getCanvas().restoreState();
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.BlockRenderer#getMinMaxWidth(float)
     */
    @Override
    public MinMaxWidth getMinMaxWidth() {
        childRenderers.clear();
        flatRenderer = null;
        IRenderer renderer = createFlatRenderer();
        addChild(renderer);
        MinMaxWidth minMaxWidth = super.getMinMaxWidth();
        return minMaxWidth;
    }

    /**
     * Adjusts the field layout.
     * @param layoutContext layout context
     */
    protected abstract void adjustFieldLayout(LayoutContext layoutContext);

    /**
     * Creates the flat renderer instance.
     *
     * @return the renderer instance
     */
    protected abstract IRenderer createFlatRenderer();

    /**
     * Applies the AcroField widget.
     *
     * @param drawContext the draw context
     */
    protected abstract void applyAcroField(DrawContext drawContext);

    /**
     * Gets the model id.
     *
     * @return the model id
     */
    protected String getModelId() {
        return ((IFormField) getModelElement()).getId();
    }

    /**
     * Checks if the renderer fits a certain width and height.
     *
     * @param availableWidth  the available width
     * @param availableHeight the available height
     * @return true, if the renderer fits
     */
    protected boolean isRendererFit(float availableWidth, float availableHeight) {
        if (occupiedArea == null) {
            return false;
        }
        return availableHeight >= occupiedArea.getBBox().getHeight() &&
                ((availableWidth >= occupiedArea.getBBox().getWidth()) ||
                        (this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X)
                                == OverflowPropertyValue.VISIBLE));
    }

    /**
     * Gets the accessibility language.
     *
     * @return the accessibility language
     */
    protected String getLang() {
        return this.<String>getProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE);
    }

    protected boolean isLayoutBasedOnFlatRenderer() {
        return true;
    }

    protected void writeAcroFormFieldLangAttribute(PdfDocument pdfDoc) {
        if (pdfDoc.isTagged()) {
            TagTreePointer formParentPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
            List<String> kidsRoles = formParentPointer.getKidsRoles();
            int lastFormIndex = kidsRoles.lastIndexOf(StandardRoles.FORM);
            TagTreePointer formPointer = formParentPointer.moveToKid(lastFormIndex);

            if (getLang() != null) {
                formPointer.getProperties().setLanguage(getLang());
            }
            formParentPointer.moveToParent();
        }
    }

    private void processLangAttribute() {
        IPropertyContainer propertyContainer = flatRenderer.getModelElement();
        String lang = getLang();
        if (propertyContainer instanceof IAccessibleElement && lang != null) {
            AccessibilityProperties properties = ((IAccessibleElement) propertyContainer).getAccessibilityProperties();
            if (properties.getLanguage() == null) {
                properties.setLanguage(lang);
            }
        }
    }
}
