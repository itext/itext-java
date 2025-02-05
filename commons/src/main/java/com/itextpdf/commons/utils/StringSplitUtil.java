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

import java.util.regex.Pattern;

/**
 * Utility class which splits string data to array by provided character.
 */
public final class StringSplitUtil {

    private StringSplitUtil() {

    }


    /**
     * Splits string data to array by provided character.
     *
     * @param data data to split.
     * @param toSplitOn character by which data will be split.
     *
     * @return array of string which were split.
     */
    public static String[] splitKeepTrailingWhiteSpace(String data, char toSplitOn) {
        return data.split(Pattern.quote(String.valueOf(toSplitOn)), -1);
    }

}
