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
import com.itextpdf.forms.util.DrawingUtil;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

/**
 * The {@link AbstractOneLineTextFieldRenderer} implementation for checkboxes.
 */
public class CheckBoxRenderer extends AbstractFormFieldRenderer {

    private static final Color DEFAULT_BORDER_COLOR = ColorConstants.DARK_GRAY;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.WHITE;
    private static final float DEFAULT_BORDER_WIDTH = 0.75f; // 1px
    private static final float DEFAULT_SIZE = 8.25f; // 11px

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

    @Override
    protected IRenderer createFlatRenderer() {
        Paragraph paragraph = new Paragraph()
                .setWidth(DEFAULT_SIZE)
                .setHeight(DEFAULT_SIZE)
                .setBorder(new SolidBorder(DEFAULT_BORDER_COLOR, DEFAULT_BORDER_WIDTH))
                .setBackgroundColor(DEFAULT_BACKGROUND_COLOR)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new FlatParagraphRenderer(paragraph);
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
        return null != this.<Object>getProperty(FormProperty.FORM_FIELD_CHECKED);
    }

    @Override
    protected void applyAcroField(DrawContext drawContext) {
        String name = getModelId();
        PdfDocument doc = drawContext.getDocument();
        Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(doc, name).setWidgetRectangle(area).createCheckBox();
        checkBox.setValue(isBoxChecked() ? "Yes" : "Off", true);
        PdfAcroForm.getAcroForm(doc, true).addField(checkBox, page);

        writeAcroFormFieldLangAttribute(doc);
    }

    @Override
    protected boolean isLayoutBasedOnFlatRenderer() {
        return false;
    }

    private class FlatParagraphRenderer extends ParagraphRenderer {

        public FlatParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }

        @Override
        public void drawChildren(DrawContext drawContext) {
            if (isBoxChecked()) {
                PdfCanvas canvas = drawContext.getCanvas();
                Rectangle rectangle = getInnerAreaBBox();
                canvas.saveState();
                canvas.setFillColor(ColorConstants.BLACK);
                DrawingUtil.drawPdfACheck(canvas, rectangle.getWidth(), rectangle.getHeight(),
                        rectangle.getLeft(), rectangle.getBottom());
                canvas.restoreState();
            }
        }
    }
}

