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
package com.itextpdf.io.source;

import java.io.IOException;

public class ThreadSafeRandomAccessSource implements IRandomAccessSource {
    private final IRandomAccessSource source;
    private final Object lockObj = new Object();
    
    public ThreadSafeRandomAccessSource(IRandomAccessSource source) {
        this.source = source;
    }

    @Override
    public int get(long position) throws IOException {
        synchronized (lockObj) {
            return source.get(position);
        }
    }

    @Override
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        synchronized (lockObj) {
            return source.get(position, bytes, off, len);
        }
    }

    @Override
    public long length() {
        synchronized (lockObj) {
            return source.length();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lockObj) {
            source.close();
        }
    }
}
