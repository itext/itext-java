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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class allows you to find the rectangle which contains all the text in the given content stream.
 */
public class TextMarginFinder implements IEventListener {

    private Rectangle textRectangle = null;

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type == EventType.RENDER_TEXT) {
            TextRenderInfo info = (TextRenderInfo) data;
            if (textRectangle == null) {
                textRectangle = info.getDescentLine().getBoundingRectangle();
            } else {
                textRectangle = Rectangle.getCommonRectangle(textRectangle, info.getDescentLine().getBoundingRectangle());
            }
            textRectangle = Rectangle.getCommonRectangle(textRectangle, info.getAscentLine().getBoundingRectangle());
        } else {
            throw new IllegalStateException(MessageFormatUtil.format("Event type not supported: {0}", type));
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_TEXT));
    }

    /**
     * Returns the common text rectangle, containing all the text found in the stream so far, ot {@code null}, if no
     * text has been found yet.
     * @return common text rectangle
     */
    public Rectangle getTextRectangle() {
        return textRectangle;
    }
}
