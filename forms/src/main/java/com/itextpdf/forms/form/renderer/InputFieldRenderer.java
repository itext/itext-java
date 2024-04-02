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
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
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
 * The {@link AbstractOneLineTextFieldRenderer} implementation for input fields.
 */
public class InputFieldRenderer extends AbstractOneLineTextFieldRenderer {

    /**
     * Creates a new {@link InputFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    public InputFieldRenderer(InputField modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new InputFieldRenderer((InputField) modelElement);
    }

    /**
     * Gets the size of the input field.
     *
     * @return the input field size
     */
    public int getSize() {
        Integer size = this.getPropertyAsInteger(FormProperty.FORM_FIELD_SIZE);
        return size == null ? (int) modelElement.<Integer>getDefaultProperty(FormProperty.FORM_FIELD_SIZE) : (int) size;
    }

    /**
     * Checks if the input field is a password field.
     *
     * @return true, if the input field is a password field
     */
    public boolean isPassword() {
        Boolean password = getPropertyAsBoolean(FormProperty.FORM_FIELD_PASSWORD_FLAG);
        return password == null ? (boolean) modelElement.
                <Boolean>getDefaultProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG) : (boolean) password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    IRenderer createParagraphRenderer(String defaultValue) {
        if (defaultValue.isEmpty() && null != ((InputField) modelElement).getPlaceholder()
                && !((InputField) modelElement).getPlaceholder().isEmpty()) {
            return ((InputField) modelElement).getPlaceholder().createRendererSubTree();
        }

        IRenderer flatRenderer = super.createParagraphRenderer(defaultValue);
        flatRenderer.setProperty(Property.NO_SOFT_WRAP_INLINE, true);
        return flatRenderer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adjustFieldLayout(LayoutContext layoutContext) {
        List<LineRenderer> flatLines = ((ParagraphRenderer) flatRenderer).getLines();
        Rectangle flatBBox = flatRenderer.getOccupiedArea().getBBox();
        updatePdfFont((ParagraphRenderer) flatRenderer);
        if (flatLines.isEmpty() || font == null) {
            LoggerFactory.getLogger(getClass()).error(
                    MessageFormatUtil.format(
                            FormsLogMessageConstants.ERROR_WHILE_LAYOUT_OF_FORM_FIELD_WITH_TYPE,
                            "text input"));
            setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flatBBox.setY(flatBBox.getTop()).setHeight(0);
        } else {
            cropContentLines(flatLines, flatBBox);
        }
        flatBBox.setWidth((float) retrieveWidth(layoutContext.getArea().getBBox().getWidth()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRenderer createFlatRenderer() {
        String defaultValue = getDefaultValue();
        boolean flatten = isFlatten();
        boolean password = isPassword();
        if (flatten && password) {
            defaultValue = obfuscatePassword(defaultValue);
        }

        return createParagraphRenderer(defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void applyAcroField(DrawContext drawContext) {
        font.setSubset(false);
        boolean password = isPassword();
        final String value = password ? "" : getDefaultValue();
        String name = getModelId();
        UnitValue fontSize = (UnitValue) this.getPropertyAsUnitValue(Property.FONT_SIZE);
        if (!fontSize.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(InputFieldRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.FONT_SIZE));
        }
        final PdfDocument doc = drawContext.getDocument();
        final Rectangle area = this.getOccupiedArea().getBBox().clone();
        applyMargins(area, false);
        final Map<Integer, Object> margins = deleteMargins();
        final PdfPage page = doc.getPage(occupiedArea.getPageNumber());
        final float fontSizeValue = fontSize.getValue();

        // Some properties are set to the HtmlDocumentRenderer, which is root renderer for this ButtonRenderer, but
        // in forms logic root renderer is CanvasRenderer, and these properties will have default values. So
        // we get them from renderer and set these properties to model element, which will be passed to forms logic.
        modelElement.setProperty(Property.FONT_PROVIDER, this.<FontProvider>getProperty(Property.FONT_PROVIDER));
        modelElement.setProperty(Property.RENDERING_MODE, this.<RenderingMode>getProperty(Property.RENDERING_MODE));
        // Default html2pdf input field appearance differs from the default one for form fields.
        // That's why we got rid of several properties we set by default during InputField instance creation.
        modelElement.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        final PdfFormField inputField = new TextFormFieldBuilder(doc, name).setWidgetRectangle(area)
                .setFont(font)
                .setGenericConformanceLevel(getGenericConformanceLevel(doc))
                .createText();
        inputField.disableFieldRegeneration();
        inputField.setValue(value);
        inputField.setFontSize(fontSizeValue);
        if (password) {
            inputField.setFieldFlag(PdfFormField.FF_PASSWORD, true);
        } else {
            inputField.setDefaultValue(new PdfString(value));
        }
        final int rotation = ((InputField)modelElement).getRotation();
        if (rotation != 0) {
            inputField.getFirstFormAnnotation().setRotation(rotation);
        }
        applyDefaultFieldProperties(inputField);
        applyAccessibilityProperties(inputField,doc);
        inputField.getFirstFormAnnotation().setFormFieldElement((InputField) modelElement);
        inputField.enableFieldRegeneration();
        PdfFormCreator.getAcroForm(doc, true).addField(inputField, page);

        applyProperties(margins);
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
                    Logger logger = LoggerFactory.getLogger(InputFieldRenderer.class);
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                            Property.FONT_SIZE));
                }
                int size = getSize();
                return (T1) (Object) UnitValue.createPointValue(
                        updateHtmlColsSizeBasedWidth(fontSize.getValue() * (size * 0.5f + 2) + 2));
            }
            return width;
        }
        return super.<T1>getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setMinMaxWidthBasedOnFixedWidth(MinMaxWidth minMaxWidth) {
        boolean result = false;
        if (hasRelativeUnitValue(Property.WIDTH)) {
            UnitValue widthUV = this.<UnitValue>getProperty(Property.WIDTH);
            boolean restoreWidth = hasOwnProperty(Property.WIDTH);
            setProperty(Property.WIDTH, null);
            Float width = retrieveWidth(0);
            if (width != null) {
                // the field can be shrinked if necessary so only max width is set here
                minMaxWidth.setChildrenMaxWidth((float) width);
                result = true;
            }
            if (restoreWidth) {
                setProperty(Property.WIDTH, widthUV);
            } else {
                deleteOwnProperty(Property.WIDTH);
            }
        } else {
            result = super.setMinMaxWidthBasedOnFixedWidth(minMaxWidth);
        }
        return result;
    }

    /**
     * Obfuscates the content of a password input field.
     *
     * @param text the password
     * @return a string consisting of '*' characters.
     */
    private String obfuscatePassword(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            builder.append('*');
        }
        return builder.toString();
    }
}
