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
package com.itextpdf.styledxmlparser.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Split CSS 'font-family' string into list of font-families or generic-families
 */
public final class FontFamilySplitterUtil {

    private static final Pattern FONT_FAMILY_PATTERN = Pattern.compile("^([\\w-]+)$");
    private static final Pattern FONT_FAMILY_PATTERN_QUOTED = Pattern.compile("^(('[\\w -]+')|(\"[\\w -]+\"))$");
    private static final Pattern FONT_FAMILY_PATTERN_QUOTED_SELECT = Pattern.compile("([\\w -]+)");

    public static List<String> splitFontFamily(String fontFamilies) {
        if (fontFamilies == null) {
            return null;
        }
        String[] names = fontFamilies.split(",");
        List<String> result = new ArrayList<>(names.length);
        for (String name : names) {
            String trimmedName = name.trim();
            // TODO DEVSIX-2534 improve pattern matching according to CSS specification. E.g. unquoted font-families with spaces.
            if (FONT_FAMILY_PATTERN.matcher(trimmedName).matches()) {
                result.add(trimmedName);
            } else if (FONT_FAMILY_PATTERN_QUOTED.matcher(trimmedName).matches()) {
                Matcher selectMatcher = FONT_FAMILY_PATTERN_QUOTED_SELECT.matcher(trimmedName);
                if (selectMatcher.find()) {
                    result.add(selectMatcher.group());
                }
            }
        }
        return result;
    }

    public static String removeQuotes(String fontFamily) {
        Matcher selectMatcher = FONT_FAMILY_PATTERN_QUOTED_SELECT.matcher(fontFamily);
        if (selectMatcher.find()) {
            return selectMatcher.group();
        }
        return null;
    }
}
