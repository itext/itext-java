package com.itextpdf.model.element;

import com.itextpdf.canvas.draw.Drawable;
import com.itextpdf.model.Property;

public class TabStop {

    private float tabPosition;
    private Property.TabAlignment tabAlignment;
    private Character tabAnchor;
    private Drawable tabLeader;

    public TabStop(float tabPosition) {
        this(tabPosition, Property.TabAlignment.LEFT);
    }

    public TabStop(float tabPosition, Property.TabAlignment tabAlignment) {
        this(tabPosition, tabAlignment, null);
    }

    public TabStop(float tabPosition, Property.TabAlignment tabAlignment, Drawable tabLeader) {
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

    public Drawable getTabLeader() {
        return tabLeader;
    }
    public void setTabLeader(Drawable tabLeader) {
        this.tabLeader = tabLeader;
    }
}