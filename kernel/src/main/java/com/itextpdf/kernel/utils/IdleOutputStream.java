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
package com.itextpdf.kernel.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} implementation which doesn't write anything.
 */
public class IdleOutputStream extends OutputStream {
    /**
     * Default constructor to create {@link IdleOutputStream}.
     */
    public IdleOutputStream() {
        // Empty constructor.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void write(int b) {
        // Idle output stream write method does nothing.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Idle output stream write method does nothing.
    }
}
