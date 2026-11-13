/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/

package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests for the Selector Query Parser.
 *
 * @author Jonathan Hedley
 */
@Category(UnitTest.class)
public class QueryParserTest extends ExtendedITextTest {
    @Test public void testOrGetsCorrectPrecedence() {
        // tests that a selector "a b, c d, e f" evals to (a AND b) OR (c AND d) OR (e AND f)"
        // top level or, three child ands
        Evaluator eval = QueryParser.parse("a b, c d, e f");
        Assert.assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        Assert.assertEquals(3, or.evaluators.size());
        for (Evaluator innerEval: or.evaluators) {
            Assert.assertTrue(innerEval instanceof CombiningEvaluator.And);
            CombiningEvaluator.And and = (CombiningEvaluator.And) innerEval;
            Assert.assertEquals(2, and.evaluators.size());
            Assert.assertTrue(and.evaluators.get(0) instanceof StructuralEvaluator.Parent);
            Assert.assertTrue(and.evaluators.get(1) instanceof Evaluator.Tag);
        }
    }

    @Test public void testParsesMultiCorrectly() {
        String query = ".foo > ol, ol > li + li";
        Evaluator eval = QueryParser.parse(query);
        Assert.assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        Assert.assertEquals(2, or.evaluators.size());

        CombiningEvaluator.And andLeft = (CombiningEvaluator.And) or.evaluators.get(0);
        CombiningEvaluator.And andRight = (CombiningEvaluator.And) or.evaluators.get(1);

        Assert.assertEquals(".foo > ol", andLeft.toString());
        Assert.assertEquals(2, andLeft.evaluators.size());
        Assert.assertEquals("ol > li + li", andRight.toString());
        Assert.assertEquals(2, andRight.evaluators.size());
        Assert.assertEquals(query, eval.toString());
    }

    @Test public void exceptionOnUncloseAttribute() {
        Assert.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse("section > a[href=\"]"));
    }

    @Test public void testParsesSingleQuoteInContains() {
        Assert.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse("p:contains(One \" One)"));
    }


    @Test public void exceptOnEmptySelector() {
        Assert.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse(""));
    }

    @Test public void exceptOnNullSelector() {
        Assert.assertThrows(Selector.SelectorParseException.class, () -> QueryParser.parse(null));
    }

    @Test public void okOnSpacesForeAndAft() {
        Evaluator parse = QueryParser.parse(" span div  ");
        Assert.assertEquals("span div", parse.toString());
    }

    @Test public void structuralEvaluatorsToString() {
        String q = "a:not(:has(span.foo)) b d > e + f ~ g";
        Evaluator parse = QueryParser.parse(q);
        Assert.assertEquals(q, parse.toString());
    }
}
