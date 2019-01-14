/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.styledxmlparser.node.impl.jsoup;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.LogMessageConstant;
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
public class JsoupXmlParser implements IXmlParser {

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(JsoupXmlParser.class);

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.html.IXmlParser#parse(java.io.InputStream, java.lang.String)
     */
    @Override
    public IDocumentNode parse(InputStream xmlStream, String charset) throws IOException {
        // Based on some brief investigations, it seems that Jsoup uses baseUri for resolving relative uri's into absolute
        // on user demand. We perform such resolving in ResourceResolver class, therefore it is not needed here.
        String baseUri = "";
        Document doc = Jsoup.parseXML(xmlStream, charset, baseUri);
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
    public IDocumentNode parse(String xml) {
        Document doc = Jsoup.parseXML(xml);
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
            logger.error(MessageFormatUtil.format(LogMessageConstant.ERROR_PARSING_COULD_NOT_MAP_NODE, jsoupNode.getClass()));
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
