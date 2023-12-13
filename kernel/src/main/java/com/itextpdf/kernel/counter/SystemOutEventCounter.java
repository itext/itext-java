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
package com.itextpdf.kernel.counter;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;

/**
 * A {@link EventCounter} implementation that outputs event type to {@link System#out}
 */
public class SystemOutEventCounter extends EventCounter {

    /**
     * The name of the class for which the ICounter was created
     * (or iText if no name is available)
     */
    protected String name;

    public SystemOutEventCounter(String name) {
        this.name = name;
    }

    public SystemOutEventCounter() {
        this("iText");
    }

    public SystemOutEventCounter(Class<?> cls) {
        this(cls.getName());
    }

    @Override
    protected void onEvent(IEvent event, IMetaInfo metaInfo) {
        System.out.println(MessageFormatUtil.format("[{0}] {1} event", name, event.getEventType()));
    }
}
