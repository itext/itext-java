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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents /OCProperties entry if pdf catalog and manages
 * the layers of the pdf document.
 *
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfOCProperties extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 1137977454824741350L;
    private List<PdfLayer> layers = new ArrayList<>();

    /**
     * Gets the order of the layers in which they will be displayed in the layer view panel,
     * including nesting.
     */
    private static void getOCGOrder(PdfArray order, PdfLayer layer) {
        if (!layer.isOnPanel())
            return;
        if (layer.getTitle() == null)
            order.add(layer.getPdfObject().getIndirectReference());
        List<PdfLayer> children = layer.getChildren();
        if (children == null)
            return;
        PdfArray kids = new PdfArray();
        if (layer.getTitle() != null)
            kids.add(new PdfString(layer.getTitle(), PdfEncodings.UNICODE_BIG));
        for (PdfLayer child : children) {
            getOCGOrder(kids, child);
        }
        if (kids.size() > 0)
            order.add(kids);
    }

    /**
     * Creates a new PdfOCProperties instance.
     *
     * @param document the document the optional content belongs to
     */
    public PdfOCProperties(PdfDocument document) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(document));
    }

    /**
     * Creates a new PdfOCProperties instance by the dictionary it represents,
     * the dictionary must be an indirect object.
     *
     * @param ocPropertiesDict the dictionary of optional content properties, must have an indirect reference.
     */
    public PdfOCProperties(PdfDictionary ocPropertiesDict) {
        super(ocPropertiesDict);
        ensureObjectIsAddedToDocument(ocPropertiesDict);
        readLayersFromDictionary();
    }

    /**
     * Use this method to set a collection of optional content groups
     * whose states are intended to follow a "radio button" paradigm.
     * That is, the state of at most one optional content group
     * in the array should be ON at a time: if one group is turned
     * ON, all others must be turned OFF.
     *
     * @param group the radio group
     */
    public void addOCGRadioGroup(List<PdfLayer> group) {
        PdfArray ar = new PdfArray();
        for (PdfLayer layer : group) {
            if (layer.getTitle() == null)
                ar.add(layer.getPdfObject().getIndirectReference());
        }
        if (ar.size() != 0) {
            PdfDictionary d = getPdfObject().getAsDictionary(PdfName.D);
            if (d == null) {
                d = new PdfDictionary();
                getPdfObject().put(PdfName.D, d);
            }
            PdfArray radioButtonGroups = d.getAsArray(PdfName.RBGroups);
            if (radioButtonGroups == null) {
                radioButtonGroups = new PdfArray();
                d.put(PdfName.RBGroups, radioButtonGroups);
                d.setModified();
            } else {
                radioButtonGroups.setModified();
            }
            radioButtonGroups.add(ar);
        }
    }

    /**
     * Fills the underlying PdfDictionary object with the current layers and their settings.
     * Note that it completely regenerates the dictionary, so your direct changes to the dictionary
     * will not take any affect.
     *
     * @return the resultant dictionary
     */
    public PdfObject fillDictionary() {
        PdfArray gr = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null)
                gr.add(layer.getIndirectReference());
        }
        getPdfObject().put(PdfName.OCGs, gr);

        // Save radio groups.
        PdfArray rbGroups = null;
        PdfDictionary d = getPdfObject().getAsDictionary(PdfName.D);
        if (d != null)
            rbGroups = d.getAsArray(PdfName.RBGroups);

        d = new PdfDictionary();
        if (rbGroups != null)
            d.put(PdfName.RBGroups, rbGroups);
        d.put(PdfName.Name, new PdfString(createUniqueName(), PdfEncodings.UNICODE_BIG));

        getPdfObject().put(PdfName.D, d);


        List<PdfLayer> docOrder = new ArrayList<>(layers);
        for (int i = 0; i < docOrder.size(); i++) {
            PdfLayer layer = docOrder.get(i);
            if (layer.getParent() != null) {
                docOrder.remove(layer);
                i--;
            }
        }

        PdfArray order = new PdfArray();
        for (Object element : docOrder) {
            PdfLayer layer = (PdfLayer) element;
            getOCGOrder(order, layer);
        }
        d.put(PdfName.Order, order);

        PdfArray off = new PdfArray();
        for (Object element : layers) {
            PdfLayer layer = (PdfLayer) element;
            if (layer.getTitle() == null && !layer.isOn())
                off.add(layer.getIndirectReference());
        }
        if (off.size() > 0)
            d.put(PdfName.OFF, off);
        else
            d.remove(PdfName.OFF);

        PdfArray locked = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null && layer.isLocked())
                locked.add(layer.getIndirectReference());
        }
        if (locked.size() > 0)
            d.put(PdfName.Locked, locked);
        else
            d.remove(PdfName.Locked);

        d.remove(PdfName.AS);
        addASEvent(PdfName.View, PdfName.Zoom);
        addASEvent(PdfName.View, PdfName.View);
        addASEvent(PdfName.Print, PdfName.Print);
        addASEvent(PdfName.Export, PdfName.Export);

        return getPdfObject();
    }

    private String createUniqueName() {
        int uniqueID = 0;
        Set<String> usedNames = new HashSet<>();
        PdfArray configs = getPdfObject().getAsArray(PdfName.Configs);
        if (null != configs) {
            for (int i = 0; i < configs.size(); i++) {
                PdfDictionary alternateDictionary = configs.getAsDictionary(i);
                if (null != alternateDictionary && alternateDictionary.containsKey(PdfName.Name)) {
                    usedNames.add(alternateDictionary.getAsString(PdfName.Name).toUnicodeString());
                }
            }
        }
        while (usedNames.contains("OCConfigName" + uniqueID)) {
            uniqueID++;
        }
        return "OCConfigName" + uniqueID;
    }

    @Override
    public void flush() {
        fillDictionary();
        super.flush();
    }

    /**
     * Gets the list of all the layers currently registered in the OCProperties.
     * Note that this is just a new list and modifications to it will not affect anything.
     */
    public List<PdfLayer> getLayers() {
        return new ArrayList<>(layers);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * This method registers a new layer in the OCProperties.
     *
     * @param layer the new layer
     */
    protected void registerLayer(PdfLayer layer) {
        if (layer == null)
            throw new IllegalArgumentException("layer argument is null");
        layers.add(layer);
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    /**
     * Populates the /AS entry in the /D dictionary.
     */
    private void addASEvent(PdfName event, PdfName category) {
        PdfArray arr = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null) {
                PdfDictionary usage = layer.getPdfObject().getAsDictionary(PdfName.Usage);
                if (usage != null && usage.get(category) != null)
                    arr.add(layer.getPdfObject().getIndirectReference());
            }
        }
        if (arr.size() == 0)
            return;
        PdfDictionary d = getPdfObject().getAsDictionary(PdfName.D);
        PdfArray arras = d.getAsArray(PdfName.AS);
        if (arras == null) {
            arras = new PdfArray();
            d.put(PdfName.AS, arras);
        }
        PdfDictionary as = new PdfDictionary();
        as.put(PdfName.Event, event);
        PdfArray categoryArray = new PdfArray();
        categoryArray.add(category);
        as.put(PdfName.Category, categoryArray);
        as.put(PdfName.OCGs, arr);
        arras.add(as);
    }

    /**
     * Reads the layers from the document to be able to modify them in the future.
     */
    private void readLayersFromDictionary() {
        PdfArray ocgs = getPdfObject().getAsArray(PdfName.OCGs);
        if (ocgs == null || ocgs.isEmpty())
            return;

        Map<PdfIndirectReference, PdfLayer> layerMap = new TreeMap<PdfIndirectReference, PdfLayer>();
        for (int ind = 0; ind < ocgs.size(); ind++) {
            PdfLayer currentLayer = new PdfLayer((PdfDictionary) ocgs.getAsDictionary(ind).makeIndirect(getDocument()));
            // We will set onPanel to true later for the objects present in /D->/Order entry.
            currentLayer.onPanel = false;
            layerMap.put(currentLayer.getIndirectReference(), currentLayer);
        }

        PdfDictionary d = getPdfObject().getAsDictionary(PdfName.D);
        if (d != null && !d.isEmpty()) {
            PdfArray off = d.getAsArray(PdfName.OFF);
            if (off != null) {
                for (int i = 0; i < off.size(); i++) {
                    PdfObject offLayer = off.get(i, false);
                    layerMap.get((PdfIndirectReference) offLayer).on = false;
                }
            }

            PdfArray locked = d.getAsArray(PdfName.Locked);
            if (locked != null) {
                for (int i = 0; i < locked.size(); i++) {
                    PdfObject lockedLayer = locked.get(i, false);
                    layerMap.get((PdfIndirectReference) lockedLayer).locked = true;
                }
            }

            PdfArray orderArray = d.getAsArray(PdfName.Order);
            if (orderArray != null && !orderArray.isEmpty())
                readOrderFromDictionary(null, orderArray, layerMap);
        }

        // Add the layers which should not be displayed on the panel to the order list
        for (PdfLayer layer : layerMap.values()) {
            if (!layer.isOnPanel())
                layers.add(layer);
        }
    }

    /**
     * Reads the /Order in the /D entry and initialized the parent-child hierarchy.
     */
    private void readOrderFromDictionary(PdfLayer parent, PdfArray orderArray, Map<PdfIndirectReference, PdfLayer> layerMap) {
        for (int i = 0; i < orderArray.size(); i++) {
            PdfObject item = orderArray.get(i);
            if (item.getType() == PdfObject.DICTIONARY) {
                PdfLayer layer = layerMap.get(item.getIndirectReference());
                if (layer != null) {
                    layers.add(layer);
                    layer.onPanel = true;
                    if (parent != null)
                        parent.addChild(layer);
                    if (i + 1 < orderArray.size() && orderArray.get(i + 1).getType() == PdfObject.ARRAY) {
                        readOrderFromDictionary(layer, orderArray.getAsArray(i + 1), layerMap);
                        i++;
                    }
                }
            } else if (item.getType() == PdfObject.ARRAY) {
                PdfArray subArray = (PdfArray) item;
                if (subArray.isEmpty()) continue;
                PdfObject firstObj = subArray.get(0);
                if (firstObj.getType() == PdfObject.STRING) {
                    PdfLayer titleLayer = PdfLayer.createTitleSilent(((PdfString) firstObj).toUnicodeString(), getDocument());
                    titleLayer.onPanel = true;
                    layers.add(titleLayer);
                    if (parent != null)
                        parent.addChild(titleLayer);
                    readOrderFromDictionary(titleLayer, new PdfArray(subArray.subList(1, subArray.size())), layerMap);
                } else {
                    readOrderFromDictionary(parent, subArray, layerMap);
                }
            }
        }
    }
}
