/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.font;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Split css font-family string into list of font-families or generic-families
 * @deprecated will be removed in iText 7.2.
 */
@Deprecated
public final class FontFamilySplitter {

    private static final Pattern FONT_FAMILY_PATTERN = Pattern.compile("^ *([\\w-]+) *$");
    private static final Pattern FONT_FAMILY_PATTERN_QUOTED = Pattern.compile("^ *(('[\\w -]+')|(\"[\\w -]+\")) *$");
    private static final Pattern FONT_FAMILY_PATTERN_QUOTED_SELECT = Pattern.compile("[\\w-]+( +[\\w-]+)*");

    public static List<String> splitFontFamily(String fontFamilies) {
        if (fontFamilies == null) {
            return null;
        }
        String[] names = fontFamilies.split(",");
        List<String> result = new ArrayList<>(names.length);
        for (String name : names) {
            if (FONT_FAMILY_PATTERN.matcher(name).matches()) {
                result.add(name.trim());
            } else if (FONT_FAMILY_PATTERN_QUOTED.matcher(name).matches()) {
                Matcher selectMatcher = FONT_FAMILY_PATTERN_QUOTED_SELECT.matcher(name);
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
