/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.util.ArrayList;
import java.util.List;


public class PdfMerger {

    private PdfDocument pdfDocument;
    private List<AddedPages> pagesToCopy = new ArrayList<>();

    /**
     * This class is used to merge a number of existing documents into one;
     * @param pdfDocument - the document into which source documents will be merged.
     */
    public PdfMerger(PdfDocument pdfDocument){
        this.pdfDocument = pdfDocument;
    }

    /**
     * This method adds pages from the source document to the List of pages which will be merged.
     * @param from - document, from which pages will be copied.
     * @param fromPage - start page in the range of pages to be copied.
     * @param toPage - end page in the range to be copied.
     * @throws PdfException
     */
    public void addPages(PdfDocument from, int fromPage, int toPage) {
        for (int pageNum = fromPage; pageNum <= toPage; pageNum++){
            enqueuePageToCopy(from, pageNum);
        }
    }

    /**
     * This method adds pages from the source document to the List of pages which will be merged.
     * @param from - document, from which pages will be copied.
     * @param pages - List of numbers of pages which will be copied.
     * @throws PdfException
     */
    public void addPages(PdfDocument from, List<Integer> pages) {
        for (Integer pageNum : pages){
            enqueuePageToCopy(from, pageNum);
        }
    }

    /**
     * This method gets all pages from the List of pages to be copied and merges them into one document.
     * @throws PdfException
     */
    public void merge() {
        for (AddedPages addedPages : pagesToCopy) {
            addedPages.from.copyPagesTo(addedPages.pagesToCopy, pdfDocument );
        }
    }

    /**
     * This method adds to the List of pages to be copied with given page.
     * Pages are stored along with their documents.
     * If last added page belongs to the same document as the new one, new page is added to the previous {@code AddedPages} instance.
     * @param from - document, from which pages will be copied.
     * @param pageNum - number of page to be copied.
     * @throws PdfException
     */
    private void enqueuePageToCopy(PdfDocument from, int pageNum) {
        if (!pagesToCopy.isEmpty()) {
            AddedPages lastAddedPagesEntry = pagesToCopy.get(pagesToCopy.size() - 1);
            if (lastAddedPagesEntry.from == from) {
                lastAddedPagesEntry.pagesToCopy.add(pageNum);
            } else {
                pagesToCopy.add(new AddedPages(from, pageNum));
            }
        } else {
            pagesToCopy.add(new AddedPages(from, pageNum));
        }
    }

    static class AddedPages {
        public AddedPages(PdfDocument from, int pageNum) {
            this.from = from;
            this.pagesToCopy = new ArrayList<>();
            this.pagesToCopy.add(pageNum);
        }

        PdfDocument from;
        List<Integer> pagesToCopy;
    }
}
