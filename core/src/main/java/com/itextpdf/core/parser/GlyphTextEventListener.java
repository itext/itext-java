package com.itextpdf.core.parser;

/**
 * This class expands each {@link TextRenderInfo} for {@link EventType#RENDER_TEXT} event types into
 * multiple {@link TextRenderInfo} instances for each glyph occurred.
 * The only difference from {@link FilteredEventListener} is that this class conveniently implements
 * {@link TextExtractionStrategy} and can therefore used as a strategy on its own.
 */
public class GlyphTextEventListener extends GlyphEventListener implements TextExtractionStrategy {

    /**
     * Constructs a {@link GlyphEventListener} instance by a {@link TextExtractionStrategy} delegate to which
     * the expanded text events for each glyph occurred will be passed on.
     *
     * @param delegate delegate to pass the expanded glyph render events to.
     */
    public GlyphTextEventListener(TextExtractionStrategy delegate) {
        super(delegate);
    }

    /**
     * As an resultant text we use the the resultant text of the delegate that implement
     * {@link TextExtractionStrategy} and was passed to this class.
     * @return the resulting text extracted from the delegate
     */
    @Override
    public String getResultantText() {
        if (delegate instanceof TextExtractionStrategy) {
            return ((TextExtractionStrategy) delegate).getResultantText();
        } else {
            return null;
        }
    }

}
