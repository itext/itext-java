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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import java.util.Set;

/**
 * A callback interface that receives notifications from the {@link PdfCanvasProcessor}
 * as various events occur (see {@link EventType}).
 */
public interface IEventListener {

    /**
     * Called when some event occurs during parsing a content stream.
     * @param data Combines the data required for processing corresponding event type.
     * @param type Event type.
     */
    void eventOccurred(IEventData data, EventType type);

    /**
     * Provides the set of event types this listener supports.
     * Returns null if all possible event types are supported.
     * @return Set of event types supported by this listener or
     * null if all possible event types are supported.
     */
    Set<EventType> getSupportedEvents();
}
