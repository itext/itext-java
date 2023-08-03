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
package com.itextpdf.signatures.logs;

/**
 * Class which contains constants to be used in logging inside sign module.
 */
public final class SignLogMessageConstant {

    public static final String EXCEPTION_WITHOUT_MESSAGE =
            "Unexpected exception without message was thrown during keystore processing";
    
    public static final String ALGORITHM_NOT_FROM_SPEC =
            "Requested algorithm might not be supported by the pdf specification.";

    private SignLogMessageConstant() {
        // Private constructor will prevent the instantiation of this class directly
    }
}
