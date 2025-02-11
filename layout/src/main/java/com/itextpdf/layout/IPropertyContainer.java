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
package com.itextpdf.layout;

import com.itextpdf.layout.properties.Property;

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
