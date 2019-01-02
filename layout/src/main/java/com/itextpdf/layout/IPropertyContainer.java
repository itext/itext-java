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
package com.itextpdf.layout;

import com.itextpdf.layout.property.Property;

/**
 * A generic Map-like interface that defines methods for storing and retrieving
 * objects by an enum key of the {@link Property} type.
 */
public interface IPropertyContainer {

    /**
     * Checks if this entity has the specified property. Compared to {@link #hasOwnProperty(int)},
     * this method can check parent's properties, styles, etc, depending on the origin of the instance
     * @param property the property to be checked
     * @return {@code true} if this instance has given property, {@code false} otherwise
     */
    boolean hasProperty(int property);

    /**
     * Checks if this entity has the specified property, i.e. if it was set to this very element earlier
     * @param property the property to be checked
     * @return {@code true} if this instance has given own property, {@code false} otherwise
     */
    boolean hasOwnProperty(int property);

    /**
     * Gets the property from this entity. Compared to {@link #getOwnProperty(int)},
     * this method can check parent's properties, styles, etc, depending on the origin of the instance
     * @param <T1> the return type associated with the property
     * @param property the property to be retrieved
     * @return the value of the given property. {@code null} will be returned if the property value was not found
     */
    <T1> T1 getProperty(int property);

    /**
     * Gets own property from this entity. The property must have been set earlier to this entity.
     * If the property is not found, {@code null} will be returned.
     * @param <T1> the return type associated with the property
     * @param property the property to be retrieved
     * @return the value of the given own property. {@code null} will be returned if the property value was not found
     */
    <T1> T1 getOwnProperty(int property);

    /**
     * Gets the default property from this entity.
     * @param <T1> the return type associated with the property
     * @param property the property to be retrieved
     * @return the default property value. If the default property is not defined, {@code null} will be returned
     */
    <T1> T1 getDefaultProperty(int property);

    /**
     * Sets a property for this entity.
     * @param property the property to be set
     * @param value the value of the property
     */
    void setProperty(int property, Object value);

    /**
     * Deletes the own property of this entity.
     * @param property the property to be deleted
     */
    void deleteOwnProperty(int property);
}
