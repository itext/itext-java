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
package com.itextpdf.svg.exceptions;

import com.itextpdf.kernel.exceptions.PdfException;

/**
 * Exception thrown by {@link com.itextpdf.svg.processors.ISvgProcessor} when it cannot process an SVG
 */
public class SvgProcessingException extends PdfException {

    /**
     * Creates a new SvgProcessingException instance.
     *
     * @param message the message
     */
    public SvgProcessingException(String message) {
        super(message);
    }
    
    /**
     * Creates a new SvgProcessingException instance.
     *
     * @param message the message
     * @param cause the nested exception
     */
    public SvgProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
