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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
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

public class PdfStructTreeRoot extends PdfObjectWrapper<PdfDictionary> implements IStructureNode {

    private static final long serialVersionUID = 2168384302241193868L;

    private PdfDocument document;
    private ParentTreeHandler parentTreeHandler;

    private static Map<String, PdfName> staticRoleNames = new ConcurrentHashMap<>();

    public PdfStructTreeRoot(PdfDocument document) {
        this((PdfDictionary) new PdfDictionary().makeIndirect(document), document);
        getPdfObject().put(PdfName.Type, PdfName.StructTreeRoot);
    }

    public PdfStructTreeRoot(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject);
        this.document = document;
        if (this.document == null) {
            ensureObjectIsAddedToDocument(pdfObject);
            this.document = pdfObject.getIndirectReference().getDocument();
        }
        setForbidRelease();
        parentTreeHandler = new ParentTreeHandler(this);
        getRoleMap(); // TODO may be remove?
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
            logger.warn(MessageFormat.format(LogMessageConstant.MAPPING_IN_STRUCT_ROOT_OVERWRITTEN, fromRole, prevVal, toRole));
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
     */
    public Collection<PdfMcr> getPageMarkedContentReferences(PdfPage page) {
        Map<Integer, PdfMcr> pageMcrs = getParentTreeHandler().getPageMarkedContentReferences(page);
        return pageMcrs != null ? Collections.unmodifiableCollection(pageMcrs.values()) : null;
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
                throw new PdfException(MessageFormatUtil.format(PdfException.CannotMovePagesInPartlyFlushedDocument, i));
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
            logger.error(LogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
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
     * @param create iText will create AF array if it doesn't exist and create value is true
     * @return associated files array.
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            getPdfObject().put(PdfName.AF, afArray);
        }
        return afArray;
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
                throw new PdfException(PdfException.StructureElementDictionaryShallBeAnIndirectObjectInOrderToHaveChildren);
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
