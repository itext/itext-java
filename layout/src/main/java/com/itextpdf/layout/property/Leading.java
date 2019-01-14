/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.layout.property;

import com.itextpdf.layout.IPropertyContainer;

import java.util.Objects;

/**
 * A specialized class that specifies the leading, "the vertical distance between
 * the baselines of adjacent lines of text" (ISO-32000-1, section 9.3.5).
 * Allows to use either an absolute (constant) leading value, or one
 * determined by font size. Pronounce as 'ledding' (cfr. Led Zeppelin).
 *
 * This class is meant to be used as the value for the
 * {@link Property#LEADING} key in an {@link IPropertyContainer}.
 */
public class Leading {
    /**
     * A leading type independent of font size.
     */
    public static final int FIXED = 1;

    /**
     * A leading type related to the font size and the resulting bounding box.
     */
    public static final int MULTIPLIED = 2;

    protected int type;
    protected float value;

    /**
     * Creates a Leading object.
     *
     * @param type a constant type that defines the calculation of actual
     * leading distance. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     * @param value to be used as a basis for the leading calculation.
     */
    public Leading(int type, float value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the calculation type of the Leading object.
     *
     * @return the calculation type. Either {@link Leading#FIXED} or {@link Leading#MULTIPLIED}
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the value to be used as the basis for the leading calculation.
     * @return a calculation value
     */
    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && type == ((Leading) obj).type && value == ((Leading) obj).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
