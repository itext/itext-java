/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.jsoup.nodes;

import java.io.IOException;

import com.itextpdf.styledxmlparser.jsoup.helper.Validate;
import com.itextpdf.styledxmlparser.jsoup.helper.StringUtil;

/**
 A text node.

 @author Jonathan Hedley, jonathan@hedley.net */
public class TextNode extends Node {
    /*
    TextNode is a node, and so by default comes with attributes and children. The attributes are seldom used, but use
    memory, and the child nodes are never used. So we don't have them, and override accessors to attributes to create
    them as needed on the fly.
     */
    private static final String TEXT_KEY = "text";
    String text;

    /**
     Create a new TextNode representing the supplied (unencoded) text).

     @param text raw text
     @param baseUri base uri
     @see #createFromEncoded(String, String)
     */
    public TextNode(String text, String baseUri) {
        this.baseUri = baseUri;
        this.text = text;
    }

	public String nodeName() {
        return "#text";
    }
    
    /**
     * Get the text content of this text node.
     * @return Unencoded, normalised text.
     * @see TextNode#getWholeText()
     */
    public String text() {
        return normaliseWhitespace(getWholeText());
    }
    
    /**
     * Set the text content of this text node.
     * @param text unencoded text
     * @return this, for chaining
     */
    public TextNode text(String text) {
        this.text = text;
        if (attributes != null)
            attributes.put(TEXT_KEY, text);
        return this;
    }

    /**
     Get the (unencoded) text of this text node, including any newlines and spaces present in the original.
     @return text
     */
    public String getWholeText() {
        return attributes == null ? text : attributes.get(TEXT_KEY);
    }

    /**
     Test if this text node is blank -- that is, empty or only whitespace (including newlines).
     @return true if this document is empty or only whitespace, false if it contains any text content.
     */
    public boolean isBlank() {
        return StringUtil.isBlank(getWholeText());
    }

    /**
     * Split this text node into two nodes at the specified string offset. After splitting, this node will contain the
     * original text up to the offset, and will have a new text node sibling containing the text after the offset.
     * @param offset string offset point to split node at.
     * @return the newly created text node containing the text after the offset.
     */
    public TextNode splitText(int offset) {
        Validate.isTrue(offset >= 0, "Split offset must be not be negative");
        Validate.isTrue(offset < text.length(), "Split offset must not be greater than current text length");

        String head = getWholeText().substring(0, offset);
        String tail = getWholeText().substring(offset);
        text(head);
        TextNode tailNode = new TextNode(tail, this.baseUri());
        if (parent() != null)
            parent().addChildren(siblingIndex()+1, tailNode);

        return tailNode;
    }

	void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (out.prettyPrint() && ((siblingIndex() == 0 && parentNode instanceof Element && ((Element) parentNode).tag().formatAsBlock() && !isBlank()) || (out.outline() && siblingNodes().size()>0 && !isBlank()) ))
            indent(accum, depth, out);

        boolean normaliseWhite = out.prettyPrint() && parent() instanceof Element
                && !Element.preserveWhitespace(parent());
        Entities.escape(accum, getWholeText(), out, false, normaliseWhite, false);
    }

	void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {}

    @Override
    public String toString() {
        return outerHtml();
    }

    /**
     * Create a new TextNode from HTML encoded (aka escaped) data.
     * @param encodedText Text containing encoded HTML (e.g. &amp;lt;)
     * @param baseUri Base uri
     * @return TextNode containing unencoded data (e.g. &lt;)
     */
    public static TextNode createFromEncoded(String encodedText, String baseUri) {
        String text = Entities.unescape(encodedText);
        return new TextNode(text, baseUri);
    }

    static String normaliseWhitespace(String text) {
        text = StringUtil.normaliseWhitespace(text);
        return text;
    }

    static String stripLeadingWhitespace(String text) {
        return text.replaceFirst("^\\s+", "");
    }

    static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ';
    }

    // attribute fiddling. create on first access.
    private void ensureAttributes() {
        if (attributes == null) {
            attributes = new Attributes();
            attributes.put(TEXT_KEY, text);
        }
    }

    @Override
    public String attr(String attributeKey) {
        ensureAttributes();
        return super.attr(attributeKey);
    }

    @Override
    public Attributes attributes() {
        ensureAttributes();
        return super.attributes();
    }

    @Override
    public Node attr(String attributeKey, String attributeValue) {
        ensureAttributes();
        return super.attr(attributeKey, attributeValue);
    }

    @Override
    public boolean hasAttr(String attributeKey) {
        ensureAttributes();
        return super.hasAttr(attributeKey);
    }

    @Override
    public Node removeAttr(String attributeKey) {
        ensureAttributes();
        return super.removeAttr(attributeKey);
    }

    @Override
    public String absUrl(String attributeKey) {
        ensureAttributes();
        return super.absUrl(attributeKey);
    }
}
