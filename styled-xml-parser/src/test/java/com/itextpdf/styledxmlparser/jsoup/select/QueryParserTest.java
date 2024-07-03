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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Tests for the Selector Query Parser.
 */
@Tag("UnitTest")
public class QueryParserTest extends ExtendedITextTest {
    @Test public void testOrGetsCorrectPrecedence() {
        // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
        // top level or, three child ands
        Evaluator eval = QueryParser.parse("a b, c d, e f");
        Assertions.assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        Assertions.assertEquals(3, or.evaluators.size());
        for (Evaluator innerEval: or.evaluators) {
            Assertions.assertTrue(innerEval instanceof CombiningEvaluator.And);
            CombiningEvaluator.And and = (CombiningEvaluator.And) innerEval;
            Assertions.assertEquals(2, and.evaluators.size());
            Assertions.assertTrue(and.evaluators.get(0) instanceof StructuralEvaluator.Parent);
            Assertions.assertTrue(and.evaluators.get(1) instanceof Evaluator.Tag);
        }
    }

    @Test public void testParsesMultiCorrectly() {
        String query = ".foo > ol, ol > li + li";
        Evaluator eval = QueryParser.parse(query);
        Assertions.assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        Assertions.assertEquals(2, or.evaluators.size());

        CombiningEvaluator.And andLeft = (CombiningEvaluator.And) or.evaluators.get(0);
        CombiningEvaluator.And andRight = (CombiningEvaluator.And) or.evaluators.get(1);

        Assertions.assertEquals(".foo > ol", andLeft.toString());
        Assertions.assertEquals(2, andLeft.evaluators.size());
        Assertions.assertEquals("ol > li + li", andRight.toString());
        Assertions.assertEquals(2, andRight.evaluators.size());
        Assertions.assertEquals(query, eval.toString());
    }

    @Test public void exceptionOnUncloseAttribute() {
        Assertions.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse("section > a[href=\"]"));
    }

    @Test public void testParsesSingleQuoteInContains() {
        Assertions.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse("p:contains(One \" One)"));
    }


    @Test public void exceptOnEmptySelector() {
        Assertions.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse(""));
    }

    @Test public void exceptOnNullSelector() {
        Assertions.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse(null));
    }

    @Test public void okOnSpacesForeAndAft() {
        Evaluator parse = QueryParser.parse(" span div  ");
        Assertions.assertEquals("span div", parse.toString());
    }

    @Test public void structuralEvaluatorsToString() {
        String q = "a:not(:has(span.foo)) b d > e + f ~ g";
        Evaluator parse = QueryParser.parse(q);
        Assertions.assertEquals(q, parse.toString());
    }
}
