package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfCatalog extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -1354567597112193418L;
	
    protected final PdfPagesTree pageTree;
    protected Map<PdfName, PdfNameTree> nameTrees = new HashMap<>();
    protected PdfNumTree pageLabels;
    protected PdfOCProperties ocProperties;

    private final static String OutlineRoot = "Outlines";
    private PdfOutline outlines;
    private boolean replaceNamedDestinations = true;
    //This HashMap contents all pages of the document and outlines associated to them
    private Map<PdfObject, List<PdfOutline>> pagesWithOutlines = new HashMap<>();
    //This flag determines if Outline tree of the document has been built via calling getOutlines method. If this flag is false all outline operations will be ignored
    private boolean outlineMode;

    protected PdfCatalog(PdfDictionary pdfObject) {
        super(pdfObject);
        if (pdfObject == null) {
            throw new PdfException(PdfException.DocumentHasNoCatalogObject);
        }
        ensureObjectIsAddedToDocument(pdfObject);
        getPdfObject().put(PdfName.Type, PdfName.Catalog);
        setForbidRelease();
        pageTree = new PdfPagesTree(this);
    }

    protected PdfCatalog(PdfDocument pdfDocument) {
        this(new PdfDictionary().makeIndirect(pdfDocument));
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

    public int getNumberOfPages() {
        return pageTree.getNumberOfPages();
    }

    public int getPageNumber(PdfPage page) {
        return pageTree.getPageNumber(page);
    }

    public int getPageNumber(PdfDictionary pageDictionary) {
        return pageTree.getPageNumber(pageDictionary);
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
                if (getDocument().getWriter() != null) {
                    ocPropertiesDict.makeIndirect(getDocument());
                }
                ocProperties = new PdfOCProperties(ocPropertiesDict);
            } else if (createIfNotExists) {
                ocProperties = new PdfOCProperties(getDocument());
            }
        }
        return ocProperties;
    }

    public PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
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
     *
     * @return
     */
    public boolean isOutlineMode() {
        return outlineMode;
    }

    /**
     * This method sets a page mode of the document
     *
     * @param pageMode
     * @return
     */
    public PdfCatalog setPageMode(PdfName pageMode) {
        if (!pageMode.equals(PdfName.UseNone) && !pageMode.equals(PdfName.UseOutlines) &&
                !pageMode.equals(PdfName.UseThumbs) && !pageMode.equals(PdfName.FullScreen) &&
                !pageMode.equals(PdfName.UseOC) && !pageMode.equals(PdfName.UseAttachments)) {
            return this;
        }
        return put(PdfName.PageMode, pageMode);
    }

    public PdfName getPageMode() {
        return getPdfObject().getAsName(PdfName.PageMode);
    }

    /**
     * This method sets a page layout of the document
     * @param pageLayout
     * @return
     */
    public PdfCatalog setPageLayout (PdfName pageLayout) {
        if (!pageLayout.equals(PdfName.SinglePage) && !pageLayout.equals(PdfName.OneColumn) &&
                !pageLayout.equals(PdfName.TwoColumnLeft) && !pageLayout.equals(PdfName.TwoColumnRight) &&
                !pageLayout.equals(PdfName.TwoPageLeft) && !pageLayout.equals(PdfName.TwoPageRight)) {
            return this;
        }
        return put(PdfName.PageLayout, pageLayout);
    }

    public PdfName getPageLayout(){
        return getPdfObject().getAsName(PdfName.PageLayout);
    }

    /**
     * This method sets the document viewer preferences, specifying the way the document shall be displayed on the
     * screen
     * @param preferences
     * @return
     */
    public PdfCatalog setViewerPreferences(PdfViewerPreferences preferences) {
        return put(PdfName.ViewerPreferences, preferences);
    }

    public PdfViewerPreferences getViewerPreferences() {
        PdfDictionary viewerPreferences = getPdfObject().getAsDictionary(PdfName.ViewerPreferences);
        if (viewerPreferences != null) {
            return new PdfViewerPreferences(viewerPreferences);
        } else {
            return null;
        }
    }

    /**
     * This method gets Names tree from the catalog.
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     * @return
     */
    public PdfNameTree getNameTree(PdfName treeType) {
        PdfNameTree tree = nameTrees.get(treeType);
        if (tree == null) {
            tree = new PdfNameTree(this, treeType);
            nameTrees.put(treeType, tree);
        }

        return tree;
    }

    /**
     * This method returns the NumberTree of Page Labels
     * @return
     */
    public PdfNumTree getPageLabelsTree(boolean createIfNotExists) {
        if (pageLabels == null && (getPdfObject().containsKey(PdfName.PageLabels) || createIfNotExists)) {
            pageLabels = new PdfNumTree(this, PdfName.PageLabels);
        }

        return pageLabels;
    }

    /**
     * An entry specifying the natural language, and optionally locale. Use this
     * to specify the Language attribute on a Tagged Pdf element.
     * For the content usage dictionary, use PdfName.Language
     */
    public void setLang(PdfString lang) {
        getPdfObject().put(PdfName.Lang, lang);
    }

    public PdfString getLang(PdfName lang) {
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

    /**
     * Sets collection dictionary that a conforming reader shall use to enhance the presentation of file attachments
     * stored in the PDF document.
     * @param collection
     * @return
     */
    public PdfCatalog setCollection(PdfCollection collection) {
        return put(PdfName.Collection, collection);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * True indicates that getOCProperties() was called, may have been modified,
     * and thus its dictionary needs to be reconstructed.
     */
    protected boolean isOCPropertiesMayHaveChanged() {
        return ocProperties != null;
    }

    PdfPage removePage(int pageNum) {
        return pageTree.removePage(pageNum);
    }
    /**
     * this method return map containing all pages of the document with associated outlines.
     *
     * @return
     */
    Map<PdfObject, List<PdfOutline>> getPagesWithOutlines() {
        return pagesWithOutlines;
    }

    /**
     * This methods adds new name to the Dests NameTree. It throws an exception, if the name already exists.
     *
     * @param key   Name of the destination.
     * @param value An object destination refers to. Must be an array or a dictionary with key /D and array.
     *              See PdfSpec 12.3.2.3 for more info.
     * @throws PdfException
     */
    void addNamedDestination(String key, PdfObject value) {
        addNameToNameTree(key, value, PdfName.Dests);
    }

    /**
     * This methods adds a new name to the specified NameTree. It throws an exception, if the name already exists.
     *
     * @param key key in the name tree
     * @param value value in the name tree
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     */
    void addNameToNameTree(String key, PdfObject value, PdfName treeType){
        getNameTree(treeType).addEntry(key, value);
    }

    /**
     * This method returns a complete outline tree of the whole document.
     *
     * @param updateOutlines - if this flag is true, the method read the whole document and creates outline tree.
     *                       If false the method gets cached outline tree (if it was cached via calling getOutlines method before).
     * @return
     * @throws PdfException
     */
    PdfOutline getOutlines(boolean updateOutlines) {
        if (outlines != null && !updateOutlines)
            return outlines;
        if (outlines != null) {
            outlines.clear();
            pagesWithOutlines.clear();
        }

        outlineMode = true;
        PdfNameTree destsTree = getNameTree(PdfName.Dests);

        PdfDictionary outlineRoot = getPdfObject().getAsDictionary(PdfName.Outlines);
        if (outlineRoot == null) {
            outlines = new PdfOutline(getDocument());
        } else {
            outlines = new PdfOutline(OutlineRoot, outlineRoot, getDocument());
            getNextItem(outlineRoot.getAsDictionary(PdfName.First), outlines, destsTree.getNames());
        }

        return outlines;
    }

    /**
     * Indicates if the catalog has any outlines
     * @return {@code true}, if there are outlines and {@code false} otherwise.
     */
    boolean hasOutlines() {
        return getPdfObject().containsKey(PdfName.Outlines);
    }

    /**
     * This method removes all outlines associated with a given page
     *
     * @param page
     * @throws PdfException
     */
    void removeOutlines(PdfPage page) {
        if (getDocument().getWriter() == null) {
            return;
        }
        if (hasOutlines()) {
            getOutlines(false);
            if (!pagesWithOutlines.isEmpty()) {
                for (PdfOutline outline : pagesWithOutlines.get(page.getPdfObject())) {
                    outline.removeOutline();
                }
            }
        }
    }

    /**
     * This method sets the root outline element in the catalog.
     *
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

    PdfDestination copyDestination(PdfObject dest, Map<PdfPage, PdfPage> page2page, PdfDocument toDocument) {
        PdfDestination d = null;
        if (dest.isArray()) {
            PdfObject pageObject = ((PdfArray)dest).get(0);
            for (PdfPage oldPage : page2page.keySet()) {
                if (oldPage.getPdfObject() == pageObject) {
                    PdfArray array = new PdfArray((PdfArray)dest);
                    array.set(0, page2page.get(oldPage).getPdfObject());
                    d = new PdfExplicitDestination(array);
                }
            }
        } else if (dest.isString()) {
            PdfNameTree destsTree = getNameTree(PdfName.Dests);
            Map<String, PdfObject> dests = destsTree.getNames();
            String name = ((PdfString) dest).toUnicodeString();
            PdfArray array = (PdfArray)dests.get(name);
            if (array != null) {
                PdfObject pageObject = array.get(0);
                for (PdfPage oldPage : page2page.keySet()) {
                    if (oldPage.getPdfObject() == pageObject) {
                        array.set(0, page2page.get(oldPage).getPdfObject());
                        d = new PdfStringDestination(name);
                        toDocument.addNameDestination(name,array);
                    }
                }
            }
        }

        return d;
    }

    private void addOutlineToPage(PdfOutline outline, Map<String, PdfObject> names) {
        PdfObject pageObj = outline.getDestination().getDestinationPage(names);
        if (pageObj != null) {
            List<PdfOutline> outs = pagesWithOutlines.get(pageObj);
            if (outs == null) {
                outs = new ArrayList<>();
                pagesWithOutlines.put(pageObj, outs);
            }
            outs.add(outline);
        }
    }

    private void getNextItem(PdfDictionary item, PdfOutline parent, Map<String, PdfObject> names) {
        PdfOutline outline = new PdfOutline(item.getAsString(PdfName.Title).toUnicodeString(), item, parent);
        PdfObject dest = item.get(PdfName.Dest);
        if (dest != null) {
            PdfDestination destination = PdfDestination.makeDestination(dest);
            outline.setDestination(destination);
            addOutlineToPage(outline, names);
        }
        parent.getAllChildren().add(outline);

        PdfDictionary processItem = item.getAsDictionary(PdfName.First);
        if (processItem != null) {
            getNextItem(processItem, outline, names);
        }
        processItem = item.getAsDictionary(PdfName.Next);
        if (processItem != null) {
            getNextItem(processItem, parent, names);
        }
    }
}
