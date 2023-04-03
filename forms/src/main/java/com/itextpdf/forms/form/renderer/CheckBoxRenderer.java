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

import com.itextpdf.commons.utils.ExperimentalFeatures;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.renderer.checkboximpl.HtmlCheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.ICheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfACheckBoxRenderingStrategy;
import com.itextpdf.forms.form.renderer.checkboximpl.PdfCheckBoxRenderingStrategy;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;


/**
 * The {@link AbstractOneLineTextFieldRenderer} implementation for checkboxes.
 */
public class CheckBoxRenderer extends AbstractFormFieldRenderer {

    // 1px
    public static final float DEFAULT_BORDER_WIDTH = 0.75F;
    private static final Color DEFAULT_BORDER_COLOR = ColorConstants.DARK_GRAY;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.WHITE;
    // 11px
    private static final float DEFAULT_SIZE = 8.25F;

    /**
     * Creates a new {@link CheckBoxRenderer} instance.
     *
     * @param modelElement the model element
     */
    public CheckBoxRenderer(CheckBox modelElement) {
        super(modelElement);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.IRenderer#getNextRenderer()
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
     * Returns whether or not the checkbox is in PDF/A mode.
     *
     * @return true if the checkbox is in PDF/A mode, false otherwise
     */
    public boolean isPdfA() {
        return this.<PdfAConformanceLevel>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL) != null;
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
        if (getRenderingMode() == RenderingMode.HTML_MODE) {
            renderingStrategy = new HtmlCheckBoxRenderingStrategy();
        } else if (getRenderingMode() == RenderingMode.DEFAULT_LAYOUT_MODE && isPdfA()) {
            renderingStrategy = new PdfACheckBoxRenderingStrategy();
        } else {
            renderingStrategy = new PdfCheckBoxRenderingStrategy();
        }
        return renderingStrategy;
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        if (!ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            super.drawBackground(drawContext);
        }
        // draw background in child
    }

    @Override
    public void drawBorder(DrawContext drawContext) {
        if (!ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            super.drawBorder(drawContext);
        }
        //draw border in child
    }

    @Override
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        if (!ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            return super.applyBorderBox(rect, borders, reverse);
        }
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

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#adjustFieldLayout()
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        this.setProperty(Property.BACKGROUND, null);
    }

    /**
     * Creates a flat renderer for the checkbox.
     *
     * @return an IRenderer object for the flat renderer
     */
    @Override
    public IRenderer createFlatRenderer() {
        if (!ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            final Paragraph paragraph = new Paragraph().setWidth(DEFAULT_SIZE).setHeight(DEFAULT_SIZE)
                    .setBorder(new SolidBorder(DEFAULT_BORDER_COLOR, DEFAULT_BORDER_WIDTH))
                    .setBackgroundColor(DEFAULT_BACKGROUND_COLOR).setHorizontalAlignment(HorizontalAlignment.CENTER);
            return new FlatParagraphRenderer(paragraph);
        }
        final UnitValue heightUV = getPropertyAsUnitValue(Property.HEIGHT);
        final UnitValue widthUV = getPropertyAsUnitValue(Property.WIDTH);

        final float height = null == heightUV ? DEFAULT_SIZE : heightUV.getValue();
        final float width = null == widthUV ? DEFAULT_SIZE : widthUV.getValue();

        final Paragraph paragraph = new Paragraph().setWidth(width).setHeight(height).setMargin(0)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER);

        paragraph.setProperty(Property.BOX_SIZING, this.<BoxSizingPropertyValue>getProperty(Property.BOX_SIZING));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));
        paragraph.setBorder(this.<Border>getProperty(Property.BORDER));
        paragraph.setProperty(Property.BACKGROUND, this.<Background>getProperty(Property.BACKGROUND));

        //In html 2 pdf rendering the boxes height width ratio is always 1:1
        // with chrome taking the max of the two as the size of the box
        if (getRenderingMode() == RenderingMode.HTML_MODE) {
            paragraph.setWidth(Math.max(width, height));
            paragraph.setHeight(Math.max(width, height));
        }
        return new FlatParagraphRenderer(paragraph);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        final String name = getModelId();
        final PdfDocument doc = drawContext.getDocument();
        final Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();
        final PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final CheckBoxFormFieldBuilder builder = new CheckBoxFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setConformanceLevel(this.<PdfAConformanceLevel>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL));
        if (this.hasProperty(FormProperty.FORM_CHECKBOX_TYPE)) {
            builder.setCheckType((CheckBoxType) this.<CheckBoxType>getProperty(FormProperty.FORM_CHECKBOX_TYPE));
        }
        final PdfButtonFormField checkBox = builder.createCheckBox();
        if (ExperimentalFeatures.ENABLE_EXPERIMENTAL_CHECKBOX_RENDERING) {
            final Border border = this.<Border>getProperty(Property.BORDER);
            if (border != null) {
                checkBox.getFirstFormAnnotation().setBorderColor(border.getColor());
                checkBox.getFirstFormAnnotation().setBorderWidth(border.getWidth());
            }
            final Background background = this.modelElement.<Background>getProperty(Property.BACKGROUND);
            if (background != null) {
                checkBox.getFirstFormAnnotation().setBackgroundColor(background.getColor());
            }
        }

        checkBox.setValue(PdfFormAnnotation.ON_STATE_VALUE);
        if (!isBoxChecked()) {
            checkBox.setValue(PdfFormAnnotation.OFF_STATE_VALUE);
        }

        PdfAcroForm.getAcroForm(doc, true).addField(checkBox, page);
        writeAcroFormFieldLangAttribute(doc);
    }

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

        @Override
        public void drawChildren(DrawContext drawContext) {
            final Rectangle rectangle = this.getInnerAreaBBox().clone();
            createCheckBoxRenderStrategy().drawCheckBoxContent(drawContext, CheckBoxRenderer.this, rectangle);
        }
    }

}



