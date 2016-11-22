package com.itextpdf.layout.margincollapse;

public class MarginsCollapseInfo {
    private boolean ignoreOwnMarginTop;
    private boolean ignoreOwnMarginBottom;
    private MarginsCollapse collapseBefore;
    private MarginsCollapse collapseAfter;

    // MarginCollapse instance which contains margin-after of the element without next sibling or parent margins (only element's margin and element's kids)
    private MarginsCollapse ownCollapseAfter;
    private boolean isSelfCollapsing;

    // when a parent has a fixed height this field tells kid how much free space parent has for the margin collapsed with kid
    private float bufferSpace;

    MarginsCollapseInfo() {
        this.ignoreOwnMarginTop = false;
        this.ignoreOwnMarginBottom = false;
        this.collapseBefore = new MarginsCollapse();
        this.collapseAfter = new MarginsCollapse();
        this.isSelfCollapsing = true;
        this.bufferSpace = 0;
    }

    MarginsCollapseInfo(boolean ignoreOwnMarginTop, boolean ignoreOwnMarginBottom, MarginsCollapse collapseBefore, MarginsCollapse collapseAfter) {
        this.ignoreOwnMarginTop = ignoreOwnMarginTop;
        this.ignoreOwnMarginBottom = ignoreOwnMarginBottom;
        this.collapseBefore = collapseBefore;
        this.collapseAfter = collapseAfter;
        this.isSelfCollapsing = true;
        this.bufferSpace = 0;
    }

    MarginsCollapse getCollapseBefore() {
        return this.collapseBefore;
    }
    MarginsCollapse getCollapseAfter() {
        return collapseAfter;
    }
    void setCollapseAfter(MarginsCollapse collapseAfter) {
        this.collapseAfter = collapseAfter;
    }
    MarginsCollapse getOwnCollapseAfter() {
        return ownCollapseAfter;
    }
    void setOwnCollapseAfter(MarginsCollapse marginsCollapse) {
        this.ownCollapseAfter = marginsCollapse;
    }

    void setSelfCollapsing(boolean selfCollapsing) {
        isSelfCollapsing = selfCollapsing;
    }

    boolean isSelfCollapsing() {
        return isSelfCollapsing;
    }

    boolean isIgnoreOwnMarginTop() {
        return ignoreOwnMarginTop;
    }

    boolean isIgnoreOwnMarginBottom() {
        return ignoreOwnMarginBottom;
    }

    float getBufferSpace() {
        return bufferSpace;
    }

    void setBufferSpace(float bufferSpace) {
        this.bufferSpace = bufferSpace;
    }
}
