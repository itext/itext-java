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

import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The {@link AbstractTextFieldRenderer} implementation for text area fields.
 */
public class TextAreaRenderer extends AbstractTextFieldRenderer {

    /**
     * Creates a new {@link TextAreaRenderer} instance.
     *
     * @param modelElement the model element
     */
    public TextAreaRenderer(TextArea modelElement) {
        super(modelElement);
    }

    /**
     * Gets the number of columns.
     *
     * @return the cols value of the text area field
     */
    public int getCols() {
        Integer cols = this.getPropertyAsInteger(FormProperty.FORM_FIELD_COLS);
        if (cols != null && cols.intValue() > 0) {
            return (int) cols;
        }
        return (int) modelElement.<Integer>getDefaultProperty(FormProperty.FORM_FIELD_COLS);
    }

    /**
     * Gets the number of rows.
     *
     * @return the rows value of the text area field
     */
    public int getRows() {
        Integer rows = this.getPropertyAsInteger(FormProperty.FORM_FIELD_ROWS);
        if (rows != null && rows.intValue() > 0) {
            return (int) rows;
        }
        return (int) modelElement.<Integer>getDefaultProperty(FormProperty.FORM_FIELD_ROWS);
    }

    @Override
    protected Float getLastYLineRecursively() {
        if (occupiedArea != null && occupiedArea.getBBox() != null) {
            return occupiedArea.getBBox().getBottom();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.renderer.IRenderer#getNextRenderer()
     */
    @Override
    public IRenderer getNextRenderer() {
        return new TextAreaRenderer((TextArea) getModelElement());
    }

    /* (non-Javadoc)
     * @see com.itextpdf.html2pdf.attach.impl.layout.form.renderer.AbstractFormFieldRenderer#adjustFieldLayout()
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        List<LineRenderer> flatLines = ((ParagraphRenderer) flatRenderer).getLines();
        updatePdfFont((ParagraphRenderer) flatRenderer);
        Rectangle flatBBox = flatRenderer.getOccupiedArea().getBBox();
        if (!flatLines.isEmpty() && font != null) {
            cropContentLines(flatLines, flatBBox);
        } else {
            LoggerFactory.getLogger(getClass()).error(MessageFormatUtil.format(
                    FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE, "text area"));
            setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flatBBox.setHeight(0);
        }
        flatBBox.setWidth((float) retrieveWidth(layoutContext.getArea().getBBox().getWidth()));
    }

    /* (non-Javadoc)
     * @see AbstractFormFieldRenderer#createFlatRenderer()
     */
    @Override
    protected IRenderer createFlatRenderer() {
        return createParagraphRenderer(getDefaultValue());
    }

    @Override
    IRenderer createParagraphRenderer(String defaultValue) {
        if (defaultValue.isEmpty()) {
            if (null != ((TextArea) modelElement).getPlaceholder() && !((TextArea) modelElement).getPlaceholder().isEmpty()) {
                return ((TextArea) modelElement).getPlaceholder().createRendererSubTree();
            }
        }
        return super.createParagraphRenderer(defaultValue);
    }

    /* (non-Javadoc)
     * @see AbstractFormFieldRenderer#applyAcroField(com.itextpdf.layout.renderer.DrawContext)
     */
    @Override
    protected void applyAcroField(DrawContext drawContext) {
        font.setSubset(false);
        String value = getDefaultValue();
        String name = getModelId();
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TextAreaRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        PdfDocument doc = drawContext.getDocument();
        Rectangle area = flatRenderer.getOccupiedArea().getBBox().clone();
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final float fontSizeValue = fontSize.getValue();
        final PdfString defaultValue = new PdfString(getDefaultValue());

        final PdfFormField inputField = new TextFormFieldBuilder(doc, name).setWidgetRectangle(area).createText()
                .setValue(value);
        inputField.setFont(font).setFontSize(fontSizeValue);
        inputField.setFieldFlag(PdfFormField.FF_MULTILINE, true);
        inputField.setDefaultValue(defaultValue);
        applyDefaultFieldProperties(inputField);
        PdfAcroForm.getAcroForm(doc, true).addField(inputField, page);

        writeAcroFormFieldLangAttribute(doc);
    }

    @Override
    public <T1> T1 getProperty(int key) {
        if (key == Property.WIDTH) {
            T1 width = super.<T1>getProperty(Property.WIDTH);
            if (width == null) {
                UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
                if (!fontSize.isPointValue()) {
                    Logger logger = LoggerFactory.getLogger(TextAreaRenderer.class);
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                            Property.FONT_SIZE));
                }
                int cols = getCols();
                return (T1) (Object) UnitValue.createPointValue(updateHtmlColsSizeBasedWidth(fontSize.getValue() * (cols * 0.5f + 2) + 2));
            }
            return width;
        }
        return super.<T1>getProperty(key);
    }

    @Override
    protected boolean setMinMaxWidthBasedOnFixedWidth(MinMaxWidth minMaxWidth) {
        if (!hasAbsoluteUnitValue(Property.WIDTH)) {
            UnitValue width = this.<UnitValue>getProperty(Property.WIDTH);
            boolean restoreWidth = hasOwnProperty(Property.WIDTH);
            setProperty(Property.WIDTH, null);
            boolean result = super.setMinMaxWidthBasedOnFixedWidth(minMaxWidth);
            if (restoreWidth) {
                setProperty(Property.WIDTH, width);
            } else {
                deleteOwnProperty(Property.WIDTH);
            }
            return result;
        }
        return super.setMinMaxWidthBasedOnFixedWidth(minMaxWidth);
    }

    private void cropContentLines(List<LineRenderer> lines, Rectangle bBox) {
        Float height = retrieveHeight();
        Float minHeight = retrieveMinHeight();
        Float maxHeight = retrieveMaxHeight();
        int rowsAttribute = getRows();
        float rowsHeight = getHeightRowsBased(lines, bBox, rowsAttribute);
        if (height != null && (float) height > 0) {
            adjustNumberOfContentLines(lines, bBox, (float) height);
        } else if (minHeight != null && (float) minHeight > rowsHeight) {
            adjustNumberOfContentLines(lines, bBox, (float) minHeight);
        } else if (maxHeight != null && (float) maxHeight > 0 && (float) maxHeight < rowsHeight) {
            adjustNumberOfContentLines(lines, bBox, (float) maxHeight);
        } else {
            adjustNumberOfContentLines(lines, bBox, rowsAttribute);
        }
    }
}
