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
package com.itextpdf.styledxmlparser.jsoup;

/**
 * A SerializationException is raised whenever serialization of a DOM element fails. This exception usually wraps an
 * {@link java.io.IOException} that may be thrown due to an inaccessible output stream.
 */
public final class SerializationException extends RuntimeException {
	/**
	 * Creates and initializes a new serialization exception with no error message and cause.
	 */
	public SerializationException() {
		super();
	}

	/**
	 * Creates and initializes a new serialization exception with the given error message and no cause.
	 * 
	 * @param message
	 *            the error message of the new serialization exception (may be <code>null</code>).
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 * Creates and initializes a new serialization exception with the specified cause and an error message of
     * <code>(cause==null ? null : cause.toString())</code> (which typically contains the class and error message of
     * <code>cause</code>).
	 * 
	 * @param cause
	 *            the cause of the new serialization exception (may be <code>null</code>).
	 */
	public SerializationException(Throwable cause) {
		super(cause == null ? "Exception with null cause" : cause.getMessage(), cause);
	}

	/**
	 * Creates and initializes a new serialization exception with the given error message and cause.
	 * 
	 * @param message
	 *            the error message of the new serialization exception.
	 * @param cause
	 *            the cause of the new serialization exception.
	 */
	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
