/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.styledxmlparser.jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortUtil {

    public static final String escapedSingleBracket = "''";
    public static final String signedNumberFormat = ",number,+#;-#";

    /**
     * @deprecated use {@link Matcher#find()} instead
     * */
    @Deprecated
    public static boolean hasMatch(Pattern pattern, String input) {
        return pattern.matcher(input).find();
    }

    public static boolean charsetIsSupported(String charsetName) {
        try {
            return Charset.isSupported(charsetName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static RandomAccessFile getReadOnlyRandomAccesFile(File file)throws FileNotFoundException {
        return new RandomAccessFile(file, "r");
    }

    /**
     * @deprecated use {@link Matcher#find()} instead
     * */
    @Deprecated
    public static boolean isSuccessful(Matcher m) {
        return m.find();
    }
}
