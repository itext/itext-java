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
package com.itextpdf.svg.css.impl;

import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.CssFontFaceRule;
import com.itextpdf.styledxmlparser.css.CssStatement;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.css.media.CssMediaRule;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.parse.CssStyleSheetParser;
import com.itextpdf.styledxmlparser.css.resolve.AbstractCssContext;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IDataNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.IStylesContainer;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of SVG`s styles and attribute resolver .
 */
public class SvgStyleResolver implements ICssResolver {

    private CssStyleSheet css;
    private static final String DEFAULT_CSS_PATH = "com/itextpdf/svg/default.css";

    /**
     * The device description.
     */
    private MediaDeviceDescription deviceDescription;

    /**
     * The list of fonts.
     */
    private List<CssFontFaceRule> fonts = new ArrayList<>();

    /**
     * The style-resolver util responsible for resolving inheritance rules
     */
    private StyleResolverUtil sru = new StyleResolverUtil();

    /**
     * The resource resolver
     */
    private ResourceResolver resourceResolver = new ResourceResolver("");

    /**
     * Creates a {@link SvgStyleResolver} with a given default CSS.
     *
     * @param defaultCssStream the default CSS
     */
    public SvgStyleResolver(InputStream defaultCssStream) throws IOException {
        this.css = CssStyleSheetParser.parse(defaultCssStream);
    }

    /**
     * Creates a SvgStyleResolver.
     */
    public SvgStyleResolver() {
        try (InputStream defaultCss = ResourceUtil.getResourceStream(DEFAULT_CSS_PATH)) {
            this.css = CssStyleSheetParser.parse(defaultCss);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.warn(SvgLogMessageConstant.ERROR_INITIALIZING_DEFAULT_CSS, e);
            this.css = new CssStyleSheet();
        }
    }

    /**
     * Creates a SvgStyleResolver. This constructor will instantiate its internal style sheet and it
     * will collect the css declarations from the provided node.
     *
     * @param rootNode node to collect css from
     * @param context  the processor context
     */
    public SvgStyleResolver(INode rootNode, SvgProcessorContext context) {
        // TODO DEVSIX-2060. Fetch default styles first.
        this.deviceDescription = context.getDeviceDescription();
        this.resourceResolver = context.getResourceResolver();
        collectCssDeclarations(rootNode, context.getResourceResolver());
        collectFonts();
    }

    @Override
    public Map<String, String> resolveStyles(INode node, AbstractCssContext context) {
        Map<String, String> styles = new HashMap<>();
        //Load in from collected style sheets
        List<CssDeclaration> styleSheetDeclarations =
                css.getCssDeclarations(node, MediaDeviceDescription.createDefault());
        for (CssDeclaration ssd : styleSheetDeclarations) {
            styles.put(ssd.getProperty(), ssd.getExpression());
        }

        //Load in attributes declarations
        if (node instanceof IElementNode) {
            IElementNode eNode = (IElementNode) node;
            for (IAttribute attr : eNode.getAttributes()) {
                processAttribute(attr, styles);
            }
        }

        //Load in and merge inherited declarations from parent
        if (node.parentNode() instanceof IStylesContainer) {
            IStylesContainer parentNode = (IStylesContainer) node.parentNode();
            Map<String, String> parentStyles = parentNode.getStyles();

            if (parentStyles == null && !(node.parentNode() instanceof IDocumentNode)) {
                Logger logger = LoggerFactory.getLogger(SvgStyleResolver.class);
                logger.error(LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES);
            }
            if (parentStyles != null) {
                for (Map.Entry<String, String> entry : parentStyles.entrySet()) {
                    String parentFontSizeString = parentStyles.get(CommonCssConstants.FONT_SIZE);
                    if (parentFontSizeString == null) {
                        parentFontSizeString = "0";
                    }
                    sru.mergeParentStyleDeclaration(styles, entry.getKey(), entry.getValue(), parentFontSizeString);
                }
            }
        }
        return styles;
    }

    /**
     * Resolves the full path of Link href attribute,
     * thanks to the resource resolver.
     *
     * @param attr          attribute to process
     * @param attributesMap
     */
    private void processXLink(final IAttribute attr, final Map<String, String> attributesMap) {
        String xlinkValue = attr.getValue();
        if (!isStartedWithHash(xlinkValue)) {
            try {
                xlinkValue = this.resourceResolver.resolveAgainstBaseUri(attr.getValue()).toExternalForm();
            } catch (MalformedURLException mue) {
                Logger logger = LoggerFactory.getLogger(SvgStyleResolver.class);
                logger.error(LogMessageConstant.UNABLE_TO_RESOLVE_IMAGE_URL, mue);
            }
        }
        attributesMap.put(attr.getKey(), xlinkValue);
    }

    /**
     * Checks if string starts with #.
     *
     * @param s test string
     * @return
     */
    private boolean isStartedWithHash(String s) {
        return s != null && s.startsWith("#");
    }

    private void collectCssDeclarations(INode rootNode, ResourceResolver resourceResolver) {
        this.css = new CssStyleSheet();
        LinkedList<INode> q = new LinkedList<>();
        if (rootNode != null) {
            q.add(rootNode);
        }
        while (!q.isEmpty()) {
            INode currentNode = q.pop();
            if (currentNode instanceof IElementNode) {
                IElementNode headChildElement = (IElementNode) currentNode;
                if (SvgConstants.Tags.STYLE.equals(headChildElement.name())) {//XML parser will parse style tag contents as text nodes
                    if (!currentNode.childNodes().isEmpty() && (currentNode.childNodes().get(0) instanceof IDataNode ||
                            currentNode.childNodes().get(0) instanceof ITextNode)) {
                        String styleData;
                        if (currentNode.childNodes().get(0) instanceof IDataNode) {
                            // TODO (RND-865)
                            styleData = ((IDataNode) currentNode.childNodes().get(0)).getWholeData();
                        } else {
                            styleData = ((ITextNode) currentNode.childNodes().get(0)).wholeText();
                        }
                        CssStyleSheet styleSheet = CssStyleSheetParser.parse(styleData);
                        //TODO(RND-863): media query wrap
                        //styleSheet = wrapStyleSheetInMediaQueryIfNecessary(headChildElement, styleSheet);
                        this.css.appendCssStyleSheet(styleSheet);
                    }

                } else if (SvgCssUtils.isStyleSheetLink(headChildElement)) {
                    String styleSheetUri = headChildElement.getAttribute(SvgConstants.Attributes.HREF);
                    try {
                        InputStream stream = resourceResolver.retrieveStyleSheet(styleSheetUri);
                        byte[] bytes = StreamUtil.inputStreamToArray(stream);

                        CssStyleSheet styleSheet = CssStyleSheetParser.parse(new ByteArrayInputStream(bytes), resourceResolver.resolveAgainstBaseUri(styleSheetUri).toExternalForm());
                        this.css.appendCssStyleSheet(styleSheet);
                    } catch (IOException exc) {
                        Logger logger = LoggerFactory.getLogger(SvgStyleResolver.class);
                        logger.error(LogMessageConstant.UNABLE_TO_PROCESS_EXTERNAL_CSS_FILE, exc);
                    }
                }
            }
            for (INode child : currentNode.childNodes()) {
                if (child instanceof IElementNode) {
                    q.add(child);
                }
            }
        }
    }

    /**
     * Gets the list of fonts.
     *
     * @return the list of {@link CssFontFaceRule} instances
     */
    public List<CssFontFaceRule> getFonts() {
        return new ArrayList<>(fonts);
    }

    /**
     * Collects fonts from the style sheet.
     */
    private void collectFonts() {
        for (CssStatement cssStatement : css.getStatements()) {
            collectFonts(cssStatement);
        }
    }

    /**
     * Collects fonts from a {@link CssStatement}.
     *
     * @param cssStatement the CSS statement
     */
    private void collectFonts(CssStatement cssStatement) {
        if (cssStatement instanceof CssFontFaceRule) {
            fonts.add((CssFontFaceRule) cssStatement);
        } else if (cssStatement instanceof CssMediaRule &&
                ((CssMediaRule) cssStatement).matchMediaDevice(deviceDescription)) {
            for (CssStatement cssSubStatement : ((CssMediaRule) cssStatement).getStatements()) {
                collectFonts(cssSubStatement);
            }
        }
    }

    private void processAttribute(IAttribute attr, Map<String, String> styles) {
        //Style attribute needs to be parsed further
        switch (attr.getKey()) {
            case SvgConstants.Attributes.STYLE:
                Map<String, String> parsed = parseStylesFromStyleAttribute(attr.getValue());
                for (Map.Entry<String, String> style : parsed.entrySet()) {
                    styles.put(style.getKey(), style.getValue());
                }
                break;
            case SvgConstants.Attributes.XLINK_HREF:
                processXLink(attr, styles);
                break;
            default:
                styles.put(attr.getKey(), attr.getValue());
        }
    }

    private Map<String, String> parseStylesFromStyleAttribute(String style) {
        Map<String, String> parsed = new HashMap<>();
        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(style);
        for (CssDeclaration declaration : declarations) {
            parsed.put(declaration.getProperty(), declaration.getExpression());
        }
        return parsed;
    }

}
