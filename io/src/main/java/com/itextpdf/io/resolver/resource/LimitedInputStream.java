/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.io.resolver.resource;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.exceptions.ReadingByteLimitException;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Implementation of the {@link InputStream} abstract class, which is used to restrict
 * reading bytes from input stream i.e. if more bytes are read than the readingByteLimit,
 * an {@link ReadingByteLimitException} exception will be thrown.
 * <p>
 * Note that the readingByteLimit is not taken into account in the {@link #skip(long)},
 * {@link #available()}, {@link #mark(int)} and {@link #reset()} methods.
 */
public class LimitedInputStream extends InputStream {
    private final InputStream inputStream;
    private boolean isStreamRead;
    private boolean isLimitViolated;
    private long readingByteLimit;

    /**
     * Creates a new {@link LimitedInputStream} instance.
     *
     * @param inputStream      the input stream, the reading of bytes from which will be limited
     * @param readingByteLimit the reading byte limit, must not be less than zero
     */
    public LimitedInputStream(InputStream inputStream, long readingByteLimit) {
        if (readingByteLimit < 0) {
            throw new IllegalArgumentException(IoExceptionMessageConstant.READING_BYTE_LIMIT_MUST_NOT_BE_LESS_ZERO);
        }
        this.isStreamRead = false;
        this.isLimitViolated = false;
        this.inputStream = inputStream;
        this.readingByteLimit = readingByteLimit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw getReadingByteLimitExceptionSupplier().get();
        }

        int nextByte = inputStream.read();
        readingByteLimit--;

        checkReadingByteLimit(nextByte);
        return nextByte;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b) throws IOException {
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw getReadingByteLimitExceptionSupplier().get();
        }

        int numberOfReadingBytes;
        if (b.length > readingByteLimit) {
            byte[] validArray = readingByteLimit == 0 ? new byte[1] : new byte[(int) readingByteLimit];
            // Still need to test if end of stream is reached, so setting 1 byte to read
            // Safe to cast to int, because count is int and greater
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


    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int workingLen = len;
        if (isStreamRead) {
            return -1;
        }
        if (isLimitViolated) {
            throw getReadingByteLimitExceptionSupplier().get();
        }

        if (workingLen > readingByteLimit) {
            // Still need to test if end of stream is reached, so setting 1 byte to read
            // Safe to cast to int, because count is int and greater
            workingLen = readingByteLimit == 0 ? 1 : (int) readingByteLimit;
        }
        int numberOfReadingBytes = inputStream.read(b, off, workingLen);
        readingByteLimit -= numberOfReadingBytes;

        checkReadingByteLimit(numberOfReadingBytes);
        return numberOfReadingBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * Returns a supplier of the exception that will be thrown when the reading byte limit is violated.
     *
     * @return a supplier of the exception
     */
    protected Supplier<ITextException> getReadingByteLimitExceptionSupplier() {
        return () -> new ReadingByteLimitException();
    }

    private void checkReadingByteLimit(int byteValue) throws ReadingByteLimitException {
        if (byteValue == -1) {
            isStreamRead = true;
            return;
        }
        if (readingByteLimit < 0) {
            isLimitViolated = true;
            throw getReadingByteLimitExceptionSupplier().get();
        }
    }
}
