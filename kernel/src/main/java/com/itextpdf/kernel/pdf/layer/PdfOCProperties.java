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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents /OCProperties entry if pdf catalog and manages
 * the layers of the pdf document.
 * <p>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfOCProperties extends PdfObjectWrapper<PdfDictionary> {

    static final String OC_CONFIG_NAME_PATTERN = "OCConfigName";

    private List<PdfLayer> layers = new ArrayList<>();

    //TODO DEVSIX-8490 remove this field when implemented
    private Set<PdfIndirectReference> references;

    //TODO DEVSIX-8490 remove this field when implemented
    private boolean isDuplicateRemoved;

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
        return this.fillDictionary(true);
    }

    /**
     * Fills the underlying PdfDictionary object with the current layers and their settings.
     * Note that it completely regenerates the dictionary, so your direct changes to the dictionary
     * will not take any affect.
     *
     * @param removeNonDocumentOcgs the flag indicating whether it is necessary
     *                              to delete OCGs not from the current document
     * @return the resultant dictionary
     */
    public PdfObject fillDictionary(boolean removeNonDocumentOcgs) {
        PdfArray gr = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null)
                gr.add(layer.getIndirectReference());
        }
        getPdfObject().put(PdfName.OCGs, gr);

        PdfDictionary filledDDictionary = new PdfDictionary();

        // Save radio groups,Name,BaseState,Intent,ListMode
        PdfDictionary dDictionary = getPdfObject().getAsDictionary(PdfName.D);
        if (dDictionary != null) {
            PdfOCProperties.copyDDictionaryField(PdfName.RBGroups, dDictionary, filledDDictionary);
            PdfOCProperties.copyDDictionaryField(PdfName.Name, dDictionary, filledDDictionary);
            PdfOCProperties.copyDDictionaryField(PdfName.BaseState, dDictionary, filledDDictionary);
            PdfOCProperties.copyDDictionaryField(PdfName.Intent, dDictionary, filledDDictionary);
            PdfOCProperties.copyDDictionaryField(PdfName.ListMode, dDictionary, filledDDictionary);
        }

        if (filledDDictionary.get(PdfName.Name) == null) {
            filledDDictionary.put(PdfName.Name, new PdfString(createUniqueName(), PdfEncodings.UNICODE_BIG));
        }

        getPdfObject().put(PdfName.D, filledDDictionary);

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
        filledDDictionary.put(PdfName.Order, order);

        PdfArray off = new PdfArray();
        for (Object element : layers) {
            PdfLayer layer = (PdfLayer) element;
            if (layer.getTitle() == null && !layer.isOn())
                off.add(layer.getIndirectReference());
        }

        if (off.size() > 0) {
            filledDDictionary.put(PdfName.OFF, off);
        }

        PdfArray locked = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null && layer.isLocked())
                locked.add(layer.getIndirectReference());
        }
        if (locked.size() > 0) {
            filledDDictionary.put(PdfName.Locked, locked);
        }

        addASEvent(PdfName.View, PdfName.Zoom);
        addASEvent(PdfName.View, PdfName.View);
        addASEvent(PdfName.Print, PdfName.Print);
        addASEvent(PdfName.Export, PdfName.Export);

        if (removeNonDocumentOcgs) {
            this.removeNotRegisteredOcgs();
        }

        return getPdfObject();
    }

    /**
     * Checks if optional content group default configuration dictionary field value matches
     * the required value for this field, if one exists.
     *
     * @param field default configuration dictionary field.
     * @param value value of that field.
     *
     * @return boolean indicating if field meets requirement.
     */
    public static boolean checkDDictonaryFieldValue(PdfName field, PdfObject value) {
        // dictionary D BaseState should have the value ON
        if (PdfName.BaseState.equals(field) && !PdfName.ON.equals(value)) {
            return false;
            //for dictionary D Intent should have the value View
        } else if (PdfName.Intent.equals(field) && !PdfName.View.equals(value)) {
            return false;
        }
        return true;
    }

    @Override
    public void flush() {
        fillDictionary();
        super.flush();
    }

    /**
     * Gets the list of all the layers currently registered in the OCProperties.
     * Note that this is just a new list and modifications to it will not affect anything.
     *
     * @return list of all the {@link PdfLayer layers} currently registered in the OCProperties
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
     * @throws IllegalArgumentException if layer param is null
     */
    protected void registerLayer(PdfLayer layer) {
        if (layer == null)
            throw new IllegalArgumentException("layer argument is null");
        layers.add(layer);
    }

    /**
     * Gets the {@link PdfDocument} that owns that OCProperties.
     *
     * @return the {@link PdfDocument} that owns that OCProperties
     */
    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

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

    private static void copyDDictionaryField(PdfName fieldToAdd, PdfDictionary fromDictionary, PdfDictionary toDictionary) {
        PdfObject value = fromDictionary.get(fieldToAdd);
        if(value != null) {
            if (PdfOCProperties.checkDDictonaryFieldValue(fieldToAdd, value)) {
                toDictionary.put(fieldToAdd, value);
            } else {
                Logger logger = LoggerFactory.getLogger(PdfOCProperties.class);
                String warnText = MessageFormatUtil.format(KernelLogMessageConstant.INVALID_DDICTIONARY_FIELD_VALUE,
                        fieldToAdd, value);
                logger.warn(warnText);
            }
        }
    }

    private void removeNotRegisteredOcgs() {
        final PdfDictionary dDict = getPdfObject().getAsDictionary(PdfName.D);

        final PdfDictionary ocProperties = this.getDocument().getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties);
        final Set<PdfIndirectReference> ocgsFromDocument = new HashSet<>();
        if (ocProperties.getAsArray(PdfName.OCGs) != null) {
            final PdfArray ocgs = ocProperties.getAsArray(PdfName.OCGs);
            for (final PdfObject ocgObj : ocgs) {
                if (ocgObj.isDictionary()) {
                    ocgsFromDocument.add(ocgObj.getIndirectReference());
                }
            }
        }

        // Remove from RBGroups OCGs not presented in the output document (in OCProperties/OCGs)
        final PdfArray rbGroups = dDict.getAsArray(PdfName.RBGroups);
        if (rbGroups != null) {
            for (final PdfObject rbGroupObj : rbGroups) {
                final PdfArray rbGroup = (PdfArray) rbGroupObj;
                for (int i = rbGroup.size() - 1; i > -1; i--) {
                    if (!ocgsFromDocument.contains(rbGroup.get(i).getIndirectReference())) {
                        rbGroup.remove(i);
                    }
                }
            }
        }
    }

    /**
     * Populates the /AS entry in the /D dictionary.
     */
    private void addASEvent(PdfName event, PdfName category) {
        PdfArray arr = new PdfArray();
        for (PdfLayer layer : layers) {
            if (layer.getTitle() == null && !layer.getPdfObject().isFlushed()) {
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
                    if (offLayer.isIndirectReference()) {
                        layerMap.get((PdfIndirectReference) offLayer).on = false;
                    } else {
                        layerMap.get(offLayer.getIndirectReference()).on = false;
                    }
                }
            }

            PdfArray locked = d.getAsArray(PdfName.Locked);
            if (locked != null) {
                for (int i = 0; i < locked.size(); i++) {
                    PdfObject lockedLayer = locked.get(i, false);
                    if (lockedLayer.isIndirectReference()) {
                        layerMap.get((PdfIndirectReference) lockedLayer).locked = true;
                    } else {
                        layerMap.get(lockedLayer.getIndirectReference()).locked = true;
                    }
                }
            }

            PdfArray orderArray = d.getAsArray(PdfName.Order);
            if (orderArray != null && !orderArray.isEmpty()) {
                references = new HashSet<>();
                isDuplicateRemoved = false;
                readOrderFromDictionary(null, orderArray, layerMap);
                //TODO DEVSIX-8490 remove this check when implemented
                if (isDuplicateRemoved) {
                    Logger logger = LoggerFactory.getLogger(PdfOCProperties.class);
                    logger.warn(KernelLogMessageConstant.DUPLICATE_ENTRIES_IN_ORDER_ARRAY_REMOVED);
                }
            }

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
                if (layer == null) {
                    continue;
                }

                //TODO DEVSIX-8490 remove this check and it statement when implemented
                if (references.contains(layer.getIndirectReference())) {
                    //We want to check if this duplicate layer has childLayers, if it has - throw an exception,
                    // else just don't add this layer.
                    if (i + 1 < orderArray.size() && orderArray.get(i + 1).getType() == PdfObject.ARRAY) {
                        final PdfArray nextArray = orderArray.getAsArray(i + 1);
                        if (nextArray.size() > 0 && nextArray.get(0).getType() != PdfObject.STRING) {
                            PdfIndirectReference ref = layer.getIndirectReference();
                            throw new PdfException(MessageFormatUtil.format(
                                    KernelExceptionMessageConstant.UNABLE_TO_REMOVE_DUPLICATE_LAYER
                                    , ref.toString()));
                        }
                    }

                    isDuplicateRemoved = true;
                } else {
                    references.add(layer.getIndirectReference());
                    layers.add(layer);
                    layer.onPanel = true;
                    if (parent != null)
                        parent.addChild(layer);
                    if (i + 1 < orderArray.size() && orderArray.get(i + 1).getType() == PdfObject.ARRAY) {
                        final PdfArray nextArray = orderArray.getAsArray(i + 1);
                        if (nextArray.size() > 0 && nextArray.get(0).getType() != PdfObject.STRING) {
                            readOrderFromDictionary(layer, orderArray.getAsArray(i + 1), layerMap);
                            i++;
                        }
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
        while (usedNames.contains(OC_CONFIG_NAME_PATTERN + uniqueID)) {
            uniqueID++;
        }
        return OC_CONFIG_NAME_PATTERN + uniqueID;
    }
}
