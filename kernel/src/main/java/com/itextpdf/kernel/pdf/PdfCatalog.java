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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfCatalog extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -1354567597112193418L;

    final private PdfPagesTree pageTree;
    protected Map<PdfName, PdfNameTree> nameTrees = new HashMap<>();
    protected PdfNumTree pageLabels;
    protected PdfOCProperties ocProperties;

    private static final String OutlineRoot = "Outlines";
    private PdfOutline outlines;
    //This HashMap contents all pages of the document and outlines associated to them
    private Map<PdfObject, List<PdfOutline>> pagesWithOutlines = new HashMap<>();
    //This flag determines if Outline tree of the document has been built via calling getOutlines method. If this flag is false all outline operations will be ignored
    private boolean outlineMode;

    private static final Set<PdfName> PAGE_MODES = new HashSet<>(
            Arrays.asList(PdfName.UseNone, PdfName.UseOutlines, PdfName.UseThumbs,
            PdfName.FullScreen, PdfName.UseOC, PdfName.UseAttachments));

    private static final Set<PdfName> PAGE_LAYOUTS = new HashSet<>(
            Arrays.asList(PdfName.SinglePage, PdfName.OneColumn, PdfName.TwoColumnLeft,
            PdfName.TwoColumnRight, PdfName.TwoPageLeft, PdfName.TwoPageRight));

    protected PdfCatalog(PdfDictionary pdfObject) {
        super(pdfObject);
        if (pdfObject == null) {
            throw new PdfException(PdfException.DocumentHasNoPdfCatalogObject);
        }
        ensureObjectIsAddedToDocument(pdfObject);
        getPdfObject().put(PdfName.Type, PdfName.Catalog);
        setForbidRelease();
        pageTree = new PdfPagesTree(this);
    }

    protected PdfCatalog(PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
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
        Logger logger = LoggerFactory.getLogger(PdfDocument.class);
        logger.warn("PdfCatalog cannot be flushed manually");
    }

    public PdfCatalog setOpenAction(PdfDestination destination) {
        return put(PdfName.OpenAction, destination.getPdfObject());
    }

    public PdfCatalog setOpenAction(PdfAction action) {
        return put(PdfName.OpenAction, action.getPdfObject());
    }

    public PdfCatalog setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * This method sets a page mode of the document.
     * <br>
     * Valid values are: {@code PdfName.UseNone}, {@code PdfName.UseOutlines}, {@code PdfName.UseThumbs},
     * {@code PdfName.FullScreen},  {@code PdfName.UseOC}, {@code PdfName.UseAttachments}.
     *
     * @param pageMode page mode.
     * @return current instance of PdfCatalog
     */
    public PdfCatalog setPageMode(PdfName pageMode) {
        if (PAGE_MODES.contains(pageMode)) {
            return put(PdfName.PageMode, pageMode);
        }
        return this;
    }

    public PdfName getPageMode() {
        return getPdfObject().getAsName(PdfName.PageMode);
    }

    /**
     * This method sets a page layout of the document
     *
     * @param pageLayout
     */
    public PdfCatalog setPageLayout(PdfName pageLayout) {
        if (PAGE_LAYOUTS.contains(pageLayout)) {
            return put(PdfName.PageLayout, pageLayout);
        }
        return this;
    }

    public PdfName getPageLayout() {
        return getPdfObject().getAsName(PdfName.PageLayout);
    }

    /**
     * This method sets the document viewer preferences, specifying the way the document shall be displayed on the
     * screen
     *
     * @param preferences
     */
    public PdfCatalog setViewerPreferences(PdfViewerPreferences preferences) {
        return put(PdfName.ViewerPreferences, preferences.getPdfObject());
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
     *
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     * @return returns {@link PdfNameTree}
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
     *
     * @return returns {@link PdfNumTree}
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
        put(PdfName.Lang, lang);
    }

    public PdfString getLang() {
        return getPdfObject().getAsString(PdfName.Lang);
    }

    public void addDeveloperExtension(PdfDeveloperExtension extension) {
        PdfDictionary extensions = getPdfObject().getAsDictionary(PdfName.Extensions);

        if (extensions == null) {
            extensions = new PdfDictionary();
            put(PdfName.Extensions, extensions);
        } else {
            PdfDictionary existingExtensionDict = extensions.getAsDictionary(extension.getPrefix());
            if (existingExtensionDict != null) {
                int diff = extension.getBaseVersion().compareTo(existingExtensionDict.getAsName(PdfName.BaseVersion));
                if (diff < 0)
                    return;
                diff = extension.getExtensionLevel() - existingExtensionDict.getAsNumber(PdfName.ExtensionLevel).intValue();
                if (diff <= 0)
                    return;
            }
        }

        extensions.put(extension.getPrefix(), extension.getDeveloperExtensions());
    }

    /**
     * Gets collection dictionary that a conforming reader shall use to enhance the presentation of file attachments
     * stored in the PDF document.
     *
     * @return {@link PdfCollection} wrapper of collection dictionary.
     */
    public PdfCollection getCollection() {
        PdfDictionary collectionDictionary = getPdfObject().getAsDictionary(PdfName.Collection);
        if (collectionDictionary != null) {
            return new PdfCollection(collectionDictionary);
        }
        return null;
    }

    /**
     * Sets collection dictionary that a conforming reader shall use to enhance the presentation of file attachments
     * stored in the PDF document.
     *
     * @param collection
     */
    public PdfCatalog setCollection(PdfCollection collection) {
        put(PdfName.Collection, collection.getPdfObject());
        return this;
    }

    public PdfCatalog put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    public PdfCatalog remove(PdfName key) {
        getPdfObject().remove(key);
        setModified();
        return this;
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

    PdfPagesTree getPageTree() {
        return pageTree;
    }

    /**
     * this method return map containing all pages of the document with associated outlines.
     *
     * @return map containing all pages of the document with associated outlines
     */
    Map<PdfObject, List<PdfOutline>> getPagesWithOutlines() {
        return pagesWithOutlines;
    }

    /**
     * This methods adds new name to the Dests NameTree. It throws an exception, if the name already exists.
     *
     * @param key   Name of the destination.
     * @param value An object destination refers to. Must be an array or a dictionary with key /D and array.
     *              See ISO 32000-1 12.3.2.3 for more info.
     */
    void addNamedDestination(String key, PdfObject value) {
        addNameToNameTree(key, value, PdfName.Dests);
    }

    /**
     * This methods adds a new name to the specified NameTree. It throws an exception, if the name already exists.
     *
     * @param key      key in the name tree
     * @param value    value in the name tree
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     */
    void addNameToNameTree(String key, PdfObject value, PdfName treeType) {
        getNameTree(treeType).addEntry(key, value);
    }

    /**
     * This method returns a complete outline tree of the whole document.
     *
     * @param updateOutlines if the flag is true, the method read the whole document and creates outline tree.
     *                       If false the method gets cached outline tree (if it was cached via calling getOutlines method before).
     * @return fully initialized {@link PdfOutline} object.
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
            if (null == getDocument().getWriter()) {
                return null;
            }
            outlines = new PdfOutline(getDocument());
        } else {
            constructOutlines(outlineRoot, destsTree.getNames());
        }

        return outlines;
    }

    /**
     * Indicates if the catalog has any outlines
     *
     * @return {@code true}, if there are outlines and {@code false} otherwise.
     */
    boolean hasOutlines() {
        return getPdfObject().containsKey(PdfName.Outlines);
    }

    /**
     * This flag determines if Outline tree of the document has been built via calling getOutlines method. If this flag is false all outline operations will be ignored
     *
     * @return state of outline mode.
     */
    boolean isOutlineMode() {
        return outlineMode;
    }

    /**
     * This method removes all outlines associated with a given page
     *
     * @param page
     */
    void removeOutlines(PdfPage page) {
        if (getDocument().getWriter() == null) {
            return;
        }
        if (hasOutlines()) {
            getOutlines(false);
            if (pagesWithOutlines.size() > 0) {
                if (pagesWithOutlines.get(page.getPdfObject()) != null) {
                    for (PdfOutline outline : pagesWithOutlines.get(page.getPdfObject())) {
                        outline.removeOutline();
                    }
                }
            }
        }
    }

    /**
     * This method sets the root outline element in the catalog.
     *
     * @param outline
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
            PdfObject pageObject = ((PdfArray) dest).get(0);
            for (PdfPage oldPage : page2page.keySet()) {
                if (oldPage.getPdfObject() == pageObject) {
                    // in the copiedArray old page ref will be correctly replaced by the new page ref as this page is already copied
                    PdfArray copiedArray = (PdfArray) dest.copyTo(toDocument, false);
                    d = new PdfExplicitDestination(copiedArray);
                    break;
                }
            }
        } else if (dest.isString() || dest.isName()) {
            PdfNameTree destsTree = getNameTree(PdfName.Dests);
            Map<String, PdfObject> dests = destsTree.getNames();
            String srcDestName = dest.isString() ? ((PdfString) dest).toUnicodeString() : ((PdfName) dest).getValue();
            PdfArray srcDestArray = (PdfArray) dests.get(srcDestName);
            if (srcDestArray != null) {
                PdfObject pageObject = srcDestArray.get(0);
                if (pageObject instanceof PdfNumber)
                    pageObject = getDocument().getPage(((PdfNumber) pageObject).intValue() + 1).getPdfObject();
                for (PdfPage oldPage : page2page.keySet()) {
                    if (oldPage.getPdfObject() == pageObject) {
                        d = new PdfStringDestination(srcDestName);
                        if (!isEqualSameNameDestExist(page2page, toDocument, srcDestName, srcDestArray, oldPage)) {
                            // in the copiedArray old page ref will be correctly replaced by the new page ref as this page is already copied
                            PdfArray copiedArray = (PdfArray) srcDestArray.copyTo(toDocument, false);
                            // here we can safely replace first item of the array because array of NamedDestination or StringDestination
                            // never refers to page in another document via PdfNumber, but should always refer to page within current document
                            // via page object reference.
                            copiedArray.set(0, page2page.get(oldPage).getPdfObject());
                            toDocument.addNamedDestination(srcDestName, copiedArray);
                        }
                        break;
                    }
                }
            }
        }
        return d;
    }

    private boolean isEqualSameNameDestExist(Map<PdfPage, PdfPage> page2page, PdfDocument toDocument, String srcDestName, PdfArray srcDestArray, PdfPage oldPage) {
        PdfArray sameNameDest = (PdfArray) toDocument.getCatalog().getNameTree(PdfName.Dests).getNames().get(srcDestName);
        boolean equalSameNameDestExists = false;
        if (sameNameDest != null && sameNameDest.getAsDictionary(0) != null) {
            PdfIndirectReference existingDestPageRef = sameNameDest.getAsDictionary(0).getIndirectReference();
            PdfIndirectReference newDestPageRef = page2page.get(oldPage).getPdfObject().getIndirectReference();
            if (equalSameNameDestExists = existingDestPageRef.equals(newDestPageRef) && sameNameDest.size() == srcDestArray.size()) {
                for (int i = 1; i < sameNameDest.size(); ++i) {
                    equalSameNameDestExists = equalSameNameDestExists && sameNameDest.get(i).equals(srcDestArray.get(i));
                }
            }
        }
        return equalSameNameDestExists;
    }

    private void addOutlineToPage(PdfOutline outline, Map<String, PdfObject> names) {
        PdfObject pageObj = outline.getDestination().getDestinationPage(names);
        if (pageObj instanceof PdfNumber)
            pageObj = getDocument().getPage(((PdfNumber) pageObj).intValue() + 1).getPdfObject();
        if (pageObj != null) {
            List<PdfOutline> outs = pagesWithOutlines.get(pageObj);
            if (outs == null) {
                outs = new ArrayList<>();
                pagesWithOutlines.put(pageObj, outs);
            }
            outs.add(outline);
        }
    }

    /**
     * Get the next outline of the current node in the outline tree by looking for a child or sibling node.
     * If there is no child or sibling of the current node {@link PdfCatalog#getParentNextOutline(PdfDictionary)} is called to get a hierarchical parent's next node. {@code null} is returned if one does not exist.
     *
     * @return the {@link PdfDictionary} object of the next outline if one exists, {@code null} otherwise.
     */
    private PdfDictionary getNextOutline(PdfDictionary first, PdfDictionary next, PdfDictionary parent) {
        if (first != null) {
            return first;
        } else if (next != null) {
            return next;
        } else {
            return getParentNextOutline(parent);
        }

    }

    /**
     * Gets the parent's next outline of the current node.
     * If the parent does not have a next we look at the grand parent, great-grand parent, etc until we find a next node or reach the root at which point {@code null} is returned to signify there is no next node present.
     *
     * @return the {@link PdfDictionary} object of the next outline if one exists, {@code null} otherwise.
     */
    private PdfDictionary getParentNextOutline(PdfDictionary parent) {
        if (parent == null) {
            return null;
        }
        PdfDictionary current = null;
        while (current == null) {
            current = parent.getAsDictionary(PdfName.Next);
            if (current == null) {
                parent = parent.getAsDictionary(PdfName.Parent);
                if (parent == null) {
                    return null;
                }
            }
        }
        return current;
    }

    private void addOutlineToPage(PdfOutline outline, PdfDictionary item, Map<String, PdfObject> names) {
        PdfObject dest = item.get(PdfName.Dest);
        if (dest != null) {
            PdfDestination destination = PdfDestination.makeDestination(dest);
            outline.setDestination(destination);
            addOutlineToPage(outline, names);
        } else {
            //Take into account outlines that specify their destination through an action
            PdfDictionary action = item.getAsDictionary(PdfName.A);
            if (action != null) {
                PdfName actionType = action.getAsName(PdfName.S);
                //Check if it is a go to action
                if (PdfName.GoTo.equals(actionType)) {
                    //Retrieve destination if it is.
                    PdfObject destObject = action.get(PdfName.D);
                    if (destObject != null) {
                        //Page is always the first object
                        PdfDestination destination = PdfDestination.makeDestination(destObject);
                        outline.setDestination(destination);
                        addOutlineToPage(outline, names);
                    }
                }
            }
        }
    }

    /**
     * Constructs {@link PdfCatalog#outlines} iteratively
     */
    private void constructOutlines(PdfDictionary outlineRoot, Map<String, PdfObject> names) {
        if (outlineRoot == null) {
            return;
        }
        PdfDictionary first = outlineRoot.getAsDictionary(PdfName.First);
        PdfDictionary current = first;
        PdfDictionary next;
        PdfDictionary parent;
        HashMap<PdfDictionary, PdfOutline> parentOutlineMap = new HashMap<>();

        outlines = new PdfOutline(OutlineRoot, outlineRoot, getDocument());
        PdfOutline parentOutline = outlines;
        parentOutlineMap.put(outlineRoot, parentOutline);

        while (current != null) {
            first = current.getAsDictionary(PdfName.First);
            next = current.getAsDictionary(PdfName.Next);
            parent = current.getAsDictionary(PdfName.Parent);

            parentOutline = parentOutlineMap.get(parent);
            PdfOutline currentOutline = new PdfOutline(current.getAsString(PdfName.Title).toUnicodeString(), current, parentOutline);
            addOutlineToPage(currentOutline, current, names);
            parentOutline.getAllChildren().add(currentOutline);

            if (first != null) {
                parentOutlineMap.put(current, currentOutline);
            }
            current = getNextOutline(first, next, parent);

        }
    }

}
