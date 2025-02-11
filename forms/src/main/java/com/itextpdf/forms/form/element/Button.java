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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.FormDefaultAccessibilityProperties;
import com.itextpdf.forms.form.renderer.ButtonRenderer;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Extension of the {@link FormField} class representing a button in html.
 */
public class Button extends FormField<Button> {
    private static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.MIDDLE;
    private static final TextAlignment DEFAULT_TEXT_ALIGNMENT = TextAlignment.CENTER;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.LIGHT_GRAY;

    /**
     * Indicates if it's the button with only single line caption.
     */
    private boolean singleLine = false;

    /**
     * Creates a new {@link Button} instance.
     *
     * @param id the id
     */
    public Button(String id) {
        super(id);
        setTextAlignment(DEFAULT_TEXT_ALIGNMENT);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        // Draw the borders inside the element by default
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
    }

    /**
     * Adds any block element to the div's contents.
     *
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public Button add(IBlockElement element) {
        singleLine = false;
        childElements.add(element);
        return this;
    }

    /**
     * Adds an image to the div's contents.
     *
     * @param element an {@link Image}
     * @return this Element
     */
    public Button add(Image element) {
        singleLine = false;
        childElements.add(element);
        return this;
    }

    /**
     * Indicates if this element represents an input with type button in html.
     *
     * @return true if it's the button with only one line caption.
     */
    public boolean isSingleLine() {
        return singleLine;
    }

    /**
     * Sets passed string value to the single line button caption.
     * Value will be clipped if it is not fit into single line. For multiple line value
     * use {@link Button#setValue(String)}. Note that when adding other elements to the button
     * after this method is called, this added value can be multiline.
     *
     * @param value string value to be set as caption.
     *
     * @return this same {@link Button} instance.
     */
    public Button setSingleLineValue(String value) {
        setValue(value);
        setProperty(Property.KEEP_TOGETHER, Boolean.FALSE);
        singleLine = true;
        return this;
    }

    /**
     * Adds passed string value as paragraph to the button.
     * Value can be multiline if it is not fit into single line. For single line value
     * use {@link Button#setSingleLineValue(String)}. Note that the new value will replace all already added elements.
     *
     * @param value string value to be added into button.
     *
     * @return {@inheritDoc}
     */
    @Override
    public IFormField setValue(String value) {
        childElements.clear();
        Paragraph paragraph = new Paragraph(value)
                .setMargin(0)
                .setMultipliedLeading(1)
                .setVerticalAlignment(DEFAULT_VERTICAL_ALIGNMENT)
                .setTextAlignment(DEFAULT_TEXT_ALIGNMENT);

        if (this.<Object>getProperty(Property.FONT) != null) {
            paragraph.setProperty(Property.FONT, this.<Object>getProperty(Property.FONT));
        }
        if (this.<UnitValue>getProperty(Property.FONT_SIZE) != null) {
            paragraph.setFontSize(this.<UnitValue>getProperty(Property.FONT_SIZE).getValue());
        }
        return add(paragraph);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        if (property == Property.KEEP_TOGETHER) {
            return (T1) (Object) true;
        }
        return super.<T1>getDefaultProperty(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null){
            tagProperties = new FormDefaultAccessibilityProperties(
                    FormDefaultAccessibilityProperties.FORM_FIELD_PUSH_BUTTON);
        }
        return tagProperties;
    }


    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new ButtonRenderer(this);
    }
}
