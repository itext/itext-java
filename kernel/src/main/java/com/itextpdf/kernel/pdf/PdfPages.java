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
package com.itextpdf.kernel.pdf;

class PdfPages extends PdfObjectWrapper<PdfDictionary> {

	private static final long serialVersionUID = 404629033132277362L;
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
    }

    public boolean addPage(int index, PdfPage pdfPage) {
        if (index < from || index > from + getCount())
            return false;
        kids.add(index - from, pdfPage.getPdfObject());
        pdfPage.getPdfObject().put(PdfName.Parent, getPdfObject());
        incrementCount();
        setModified();
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
