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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.commons.utils.MessageFormatUtil;

/**
 * A Parse Error records an error in the input HTML that occurs in either the tokenisation or the tree building phase.
 */
public class ParseError {
    private int pos;
    private String errorMsg;

    ParseError(int pos, String errorMsg) {
        this.pos = pos;
        this.errorMsg = errorMsg;
    }

    ParseError(int pos, String errorFormat, Object... args) {
        this.errorMsg = MessageFormatUtil.format(errorFormat, args);
        this.pos = pos;
    }

    /**
     * Retrieve the error message.
     * @return the error message.
     */
    public String getErrorMessage() {
        return errorMsg;
    }

    /**
     * Retrieves the offset of the error.
     * @return error offset within input
     */
    public int getPosition() {
        return pos;
    }

    @Override
    public String toString() {
        return pos + ": " + errorMsg;
    }
}
