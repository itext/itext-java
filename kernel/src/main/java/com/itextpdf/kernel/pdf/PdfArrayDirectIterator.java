package com.itextpdf.kernel.pdf;

import java.util.Iterator;

class PdfArrayDirectIterator implements Iterator<PdfObject> {
    Iterator<PdfObject> array;

    PdfArrayDirectIterator(Iterator<PdfObject> array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return array.hasNext();
    }

    @Override
    public PdfObject next() {
        PdfObject obj = array.next();
        if (obj.isIndirectReference()) {
            obj = ((PdfIndirectReference) obj).getRefersTo(true);
        }
        return obj;
    }

    @Override
    public void remove() {
        array.remove();
    }
}

