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
package com.itextpdf.kernel.exceptions;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

/**
 * Exception class for exceptions occurred during decompressed pdf streams processing.
 */
public class MemoryLimitsAwareException extends PdfException {
    /**
     * Creates a new instance of MemoryLimitsAwareException.
     *
     * @param message the detail message.
     */
    public MemoryLimitsAwareException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of MemoryLimitsAwareException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public MemoryLimitsAwareException(Throwable cause) {
        this(KernelExceptionMessageConstant.UNKNOWN_PDF_EXCEPTION, cause);
    }

    /**
     * Creates a new instance of MemoryLimitsAwareException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public MemoryLimitsAwareException(String message, Throwable cause) {
        super(message, cause);
    }
}
