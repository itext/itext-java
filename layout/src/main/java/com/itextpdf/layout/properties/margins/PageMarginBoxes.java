package com.itextpdf.layout.properties.margins;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.SectionBreakRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class to store information about all page margin boxes for single page.
 */
public class PageMarginBoxes {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageMarginBoxes.class);

    private final Map<MarginBoxName, PageMarginContent> margins = new LinkedHashMap<>();

    private float[] marginSizes = new float[4];

    /**
     * Creates new {@link PageMarginBoxes} instance.
     *
     * @param margins list of {@link PageMarginContent} instances representing page margin content
     * for corresponding margin box name {@link MarginBoxName} (position on the page)
     */
    public PageMarginBoxes(List<PageMarginContent> margins) {
        for (PageMarginContent margin : margins) {
            this.margins.put(margin.getMarginBoxName(), margin);
        }
    }

    /**
     * Creates new {@link PageMarginBoxes} instance by copying existing one.
     *
     * @param other {@link PageMarginBoxes} to copy
     */
    public PageMarginBoxes(PageMarginBoxes other) {
        for (Map.Entry<MarginBoxName, PageMarginContent> margin : other.margins.entrySet()) {
            this.margins.put(margin.getKey(), new PageMarginContent(margin.getValue()));
        }
        this.marginSizes = other.marginSizes;
    }

    /**
     * Draws all page margins for the page specified via page number.
     *
     * @param marginRenderer renderer for the margin content to draw
     * @param marginRect page margin box rectangle
     * @param documentRenderer document renderer to use as parent for margin renderer
     * @param document {@link PdfDocument} to which content is written
     * @param pageNumber page number
     * @param marginBoxName string value representing margin box name (defining its position on the page)
     */
    public static void draw(IRenderer marginRenderer, Rectangle marginRect, DocumentRenderer documentRenderer,
                            PdfDocument document, int pageNumber, String marginBoxName) {
        PdfPage page = document.getPage(pageNumber);

        LayoutResult result = marginRenderer.setParent(documentRenderer)
                .layout(new LayoutContext(new LayoutArea(pageNumber, marginRect)));

        IRenderer rendererToDraw = result.getStatus() == LayoutResult.FULL ? marginRenderer : result.getSplitRenderer();
        if (rendererToDraw == null) {
            // Margin box elements have overflow property set to HIDDEN, therefore it is expected to neither get
            // LayoutResult other than FULL nor get no split renderer (result NOTHING) even if result is not FULL.
            LOGGER.error(MessageFormatUtil.format(
                    LayoutLogMessageConstant.PAGE_MARGIN_BOX_CONTENT_CANNOT_BE_DRAWN, marginBoxName, pageNumber));
            return;
        }

        TagTreePointer tagPointer = null, backupPointer = null;
        PdfPage backupPage = null;
        if (document.isTagged()) {
            tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
            backupPage = tagPointer.getCurrentPage();
            backupPointer = new TagTreePointer(tagPointer);
            tagPointer.moveToRoot();
            tagPointer.setPageForTagging(page);
        }

        rendererToDraw.setParent(documentRenderer)
                .draw(new DrawContext(page.getDocument(), new PdfCanvas(page), document.isTagged()));

        if (document.isTagged() && tagPointer != null) {
            tagPointer.setPageForTagging(backupPage);
            tagPointer.moveToPointer(backupPointer);
        }
    }

    /**
     * Creates renderer from element excluding page breaks and adds tagging tree hints.
     *
     * @param element {@link IElement} to create renderer for
     * @param documentRenderer document renderer to use as parent for margin renderer
     * @param pdfDocument {@link PdfDocument} to which content is written
     *
     * @return created {@link IRenderer}
     */
    public static IRenderer createRendererFromElement(IElement element, DocumentRenderer documentRenderer,
                                                      PdfDocument pdfDocument) {
        if (element == null) {
            return null;
        }
        IRenderer renderer = element.createRendererSubTree();
        removePageBreaks(renderer);
        renderer.setParent(documentRenderer);
        if (pdfDocument.isTagged()) {
            LayoutTaggingHelper taggingHelper = renderer.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            LayoutTaggingHelper.addTreeHints(taggingHelper, renderer);
        }
        return renderer;
    }

    /**
     * Gets rid of all page breaks that might have occurred inside page margin boxes
     * because of the running/layout elements.
     *
     * @param renderer the root renderer of the renderers subtree
     */
    private static void removePageBreaks(IRenderer renderer) {
        List<IRenderer> pageBreaks = null;
        for (IRenderer child : renderer.getChildRenderers()) {
            if (child instanceof AreaBreakRenderer || child instanceof SectionBreakRenderer) {
                if (pageBreaks == null) {
                    pageBreaks = new ArrayList<>();
                }
                pageBreaks.add(child);
            } else {
                removePageBreaks(child);
            }
        }
        if (pageBreaks != null) {
            renderer.getChildRenderers().removeAll(pageBreaks);
        }
    }

    /**
     * Gets page margin content {@link PageMarginContent} by margin box name.
     *
     * @param marginBoxName {@link MarginBoxName} margin box name to get content for
     *
     * @return page margin content {@link PageMarginContent} by margin box name
     */
    public PageMarginContent getPageMarginContent(MarginBoxName marginBoxName) {
        return this.margins.get(marginBoxName);
    }

    /**
     * Gets page margin sizes in top, right, bottom, left order.
     *
     * @return array of float top, right, bottom, left margin sizes
     */
    public float[] getMarginSizes() {
        return marginSizes;
    }

    /**
     * Sets page margin sizes in top, right, bottom, left order.
     *
     * @param marginSizes array of float top, right, bottom, left margin sizes
     *
     * @return this same {@link PageMarginBoxes} instance
     */
    public PageMarginBoxes setMarginSizes(float[] marginSizes) {
        this.marginSizes = marginSizes;
        return this;
    }

    /**
     * Layouts all page margins to calculate their occupied area and page margin sizes.
     *
     * @param documentRenderer {@link DocumentRenderer} renderer for the document to which content will be written
     * @param pageNumber page number
     * @param pageSize page size
     *
     * @return float array of top, right, bottom, left margin sizes
     */
    public float[] layout(DocumentRenderer documentRenderer, int pageNumber, Rectangle pageSize) {
        PageMarginContent topM = this.getPageMarginContent(MarginBoxName.TOP);
        PageMarginContent rightM = this.getPageMarginContent(MarginBoxName.RIGHT);
        PageMarginContent bottomM = this.getPageMarginContent(MarginBoxName.BOTTOM);
        PageMarginContent leftM = this.getPageMarginContent(MarginBoxName.LEFT);

        // Layout all margins.
        LayoutResult top = null;
        MinMaxWidth rightMinMaxWidth = null;
        LayoutResult bottom = null;
        MinMaxWidth leftMinMaxWidth = null;

        if (topM != null) {
            IRenderer topMargin = topM.getMarginContent().createRendererSubTree();
            top = topMargin.setParent(documentRenderer).layout(new LayoutContext(new LayoutArea(pageNumber, pageSize)));
        }
        if (rightM != null) {
            IRenderer rightMargin = rightM.getMarginContent().createRendererSubTree();
            rightMinMaxWidth = ((AbstractRenderer) rightMargin.setParent(documentRenderer)).getMinMaxWidth();
        }
        if (bottomM != null) {
            IRenderer bottomMargin = bottomM.getMarginContent().createRendererSubTree();
            bottom = bottomMargin.setParent(documentRenderer).layout(new LayoutContext(new LayoutArea(pageNumber, pageSize)));
        }
        if (leftM != null) {
            IRenderer leftMargin = leftM.getMarginContent().createRendererSubTree();
            leftMinMaxWidth = ((AbstractRenderer) leftMargin.setParent(documentRenderer)).getMinMaxWidth();
        }

        Document document = (Document) documentRenderer.getModelElement();
        // Save rectangles for all renderers.
        float leftMargin = leftMinMaxWidth == null ? document.getLeftMargin() : leftMinMaxWidth.getMinWidth();
        float rightMargin = rightMinMaxWidth == null ? document.getRightMargin() : rightMinMaxWidth.getMinWidth();
        Rectangle topBBox = top == null ?
                new Rectangle(document.getLeftMargin(),
                        pageSize.getTop() - document.getTopMargin(), pageSize.getWidth(), document.getTopMargin())
                : top.getOccupiedArea().getBBox();
        Rectangle bottomBBox = bottom == null ?
                new Rectangle(document.getLeftMargin(), 0, pageSize.getWidth(), document.getBottomMargin())
                : bottom.getOccupiedArea().getBBox();

        if (topM != null) {
            topM.setPageMarginBoxRectangle(new Rectangle(leftMargin, topBBox.getY(),
                    topBBox.getWidth() - rightMargin - leftMargin, topBBox.getHeight()));
        }
        if (rightM != null) {
            rightM.setPageMarginBoxRectangle(new Rectangle(pageSize.getRight() - rightMargin, bottomBBox.getHeight(),
                    rightMargin, pageSize.getHeight() - topBBox.getHeight() - bottomBBox.getHeight()));
        }
        if (bottomM != null) {
            bottomM.setPageMarginBoxRectangle(new Rectangle(leftMargin, 0,
                    bottomBBox.getWidth() - rightMargin - leftMargin, bottomBBox.getHeight()));
        }
        if (leftM != null) {
            leftM.setPageMarginBoxRectangle(new Rectangle(0, bottomBBox.getHeight(),
                    leftMargin, pageSize.getHeight() - topBBox.getHeight() - bottomBBox.getHeight()));
        }

        return new float[]{topBBox.getHeight(), rightMargin, bottomBBox.getHeight(), leftMargin};
    }

    /**
     * Draws all page margins for the page specified via page number.
     *
     * @param documentRenderer document renderer to use as parent for margin renderer
     * @param document {@link PdfDocument} to which content is written
     * @param pageNumber page number
     */
    public void draw(DocumentRenderer documentRenderer, PdfDocument document, int pageNumber) {
        for (PageMarginContent margin : margins.values()) {
            Rectangle rect = margin.getPageMarginBoxRectangle();
            if (rect == null) {
                // Margins weren't layouted, we can get here if page is added manually and is empty.
                layout(documentRenderer, pageNumber, document.getPage(pageNumber).getPageSize());
                rect = margin.getPageMarginBoxRectangle();
            }
            IElement element = margin.getMarginContent();
            MarginBoxName marginBoxName = margin.getMarginBoxName();
            setPageMarginTagRole(element);
            IRenderer renderer = createRendererFromElement(element, documentRenderer, document);
            draw(renderer, rect, documentRenderer, document, pageNumber, marginBoxName.name());
        }
    }

    /**
     * Sets the role of the page margin element to use in the document tag tree.
     *
     * @param element {@link IElement} to set role for
     */
    protected void setPageMarginTagRole(IElement element) {
        if (element instanceof IAccessibleElement) {
            ((IAccessibleElement) element).getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageMarginBoxes that = (PageMarginBoxes) o;
        boolean result = true;
        for (Map.Entry<MarginBoxName, PageMarginContent> pageMarginEntry : this.margins.entrySet()) {
            MarginBoxName marginBoxName = pageMarginEntry.getKey();
            if (!that.margins.containsKey(marginBoxName)) {
                return false;
            }
            result &= Objects.equals(pageMarginEntry.getValue(), that.margins.get(marginBoxName));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(margins);
    }
}
