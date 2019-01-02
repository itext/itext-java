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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
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
    public CanvasTag(PdfName role, int mcid) {
        this.role = role;
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
     * @throws IllegalStateException if there is no MCID
     */
    public int getMcid() {
        int mcid = -1;
        if (properties != null) {
           mcid = (int) properties.getAsInt(PdfName.MCID);
        }
        if (mcid == -1) {
            throw new IllegalStateException("CanvasTag has no MCID");
        }
        return mcid;
    }

    /**
     * Determine if an MCID is available
     * @return true if the MCID is available, false otherwise
     */
    public boolean hasMcid(){
        return properties != null && properties.containsKey(PdfName.MCID);
    }

    /**
     * Sets a dictionary of properties to the {@link CanvasTag tag}'s properties. All existing properties (if any) will be lost.
     *
     * @param properties a dictionary
     * @return current {@link CanvasTag}
     */
    public CanvasTag setProperties(PdfDictionary properties) {
        this.properties = properties;
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

    public String getActualText() {
        return getPropertyAsString(PdfName.ActualText);
    }

    public String getExpansionText() {
        return getPropertyAsString(PdfName.E);
    }

    private String getPropertyAsString(PdfName name) {
        PdfString text = properties.getAsString(name);
        String result = null;
        if (text != null) {
            result = text.toUnicodeString();
        }
        return result;
    }

    private void ensurePropertiesInit() {
        if (properties == null) {
            properties = new PdfDictionary();
        }
    }
}
