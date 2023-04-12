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
package com.itextpdf.commons.utils;

/**
 * Experimental features class which contains constants related to experimental form fields drawing.
 */
// TODO Shall be removed in the scope of DEVSIX-7385
public final class ExperimentalFeatures {

    /**
     * Determines, whether the old or the new text form field drawing logic will be used.
     */
    public static boolean ENABLE_EXPERIMENTAL_TEXT_FORM_RENDERING = true;

    private ExperimentalFeatures() {
        // utility class
    }

}
