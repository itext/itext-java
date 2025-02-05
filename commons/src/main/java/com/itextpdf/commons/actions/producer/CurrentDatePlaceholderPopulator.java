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

import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class is used to populate <code>currentDate</code> placeholder. Placeholder should be configured
 * with parameter defining the format of date output. Within format strings, unquoted letters from
 * <code>A</code> to <code>Z</code> and from <code>a</code> to <code>z</code> are process as pattern
 * letters. Chain of equal pattern letters forms an appropriate component of
 * <code>currentDate</code> format. There following components are supported:
 *
 * <p>
 * <ul>
 *     <li><code>d</code> is for the day of the month, from 1 through 31
 *     <li><code>dd</code> is for the day of the month, from 01 through 31
 *     <li><code>M</code> defines the month from 1 to 12
 *     <li><code>MM</code> defines the month from 01 to 12
 *     <li><code>MMM</code> defines the abbreviated name of the month
 *     <li><code>MMMM</code> defines the full name of month
 *     <li><code>yy</code> means the year from 00 to 99
 *     <li><code>yyyy</code> means the year in for digits format
 *     <li><code>s</code> shows current second, from 0 through 59
 *     <li><code>ss</code> shows current second, from 00 through 59
 *     <li><code>m</code> is replaced with the current minute from 0 to 59
 *     <li><code>mm</code> is replaced with the current minute from 00 to 59
 *     <li><code>H</code> stands for the current hour, using a 24-hour clock from 0 to 23
 *     <li><code>HH</code> stands for the current hour, using a 24-hour clock from 00 to 23
 * </ul>
 *
 * <p>
 * Text can be quoted using single quotes (') to avoid interpretation. All other characters are not
 * interpreted and just copied into the output string. String may contain escaped apostrophes
 * <code>\'</code> which processed as characters. Backslash is used for escaping so you need double
 * backslash to print it <code>\\</code>. All the rest backslashes (not followed by apostrophe or
 * one more backslash) are simply ignored.
 *
 * <p>
 * The result of the processing is current date representing in accordance with the provided format.
 */
class CurrentDatePlaceholderPopulator extends AbstractFormattedPlaceholderPopulator {

    private static final Set<String> ALLOWED_PATTERNS = new HashSet<>(Arrays.asList(
                "dd", "MM", "MMM", "MMMM", "yy", "yyyy", "ss", "mm", "HH"
            ));

    public CurrentDatePlaceholderPopulator() {
        // Empty constructor.
    }

    /**
     * Builds a replacement for a placeholder <code>currentDate</code> in accordance with the
     * provided format.
     *
     * @param events is a list of event involved into document processing. It is not used during
     *               the placeholder replacement
     * @param parameter defines output format in accordance with the description
     *
     * @return date of producer line creation in accordance with defined format
     *
     * @throws IllegalArgumentException if format of the date pattern is invalid
     */
    @Override
    public String populate(List<ConfirmedEventWrapper> events, String parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "currentDate")
            );
        }

        final Date now = DateTimeUtil.getCurrentTimeDate();
        return formatDate(now, parameter);
    }

    private String formatDate(Date date, String format) {
        final StringBuilder builder = new StringBuilder();
        char[] formatArray = format.toCharArray();

        for (int i = 0; i < formatArray.length; i++) {
            if (formatArray[i] == APOSTROPHE) {
                i = attachQuotedString(i, builder, formatArray);
            } else if (isLetter(formatArray[i])) {
                i = processDateComponent(i, date, builder, formatArray);
            } else {
                builder.append(formatArray[i]);
            }
        }

        return builder.toString();
    }

    private int processDateComponent(int index, Date date, StringBuilder builder, char[] formatArray) {
        final StringBuilder peaceBuilder = new StringBuilder();
        final char currentChar = formatArray[index];
        peaceBuilder.append(currentChar);
        while (index + 1 < formatArray.length && currentChar == formatArray[index + 1]) {
            index++;
            peaceBuilder.append(formatArray[index]);
        }
        final String piece = peaceBuilder.toString();
        if (ALLOWED_PATTERNS.contains(piece)) {
            builder.append(DateTimeUtil.format(date, piece));
        } else {
            throw new IllegalArgumentException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, piece)
            );
        }

        return index;
    }
}
