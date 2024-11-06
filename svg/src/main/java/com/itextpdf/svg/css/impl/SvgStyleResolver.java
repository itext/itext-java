/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.css.impl;

import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.io.util.ResourceUtil;
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
import com.itextpdf.styledxmlparser.css.resolve.CssDefaults;
import com.itextpdf.styledxmlparser.css.resolve.CssInheritance;
import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.node.IAttribute;
import com.itextpdf.styledxmlparser.node.IAttributesContainer;
import com.itextpdf.styledxmlparser.node.IDataNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.IStylesContainer;
import com.itextpdf.styledxmlparser.node.ITextNode;
import com.itextpdf.styledxmlparser.node.IXmlDeclarationNode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.styledxmlparser.util.StyleUtil;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of SVG`s styles and attribute resolver .
 */
public class SvgStyleResolver implements ICssResolver {
    // It is necessary to cast parameters asList method to IStyleInheritance to C# compiler understand which types is used
    public static final Set<IStyleInheritance> INHERITANCE_RULES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList((IStyleInheritance) new CssInheritance(), (IStyleInheritance) new SvgAttributeInheritance())));

    // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
    private static final String[] ELEMENTS_INHERITING_PARENT_STYLES = new String[]{
            Tags.MARKER, Tags.LINEAR_GRADIENT, Tags.LINEAR_GRADIENT.toLowerCase(), Tags.PATTERN
    };

    private static final float DEFAULT_FONT_SIZE = CssDimensionParsingUtils.parseAbsoluteFontSize(
            CssDefaults.getDefaultValue(SvgConstants.Attributes.FONT_SIZE));

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgStyleResolver.class);

    private CssStyleSheet css;
    private static final String DEFAULT_CSS_PATH = "com/itextpdf/svg/default.css";
    private boolean isFirstSvgElement = true;

    /**
     * The device description.
     */
    private MediaDeviceDescription deviceDescription;

    /**
     * The list of fonts.
     */
    private final List<CssFontFaceRule> fonts = new ArrayList<>();

    /**
     * The resource resolver
     */
    private final ResourceResolver resourceResolver;

    /**
     * Creates a {@link SvgStyleResolver} with a given default CSS.
     *
     * @param defaultCssStream the default CSS
     * @param context the processor context
     * @throws IOException if any input/output issue occurs
     */
    public SvgStyleResolver(InputStream defaultCssStream, SvgProcessorContext context) throws IOException {
        this.css = CssStyleSheetParser.parse(defaultCssStream);
        this.resourceResolver = context.getResourceResolver();
        this.css.appendCssStyleSheet(context.getCssStyleSheet());
    }

    /**
     * Creates a {@link SvgStyleResolver}.
     *
     * @param context the processor context
     */
    public SvgStyleResolver(SvgProcessorContext context) {
        try (InputStream defaultCss = ResourceUtil.getResourceStream(DEFAULT_CSS_PATH)) {
            this.css = CssStyleSheetParser.parse(defaultCss);
        } catch (IOException e) {
            LOGGER.warn(SvgLogMessageConstant.ERROR_INITIALIZING_DEFAULT_CSS, e);
            this.css = new CssStyleSheet();
        }
        this.resourceResolver = context.getResourceResolver();
        this.css.appendCssStyleSheet(context.getCssStyleSheet());
    }

    /**
     * Creates a {@link SvgStyleResolver}. This constructor will instantiate its internal
     * style sheet and it will collect the css declarations from the provided node.
     *
     * @param rootNode node to collect css from
     * @param context the processor context
     */
    public SvgStyleResolver(INode rootNode, SvgProcessorContext context) {
        // TODO DEVSIX-2060. Fetch default styles first.
        this.deviceDescription = context.getDeviceDescription();
        this.resourceResolver = context.getResourceResolver();
        this.css = new CssStyleSheet();
        this.css.appendCssStyleSheet(context.getCssStyleSheet());
        collectCssDeclarations(rootNode, this.resourceResolver);
        collectFonts();
    }

    public static void resolveFontSizeStyle(Map<String, String> styles, SvgCssContext cssContext, String parentFontSizeStr) {
        String elementFontSize = styles.get(SvgConstants.Attributes.FONT_SIZE);
        String resolvedFontSize;
        if (CssTypesValidationUtils.isNegativeValue(elementFontSize)) {
            elementFontSize = parentFontSizeStr;
        }

        if (CssTypesValidationUtils.isRelativeValue(elementFontSize) || CommonCssConstants.LARGER.equals(elementFontSize)
                || CommonCssConstants.SMALLER.equals(elementFontSize)) {
            float baseFontSize;
            if (CssTypesValidationUtils.isRemValue(elementFontSize)) {
                baseFontSize = cssContext == null ? DEFAULT_FONT_SIZE : cssContext.getRootFontSize();
            } else if (parentFontSizeStr == null) {
                baseFontSize = CssDimensionParsingUtils.parseAbsoluteFontSize(
                        CssDefaults.getDefaultValue(SvgConstants.Attributes.FONT_SIZE));
            } else {
                baseFontSize = CssDimensionParsingUtils.parseAbsoluteLength(parentFontSizeStr);
            }

            final float absoluteFontSize = CssDimensionParsingUtils.parseRelativeFontSize(elementFontSize, baseFontSize);
            // Format to 4 decimal places to prevent differences between Java and C#
            resolvedFontSize = DecimalFormatUtil.formatNumber(absoluteFontSize, "0.####");
        } else if (elementFontSize == null){
            resolvedFontSize = DecimalFormatUtil.formatNumber(DEFAULT_FONT_SIZE, "0.####");
        } else {
            resolvedFontSize = DecimalFormatUtil.formatNumber(CssDimensionParsingUtils.parseAbsoluteFontSize(elementFontSize), "0.####");
        }
        styles.put(SvgConstants.Attributes.FONT_SIZE, resolvedFontSize + CommonCssConstants.PT);
    }

    public static boolean isElementNested(IElementNode element, String parentElementNameForSearch) {
        if (!(element.parentNode() instanceof IElementNode)) {
            return false;
        }
        final IElementNode parentElement = (IElementNode) element.parentNode();
        if (parentElement == null) {
            return false;
        }
        if (parentElement.name() != null && parentElement.name().equals(parentElementNameForSearch)) {
            return true;
        }

        return isElementNested(parentElement, parentElementNameForSearch);
    }

    @Override
    public Map<String, String> resolveStyles(INode element, AbstractCssContext context) {
        if (context instanceof SvgCssContext) {
            return resolveStyles(element, (SvgCssContext) context);
        }
        throw new SvgProcessingException(SvgLogMessageConstant.CUSTOM_ABSTRACT_CSS_CONTEXT_NOT_SUPPORTED);
    }

    /**
     * Resolves node styles without inheritance of parent element styles.
     *
     * @param node the node
     * @param cssContext the CSS context (RootFontSize, etc.)
     * @return the map containing the resolved styles that are defined in the body of the element
     */
    public Map<String, String> resolveNativeStyles(INode node, AbstractCssContext cssContext) {
        final Map<String, String> styles = new HashMap<>();
        IAttribute styleAttr = null;
        // Load in attributes declarations except style
        if (node instanceof IElementNode) {
            IElementNode eNode = (IElementNode) node;
            for (IAttribute attr : eNode.getAttributes()) {
                if (Attributes.STYLE.equals(attr.getKey())) {
                    styleAttr = attr;
                } else {
                    processAttribute(attr, styles);
                }
            }
        }

        // Load in from collected style sheets
        final List<CssDeclaration> styleSheetDeclarations = css.getCssDeclarations(node,
                MediaDeviceDescription.createDefault());
        for (CssDeclaration ssd : styleSheetDeclarations) {
            styles.put(ssd.getProperty(), ssd.getExpression());
        }

        // Inline CSS from style attribute overrides presentation attributes and collected style sheets
        if (styleAttr != null) {
            processAttribute(styleAttr, styles);
        }
        return styles;
    }

    private static boolean onlyNativeStylesShouldBeResolved(IElementNode element) {
        for (final String elementInheritingParentStyles : ELEMENTS_INHERITING_PARENT_STYLES) {
            if (elementInheritingParentStyles.equals(element.name())
                    || SvgStyleResolver.isElementNested(element, elementInheritingParentStyles)) {
                return false;
            }
        }
        return SvgStyleResolver.isElementNested(element, Tags.DEFS);
    }

    private Map<String, String> resolveStyles(INode element, SvgCssContext context) {
        // Resolves node styles without inheritance of parent element styles
        Map<String, String> styles = resolveNativeStyles(element, context);
        if (element instanceof IElementNode && SvgStyleResolver.onlyNativeStylesShouldBeResolved((IElementNode) element)) {
            return styles;
        }

        String parentFontSizeStr = null;
        // Load in and merge inherited styles from parent
        if (element.parentNode() instanceof IStylesContainer) {
            final IStylesContainer parentNode = (IStylesContainer) element.parentNode();
            Map<String, String> parentStyles = parentNode.getStyles();

            if (parentStyles == null && !(parentNode instanceof IElementNode)) {
                LOGGER.error(StyledXmlParserLogMessageConstant.ERROR_RESOLVING_PARENT_STYLES);
            }

            if (parentStyles != null) {
                parentFontSizeStr = parentStyles.get(SvgConstants.Attributes.FONT_SIZE);
                for (Map.Entry<String, String> entry : parentStyles.entrySet()) {
                    styles = StyleUtil.mergeParentStyleDeclaration(styles, entry.getKey(), entry.getValue(),
                            parentFontSizeStr, INHERITANCE_RULES);
                }
            }
        }

        SvgStyleResolver.resolveFontSizeStyle(styles, context, parentFontSizeStr);

        // Set root font size
        final boolean isSvgElement = element instanceof IElementNode
                && SvgConstants.Tags.SVG.equals(((IElementNode) element).name());
        if (isFirstSvgElement && isSvgElement) {
            isFirstSvgElement = false;
            final String rootFontSize = styles.get(SvgConstants.Attributes.FONT_SIZE);
            if (rootFontSize != null) {
                context.setRootFontSize(styles.get(SvgConstants.Attributes.FONT_SIZE));
            }
        }

        return styles;
    }

    /**
     * Resolves the full path of link href attribute,
     * thanks to the resource resolver.
     *
     * @param attr the attribute to process
     * @param attributesMap the element styles map
     */
    private void processXLink(final IAttribute attr, final Map<String, String> attributesMap) {
        String xlinkValue = attr.getValue();
        if (!isStartedWithHash(xlinkValue) && !ResourceResolver.isDataSrc(xlinkValue)) {
            try {
                xlinkValue = this.resourceResolver.resolveAgainstBaseUri(attr.getValue()).toExternalForm();
            } catch (MalformedURLException mue) {
                LOGGER.error(StyledXmlParserLogMessageConstant.UNABLE_TO_RESOLVE_IMAGE_URL, mue);
            }
        }
        attributesMap.put(attr.getKey(), xlinkValue);
    }

    /**
     * Checks if string starts with #.
     *
     * @param s the test string
     * @return true if the string starts with #, otherwise false
     */
    private boolean isStartedWithHash(String s) {
        return s != null && s.startsWith("#");
    }

    private void collectCssDeclarations(INode rootNode, ResourceResolver resourceResolver) {
        LinkedList<INode> q = new LinkedList<>();
        if (rootNode != null) {
            q.add(rootNode);
        }
        while (!q.isEmpty()) {
            INode currentNode = q.pop();
            if (currentNode instanceof IElementNode) {
                IElementNode headChildElement = (IElementNode) currentNode;
                if (SvgConstants.Tags.STYLE.equals(headChildElement.name())) {
                    // XML parser will parse style tag contents as text nodes
                    for (INode node : currentNode.childNodes()) {
                        if (node instanceof IDataNode || node instanceof ITextNode) {
                            String styleData = node instanceof IDataNode ? ((IDataNode) node).getWholeData() :
                                    ((ITextNode) node).wholeText();

                            CssStyleSheet styleSheet = CssStyleSheetParser.parse(styleData);
                            // TODO (DEVSIX-2263): media query wrap
                            // styleSheet = wrapStyleSheetInMediaQueryIfNecessary(headChildElement, styleSheet);
                            this.css.appendCssStyleSheet(styleSheet);
                        }
                    }
                } else if (CssUtils.isStyleSheetLink(headChildElement)) {
                    parseStylesheet(headChildElement);
                }
            } else if (currentNode instanceof IXmlDeclarationNode) {
                IXmlDeclarationNode declarationNode = (IXmlDeclarationNode) currentNode;
                if (SvgConstants.Tags.XML_STYLESHEET.equals(declarationNode.name())) {
                    parseStylesheet(declarationNode);
                }
            }
            for (INode child : currentNode.childNodes()) {
                if (child instanceof IElementNode || child instanceof IXmlDeclarationNode) {
                    q.add(child);
                }
            }
        }
    }

    private void parseStylesheet(IAttributesContainer attributesNode) {
        String styleSheetUri = attributesNode.getAttribute(SvgConstants.Attributes.HREF);
        try (InputStream stream = resourceResolver.retrieveResourceAsInputStream(styleSheetUri)) {
            if (stream != null) {
                CssStyleSheet styleSheet = CssStyleSheetParser.parse(stream,
                        resourceResolver.resolveAgainstBaseUri(styleSheetUri).toExternalForm());
                this.css.appendCssStyleSheet(styleSheet);
            }
        } catch (Exception exc) {
            LOGGER.error(StyledXmlParserLogMessageConstant.UNABLE_TO_PROCESS_EXTERNAL_CSS_FILE, exc);
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
