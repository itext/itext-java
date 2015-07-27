package com.itextpdf.core.pdf.layer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.pdf.*;

import java.util.*;

/**
 * This class represents /OCProperties entry if pdf catalog and manages
 * the layers of the pdf document.
 */
public class PdfOCProperties extends PdfObjectWrapper<PdfDictionary> {

    private List<PdfLayer> layers = new ArrayList<PdfLayer>();

    /**
     * Creates a new PdfOCProperties instance by the dictionary it represents.
     * @param ocPropertiesDict the dictionary of optional content properties
     * @param pdfDocument the document the optional content belongs to
     * @throws PdfException
     */
    public PdfOCProperties(PdfDictionary ocPropertiesDict, PdfDocument pdfDocument) {
        super(ocPropertiesDict);
        makeIndirect(pdfDocument);
        readLayersFromDictionary();
    }

    /**
     * Use this method to set a collection of optional content groups
     * whose states are intended to follow a "radio button" paradigm.
     * That is, the state of at most one optional content group
     * in the array should be ON at a time: if one group is turned
     * ON, all others must be turned OFF.
     * @param group the radio group
     */
    public void addOCGRadioGroup(final List<PdfLayer> group) {
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
        getPdfObject().put(PdfName.D, d);


        List<PdfLayer> docOrder = new ArrayList<PdfLayer>(layers);
        for (Iterator<PdfLayer> it = docOrder.iterator(); it.hasNext();) {
            PdfLayer layer = it.next();
            if (layer.getParent() != null)
                it.remove();
        }
        PdfArray order = new PdfArray();
        for (Object element : docOrder) {
            PdfLayer layer = (PdfLayer)element;
            getOCGOrder(order, layer);
        }
        d.put(PdfName.Order, order);

        PdfArray off = new PdfArray();
        for (Object element : layers) {
            PdfLayer layer = (PdfLayer)element;
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
        return new ArrayList<PdfLayer>(layers);
    }

    /**
     * This method registers a new layer in the OCProperties.
     * @param layer the new layer
     */
    protected void registerLayer(final PdfLayer layer) {
        //TODO PdfWriter.checkPdfIsoConformance(this, PdfIsoKeys.PDFISOKEY_LAYER, layer);
        if (layer == null)
            throw new IllegalArgumentException("layer argument is null");
        layers.add(layer);
    }

    /**
     * Gets the order of the layers in which they will be displayed in the layer view panel,
     * including nesting.
     */
    private static void getOCGOrder(final PdfArray order, final PdfLayer layer) {
        if (!layer.isOnPanel())
            return;
        if (layer.getTitle() == null)
            order.add(layer.getPdfObject().getIndirectReference());
        List<PdfLayer> children = layer.getChildren();
        if (children == null)
            return;
        PdfArray kids = new PdfArray();
        if (layer.getTitle() != null)
            kids.add(new PdfString(layer.getTitle(), PdfEncodings.UnicodeBig));
        for (PdfLayer child : children) {
            getOCGOrder(kids, child);
        }
        if (kids.size() > 0)
            order.add(kids);
    }

    /**
     * Populates the /AS entry in the /D dictionary.
     * @throws PdfException
     */
    private void addASEvent(final PdfName event, final PdfName category) {
        PdfArray arr = new PdfArray();
        for (Object element : layers) {
            PdfLayer layer = (PdfLayer)element;
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
     * @throws PdfException
     */
    private void readLayersFromDictionary() {
        PdfArray ocgs = getPdfObject().getAsArray(PdfName.OCGs);
        if (ocgs == null || ocgs.isEmpty())
            return;

        Map<PdfIndirectReference, PdfLayer> layerMap = new TreeMap<PdfIndirectReference, PdfLayer>();
        for (int ind = 0; ind < ocgs.size(); ind++) {
            PdfLayer currentLayer = new PdfLayer(ocgs.getAsDictionary(ind), getDocument());
            // We will set onPanel to true later for the objects present in /D->/Order entry.
            currentLayer.onPanel = false;
            layerMap.put(currentLayer.getIndirectReference(), currentLayer);
        }

        PdfDictionary d = getPdfObject().getAsDictionary(PdfName.D);
        if (d != null && !d.isEmpty()) {
            PdfArray off = d.getAsArray(PdfName.OFF);
            if (off != null) {
                for (PdfObject offLayer : off) {
                    layerMap.get(offLayer).on = false;
                }
            }

            PdfArray locked = d.getAsArray(PdfName.Locked);
            if (locked != null) {
                for (PdfObject lockedLayer : locked) {
                    layerMap.get(lockedLayer).locked = true;
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
     * @throws PdfException
     */
    private void readOrderFromDictionary(PdfLayer parent, PdfArray orderArray, Map<PdfIndirectReference, PdfLayer> layerMap) {
        for (int i = 0; i < orderArray.size(); i++) {
            PdfObject item = orderArray.get(i);
            if (item.getType() == PdfObject.Dictionary) {
                PdfLayer layer = layerMap.get(item.getIndirectReference());
                if (layer != null) {
                    layers.add(layer);
                    layer.onPanel = true;
                    if (parent != null)
                        parent.addChild(layer);
                    if (i + 1 < orderArray.size() && orderArray.get(i + 1).getType() == PdfObject.Array) {
                        readOrderFromDictionary(layer, orderArray.getAsArray(i + 1), layerMap);
                        i++;
                    }
                }
            } else if (item.getType() == PdfObject.Array) {
                PdfArray subArray = (PdfArray)item;
                if (subArray.isEmpty()) continue;
                PdfObject firstObj = subArray.get(0);
                if (firstObj.getType() == PdfObject.String) {
                    PdfLayer titleLayer = PdfLayer.createTitleSilent(((PdfString)firstObj).toUnicodeString(), getDocument());
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
