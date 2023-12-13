/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.IOException;

import java.io.Serializable;

public class OutputStream<T extends java.io.OutputStream> extends java.io.OutputStream implements Serializable {

    private static final long serialVersionUID = -5337390096148526418L;

    //long=19 + max frac=6 => 26 => round to 32.
    private final ByteBuffer numBuffer = new ByteBuffer(32);

    protected java.io.OutputStream outputStream = null;
    protected long currentPos = 0;
    protected boolean closeStream = true;

    public static boolean getHighPrecision() {
        return ByteUtils.HighPrecision;
    }

    public static void setHighPrecision(boolean value) {
        ByteUtils.HighPrecision = value;
    }

    public OutputStream(java.io.OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * Do not use this constructor. This is only for internal usage.
     */
    protected OutputStream() {
        super();
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

    public void writeByte(byte value) {
        try {
            write(value);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteByte, e);
        }
    }

    @Override
    public void flush() throws java.io.IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws java.io.IOException {
        if (closeStream)
            outputStream.close();
    }

    public T writeLong(long value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteIntNumber, e);
        }
    }

    public T writeInteger(int value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteIntNumber, e);
        }
    }

    public T writeFloat(float value) {
        return writeFloat(value, ByteUtils.HighPrecision);
    }

    public T writeFloat(float value, boolean highPrecision) {
        return writeDouble(value, highPrecision);
    }

    public T writeFloats(float[] value) {
        for (int i = 0; i < value.length; i++) {
            writeFloat(value[i]);
            if (i < value.length - 1)
                writeSpace();
        }
        return (T) this;
    }

    public T writeDouble(double value) {
        return writeDouble(value, ByteUtils.HighPrecision);
    }

    public T writeDouble(double value, boolean highPrecision) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset(), highPrecision);
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteFloatNumber, e);
        }
    }

    public T writeByte(int value) {
        try {
            write(value);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteByte, e);
        }
    }

    public T writeSpace() {
        return writeByte(' ');
    }

    public T writeNewLine() {
        return writeByte('\n');
    }

    public T writeString(String value) {
        return writeBytes(ByteUtils.getIsoBytes(value));
    }

    public T writeBytes(byte[] b) {
        try {
            write(b);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteBytes, e);
        }
    }

    public T writeBytes(byte[] b, int off, int len) {
        try {
            write(b, off, len);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteBytes, e);
        }
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    public void assignBytes(byte[] bytes, int count) {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).assignBytes(bytes, count);
            currentPos = count;
        } else
            throw new IOException(IOException.BytesCanBeAssignedToByteArrayOutputStreamOnly);
    }

    public void reset() {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).reset();
            currentPos = 0;
        } else
            throw new IOException(IOException.BytesCanBeResetInByteArrayOutputStreamOnly);
    }

    /**
     * This method is invoked while deserialization
     *
     * @param in given ObjectInputStream
     * @throws java.io.IOException occurs in cases of producing failed or interrupted I/O operations
     * @throws java.lang.ClassNotFoundException thrown when an application could not find the desired class
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * This method is invoked while serialization
     *
     * @param out given ObjectOutputStream
     * @throws java.io.IOException occurs in cases of producing failed or interrupted I/O operations
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        java.io.OutputStream tempOutputStream = outputStream;
        outputStream = null;
        out.defaultWriteObject();
        outputStream = tempOutputStream;
    }

}
