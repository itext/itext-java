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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssRuleSetTest extends ExtendedITextTest {

    @Test
    public void addCssRuleSetWithNormalImportantDeclarationsTest() {
        String src =
                "float:right; clear:right !important;width:22.0em!important; margin:0 0 1.0em 1.0em; "
                        + "background:#f9f9f9; "
                        + "border:1px solid #aaa;padding:0.2em ! important;border-spacing:0.4em 0; text-align:center "
                        + "!important; "
                        + "line-height:1.4em; font-size:88%!  important;";

        String[] expectedNormal = new String[] {
                "float: right",
                "margin: 0 0 1.0em 1.0em",
                "background: #f9f9f9",
                "border: 1px solid #aaa",
                "border-spacing: 0.4em 0",
                "line-height: 1.4em"
        };

        String[] expectedImportant = new String[] {
                "clear: right",
                "width: 22.0em",
                "padding: 0.2em",
                "text-align: center",
                "font-size: 88%"
        };

        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(src);
        CssSelector selector = new CssSelector("h1");
        CssRuleSet cssRuleSet = new CssRuleSet(selector, declarations);
        List<CssDeclaration> normalDeclarations = cssRuleSet.getNormalDeclarations();
        for (int i = 0; i < expectedNormal.length; i++) {
            Assertions.assertEquals(expectedNormal[i], normalDeclarations.get(i).toString());
        }
        List<CssDeclaration> importantDeclarations = cssRuleSet.getImportantDeclarations();
        for (int i = 0; i < expectedImportant.length; i++) {
            Assertions.assertEquals(expectedImportant[i], importantDeclarations.get(i).toString());
        }
    }
}
