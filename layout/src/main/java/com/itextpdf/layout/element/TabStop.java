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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.properties.TabAlignment;

/**
 * A TabStop is the closest location on a line of text that the text will jump
 * to if a {@link Tab} is inserted. At least one TabStop must be defined on an
 * element if you want to use {@link Tab Tabs}.
 * 
 * This object can be added to a {@link Paragraph} with the method
 * {@link Paragraph#addTabStops}.
 */
public class TabStop {

    // tabPosition here is absolute value
    private float tabPosition;
    private TabAlignment tabAlignment;
    private Character tabAnchor;
    private ILineDrawer tabLeader;

    /**
     * Creates a TabStop at the appropriate position.
     * @param tabPosition a <code>float</code>, measured in absolute points
     */
    public TabStop(float tabPosition) {
        this(tabPosition, TabAlignment.LEFT);
    }

    /**
     * Creates a TabStop at the appropriate position, with a specified tab
     * alignment. A tab alignment defines the way the textual content should be
     * positioned with regards to this tab stop.
     * 
     * @param tabPosition a <code>float</code>, measured in absolute points
     * @param tabAlignment a {@link TabAlignment} value
     */
    public TabStop(float tabPosition, TabAlignment tabAlignment) {
        this(tabPosition, tabAlignment, null);
    }

    /**
     * Creates a TabStop at the appropriate position, with a specified tab
     * alignment and an explicitly given line pattern. A tab alignment defines
     * the way the textual content should be positioned with regards to this tab
     * stop. The line pattern defines a pattern that should be repeated until
     * the TabStop is reached. If null, the space leading up to the TabStop will
     * be empty.
     * @param tabPosition a <code>float</code>, measured in absolute points
     * @param tabAlignment a {@link TabAlignment} value
     * @param tabLeader the {@link ILineDrawer} value, a pattern drawing object
     */
    public TabStop(float tabPosition, TabAlignment tabAlignment, ILineDrawer tabLeader) {
        this.tabPosition = tabPosition;
        this.tabAlignment = tabAlignment;
        this.tabLeader = tabLeader;
        this.tabAnchor = '.';
    }

    /**
     * Returns the position of a tab stop.
     *
     * @return tabPosition, measured in absolute points
     */
    public float getTabPosition() {
        return tabPosition;
    }

    /**
     * Returns the alignment of a tab stop, which defines the way the textual content
     * should be positioned in regard to this tab stop.
     *
     * @return a {@link TabAlignment} value
     */
    public TabAlignment getTabAlignment() {
        return tabAlignment;
    }

    /**
     * Sets the alignment, which defines the way the textual content
     * should be positioned in regard to this tab stop.
     *
     * @param tabAlignment a {@link TabAlignment} value
     */
    public void setTabAlignment(TabAlignment tabAlignment) {
        this.tabAlignment = tabAlignment;
    }

    /**
     * Returns the anchor of a tab stop.
     *
     * @return a {@link Character} value
     */
    public Character getTabAnchor() {
        return tabAnchor;
    }

    /**
     * Sets the anchor of a tab stop.
     *
     * @param tabAnchor a {@link Character} value
     */
    public void setTabAnchor(Character tabAnchor) {
        this.tabAnchor = tabAnchor;
    }

    /**
     * Returns the tab leader of a tab stop, which defines a pattern that
     * should be repeated until the TabStop is reached.
     *
     * @return a {@link ILineDrawer} value, a pattern drawing object
     */
    public ILineDrawer getTabLeader() {
        return tabLeader;
    }

    /**
     * Sets the tab leader of a tab stop, which defines a pattern that
     * should be repeated until the TabStop is reached.
     *
     * @param tabLeader a {@link ILineDrawer} value
     */
    public void setTabLeader(ILineDrawer tabLeader) {
        this.tabLeader = tabLeader;
    }
}
