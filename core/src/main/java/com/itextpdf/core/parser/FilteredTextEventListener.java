package com.itextpdf.core.parser;

/**
 * A text event listener which filters events on the fly before passing them on to the delegate.
 * The only difference from {@link FilteredEventListener} is that this class conveniently implements
 * {@link TextExtractionStrategy} and can therefore used as a strategy on its own, apart from the inherited
 * function of filtering event appropriately to its delegates.
 */
public class FilteredTextEventListener extends FilteredEventListener implements TextExtractionStrategy {
    /**
     * Constructs a {@link FilteredTextEventListener} instance with a {@link TextExtractionStrategy} delegate.
     *
     * @param delegate  a delegate that fill be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     */
    public FilteredTextEventListener(TextExtractionStrategy delegate, EventFilter... filterSet) {
        super(delegate, filterSet);
    }

    /**
     * As an resultant text we use the concatenation of all the resultant text of all the delegates that implement
     * {@link TextExtractionStrategy}.
     * @return the resulting concatenation of the text extracted from the delegates
     */
    @Override
    public String getResultantText() {
        StringBuilder sb = new StringBuilder();
        for (EventListener delegate : delegates) {
            if (delegate instanceof TextExtractionStrategy) {
                sb.append(((TextExtractionStrategy) delegate).getResultantText());
            }
        }
        return sb.toString();
    }

}
