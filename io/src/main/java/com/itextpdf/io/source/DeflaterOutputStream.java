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
import java.io.OutputStream;
import java.util.zip.Deflater;

public class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream {

    public DeflaterOutputStream(OutputStream out, int level, int size) {
        super(out, new Deflater(level), size);
    }

    public DeflaterOutputStream(OutputStream out, int level) {
        this(out, level, 512);
    }

    public DeflaterOutputStream(OutputStream out) {
        this(out, -1);
    }

    @Override
    public void close() throws IOException {
        finish();
        super.close();
    }

    @Override
    public void finish() throws IOException {
        super.finish();
        def.end();
    }
}
