package com.itextpdf.core.pdf.layer;

import com.itextpdf.core.PdfException;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An optional content group is a dictionary representing a collection of graphics
 * that can be made visible or invisible dynamically by users of viewer applications.
 * In iText they are referenced as layers.
 */
public class PdfLayer extends PdfObjectWrapper<PdfDictionary> implements PdfOCG {

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
     * Creates a new layer by existing dictionary and document.
     * @param layerDictionary the layer dictionary
     * @param document the PdfDocument which the layer belongs to
     * @throws PdfException
     */
    public PdfLayer(PdfDictionary layerDictionary,PdfDocument document) {
        super(layerDictionary);
        makeIndirect(document);
    }

    /**
     * Creates a new layer by its name and document.
     * @param name the layer name
     * @param document the PdfDocument which the layer belongs to
     * @throws PdfException
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
    public static void addOCGRadioGroup(PdfDocument document, final List<PdfLayer> group) {
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
        getPdfObject().put(PdfName.Name, new PdfString(name, PdfEncodings.UnicodeBig));
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
     * @return true of the layer is currently locked, false otherwise.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Use this method to lock an optional content group.
     * The state of a locked group cannot be changed through the user interface
     * of a viewer application. Producers can use this entry to prevent the visibility
     * of content that depends on these groups from being changed by users.
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
     * The default value is PdfName.View, so it will be the only element of the
     * resultant colletion if no intents are currently specified.
     * @return the collection of intents.
     * @throws PdfException
     */
    public Collection<PdfName> getIntents() {
        final PdfObject intent = getPdfObject().get(PdfName.Intent);
        if (intent instanceof PdfName)
            return Arrays.asList((PdfName) intent);
        else if (intent instanceof PdfArray) {
            return (Collection<PdfName>) intent;
        }
        return Arrays.asList(PdfName.View);
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
        } else if (intents.size() > 1) {
            getPdfObject().put(PdfName.Intent, new PdfArray(intents));
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
        dic.put(PdfName.Creator, new PdfString(creator, PdfEncodings.UnicodeBig));
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
        dic.put(PdfName.Lang, new PdfString(lang, PdfEncodings.UnicodeBig));
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
        if (type == null || !type.equals("Ind") && !type.equals("Ttl") && !type.equals("Org"))
            throw new IllegalArgumentException("Illegal type argument");
        if (names == null || names.length == 0)
            throw new IllegalArgumentException("Illegal names argument");
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Type, new PdfName(type));
        if (names.length == 1) {
            dic.put(PdfName.Name, new PdfString(names[0], PdfEncodings.UnicodeBig));
        } else {
            PdfArray namesArray = new PdfArray();
            for (String name : names) {
                namesArray.add(new PdfString(name, PdfEncodings.UnicodeBig));
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
     * @since 5.0.2
     */
    public void setPageElement(String pe) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.Subtype, new PdfName(pe));
        usage.put(PdfName.PageElement, dic);
        usage.setModified();
    }

    /**
     * Gets the indirect reference to the current layer object,
     * making it indirect first if necessary.
     * @return the indirect reference to the object representing the layer
     * @throws PdfException
     */
    public PdfIndirectReference getIndirectReference() {
        getPdfObject().makeIndirect(getDocument());
        return getPdfObject().getIndirectReference();
    }

    /**
     * Gets the title of the layer if it is a title layer, or null if it is a usual layer.
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

    /**
     * Creates a title layer without registering it in PdfOCProperties.
     * @param title the title of the layer
     * @param document the document this title layer belongs to
     * @return the created layer
     * @throws PdfException
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
     * @throws PdfException
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
        try {
            return getDocument().getCatalog().getOCProperties(true);
        } catch (Exception ignored) {}
        return null;
    }

}
