/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

import java.util.ArrayList;
import java.util.List;

/**
 * Document outline object
 * See ISO-320001, 12.3.3 Document Outline.
 */
public class PdfOutline {
    /**
     * A flag for displaying the outline item’s text with italic font.
     */
    public static final int FLAG_ITALIC = 1;
    /**
     * A flag for displaying the outline item’s text with bold font.
     */
    public static final int FLAG_BOLD = 2;

    private List<PdfOutline> children = new ArrayList<>();
    private String title;
    private PdfDictionary content;
    private PdfDestination destination;
    private PdfOutline parent;
    private PdfDocument pdfDoc;

    /**
     * Create instance of document outline.
     *
     * @param title       the text that shall be displayed on the screen for this item.
     * @param content     Outline dictionary
     * @param pdfDocument {@link PdfDocument} the outline belongs to.
     */
    PdfOutline(String title, PdfDictionary content, PdfDocument pdfDocument) {
        this.title = title;
        this.content = content;
        this.pdfDoc = pdfDocument;
    }

    /**
     * Create instance of document outline.
     *
     * @param title   the text that shall be displayed on the screen for this item.
     * @param content Outline dictionary
     * @param parent  parent outline.
     *                {@link #addOutline(String, int)} and {@link #addOutline(String)} instead.
     */
    PdfOutline(String title, PdfDictionary content, PdfOutline parent) {
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.pdfDoc = parent.pdfDoc;
        content.makeIndirect(parent.pdfDoc);
    }

    /**
     * This constructor creates root outline in the document.
     *
     * @param doc {@link PdfDocument}
     */
    PdfOutline(PdfDocument doc) {
        content = new PdfDictionary();
        content.put(PdfName.Type, PdfName.Outlines);
        this.pdfDoc = doc;
        content.makeIndirect(doc);
        doc.getCatalog().addRootOutline(this);
    }

    /**
     * Gets title of the outline.
     *
     * @return String value.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of the outline with {@link PdfEncodings#UNICODE_BIG} encoding,
     * {@code Title} key.
     *
     * @param title String value.
     */
    public void setTitle(String title) {
        this.title = title;
        this.content.put(PdfName.Title, new PdfString(title, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Sets color for the outline entry’s text, {@code C} key.
     *
     * @param color {@link Color}
     */
    public void setColor(Color color) {
        content.put(PdfName.C, new PdfArray(color.getColorValue()));
    }

    /**
     * Gets color for the outline entry's text, {@code C} key.
     *
     * @return color {@link Color}.
     */
    public Color getColor() {
        PdfArray colorArray = content.getAsArray(PdfName.C);
        if (colorArray == null) {
            return null;
        } else {
            return Color.makeColor(PdfColorSpace.makeColorSpace(PdfName.DeviceRGB),
                    colorArray.toFloatArray());
        }
    }

    /**
     * Sets text style for the outline entry’s text, {@code F} key.
     *
     * @param style Could be either {@link #FLAG_BOLD} or {@link #FLAG_ITALIC}. Default value is {@code 0}.
     */
    public void setStyle(int style) {
        if (style == FLAG_BOLD || style == FLAG_ITALIC) {
            content.put(PdfName.F, new PdfNumber(style));
        }
    }

    /**
     * Gets text style for the outline entry's text, {@code F} key.
     *
     * @return style value.
     */
    public Integer getStyle() {
        return content.getAsInt(PdfName.F);
    }

    /**
     * Gets content dictionary.
     *
     * @return {@link PdfDictionary}.
     */
    public PdfDictionary getContent() {
        return content;
    }

    /**
     * Gets list of children outlines.
     *
     * @return List of {@link PdfOutline}.
     */
    public List<PdfOutline> getAllChildren() {
        return children;
    }

    /**
     * Gets parent outline.
     *
     * @return {@link PdfOutline}.
     */
    public PdfOutline getParent() {
        return parent;
    }

    /**
     * Gets {@link PdfDestination}.
     *
     * @return {@link PdfDestination}.
     */
    public PdfDestination getDestination() {
        return destination;
    }

    /**
     * Adds {@link PdfDestination} for the outline, {@code Dest} key.
     *
     * @param destination instance of {@link PdfDestination}.
     */
    public void addDestination(PdfDestination destination) {
        setDestination(destination);
        content.put(PdfName.Dest, destination.getPdfObject());
    }

    /**
     * Adds {@link PdfAction} for the outline, {@code A} key.
     *
     * @param action instance of {@link PdfAction}.
     */
    public void addAction(PdfAction action) {
        PdfName actionType = action.getPdfObject().getAsName(PdfName.S);
        if (PdfName.GoTo.equals(actionType)) {
            PdfObject destObject = action.getPdfObject().get(PdfName.D);
            if (destObject != null) {
                setDestination(PdfDestination.makeDestination(destObject));
            }
        }

        content.put(PdfName.A, action.getPdfObject());
    }

    /**
     * Defines if the outline needs to be closed or not.
     * By default, outlines are open.
     *
     * @param open if false, the outline will be closed by default
     */
    public void setOpen(boolean open) {
        if (!open)
            content.put(PdfName.Count, new PdfNumber(-1));
        else if (children.size() > 0)
            content.put(PdfName.Count, new PdfNumber(children.size()));
        else
            content.remove(PdfName.Count);
    }

    /**
     * Defines if the outline is open or closed.
     *
     * @return true if open,false otherwise.
     */
    public boolean isOpen() {
        Integer count = content.getAsInt(PdfName.Count);
        return count == null || count >= 0;
    }

    /**
     * Adds a new {@code PdfOutline} with specified parameters as a child to existing {@code PdfOutline}
     * and put it to specified position in the existing {@code PdfOutline} children list.
     *
     * @param title    an outline title
     * @param position a position in the current outline child List where a new outline should be added.
     *                 If the position equals -1, then the outline will be put in the end of children list.
     * @return just created outline
     */
    public PdfOutline addOutline(String title, int position) {
        if (position == -1)
            position = children.size();
        PdfDictionary dictionary = new PdfDictionary();
        PdfOutline outline = new PdfOutline(title, dictionary, this);
        dictionary.put(PdfName.Title, new PdfString(title, PdfEncodings.UNICODE_BIG));
        dictionary.put(PdfName.Parent, content);
        if (children.size() > 0) {
            if (position != 0) {
                PdfDictionary prevContent = children.get(position - 1).getContent();
                dictionary.put(PdfName.Prev, prevContent);
                prevContent.put(PdfName.Next, dictionary);
            }
            if (position != children.size()) {
                PdfDictionary nextContent = children.get(position).getContent();
                dictionary.put(PdfName.Next, nextContent);
                nextContent.put(PdfName.Prev, dictionary);
            }
        }

        if (position == 0)
            content.put(PdfName.First, dictionary);
        if (position == children.size())
            content.put(PdfName.Last, dictionary);

        PdfNumber count = this.content.getAsNumber(PdfName.Count);
        if (count == null || count.getValue() != -1) {
            content.put(PdfName.Count, new PdfNumber(children.size() + 1));
        }
        children.add(position, outline);

        return outline;
    }

    /**
     * Adds an {@code PdfOutline} as a child to existing {@code PdfOutline}
     * and put it in the end of the existing {@code PdfOutline} children list.
     *
     * @param title an outline title
     * @return just created outline
     */
    public PdfOutline addOutline(String title) {
        return addOutline(title, -1);
    }

    /**
     * Adds an {@code PdfOutline} as a child to existing {@code PdfOutline}
     * and put it to the end of the existing {@code PdfOutline} children list.
     *
     * @param outline an outline to add.
     * @return just created outline
     */
    public PdfOutline addOutline(PdfOutline outline) {
        PdfOutline newOutline = addOutline(outline.getTitle());
        newOutline.addDestination(outline.getDestination());

        List<PdfOutline> children = outline.getAllChildren();
        for (PdfOutline child : children) {
            newOutline.addOutline(child);
        }

        return newOutline;
    }

    /**
     * Remove this outline from the document. Outlines that are children of this outline are removed recursively
     */
    public void removeOutline() {
        if (!pdfDoc.hasOutlines() || isOutlineRoot()) {
            pdfDoc.getCatalog().remove(PdfName.Outlines);
            return;
        }
        PdfOutline parent = this.parent;
        List<PdfOutline> children = parent.children;
        children.remove(this);
        PdfDictionary parentContent = parent.content;
        if (children.size() > 0) {
            parentContent.put(PdfName.First, children.get(0).content);
            parentContent.put(PdfName.Last, children.get(children.size() - 1).content);
        } else {
            parent.removeOutline();
            return;
        }

        PdfDictionary next = content.getAsDictionary(PdfName.Next);
        PdfDictionary prev = content.getAsDictionary(PdfName.Prev);
        if (prev != null) {
            if (next != null) {
                prev.put(PdfName.Next, next);
                next.put(PdfName.Prev, prev);
            } else {
                prev.remove(PdfName.Next);
            }
        } else if (next != null) {
            next.remove(PdfName.Prev);
        }
    }

    /**
     * Clear list of children.
     */
    void clear() {
        children.clear();
    }

    /**
     * Sets {@link PdfDestination}.
     *
     * @param destination instance of {@link PdfDestination}.
     */
    void setDestination(PdfDestination destination) {
        this.destination = destination;
    }

    /**
     * Gets the Outline root in {@link PdfOutline#pdfDoc}'s catalog entry
     *
     * @return The {@link PdfDictionary} of the document's Outline root, or {@code null} if it can't be found.
     */
    private PdfDictionary getOutlineRoot() {
        if (!pdfDoc.hasOutlines()) {
            return null;
        }
        return pdfDoc.getCatalog().getPdfObject().getAsDictionary(PdfName.Outlines);
    }


    /**
     * Determines if the current {@link PdfOutline} object is the Outline Root.
     *
     * @return {@code false} if this is not the outline root or the root can not be found, {@code true} otherwise.
     */
    private boolean isOutlineRoot() {
        PdfDictionary outlineRoot = getOutlineRoot();
        return outlineRoot == content;
    }
}
