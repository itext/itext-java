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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.forms.form.renderer.checkboximpl.AbstractCheckBoxRendererFactory;
import com.itextpdf.forms.form.renderer.checkboximpl.CheckBoxHtmlRendererFactory;
import com.itextpdf.forms.form.renderer.checkboximpl.CheckBoxPdfARendererFactory;
import com.itextpdf.forms.form.renderer.checkboximpl.CheckBoxPdfRendererFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;


/**
 * The {@link AbstractOneLineTextFieldRenderer} implementation for checkboxes.
 */
public class CheckBoxRenderer extends AbstractFormFieldRenderer {


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
     * Creates a flat renderer for the checkbox.
     *
     * @return an IRenderer object for the flat renderer
     */
    @Override
    protected IRenderer createFlatRenderer() {
        return createCheckBoxRenderFactory().createFlatRenderer();
    }

    /**
     * Creates a CheckBoxRenderFactory for the checkbox based on the different rendering modes and PDFA mode.
     *
     * @return a CheckBoxRenderFactory object for the checkbox
     */
    public AbstractCheckBoxRendererFactory createCheckBoxRenderFactory() {
        // html rendering is pdfa compliant so we dont have to check if its pdfa
        if (getRenderingMode() == RenderingMode.HTML_MODE) {
            return new CheckBoxHtmlRendererFactory(this);
        }
        if (getRenderingMode() == RenderingMode.DEFAULT_LAYOUT_MODE && isPdfA()) {
            return new CheckBoxPdfARendererFactory(this);
        }
        return new CheckBoxPdfRendererFactory(this);

    }

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#adjustFieldLayout()
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        this.setProperty(Property.BACKGROUND, null);
    }

    /**
     * Defines whether the box is checked or not.
     *
     * @return the default value of the checkbox field
     */
    public boolean isBoxChecked() {
        return Boolean.TRUE.equals(this.<Boolean>getProperty(FormProperty.FORM_FIELD_CHECKED));
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        String name = getModelId();
        PdfDocument doc = drawContext.getDocument();
        Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final CheckBoxFormFieldBuilder builder = new CheckBoxFormFieldBuilder(doc, name)
                .setWidgetRectangle(area)
                .setConformanceLevel(this.<PdfAConformanceLevel>getProperty(FormProperty.FORM_CONFORMANCE_LEVEL));
        if (this.hasProperty(FormProperty.FORM_CHECKBOX_TYPE)) {
            builder.setCheckType((CheckBoxType) this.<CheckBoxType>getProperty(FormProperty.FORM_CHECKBOX_TYPE));
        }

        final PdfButtonFormField checkBox = builder.createCheckBox();
        checkBox.setValue(isBoxChecked() ? PdfFormAnnotation.ON_STATE_VALUE : PdfFormAnnotation.OFF_STATE_VALUE, true);
        PdfAcroForm.getAcroForm(doc, true).addField(checkBox, page);
        writeAcroFormFieldLangAttribute(doc);
    }

    @Override
    protected boolean isLayoutBasedOnFlatRenderer() {
        return false;
    }

}



