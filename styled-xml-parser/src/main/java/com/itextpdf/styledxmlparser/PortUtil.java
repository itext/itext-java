/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.styledxmlparser;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in the future.
 */
public class PortUtil {

    /**
     * Instantiates a {@link PortUtil} instance.
     */
    private PortUtil() {
    }

    /**
     * Wraps a {@link Reader} instance in a {@link BufferedReader}.
     *
     * @param inputStreamReader the original reader
     * @return the buffered reader
     */
    public static Reader wrapInBufferedReader(Reader inputStreamReader) {
        return new BufferedReader(inputStreamReader);
    }

    /**
     * By default "." symbol in regular expressions does not match line terminators.
     * The issue is more complicated by the fact that "." does not match only "\n" in C#, while it does not
     * match several other characters as well in Java.
     * This utility method creates a pattern in which dots match any character, including line terminators
     * @param regex regular expression string
     * @return pattern in which dot characters match any Unicode char, including line terminators
     */
    public static Pattern createRegexPatternWithDotMatchingNewlines(String regex) {
        return Pattern.compile(regex, Pattern.DOTALL);
    }

}
