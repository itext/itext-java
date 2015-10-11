package com.itextpdf.core.pdf.layer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Content typically belongs to a single optional content group,
 * and is visible when the group is <B>ON</B> and invisible when it is <B>OFF</B>. To express more
 * complex visibility policies, content should not declare itself to belong to an optional
 * content group directly, but rather to an optional content membership dictionary
 * represented by this class.
 */
public class PdfLayerMembership extends PdfObjectWrapper<PdfDictionary> implements PdfOCG {

    /**
     * Creates a new, empty membership layer.
     * @throws PdfException
     */
    public PdfLayerMembership(PdfDocument doc) {
        super(new PdfDictionary());
        makeIndirect(doc);
        getPdfObject().put(PdfName.Type, PdfName.OCMD);
    }

    /**
     * Creates a new PdfLayerMembership instance by its PdfDictionary.
     * @throws PdfException
     */
    public PdfLayerMembership(PdfDictionary membershipDictionary, PdfDocument doc) {
        super(membershipDictionary);
        makeIndirect(doc);
        if (!PdfName.OCMD.equals(membershipDictionary.getAsName(PdfName.Type)))
            throw new IllegalArgumentException("Invalid membershipDictionary.");
    }

    /**
     * Gets the collection of the layers this layer membership operates with.
     * @throws PdfException
     */
    public Collection<PdfLayer> getLayers() {
        final PdfObject layers = getPdfObject().get(PdfName.OCGs);
        if (layers instanceof PdfDictionary)
            return new ArrayList<PdfLayer>() {{add(new PdfLayer((PdfDictionary) layers, getDocument()));}};
        else if (layers instanceof PdfArray) {
            List<PdfLayer> layerList = new ArrayList<PdfLayer>();
            for (int ind = 0; ind < ((PdfArray) layers).size(); ind++) {
                layerList.add(new PdfLayer(((PdfArray) layers).getAsDictionary(ind), getDocument()));
            }
            return layerList;
        }
        return null;
    }

    /**
     * Adds a new layer to the current layer membership.
     * @param layer the layer to be added
     * @throws PdfException
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
     * @throws PdfException
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
     * @throws PdfException
     */
    public PdfVisibilityExpression getVisibilityExpression() {
        PdfArray ve = getPdfObject().getAsArray(PdfName.VE);
        return ve != null ? new PdfVisibilityExpression(ve) : null;
    }

    @Override
    public PdfIndirectReference getIndirectReference() {
        getPdfObject().makeIndirect(getDocument());
        return getPdfObject().getIndirectReference();
    }
}
