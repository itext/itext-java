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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a wrapper-class for structure tree root dictionary. See ISO-32000-1 "14.7.2 Structure hierarchy".
 */
public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IStructureNode {


    private PdfDocument document;
    private ParentTreeHandler parentTreeHandler;
    private PdfStructIdTree idTree = null;

    private static Map<String, PdfName> staticRoleNames = new ConcurrentHashMap<>();

    /**
     * Creates a new structure tree root instance, this initializes empty logical structure in the document.
     * This class also handles global state of parent tree, so it's not expected to create multiple instances
     * of this class. Instead, use {@link PdfDocument#getStructTreeRoot()}.
     *
     * @param document a document to which new instance of struct tree root will be bound
     */
    public PdfStructTreeRoot(PdfDocument document) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(document), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    /**
     * Creates wrapper instance for already existing logical structure tree root in the document.
     * This class also handles global state of parent tree, so it's not expected to create multiple instances
     * of this class. Instead, use {@link PdfDocument#getStructTreeRoot()}.
     *
     * @param structTreeRootDict a dictionary that defines document structure tree root
     * @param document a document, which contains given structure tree root dictionary
     */
    public PdfStructTreeRoot(PdfDictionary structTreeRootDict, PdfDocument document) {
        super(structTreeRootDict);
        this.document = document;
        if (this.document == null) {
            ensureObjectIsAddedToDocument(structTreeRootDict);
            this.document = structTreeRootDict.getIndirectReference().getDocument();
        }
        setForbidRelease();
        parentTreeHandler = new ParentTreeHandler(this);

        // Always init role map dictionary in order to avoid inconsistency, because
        // iText often initializes it during role mapping resolution anyway.
        // In future, better way might be to not write it to the document needlessly
        // and avoid possible redundant modifications in append mode.
        getRoleMap();
    }

    public static PdfName convertRoleToPdfName(String role) {
        PdfName name = PdfName.staticNames.get(role);
        if (name != null) {
            return name;
        }
        name = staticRoleNames.get(role);
        if (name != null) {
            return name;
        }
        name = new PdfName(role);
        staticRoleNames.put(role, name);
        return name;
    }

    public PdfStructElem addKid(PdfStructElem structElem) {
        return addKid(-1, structElem);
    }

    public PdfStructElem addKid(int index, PdfStructElem structElem) {
        addKidObject(index, structElem.getPdfObject());
        return structElem;
    }

    @Override
    public IStructureNode getParent() {
        return null;
    }

    /**
     * Gets list of the direct kids of StructTreeRoot.
     * If certain kid is flushed, there will be a {@code null} in the list on it's place.
     *
     * @return list of the direct kids of StructTreeRoot.
     */
    @Override
    public List<IStructureNode> getKids() {
        PdfObject k = getPdfObject().get(PdfName.K);
        List<IStructureNode> kids = new ArrayList<>();

        if (k != null) {
            if (k.isArray()) {
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    ifKidIsStructElementAddToList(a.get(i), kids);
                }
            } else {
                ifKidIsStructElementAddToList(k, kids);
            }
        }
        return kids;
    }

    public PdfArray getKidsObject() {
        PdfArray k = null;
        PdfObject kObj = getPdfObject().get(PdfName.K);
        if (kObj != null && kObj.isArray()) {
            k = (PdfArray) kObj;
        }
        if (k == null) {
            k = new PdfArray();
            getPdfObject().put(PdfName.K, k);
            setModified();
            if (kObj != null) {
                k.add(kObj);
            }
        }
        return k;
    }

    public void addRoleMapping(String fromRole, String toRole) {
        PdfDictionary roleMap = getRoleMap();
        PdfObject prevVal = roleMap.put(convertRoleToPdfName(fromRole), convertRoleToPdfName(toRole));
        if (prevVal != null && prevVal instanceof PdfName) {
            Logger logger = LoggerFactory.getLogger(PdfStructTreeRoot.class);
            logger.warn(MessageFormat.format(IoLogMessageConstant.MAPPING_IN_STRUCT_ROOT_OVERWRITTEN, fromRole, prevVal,
                    toRole));
        }

        if (roleMap.isIndirect()) {
            roleMap.setModified();
        } else {
            setModified();
        }
    }

    public PdfDictionary getRoleMap() {
        PdfDictionary roleMap = getPdfObject().getAsDictionary(PdfName.RoleMap);
        if (roleMap == null) {
            roleMap = new PdfDictionary();
            getPdfObject().put(PdfName.RoleMap, roleMap);
            setModified();
        }
        return roleMap;
    }

    /**
     * Gets namespaces used within the document. Essentially this method returns value of {@link #getNamespacesObject()}
     * wrapped in the {@link PdfNamespace} and {@link List} classes. Therefore limitations of the referred method are
     * applied to this method too.
     *
     * @return a {@link List} of {@link PdfNamespace}s used within the document.
     */
    public List<PdfNamespace> getNamespaces() {
        PdfArray namespacesArray = getPdfObject().getAsArray(PdfName.Namespaces);
        if (namespacesArray == null) {
            return Collections.<PdfNamespace>emptyList();
        } else {
            List<PdfNamespace> namespacesList = new ArrayList<>(namespacesArray.size());
            for (int i = 0; i < namespacesArray.size(); ++i) {
                namespacesList.add(new PdfNamespace(namespacesArray.getAsDictionary(i)));
            }
            return namespacesList;
        }
    }

    /**
     * Adds a {@link PdfNamespace} to the list of the namespaces used within the document.
     * <p>
     * This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.
     *
     * @param namespace a {@link PdfNamespace} to be added.
     */
    public void addNamespace(PdfNamespace namespace) {
        getNamespacesObject().add(namespace.getPdfObject());
        setModified();
    }

    /**
     * An array of namespaces used within the document. This value, however, is not automatically updated while
     * the document is processed. It identifies only the namespaces that were in the document at the moment of it's
     * opening.
     *
     * @return {@link PdfArray} of namespaces used within the document.
     */
    public PdfArray getNamespacesObject() {
        PdfArray namespacesArray = getPdfObject().getAsArray(PdfName.Namespaces);
        if (namespacesArray == null) {
            namespacesArray = new PdfArray();
            VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.Namespaces, PdfName.StructTreeRoot);
            getPdfObject().put(PdfName.Namespaces, namespacesArray);
            setModified();
        }
        return namespacesArray;
    }

    /**
     * A {@link List} containing one or more {@link PdfFileSpec} objects, where each specified file
     * is a pronunciation lexicon, which is an XML file conforming to the Pronunciation Lexicon Specification (PLS) Version 1.0.
     * These pronunciation lexicons may be used as pronunciation hints when the document’s content is presented via
     * text-to-speech. Where two or more pronunciation lexicons apply to the same text, the first match – as defined by
     * the order of entries in the array and the order of entries inside the pronunciation lexicon file – should be used.
     * <p>
     * See ISO 32000-2 14.9.6, "Pronunciation hints".
     *
     * @return A {@link List} containing one or more {@link PdfFileSpec}.
     */
    public List<PdfFileSpec> getPronunciationLexiconsList() {
        PdfArray pronunciationLexicons = getPdfObject().getAsArray(PdfName.PronunciationLexicon);
        if (pronunciationLexicons == null) {
            return Collections.<PdfFileSpec>emptyList();
        } else {
            List<PdfFileSpec> lexiconsList = new ArrayList<>(pronunciationLexicons.size());
            for (int i = 0; i < pronunciationLexicons.size(); ++i) {
                lexiconsList.add(PdfFileSpec.wrapFileSpecObject(pronunciationLexicons.get(i)));
            }
            return lexiconsList;
        }
    }

    /**
     * Adds a single  {@link PdfFileSpec} object, which specifies XML file conforming to PLS.
     * For more info see {@link #getPronunciationLexiconsList()}.
     * <p>
     * This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.
     *
     * @param pronunciationLexiconFileSpec a {@link PdfFileSpec} object, which specifies XML file conforming to PLS.
     */
    public void addPronunciationLexicon(PdfFileSpec pronunciationLexiconFileSpec) {
        PdfArray pronunciationLexicons = getPdfObject().getAsArray(PdfName.PronunciationLexicon);
        if (pronunciationLexicons == null) {
            pronunciationLexicons = new PdfArray();
            VersionConforming.validatePdfVersionForDictEntry(getDocument(), PdfVersion.PDF_2_0, PdfName.PronunciationLexicon, PdfName.StructTreeRoot);
            getPdfObject().put(PdfName.PronunciationLexicon, pronunciationLexicons);
        }
        pronunciationLexicons.add(pronunciationLexiconFileSpec.getPdfObject());
        setModified();
    }

    /**
     * Creates and flushes parent tree entry for the page.
     * Effectively this means that new content mustn't be added to the page.
     *
     * @param page {@link PdfPage} for which to create parent tree entry. Typically this page is flushed after this call.
     */
    public void createParentTreeEntryForPage(PdfPage page) {
        getParentTreeHandler().createParentTreeEntryForPage(page);
    }

    public void savePageStructParentIndexIfNeeded(PdfPage page) {
        getParentTreeHandler().savePageStructParentIndexIfNeeded(page);
    }

    /**
     * Gets an unmodifiable collection of marked content references on page.
     *
     * NOTE: Do not remove tags when iterating over returned collection, this could
     * lead to the ConcurrentModificationException, because returned collection is backed by the internal list of the
     * actual page tags.
     *
     * @param page {@link PdfPage} to obtain unmodifiable collection of marked content references
     * @return the unmodifiable collection of marked content references on page, if no Mcrs defined returns null
     */
    public Collection<PdfMcr> getPageMarkedContentReferences(PdfPage page) {
        ParentTreeHandler.PageMcrsContainer pageMcrs = getParentTreeHandler().getPageMarkedContentReferences(page);
        return pageMcrs != null ? Collections.unmodifiableCollection(pageMcrs.getAllMcrsAsCollection()) : null;
    }

    public PdfMcr findMcrByMcid(PdfDictionary pageDict, int mcid) {
        return getParentTreeHandler().findMcrByMcid(pageDict, mcid);
    }

    public PdfObjRef findObjRefByStructParentIndex(PdfDictionary pageDict, int structParentIndex) {
        return getParentTreeHandler().findObjRefByStructParentIndex(pageDict, structParentIndex);
    }

    @Override
    public PdfName getRole() {
        return null;
    }

    @Override
    public void flush() {
        for (int i = 0; i < getDocument().getNumberOfPages(); ++i) {
            createParentTreeEntryForPage(getDocument().getPage(i + 1));
        }
        getPdfObject().put(PdfName.ParentTree, getParentTreeHandler().buildParentTree());
        getPdfObject().put(PdfName.ParentTreeNextKey, new PdfNumber((int) getDocument().getNextStructParentIndex()));
        if(this.idTree != null && this.idTree.isModified()) {
            getPdfObject().put(PdfName.IDTree, this.idTree.buildTree().makeIndirect(getDocument()));
        }
        if (!getDocument().isAppendMode()) {
            flushAllKids(this);
        }
        super.flush();
    }

    /**
     * Copies structure to a {@code destDocument}.
     *
     * NOTE: Works only for {@link PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument document to copy structure to. Shall not be current document.
     * @param page2page    association between original page and copied page.
     */
    public void copyTo(PdfDocument destDocument, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, page2page, getDocument());
    }

    /**
     * Copies structure to a {@code destDocument} and insert it in a specified position in the document.
     *
     * NOTE: Works only for {@link PdfStructTreeRoot} that is read from the document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param destDocument     document to copy structure to.
     * @param insertBeforePage indicates where the structure to be inserted.
     * @param page2page        association between original page and copied page.
     */
    public void copyTo(PdfDocument destDocument, int insertBeforePage, Map<PdfPage, PdfPage> page2page) {
        StructureTreeCopier.copyTo(destDocument, insertBeforePage, page2page, getDocument());
    }

    /**
     * Moves structure associated with specified page and insert it in a specified position in the document.
     * <p>
     * NOTE: Works only for document with not flushed pages.
     *
     * @param fromPage page which tag structure will be moved
     * @param insertBeforePage indicates before tags of which page tag structure will be moved to
     */
    public void move(PdfPage fromPage, int insertBeforePage) {
        for (int i = 1; i <= getDocument().getNumberOfPages(); ++i) {
            if (getDocument().getPage(i).isFlushed()) {
                throw new PdfException(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.CANNOT_MOVE_PAGES_IN_PARTLY_FLUSHED_DOCUMENT, i));
            }
        }
        StructureTreeCopier.move(getDocument(), fromPage, insertBeforePage);
    }

    public int getParentTreeNextKey() {
        // /ParentTreeNextKey entry is always inited on ParentTreeHandler initialization
        return getPdfObject().getAsNumber(PdfName.ParentTreeNextKey).intValue();
    }

    public int getNextMcidForPage(PdfPage page) {
        return getParentTreeHandler().getNextMcidForPage(page);
    }

    public PdfDocument getDocument() {
        return document;
    }

    /**
     * Adds file associated with structure tree root and identifies the relationship between them.
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the structure tree root dictionary.
     * If description is provided, it also will add file description to catalog Names tree.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param description the file description
     * @param fs          file specification dictionary of associated file
     */
    public void addAssociatedFile(String description, PdfFileSpec fs) {
        if (null == ((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfStructTreeRoot.class);
            logger.error(IoLogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }
        if (null != description) {
            getDocument().getCatalog().getNameTree(PdfName.EmbeddedFiles).addEntry(description, fs.getPdfObject());
        }
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray();
            getPdfObject().put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
    }

    /**
     * <p>
     * Adds file associated with structure tree root and identifies the relationship between them.
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the structure tree root dictionary.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param fs file specification dictionary of associated file
     */
    public void addAssociatedFile(PdfFileSpec fs) {
        addAssociatedFile(null, fs);
    }

    /**
     * Returns files associated with structure tree root.
     *
     * @param create defines whether AF arrays will be created if it doesn't exist
     * @return associated files array
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            getPdfObject().put(PdfName.AF, afArray);
        }
        return afArray;
    }

    /**
     * Returns the document's structure element ID tree wrapped in a {@link PdfStructIdTree}
     * object. If no such tree exists, it is initialized. The initialization happens lazily,
     * and does not trigger any PDF object changes unless populated.
     *
     * @return the {@link PdfStructIdTree} of the document
     */
    public PdfStructIdTree getIdTree() {
        if(this.idTree == null) {
            // Attempt to parse the ID tree in the document if there is one
            PdfDictionary idTreeDict = this.getPdfObject().getAsDictionary(PdfName.IDTree);
            if (idTreeDict == null) {
                // No tree found -> initialise one
                // Don't call setModified() here, registering the first ID will
                // take care of that for us.
                // The ID tree will be registered at flush time.
                this.idTree = new PdfStructIdTree(document);
            } else {
                this.idTree = PdfStructIdTree.readFromDictionary(document, idTreeDict);
            }
        }
        return this.idTree;
    }

    ParentTreeHandler getParentTreeHandler() {
        return parentTreeHandler;
    }

    void addKidObject(int index, PdfDictionary structElem) {
        if (index == -1) {
            getKidsObject().add(structElem);
        } else {
            getKidsObject().add(index, structElem);
        }
        if (PdfStructElem.isStructElem(structElem)) {
            if (getPdfObject().getIndirectReference() == null) {
                throw new PdfException(
                        KernelExceptionMessageConstant.STRUCTURE_ELEMENT_DICTIONARY_SHALL_BE_AN_INDIRECT_OBJECT_IN_ORDER_TO_HAVE_CHILDREN);
            }
            structElem.put(PdfName.P, getPdfObject());
        }
        setModified();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private void flushAllKids(IStructureNode elem) {
        for (IStructureNode kid : elem.getKids()) {
            if (kid instanceof PdfStructElem && !((PdfStructElem) kid).isFlushed()) {
                flushAllKids(kid);
                ((PdfStructElem) kid).flush();
            }
        }
    }

    private void ifKidIsStructElementAddToList(PdfObject kid, List<IStructureNode> kids) {
        if (kid.isFlushed()) {
            kids.add(null);
        } else if (kid.isDictionary() && PdfStructElem.isStructElem((PdfDictionary) kid)) {
            kids.add(new PdfStructElem((PdfDictionary) kid));
        }
    }
}
