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

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.layout.element.AbstractElement;

/**
 * Implementation of the {@link AbstractElement} class for form fields.
 *
 * @param <T> the generic type of the form field (e.g. input field, button, text area)
 */
public abstract class FormField<T extends IFormField> extends AbstractElement<T> implements IFormField {

    /** The id. */
    private final String id;

    /**
     * Instantiates a new {@link FormField} instance.
     *
     * @param id the id
     */
    FormField(String id) {
        if (id == null || id.contains(".")) {
            throw new IllegalArgumentException("id should not contain '.'");
        }
        this.id = id;
    }

    /* (non-Javadoc)
     * @see IFormField#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.ElementPropertyContainer#getDefaultProperty(int)
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case FormProperty.FORM_FIELD_FLATTEN:
                return (T1) (Object) true;
            case FormProperty.FORM_FIELD_VALUE:
                return (T1) (Object) "";
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Set the form field to be interactive and added into Acroform instead of drawing it on a page.
     *
     * @param interactive {@code true} if the form field element shall be added into Acroform, {@code false} otherwise.
     *                By default, the form field element is not interactive and drawn on a page.
     * @return this same {@link FormField} instance.
     */
    public FormField<T> setInteractive(boolean interactive) {
        setProperty(FormProperty.FORM_FIELD_FLATTEN, !interactive);
        return this;
    }
}
