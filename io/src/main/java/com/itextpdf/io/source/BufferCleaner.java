/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE: that this code was edited since original code is compatible with android sdk not lower than 26.
 * This edited code has been verified to be compatible with android sdk 19.
 */

package com.itextpdf.io.source;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;


class BufferCleaner {
    Class<?> unmappableBufferClass;
    final Method method;
    final Object theUnsafe;

    BufferCleaner(final Class<?> unmappableBufferClass, final Method method, final Object theUnsafe) {
        this.unmappableBufferClass = unmappableBufferClass;
        this.method = method;
        this.theUnsafe = theUnsafe;
    }

    void freeBuffer(String resourceDescription, final java.nio.ByteBuffer buffer) throws IOException {
        assert Objects.equals(void.class, method.getReturnType());
        assert method.getParameterTypes().length == 1;
        assert Objects.equals(ByteBuffer.class, method.getParameterTypes()[0]);
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("unmapping only works with direct buffers");
        }
        if (!unmappableBufferClass.isInstance(buffer)) {
            throw new IllegalArgumentException("buffer is not an instance of " + unmappableBufferClass.getName());
        }
        final Throwable error = AccessController.doPrivileged(new PrivilegedAction<Throwable>() {
            public Throwable run() {
                try {
                    method.invoke(theUnsafe, buffer);
                    return null;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return e;
                }
            }
        });
        if (error != null) {
            throw new IOException("Unable to unmap the mapped buffer: " + resourceDescription, error);
        }
    }

    static Object unmapHackImpl() {
        try {
            // *** sun.misc.Unsafe unmapping (Java 9+) ***
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Method method = unsafeClass.getDeclaredMethod("invokeCleaner", ByteBuffer.class);
            final Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Object theUnsafe = f.get(null);
            return new BufferCleaner(ByteBuffer.class, method, theUnsafe);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
