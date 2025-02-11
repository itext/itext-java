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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
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
                    LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.INCORRECT_CHARACTER_SEQUENCE));
                    counter = 0;
                }
            }
        }
    }
}
