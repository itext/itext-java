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

/**
 * Utility class for runtime-related operations.
 */
public final class RuntimeUtil {

    private RuntimeUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if a class is loaded in the current runtime environment.
     *
     * @param fullyQualifiedClassName the fully qualified name of the class to check
     * @return true if the class is loaded, false otherwise
     */
    public static boolean isClassLoaded(String fullyQualifiedClassName) {
        try {
            Class.forName(fullyQualifiedClassName);
            return true;
        } catch (ClassNotFoundException ignored) {
            // ignore the exception, it means the resources are not available
            return false;
        }
    }
}
