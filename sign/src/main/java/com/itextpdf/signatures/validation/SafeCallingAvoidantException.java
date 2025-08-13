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
package com.itextpdf.signatures.validation;

import com.itextpdf.kernel.exceptions.PdfException;

/**
 * In some cases we need to propagate the exception without @{link SafeCalling} mechanism converting it to
 * report items.
 * This exception is used to indicate that something actually went wrong and not only the validation report is Invalid,
 * but an underlying process might be affected.
 */
public class SafeCallingAvoidantException extends PdfException {

    /**
     * Creates a new instance of {@link SafeCallingAvoidantException} with the specified detail message.
     *
     * @param message the detail message
     */
    public SafeCallingAvoidantException(String message) {
        super(message);
    }


    /**
     * Creates a new instance of {@link SafeCallingAvoidantException} with the specified detail message
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public SafeCallingAvoidantException(String message, Object obj) {
        this(message);
        this.object = obj;
    }

}
