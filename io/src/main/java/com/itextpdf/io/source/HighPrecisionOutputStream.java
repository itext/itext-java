/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

/**
 * Output stream based on {@link java.io.OutputStream} for which it is possible to set high precision in order to write
 * all floats and doubles with high precision.
 *
 * @param <T> {@link java.io.OutputStream}
 */
public class HighPrecisionOutputStream<T extends java.io.OutputStream> extends java.io.OutputStream {

    // long=19 + max frac=6 => 26 => round to 32.
    private final ByteBuffer numBuffer = new ByteBuffer(32);
    private Boolean localHighPrecision;
    protected java.io.OutputStream outputStream = null;
    protected long currentPos = 0;
    protected boolean closeStream = true;

    /**
     * Gets global high precision setting.
     *
     * @return global high precision setting.
     */
    public static boolean getHighPrecision() {
        return ByteUtils.HighPrecision;
    }

    /**
     * Sets global high precision setting for all {@link HighPrecisionOutputStream} instances.
     *
     * @param value if true, all floats and double will be written with high precision
     *              in all {@link HighPrecisionOutputStream} instances.
     */
    public static void setHighPrecision(boolean value) {
        ByteUtils.HighPrecision = value;
    }

    /**
     * Gets local high precision setting.
     *
     * @return local high precision setting.
     */
    public boolean getLocalHighPrecision() {
        return this.localHighPrecision;
    }

    /**
     * Sets local high precision setting for the {@link HighPrecisionOutputStream}.
     * Global {@link ByteUtils#HighPrecision} setting will be overridden by this one.
     *
     * @param value if true, all floats and double will be written with high precision
     *              in the underlying {@link HighPrecisionOutputStream}.
     */
    public void setLocalHighPrecision(boolean value) {
        this.localHighPrecision = value;
    }

    /**
     * Creates a new {@link HighPrecisionOutputStream} instance
     * based on {@link java.io.OutputStream} instance.
     *
     * @param outputStream the {@link HighPrecisionOutputStream} instance.
     */
    public HighPrecisionOutputStream(java.io.OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * Creates a new {@link HighPrecisionOutputStream} instance
     * based on {@link java.io.OutputStream} instance and precision setting value.
     *
     * @param outputStream       the {@link java.io.OutputStream} instance.
     * @param localHighPrecision If true, all float and double values
     *                           will be written with high precision.
     *                           Global {@link ByteUtils#HighPrecision} setting
     *                           will be overridden by this one.
     */
    public HighPrecisionOutputStream(java.io.OutputStream outputStream, boolean localHighPrecision) {
        super();
        this.outputStream = outputStream;
        this.localHighPrecision = localHighPrecision;
    }

    @Override
    public void write(int b) throws java.io.IOException {
        outputStream.write(b);
        currentPos++;
    }

    @Override
    public void write(byte[] b) throws java.io.IOException {
        outputStream.write(b);
        currentPos += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws java.io.IOException {
        outputStream.write(b, off, len);
        currentPos += len;
    }

    /**
     * See {@link java.io.OutputStream#write(int)}.
     *
     * @param value byte to write.
     *
     * @throws IOException if {@link java.io.IOException} occurs.
     */
    public void writeByte(byte value) {
        try {
            write(value);
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_BYTE, e);
        }
    }

    @Override
    public void flush() throws java.io.IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws java.io.IOException {
        if (closeStream) {
            outputStream.close();
        }
    }

    /**
     * Writes long to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value value to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeLong(long value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_INT_NUMBER, e);
        }
    }

    /**
     * Writes int to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value value to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeInteger(int value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_INT_NUMBER, e);
        }
    }

    /**
     * Writes float to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value value to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeFloat(float value) {
        return writeFloat(value, localHighPrecision == null ? ByteUtils.HighPrecision : localHighPrecision);
    }

    /**
     * Writes float to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value         value to write.
     * @param highPrecision If true, float value will be written with high precision.
     *
     * @return this stream as passed generic stream.
     */
    public T writeFloat(float value, boolean highPrecision) {
        return writeDouble(value, highPrecision);
    }

    /**
     * Writes float array to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value float array to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeFloats(float[] value) {
        for (int i = 0; i < value.length; i++) {
            writeFloat(value[i]);
            if (i < value.length - 1) {
                writeSpace();
            }
        }
        return (T) this;
    }

    /**
     * Writes double to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value value to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeDouble(double value) {
        return writeDouble(value, localHighPrecision == null ? ByteUtils.HighPrecision : localHighPrecision);
    }

    /**
     * Writes double to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value         value to write.
     * @param highPrecision If true, double value will be written with high precision.
     *
     * @return this stream as passed generic stream.
     */
    public T writeDouble(double value, boolean highPrecision) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset(), highPrecision);
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_FLOAT_NUMBER, e);
        }
    }

    /**
     * Writes byte to internal {@link java.io.OutputStream}.
     *
     * @param value value to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeByte(int value) {
        try {
            write(value);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_BYTE, e);
        }
    }

    /**
     * Writes space to internal {@link java.io.OutputStream}.
     *
     * @return this stream as passed generic stream.
     */
    public T writeSpace() {
        return writeByte(' ');
    }

    /**
     * Writes new line to internal {@link java.io.OutputStream}.
     *
     * @return this stream as passed generic stream.
     */
    public T writeNewLine() {
        return writeByte('\n');
    }

    /**
     * Writes {@code String} to internal {@link java.io.OutputStream} in ISO format.
     *
     * @param value string to write.
     *
     * @return this stream as passed generic stream.
     */
    public T writeString(String value) {
        return writeBytes(ByteUtils.getIsoBytes(value));
    }

    /**
     * See {@link HighPrecisionOutputStream#write(byte[])}.
     *
     * @param b byte array to write.
     *
     * @return this stream as passed generic stream.
     *
     * @throws com.itextpdf.io.exceptions.IOException if {@link java.io.IOException} is thrown.
     */
    public T writeBytes(byte[] b) {
        try {
            write(b);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_BYTES, e);
        }
    }

    /**
     * See {@link HighPrecisionOutputStream#write(byte[], int, int)}.
     *
     * @param      b     the data to write.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     *
     * @return this stream as passed generic stream.
     *
     * @throws com.itextpdf.io.exceptions.IOException if {@link java.io.IOException} is thrown.
     */
    public T writeBytes(byte[] b, int off, int len) {
        try {
            write(b, off, len);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.CANNOT_WRITE_BYTES, e);
        }
    }

    /**
     * Gets current output stream position.
     *
     * @return current output stream position.
     */
    public long getCurrentPos() {
        return currentPos;
    }

    /**
     * Gets internal {@link java.io.OutputStream}.
     *
     * @return internal {@link java.io.OutputStream}.
     */
    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Returns true, if internal {@link java.io.OutputStream} have to be closed after {@link #close()} call,
     * false otherwise.
     *
     * @return true if stream needs to be closed, false if it's done manually.
     */
    public boolean isCloseStream() {
        return closeStream;
    }

    /**
     * Sets internal {@link java.io.OutputStream} to be closed after {@link HighPrecisionOutputStream#close()}.
     *
     * @param closeStream true if stream needs to be closed, false if it's done manually.
     */
    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    /**
     * See {@link ByteArrayOutputStream#assignBytes(byte[], int)}.
     *
     * @param bytes bytes to assign.
     * @param count number of bytes to assign.
     */
    public void assignBytes(byte[] bytes, int count) {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).assignBytes(bytes, count);
            currentPos = count;
        } else {
            throw new IOException(IoExceptionMessageConstant.BYTES_CAN_BE_ASSIGNED_TO_BYTE_ARRAY_OUTPUT_STREAM_ONLY);
        }
    }

    /**
     * See {@link ByteArrayOutputStream#reset()}.
     *
     * @throws com.itextpdf.io.exceptions.IOException if internal {@link HighPrecisionOutputStream} is not
     * a {@link ByteArrayOutputStream} instance.
     */
    public void reset() {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).reset();
            currentPos = 0;
        } else {
            throw new IOException(IoExceptionMessageConstant.BYTES_CAN_BE_RESET_IN_BYTE_ARRAY_OUTPUT_STREAM_ONLY);
        }
    }
}
