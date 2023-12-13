/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.styledxmlparser.jsoup.safety;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.helper.Validate;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.DataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.jsoup.select.NodeTraversor;
import com.itextpdf.styledxmlparser.jsoup.select.NodeVisitor;


/**
 * The whitelist based HTML cleaner. Use to ensure that end-user provided HTML contains only the elements and attributes
 * that you are expecting; no junk, and no cross-site scripting attacks!
 * <p>
 * The HTML cleaner parses the input as HTML and then runs it through a white-list, so the output HTML can only contain
 * HTML that is allowed by the whitelist.
 * <p>
 * It is assumed that the input HTML is a body fragment; the clean methods only pull from the source's body, and the
 * canned white-lists only allow body contained tags.
 * <p>
 * Rather than interacting directly with a Cleaner object, generally see the {@code clean} methods in {@link Jsoup}.
 */
public class Cleaner {
    private Whitelist whitelist;

    /**
     * Create a new cleaner, that sanitizes documents using the supplied whitelist.
     * @param whitelist white-list to clean with
     */
    public Cleaner(Whitelist whitelist) {
        Validate.notNull(whitelist);
        this.whitelist = whitelist;
    }

    /**
     * Creates a new, clean document, from the original dirty document, containing only elements allowed by the whitelist.
     * The original document is not modified. Only elements from the dirt document's <code>body</code> are used.
     * @param dirtyDocument Untrusted base document to clean.
     * @return cleaned document.
     */
    public Document clean(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        if (dirtyDocument.body() != null) // frameset documents won't have a body. the clean doc will have empty body.
            copySafeNodes(dirtyDocument.body(), clean.body());

        return clean;
    }

    /**
     * Determines if the input document is valid, against the whitelist. It is considered valid if all the tags and attributes
     * in the input HTML are allowed by the whitelist.
     * <p>
     * This method can be used as a validator for user input forms. An invalid document will still be cleaned successfully
     * using the {@link #clean(Document)} document. If using as a validator, it is recommended to still clean the document
     * to ensure enforced attributes are set correctly, and that the output is tidied.
     *
     * @param dirtyDocument document to test
     * @return true if no tags or attributes need to be removed; false if they do
     */
    public boolean isValid(Document dirtyDocument) {
        Validate.notNull(dirtyDocument);

        Document clean = Document.createShell(dirtyDocument.baseUri());
        int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
        return numDiscarded == 0;
    }

    /**
     * Iterates the input and copies trusted nodes (tags, attributes, text) into the destination.
     */
    private final class CleaningVisitor implements NodeVisitor {
        int numDiscarded = 0;
        final Element root;
        Element destination; // current element to append nodes to

        CleaningVisitor(Element root, Element destination) {
            this.root = root;
            this.destination = destination;
        }

        public void head(Node source, int depth) {
            if (source instanceof Element) {
                Element sourceEl = (Element) source;

                if (whitelist.isSafeTag(sourceEl.tagName())) { // safe, clone and copy safe attrs
                    ElementMeta meta = createSafeElement(sourceEl);
                    Element destChild = meta.el;
                    destination.appendChild(destChild);

                    numDiscarded += meta.numAttribsDiscarded;
                    destination = destChild;
                } else if (source != root) { // not a safe tag, so don't add. don't count root against discarded.
                    numDiscarded++;
                }
            } else if (source instanceof TextNode) {
                TextNode sourceText = (TextNode) source;
                TextNode destText = new TextNode(sourceText.getWholeText(), source.baseUri());
                destination.appendChild(destText);
            } else if (source instanceof DataNode && whitelist.isSafeTag(source.parent().nodeName())) {
              DataNode sourceData = (DataNode) source;
              DataNode destData = new DataNode(sourceData.getWholeData(), source.baseUri());
              destination.appendChild(destData);
            } else { // else, we don't care about comments, xml proc instructions, etc
                numDiscarded++;
            }
        }

        public void tail(Node source, int depth) {
            if (source instanceof Element && whitelist.isSafeTag(source.nodeName())) {
                destination = (Element) destination.parent(); // would have descended, so pop destination stack
            }
        }
    }

    private int copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest);
        NodeTraversor traversor = new NodeTraversor(cleaningVisitor);
        traversor.traverse(source);
        return cleaningVisitor.numDiscarded;
    }

    private ElementMeta createSafeElement(Element sourceEl) {
        String sourceTag = sourceEl.tagName();
        Attributes destAttrs = new Attributes();
        Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
        int numDiscarded = 0;

        Attributes sourceAttrs = sourceEl.attributes();
        for (Attribute sourceAttr : sourceAttrs) {
            if (whitelist.isSafeAttribute(sourceTag, sourceEl, sourceAttr))
                destAttrs.put(sourceAttr);
            else
                numDiscarded++;
        }
        Attributes enforcedAttrs = whitelist.getEnforcedAttributes(sourceTag);
        destAttrs.addAll(enforcedAttrs);

        return new ElementMeta(dest, numDiscarded);
    }

    private static class ElementMeta {
        Element el;
        int numAttribsDiscarded;

        ElementMeta(Element el, int numAttribsDiscarded) {
            this.el = el;
            this.numAttribsDiscarded = numAttribsDiscarded;
        }
    }

}
