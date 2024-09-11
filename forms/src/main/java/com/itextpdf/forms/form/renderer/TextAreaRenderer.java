/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.AbstractPdfFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.util.FormFieldRendererUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new TextAreaRenderer((TextArea) getModelElement());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        UnitValue fontSize = getPropertyAsUnitValue(Property.FONT_SIZE);
        if (fontSize != null && fontSize.getValue() < EPS) {
            approximateFontSizeToFitMultiLine(layoutContext);
        }
        return super.layout(layoutContext);
    }

    /**
     * {@inheritDoc}
     */
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
                float fontSizeValue = fontSize.getValue();
                if (fontSizeValue < EPS) {
                    fontSizeValue = AbstractPdfFormField.DEFAULT_FONT_SIZE;
                }
                int cols = getCols();
                return (T1) (Object) UnitValue.createPointValue(
                        updateHtmlColsSizeBasedWidth(fontSizeValue * (cols * 0.5f + 2) + 2));
            }
            return width;
        }
        return super.<T1>getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        List<LineRenderer> flatLines = ((ParagraphRenderer) flatRenderer).getLines();
        updatePdfFont((ParagraphRenderer) flatRenderer);
        Rectangle flatBBox = flatRenderer.getOccupiedArea().getBBox();
        if (flatLines.isEmpty() || font == null) {
            LoggerFactory.getLogger(getClass()).error(MessageFormatUtil.format(
                    FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE, "text area"));
            setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flatBBox.setHeight(0);
        } else {
            if (!hasOwnOrModelProperty(FormProperty.FORM_FIELD_ROWS)) {
                setProperty(FormProperty.FORM_FIELD_ROWS, flatLines.size());
            }
            cropContentLines(flatLines, flatBBox);
        }
        flatBBox.setWidth((float) retrieveWidth(layoutContext.getArea().getBBox().getWidth()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRenderer createFlatRenderer() {
        return createParagraphRenderer(getDefaultValue());
    }

    /**
     * {@inheritDoc}
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
        Rectangle area = getOccupiedArea().getBBox().clone();
        applyMargins(area, false);
        final Map<Integer, Object> properties = FormFieldRendererUtil.removeProperties(modelElement);
        PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final float fontSizeValue = fontSize.getValue();
        final PdfString defaultValue = new PdfString(getDefaultValue());

        // Default html2pdf text area appearance differs from the default one for form fields.
        // That's why we got rid of several properties we set by default during TextArea instance creation.
        modelElement.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        final PdfFormField inputField = new TextFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setConformance(getConformance(doc))
                .setFont(font)
                .createMultilineText();
        inputField.disableFieldRegeneration();
        inputField.setValue(value);
        inputField.setFontSize(fontSizeValue);
        inputField.setDefaultValue(defaultValue);
        applyDefaultFieldProperties(inputField);
        inputField.getFirstFormAnnotation().setFormFieldElement((TextArea) modelElement);
        inputField.enableFieldRegeneration();
        applyAccessibilityProperties(inputField, doc);
        PdfFormCreator.getAcroForm(doc, true).addField(inputField, page);

        FormFieldRendererUtil.reapplyProperties(modelElement, properties);
    }

    /**
     * {@inheritDoc}
     */
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

    @Override
    IRenderer createParagraphRenderer(String defaultValue) {
        if (defaultValue.isEmpty() && null != ((TextArea) modelElement).getPlaceholder() && !((TextArea) modelElement)
                .getPlaceholder().isEmpty()) {
            return ((TextArea) modelElement).getPlaceholder().createRendererSubTree();
        }

        IRenderer flatRenderer = super.createParagraphRenderer(defaultValue);
        flatRenderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);
        return flatRenderer;
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

    private void approximateFontSizeToFitMultiLine(LayoutContext layoutContext) {
        float fontSize = approximateFontSize(layoutContext, AbstractPdfFormField.MIN_FONT_SIZE,
                AbstractPdfFormField.DEFAULT_FONT_SIZE);
        ((TextArea) modelElement).setFontSize(fontSize < 0 ? AbstractPdfFormField.DEFAULT_FONT_SIZE : fontSize);
    }
}
