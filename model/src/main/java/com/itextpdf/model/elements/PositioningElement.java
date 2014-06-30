package com.itextpdf.model.elements;

public class PositioningElement implements IAccessibleElement {

    public ElementPosition getRequestedPosition() {
        return null;
    }

    /**
     * Sets the requested position for element.
     * It is not necessarily that element will be placed as requested. It is decided by the layout manager if to place the element as requested or not.
     * @param elementPosition
     */
    public void setRequestedPosition(ElementPosition elementPosition) {

    }
}
