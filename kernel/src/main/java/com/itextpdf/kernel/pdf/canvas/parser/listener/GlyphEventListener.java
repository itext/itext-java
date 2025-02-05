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
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import java.util.Set;

/**
 * This class expands each {@link TextRenderInfo} for {@link EventType#RENDER_TEXT} event types into
 * multiple {@link TextRenderInfo} instances for each glyph occurred.
 */
public class GlyphEventListener implements IEventListener {

    protected final IEventListener delegate;

    /**
     * Constructs a {@link GlyphEventListener} instance by a delegate to which the expanded text events for each
     * glyph occurred will be passed on.
     * @param delegate delegate to pass the expanded glyph render events to.
     */
    public GlyphEventListener(IEventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo textRenderInfo = (TextRenderInfo) data;
            for (TextRenderInfo glyphRenderInfo : textRenderInfo.getCharacterRenderInfos()) {
                delegate.eventOccurred(glyphRenderInfo, type);
            }
        } else {
            delegate.eventOccurred(data, type);
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return delegate.getSupportedEvents();
    }
}
