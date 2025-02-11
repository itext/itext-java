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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Content typically belongs to a single optional content group,
 * and is visible when the group is <B>ON</B> and invisible when it is <B>OFF</B>. To express more
 * complex visibility policies, content should not declare itself to belong to an optional
 * content group directly, but rather to an optional content membership dictionary
 * represented by this class.
 *
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfLayerMembership extends PdfObjectWrapper<PdfDictionary> implements IPdfOCG {


	/**
     * Creates a new, empty membership layer.
     *
     * @param doc a {@link PdfDocument} where a new empty membership layer creates
     */
    public PdfLayerMembership(PdfDocument doc) {
        super(new PdfDictionary());
        makeIndirect(doc);
        getPdfObject().put(PdfName.Type, PdfName.OCMD);
    }

    /**
     * Creates a new PdfLayerMembership instance by its PdfDictionary, which must be an indirect object.
     *
     * @param membershipDictionary the membership dictionary, must have an indirect reference.
     */
    public PdfLayerMembership(PdfDictionary membershipDictionary) {
        super(membershipDictionary);
        ensureObjectIsAddedToDocument(membershipDictionary);
        if (!PdfName.OCMD.equals(membershipDictionary.getAsName(PdfName.Type)))
            throw new IllegalArgumentException("Invalid membershipDictionary.");
    }

    /**
     * Gets the collection of the layers this layer membership operates with.
     *
     * @return list of {@link PdfLayer layers} this layer membership operates with
     */
    public Collection<PdfLayer> getLayers() {
        final PdfObject layers = getPdfObject().get(PdfName.OCGs);
        if (layers instanceof PdfDictionary) {
            List<PdfLayer> list = new ArrayList<>();
            list.add(new PdfLayer((PdfDictionary) ((PdfDictionary) layers).makeIndirect(getDocument())));
            return list;
        }
        else if (layers instanceof PdfArray) {
            List<PdfLayer> layerList = new ArrayList<>();
            for (int ind = 0; ind < ((PdfArray) layers).size(); ind++) {
                layerList.add(new PdfLayer(((PdfArray) (((PdfArray) layers).makeIndirect(getDocument()))).getAsDictionary(ind)));
            }
            return layerList;
        }
        return null;
    }

    /**
     * Adds a new layer to the current layer membership.
     * @param layer the layer to be added
     */
    public void addLayer(PdfLayer layer) {
        PdfArray layers = getPdfObject().getAsArray(PdfName.OCGs);
        if (layers == null) {
            layers = new PdfArray();
            getPdfObject().put(PdfName.OCGs, layers);
        }
        layers.add(layer.getPdfObject());
        layers.setModified();
    }

    /**
     * Sets the visibility policy for content belonging to this
     * membership dictionary. Possible values are AllOn, AnyOn, AnyOff and AllOff.
     * AllOn - Visible only if all of the entries are <B>ON</B>.
     * AnyOn - Visible if any of the entries are <B>ON</B>.
     * AnyOff - Visible if any of the entries are <B>OFF</B>.
     * AllOff - Visible only if all of the entries are <B>OFF</B>.
     * The default value is AnyOn.
     * @param visibilityPolicy the visibility policy
     */
    public void setVisibilityPolicy(PdfName visibilityPolicy) {
        if (visibilityPolicy == null || !PdfName.AllOn.equals(visibilityPolicy) && !PdfName.AnyOn.equals(visibilityPolicy)
                && !PdfName.AnyOff.equals(visibilityPolicy) && !PdfName.AllOff.equals(visibilityPolicy))
            throw new IllegalArgumentException("Argument: visibilityPolicy");
        getPdfObject().put(PdfName.P, visibilityPolicy);
        getPdfObject().setModified();
    }

    /**
     * Gets the visibility policy for content belonging to this optional content membership dictionary.
     * @return the visibility policy for content belonging to this membership dictionary
     */
    public PdfName getVisibilityPolicy() {
        PdfName visibilityPolicy = getPdfObject().getAsName(PdfName.P);
        if (visibilityPolicy == null || !visibilityPolicy.equals(PdfName.AllOn) && !visibilityPolicy.equals(PdfName.AllOff)
                && !visibilityPolicy.equals(PdfName.AnyOn) && !visibilityPolicy.equals(PdfName.AnyOff))
            return PdfName.AnyOn;
        return visibilityPolicy;
    }

    /**
     * Sets the visibility expression for content belonging to this
     * membership dictionary.
     * @param visibilityExpression A (nested) array of which the first value is /And, /Or, or /Not
     * followed by a series of indirect references to OCGs or other visibility
     * expressions.
     */
    public void setVisibilityExpression(PdfVisibilityExpression visibilityExpression) {
        getPdfObject().put(PdfName.VE, visibilityExpression.getPdfObject());
        getPdfObject().setModified();
    }

    /**
     * Gets the visibility expression for content belonging to this optional content membership dictionary.
     * @return the visibility expression for content belonging to this membership dictionary, if not set return null
     */
    public PdfVisibilityExpression getVisibilityExpression() {
        PdfArray ve = getPdfObject().getAsArray(PdfName.VE);
        return ve != null ? new PdfVisibilityExpression(ve) : null;
    }

    @Override
    public PdfIndirectReference getIndirectReference() {
        return getPdfObject().getIndirectReference();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Gets the {@link PdfDocument} that owns that layer membership.
     *
     * @return the {@link PdfDocument} that owns that layer membership
     */
    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }
}
