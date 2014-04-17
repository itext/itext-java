package com.itextpdf.model.elements;

import java.util.ArrayList;
import java.util.List;

public class Paragraph implements IAccessibleElement {

    /**
     * Filled in by user. This is a position user wants to place element to.
     */
    protected ElementPosition requestedPosition = null;

    /**
     * Filled in by layout manager after placing element to the document.
     * It may differ from requested position if layout manager setting do not allow to place element as requested.
     */
    protected List<ElementPosition> calculatedPosition = null;

    public Paragraph(String text) {

    }

    public ElementPosition getRequestedPosition() {
        return requestedPosition;
    }

    public void setRequestedPosition(ElementPosition elementPosition) {
        this.requestedPosition = elementPosition;
    }

}
