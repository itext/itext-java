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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssRuleSetParserTest extends ExtendedITextTest {

    @Test
    public void parsePropertyDeclarationsTest() throws IOException {
        String src = "float:right; clear:right;width:22.0em; margin:0 0 1.0em 1.0em; background:#f9f9f9; "
                + "border:1px solid #aaa;padding:0.2em;border-spacing:0.4em 0; text-align:center; "
                + "line-height:1.4em; font-size:88%;";

        String[] expected = new String[] {
                "float: right",
                "clear: right",
                "width: 22.0em",
                "margin: 0 0 1.0em 1.0em",
                "background: #f9f9f9",
                "border: 1px solid #aaa",
                "padding: 0.2em",
                "border-spacing: 0.4em 0",
                "text-align: center",
                "line-height: 1.4em",
                "font-size: 88%"
        };

        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(src);
        Assertions.assertEquals(expected.length, declarations.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], declarations.get(i).toString());
        }
    }
}
