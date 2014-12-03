package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

class PdfPages extends PdfObjectWrapper<PdfDictionary> {
    private int from;
    private PdfNumber count;
    private final PdfArray kids;
    private final PdfPages parent;

    public PdfPages(int from, PdfDocument pdfDocument, PdfPages parent) {
        super(new PdfDictionary(), pdfDocument);
        this.from = from;
        this.count = new PdfNumber(0);
        this.kids = new PdfArray();
        this.parent = parent;
        pdfObject.put(PdfName.Type, PdfName.Pages);
        pdfObject.put(PdfName.Kids, this.kids);
        pdfObject.put(PdfName.Count, this.count);
    }

    public PdfPages(int from, PdfDocument pdfDocument) {
        this(from, pdfDocument, null);
    }

    public PdfPages(int from, int maxCount, PdfDictionary pdfObject, PdfPages parent) throws PdfException {
        super(pdfObject);
        this.from = from;
        this.count = pdfObject.getAsNumber(PdfName.Count);
        this.parent = parent;
        if (this.count == null) {
            this.count = new PdfNumber(1);
            pdfObject.put(PdfName.Count, this.count);
        } else if (maxCount < this.count.getIntValue()) {
            this.count.setValue(maxCount);
        }
        this.kids = pdfObject.getAsArray(PdfName.Kids);
        pdfObject.put(PdfName.Type, PdfName.Pages);
    }

    public void addPage(PdfDictionary page) throws PdfException {
        kids.add(page);
        incrementCount();
        page.put(PdfName.Parent, getPdfObject());
    }

    public boolean addPage(int index, PdfPage pdfPage) throws PdfException {
        if (index < from || index > from + getCount())
            return false;
        kids.add(index - from, pdfPage.getPdfObject());
        pdfPage.getPdfObject().put(PdfName.Parent, getPdfObject());
        incrementCount();
        setModified();
        return true;
    }

    public boolean removePage(int pageNum) throws PdfException {
        if (pageNum < from || pageNum >= from + getCount())
            return false;
        decrementCount();
        kids.remove(pageNum - from);
        return true;
    }

    public void addPages(PdfPages pdfPages) {
        kids.add(pdfPages.getPdfObject());
        count.setValue(count.getIntValue() + pdfPages.getCount());
        pdfPages.getPdfObject().put(PdfName.Parent, getPdfObject());
        setModified();
    }

    // remove empty PdfPage.
    public void removeFromParent() {
        if (parent != null) {
            assert getCount() == 0;
            parent.kids.remove(getPdfObject());
            if (parent.getCount() == 0) {
                parent.removeFromParent();
            }
        }
    }

    public int getFrom() {
        return from;
    }

    public int getCount() {
        return count.getIntValue();
    }

    public void correctFrom(int correction){
        from += correction;
    }

    public PdfArray getKids() throws PdfException {
        return pdfObject.getAsArray(PdfName.Kids);
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
}
