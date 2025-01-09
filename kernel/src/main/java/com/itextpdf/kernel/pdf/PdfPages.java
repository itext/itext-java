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
package com.itextpdf.kernel.pdf;

class PdfPages extends PdfObjectWrapper<PdfDictionary> {

	private int from;
    private PdfNumber count;
    private final PdfArray kids;
    private final PdfPages parent;

    public PdfPages(int from, PdfDocument pdfDocument, PdfPages parent) {
        super(new PdfDictionary());
        if (pdfDocument.getWriter() != null) {
            getPdfObject().makeIndirect(pdfDocument);
        }
        setForbidRelease();
        this.from = from;
        this.count = new PdfNumber(0);
        this.kids = new PdfArray();
        this.parent = parent;
        getPdfObject().put(PdfName.Type, PdfName.Pages);
        getPdfObject().put(PdfName.Kids, this.kids);
        getPdfObject().put(PdfName.Count, this.count);
        if (parent != null) {
            getPdfObject().put(PdfName.Parent, this.parent.getPdfObject());
        }
    }

    public PdfPages(int from, PdfDocument pdfDocument) {
        this(from, pdfDocument, null);
    }

    public PdfPages(int from, int maxCount, PdfDictionary pdfObject, PdfPages parent) {
        super(pdfObject);
        setForbidRelease();
        this.from = from;
        this.count = pdfObject.getAsNumber(PdfName.Count);
        this.parent = parent;
        if (this.count == null) {
            this.count = new PdfNumber(1);
            pdfObject.put(PdfName.Count, this.count);
        } else if (maxCount < this.count.intValue()) {
            this.count.setValue(maxCount);
        }
        this.kids = pdfObject.getAsArray(PdfName.Kids);
        pdfObject.put(PdfName.Type, PdfName.Pages);
    }

    public void addPage(PdfDictionary page) {
        kids.add(page);
        incrementCount();
        page.put(PdfName.Parent, getPdfObject());
        page.setModified();
    }

    public boolean addPage(int index, PdfPage pdfPage) {
        if (index < from || index > from + getCount())
            return false;
        kids.add(index - from, pdfPage.getPdfObject());
        pdfPage.getPdfObject().put(PdfName.Parent, getPdfObject());
        pdfPage.setModified();
        incrementCount();
        return true;
    }

    public boolean removePage(int pageNum) {
        if (pageNum < from || pageNum >= from + getCount())
            return false;
        decrementCount();
        kids.remove(pageNum - from);
        return true;
    }

    public void addPages(PdfPages pdfPages) {
        kids.add(pdfPages.getPdfObject());
        count.setValue(count.intValue() + pdfPages.getCount());
        pdfPages.getPdfObject().put(PdfName.Parent, getPdfObject());
        pdfPages.setModified();
        setModified();
    }

    // remove empty PdfPage.
    public void removeFromParent() {
        if (parent != null) {
            assert getCount() == 0;
            parent.kids.remove(getPdfObject().getIndirectReference());
            if (parent.getCount() == 0) {
                parent.removeFromParent();
            }
        }
    }

    public int getFrom() {
        return from;
    }

    public int getCount() {
        return count.intValue();
    }

    public void correctFrom(int correction){
        from += correction;
    }

    public PdfArray getKids() {
        return getPdfObject().getAsArray(PdfName.Kids);
    }

    public PdfPages getParent() {
        return parent;
    }

    public void incrementCount(){
        count.increment();
        setModified();
        if (parent != null)
            parent.incrementCount();
    }

    public void decrementCount(){
        count.decrement();
        setModified();
        if (parent != null)
            parent.decrementCount();
    }

    public int compareTo(int index) {
        if (index < from)
            return 1;
        if (index >= from + getCount())
            return -1;
        return 0;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
