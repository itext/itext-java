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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.renderer.checkboximpl.HtmlCheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.ICheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfACheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfCheckBoxRenderingStrategy;
import com.itextpdf.forms.util.BorderStyleUtil;
import com.itextpdf.forms.util.FormFieldRendererUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.Map;


/**
 * The {@link AbstractFormFieldRenderer} implementation for checkboxes.
 */
public class CheckBoxRenderer extends AbstractFormFieldRenderer {

    // 1px
    public static final float DEFAULT_BORDER_WIDTH = 0.75F;
    // 11px
    private static final float DEFAULT_SIZE = 8.25F;


    /**
     * Creates a new {@link CheckBoxRenderer} instance.
     *
     * @param modelElement the model element
     */
    public CheckBoxRenderer(CheckBox modelElement) {
        super(modelElement);
        this.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.MIDDLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new CheckBoxRenderer((CheckBox) modelElement);
    }


    /**
     * Gets the rendering mode of the checkbox.
     *
     * @return the rendering mode of the checkbox
     */
    public RenderingMode getRenderingMode() {
        final RenderingMode renderingMode = this.<RenderingMode>getProperty(Property.RENDERING_MODE);
        if (renderingMode != null) {
            return renderingMode;
        }
        return RenderingMode.DEFAULT_LAYOUT_MODE;
    }

    /**
     * Gets the checkBoxType.
     *
     * @return the checkBoxType
     */
    public CheckBoxType getCheckBoxType() {
        if (this.hasProperty(FormProperty.FORM_CHECKBOX_TYPE)) {
            return (CheckBoxType) this.<CheckBoxType>getProperty(FormProperty.FORM_CHECKBOX_TYPE);
        }
        return CheckBoxType.CROSS;
    }

    /**
     * creates a ICheckBoxRenderingStrategy based on the current settings.
     *
     * @return the ICheckBoxRenderingStrategy
     */
    public ICheckBoxRenderingStrategy createCheckBoxRenderStrategy() {
        // html rendering is PDFA compliant this means we don't have to check if its PDFA.
        ICheckBoxRenderingStrategy renderingStrategy;
        final PdfConformance conformance = this.<PdfConformance>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL);
        boolean isConformantPdfDocument = conformance != null && conformance.isPdfAOrUa();
        if (getRenderingMode() == RenderingMode.HTML_MODE) {
            renderingStrategy = new HtmlCheckBoxRenderingStrategy();
        } else if (getRenderingMode() == RenderingMode.DEFAULT_LAYOUT_MODE && isConformantPdfDocument) {
            renderingStrategy = new PdfACheckBoxRenderingStrategy();
        } else {
            renderingStrategy = new PdfCheckBoxRenderingStrategy();
        }
        return renderingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBackground(DrawContext drawContext) {
        // Do not draw background here. It will be drawn in flat renderer.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBorder(DrawContext drawContext) {
        // Do not draw border here. It will be drawn in flat renderer.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        // Do not apply borders here, they will be applied in flat renderer
        return rect;
    }

    /**
     * Defines whether the box is checked or not.
     *
     * @return the default value of the checkbox field
     */
    public boolean isBoxChecked() {
        return Boolean.TRUE.equals(this.<Boolean>getProperty(FormProperty.FORM_FIELD_CHECKED));
    }

    /**
     * Adjusts the field layout.
     *
     * @param layoutContext layout context
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        // We don't need any layout adjustments
    }

    /**
     * Applies given paddings to the given rectangle.
     *
     * Checkboxes don't support setting of paddings as they are always centered.
     * So that this method returns the rectangle as is.
     *
     * @param rect     a rectangle paddings will be applied on.
     * @param paddings the paddings to be applied on the given rectangle
     * @param reverse  indicates whether paddings will be applied
     *                 inside (in case of false) or outside (in case of true) the rectangle.
     *
     * @return The rectangle NOT modified by the paddings.
     */
    @Override
    protected Rectangle applyPaddings(Rectangle rect, UnitValue[] paddings, boolean reverse) {
        return rect;
    }

    /**
     * Creates a flat renderer for the checkbox.
     *
     * @return an IRenderer object for the flat renderer
     */
    @Override
    public IRenderer createFlatRenderer() {
        final UnitValue heightUV = getPropertyAsUnitValue(Property.HEIGHT);
        final UnitValue widthUV = getPropertyAsUnitValue(Property.WIDTH);

        // if it is a percentage value, we need to calculate the actual value but we
        // don't have the parent's width yet, so we will take the default value
        float height = DEFAULT_SIZE;
        if (heightUV != null && heightUV.isPointValue()) {
            height = heightUV.getValue();
        }

        float width = DEFAULT_SIZE;
        if (widthUV != null && widthUV.isPointValue()) {
            width = widthUV.getValue();
        }

        final Paragraph paragraph = new Paragraph().setWidth(width).setHeight(height).setMargin(0)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(TextAlignment.CENTER);

        paragraph.setProperty(Property.BOX_SIZING, this.<BoxSizingPropertyValue>getProperty(Property.BOX_SIZING));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));
        paragraph.setBorderTop(this.<Border>getProperty(Property.BORDER_TOP));
        paragraph.setBorderRight(this.<Border>getProperty(Property.BORDER_RIGHT));
        paragraph.setBorderBottom(this.<Border>getProperty(Property.BORDER_BOTTOM));
        paragraph.setBorderLeft(this.<Border>getProperty(Property.BORDER_LEFT));
        paragraph.setProperty(Property.BACKGROUND, this.<Background>getProperty(Property.BACKGROUND));

        //In html 2 pdf rendering the boxes height width ratio is always 1:1
        // with chrome taking the max of the two as the size of the box
        if (getRenderingMode() == RenderingMode.HTML_MODE) {
            paragraph.setWidth(Math.max(width, height));
            paragraph.setHeight(Math.max(width, height));
        }
        return new FlatParagraphRenderer(paragraph);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void applyAcroField(DrawContext drawContext) {
        final String name = getModelId();
        final PdfDocument doc = drawContext.getDocument();
        final Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();

        final Map<Integer, Object> properties = FormFieldRendererUtil.removeProperties(this.modelElement);
        final PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final CheckBoxFormFieldBuilder builder = new CheckBoxFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setConformance(this.<PdfConformance>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL));

        if (this.hasProperty(FormProperty.FORM_CHECKBOX_TYPE)) {
            builder.setCheckType((CheckBoxType) this.<CheckBoxType>getProperty(FormProperty.FORM_CHECKBOX_TYPE));
        }
        final PdfButtonFormField checkBox = builder.createCheckBox();
        checkBox.disableFieldRegeneration();
        BorderStyleUtil.applyBorderProperty(this, checkBox.getFirstFormAnnotation());
        final Background background = this.modelElement.<Background>getProperty(Property.BACKGROUND);
        if (background != null) {
            checkBox.getFirstFormAnnotation().setBackgroundColor(background.getColor());
        }
        checkBox.setValue(PdfFormAnnotation.ON_STATE_VALUE);
        if (!isBoxChecked()) {
            checkBox.setValue(PdfFormAnnotation.OFF_STATE_VALUE);
        }

        applyAccessibilityProperties(checkBox, doc);
        checkBox.getFirstFormAnnotation().setFormFieldElement((CheckBox) modelElement);
        checkBox.enableFieldRegeneration();

        PdfFormCreator.getAcroForm(doc, true).addField(checkBox, page);
        FormFieldRendererUtil.reapplyProperties(modelElement, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isLayoutBasedOnFlatRenderer() {
        return false;
    }

    /**
     * A flat renderer for the checkbox.
     */
    protected class FlatParagraphRenderer extends ParagraphRenderer {

        /**
         * Creates a new {@link FlatParagraphRenderer} instance.
         *
         * @param modelElement the model element
         */
        public FlatParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void drawChildren(DrawContext drawContext) {
            final Rectangle rectangle = this.getInnerAreaBBox().clone();
            PdfCanvas canvas = drawContext.getCanvas();
            boolean isTaggingEnabled = drawContext.isTaggingEnabled();
            if (isTaggingEnabled) {
                TagTreePointer tp = drawContext.getDocument().getTagStructureContext().getAutoTaggingPointer();
                canvas.openTag(tp.getTagReference());
            }
            createCheckBoxRenderStrategy().drawCheckBoxContent(drawContext, CheckBoxRenderer.this, rectangle);
            if (isTaggingEnabled) {
                canvas.closeTag();
            }

        }
    }

}



