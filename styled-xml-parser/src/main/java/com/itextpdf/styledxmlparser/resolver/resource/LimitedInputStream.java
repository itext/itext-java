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

import com.itextpdf.styledxmlparser.exceptions.StyledXmlParserExceptionMessage;
import com.itextpdf.styledxmlparser.exceptions.ReadingByteLimitException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the {@link InputStream} abstract class, which is used to restrict
 * reading bytes from input stream i.e. if more bytes are read than the readingByteLimit,
 * an {@link ReadingByteLimitException} exception will be thrown.
 *
 * Note that the readingByteLimit is not taken into account in the {@link #skip(long)},
 * {@link #available()}, {@link #mark(int)} and {@link #reset()} methods.
 */
class LimitedInputStream extends InputStream {
    private boolean isStreamRead;
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
        this.isStreamRead = false;
        this.isLimitViolated = false;
        this.inputStream = inputStream;
        this.readingByteLimit = readingByteLimit;
    }

    @Override
    public int read() throws IOException {
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw new ReadingByteLimitException();
        }

        int nextByte = inputStream.read();
        readingByteLimit--;

        checkReadingByteLimit(nextByte);
        return nextByte;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw new ReadingByteLimitException();
        }

        int numberOfReadingBytes;
        if (b.length > readingByteLimit) {
            byte[] validArray;
            if (readingByteLimit == 0) {
                // Still need to test if end of stream is reached, so setting 1 byte to read
                validArray = new byte[1];
            } else {
                // Safe to cast to int, because count is int and greater
                validArray = new byte[(int) readingByteLimit];
            }
            numberOfReadingBytes = inputStream.read(validArray);
            if (numberOfReadingBytes != -1) {
                System.arraycopy(validArray, 0, b, 0, numberOfReadingBytes);
            }
        } else {
            numberOfReadingBytes = inputStream.read(b);
        }
        readingByteLimit -= numberOfReadingBytes;

        checkReadingByteLimit(numberOfReadingBytes);
        return numberOfReadingBytes;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw new ReadingByteLimitException();
        }

        if (len > readingByteLimit) {
            if (readingByteLimit == 0) {
                // Still need to test if end of stream is reached, so setting 1 byte to read
                len = 1;
            } else {
                // Safe to cast to int, because count is int and greater
                len = (int) readingByteLimit;
            }
        }
        int numberOfReadingBytes = inputStream.read(b, off, len);
        readingByteLimit -= numberOfReadingBytes;

        checkReadingByteLimit(numberOfReadingBytes);
        return numberOfReadingBytes;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        // The body of the method is empty, because markSupported method always returns false
    }

    @Override
    public synchronized void reset() {
        // The body of the method is empty, because markSupported method always returns false
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private void checkReadingByteLimit(int byteValue) throws ReadingByteLimitException {
        if (byteValue == -1) {
            isStreamRead = true;
        } else if (readingByteLimit < 0) {
            isLimitViolated = true;
            throw new ReadingByteLimitException();
        }
    }
}
