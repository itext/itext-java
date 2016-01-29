package com.itextpdf.kernel.parser;

import java.util.Set;

/**
 * This class expands each {@link TextRenderInfo} for {@link EventType#RENDER_TEXT} event types into
 * multiple {@link TextRenderInfo} instances for each glyph occurred.
 */
public class GlyphEventListener implements EventListener {

    protected final EventListener delegate;

    /**
     * Constructs a {@link GlyphEventListener} instance by a delegate to which the expanded text events for each
     * glyph occurred will be passed on.
     * @param delegate delegate to pass the expanded glyph render events to.
     */
    public GlyphEventListener(EventListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void eventOccurred(EventData data, EventType type) {
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
