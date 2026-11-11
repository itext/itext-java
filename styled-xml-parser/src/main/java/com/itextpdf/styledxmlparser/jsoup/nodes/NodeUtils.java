/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.parser.HtmlTreeBuilder;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;

/**
 * Internal helpers for Nodes, to keep the actual node APIs relatively clean. A jsoup internal class, so don't use it as
 * there is no contract API).
 */
final class NodeUtils {
    /**
     * Get the output setting for this node,  or if this node has no document (or parent), retrieve the default output
     * settings
     */
    static Document.OutputSettings outputSettings(Node node) {
        Document owner = node.ownerDocument();
        return owner != null ? owner.outputSettings() : (new Document("")).outputSettings();
    }

    /**
     * Get the parser that was used to make this node, or the default HTML parser if it has no parent.
     */
    static Parser parser(Node node) {
        Document doc = node.ownerDocument();
        return doc != null && doc.parser() != null ? doc.parser() : new Parser(new HtmlTreeBuilder());
    }
}
