package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

class PdfPages extends PdfObjectWrapper<PdfDictionary> {
    private int from;
    private PdfNumber count;
    private final PdfArray kids;

    public PdfPages(int from, PdfDocument pdfDocument) {
        super(new PdfDictionary(), pdfDocument);
        this.from = from;
        this.count = new PdfNumber(0);
        this.kids = new PdfArray();
        pdfObject.put(PdfName.Type, PdfName.Pages);
        pdfObject.put(PdfName.Kids, this.kids);
        pdfObject.put(PdfName.Count, this.count);
    }

    public PdfPages(int from, PdfDictionary pdfObject, int maxCount) throws PdfException {
        super(pdfObject);
        this.from = from;
        this.count = pdfObject.getAsNumber(PdfName.Count);
        if (this.count == null) {
            this.count = new PdfNumber(1);
            pdfObject.put(PdfName.Count, this.count);
        } else if (maxCount < this.count.getIntValue()) {
            this.count.setValue(maxCount);
        }
        this.kids = pdfObject.getAsArray(PdfName.Kids);
        pdfObject.put(PdfName.Type, PdfName.Pages);
    }

    public boolean addPage(int index, PdfPage pdfPage) throws PdfException {
        if (index < from || index > from + getCount())
            return false;
        kids.add(index - from, pdfPage.getPdfObject());
        pdfPage.getPdfObject().put(PdfName.Parent, getPdfObject());
        count.increment();
        updateModifiedState(pdfPage.getPdfObject());
        return true;
    }

    public boolean remove(int pageNum) throws PdfException {
        if (pageNum < from || pageNum >= from + getCount())
            return false;
        count.decrement();
        kids.remove(pageNum - from);
        setModified();
        return true;
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

    public void addPage(PdfDictionary page) throws PdfException {
        kids.add(page);
        count.increment();
        page.put(PdfName.Parent, getPdfObject());
        updateModifiedState(page);
    }

    public void addPages(PdfPages pdfPages) {
        kids.add(pdfPages.getPdfObject());
        count.setValue(count.getIntValue() + pdfPages.getCount());
        pdfPages.getPdfObject().put(PdfName.Parent, getPdfObject());
        updateModifiedState(pdfPages.getPdfObject());
    }

    public PdfArray getKids() throws PdfException {
        return pdfObject.getAsArray(PdfName.Kids);
    }

    public void updateModifiedState(PdfObject newObject){
        if (newObject.isModified()) {
            setModified();
        }
    }

    public int compareTo(int index) {
        if (index < from)
            return 1;
        if (index >= from + getCount())
            return -1;
        return 0;
    }
}
