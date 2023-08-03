/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An optional content group is a dictionary representing a collection of graphics
 * that can be made visible or invisible dynamically by users of viewer applications.
 * In iText they are referenced as layers.
 *
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfLayer extends PdfObjectWrapper<PdfDictionary> implements IPdfOCG {

	/**
     * Used for titling group of objects but not actually grouping them.
     */
    protected String title;
    protected boolean on = true;
    protected boolean onPanel = true;
    protected boolean locked = false;
    protected PdfLayer parent;
    protected List<PdfLayer> children;

    /**
     * Creates a new layer by existing dictionary, which must be an indirect object.
     *
     * @param layerDictionary the layer dictionary, must have an indirect reference.
     */
    public PdfLayer(PdfDictionary layerDictionary) {
        super(layerDictionary);
        setForbidRelease();
        ensureObjectIsAddedToDocument(layerDictionary);
    }

    /**
     * Creates a new layer by its name and document.
     * @param name the layer name
     * @param document the PdfDocument which the layer belongs to
     */
    public PdfLayer(String name, PdfDocument document) {
        this(document);
        setName(name);
        document.getCatalog().getOCProperties(true).registerLayer(this);
    }

    private PdfLayer(PdfDocument document) {
        super(new PdfDictionary());
        makeIndirect(document);
        getPdfObject().put(PdfName.Type, PdfName.OCG);
    }

    /**
     * Creates a title layer. A title layer is not really a layer but a collection of layers
     * under the same title heading.
     * @param title the title text
     * @param document the <CODE>PdfDocument</CODE>
     * @return the title layer
     */
    public static PdfLayer createTitle(String title, PdfDocument document) {
        PdfLayer layer = createTitleSilent(title, document);
        document.getCatalog().getOCProperties(true).registerLayer(layer);
        return layer;
    }

    /**
     * Use this method to set a collection of optional content groups
     * whose states are intended to follow a "radio button" paradigm.
     * That is, the state of at most one optional content group
     * in the array should be ON at a time: if one group is turned
     * ON, all others must be turned OFF.
     * @param document the <CODE>PdfDocument</CODE>
     * @param group the radio group
     */
    public static void addOCGRadioGroup(PdfDocument document, List<PdfLayer> group) {
        document.getCatalog().getOCProperties(true).addOCGRadioGroup(group);
    }

    /**
     * Adds a child layer. Nested layers can only have one parent.
     * @param childLayer the child layer
     */
    public void addChild(PdfLayer childLayer) {
        if (childLayer.parent != null)
            throw new IllegalArgumentException("Illegal argument: childLayer");
        childLayer.parent = this;
        if (children == null)
            children = new ArrayList<>();
        children.add(childLayer);
    }

    /**
     * Gets the parent of this layer, be it a title layer, or a usual one.
     * @return the parent of the layer, or null if it has no parent
     */
    public PdfLayer getParent() {
        return parent;
    }

    /**
     * Sets the name of the layer to be displayed in the Layers panel.
     * @param name the name of the layer.
     */
    public void setName(String name) {
        getPdfObject().put(PdfName.Name, new PdfString(name, PdfEncodings.UNICODE_BIG));
        getPdfObject().setModified();
    }

    /**
     * Gets the initial visibility of the layer when the document is opened.
     * @return the initial visibility of the layer
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Sets the initial visibility of the layer when the document is opened.
     * @param on the initial visibility of the layer
     */
    public void setOn(boolean on) {
        if (this.on != on)
            fetchOCProperties().setModified();
        this.on = on;
    }

    /**
     * Gets whether the layer is currently locked or not. If the layer is locked,
     * it will not be possible to change its state (on/off) in a viewer.
     * @return true if the layer is currently locked, false otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Use this method to lock an optional content group.
     * The state of a locked group cannot be changed through the user interface
     * of a viewer application. Producers can use this entry to prevent the visibility
     * of content that depends on these groups from being changed by users.
     *
     * @param locked sets whether the layer is currently locked or not
     */
    public void setLocked(boolean locked) {
        if (this.isLocked() != locked)
            fetchOCProperties().setModified();
        this.locked = locked;
    }

    /**
     * Gets the layer visibility in Acrobat's layer panel
     * @return the layer visibility in Acrobat's layer panel
     */
    public boolean isOnPanel() {
        return onPanel;
    }

    /**
     * Sets the visibility of the layer in Acrobat's layer panel. If <CODE>false</CODE>
     * the layer cannot be directly manipulated by the user. Note that any children layers will
     * also be absent from the panel.
     * @param onPanel the visibility of the layer in Acrobat's layer panel
     */
    public void setOnPanel(boolean onPanel) {
        if (this.on != onPanel)
            fetchOCProperties().setModified();
        this.onPanel = onPanel;
    }

    /**
     * Gets a collection of current intents specified for this layer.
     * The default value is {@link PdfName#View}, so it will be the only element of the
     * resultant collection if no intents are currently specified.
     * @return the collection of intents.
     */
    public Collection<PdfName> getIntents() {
        final PdfObject intent = getPdfObject().get(PdfName.Intent);
        if (intent instanceof PdfName)
            return Collections.singletonList((PdfName) intent);
        else if (intent instanceof PdfArray) {
            PdfArray intentArr = (PdfArray) intent;
            Collection<PdfName> intentsCollection = new ArrayList<>(intentArr.size());
            for (PdfObject i : intentArr) {
                if (i instanceof PdfName) {
                    intentsCollection.add((PdfName) i);
                }
            }
            return intentsCollection;
        }
        return Collections.singletonList(PdfName.View);
    }

    /**
     * Sets the intents of the layer.
     * @param intents the list of intents.
     */
    public void setIntents(List<PdfName> intents) {
        if (intents == null || intents.size() == 0) {
            getPdfObject().remove(PdfName.Intent);
        } else if (intents.size() == 1) {
            getPdfObject().put(PdfName.Intent, intents.get(0));
        } else {
            // intents.size() > 1

            PdfArray array = new PdfArray();
            for (PdfName intent : intents) {
                array.add(intent);
            }
            getPdfObject().put(PdfName.Intent, array);
        }
        getPdfObject().setModified();
    }

    /**
     * Used by the creating application to store application-specific
     * data associated with this optional content group.
     * @param creator a text string specifying the application that created the group
     * @param subtype a string defining the type of content controlled by the group. Suggested
     * values include but are not limited to <B>Artwork</B>, for graphic-design or publishing
     * applications, and <B>Technical</B>, for technical designs such as building plans or
     * schematics
     */
    public void setCreatorInfo(String creator, String subtype) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Creator, new PdfString(creator, PdfEncodings.UNICODE_BIG));
        dic.put(PdfName.Subtype, new PdfName(subtype));
        usage.put(PdfName.CreatorInfo, dic);
        usage.setModified();
    }

    /**
     * Specifies the language of the content controlled by this
     * optional content group
     * @param lang a language string which specifies a language and possibly a locale
     * (for example, <B>es-MX</B> represents Mexican Spanish)
     * @param preferred used by viewer applications when there is a partial match but no exact
     * match between the system language and the language strings in all usage dictionaries
     */
    public void setLanguage(String lang, boolean preferred) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Lang, new PdfString(lang, PdfEncodings.UNICODE_BIG));
        if (preferred)
            dic.put(PdfName.Preferred, PdfName.ON);
        usage.put(PdfName.Language, dic);
        usage.setModified();
    }

    /**
     * Specifies the recommended state for content in this
     * group when the document (or part of it) is saved by a viewer application to a format
     * that does not support optional content (for example, an earlier version of
     * PDF or a raster image format).
     * @param export the export state
     */
    public void setExport(boolean export) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.ExportState, export ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.Export, dic);
        usage.setModified();
    }

    /**
     * Specifies a range of magnifications at which the content
     * in this optional content group is best viewed.
     * @param min the minimum recommended magnification factors at which the group
     * should be ON. A negative value will set the default to 0
     * @param max the maximum recommended magnification factor at which the group
     * should be ON. A negative value will set the largest possible magnification supported by the
     * viewer application
     */
    public void setZoom(float min, float max) {
        if (min <= 0 && max < 0)
            return;
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        if (min > 0)
            dic.put(PdfName.min, new PdfNumber(min));
        if (max >= 0)
            dic.put(PdfName.max, new PdfNumber(max));
        usage.put(PdfName.Zoom, dic);
        usage.setModified();
    }

    /**
     * Specifies that the content in this group is intended for
     * use in printing
     * @param subtype a name specifying the kind of content controlled by the group;
     * for example, <B>Trapping</B>, <B>PrintersMarks</B> and <B>Watermark</B>
     * @param printState indicates that the group should be
     * set to that state when the document is printed from a viewer application
     */
    public void setPrint(String subtype, boolean printState) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Subtype, new PdfName(subtype));
        dic.put(PdfName.PrintState, printState ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.Print, dic);
        usage.setModified();
    }

    /**
     * Indicates that the group should be set to that state when the
     * document is opened in a viewer application.
     * @param view the view state
     */
    public void setView(boolean view) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.ViewState, view ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.View, dic);
        usage.setModified();
    }

    /**
     * Specifies one or more users for whom this optional content group
     * is primarily intended.
     * @param type a name that can be Ind (individual), Ttl (title), or Org (organization).
     * @param names one or more text strings representing
     * the name(s) of the individual, position or organization
     */
    public void setUser(String type, String... names) {
        if (type == null || !"Ind".equals(type) && !"Ttl".equals(type) && !"Org".equals(type))
            throw new IllegalArgumentException("Illegal type argument");
        if (names == null || names.length == 0)
            throw new IllegalArgumentException("Illegal names argument");
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Type, new PdfName(type));
        if (names.length == 1) {
            dic.put(PdfName.Name, new PdfString(names[0], PdfEncodings.UNICODE_BIG));
        } else {
            PdfArray namesArray = new PdfArray();
            for (String name : names) {
                namesArray.add(new PdfString(name, PdfEncodings.UNICODE_BIG));
            }
            dic.put(PdfName.Name, namesArray);
        }
        usage.put(PdfName.User, dic);
        usage.setModified();
    }

    /**
     * Indicates that the group contains a pagination artifact.
     * @param pe one of the following names: "HF" (Header Footer),
     * "FG" (Foreground), "BG" (Background), or "L" (Logo).
     */
    public void setPageElement(String pe) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Subtype, new PdfName(pe));
        usage.put(PdfName.PageElement, dic);
        usage.setModified();
    }

    /**
     * Gets the indirect reference to the current layer object.
     * @return the indirect reference to the object representing the layer
     */
    public PdfIndirectReference getIndirectReference() {
        return getPdfObject().getIndirectReference();
    }

    /**
     * Gets the title of the layer if it is a title layer, or null if it is a usual layer.
     *
     * @return the title of the layer if it is a title layer, or null if it is a usual layer
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of the current child layers of the layer.
     * BE CAREFUL! Do not try to add a child layer using the resultant child list,
     * use #addChild method instead.
     * @return the list of the current child layers, null if the layer has no children.
     */
    public List<PdfLayer> getChildren() {
        return children == null ? null : new ArrayList<>(children);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Gets the {@link PdfDocument} that owns that layer.
     *
     * @return the {@link PdfDocument} that owns that layer
     */
    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    /**
     * Creates a title layer without registering it in PdfOCProperties.
     * @param title the title of the layer
     * @param document the document this title layer belongs to
     * @return the created layer
     */
    protected static PdfLayer createTitleSilent(String title, PdfDocument document) {
        if (title == null)
            throw new IllegalArgumentException("Invalid title argument");
        PdfLayer layer = new PdfLayer(document);
        layer.title = title;
        return layer;
    }

    /**
     * Gets the /Usage dictionary, creating a new one if necessary.
     * @return the /Usage dictionary
     */
    protected PdfDictionary getUsage() {
        PdfDictionary usage = getPdfObject().getAsDictionary(PdfName.Usage);
        if (usage == null) {
            usage = new PdfDictionary();
            getPdfObject().put(PdfName.Usage, usage);
        }
        return usage;
    }

    private PdfOCProperties fetchOCProperties() {
        return getDocument().getCatalog().getOCProperties(true);
    }
}
