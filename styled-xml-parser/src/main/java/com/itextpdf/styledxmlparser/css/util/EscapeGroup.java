package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to store escape characters and their processing logic.
 * This class is used in {@link CssUtils#splitString(String, char, EscapeGroup...)} method.
 */
public class EscapeGroup {
    private static final Logger LOGGER = LoggerFactory.getLogger(EscapeGroup.class);

    private final char openCharacter;
    private final char closeCharacter;

    private int counter = 0;

    /**
     * Creates instance of {@link EscapeGroup}.
     *
     * @param openCharacter  opening escape character
     * @param closeCharacter closing escape character
     */
    public EscapeGroup(char openCharacter, char closeCharacter) {
        this.openCharacter = openCharacter;
        this.closeCharacter = closeCharacter;
    }

    /**
     * Creates instance of {@link EscapeGroup} when opening and closing characters are the same.
     *
     * @param escapeChar opening and closing escape character
     */
    public EscapeGroup(char escapeChar) {
        this.openCharacter = escapeChar;
        this.closeCharacter = escapeChar;
    }

    /**
     * Is currently processed character in {@link CssUtils#splitString(String, char, EscapeGroup...)} escaped.
     *
     * @return true if escaped, false otherwise
     */
    boolean isEscaped() {
        return counter != 0;
    }

    /**
     * Processes given character.
     *
     * @param nextCharacter next character to process
     */
    void processCharacter(char nextCharacter) {
        if (openCharacter == closeCharacter) {
            if (nextCharacter == openCharacter) {
                if (isEscaped()) {
                    ++counter;
                } else {
                    --counter;
                }
            }
        } else {
            if (nextCharacter == openCharacter) {
                ++counter;
            } else if (nextCharacter == closeCharacter) {
                --counter;
                if (counter < 0) {
                    LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.INCORRECT_CHARACTER_SEQUENCE));
                    counter = 0;
                }
            }
        }
    }
}
