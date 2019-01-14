/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.property.TabAlignment;

/**
 * A TabStop is the closest location on a line of text that the text will jump
 * to if a {@link Tab} is inserted. At least one TabStop must be defined on an
 * element if you want to use {@link Tab Tabs}.
 * 
 * This object can be added to a {@link Paragraph} with the method
 * {@link Paragraph#addTabStops}.
 */
public class TabStop {

    private float tabPosition;
    private TabAlignment tabAlignment;
    private Character tabAnchor;
    private ILineDrawer tabLeader;

    /**
     * Creates a TabStop at the appropriate position.
     * @param tabPosition a <code>float</code>, measured in points
     */
    public TabStop(float tabPosition) {
        this(tabPosition, TabAlignment.LEFT);
    }

    /**
     * Creates a TabStop at the appropriate position, with a specified tab
     * alignment. A tab alignment defines the way the textual content should be
     * positioned with regards to this tab stop.
     * 
     * @param tabPosition a <code>float</code>, measured in points
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
     * @param tabPosition a <code>float</code>, measured in points
     * @param tabAlignment a {@link TabAlignment} value
     * @param tabLeader the {@link ILineDrawer} value, a pattern drawing object
     */
    public TabStop(float tabPosition, TabAlignment tabAlignment, ILineDrawer tabLeader) {
        this.tabPosition = tabPosition;
        this.tabAlignment = tabAlignment;
        this.tabLeader = tabLeader;
        this.tabAnchor = '.';
    }

    public float getTabPosition() {
        return tabPosition;
    }

    public TabAlignment getTabAlignment() {
        return tabAlignment;
    }

    public void setTabAlignment(TabAlignment tabAlignment) {
        this.tabAlignment = tabAlignment;
    }

    public Character getTabAnchor() {
        return tabAnchor;
    }

    public void setTabAnchor(Character tabAnchor) {
        this.tabAnchor = tabAnchor;
    }

    public ILineDrawer getTabLeader() {
        return tabLeader;
    }

    public void setTabLeader(ILineDrawer tabLeader) {
        this.tabLeader = tabLeader;
    }
}
