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
 * Represents f element, child of the xfdf element.
 * Corresponds to the F key in the file dictionary.
 * Specifies the source file or target file: the PDF document that this XFDF file was exported from or is intended to be
 * imported into.
 * Attributes: href.
 * For more details see paragraph 6.2.2 in Xfdf document specification.
 */
public class FObject {

    /**
     * The name of the source or target file.
     */
    private String href;

    /**
     * Creates an instance of {@link FObject}.
     *
     * @param href the name of the source or target file
     */
    public FObject(String href) {
        this.href = href;
    }

    /**
     * Gets the name of the source or target file.
     *
     * @return the name of the source or target file.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the name of the source or target file.
     *
     * @param href the name of the source or target file
     *
     * @return current {@link FObject f object}.
     */
    public FObject setHref(String href) {
        this.href = href;
        return this;
    }
}
