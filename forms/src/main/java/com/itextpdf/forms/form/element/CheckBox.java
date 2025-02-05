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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.FormDefaultAccessibilityProperties;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of the {@link FormField} class representing a checkbox so that
 * a {@link CheckBoxRenderer} is used instead of the default renderer for fields.
 */
public class CheckBox extends FormField<CheckBox> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckBox.class);

    /**
     * Creates a new {@link CheckBox} instance.
     *
     * @param id the id
     */
    public CheckBox(String id) {
        super(id);
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        setChecked(false);
    }

    /**
     * Sets the checked state of the checkbox.
     *
     * @param checked the checked state to set
     *
     * @return this checkbox instance
     */
    public CheckBox setChecked(boolean checked) {
        setProperty(FormProperty.FORM_FIELD_CHECKED, checked);
        return this;
    }

    /**
     * Sets the conformance for the checkbox.
     *
     * @param conformance The PDF conformance to set.
     *
     * @return this checkbox instance
     */
    public CheckBox setPdfConformance(PdfConformance conformance) {
        setProperty(FormProperty.FORM_CONFORMANCE_LEVEL, conformance);
        return this;
    }

    /**
     * Sets the icon of the checkbox.
     *
     * @param checkBoxType the type of the checkbox to set
     *
     * @return this checkbox instance
     */
    public CheckBox setCheckBoxType(CheckBoxType checkBoxType) {
        if (checkBoxType == null) {
            LOGGER.warn(MessageFormatUtil.format(
                    FormsLogMessageConstants.INVALID_VALUE_FALLBACK_TO_DEFAULT, "checkBoxType", null));
            return this;
        }
        setProperty(FormProperty.FORM_CHECKBOX_TYPE, checkBoxType);
        return this;
    }

    /**
     * Sets the size of the checkbox.
     *
     * @param size the size of the checkbox to set, in points
     *
     * @return this checkbox instance
     */
    public CheckBox setSize(float size) {
        if (size <= 0) {
            LOGGER.warn(MessageFormatUtil.format(
                    FormsLogMessageConstants.INVALID_VALUE_FALLBACK_TO_DEFAULT, "size", size));
            return this;
        }
        setProperty(Property.WIDTH, UnitValue.createPointValue(size));
        setProperty(Property.HEIGHT, UnitValue.createPointValue(size));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null){
            tagProperties = new FormDefaultAccessibilityProperties(FormDefaultAccessibilityProperties.FORM_FIELD_CHECK);
        }
        if (tagProperties instanceof FormDefaultAccessibilityProperties){
            ((FormDefaultAccessibilityProperties)tagProperties).updateCheckedValue(this);
        }

        return tagProperties;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.element.AbstractElement#makeNewRenderer()
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new CheckBoxRenderer(this);
    }

}
