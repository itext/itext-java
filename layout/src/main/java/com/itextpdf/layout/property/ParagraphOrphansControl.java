package com.itextpdf.layout.property;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialized class holding configurable parameters related to {@link com.itextpdf.layout.element.Paragraph}'s
 * orphans restrictions. This class is meant to be used as the value for the {@link Property#ORPHANS_CONTROL} key.
 */
public class ParagraphOrphansControl {
    private int minOrphans;

    /**
     * Creates a {@link ParagraphOrphansControl} instance with a specified orphans limitation.
     *
     * @param minOrphans minimal number of paragraph's lines to remain on an area before an area break.
     */
    public ParagraphOrphansControl(int minOrphans) {
        this.minOrphans = minOrphans;
    }

    /**
     * Sets parameter that defines orphans restrictions.
     *
     * @param minOrphans minimal number of paragraph's lines to remain on an area before an area break.
     * @return this {@link ParagraphOrphansControl} instance
     */
    public ParagraphOrphansControl setMinAllowedOrphans(int minOrphans) {
        this.minOrphans = minOrphans;
        return this;
    }

    /**
     * Gets minimal number of paragraph's lines to remain on an area before a split.
     *
     * @return minimal number of paragraph's lines to remain on an area before an area break.
     */
    public int getMinOrphans() {
        return minOrphans;
    }

    /**
     * Writes a log message reporting that orphans constraint is violated.
     *
     * This method is to be overridden if violation scenarios need to be handled in some other way.
     *
     * @param renderer a renderer processing orphans
     * @param message  {@link String} explaining the reason for violation
     */
    public void handleViolatedOrphans(ParagraphRenderer renderer, String message) {
        Logger logger = LoggerFactory.getLogger(ParagraphOrphansControl.class);
        if (renderer.getOccupiedArea() != null && renderer.getLines() != null) {
            int pageNumber = renderer.getOccupiedArea().getPageNumber();
            String warnText = MessageFormatUtil.format(LogMessageConstant.ORPHANS_CONSTRAINT_VIOLATED, pageNumber,
                    minOrphans, renderer.getLines().size(), message);
            logger.warn(warnText);
        } else {
            logger.warn(LogMessageConstant.PREMATURE_CALL_OF_HANDLE_VIOLATION_METHOD);
        }
    }
}
