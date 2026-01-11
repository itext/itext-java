/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.styledxmlparser.css.selector.ICssSelector;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A class representing a CSS pseudo-class selector item for the ":has()" CSS pseudo-class.
 * This class is responsible for evaluating whether a given node matches the conditions
 * defined in the ":has()" selector.
 * <p>
 * The primary responsibility of this class is handling the logic for matching nodes based on
 * argument selectors and compiled versions of those selectors. The ":has()" pseudo-class takes
 * other selectors as its arguments, and it matches elements that have descendants or other
 * relative elements matching the provided criteria.
 */
class CssPseudoClassHasSelectorItem extends CssPseudoClassSelectorItem {
    private final List<CompiledHasArgument> compiledArguments;

    CssPseudoClassHasSelectorItem(List<ICssSelector> argumentSelectors, String argumentsString) {
        super(CommonCssConstants.HAS, argumentsString);
        this.compiledArguments = compileArguments(argumentSelectors);
    }

    CssPseudoClassHasSelectorItem(ICssSelector argumentsSelector) {
        this(Collections.singletonList(argumentsSelector), argumentsSelector.toString());
    }

    @Override
    public int getSpecificity() {
        int maxSpecificity = 0;
        for (CompiledHasArgument compiled : compiledArguments) {
            if (compiled.getSelector() != null) {
                maxSpecificity = Math.max(maxSpecificity, compiled.getSelector().calculateSpecificity());
            }
        }
        return maxSpecificity;
    }

    @Override
    public boolean matches(INode node) {
        if (!CssSelectorItemTraversalUtil.isValidElementNode(node)) {
            return false;
        }

        for (CompiledHasArgument compiledArgument : compiledArguments) {
            if (matchesCompiled(node, compiledArgument)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesCompiled(INode node, CompiledHasArgument compiled) {
        return compiled.getRelativeSteps() != null
                ? matchesRelativeSelector(node, compiled.getRelativeSteps())
                : hasDescendantMatching(node, compiled.getSelector());
    }

    private static List<CompiledHasArgument> compileArguments(List<ICssSelector> argumentSelectors) {
        List<CompiledHasArgument> result = new ArrayList<>(argumentSelectors.size());
        for (ICssSelector selector : argumentSelectors) {
            final CompiledHasArgument compiled;
            if (!(selector instanceof CssSelector)) {
                compiled = new CompiledHasArgument(selector, null);
            } else {
                CssSelector cssSelector = (CssSelector) selector;
                List<ICssSelectorItem> items = cssSelector.getSelectorItems();
                if (items.isEmpty()) {
                    compiled = new CompiledHasArgument(null, null);
                } else if (items.get(0) instanceof CssSeparatorSelectorItem) {
                    List<RelativeStep> steps = compileRelativeSteps(items);
                    compiled = new CompiledHasArgument(cssSelector, steps);
                } else {
                    compiled = new CompiledHasArgument(cssSelector, null);
                }
            }
            result.add(compiled);
        }

        return result;
    }

    private static List<RelativeStep> compileRelativeSteps(List<ICssSelectorItem> relativeItems) {
        int i = 0;
        List<RelativeStep> steps = new ArrayList<>();

        while (i < relativeItems.size()) {
            ICssSelectorItem item = relativeItems.get(i);
            if (!(item instanceof CssSeparatorSelectorItem)) {
                return Collections.<RelativeStep>emptyList();
            }

            i++;

            int seqStart = i;
            while (i < relativeItems.size() && !(relativeItems.get(i) instanceof CssSeparatorSelectorItem)) {
                i++;
            }

            if (seqStart == i) {
                return Collections.<RelativeStep>emptyList();
            }

            CssSelector sequenceSelector = new CssSelector(relativeItems.subList(seqStart, i));
            steps.add(new RelativeStep(((CssSeparatorSelectorItem) item).getSeparator(), sequenceSelector));
        }

        return steps;
    }

    private static boolean matchesRelativeSelector(INode scope, List<RelativeStep> steps) {
        if (steps.isEmpty()) {
            return false;
        }

        List<INode> currentScopes = Collections.singletonList(scope);

        for (RelativeStep step : steps) {
            List<INode> nextScopes = new ArrayList<>();

            for (INode currentScope : currentScopes) {
                fillNextScopesByCombinator(currentScope, step.getCombinator(), step.getSelector(), nextScopes);
            }

            if (nextScopes.isEmpty()) {
                return false;
            }
            currentScopes = nextScopes;
        }

        return true;
    }

    private static void fillNextScopesByCombinator(INode scope, char combinator, CssSelector sequenceSelector,
                                                   List<INode> nextScopes) {
        switch (combinator) {
            case '>':
                for (INode child : scope.childNodes()) {
                    if (child instanceof IElementNode && sequenceSelector.matches(child)) {
                        nextScopes.add(child);
                    }
                }
                return;

            case ' ':
                CssSelectorItemTraversalUtil.forEachDescendantElement(scope, candidate -> {
                    if (sequenceSelector.matches(candidate)) {
                        nextScopes.add(candidate);
                    }
                });
                return;

            case '+':
                INode next = CssSelectorItemTraversalUtil.getNextElementSibling(scope);
                if (next != null && sequenceSelector.matches(next)) {
                    nextScopes.add(next);
                }
                return;

            case '~':
                CssSelectorItemTraversalUtil.forEachFollowingElementSibling(scope, sibling -> {
                    if (sequenceSelector.matches(sibling)) {
                        nextScopes.add(sibling);
                    }
                });
                return;

            default:
        }
    }

    private static boolean hasDescendantMatching(INode scope, ICssSelector selector) {
        if (selector == null) {
            return false;
        }

        if (selector instanceof CssSelector) {
            CssSelector cssSelector = (CssSelector) selector;
            return CssSelectorItemTraversalUtil.anyDescendantElementMatches(scope,
                    (candidate) -> cssSelector.matchesWithinScope(candidate, scope));
        }

        return CssSelectorItemTraversalUtil.anyDescendantElementMatches(
                scope, (candidate) -> selector.matches(candidate));
    }

    private static final class CompiledHasArgument {
        private final ICssSelector selector;
        private final List<RelativeStep> relativeSteps;

        public CompiledHasArgument(ICssSelector selector, List<RelativeStep> relativeSteps) {
            this.selector = selector;
            this.relativeSteps = relativeSteps;
        }

        public ICssSelector getSelector() {
            return selector;
        }

        public List<RelativeStep> getRelativeSteps() {
            return relativeSteps;
        }
    }

    private static final class RelativeStep {
        private final char combinator;
        private final CssSelector selector;

        public RelativeStep(char combinator, CssSelector selector) {
            this.combinator = combinator;
            this.selector = selector;
        }

        public char getCombinator() {
            return combinator;
        }

        public CssSelector getSelector() {
            return selector;
        }
    }
}
