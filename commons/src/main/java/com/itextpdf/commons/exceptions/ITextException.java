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
package com.itextpdf.commons.exceptions;

/**
 * General iText exception.
 *
 * <p>
 * Important note, not all iText exceptions are extended from ITextException.
 */
public class ITextException extends RuntimeException {
    /**
     * Creates a new ITextException with no error message and cause.
     */
    public ITextException() {
        super(CommonsExceptionMessageConstant.UNKNOWN_ITEXT_EXCEPTION);
    }

    /**
     * Creates a new ITextException.
     *
     * @param message the detail message
     */
    public ITextException(String message) {
        super(message);
    }

    /**
     * Creates a new ITextException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method)
     */
    public ITextException(Throwable cause) {
        super(CommonsExceptionMessageConstant.UNKNOWN_ITEXT_EXCEPTION, cause);
    }

    /**
     * Creates a new ITextException.
     *
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method)
     */
    public ITextException(String message, Throwable cause) { super(message, cause); }
}
