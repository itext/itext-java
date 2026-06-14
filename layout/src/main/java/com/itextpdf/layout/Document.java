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
package com.itextpdf.layout;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.ILargeElement;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Document is the default root element when creating a self-sufficient PDF. It
 * mainly operates high-level operations e.g. setting page size and rotation,
 * adding elements, and writing text at specific coordinates. It has no
 * knowledge of the actual PDF concepts and syntax.
 * <p>
 * A {@link Document}'s rendering behavior can be modified by extending
 * {@link DocumentRenderer} and setting an instance of this newly created with
 * {@link #setRenderer(com.itextpdf.layout.renderer.DocumentRenderer) }.
 */
public class Document extends RootElement<Document> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Document.class);

    private final Map<Integer, PageMarginBoxes> pageMargins = new HashMap<>();
    private final List<Tuple2<Predicate<Integer>, PageMarginBoxes>> pageMarginsRules = new ArrayList<>();
    private final List<Function<Integer, PageMarginBoxes>> pageMarginsFunctions = new ArrayList<>();

    /**
     * Creates a document from a {@link PdfDocument}. Initializes the first page
     * with the {@link PdfDocument}'s current default {@link PageSize}.
     *
     * @param pdfDoc the in-memory representation of the PDF document
     */
    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}.
     *
     * @param pdfDoc the in-memory representation of the PDF document
     * @param pageSize the page size
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        this(pdfDoc, pageSize, true);
    }

    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}.
     *
     * @param pdfDoc the in-memory representation of the PDF document
     * @param pageSize the page size
     * @param immediateFlush if true, write pages and page-related instructions
     * to the {@link PdfDocument} as soon as possible.
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize, boolean immediateFlush) {
        super();
        this.pdfDocument = pdfDoc;
        this.pdfDocument.setDefaultPageSize(pageSize);
        this.immediateFlush = immediateFlush;
        this.ensureRootTagIsCreated();
    }

    /**
     * Closes the document and associated PdfDocument.
     */
    @Override
    public void close() {
        if (rootRenderer != null) {
            rootRenderer.close();
        }
        pdfDocument.close();
    }

    /**
     * Terminates the current element, usually a page. Sets the next element
     * to be the size specified in the argument.
     *
     * @param areaBreak an {@link AreaBreak}, optionally with a specified size
     *
     * @return this element
     */
    public Document add(AreaBreak areaBreak) {
        checkClosingStatus();
        childElements.add(areaBreak);
        ensureRootRendererNotNull().addChild(areaBreak.createRendererSubTree());
        if (immediateFlush) {
            childElements.remove(childElements.size() - 1);
        }
        return this;
    }

    /**
     * Terminates the current page if it's not the first one in the document.
     * Sets the page size and/or page margins specified in the arguments for the next page.
     *
     * @param sectionBreak {@link SectionBreak}, optionally with a specified page size and/or page margins
     *
     * @return this same {@link Document} instance
     */
    public Document add(SectionBreak sectionBreak) {
        checkClosingStatus();
        childElements.add(sectionBreak);
        ensureRootRendererNotNull().addChild(sectionBreak.createRendererSubTree());
        if (immediateFlush) {
            childElements.remove(childElements.size() - 1);
        }
        return this;
    }

    @Override
    public Document add(IBlockElement element) {
        checkClosingStatus();
        super.add(element);
        if (element instanceof ILargeElement) {
            ((ILargeElement) element).setDocument(this);
            ((ILargeElement) element).flushContent();
        }
        return this;
    }

    /**
     * Gets PDF document.
     *
     * @return the in-memory representation of the PDF document
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Changes the {@link DocumentRenderer} at runtime. Use this to customize
     * the Document's {@link IRenderer} behavior.
     *
     * @param documentRenderer the DocumentRenderer to set
     */
    public void setRenderer(DocumentRenderer documentRenderer) {
        this.rootRenderer = documentRenderer;
    }

    /**
     * Forces all registered renderers (including child element renderers) to
     * flush their contents to the content stream.
     */
    public void flush() {
        rootRenderer.flush();
    }

    /**
     * Performs an entire recalculation of the document flow, taking into
     * account all its current child elements. May become very
     * resource-intensive for large documents.
     * <p>
     * Do not use when you have set {@link #immediateFlush} to <code>true</code>.
     */
    public void relayout() {
        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

        if (rootRenderer instanceof DocumentRenderer) {
            ((DocumentRenderer) rootRenderer).getTargetCounterHandler().prepareHandlerToRelayout();
        }

        IRenderer nextRelayoutRenderer = rootRenderer != null ? rootRenderer.getNextRenderer() : null;
        if (nextRelayoutRenderer == null || !(nextRelayoutRenderer instanceof RootRenderer)) {
            nextRelayoutRenderer = new DocumentRenderer(this, immediateFlush);
        }

        // Even though #relayout() only makes sense when immediateFlush=false and therefore no elements
        // should have been written to document, still empty pages are created during layout process
        // because we need to know the effective page size which may differ from page to page.
        // Therefore, we remove all the pages that might have been created before proceeding to relayout elements.
        while (pdfDocument.getNumberOfPages() > 0) {
            pdfDocument.removePage(pdfDocument.getNumberOfPages());
        }

        rootRenderer = (RootRenderer) nextRelayoutRenderer;
        for (IElement element : childElements) {
            createAndAddRendererSubTree(element);
        }
    }

    /**
     * Gets the left margin, measured in points
     *
     * @return a <code>float</code> containing the left margin value
     */
    public float getLeftMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_LEFT);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_LEFT));
    }

    /**
     * Sets the left margin, measured in points
     *
     * @param leftMargin a <code>float</code> containing the new left margin value
     */
    public void setLeftMargin(float leftMargin) {
        setProperty(Property.MARGIN_LEFT, leftMargin);
    }

    /**
     * Gets the right margin, measured in points
     *
     * @return a <code>float</code> containing the right margin value
     */
    public float getRightMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_RIGHT);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_RIGHT));
    }

    /**
     * Sets the right margin, measured in points
     *
     * @param rightMargin a <code>float</code> containing the new right margin value
     */
    public void setRightMargin(float rightMargin) {
        setProperty(Property.MARGIN_RIGHT, rightMargin);
    }

    /**
     * Gets the top margin, measured in points
     *
     * @return a <code>float</code> containing the top margin value
     */
    public float getTopMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_TOP);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_TOP));
    }

    /**
     * Sets the top margin, measured in points
     *
     * @param topMargin a <code>float</code> containing the new top margin value
     */
    public void setTopMargin(float topMargin) {
        setProperty(Property.MARGIN_TOP, topMargin);
    }

    /**
     * Gets the bottom margin, measured in points
     *
     * @return a <code>float</code> containing the bottom margin value
     */
    public float getBottomMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_BOTTOM);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_BOTTOM));
    }

    /**
     * Sets the bottom margin, measured in points
     *
     * @param bottomMargin a <code>float</code> containing the new bottom margin value
     */
    public void setBottomMargin(float bottomMargin) {
        setProperty(Property.MARGIN_BOTTOM, bottomMargin);
    }

    /**
     * Convenience method to set all margins with one method.
     *
     * @param topMargin the upper margin
     * @param rightMargin the right margin
     * @param leftMargin the left margin
     * @param bottomMargin the lower margin
     */
    public void setMargins(float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        setTopMargin(topMargin);
        setRightMargin(rightMargin);
        setBottomMargin(bottomMargin);
        setLeftMargin(leftMargin);
    }

    /**
     * Gets page margins by specified page number.
     *
     * @param pageNumber number of the page to get margins for
     *
     * @return {@link PageMarginBoxes} page margins
     */
    public PageMarginBoxes getPageMargins(int pageNumber) {
        if (pageMargins.containsKey(pageNumber)) {
            return pageMargins.get(pageNumber);
        }

        for (Tuple2<Predicate<Integer>, PageMarginBoxes> rule : pageMarginsRules) {
            if (rule.getFirst().test(pageNumber)) {
                PageMarginBoxes pageMarginBoxes = rule.getSecond();
                return pageMarginBoxes != null ? new PageMarginBoxes(pageMarginBoxes) : null;
            }
        }

        for (Function<Integer, PageMarginBoxes> function : pageMarginsFunctions) {
            PageMarginBoxes pageMarginBoxes = function.apply(pageNumber);
            if (pageMarginBoxes != null) {
                return pageMarginBoxes;
            }
        }

        return null;
    }

    /**
     * Sets page margins for page with provided number.
     *
     * @param pageNumber number of the page to set margins for
     * @param margins {@link PageMarginBoxes} page margins to set
     *
     * @return this same {@link Document} instance
     */
    public Document setPageMargins(int pageNumber, PageMarginBoxes margins) {
        pageMargins.put(pageNumber, margins);
        return this;
    }

    /**
     * Sets page margins for page based on provided condition for page number.
     *
     * @param condition matching rule with page number as argument
     * @param margins {@link PageMarginBoxes} page margins to set
     *
     * @return this same {@link Document} instance
     */
    public Document setPageMargins(Predicate<Integer> condition, PageMarginBoxes margins) {
        pageMarginsRules.add(new Tuple2<>(condition, margins));
        return this;
    }

    /**
     * Sets page margins for page based on provided function for page number.
     *
     * @param function function with page number as argument, return {@code null} in case result should be ignored
     * and {@link PageMarginBoxes} page margins return value
     *
     * @return this same {@link Document} instance
     */
    public Document setPageMargins(Function<Integer, PageMarginBoxes> function) {
        this.pageMarginsFunctions.add(function);
        return this;
    }

    /**
     * Checks whether page margins have been specified for the given page number.
     *
     * <p>
     * This method returns {@code true} if the margins for the page are determined by
     * any of the following mechanisms (in order of precedence):
     * <ol>
     *   <li>Explicitly set margins for page number via {@link #setPageMargins(int, PageMarginBoxes)}
     *   <li>Matching rule set via {@link #setPageMargins(Predicate, PageMarginBoxes)}
     *   <li>Function set via {@link #setPageMargins(Function)}
     * </ol>
     *
     * <p>
     * NOTE: the method returns {@code true} even if the value produced by the mechanisms is {@code null}.
     * Only when none of the above apply and the default static margins are used, this method returns {@code false}.
     *
     * @param pageNumber the page number to check
     *
     * @return {@code true} if margins for the page are defined explicitly by page number,
     * by matching rule or by function, {@code false} otherwise
     */
    public boolean isPageMarginsSpecified(int pageNumber) {
        if (pageMargins.containsKey(pageNumber)) {
            return true;
        }

        for (Tuple2<Predicate<Integer>, PageMarginBoxes> rule : pageMarginsRules) {
            if (rule.getFirst().test(pageNumber)) {
                return true;
            }
        }

        for (Function<Integer, PageMarginBoxes> function : pageMarginsFunctions) {
            PageMarginBoxes pageMarginBoxes = function.apply(pageNumber);
            if (pageMarginBoxes != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets {@link FootnotesProperties} specified for the document to customize footnotes.
     *
     * @return {@link FootnotesProperties} specified for the document
     */
    public FootnotesProperties getFootnotesProperties() {
        FootnotesProperties property = this.<FootnotesProperties>getProperty(Property.FOOTNOTES_PROPERTIES);
        return property != null ? property :
                this.<FootnotesProperties>getDefaultProperty(Property.FOOTNOTES_PROPERTIES);
    }

    /**
     * Sets {@link FootnotesProperties} for the document.
     *
     * @param footnotesProperties {@link FootnotesProperties} to customize footnotes
     */
    public void setFootnotesProperties(FootnotesProperties footnotesProperties) {
        FootnotesProperties currentProperties = this.getFootnotesProperties();
        FootnoteNumberingConfig footnoteNumberingConfig = currentProperties.getFootnoteNumberingConfig();
        if (footnotesProperties != null) {
            if (FootnoteNumberingConfig.PER_DOCUMENT == footnotesProperties.getFootnoteNumberingConfig()) {
                if (this.hasOwnProperty(Property.FOOTNOTES_PROPERTIES) &&
                        FootnoteNumberingConfig.PER_DOCUMENT != footnoteNumberingConfig) {
                    LOGGER.warn(LayoutLogMessageConstant.FOOTNOTE_NUM_PER_DOCUMENT_SHOULD_BE_FIRST);
                    footnotesProperties.setFootnoteNumberingConfig(footnoteNumberingConfig);
                }
            } else if (FootnoteNumberingConfig.PER_DOCUMENT == footnoteNumberingConfig) {
                LOGGER.warn(LayoutLogMessageConstant.FOOTNOTE_NUM_PER_DOCUMENT_CANNOT_BE_CHANGED);
                footnotesProperties.setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT);
            }
        }
        this.setProperty(Property.FOOTNOTES_PROPERTIES, footnotesProperties);
    }

    /**
     * Returns the area that will actually be used to write on the page, given
     * the current margins. Does not have any side effects on the document.
     *
     * @param pageSize the size of the page to
     *
     * @return a {@link Rectangle} with the required dimensions and origin point
     */
    public Rectangle getPageEffectiveArea(PageSize pageSize) {
        float x = pageSize.getLeft() + getLeftMargin();
        float y = pageSize.getBottom() + getBottomMargin();
        float width = pageSize.getWidth() - getLeftMargin() - getRightMargin();
        float height = pageSize.getHeight() - getBottomMargin() - getTopMargin();
        return new Rectangle(x, y, width, height);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.FONT:
                if (getPdfDocument().getConformance() != null && getPdfDocument().getConformance().conformsToAny()) {
                    return (T1) (Object) getPdfDocument().getDefaultFont();
                }
                return super.<T1>getDefaultProperty(property);
            case Property.MARGIN_BOTTOM:
            case Property.MARGIN_LEFT:
            case Property.MARGIN_RIGHT:
            case Property.MARGIN_TOP:
                return (T1) (Object) 36f;
            case Property.FOOTNOTES_PROPERTIES:
                return (T1) (Object) new FootnotesProperties();
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }


    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new DocumentRenderer(this, immediateFlush);
        return rootRenderer;
    }

    /**
     * Checks whether a method is invoked at the closed document
     */
    protected void checkClosingStatus() {
        if (getPdfDocument().isClosed()) {
            throw new PdfException(LayoutExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION);
        }
    }
}
