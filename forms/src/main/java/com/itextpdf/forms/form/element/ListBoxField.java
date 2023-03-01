/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.forms.form.renderer.SelectFieldListBoxRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A field that represents a control for selecting one or several of the provided options.
 */
public class ListBoxField extends AbstractSelectField {

    /**
     * Creates a new list box field.
     *
     * @param size the size of the list box, which will define the height of visible properties,
     *             shall be greater than zero
     * @param allowMultipleSelection a boolean flag that defines whether multiple options are allowed
     *                              to be selected at once
     * @param id the id
     */
    public ListBoxField(String id, int size, boolean allowMultipleSelection) {
        super(id);
        setProperty(FormProperty.FORM_FIELD_SIZE, size);
        setProperty(FormProperty.FORM_FIELD_MULTIPLE, allowMultipleSelection);
    }

    /* (non-Javadoc)
     * @see FormField#getDefaultProperty(int)
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case FormProperty.FORM_FIELD_MULTIPLE:
                return (T1) (Object) false;
            case FormProperty.FORM_FIELD_SIZE:
                return (T1) (Object) 4;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new SelectFieldListBoxRenderer(this);
    }
}
