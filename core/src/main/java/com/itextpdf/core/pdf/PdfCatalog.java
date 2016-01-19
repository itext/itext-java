package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.layer.PdfOCProperties;
import com.itextpdf.core.pdf.navigation.PdfDestination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfCatalog extends PdfObjectWrapper<PdfDictionary> {

    protected final PdfPagesTree pageTree;
    protected PdfNameTree destinationTree = null;
    protected PdfOCProperties ocProperties;

    private final static String OutlineRoot = "Outlines";
    private PdfOutline outlines;
    private boolean replaceNamedDestinations = true;
    //This HashMap contents all pages of the document and outlines associated to them
    private Map<PdfObject, List<PdfOutline>> pagesWithOutlines = new HashMap<>();
    //This flag determines if Outline tree of the document has been built via calling getOutlines method. If this flag is false all outline operations will be ignored
    private boolean outlineMode;
    private Map<Object, PdfObject> names = new HashMap<>();
    private boolean isNamedDestinationsGot = false;

    protected PdfCatalog(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject);
        if (pdfObject == null) {
            throw new PdfException(PdfException.DocumentHasNoCatalogObject);
        }
        getPdfObject().makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Catalog);
        pageTree = new PdfPagesTree(this);
    }

    protected PdfCatalog(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
    }

    public void addPage(PdfPage page) {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        if (page.getDocument() != null && page.getDocument() != getDocument())
            throw new PdfException(PdfException.Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3).setMessageParams(page, getDocument(), page.getDocument());
        pageTree.addPage(page);
    }

    public void addPage(int index, PdfPage page) {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        if (page.getDocument() != null && page.getDocument() != getDocument())
            throw new PdfException(PdfException.Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3).setMessageParams(page, getDocument(), page.getDocument());
        pageTree.addPage(index, page);
    }

    public PdfPage getPage(int pageNum) {
        return pageTree.getPage(pageNum);
    }

    public PdfPage getPage(PdfDictionary pageDictionary) {
        return pageTree.getPage(pageDictionary);
    }

    public int getNumOfPages() {
        return pageTree.getNumOfPages();
    }

    public int getPageNum(PdfPage page) {
        return pageTree.getPageNum(page);
    }

    public int getPageNum(PdfDictionary pageDictionary) {
        return pageTree.getPageNum(pageDictionary);
    }

    public boolean removePage(PdfPage page) {
        //TODO log removing flushed page
        if(outlineMode)
            removeOutlines(page);
        return pageTree.removePage(page);
    }

    public PdfPage removePage(int pageNum) {
        //TODO log removing flushed page
        if(outlineMode)
            removeOutlines(getPage(pageNum));
        return pageTree.removePage(pageNum);
    }

    /**
     * Use this method to get the <B>Optional Content Properties Dictionary</B>.
     * Note that if you call this method, then the PdfDictionary with OCProperties will be
     * generated from PdfOCProperties object right before closing the PdfDocument,
     * so if you want to make low-level changes in Pdf structures themselves (PdfArray, PdfDictionary, etc),
     * then you should address directly those objects, e.g.:
     * <CODE>
     * PdfCatalog pdfCatalog = pdfDoc.getCatalog();
     * PdfDictionary ocProps = pdfCatalog.getAsDictionary(PdfName.OCProperties);
     * // manipulate with ocProps.
     * </CODE>
     * Also note that this method is implicitly called when creating a new PdfLayer instance,
     * so you should either use hi-level logic of operating with layers,
     * or manipulate low-level Pdf objects by yourself.
     *
     * @param createIfNotExists true to create new /OCProperties entry in catalog if not exists,
     *                          false to return null if /OCProperties entry in catalog is not present.
     * @return the Optional Content Properties Dictionary
     */
    public PdfOCProperties getOCProperties(boolean createIfNotExists) {
        if (ocProperties != null)
            return ocProperties;
        else {
            PdfDictionary ocPropertiesDict = getPdfObject().getAsDictionary(PdfName.OCProperties);
            if (ocPropertiesDict != null) {
                ocProperties = new PdfOCProperties(ocPropertiesDict, getDocument());
            } else if (createIfNotExists) {
                ocProperties = new PdfOCProperties(new PdfDictionary(), getDocument());
            }
        }
        return ocProperties;
    }

    /**
     * PdfCatalog will be flushed in PdfDocument.close(). User mustn't flush PdfCatalog!
     */
    @Override
    public void flush() {
        throw new PdfException(PdfException.YouCannotFlushPdfCatalogManually);
    }

    public PdfCatalog setOpenAction(PdfDestination destination) {
        return put(PdfName.OpenAction, destination);
    }

    public PdfCatalog setOpenAction(PdfAction action) {
        return put(PdfName.OpenAction, action);
    }

    public PdfCatalog setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    public boolean isReplaceNamedDestinations() {
        return replaceNamedDestinations;
    }

    public void setReplaceNamedDestinations(boolean replaceNamedDestinations) {
        this.replaceNamedDestinations = replaceNamedDestinations;
    }

    /**
     * This flag determines if Outline tree of the document has been built via calling getOutlines method. If this flag is false all outline operations will be ignored
     * @return
     */
    public boolean isOutlineMode() {
        return outlineMode;
    }

    /**
     * This method sets a page mode of the document
     * @param pageMode
     * @return
     */
    public PdfCatalog setPageMode(PdfName pageMode){
        return put(PdfName.PageMode, pageMode);
    }

    public PdfName getPageMode() {
        return getPdfObject().getAsName(PdfName.PageMode);
    }

    /**
     * This method gets Names tree from the catalog.
     * @return
     * @throws PdfException
     */
    public Map<Object, PdfObject> getNamedDestinations() {
        Map<Object, PdfObject> names = getNamedDestinatnionsFromNames();
        names.putAll(getNamedDestinatnionsFromStrings());
        isNamedDestinationsGot = true;
        return names;
    }

    /**
    * An entry specifying the natural language, and optionally locale. Use this
    * to specify the Language attribute on a Tagged Pdf element.
    * For the content usage dictionary, use PdfName.Language
    */
    public void setLang(PdfString lang){
        getPdfObject().put(PdfName.Lang,lang);
    }

    public PdfString getLang(PdfName lang){
        return getPdfObject().getAsString(PdfName.Lang);
    }

    public void addDeveloperExtension(PdfDeveloperExtension extension) {
        PdfDictionary extensions = getPdfObject().getAsDictionary(PdfName.Extensions);

        if (extensions == null) {
            extensions = new PdfDictionary();
            put(PdfName.Extensions, extensions);
        } else {
            PdfDictionary existingExtensionDict = extensions.getAsDictionary(extension.getPrefix());

            if (extension != null) { // TODO: refactor
                int diff = extension.getBaseVersion().compareTo(existingExtensionDict.getAsName(PdfName.BaseVersion));
                if (diff < 0)
                    return;
                diff = extension.getExtensionLevel() - existingExtensionDict.getAsNumber(PdfName.ExtensionLevel).getIntValue();
                if (diff <= 0)
                    return;
            }
        }

        extensions.put(extension.getPrefix(), extension.getDeveloperExtensions());
    }

    public PdfNameTree getNameTree(PdfName treeType) {
        PdfNameTree nameTree = new PdfNameTree(this, treeType);
        return nameTree;
    }

    /**
     * True indicates that getOCProperties() was called, may have been modified,
     * and thus its dictionary needs to be reconstructed.
     */
    protected boolean isOCPropertiesMayHaveChanged() {
        return ocProperties != null;
    }

    /**
     * this method return map containing all pages of the document with associated outlines.
      * @return
     */
    Map<PdfObject, List<PdfOutline>> getPagesWithOutlines() {
        return pagesWithOutlines;
    }

    /**
     * This methods adds new name in the Dests NameTree. It throws an exception, if the name already exists.
     * @param key Name of the destination.
     * @param value An object destination refers to.
     * @throws PdfException
     */
    void addNewDestinationName(PdfObject key, PdfObject value) {
        if (!isNamedDestinationsGot)
            names = getNamedDestinations();
        if (names.containsKey(key))
            throw new PdfException(PdfException.NameAlreadyExistsInTheNameTree);

        if (destinationTree == null){
            destinationTree = new PdfNameTree(this, PdfName.Dests);
        }
        PdfDictionary destination = new PdfDictionary();
        destination.put(PdfName.D, value);
        destinationTree.addNewName(key, destination);
        names.put(key, destination);
    }

    /**
     * This method returns a complete outline tree of the whole document.
     * @param updateOutlines - if this flag is true, the method read the whole document and creates outline tree.
     *                       If false the method gets cached outline tree (if it was cached via calling getOutlines method before).
     * @return
     * @throws PdfException
     */
    PdfOutline getOutlines(boolean updateOutlines) {
        if (outlines!= null && !updateOutlines)
            return outlines;
        if (outlines != null){
            outlines.clear();
            pagesWithOutlines.clear();
        }

        outlineMode = true;
        if (!isNamedDestinationsGot)
            names = getNamedDestinations();
        PdfDictionary outlineRoot = getPdfObject().getAsDictionary(PdfName.Outlines);
        if (outlineRoot == null){
            return null;
        }

        outlines = new PdfOutline(OutlineRoot, outlineRoot, getDocument());
        getNextItem(outlineRoot.getAsDictionary(PdfName.First), outlines, names);

        return outlines;
    }

    /**
     * This method sets the root outline element in the catalog.
     * @param outline
     * @throws PdfException
     */
    void addRootOutline(PdfOutline outline) {
        if (!outlineMode)
            return;

        if (pagesWithOutlines.size() == 0) {
            put(PdfName.Outlines, outline.getContent());
        }
    }

    /**
     * This method removes all outlines associated with a given page
     * @param page
     * @throws PdfException
     */
    private void removeOutlines(PdfPage page) {
        for(PdfOutline outline: pagesWithOutlines.get(page.getPdfObject().getIndirectReference())){
            outline.removeOutline();
        }
    }

    private void addOutlineToPage(PdfOutline outline, Map<Object, PdfObject> names) {
        PdfObject obj = outline.getDestination().getDestinationPage(names);
        List<PdfOutline> outs = pagesWithOutlines.get(obj);
        if (outs == null) {
            outs = new ArrayList<PdfOutline>();
            pagesWithOutlines.put(obj, outs);
        }
        outs.add(outline);
    }

    private void getNextItem(PdfDictionary item, PdfOutline parent, Map<Object, PdfObject> names) {
        PdfOutline outline = new PdfOutline(item.getAsString(PdfName.Title).toUnicodeString(), item, parent);
        PdfObject dest = item.get(PdfName.Dest);
        if (dest != null) {
            PdfDestination destination = PdfDestination.makeDestination(dest);
            outline.setDestination(destination);
            if (replaceNamedDestinations){
                destination.replaceNamedDestination(names);
            }
            addOutlineToPage(outline, names);
        }
        parent.getAllChildren().add(outline);

        PdfDictionary processItem = item.getAsDictionary(PdfName.First);
        if (processItem != null){
            getNextItem(processItem, outline, names);
        }
        processItem = item.getAsDictionary(PdfName.Next);
        if (processItem != null){
            getNextItem(processItem, parent, names);
        }
    }

    private Map<Object, PdfObject> getNamedDestinatnionsFromNames() {
        Map<Object, PdfObject> names = new HashMap<Object, PdfObject>();
        PdfDictionary destinations = getDocument().getCatalog().getPdfObject().getAsDictionary(PdfName.Dests);
        if(destinations != null){
            Set<PdfName> keys = destinations.keySet();
            for (PdfName key : keys){
                PdfArray array = getNameArray(destinations.get(key));
                if (array == null){
                    continue;
                }
                names.put(key, array);
            }
            return names;
        }
        return names;
    }

    private Map<String, PdfObject> getNamedDestinatnionsFromStrings() {
        PdfDictionary dictionary = getDocument().getCatalog().getPdfObject().getAsDictionary(PdfName.Names);
        if(dictionary != null){
            dictionary = dictionary.getAsDictionary(PdfName.Dests);
            if (dictionary != null){
                Map<String, PdfObject> names = readTree(dictionary);
                for (Iterator<Map.Entry<String, PdfObject>> it = names.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, PdfObject> entry = it.next();
                    PdfArray arr = getNameArray(entry.getValue());
                    if (arr != null)
                        entry.setValue(arr);
                    else
                        it.remove();
                }
                return names;
            }
        }

        return new HashMap<String, PdfObject>();
    }

    private Map<String, PdfObject> readTree(PdfDictionary dictionary) {
        Map<String, PdfObject> items = new HashMap<String, PdfObject>();
        if (dictionary != null){
            iterateItems(dictionary, items, null);
        }
        return items;
    }

    private PdfString iterateItems(PdfDictionary dictionary, Map<String, PdfObject> items, PdfString leftOver) {
        PdfArray names = dictionary.getAsArray(PdfName.Names);
        if (names != null){
            for (int k = 0; k < names.size(); k++){
                PdfString name;
                if (leftOver == null)
                    name = names.getAsString(k++);
                else {
                    name = leftOver;
                    leftOver = null;
                }
                if(k < names.size()){
                    items.put(name.toUnicodeString(), names.get(k));
                }
                else {
                    return name;
                }
            }
        } else if ((names = dictionary.getAsArray(PdfName.Kids)) != null){
            for (int k = 0; k < names.size(); k++){
                PdfDictionary kid = names.getAsDictionary(k);
                leftOver = iterateItems(kid, items, leftOver);
            }
        }
        return null;
    }

    private PdfArray getNameArray(PdfObject obj) {
        if(obj == null)
            return null;
        if (obj.isArray())
            return (PdfArray)obj;
        else if (obj.isDictionary()) {
            PdfArray arr = ((PdfDictionary)obj).getAsArray(PdfName.D);
            return arr;
        }
        return null;
    }
}
