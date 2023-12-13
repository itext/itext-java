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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A RandomAccessSource that is based on an underlying {@link java.nio.ByteBuffer}.  This class takes steps to ensure that the byte buffer
 * is completely freed from memory during {@link ByteBufferRandomAccessSource#close()}
 */
class ByteBufferRandomAccessSource implements IRandomAccessSource, Serializable {

    private static final long serialVersionUID = -1477190062876186034L;
    /**
     * Internal cache of memory mapped buffers
     */
    private transient java.nio.ByteBuffer byteBuffer;
    private byte[] bufferMirror;

    /**
     * Constructs a new {@link ByteBufferRandomAccessSource} based on the specified ByteBuffer
     *
     * @param byteBuffer the buffer to use as the backing store
     */
    public ByteBufferRandomAccessSource(java.nio.ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: Because ByteBuffers don't support long indexing, the position must be a valid positive int
     *
     * @param position the position to read the byte from - must be less than Integer.MAX_VALUE
     */
    public int get(long position) throws java.io.IOException {
        if (position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Position must be less than Integer.MAX_VALUE");
        try {

            if (position >= ((Buffer) byteBuffer).limit())
                return -1;
            byte b = byteBuffer.get((int) position);
            return b & 0xff;
        } catch (BufferUnderflowException e) {
            // EOF
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: Because ByteBuffers don't support long indexing, the position must be a valid positive int
     *
     * @param position the position to read the byte from - must be less than Integer.MAX_VALUE
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        if (position > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Position must be less than Integer.MAX_VALUE");

        if (position >= ((Buffer) byteBuffer).limit())
            return -1;

        // Not thread safe!
        ((Buffer) byteBuffer).position((int) position);
        int bytesFromThisBuffer = Math.min(len, byteBuffer.remaining());
        byteBuffer.get(bytes, off, bytesFromThisBuffer);

        return bytesFromThisBuffer;
    }


    /**
     * {@inheritDoc}
     */
    public long length() {
        return ((Buffer) byteBuffer).limit();
    }

    /**
     * @see java.io.RandomAccessFile#close()
     * Cleans the mapped bytebuffers and closes the channel
     */
    public void close() throws java.io.IOException {
        clean(byteBuffer);
    }


    /**
     * <code>true</code>, if this platform supports unmapping mmapped files.
     */
    public static final boolean UNMAP_SUPPORTED;

    /**
     * Reference to a BufferCleaner that does unmapping; {@code null} if not supported.
     */
    private static final BufferCleaner CLEANER;

    static {
        final Object hack = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                return BufferCleaner.unmapHackImpl();
            }
        });
        if (hack instanceof BufferCleaner) {
            CLEANER = (BufferCleaner) hack;
            UNMAP_SUPPORTED = true;
        } else {
            CLEANER = null;
            UNMAP_SUPPORTED = false;
        }
    }

    /**
     * invokes the clean method on the ByteBuffer's cleaner
     *
     * @param buffer ByteBuffer
     * @return boolean true on success
     */
    private static boolean clean(final java.nio.ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect())
            return false;

        Boolean b = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                Boolean success = Boolean.FALSE;
                try {
                    // java 9
                    if (UNMAP_SUPPORTED)
                        CLEANER.freeBuffer(buffer.toString(), buffer);
                    // java 8 and lower
                    else {
                        Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class<?>[]) null);
                        getCleanerMethod.setAccessible(true);
                        Object cleaner = getCleanerMethod.invoke(buffer, (Object[]) null);
                        Method clean = cleaner.getClass().getMethod("clean", (Class<?>[]) null);
                        clean.invoke(cleaner, (Object[]) null);
                    }
                    success = Boolean.TRUE;
                } catch (Exception e) {
                    // This really is a show stopper on windows
                    Logger logger = LoggerFactory.getLogger(ByteBufferRandomAccessSource.class);
                    logger.debug(e.getMessage());
                }
                return success;
            }
        });

        return b;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (byteBuffer != null && byteBuffer.hasArray()) {
            throw new NotSerializableException(byteBuffer.getClass().toString());
        } else if (byteBuffer != null) {
            bufferMirror = byteBuffer.array();
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (bufferMirror != null) {
            byteBuffer = java.nio.ByteBuffer.wrap(bufferMirror);
            bufferMirror = null;
        }
    }

}
