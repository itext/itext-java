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

import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Utility class providing methods for traversing and filtering nodes
 * in a tree structure, particularly focusing on element nodes based
 * on various criteria. This class serves as a helper for operations
 * related to CSS selector processing.
 */
final class CssSelectorItemTraversalUtil {

    private CssSelectorItemTraversalUtil() {
        // utility class
    }

    static boolean isValidElementNode(INode node) {
        return node instanceof IElementNode
                && !(node instanceof ICustomElementNode)
                && !(node instanceof IDocumentNode);
    }

    static List<INode> getElementSiblings(INode node) {
        INode parent = node != null ? node.parentNode() : null;
        return parent != null ? getElementChildren(parent) : Collections.<INode>emptyList();
    }

    static List<INode> getElementSiblingsOfSameType(INode node) {
        if (!(node instanceof IElementNode)) {
            return Collections.<INode>emptyList();
        }
        INode parent = node.parentNode();
        if (parent == null) {
            return Collections.<INode>emptyList();
        }

        List<INode> children = parent.childNodes();
        if (children == null || children.isEmpty()) {
            return Collections.<INode>emptyList();
        }

        List<INode> result = new ArrayList<>(children.size());
        String name = ((IElementNode) node).name();
        for (INode child : children) {
            if (child instanceof IElementNode && name.equals(((IElementNode) child).name())) {
                result.add(child);
            }
        }

        return result;
    }

    static INode getNextElementSibling(INode node) {
        INode parent = node != null ? node.parentNode() : null;
        if (parent == null) {
            return null;
        }

        boolean afterNode = false;
        for (INode sibling : parent.childNodes()) {
            if (!afterNode) {
                afterNode = (sibling == node);
                continue;
            }
            if (sibling instanceof IElementNode) {
                return sibling;
            }
        }
        return null;
    }

    static void forEachFollowingElementSibling(INode node, Consumer<INode> action) {
        INode parent = node != null ? node.parentNode() : null;
        if (parent == null) {
            return;
        }

        boolean afterNode = false;
        for (INode sibling : parent.childNodes()) {
            if (!afterNode) {
                afterNode = (sibling == node);
                continue;
            }
            if (sibling instanceof IElementNode) {
                action.accept(sibling);
            }
        }
    }

    static boolean anyDescendantElementMatches(INode scope, Predicate<INode> predicate) {
        if (scope == null) {
            return false;
        }

        Stack<INode> stack = new Stack<>();
        for (INode child : scope.childNodes()) {
            if (child instanceof IElementNode) {
                stack.push(child);
            }
        }

        while (!stack.isEmpty()) {
            INode candidate = stack.pop();
            if (predicate.test(candidate)) {
                return true;
            }
            for (INode child : candidate.childNodes()) {
                if (child instanceof IElementNode) {
                    stack.push(child);
                }
            }
        }

        return false;
    }

    static void forEachDescendantElement(INode scope, Consumer<INode> action) {
        if (scope == null) {
            return;
        }

        Stack<INode> stack = new Stack<>();
        for (INode child : scope.childNodes()) {
            if (child instanceof IElementNode) {
                stack.push(child);
            }
        }

        while (!stack.isEmpty()) {
            INode candidate = stack.pop();
            action.accept(candidate);
            for (INode child : candidate.childNodes()) {
                if (child instanceof IElementNode) {
                    stack.push(child);
                }
            }
        }
    }

    private static List<INode> getElementChildren(INode parent) {
        if (parent == null) {
            return Collections.<INode>emptyList();
        }
        List<INode> children = parent.childNodes();
        if (children == null || children.isEmpty()) {
            return Collections.<INode>emptyList();
        }
        List<INode> result = new ArrayList<>(children.size());
        for (INode child : children) {
            if (child instanceof IElementNode) {
                result.add(child);
            }
        }
        return result;
    }
}
