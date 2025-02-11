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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;

/**
 * Abstract populator for placeholders consuming a parameter which is a pattern string. Any latin
 * letter inside the pattern which is not quoted considered as a param defining the component of the
 * outputted value.
 */
abstract class AbstractFormattedPlaceholderPopulator implements IPlaceholderPopulator {
    /**
     * Escaping character.
     */
    protected static final char APOSTROPHE = '\'';

    private static final char ESCAPE_CHARACTER = '\\';
    private static final char A_UPPERCASE = 'A';
    private static final char Z_UPPERCASE = 'Z';
    private static final char A_LOWERCASE = 'a';
    private static final char Z_LOWERCASE = 'z';

    /**
     * Processes quoted string inside format array. It is expected that provided index points to the
     * apostrophe character so that since the <code>index + 1</code> position quoted string starts.
     *
     * <p>
     * String may contain escaped apostrophes <code>\'</code> which processed as characters.
     * Backslash is used for escaping so you need double backslash to print it <code>\\</code>. All
     * the rest backslashes (not followed by apostrophe or one more backslash) are simply ignored.
     *
     * @param index is a index of apostrophe starting a new quoted string
     * @param builder is a {@link StringBuilder} building a resulting formatted string. It is
     *                updated by the method: quoted string is attached
     * @param formatArray is a format representation
     *
     * @return index of the character after the closing apostrophe
     *
     * @throws IllegalArgumentException if there is no closing apostrophe
     */
    protected int attachQuotedString(int index, StringBuilder builder, char[] formatArray) {
        boolean isEscaped = false;
        index++;
        while (index < formatArray.length && (formatArray[index] != APOSTROPHE || isEscaped)) {
            isEscaped = formatArray[index] == ESCAPE_CHARACTER && !isEscaped;

            if (! isEscaped) {
                builder.append(formatArray[index]);
            }

            index++;
        }

        if (index == formatArray.length) {
            throw new IllegalArgumentException(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION);
        }

        return index;
    }

    /**
     * Checks if provided character is a latin letter.
     *
     * @param ch is character to check
     *
     * @return <code>true</code> if character is a latin letter and <code>false</code> otherwise
     */
    protected final boolean isLetter(char ch) {
        return (A_LOWERCASE <= ch && Z_LOWERCASE >= ch) ||
                (A_UPPERCASE <= ch && Z_UPPERCASE >= ch);
    }
}
