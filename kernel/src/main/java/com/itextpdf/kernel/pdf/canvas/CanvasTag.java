package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;

/**
 * This class represents a single tag on a single piece of marked content.
 *
 * <p>
 * In Tagged PDF, a tag is the basic structure unit for marking content. The tag
 * structure and hierarchy is largely comparable to HTML. As in HTML, every tag
 * type has a name, defined here in the <code>role</code> attribute. The tagging
 * mechanism in Tagged PDF is extensible, so PDF creators can choose to create
 * custom tags.</p>
 */
public class CanvasTag {

    /**
     * The type of the tag.
     */
    protected PdfName role;

    /**
     * The marked content id of the tag.
     */
    protected Integer mcid;

    /**
     * The properties of the tag.
     */
    protected PdfDictionary properties;

    /**
     * Creates a tag that is referenced to the document's tag structure (i.e.
     * logical structure).
     *
     * @param role the type of tag
     */
    public CanvasTag(PdfName role) {
        this.role = role;
    }

    /**
     * Creates a tag that is referenced to the document's tag structure (i.e.
     * logical structure).
     *
     * @param role the type of tag
     * @param mcid marked content id which serves as a reference to the document's logical structure
     */
    public CanvasTag(PdfName role, Integer mcid) {
        this.role = role;
        this.mcid = mcid;

        addProperty(PdfName.MCID, new PdfNumber(mcid));
    }

    /**
     * Creates a tag that is referenced to the document's tag structure (i.e.
     * logical structure).
     *
     * @param mcr the {@link PdfMcr Marked Content Reference} wrapper object
     */
    public CanvasTag(PdfMcr mcr) {
        this(mcr.getRole(), mcr.getMcid());
    }

    /**
     * Get the role of the tag.
     *
     * @return the role of the tag as a PdfName
     */
    public PdfName getRole() {
        return role;
    }

    /**
     * Get the marked content id of the tag.
     *
     * @return marked content id
     */
    public Integer getMcid() {
        return mcid;
    }

    /**
     * Adds a dictionary of properties to the {@link CanvasTag tag}'s properties.
     *
     * @param properties a dictionary
     * @return current {@link CanvasTag}
     */
    public CanvasTag addProperties(PdfDictionary properties) {
        if (properties == null) {
            return this;
        }

        ensurePropertiesInit();
        this.properties.putAll(properties);
        return this;
    }

    /**
     * Adds a single property to the {@link CanvasTag tag}'s properties.
     *
     * @param name a key
     * @param value the value for the key
     * @return current {@link CanvasTag}
     */
    public CanvasTag addProperty(PdfName name, PdfObject value) {
        ensurePropertiesInit();

        properties.put(name, value);
        return this;
    }

    /**
     * Removes a single property from the {@link CanvasTag tag}'s properties.
     *
     * @param name the key of the key-value pair to be removed
     * @return current {@link CanvasTag}
     */
    public CanvasTag removeProperty(PdfName name) {
        if (properties != null) {
            properties.remove(name);
        }
        return this;
    }

    /**
     * Gets a property from the {@link CanvasTag tag}'s properties dictionary.
     *
     * @param name the key of the key-value pair to be retrieved
     * @return the value corresponding to the key
     */
    public PdfObject getProperty(PdfName name) {
        if (properties == null) {
            return null;
        }
        return properties.get(name);
    }

    /**
     * Get the properties of the tag.
     *
     * @return properties of the tag
     */
    public PdfDictionary getProperties() {
        return properties;
    }

    private void ensurePropertiesInit() {
        if (properties == null) {
            properties = new PdfDictionary();
        }
    }
}
