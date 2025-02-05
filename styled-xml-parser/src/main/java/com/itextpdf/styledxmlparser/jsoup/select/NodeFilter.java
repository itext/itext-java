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

import com.itextpdf.styledxmlparser.jsoup.nodes.Node;

/**
 * Node filter interface. Provide an implementing class to {@link NodeTraversor} to iterate through nodes.
 * <p>
 * This interface provides two methods, {@code head} and {@code tail}. The head method is called when the node is first
 * seen, and the tail method when all of the node's children have been visited. As an example, head can be used to
 * create a start tag for a node, and tail to create the end tag.
 * <p>
 * For every node, the filter has to decide whether to
 * <ul>
 * <li>continue ({@link FilterResult#CONTINUE}),
 * <li>skip all children ({@link FilterResult#SKIP_CHILDREN}),
 * <li>skip node entirely ({@link FilterResult#SKIP_ENTIRELY}),
 * <li>remove the subtree ({@link FilterResult#REMOVE}),
 * <li>interrupt the iteration and return ({@link FilterResult#STOP}).
 * </ul>
 * The difference between {@link FilterResult#SKIP_CHILDREN} and {@link FilterResult#SKIP_ENTIRELY} is that the first
 * will invoke {@link NodeFilter#tail(Node, int)} on the node, while the latter will not.
 * Within {@link NodeFilter#tail(Node, int)}, both are equivalent to {@link FilterResult#CONTINUE}.
 */
public interface NodeFilter {
    /**
     * Filter decision.
     */
    enum FilterResult {
        /** Continue processing the tree */
        CONTINUE,
        /** Skip the child nodes, but do call {@link NodeFilter#tail(Node, int)} next. */
        SKIP_CHILDREN,
        /** Skip the subtree, and do not call {@link NodeFilter#tail(Node, int)}. */
        SKIP_ENTIRELY,
        /** Remove the node and its children */
        REMOVE,
        /** Stop processing */
        STOP
    }

    /**
     * Callback for when a node is first visited.
     * @param node the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node of that will have depth 1.
     * @return Filter decision
     */
    FilterResult head(Node node, int depth);

    /**
     * Callback for when a node is last visited, after all of its descendants have been visited.
     * @param node the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node of that will have depth 1.
     * @return Filter decision
     */
    FilterResult tail(Node node, int depth);
}
