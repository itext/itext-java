/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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



