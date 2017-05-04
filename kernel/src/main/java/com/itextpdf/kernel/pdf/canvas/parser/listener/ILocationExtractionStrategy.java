package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.filter.IEventFilter;

import java.util.Collection;

/**
 * This is a special interface for {@link IEventFilter} that returns a collection of rectangles as result of its work.
 */
public interface ILocationExtractionStrategy extends IEventListener {

    /**
     * Returns the rectangles that have been processed so far.
     *
     * @return {@link Collection<IPdfTextLocation>} instance with the current resultant IPdfTextLocations
     */
    Collection<IPdfTextLocation> getResultantLocations();

}
