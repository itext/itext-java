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

    /**
     * @param page            a page where element should be placed.
     * @param elementPosition a position on the page where element should be placed.
     * @param layout          either fixed or flowing.
     */
    public ElementPosition(int page, ILayoutShape elementPosition, int layout) {

    }

    public ElementPosition(ILayoutShape elementPosition, int layout) {
        this(PdfPage.LastPage, elementPosition, layout);
    }

}
