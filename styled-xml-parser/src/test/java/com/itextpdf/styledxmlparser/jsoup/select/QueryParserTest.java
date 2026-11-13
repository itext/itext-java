/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

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
        assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        assertEquals(3, or.evaluators.size());
        for (Evaluator innerEval: or.evaluators) {
            assertTrue(innerEval instanceof CombiningEvaluator.And);
            CombiningEvaluator.And and = (CombiningEvaluator.And) innerEval;
            assertEquals(2, and.evaluators.size());
            assertTrue(and.evaluators.get(0) instanceof Evaluator.Tag);
            assertTrue(and.evaluators.get(1) instanceof StructuralEvaluator.Parent);
        }
    }

    @Test public void testParsesMultiCorrectly() {
        Evaluator eval = QueryParser.parse(".foo > ol, ol > li + li");
        assertTrue(eval instanceof CombiningEvaluator.Or);
        CombiningEvaluator.Or or = (CombiningEvaluator.Or) eval;
        assertEquals(2, or.evaluators.size());

        CombiningEvaluator.And andLeft = (CombiningEvaluator.And) or.evaluators.get(0);
        CombiningEvaluator.And andRight = (CombiningEvaluator.And) or.evaluators.get(1);

        assertEquals("ol :ImmediateParent.foo", andLeft.toString());
        assertEquals(2, andLeft.evaluators.size());
        assertEquals("li :prevli :ImmediateParentol", andRight.toString());
        assertEquals(2, andLeft.evaluators.size());
    }
}
