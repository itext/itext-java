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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.styledxmlparser.exceptions.ReadingByteLimitException;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Implementation of the {@link InputStream} abstract class, which is used to restrict
 * reading bytes from input stream i.e. if more bytes are read than the readingByteLimit,
 * an {@link ReadingByteLimitException} exception will be thrown.
 * <p>
 * Note that the readingByteLimit is not taken into account in the {@link #skip(long)},
 * {@link #available()}, {@link #mark(int)} and {@link #reset()} methods.
 *
 * @deprecated In favor of {@link com.itextpdf.io.resolver.resource.LimitedInputStream}
 */
@Deprecated
class LimitedInputStream extends com.itextpdf.io.resolver.resource.LimitedInputStream {

    /**
     * Creates a new {@link LimitedInputStream} instance.
     *
     * @param inputStream      the input stream, the reading of bytes from which will be limited
     * @param readingByteLimit the reading byte limit, must not be less than zero
     */
    public LimitedInputStream(InputStream inputStream, long readingByteLimit) {
        super(inputStream, readingByteLimit);
    }

    /**
     * @return a supplier of the exception that will be thrown when the reading byte limit is violated
     */
    @Override
    protected Supplier<ITextException> getReadingByteLimitExceptionSupplier() {
        return () -> new ReadingByteLimitException();
    }
}
