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
package com.itextpdf.forms.xfdf;

/**
 * Represents ids element, child of the xfdf element.
 * Corresponds to the ID key in the file dictionary.
 * The two attributes are file identifiers for the source or target file designated by the f element, taken
 * from the ID entry in the file’s trailer dictionary.
 * Attributes: original, modified.
 * For more details see paragraph 6.2.3 in Xfdf document specification.
 */
public class IdsObject {

    /**
     * This attribute corresponds to the permanent identifier which
     * is based on the contents of the file at the time it was originally created.
     * This value does not change when the file is incrementally updated.
     * The value shall be a hexadecimal number.
     * A common value for this is an MD5 checksum.
     */
    private String original;

    /**
     * The attribute contains a unique identifier for the
     * modified version of the pdf and corresponding xfdf document. The
     * modified attribute corresponds to the changing identifier that is based
     * on the file's contents at the time it was last updated.
     * The value shall be a hexadecimal number.
     * A common value for this is an MD5 checksum.
     */
    private String modified;

    /**
     * Creates an instance of {@link IdsObject}.
     */
    public IdsObject() {
    }

    /**
     * Gets the string value of the permanent identifier which
     * is based on the contents of the file at the time it was originally created.
     * This value does not change when the file is incrementally updated.
     * The value shall be a hexadecimal number.
     *
     * @return the permanent identifier value.
     */
    public String getOriginal() {
        return original;
    }

    /**
     * Sets the string value of the permanent identifier which
     * is based on the contents of the file at the time it was originally created.
     * This value does not change when the file is incrementally updated.
     * The value shall be a hexadecimal number.
     * A common value for this is an MD5 checksum.
     *
     * @param original the permanent identifier value
     *
     * @return current {@link IdsObject ids object}.
     */
    public IdsObject setOriginal(String original) {
        this.original = original;
        return this;
    }

    /**
     * Gets the string value of the unique identifier for the
     * modified version of the pdf and corresponding xfdf document. The
     * modified attribute corresponds to the changing identifier that is based
     * on the file's contents at the time it was last updated.
     * The value shall be a hexadecimal number.
     *
     * @return the unique identifier value.
     */
    public String getModified() {
        return modified;
    }

    /**
     * Sets the string value of the unique identifier for the
     * modified version of the pdf and corresponding xfdf document. The
     * modified attribute corresponds to the changing identifier that is based
     * on the file's contents at the time it was last updated.
     * The value shall be a hexadecimal number.
     * A common value for this is an MD5 checksum.
     *
     * @param modified the unique identifier value
     *
     * @return current {@link IdsObject ids object}.
     */
    public IdsObject setModified(String modified) {
        this.modified = modified;
        return this;
    }
}
