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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.ArrayList;
import java.util.List;


public class PdfMerger {

    private PdfDocument pdfDocument;
    private boolean closeSrcDocuments;
    private boolean mergeTags;
    private boolean mergeOutlines;

    /**
     * This class is used to merge a number of existing documents into one. By default, if source document
     * contains tags and outlines, they will be also copied to the destination document.
     *
     * @param pdfDocument the document into which source documents will be merged
     */
    public PdfMerger(PdfDocument pdfDocument) {
        this(pdfDocument, true, true);
    }

    /**
     * This class is used to merge a number of existing documents into one.
     *
     * @param pdfDocument the document into which source documents will be merged
     * @param mergeTags if true, then tags from the source document are copied even if destination document is not set as
     *                  tagged. Note, that if false, tag structure is still could be copied if the destination document
     *                  is explicitly marked as tagged with {@link PdfDocument#setTagged()}
     * @param mergeOutlines if true, then outlines from the source document are copied even if in destination document
     *                      outlines are not initialized. Note, that if false, outlines are still could be copied if the
     *                      destination document outlines were explicitly initialized with {@link PdfDocument#initializeOutlines()}
     */
    public PdfMerger(PdfDocument pdfDocument, boolean mergeTags, boolean mergeOutlines) {
        this.pdfDocument = pdfDocument;
        this.mergeTags = mergeTags;
        this.mergeOutlines = mergeOutlines;
    }

    /**
     * If set to <i>true</i> then passed to the <i>{@code PdfMerger#merge}</i> method source documents will be closed
     * immediately after merging specified pages into current document. If <i>false</i> - PdfDocuments are left open.
     * Default value - <i>false</i>.
     *
     * @param closeSourceDocuments should be true to close pdf documents in merge method
     * @return this {@code PdfMerger} instance
     */
    public PdfMerger setCloseSourceDocuments(boolean closeSourceDocuments) {
        this.closeSrcDocuments = closeSourceDocuments;
        return this;
    }

    /**
     * This method merges pages from the source document to the current one.
     * <p>
     * If <i>closeSourceDocuments</i> flag is set to <i>true</i> (see {@link #setCloseSourceDocuments(boolean)}),
     * passed {@code PdfDocument} will be closed after pages are merged.
     * <p>
     * See also {@link com.itextpdf.kernel.pdf.PdfDocument#copyPagesTo}.
     *
     * @param from - document, from which pages will be copied
     * @param fromPage - start page in the range of pages to be copied
     * @param toPage - end (inclusive) page in the range to be copied
     * @return this {@code PdfMerger} instance
     */
    public PdfMerger merge(PdfDocument from, int fromPage, int toPage) {
        List<Integer> pages = new ArrayList<>(toPage - fromPage);
        for (int pageNum = fromPage; pageNum <= toPage; pageNum++){
            pages.add(pageNum);
        }
        return merge(from, pages);
    }

    /**
     * This method merges pages from the source document to the current one.
     * <p>
     * If <i>closeSourceDocuments</i> flag is set to <i>true</i> (see {@link #setCloseSourceDocuments(boolean)}),
     * passed {@code PdfDocument} will be closed after pages are merged.
     * <p>
     * See also {@link com.itextpdf.kernel.pdf.PdfDocument#copyPagesTo}.
     *
     * @param from - document, from which pages will be copied
     * @param pages - List of numbers of pages which will be copied
     * @return this {@code PdfMerger} instance
     */
    public PdfMerger merge(PdfDocument from, List<Integer> pages) {
        if (mergeTags && from.isTagged()) {
            pdfDocument.setTagged();
        }
        if (mergeOutlines && from.hasOutlines()) {
            pdfDocument.initializeOutlines();
        }

        from.copyPagesTo(pages, pdfDocument);
        if (closeSrcDocuments) {
            from.close();
        }
        return this;
    }

    /**
     * Closes the current document.
     * <p>
     * It is a complete equivalent of calling {@code PdfDocument#close} on the PdfDocument
     * passed to the constructor of this PdfMerger instance. This means that it is enough to call
     * <i>close</i> either on passed PdfDocument or on this PdfMerger instance, but there is no need
     * to call them both.
     */
    public void close() {
        pdfDocument.close();
    }
}
