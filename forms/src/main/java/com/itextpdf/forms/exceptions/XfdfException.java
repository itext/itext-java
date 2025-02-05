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
package com.itextpdf.forms.exceptions;

import com.itextpdf.commons.exceptions.ITextException;

/**
 * This class represents iText exception that should be thrown when some errors occur while working with
 * XFDF objects (XFDF file is XML-based Acrobat Forms Data Format).
 */
public class XfdfException extends ITextException {

    /**
     * The exception thrown when some errors occur while working with XFDF objects.
     *
     * @param message exception message.
     */
    public XfdfException(String message) {
        super(message);
    }

    /**
     * Message in case one tries to add attribute with null name or value.
     */
    public static final String ATTRIBUTE_NAME_OR_VALUE_MISSING = "Attribute name or value are missing";

    /**
     * Message in case one tries to add annotation without indicating the page it belongs to.
     */
    public static final String PAGE_IS_MISSING = "Required Page attribute is missing.";
}

