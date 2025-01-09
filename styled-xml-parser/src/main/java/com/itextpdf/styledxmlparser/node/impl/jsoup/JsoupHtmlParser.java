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
package com.itextpdf.styledxmlparser.node.impl.jsoup;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Comment;
import com.itextpdf.styledxmlparser.jsoup.nodes.DataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.DocumentType;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDataNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentTypeNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that uses JSoup to parse HTML.
 */
public class JsoupHtmlParser implements IXmlParser {

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(JsoupHtmlParser.class);

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.IXmlParser#parse(java.io.InputStream, java.lang.String)
     */
    @Override
    public IDocumentNode parse(InputStream htmlStream, String charset) throws IOException {
        // Based on some brief investigations, it seems that Jsoup uses baseUri for resolving relative uri's into absolute
        // on user demand. We perform such resolving in ResourceResolver class, therefore it is not needed here.
        String baseUri = "";
        Document doc = Jsoup.parse(htmlStream, charset, baseUri);
        INode result = wrapJsoupHierarchy(doc);
        if (result instanceof IDocumentNode) {
            return (IDocumentNode) result;
        } else {
            throw new IllegalStateException();
        }
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.IXmlParser#parse(java.lang.String)
     */
    @Override
    public IDocumentNode parse(String html) {
        Document doc = Jsoup.parse(html);
        INode result = wrapJsoupHierarchy(doc);
        if (result instanceof IDocumentNode) {
            return (IDocumentNode) result;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Wraps JSoup nodes into pdfHTML {@link INode} classes.
     *
     * @param jsoupNode the JSoup node instance
     * @return the {@link INode} instance
     */
    private INode wrapJsoupHierarchy(Node jsoupNode) {
        INode resultNode = null;
        if (jsoupNode instanceof Document) {
            resultNode = new JsoupDocumentNode((Document) jsoupNode) ;
        } else if (jsoupNode instanceof TextNode) {
            resultNode = new JsoupTextNode((TextNode) jsoupNode);
        } else if (jsoupNode instanceof Element) {
            resultNode = new JsoupElementNode((Element) jsoupNode);
        } else if (jsoupNode instanceof DataNode) {
            resultNode = new JsoupDataNode((DataNode) jsoupNode);
        } else if (jsoupNode instanceof DocumentType) {
            resultNode = new JsoupDocumentTypeNode((DocumentType) jsoupNode);
        } else if (jsoupNode instanceof Comment) {
        } else {
            logger.error(MessageFormatUtil.format("Could not map node type: {0}", jsoupNode.getClass()));
        }

        for (Node node : jsoupNode.childNodes()) {
            INode childNode = wrapJsoupHierarchy(node);
            if (childNode != null) {
                resultNode.addChild(childNode);
            }
        }

        return resultNode;
    }
}
