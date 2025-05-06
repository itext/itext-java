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
package com.itextpdf.kernel.utils.checkers;

import java.util.regex.Pattern;

/**
 * This class is a validator for IETF BCP 47 language tag (RFC 5646).
 */
public final class BCP47Validator {
    private static final String REGULAR =
            "(art-lojban|cel-gaulish|no-bok|no-nyn|zh-guoyu|zh-hakka|zh-min|zh-min-nan|zh-xiang)";
    private static final String IRREGULAR = "(en-GB-oed|i-ami|i-bnn|i-default|i-enochian|i-hak|i-klingon|i-lux|" +
            "i-mingo|i-navajo|i-pwn|i-tao|i-tay|i-tsu|sgn-BE-FR|sgn-BE-NL|sgn-CH-DE)";
    private static final String GRANDFATHERED = "(?<grandfathered>" + IRREGULAR + "|" + REGULAR + ")";
    private static final String PRIVATE_USE = "(?<privateUse>x(-[A-Za-z0-9]{1,8})+)";
    private static final String SINGLETON = "[0-9A-WY-Za-wy-z]";
    private static final String EXTENSION = "(?<extension>" + SINGLETON + "(-[A-Za-z0-9]{2,8})+)";
    private static final String VARIANT = "(?<variant>[A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3})";
    private static final String REGION = "(?<region>[A-Za-z]{2}|[0-9]{3})";
    private static final String SCRIPT = "(?<script>[A-Za-z]{4})";
    private static final String EXTLANG = "(?<extlang>[A-Za-z]{3}(-[A-Za-z]{3}){0,2})";
    private static final String LANGUAGE = "(?<language>([A-Za-z]{2,3}(-" + EXTLANG + ")?)|[A-Za-z]{4}|[A-Za-z]{5,8})";
    private static final String LANGTAG = "(" + LANGUAGE + "(-" + SCRIPT + ")?" + "(-" + REGION + ")?"
            + "(-" + VARIANT + ")*" + "(-" + EXTENSION + ")*" + "(-" + PRIVATE_USE + ")?" + ")";
    // Java regex polices doesn't allow duplicate named capture groups,
    // so we have to change the 2nd use <privateUse> to ?<privateUse1>.
    private static final Pattern LANGUAGE_TAG_PATTERN = Pattern.compile("^(" + GRANDFATHERED + "|" + LANGTAG + "|" +
            PRIVATE_USE.replace("privateUse", "privateUse1") + ")$");

    private BCP47Validator() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Validate language tag against RFC 5646.
     *
     * @param languageTag language tag string
     *
     * @return {@code true} if it is a valid tag, {@code false} otherwise
     */
    public static boolean validate(String languageTag) {
        return LANGUAGE_TAG_PATTERN.matcher(languageTag).matches();
    }
}
