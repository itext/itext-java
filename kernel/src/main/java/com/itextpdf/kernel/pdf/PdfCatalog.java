/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.layer.PdfOCProperties;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.utils.NullCopyFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The root of a document’s object hierarchy.
 */
public class PdfCatalog extends PdfObjectWrapper<PdfDictionary> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfCatalog.class);
    private static final String ROOT_OUTLINE_TITLE = "Outlines";
    private static final Set<PdfName> PAGE_MODES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(PdfName.UseNone, PdfName.UseOutlines, PdfName.UseThumbs,
                    PdfName.FullScreen, PdfName.UseOC, PdfName.UseAttachments)));
    private static final Set<PdfName> PAGE_LAYOUTS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(PdfName.SinglePage, PdfName.OneColumn, PdfName.TwoColumnLeft,
                    PdfName.TwoColumnRight, PdfName.TwoPageLeft, PdfName.TwoPageRight)));
    final private PdfPagesTree pageTree;
    /**
     * Map of the {@link PdfNameTree}. Used for creation {@code name tree}  dictionary.
     */
    protected Map<PdfName, PdfNameTree> nameTrees = new LinkedHashMap<>();
    /**
     * Defining the page labelling for the document.
     */
    protected PdfNumTree pageLabels;
    /**
     * The document’s optional content properties dictionary.
     */
    protected PdfOCProperties ocProperties;
    private PdfOutline outlines;
    //This HashMap contents all pages of the document and outlines associated to them
    private final Map<PdfObject, List<PdfOutline>> pagesWithOutlines = new HashMap<>();
    //This flag determines if Outline tree of the document has been built via calling getOutlines method.
    // If this flag is false all outline operations will be ignored
    private boolean outlineMode;

    /**
     * Create {@link PdfCatalog} dictionary.
     *
     * @param pdfObject the dictionary to be wrapped
     */
    protected PdfCatalog(PdfDictionary pdfObject) {
        super(pdfObject);
        if (pdfObject == null) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_HAS_NO_PDF_CATALOG_OBJECT);
        }
        ensureObjectIsAddedToDocument(pdfObject);
        getPdfObject().put(PdfName.Type, PdfName.Catalog);
        setForbidRelease();
        pageTree = new PdfPagesTree(this);
    }

    /**
     * Create {@link PdfCatalog} to {@link PdfDocument}.
     *
     * @param pdfDocument A {@link PdfDocument} object representing the document
     *                    to which redaction applies
     */
    protected PdfCatalog(PdfDocument pdfDocument) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
    }

    /**
     * Use this method to get the <B>Optional Content Properties Dictionary</B>.
     * Note that if you call this method, then the {@link PdfDictionary} with OCProperties will be
     * generated from {@link PdfOCProperties} object right before closing the {@link PdfDocument},
     * so if you want to make low-level changes in Pdf structures themselves ({@link PdfArray},
     * {@link PdfDictionary}, etc), then you should address directly those objects, e.g.:
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
     *
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

    /**
     * Get {@link PdfDocument} with indirect reference associated with the object.
     *
     * @return the resultant dictionary
     */
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

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * A value specifying a destination that shall be displayed when the document is opened.
     * See ISO 32000-1, Table 28 – Entries in the catalog dictionary.
     *
     * @param destination instance of {@link PdfDestination}.
     *
     * @return destination
     */
    public PdfCatalog setOpenAction(PdfDestination destination) {
        return put(PdfName.OpenAction, destination.getPdfObject());
    }

    /**
     * A value specifying an action that shall be performed when the document is opened.
     * See ISO 32000-1, Table 28 – Entries in the catalog dictionary.
     *
     * @param action instance of {@link PdfAction}.
     *
     * @return action
     */
    public PdfCatalog setOpenAction(PdfAction action) {
        return put(PdfName.OpenAction, action.getPdfObject());
    }

    /**
     * The actions that shall be taken in response to various trigger events affecting the document as a whole.
     * See ISO 32000-1, Table 28 – Entries in the catalog dictionary.
     *
     * @param key    the key of which the associated value needs to be returned
     * @param action instance of {@link PdfAction}.
     *
     * @return additional action
     */
    public PdfCatalog setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * Get page mode of the document.
     *
     * @return current instance of {@link PdfCatalog}
     */
    public PdfName getPageMode() {
        return getPdfObject().getAsName(PdfName.PageMode);
    }

    /**
     * This method sets a page mode of the document.
     * <br>
     * Valid values are: {@code PdfName.UseNone}, {@code PdfName.UseOutlines}, {@code PdfName.UseThumbs},
     * {@code PdfName.FullScreen},  {@code PdfName.UseOC}, {@code PdfName.UseAttachments}.
     *
     * @param pageMode page mode.
     *
     * @return current instance of PdfCatalog
     */
    public PdfCatalog setPageMode(PdfName pageMode) {
        if (PAGE_MODES.contains(pageMode)) {
            return put(PdfName.PageMode, pageMode);
        }
        return this;
    }

    /**
     * Get page layout of the document.
     *
     * @return name object of page layout that shall be used when document is opened
     */
    public PdfName getPageLayout() {
        return getPdfObject().getAsName(PdfName.PageLayout);
    }

    /**
     * This method sets a page layout of the document
     *
     * @param pageLayout page layout of the document
     *
     * @return {@link PdfCatalog} instance with applied page layout
     */
    public PdfCatalog setPageLayout(PdfName pageLayout) {
        if (PAGE_LAYOUTS.contains(pageLayout)) {
            return put(PdfName.PageLayout, pageLayout);
        }
        return this;
    }

    /**
     * Get viewer preferences of the document.
     *
     * @return dictionary of viewer preferences
     */
    public PdfViewerPreferences getViewerPreferences() {
        PdfDictionary viewerPreferences = getPdfObject().getAsDictionary(PdfName.ViewerPreferences);
        if (viewerPreferences != null) {
            return new PdfViewerPreferences(viewerPreferences);
        } else {
            return null;
        }
    }

    /**
     * This method sets the document viewer preferences, specifying the way the document shall be displayed on the
     * screen
     *
     * @param preferences document's {@link PdfViewerPreferences viewer preferences}
     *
     * @return {@link PdfCatalog} instance with applied viewer preferences
     */
    public PdfCatalog setViewerPreferences(PdfViewerPreferences preferences) {
        return put(PdfName.ViewerPreferences, preferences.getPdfObject());
    }

    /**
     * This method gets Names tree from the catalog.
     *
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     *
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
     * @param createIfNotExists defines whether the NumberTree of Page Labels should be created
     *                          if it didn't exist before
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
     * Get natural language.
     *
     * @return natural language
     */
    public PdfString getLang() {
        return getPdfObject().getAsString(PdfName.Lang);
    }

    /**
     * An entry specifying the natural language, and optionally locale. Use this
     * to specify the Language attribute on a Tagged Pdf element.
     * For the content usage dictionary, use PdfName.Language
     *
     * @param lang {@link PdfString language} to be set
     */
    public void setLang(PdfString lang) {
        put(PdfName.Lang, lang);
    }

    /**
     * Add an extensions dictionary containing developer prefix identification and version
     * numbers for developer extensions that occur in this document.
     * See ISO 32000-1, Table 28 – Entries in the catalog dictionary.
     *
     * @param extension enables developers to identify their own extension
     *                  relative to a base version of PDF
     */
    public void addDeveloperExtension(PdfDeveloperExtension extension) {
        PdfDictionary extensions = getPdfObject().getAsDictionary(PdfName.Extensions);

        if (extensions == null) {
            extensions = new PdfDictionary();
            put(PdfName.Extensions, extensions);
        }

        if (extension.isMultiValued()) {
            // for multivalued extensions, we only check whether one of the same level is present or not
            // (main use case: ISO extensions)
            PdfArray existingExtensionArray = extensions.getAsArray(extension.getPrefix());
            if (existingExtensionArray == null) {
                existingExtensionArray = new PdfArray();
                extensions.put(extension.getPrefix(), existingExtensionArray);
            } else {
                for (int i = 0; i < existingExtensionArray.size(); i++) {
                    PdfDictionary pdfDict = existingExtensionArray.getAsDictionary(i);
                    // for array-based extensions, we check for membership only, since comparison doesn't make sense
                    if (pdfDict.getAsNumber(PdfName.ExtensionLevel).intValue() == extension.getExtensionLevel()) {
                        return;
                    }
                }
            }
            existingExtensionArray.add(extension.getDeveloperExtensions());
            existingExtensionArray.setModified();
        } else {
            // for single-valued extensions, we compare against the existing extension level
            PdfDictionary existingExtensionDict = extensions.getAsDictionary(extension.getPrefix());
            if (existingExtensionDict != null) {
                int diff = extension.getBaseVersion().compareTo(existingExtensionDict.getAsName(PdfName.BaseVersion));
                if (diff < 0)
                    return;
                diff = extension.getExtensionLevel() - existingExtensionDict.
                        getAsNumber(PdfName.ExtensionLevel).intValue();
                if (diff <= 0)
                    return;
            }
            extensions.put(extension.getPrefix(), extension.getDeveloperExtensions());
        }
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
     * @param collection {@link PdfCollection dictionary}
     *
     * @return {@link PdfCatalog} instance with applied collection dictionary
     */
    public PdfCatalog setCollection(PdfCollection collection) {
        put(PdfName.Collection, collection.getPdfObject());
        return this;
    }

    /**
     * Add key and value to {@link PdfCatalog} dictionary.
     *
     * @param key   the dictionary key corresponding with the PDF object
     * @param value the value of key
     *
     * @return the key and value
     */
    public PdfCatalog put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    /**
     * Remove key from catalog dictionary.
     *
     * @param key the dictionary key corresponding with the PDF object
     *
     * @return the key
     */
    public PdfCatalog remove(PdfName key) {
        getPdfObject().remove(key);
        setModified();
        return this;
    }

    /**
     * True indicates that getOCProperties() was called, may have been modified,
     * and thus its dictionary needs to be reconstructed.
     *
     * @return boolean indicating if the dictionary needs to be reconstructed
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
    void addNamedDestination(PdfString key, PdfObject value) {
        addNameToNameTree(key, value, PdfName.Dests);
    }

    /**
     * This methods adds a new name to the specified NameTree. It throws an exception, if the name already exists.
     *
     * @param key      key in the name tree
     * @param value    value in the name tree
     * @param treeType type of the tree (Dests, AP, EmbeddedFiles etc).
     */
    void addNameToNameTree(PdfString key, PdfObject value, PdfName treeType) {
        getNameTree(treeType).addEntry(key, value);
    }

    /**
     * This method returns a complete outline tree of the whole document.
     *
     * @param updateOutlines if the flag is true, the method read the whole document and creates outline tree.
     *                       If false the method gets cached outline tree (if it was cached via calling
     *                       getOutlines method before).
     *
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
            constructOutlines(outlineRoot, destsTree);
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
     * This flag determines if Outline tree of the document has been built via calling getOutlines method.
     * If this flag is false all outline operations will be ignored
     *
     * @return state of outline mode.
     */
    boolean isOutlineMode() {
        return outlineMode;
    }

    /**
     * This method removes all outlines associated with a given page
     *
     * @param page the page to remove outlines
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
     * @param outline the outline dictionary that shall be the root of the document’s outline hierarchy
     */
    void addRootOutline(PdfOutline outline) {
        if (!outlineMode)
            return;

        if (pagesWithOutlines.size() == 0) {
            put(PdfName.Outlines, outline.getContent());
        }
    }

    /**
     * Construct {@link PdfCatalog dictionary} iteratively. Invalid pdf documents will be processed depending on {@link
     * StrictnessLevel}, if it set to lenient, we will ignore and process invalid outline structure, otherwise {@link
     * PdfException} will be thrown.
     *
     * @param outlineRoot {@link PdfOutline dictionary} root.
     * @param names       map containing the PdfObjects stored in the tree.
     */
    void constructOutlines(PdfDictionary outlineRoot, IPdfNameTreeAccess names) {
        if (outlineRoot == null) {
            return;
        }

        PdfReader reader = getDocument().getReader();
        final boolean isLenientLevel =
                reader == null || StrictnessLevel.CONSERVATIVE.isStricter(reader.getStrictnessLevel());
        PdfDictionary current = outlineRoot.getAsDictionary(PdfName.First);

        outlines = new PdfOutline(ROOT_OUTLINE_TITLE, outlineRoot, getDocument());
        PdfOutline parentOutline = outlines;

        Map<PdfOutline, PdfDictionary> nextUnprocessedChildForParentMap = new HashMap<>();
        Set<PdfDictionary> alreadyVisitedOutlinesSet = new HashSet<>();

        while (current != null) {
            PdfDictionary parent = current.getAsDictionary(PdfName.Parent);
            if (null == parent && !isLenientLevel) {
                throw new PdfException(
                        MessageFormatUtil.format(
                                KernelExceptionMessageConstant.CORRUPTED_OUTLINE_NO_PARENT_ENTRY,
                                current.indirectReference));
            }
            PdfString title = current.getAsString(PdfName.Title);
            if (null == title) {
                throw new PdfException(
                        MessageFormatUtil.format(
                                KernelExceptionMessageConstant.CORRUPTED_OUTLINE_NO_TITLE_ENTRY,
                                current.indirectReference));
            }
            PdfOutline currentOutline = new PdfOutline(title.toUnicodeString(), current, parentOutline);
            alreadyVisitedOutlinesSet.add(current);
            addOutlineToPage(currentOutline, current, names);
            parentOutline.getAllChildren().add(currentOutline);

            PdfDictionary first = current.getAsDictionary(PdfName.First);
            PdfDictionary next = current.getAsDictionary(PdfName.Next);
            if (first != null) {
                if (alreadyVisitedOutlinesSet.contains(first)) {
                    if (!isLenientLevel) {
                        throw new PdfException(MessageFormatUtil.format(
                                KernelExceptionMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP, first));
                    }
                    LOGGER.warn(MessageFormatUtil.format(
                            KernelLogMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP, first));
                    return;
                }
                // Down in hierarchy; when returning up, process `next`.
                nextUnprocessedChildForParentMap.put(parentOutline, next);
                parentOutline = currentOutline;
                current = first;
            } else if (next != null) {
                if (alreadyVisitedOutlinesSet.contains(next)) {
                    if (!isLenientLevel) {
                        throw new PdfException(MessageFormatUtil.format(
                                KernelExceptionMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP, next));
                    }
                    LOGGER.warn(MessageFormatUtil.format(
                            KernelLogMessageConstant.CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP, next));
                    return;
                }
                // Next sibling in hierarchy
                current = next;
            } else {
                // Up in hierarchy using 'nextUnprocessedChildForParentMap'.
                current = null;
                while (current == null && parentOutline != null) {
                    parentOutline = parentOutline.getParent();
                    if (parentOutline != null) {
                        current = nextUnprocessedChildForParentMap.get(parentOutline);
                    }
                }
            }
        }
    }

    PdfDestination copyDestination(PdfObject dest, Map<PdfPage, PdfPage> page2page, PdfDocument toDocument) {
        if (null == dest) {
            return null;
        }
        PdfDestination d = null;
        if (dest.isArray()) {
            PdfObject pageObject = ((PdfArray) dest).get(0);
            for (PdfPage oldPage : page2page.keySet()) {
                if (oldPage.getPdfObject() == pageObject) {
                    // in the copiedArray old page ref will be correctly replaced by the new page ref
                    // as this page is already copied
                    final PdfArray copiedArray = (PdfArray) dest.copyTo(toDocument, false,
                            NullCopyFilter.getInstance());
                    d = new PdfExplicitDestination(copiedArray);
                    break;
                }
            }
        } else if (dest.isString() || dest.isName()) {
            PdfNameTree destsTree = getNameTree(PdfName.Dests);
            Map<PdfString, PdfObject> dests = destsTree.getNames();
            PdfString srcDestName = dest.isString() ? (PdfString) dest : new PdfString(((PdfName) dest).getValue());
            PdfArray srcDestArray = (PdfArray) dests.get(srcDestName);
            if (srcDestArray != null) {
                PdfObject pageObject = srcDestArray.get(0);
                if (pageObject instanceof PdfNumber)
                    pageObject = getDocument().getPage(((PdfNumber) pageObject).intValue() + 1).getPdfObject();
                for (PdfPage oldPage : page2page.keySet()) {
                    if (oldPage.getPdfObject() == pageObject) {
                        d = new PdfStringDestination(srcDestName);
                        if (!isEqualSameNameDestExist(page2page, toDocument, srcDestName, srcDestArray, oldPage)) {
                            // in the copiedArray old page ref will be correctly replaced by the new page ref as this
                            // page is already copied
                            PdfArray copiedArray = (PdfArray) srcDestArray.copyTo(toDocument, false);
                            // here we can safely replace first item of the array because array of NamedDestination or
                            // StringDestination never refers to page in another document via PdfNumber, but should
                            // always refer to page within current document via page object reference.
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

    PdfDictionary fillAndGetOcPropertiesDictionary() {
        if (ocProperties != null) {
            ocProperties.fillDictionary(false);
            getPdfObject().put(PdfName.OCProperties, ocProperties.getPdfObject());
            ocProperties = null;
        }
        if (getPdfObject().getAsDictionary(PdfName.OCProperties) == null) {
            final PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.makeIndirect(getDocument());
            getDocument().getCatalog().getPdfObject().put(PdfName.OCProperties, pdfDictionary);
        }
        return getPdfObject().getAsDictionary(PdfName.OCProperties);
    }

    private boolean isEqualSameNameDestExist(Map<PdfPage, PdfPage> page2page, PdfDocument toDocument,
            PdfString srcDestName, PdfArray srcDestArray, PdfPage oldPage) {
        PdfArray sameNameDest = (PdfArray) toDocument.getCatalog().getNameTree(PdfName.Dests).
                getNames().get(srcDestName);
        boolean equalSameNameDestExists = false;
        if (sameNameDest != null && sameNameDest.getAsDictionary(0) != null) {
            PdfIndirectReference existingDestPageRef = sameNameDest.getAsDictionary(0).getIndirectReference();
            PdfIndirectReference newDestPageRef = page2page.get(oldPage).getPdfObject().getIndirectReference();
            if (equalSameNameDestExists = existingDestPageRef.equals(newDestPageRef) &&
                    sameNameDest.size() == srcDestArray.size()) {
                for (int i = 1; i < sameNameDest.size(); ++i) {
                    equalSameNameDestExists = equalSameNameDestExists &&
                            sameNameDest.get(i).equals(srcDestArray.get(i));
                }
            }
        }
        return equalSameNameDestExists;
    }

    private void addOutlineToPage(PdfOutline outline, IPdfNameTreeAccess names) {
        PdfObject pageObj = outline.getDestination().getDestinationPage(names);
        if (pageObj instanceof PdfNumber) {
            final int pageNumber = ((PdfNumber) pageObj).intValue() + 1;
            try {
                pageObj = getDocument().getPage(pageNumber).getPdfObject();
            } catch (IndexOutOfBoundsException ex) {
                pageObj = null;
                LOGGER.warn(MessageFormatUtil.format(
                        IoLogMessageConstant.OUTLINE_DESTINATION_PAGE_NUMBER_IS_OUT_OF_BOUNDS, pageNumber)
                );
            }
        }

        if (pageObj != null) {
            List<PdfOutline> outs = pagesWithOutlines.get(pageObj);
            if (outs == null) {
                outs = new ArrayList<>();
                pagesWithOutlines.put(pageObj, outs);
            }
            outs.add(outline);
        }
    }

    private void addOutlineToPage(PdfOutline outline, PdfDictionary item, IPdfNameTreeAccess names) {
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
}
