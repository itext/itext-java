package com.itextpdf.layout.layout;

public class PositionedLayoutContext extends LayoutContext {

    private LayoutArea parentOccupiedArea;

    public PositionedLayoutContext(LayoutArea area, LayoutArea parentOccupiedArea) {
        super(area);
        this.parentOccupiedArea = parentOccupiedArea;
    }

    public LayoutArea getParentOccupiedArea() {
        return parentOccupiedArea;
    }

}
