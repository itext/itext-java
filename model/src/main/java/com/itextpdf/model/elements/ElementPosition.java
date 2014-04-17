package com.itextpdf.model.elements;

import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.layout.ILayoutMgr;
import com.itextpdf.model.layout.shapes.ILayoutShape;

public class ElementPosition {

    public ElementPosition() {
        this(null);
    }

    public ElementPosition(ILayoutShape elementPosition) {
        this(PdfPage.CurrentPage, elementPosition, ILayoutMgr.Flowing);
    }

    public ElementPosition(int page, ILayoutShape elementPosition) {
        this(page, elementPosition, ILayoutMgr.Flowing);
    }

    public ElementPosition(int page, ILayoutShape elementPosition, int layout) {

    }

    public ElementPosition(ILayoutShape elementPosition, int layout) {
        this(PdfPage.CurrentPage, elementPosition, layout);
    }

}
