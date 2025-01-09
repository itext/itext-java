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
 * Node visitor interface. Provide an implementing class to {@link NodeTraversor} to iterate through nodes.
 * <p>
 * This interface provides two methods, {@code head} and {@code tail}. The head method is called when the node is first
 * seen, and the tail method when all of the node's children have been visited. As an example, {@code head} can be used to
 * emit a start tag for a node, and {@code tail} to create the end tag.
 */
public interface NodeVisitor {
    /**
     * Callback for when a node is first visited.
     * <p>
     * The node may be modified (e.g. {@link Node#attr(String)} or replaced {@link Node#replaceWith(Node)}). If it's
     * {@code instanceOf Element}, you may cast it to an {@link Element} and access those methods.
     * <p>
     * Note that nodes may not be removed during traversal using this method; use {@link
     * NodeTraversor#filter(NodeFilter, Node)} with a {@link NodeFilter.FilterResult#REMOVE} return instead.
     *
     * @param node  the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node
     *              of that will have depth 1.
     */
    void head(Node node, int depth);

    /**
     * Callback for when a node is last visited, after all of its descendants have been visited.
     * <p>
     * Note that replacement with {@link Node#replaceWith(Node)}
     * is not supported in {@code tail}.
     *
     * @param node  the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node
     *              of that will have depth 1.
     */
    void tail(Node node, int depth);
}
