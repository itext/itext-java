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
package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssRuleSet;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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


    @Test
    public void commaInsidePseudoClassTest() throws IOException {
        String src = "p > :not(strong), :not(b.important)";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "p > :not(strong) {\n"
                        + "    color: darkmagenta\n"
                        + "}",
                ":not(b.important) {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void defaultPseudoClassTest() throws IOException {
        String src = "p > :not(strong)";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "p > :not(strong) {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void doubleQuotesPseudoClassTest() throws IOException {
        String src = "p > :not(strong) \"asd\"";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "p > :not(strong) \"asd\" {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void singleQuotesPseudoClassTest() throws IOException {
        String src = "p > :not(strong) 'a'";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "p > :not(strong) 'a' {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void functionAndSelectTest() throws IOException {
        String src = "*[data-dito-element=listbox] select";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "*[data-dito-element=\"listbox\"] select {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void singleCommaEndingPseudoClassTest() throws IOException {
        String src = "p > :not(strong),";
        String properties = "color: darkmagenta;";

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(0, cssRuleSets.size());
    }

    @Test
    public void commaEndingPseudoClassTest() throws IOException {
        String src = "p > :not(strong),,,";
        String properties = "color: darkmagenta;";

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(0, cssRuleSets.size());
    }

    @Test
    public void spaceBetweenPseudoClassesTest() throws IOException {
        String src = ":not(:empty) :not(:empty) :not(:empty) :not(:empty) :not(:empty)";
        String properties = "border: green solid 3px;";

        String[] expected = new String[] {
                ":not(:empty) :not(:empty) :not(:empty) :not(:empty) :not(:empty) {\n"
                        + "    border: green solid 3px\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }
    @Test
    public void pseudoClassWithSumTest() throws IOException {
        String src = " article:not(.archived) section.highlight + aside:not(.hidden, [data-disabled=\"true\"])";
        String properties = "background-color: lightyellow;";

        String[] expected = new String[] {
                "article:not(.archived) section.highlight + aside:not(.hidden , [data-disabled=\"true\"]) {\n"
                        + "    background-color: lightyellow\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void complexPseudoClassTest() throws IOException {
        String src = "div:not([data-role=\"admin\"], :nth-of-type(2n)):not(:empty)::before";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "div:not([data-role=\"admin\"] , :nth-of-type(2n)):not(:empty)::before {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void notAddSpaceAfterFourDotsTest() throws IOException {
        String src = "[data-foo]::before";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "[data-foo]::before {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void disappearedCommasTest() throws IOException {
        String src = "strong:not([data-time=\"37\"], :empty, b.warning)";
        String properties = "color: darkmagenta;";

        String[] expected = new String[] {
                "strong:not([data-time=\"37\"] , :empty , b.warning) {\n"
                        + "    color: darkmagenta\n"
                        + "}"
        };

        List<CssRuleSet> cssRuleSets = CssRuleSetParser.parseRuleSet(src, properties);
        Assertions.assertEquals(expected.length, cssRuleSets.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], cssRuleSets.get(i).toString());
        }
    }

    @Test
    public void splitByTokensQuotesTest() throws IOException {
        Assertions.assertArrayEquals(new String[] {"a.className 'someText'"},
                CssRuleSetParser.splitByTokens("a.className 'someText'"));

        Assertions.assertArrayEquals(new String[] {"a.className \"someText\""},
                CssRuleSetParser.splitByTokens("a.className \"someText\""));

        Assertions.assertArrayEquals(new String[] {"a.className text"},
                CssRuleSetParser.splitByTokens("a.className \"text"));

        Assertions.assertArrayEquals(new String[] {"a.className text"},
                CssRuleSetParser.splitByTokens("a.className text'"));
    }


    @Test
    public void splitByTokensCommaTest() throws IOException {
        Assertions.assertArrayEquals(new String[] {"strong:not([data-time=\"37\"], :empty, b.warning)"},
                CssRuleSetParser.splitByTokens("strong:not([data-time=\"37\"], :empty, b.warning)"));

        Assertions.assertArrayEquals(new String[] {"p > :not(strong, b.important)"},
                CssRuleSetParser.splitByTokens("p > :not(strong, b.important)"));

        Assertions.assertArrayEquals(new String[] {"\"a.class, b\""},
                CssRuleSetParser.splitByTokens("\"a.class, b\""));

        Assertions.assertArrayEquals(new String[] {"'a.class, b'"},
                CssRuleSetParser.splitByTokens("'a.class, b'"));

        Assertions.assertArrayEquals(new String[] {"a.class", "p"},
                CssRuleSetParser.splitByTokens("a.class, p"));
    }
}
