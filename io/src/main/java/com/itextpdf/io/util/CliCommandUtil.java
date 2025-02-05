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
package com.itextpdf.io.util;

import com.itextpdf.commons.utils.SystemUtil;

public final class CliCommandUtil {

    private CliCommandUtil() {
    }

    /**
     * Checks if the command, passed as parameter, is executable and the output version text contains
     * expected text
     *
     * @param command     a string command to execute
     * @param versionText an expected version text line
     * @return boolean result of checking: true - the required command is executable and the output version
     * text is correct
     */
    public static boolean isVersionCommandExecutable(String command, String versionText) {
        if ((command == null) || (versionText == null)) {
            return false;
        }

        try {
            String result = SystemUtil.runProcessAndGetOutput(command, "-version");
            return result.contains(versionText);
        } catch (Exception e) {
            return false;
        }
    }
}
