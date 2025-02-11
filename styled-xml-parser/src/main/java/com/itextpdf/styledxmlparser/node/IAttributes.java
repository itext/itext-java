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
package com.itextpdf.styledxmlparser.node;

/**
 * Interface for a series of HTML attributes.
 */
public interface IAttributes extends Iterable<IAttribute> {

    /**
     * Gets the value of an attribute, given a key.
     *
     * @param key the key
     * @return the attribute
     */
    String getAttribute(String key);

    /**
     * Adds a key and a value of an attributes.
     *
     * @param key the key
     * @param value the value
     */
    void setAttribute(String key, String value);

    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes
     */
    int size();

}
