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
package com.itextpdf.forms.form.element;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.CheckBoxRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
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
     * Sets the rendering mode for the checkbox.
     *
     * @param renderingMode the rendering mode to set
     *
     * @return this checkbox instance
     */
    public CheckBox setRenderingMode(RenderingMode renderingMode) {
        if (renderingMode == null) {
            LOGGER.warn(MessageFormatUtil.format(
                    FormsLogMessageConstants.INVALID_VALUE_FALLBACK_TO_DEFAULT, "renderingMode", null));
            return this;
        }
        setProperty(Property.RENDERING_MODE, renderingMode);
        return this;
    }

    /**
     * Sets the PDF/A conformance level for the checkbox.
     *
     * @param conformanceLevel the PDF/A conformance level to set
     *
     * @return this checkbox instance
     */
    public CheckBox setPdfAConformanceLevel(PdfAConformanceLevel conformanceLevel) {
        setProperty(FormProperty.FORM_CONFORMANCE_LEVEL, conformanceLevel);
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

    /* (non-Javadoc)
     * @see com.itextpdf.layout.element.AbstractElement#makeNewRenderer()
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new CheckBoxRenderer(this);
    }

}
