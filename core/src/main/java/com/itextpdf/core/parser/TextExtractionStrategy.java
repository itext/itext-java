package com.itextpdf.core.parser;

/**
 * This is a special interface for {@link EventFilter} that returns text as result of its work.
 */
public interface TextExtractionStrategy extends EventListener {

    /**
     * Returns the text that has been processed so far.
     * @return {@link String} instance with the current resultant text
     */
    String getResultantText();

}
