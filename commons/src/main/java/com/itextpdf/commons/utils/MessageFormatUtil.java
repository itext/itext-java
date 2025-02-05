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
package com.itextpdf.commons.utils;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class MessageFormatUtil {

    private MessageFormatUtil() {
        // Empty constructor.
    }

    /**
     * This method provides a generic way for formatting strings.
     * Indexed arguments can be referred with {index},
     * to escape curly braces you have to double them.
     *
     * <p>
     * Only basic escaping is allowed, single quotes in a set of curly braces are not supported and
     * multiple escaped braces in a row are also not supported
     * 
     * <p>
     * Allowed {{{0}}}
     * Allowed '{0}'
     * Allowed '{{{0}}}'
     *
     * <p>
     * Not allowed {{'{0}'}}
     * Not allowed {{{{{0}}}}}
     *
     * @param pattern   to format
     * @param arguments arguments
     *
     * @return The formatted string
     */
    public static String format(String pattern, Object... arguments) {
        return new MessageFormat(
                pattern.replace("'", "''")
                        .replace("{{{","'{'{" )
                        .replace("}}}","}'}'" )
                        .replace("{{","'{'" )
                        .replace("}}","'}'" )
                ,Locale.ROOT).format(arguments);
    }
}
