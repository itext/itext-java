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
package com.itextpdf.kernel.log;

import com.itextpdf.io.util.MessageFormatUtil;

/**
 * A {@link ICounter} implementation that outputs information about read and written documents to {@link System#out}
 * @deprecated will be removed in the next major release, please use {@link com.itextpdf.kernel.counter.SystemOutEventCounter} instead.
 */
@Deprecated
public class SystemOutCounter implements ICounter {

    /**
     * The name of the class for which the ICounter was created
     * (or iText if no name is available)
     */
    protected String name;

    public SystemOutCounter(String name) {
        this.name = name;
    }

    public SystemOutCounter() {
        this("iText");
    }

    public SystemOutCounter(Class<?> cls) {
        this(cls.getName());
    }


    @Override
    public void onDocumentRead(long size) {
        System.out.println(MessageFormatUtil.format("[{0}] {1} bytes read", name, size));
    }

    @Override
    public void onDocumentWritten(long size) {
        System.out.println(MessageFormatUtil.format("[{0}] {1} bytes written", name, size));
    }
}
