package com.itextpdf.layout.property;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialized class holding configurable parameters related to {@link com.itextpdf.layout.element.Paragraph}'s
 * widows restrictions. This class is meant to be used as the value for the {@link Property#WIDOWS_CONTROL} key.
 */
public class ParagraphWidowsControl {
    private int minWidows;
    private int maxLinesToMove;
    private boolean overflowOnWidowsViolation;

    /**
     * Creates a {@link ParagraphWidowsControl} instance with specified widows restrictions.
     *
     * @param minWidows                    minimal number of paragraph's lines to be overflowed to the next area.
     * @param maxLinesToMove               a number of lines that are allowed to be moved to the next area
     *                                     in order to fix widows constraint violation.
     * @param overflowParagraphOnViolation defines whether the entire paragraph should be pushed to the next area
     *                                     if widows constraint is violated and cannot be automatically fixed.
     */
    public ParagraphWidowsControl(int minWidows, int maxLinesToMove, boolean overflowParagraphOnViolation) {
        this.minWidows = minWidows;
        this.maxLinesToMove = maxLinesToMove;
        overflowOnWidowsViolation = overflowParagraphOnViolation;
    }

    /**
     * Sets parameters that define widows restrictions and conditions of handling cases of widows constraint violation.
     *
     * @param minWidows                    minimal number of paragraph's lines to be overflowed to the next area.
     * @param maxLinesToMove               a number of lines that are allowed to be moved to the next area
     *                                     in order to fix widows constraint violation.
     * @param overflowParagraphOnViolation defines whether paragraph should be completely pushed to the next area
     *                                     if widows constraint is violated and cannot be automatically fixed.
     * @return this {@link ParagraphWidowsControl} instance.
     */
    public ParagraphWidowsControl setMinAllowedWidows(int minWidows, int maxLinesToMove,
            boolean overflowParagraphOnViolation) {
        this.minWidows = minWidows;
        this.maxLinesToMove = maxLinesToMove;
        overflowOnWidowsViolation = overflowParagraphOnViolation;
        return this;
    }

    /**
     * Gets minimal number of paragraph's lines to be overflowed to the next area.
     *
     * @return minimal number of paragraph's lines to be overflowed to the next area
     */
    public int getMinWidows() {
        return minWidows;
    }

    /**
     * Gets a number of lines that are allowed to be moved to the next area in order to fix
     * widows constraint violation.
     *
     * @return a number of lines that are allowed to be moved to the next are
     */
    public int getMaxLinesToMove() {
        return maxLinesToMove;
    }

    /**
     * Indicates whether paragraph should be completely pushed to the next area if widows constraint is violated and
     * cannot be automatically fixed.
     *
     * @return true if paragraph should be completely pushed to the next area if widows constraint is violated and
     * cannot be automatically fixed, otherwise - false
     */
    public boolean isOverflowOnWidowsViolation() {
        return overflowOnWidowsViolation;
    }

    /**
     * Writes a log message reporting that widows constraint is violated and cannot be automatically fixed.
     *
     * This method is to be overridden if violation scenarios need to be handled in some other way.
     *
     * @param widowsRenderer a renderer processing widows
     * @param message        {@link String} explaining the reason for violation
     */
    public void handleViolatedWidows(ParagraphRenderer widowsRenderer, String message) {
        Logger logger = LoggerFactory.getLogger(ParagraphWidowsControl.class);
        if (widowsRenderer.getOccupiedArea() != null && widowsRenderer.getLines() != null) {
            int pageNumber = widowsRenderer.getOccupiedArea().getPageNumber();
            String warnText = MessageFormatUtil.format(LogMessageConstant.WIDOWS_CONSTRAINT_VIOLATED,
                    pageNumber, minWidows, widowsRenderer.getLines().size(), message);
            logger.warn(warnText);
        } else {
            logger.warn(LogMessageConstant.PREMATURE_CALL_OF_HANDLE_VIOLATION_METHOD);
        }
    }
}
