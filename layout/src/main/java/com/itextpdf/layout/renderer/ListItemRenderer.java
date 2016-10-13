/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.HeightType;
import com.itextpdf.layout.property.ListSymbolAlignment;
import com.itextpdf.layout.property.Property;

public class ListItemRenderer extends DivRenderer {

    protected IRenderer symbolRenderer;
    protected float symbolAreaWidth;

    /**
     * Creates a ListItemRenderer from its corresponding layout object.
     * @param modelElement the {@link com.itextpdf.layout.element.ListItem} which this object should manage
     */
    public ListItemRenderer(ListItem modelElement) {
        super(modelElement);
    }

    public void addSymbolRenderer(IRenderer symbolRenderer, float symbolAreaWidth) {
        this.symbolRenderer = symbolRenderer;
        this.symbolAreaWidth = symbolAreaWidth;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        if (symbolRenderer != null && this.<Object>getProperty(Property.HEIGHT) == null) {
            // TODO this is actually MinHeight.
            setProperty(Property.HEIGHT, symbolRenderer.getOccupiedArea().getBBox().getHeight());
            setProperty(Property.HEIGHT_TYPE, HeightType.MIN_HEIGHT);
        }
        return super.layout(layoutContext);
    }

    @Override
    public void draw(DrawContext drawContext) {
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            tagPointer = drawContext.getDocument().getTagStructureContext().getAutoTaggingPointer();
            IAccessibleElement modelElement = (IAccessibleElement) getModelElement();
            PdfName role = modelElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                boolean lBodyTagIsCreated = tagPointer.isElementConnectedToTag(modelElement);
                if (!lBodyTagIsCreated) {
                    tagPointer.addTag(PdfName.LI);
                } else {
                    tagPointer.moveToTag(modelElement).moveToParent();
                }
            } else {
                isTagged = false;
            }
        }

        super.draw(drawContext);

        // It will be null in case of overflow (only the "split" part will contain symbol renderer.
        if (symbolRenderer != null) {
            symbolRenderer.setParent(parent);
            float x = occupiedArea.getBBox().getX();
            if (childRenderers.size() > 0) {
                Float yLine = ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
                if (yLine != null) {
                    if (symbolRenderer instanceof TextRenderer) {
                        ((TextRenderer) symbolRenderer).moveYLineTo((float) yLine);
                    } else {
                        symbolRenderer.move(0, (float) yLine - symbolRenderer.getOccupiedArea().getBBox().getY());
                    }
                } else {
                    symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                            (symbolRenderer.getOccupiedArea().getBBox().getY() + symbolRenderer.getOccupiedArea().getBBox().getHeight()));
                }
            } else {
                symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                        symbolRenderer.getOccupiedArea().getBBox().getHeight() - symbolRenderer.getOccupiedArea().getBBox().getY());
            }

            ListSymbolAlignment listSymbolAlignment = (ListSymbolAlignment)parent.<ListSymbolAlignment>getProperty(Property.LIST_SYMBOL_ALIGNMENT,
                    ListSymbolAlignment.RIGHT);
            float xPosition = x - symbolRenderer.getOccupiedArea().getBBox().getX();
            if (listSymbolAlignment == ListSymbolAlignment.RIGHT) {
                xPosition += symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth();
            }
            symbolRenderer.move(xPosition, 0);

            if (isTagged) {
                tagPointer.addTag(0, PdfName.Lbl);
            }
            symbolRenderer.draw(drawContext);
            if (isTagged) {
                tagPointer.moveToParent();
            }
        }

        if (isTagged) {
            tagPointer.moveToParent();
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return new ListItemRenderer((ListItem) modelElement);
    }

    @Override
    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        ListItemRenderer splitRenderer = (ListItemRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        if (layoutResult == LayoutResult.PARTIAL) {
            splitRenderer.symbolRenderer = symbolRenderer;
            splitRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        splitRenderer.setProperty(Property.MARGIN_LEFT, this.<Object>getProperty(Property.MARGIN_LEFT));
        return splitRenderer;
    }

    @Override
    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        ListItemRenderer overflowRenderer = (ListItemRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        if (layoutResult == LayoutResult.NOTHING) {
            overflowRenderer.symbolRenderer = symbolRenderer;
            overflowRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        overflowRenderer.setProperty(Property.MARGIN_LEFT, this.<Object>getProperty(Property.MARGIN_LEFT));
        return overflowRenderer;
    }
}
