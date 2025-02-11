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
package com.itextpdf.styledxmlparser.jsoup.select;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;

/**
 * Collects a list of elements that match the supplied criteria.
 */
public class Collector {

    private Collector() {}

    /**
     Build a list of elements, by visiting root and every descendant of root, and testing it against the evaluator.
     @param eval Evaluator to test elements against
     @param root root of tree to descend
     @return list of matches; empty if none
     */
    public static Elements collect (Evaluator eval, Element root) {
        Elements elements = new Elements();
        NodeTraversor.traverse(new Accumulator(root, elements, eval), root);
        return elements;
    }

    private static class Accumulator implements NodeVisitor {
        private final Element root;
        private final Elements elements;
        private final Evaluator eval;

        Accumulator(Element root, Elements elements, Evaluator eval) {
            this.root = root;
            this.elements = elements;
            this.eval = eval;
        }

        public void head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(root, el))
                    elements.add(el);
            }
        }

        public void tail(Node node, int depth) {
            // void
        }
    }

    /**
     Finds the first Element that matches the Evaluator that descends from the root, and stops the query once that first
     match is found.
     @param eval Evaluator to test elements against
     @param root root of tree to descend
     @return the first match; {@code null} if none
     */
    public static Element findFirst(Evaluator eval, Element root) {
        FirstFinder finder = new FirstFinder(root, eval);
        NodeTraversor.filter(finder, root);
        return finder.match;
    }

    private static class FirstFinder implements NodeFilter {
        Element match = null;

        private final Element root;
        private final Evaluator eval;

        FirstFinder(Element root, Evaluator eval) {
            this.root = root;
            this.eval = eval;
        }

        @Override
        public FilterResult head(Node node, int depth) {
            if (node instanceof Element) {
                Element el = (Element) node;
                if (eval.matches(root, el)) {
                    match = el;
                    return FilterResult.STOP;
                }
            }
            return FilterResult.CONTINUE;
        }

        @Override
        public FilterResult tail(Node node, int depth) {
            return FilterResult.CONTINUE;
        }
    }

}
