/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.styledxmlparser.StyledXmlParserExceptionMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the {@link InputStream} abstract class, which is used to restrict
 * reading bytes from input stream i.e. if more bytes are read than the readingByteLimit,
 * an {@link ReadingByteLimitException} exception will be thrown.
 */
class LimitedInputStream extends InputStream {
    private boolean isLimitViolated;
    private long readingByteLimit;
    private InputStream inputStream;

    /**
     * Creates a new {@link LimitedInputStream} instance.
     *
     * @param inputStream the input stream, the reading of bytes from which will be limited
     * @param readingByteLimit the reading byte limit, must not be less than zero
     */
    public LimitedInputStream(InputStream inputStream, long readingByteLimit) {
        if (readingByteLimit < 0) {
            throw new IllegalArgumentException(StyledXmlParserExceptionMessage.READING_BYTE_LIMIT_MUST_NOT_BE_LESS_ZERO);
        }
        this.isLimitViolated = false;
        this.inputStream = inputStream;
        this.readingByteLimit = readingByteLimit;
    }

    @Override
    public int read() throws IOException {
        if (isLimitViolated) {
            throw new ReadingByteLimitException();
        }

        readingByteLimit--;
        int nextByte = inputStream.read();

        if (nextByte != -1 && readingByteLimit < 0) {
            isLimitViolated = true;
            throw new ReadingByteLimitException();
        }
        return nextByte;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
