package com.itextpdf.model.elements;

import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.layout.ILayoutMgr;
import com.itextpdf.model.layout.shapes.ILayoutShape;

/**
 * Together with ILayoutShape specifies the element position in document.
 */
public class ElementPosition {

    public ElementPosition() {
        this(null);
    }

    public ElementPosition(ILayoutShape elementPosition) {
        this(PdfPage.LastPage, elementPosition, ILayoutMgr.Flowing);
    }

    public ElementPosition(int page, ILayoutShape elementPosition) {
        this(page, elementPosition, ILayoutMgr.Flowing);
    }

    public ElementPosition(int page, ILayoutShape elementPosition, int layout) {

    }

    public ElementPosition(ILayoutShape elementPosition, int layout) {
        this(PdfPage.LastPage, elementPosition, layout);
    }

}
