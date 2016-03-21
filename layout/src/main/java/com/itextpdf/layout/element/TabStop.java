package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.canvas.draw.LineDrawer;
import com.itextpdf.layout.Property;

public class TabStop {

    private float tabPosition;
    private Property.TabAlignment tabAlignment;
    private Character tabAnchor;
    private LineDrawer tabLeader;

    public TabStop(float tabPosition) {
        this(tabPosition, Property.TabAlignment.LEFT);
    }

    public TabStop(float tabPosition, Property.TabAlignment tabAlignment) {
        this(tabPosition, tabAlignment, null);
    }

    public TabStop(float tabPosition, Property.TabAlignment tabAlignment, LineDrawer tabLeader) {
        this.tabPosition = tabPosition;
        this.tabAlignment = tabAlignment;
        this.tabLeader = tabLeader;

        this.tabAnchor = '.';
    }

    public float getTabPosition() {
        return tabPosition;
    }

    public Property.TabAlignment getTabAlignment() {
        return tabAlignment;
    }

    public void setTabAlignment(Property.TabAlignment tabAlignment) {
        this.tabAlignment = tabAlignment;
    }

    public Character getTabAnchor() {
        return tabAnchor;
    }

    public void setTabAnchor(Character tabAnchor) {
        this.tabAnchor = tabAnchor;
    }

    public LineDrawer getTabLeader() {
        return tabLeader;
    }
    public void setTabLeader(LineDrawer tabLeader) {
        this.tabLeader = tabLeader;
    }
}