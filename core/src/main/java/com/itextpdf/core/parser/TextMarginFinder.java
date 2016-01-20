package com.itextpdf.core.parser;

import com.itextpdf.basics.geom.Rectangle;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class allows you to find the rectangle which contains all the text in the given content stream.
 */
public class TextMarginFinder implements EventListener {

    private Rectangle textRectangle = null;

    @Override
    public void eventOccurred(EventData data, EventType type) {
        if (type == EventType.RENDER_TEXT) {
            TextRenderInfo info = (TextRenderInfo) data;
            if (textRectangle == null) {
                textRectangle = info.getDescentLine().getBoundingRectange();
            } else {
                textRectangle = Rectangle.getCommonRectangle(textRectangle, info.getDescentLine().getBoundingRectange());
            }
            textRectangle = Rectangle.getCommonRectangle(textRectangle, info.getAscentLine().getBoundingRectange());
        } else {
            throw new IllegalStateException(String.format("Event type not supported: %s", type));
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
