/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * This method is invoked while serialization
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        java.io.OutputStream tempOutputStream = outputStream;
        outputStream = null;
        out.defaultWriteObject();
        outputStream = tempOutputStream;
    }

}
