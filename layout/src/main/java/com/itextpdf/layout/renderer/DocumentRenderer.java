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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.Transform;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.List;

public class DocumentRenderer extends RootRenderer {

    protected Document document;
    protected List<Integer> wrappedContentPage = new ArrayList<>();

    public DocumentRenderer(Document document) {
        this(document, true);
    }

    public DocumentRenderer(Document document, boolean immediateFlush) {
        this.document = document;
        this.immediateFlush = immediateFlush;
        this.modelElement = document;
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new IllegalStateException("Not applicable for DocumentRenderer");
    }

    /**
     * For {@link DocumentRenderer}, this has a meaning of the renderer that will be used for relayout.
     *
     * @return relayout renderer.
     */
    @Override
    public IRenderer getNextRenderer() {
        return new DocumentRenderer(document, immediateFlush);
    }

    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        flushWaitingDrawingElements();
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            taggingHelper.releaseFinishedHints();
        }
        AreaBreak areaBreak = overflowResult != null && overflowResult.getAreaBreak() != null ? overflowResult.getAreaBreak() : null;
        if (areaBreak != null && areaBreak.getType() == AreaBreakType.LAST_PAGE) {
            while (currentPageNumber < document.getPdfDocument().getNumberOfPages()) {
                moveToNextPage();
            }
        } else {
            moveToNextPage();
        }
        PageSize customPageSize = areaBreak != null ? areaBreak.getPageSize() : null;
        while (document.getPdfDocument().getNumberOfPages() >= currentPageNumber && document.getPdfDocument().getPage(currentPageNumber).isFlushed()) {
            currentPageNumber++;
        }
        PageSize lastPageSize = ensureDocumentHasNPages(currentPageNumber, customPageSize);
        if (lastPageSize == null) {
            lastPageSize = new PageSize(document.getPdfDocument().getPage(currentPageNumber).getTrimBox());
        }
        return (currentArea = new RootLayoutArea(currentPageNumber, getCurrentPageEffectiveArea(lastPageSize)));
    }

    protected void flushSingleRenderer(IRenderer resultRenderer) {
        Transform transformProp = resultRenderer.<Transform>getProperty(Property.TRANSFORM);
        if (!waitingDrawingElements.contains(resultRenderer)) {
            processWaitingDrawing(resultRenderer, transformProp, waitingDrawingElements);
            if (FloatingHelper.isRendererFloating(resultRenderer) || transformProp != null)
                return;
        }

        if (!resultRenderer.isFlushed() && null != resultRenderer.getOccupiedArea()) { // TODO Remove checking occupied area to be not null when DEVSIX-1001 is resolved.
            int pageNum = resultRenderer.getOccupiedArea().getPageNumber();

            PdfDocument pdfDocument = document.getPdfDocument();
            ensureDocumentHasNPages(pageNum, null);
            PdfPage correspondingPage = pdfDocument.getPage(pageNum);
            if (correspondingPage.isFlushed()) {
                throw new PdfException(PdfException.CannotDrawElementsOnAlreadyFlushedPages);
            }

            boolean wrapOldContent = pdfDocument.getReader() != null && pdfDocument.getWriter() != null &&
                    correspondingPage.getContentStreamCount() > 0 && correspondingPage.getLastContentStream().getLength() > 0 &&
                    !wrappedContentPage.contains(pageNum) && pdfDocument.getNumberOfPages() >= pageNum;
            wrappedContentPage.add(pageNum);

            if (pdfDocument.isTagged()) {
                pdfDocument.getTagStructureContext().getAutoTaggingPointer().setPageForTagging(correspondingPage);
            }
            resultRenderer.draw(new DrawContext(pdfDocument, new PdfCanvas(correspondingPage, wrapOldContent), pdfDocument.isTagged()));
        }
    }

    protected PageSize addNewPage(PageSize customPageSize) {
        if (customPageSize != null) {
            document.getPdfDocument().addNewPage(customPageSize);
        } else {
            document.getPdfDocument().addNewPage();
        }
        return customPageSize != null ? customPageSize : document.getPdfDocument().getDefaultPageSize();
    }

    /**
     * Adds some pages so that the overall number is at least n.
     * Returns the page size of the n'th page.
     */
    private PageSize ensureDocumentHasNPages(int n, PageSize customPageSize) {
        PageSize lastPageSize = null;
        while (document.getPdfDocument().getNumberOfPages() < n) {
            lastPageSize = addNewPage(customPageSize);
        }
        return lastPageSize;
    }

    private Rectangle getCurrentPageEffectiveArea(PageSize pageSize) {
        float leftMargin = (float) getPropertyAsFloat(Property.MARGIN_LEFT);
        float bottomMargin = (float) getPropertyAsFloat(Property.MARGIN_BOTTOM);
        float topMargin = (float) getPropertyAsFloat(Property.MARGIN_TOP);
        float rightMargin = (float) getPropertyAsFloat(Property.MARGIN_RIGHT);
        return new Rectangle(pageSize.getLeft() + leftMargin,
                pageSize.getBottom() + bottomMargin,
                pageSize.getWidth() - leftMargin - rightMargin,
                pageSize.getHeight() - bottomMargin - topMargin);
    }

    private void moveToNextPage() {
        // We don't flush this page immediately, but only flush previous one because of manipulations with areas in case
        // of keepTogether property.
        if (immediateFlush && currentPageNumber > 1) {
            document.getPdfDocument().getPage(currentPageNumber - 1).flush();
        }
        currentPageNumber++;
    }
}
