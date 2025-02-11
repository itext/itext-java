/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.margincollapse;


public class MarginsCollapseInfo {
    private boolean ignoreOwnMarginTop;
    private boolean ignoreOwnMarginBottom;
    private MarginsCollapse collapseBefore;
    private MarginsCollapse collapseAfter;

    // MarginCollapse instance which contains margin-after of the element without next sibling or parent margins (only element's margin and element's kids)
    private MarginsCollapse ownCollapseAfter;
    private boolean isSelfCollapsing;

    // when a parent has a fixed height these fields tells kid how much free space parent has for the margin collapsed with kid
    private float bufferSpaceOnTop;
    private float bufferSpaceOnBottom;

    private float usedBufferSpaceOnTop;
    private float usedBufferSpaceOnBottom;

    private boolean clearanceApplied;

    MarginsCollapseInfo() {
        this.ignoreOwnMarginTop = false;
        this.ignoreOwnMarginBottom = false;
        this.collapseBefore = new MarginsCollapse();
        this.collapseAfter = new MarginsCollapse();
        this.isSelfCollapsing = true;
        this.bufferSpaceOnTop = 0;
        this.bufferSpaceOnBottom = 0;
        this.usedBufferSpaceOnTop = 0;
        this.usedBufferSpaceOnBottom = 0;
        this.clearanceApplied = false;
    }

    MarginsCollapseInfo(boolean ignoreOwnMarginTop, boolean ignoreOwnMarginBottom, MarginsCollapse collapseBefore, MarginsCollapse collapseAfter) {
        this.ignoreOwnMarginTop = ignoreOwnMarginTop;
        this.ignoreOwnMarginBottom = ignoreOwnMarginBottom;
        this.collapseBefore = collapseBefore;
        this.collapseAfter = collapseAfter;
        this.isSelfCollapsing = true;
        this.bufferSpaceOnTop = 0;
        this.bufferSpaceOnBottom = 0;
        this.usedBufferSpaceOnTop = 0;
        this.usedBufferSpaceOnBottom = 0;
        this.clearanceApplied = false;
    }

    public void copyTo(MarginsCollapseInfo destInfo) {
        destInfo.ignoreOwnMarginTop = this.ignoreOwnMarginTop;
        destInfo.ignoreOwnMarginBottom = this.ignoreOwnMarginBottom;
        destInfo.collapseBefore = this.collapseBefore;
        destInfo.collapseAfter = this.collapseAfter;

        destInfo.setOwnCollapseAfter(ownCollapseAfter);
        destInfo.setSelfCollapsing(isSelfCollapsing);
        destInfo.setBufferSpaceOnTop(bufferSpaceOnTop);
        destInfo.setBufferSpaceOnBottom(bufferSpaceOnBottom);
        destInfo.setUsedBufferSpaceOnTop(usedBufferSpaceOnTop);
        destInfo.setUsedBufferSpaceOnBottom(usedBufferSpaceOnBottom);

        destInfo.setClearanceApplied(clearanceApplied);
    }

    public static MarginsCollapseInfo createDeepCopy(MarginsCollapseInfo instance) {
        MarginsCollapseInfo copy = new MarginsCollapseInfo();
        instance.copyTo(copy);

        copy.collapseBefore = instance.collapseBefore.clone();
        copy.collapseAfter = instance.collapseAfter.clone();
        if (instance.ownCollapseAfter != null) {
            copy.setOwnCollapseAfter(instance.ownCollapseAfter.clone());
        }

        return copy;
    }

    public static void updateFromCopy(MarginsCollapseInfo originalInstance, MarginsCollapseInfo processedCopy) {
        originalInstance.ignoreOwnMarginTop = processedCopy.ignoreOwnMarginTop;
        originalInstance.ignoreOwnMarginBottom = processedCopy.ignoreOwnMarginBottom;

        originalInstance.collapseBefore.joinMargin(processedCopy.collapseBefore);
        originalInstance.collapseAfter.joinMargin(processedCopy.collapseAfter);

        if (processedCopy.getOwnCollapseAfter() != null) {
            if (originalInstance.getOwnCollapseAfter() == null) {
                originalInstance.setOwnCollapseAfter(new MarginsCollapse());
            }
            originalInstance.getOwnCollapseAfter().joinMargin(processedCopy.getOwnCollapseAfter());
        }
        originalInstance.setSelfCollapsing(processedCopy.isSelfCollapsing);
        originalInstance.setBufferSpaceOnTop(processedCopy.bufferSpaceOnTop);
        originalInstance.setBufferSpaceOnBottom(processedCopy.bufferSpaceOnBottom);
        originalInstance.setUsedBufferSpaceOnTop(processedCopy.usedBufferSpaceOnTop);
        originalInstance.setUsedBufferSpaceOnBottom(processedCopy.usedBufferSpaceOnBottom);

        originalInstance.setClearanceApplied(processedCopy.clearanceApplied);
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

    float getBufferSpaceOnTop() {
        return bufferSpaceOnTop;
    }

    void setBufferSpaceOnTop(float bufferSpaceOnTop) {
        this.bufferSpaceOnTop = bufferSpaceOnTop;
    }

    float getBufferSpaceOnBottom() {
        return bufferSpaceOnBottom;
    }

    void setBufferSpaceOnBottom(float bufferSpaceOnBottom) {
        this.bufferSpaceOnBottom = bufferSpaceOnBottom;
    }

    float getUsedBufferSpaceOnTop() {
        return usedBufferSpaceOnTop;
    }

    void setUsedBufferSpaceOnTop(float usedBufferSpaceOnTop) {
        this.usedBufferSpaceOnTop = usedBufferSpaceOnTop;
    }

    float getUsedBufferSpaceOnBottom() {
        return usedBufferSpaceOnBottom;
    }

    void setUsedBufferSpaceOnBottom(float usedBufferSpaceOnBottom) {
        this.usedBufferSpaceOnBottom = usedBufferSpaceOnBottom;
    }

    boolean isClearanceApplied() {
        return clearanceApplied;
    }

    void setClearanceApplied(boolean clearanceApplied) {
        this.clearanceApplied = clearanceApplied;
    }
}
