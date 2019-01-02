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

    private static final long serialVersionUID = -597407628148657784L;

	/**
     * Creates a new, empty membership layer.
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
     * Gets the visibility policy for content belonging to this
     * optional content membership dictionary.
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
     * Gets the visibility expression for content belonging to this
     * optional content membership dictionary.
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

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }
}
