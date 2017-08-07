/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfObjRef;
import com.itextpdf.kernel.pdf.tagging.PdfStructElement;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code TagStructureContext} class is used to track necessary information of document's tag structure.
 * It is also used to make some global modifications of the tag tree like removing or flushing page tags, however
 * these two methods and also others are called automatically and are for the most part for internal usage.
 * <br/><br/>
 * There shall be only one instance of this class per {@code PdfDocument}. To obtain instance of this class use
 * {@link PdfDocument#getTagStructureContext()}.
 */
public class TagStructureContext {

    private static final Set<PdfName> allowedRootTagRoles = new HashSet<PdfName>();

    static {
        allowedRootTagRoles.add(PdfName.Document);
        allowedRootTagRoles.add(PdfName.Part);
        allowedRootTagRoles.add(PdfName.Art);
        allowedRootTagRoles.add(PdfName.Sect);
        allowedRootTagRoles.add(PdfName.Div);
    }

    private PdfDocument document;
    private PdfStructElement rootTagElement;
    protected TagTreePointer autoTaggingPointer;
    private PdfVersion tagStructureTargetVersion;
    private boolean forbidUnknownRoles;

    private WaitingTagsManager waitingTagsManager;

    private Set<PdfDictionary> namespaces;
    private Map<String, PdfNamespace> nameToNamespace;
    private PdfNamespace documentDefaultNamespace;

    /**
     * Do not use this constructor, instead use {@link PdfDocument#getTagStructureContext()}
     * method.
     * <br/><br/>
     * Creates {@code TagStructureContext} for document. There shall be only one instance of this
     * class per {@code PdfDocument}.
     * @param document the document which tag structure will be manipulated with this class.
     */
    public TagStructureContext(PdfDocument document) {
        this(document, document.getPdfVersion());
    }

    /**
     * Do not use this constructor, instead use {@link PdfDocument#getTagStructureContext()}
     * method.
     * <br/><br/>
     * Creates {@code TagStructureContext} for document. There shall be only one instance of this
     * class per {@code PdfDocument}.
     * @param document the document which tag structure will be manipulated with this class.
     * @param tagStructureTargetVersion the version of the pdf standard to which the tag structure shall adhere.
     */
    public TagStructureContext(PdfDocument document, PdfVersion tagStructureTargetVersion) {
        this.document = document;
        if (!document.isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        waitingTagsManager = new WaitingTagsManager();
        namespaces = new LinkedHashSet<>();
        nameToNamespace = new HashMap<>();

        this.tagStructureTargetVersion = tagStructureTargetVersion;
        forbidUnknownRoles = true;

        if (targetTagStructureVersionIs2()) {
            initRegisteredNamespaces();
            setNamespaceForNewTagsBasedOnExistingRoot();
        }
    }

    /**
     * If forbidUnknownRoles is set to true, then if you would try to add new tag which has not a standard role and
     * it's role is not mapped through RoleMap, an exception will be raised.
     * Default value - true.
     * @param forbidUnknownRoles new value of the flag
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext setForbidUnknownRoles(boolean forbidUnknownRoles) {
        this.forbidUnknownRoles = forbidUnknownRoles;
        return this;
    }

    public PdfVersion getTagStructureTargetVersion() {
        return tagStructureTargetVersion;
    }

    /**
     * All document auto tagging logic uses {@link TagTreePointer} returned by this method to manipulate tag structure.
     * Typically it points at the root tag. This pointer also could be used to tweak auto tagging process
     * (e.g. move this pointer to the Sect tag, which would result in placing all automatically tagged content
     * under Sect tag).
     * @return the {@code TagTreePointer} which is used for all auto tagging of the document.
     */
    public TagTreePointer getAutoTaggingPointer() {
        if (autoTaggingPointer == null) {
            autoTaggingPointer = new TagTreePointer(document);
        }
        return autoTaggingPointer;
    }

    /**
     * Gets {@link WaitingTagsManager} for the current document. It allows to mark tags as waiting,
     * which would indicate that they are incomplete and are not ready to be flushed.
     * @return document's {@link WaitingTagsManager} class instance.
     */
    public WaitingTagsManager getWaitingTagsManager() {
        return waitingTagsManager;
    }

    /**
     * A namespace that is used as a default value for the tagging for any new {@link TagTreePointer} created
     * (including the pointer returned by {@link #getAutoTaggingPointer()}, which implies that automatically
     * created tag structure will be in this namespace by default).
     * <p>
     * By default, this value is defined based on the PDF document version and the existing tag structure inside
     * a document. For the new empty PDF 2.0 documents this namespace is set to {@link StandardStructureNamespace#PDF_2_0}.
     * </p>
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @return a {@link PdfNamespace} which is used as a default value for the document tagging.
     */
    public PdfNamespace getDocumentDefaultNamespace() {
        return documentDefaultNamespace;
    }

    /**
     * Sets a namespace that will be used as a default value for the tagging for any new {@link TagTreePointer} created.
     * See {@link #getDocumentDefaultNamespace()} for more info.
     * <p>
     * Be careful when changing this property value. It is most recommended to do it right after the {@link PdfDocument} was
     * created, before any content was added. Changing this value after any content was added might result in the mingled
     * tag structure from the namespaces point of view. So in order to maintain the document consistent but in the namespace
     * different from default, set this value before any modifications to the document were made and before
     * {@link #getAutoTaggingPointer()} method was called for the first time.
     * </p>
     * <p>This value has meaning only for the PDF documents of version <b>2.0 and higher</b>.</p>
     * @param namespace a {@link PdfNamespace} which is to be used as a default value for the document tagging.
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext setDocumentDefaultNamespace(PdfNamespace namespace) {
        this.documentDefaultNamespace = namespace;
        return this;
    }

    /**
     * This method defines a recommended way to obtain {@link PdfNamespace} class instances.
     * <p>
     * Returns either a wrapper over an already existing namespace dictionary in the document or over a new one
     * if such namespace wasn't encountered before. Calling this method is considered as encountering a namespace,
     * i.e. two sequential calls on this method will return the same namespace instance (which is not true in general case
     * of two method calls, for instance if several namespace instances with the same name are created via
     * {@link PdfNamespace} constructors and set to the elements of the tag structure, then the last encountered one
     * will be returned by this method). However encountered namespaces will not be added to the document's structure tree root
     * {@link PdfName#Namespaces /Namespaces} array unless they were set to the certain element of the tag structure.
     * </p>
     * @param namespaceName a {@link String} defining the namespace name (conventionally a uniform resource identifier, or URI).
     * @return {@link PdfNamespace) wrapper over either already existing namespace object or over the new one.
     */
    public PdfNamespace fetchNamespace(String namespaceName) {
        PdfNamespace ns = nameToNamespace.get(namespaceName);
        if (ns == null) {
            ns = new PdfNamespace(namespaceName);
            nameToNamespace.put(namespaceName, ns);
        }

        return ns;
    }

    /**
     * Gets an instance of the {@link IRoleMappingResolver} corresponding to the current tag structure target version.
     * This method implies that role is in the default standard structure namespace.
     * @param role a role in the default standard structure namespace which mapping is to be resolved.
     * @return a {@link IRoleMappingResolver} instance, with the giving role as current.
     */
    public IRoleMappingResolver getRoleMappingResolver(PdfName role) {
        return getRoleMappingResolver(role, null);
    }

    /**
     * Gets an instance of the {@link IRoleMappingResolver} corresponding to the current tag structure target version.
     * @param role a role in the given namespace which mapping is to be resolved.
     * @param namespace a {@link PdfNamespace} which this role belongs to.
     * @return a {@link IRoleMappingResolver} instance, with the giving role in the given {@link PdfNamespace} as current.
     */
    public IRoleMappingResolver getRoleMappingResolver(PdfName role, PdfNamespace namespace) {
        if (targetTagStructureVersionIs2()) {
            return new RoleMappingResolverPdf2(role, namespace, getDocument());
        } else {
            return new RoleMappingResolver(role, getDocument());
        }
    }

    /**
     * Checks if the given role and namespace are specified to be obligatory mapped to the standard structure namespace
     * in order to be a valid role in the Tagged PDF.
     * @param role a role in the given namespace which mapping necessity is to be checked.
     * @param namespace a {@link PdfNamespace} which this role belongs to, null value refers to the default standard
     *                  structure namespace.
     * @return true, if the given role in the given namespace is either mapped to the standard structure role or doesn't
     * have to; otherwise false.
     */
    public boolean checkIfRoleShallBeMappedToStandardRole(PdfName role, PdfNamespace namespace) {
        return resolveMappingToStandardOrDomainSpecificRole(role, namespace) != null;
    }

    /**
     * Gets an instance of the {@link IRoleMappingResolver} which is already in the "resolved" state: it returns
     * role in the standard or domain-specific namespace for the {@link IRoleMappingResolver#getRole()} and {@link IRoleMappingResolver#getNamespace()}
     * methods calls which correspond to the mapping of the given role; or null if the given role is not mapped to the standard or domain-specific one.
     * @param role a role in the given namespace which mapping is to be resolved.
     * @param namespace a {@link PdfNamespace} which this role belongs to.
     * @return an instance of the {@link IRoleMappingResolver} which returns false
     * for the {@link IRoleMappingResolver#currentRoleShallBeMappedToStandard()} method call; if mapping cannot be resolved
     * to this state, this method returns null, which means that the given role
     * in the specified namespace is not mapped to the standard role in the standard namespace.
     */
    public IRoleMappingResolver resolveMappingToStandardOrDomainSpecificRole(PdfName role, PdfNamespace namespace) {
        IRoleMappingResolver mappingResolver = getRoleMappingResolver(role, namespace);
        mappingResolver.resolveNextMapping();
        int i = 0;
        // reasonably large arbitrary number that will help to avoid a possible infinite loop
        int maxIters = 100;
        while (mappingResolver.currentRoleShallBeMappedToStandard()) {
            if (++i > maxIters) {
                Logger logger = LoggerFactory.getLogger(TagStructureContext.class);
                logger.error(composeTooMuchTransitiveMappingsException(role, namespace));
                return null;
            }
            if (!mappingResolver.resolveNextMapping()) {
                return null;
            }
        }
        return mappingResolver;
    }

    /**
     * <p>NOTE: this method has been deprecated, use {@link WaitingTagsManager} class functionality instead
     * (can be obtained via {@link TagStructureContext#getWaitingTagsManager()}).</p>
     *
     * Checks if given {@code IAccessibleElement} is connected to some tag.
     * @param element element to check if it has a connected tag.
     * @return true, if there is a tag which retains the connection to the given accessible element.
     * @deprecated Will be removed in iText 7.1. Use {@link WaitingTagsManager}
     * and {@link TagStructureContext#getWaitingTagsManager()} instead.
     */
    @Deprecated
    public boolean isElementConnectedToTag(IAccessibleElement element) {
        return waitingTagsManager.getStructForObj(element) != null;
    }

    /**
     * <p>NOTE: this method has been deprecated, use {@link WaitingTagsManager} class functionality instead
     * (can be obtained via {@link TagStructureContext#getWaitingTagsManager()}).</p>
     *
     * Destroys the connection between the given accessible element and the tag to which this element is connected to.
     * @param element {@code IAccessibleElement} which connection to the tag (if there is one) will be removed.
     * @return current {@link TagStructureContext} instance.
     * @deprecated Will be removed in iText 7.1. Use {@link WaitingTagsManager}
     * and {@link TagStructureContext#getWaitingTagsManager()} instead.
     */
    @Deprecated
    public TagStructureContext removeElementConnectionToTag(IAccessibleElement element) {
        waitingTagsManager.removeWaitingState(element);
        return this;
    }

    /**
     * Removes annotation content item from the tag structure.
     * If annotation is not added to the document or is not tagged, nothing will happen.
     * @return {@link TagTreePointer} instance which points at annotation tag parent if annotation was removed,
     * otherwise returns null.
     */
    public TagTreePointer removeAnnotationTag(PdfAnnotation annotation) {
        PdfStructElement structElem = null;
        PdfDictionary annotDic = annotation.getPdfObject();

        PdfNumber structParentIndex = (PdfNumber) annotDic.get(PdfName.StructParent);
        if (structParentIndex != null) {
            PdfObjRef objRef = document.getStructTreeRoot().findObjRefByStructParentIndex(annotDic.getAsDictionary(PdfName.P), structParentIndex.intValue());

            if (objRef != null) {
                PdfStructElement parent = (PdfStructElement) objRef.getParent();
                parent.removeKid(objRef);
                structElem = parent;
            }
        }
        annotDic.remove(PdfName.StructParent);
        annotDic.setModified();

        if (structElem != null) {
            return new TagTreePointer(document).setCurrentStructElem(structElem);
        }
        return null;
    }

    /**
     * Removes content item from the tag structure.
     * <br/>
     * Nothing happens if there is no such mcid on given page.
     * @param page page, which contains this content item
     * @param mcid marked content id of this content item
     * @return {@code TagTreePointer} which points at the parent of the removed content item, or null if there is no
     * such mcid on given page.
     */
    public TagTreePointer removeContentItem(PdfPage page, int mcid) {
        PdfMcr mcr = document.getStructTreeRoot().findMcrByMcid(page.getPdfObject(), mcid);
        if (mcr == null) {
            return null;
        }

        PdfStructElement parent = (PdfStructElement) mcr.getParent();
        parent.removeKid(mcr);
        return new TagTreePointer(document).setCurrentStructElem(parent);
    }

    /**
     * Removes all tags that belong only to this page. The logic which defines if tag belongs to the page is described
     * at {@link #flushPageTags(PdfPage)}.
     * @param page page that defines which tags are to be removed
     * @return current {@link TagStructureContext} instance.
     */
    public TagStructureContext removePageTags(PdfPage page) {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        Collection<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            // We create a copy here, because pageMcrs is backed by the internal collection which is changed when mcrs are removed.
            List<PdfMcr> mcrsList = new ArrayList<>(pageMcrs);
            for (PdfMcr mcr : mcrsList) {
                removePageTagFromParent(mcr, mcr.getParent());
            }
        }
        return this;
    }

    /**
     * <p>NOTE: this method has been deprecated, use {@link WaitingTagsManager} class functionality instead
     * (can be obtained via {@link TagStructureContext#getWaitingTagsManager()}).</p>
     *
     * Sets the tag, which is connected with the given accessible element, as a current tag for the given
     * {@link TagTreePointer}. An exception will be thrown, if given accessible element is not connected to any tag.
     * @param element an element which has a connection with some tag.
     * @param tagPointer {@link TagTreePointer} which will be moved to the tag connected to the given accessible element.
     * @return current {@link TagStructureContext} instance.
     * @deprecated Will be removed in iText 7.1. Use {@link WaitingTagsManager}
     * and {@link TagStructureContext#getWaitingTagsManager()} instead.
     */
    @Deprecated
    public TagStructureContext moveTagPointerToTag(IAccessibleElement element, TagTreePointer tagPointer) {
        if (!waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, element)) {
            throw new PdfException(PdfException.GivenAccessibleElementIsNotConnectedToAnyTag);
        }
        return this;
    }

    /**
     * <p>NOTE: this method has been deprecated, use {@link WaitingTagsManager} class functionality instead
     * (can be obtained via {@link TagStructureContext#getWaitingTagsManager()}).</p>
     *
     * Destroys all the retained connections.
     * @return current {@link TagStructureContext} instance.
     * @deprecated Will be removed in iText 7.1. Use {@link WaitingTagsManager}
     * and {@link TagStructureContext#getWaitingTagsManager()} instead.
     */
    @Deprecated
    public TagStructureContext removeAllConnectionsToTags() {
        waitingTagsManager.removeAllWaitingStates();
        return this;
    }

    /**
     * Flushes the tags which are considered to belong to the given page.
     * The logic that defines if the given tag (structure element) belongs to the page is the following:
     * if all the marked content references (dictionary or number references), that are the
     * descenders of the given structure element, belong to the current page - the tag is considered
     * to belong to the page. If tag has descenders from several pages - it is flushed, if all other pages except the
     * current one are flushed.
     *
     * <br><br>
     * If some of the page's tags have waiting state (see {@link WaitingTagsManager} these tags are considered
     * as not yet finished ones, and they and their children won't be flushed.
     * @param page a page which tags will be flushed.
     */
    public TagStructureContext flushPageTags(PdfPage page) {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        Collection<PdfMcr> pageMcrs = structTreeRoot.getPageMarkedContentReferences(page);
        if (pageMcrs != null) {
            for (PdfMcr mcr : pageMcrs) {
                PdfStructElement parent = (PdfStructElement) mcr.getParent();
                flushParentIfBelongsToPage(parent, page);
            }
        }

        return this;
    }

    /**
     * Transforms root tags in a way that complies with the tagged PDF specification.
     * Depending on PDF version behaviour may differ.
     * <p>
     * ISO 32000-1 (PDF 1.7 and lower)
     * 14.8.4.2 Grouping Elements
     * </p>
     * <p>
     * "In a tagged PDF document, the structure tree shall contain a single top-level element; that is,
     * the structure tree root (identified by the StructTreeRoot entry in the document catalogue) shall
     * have only one child in its K (kids) array. If the PDF file contains a complete document, the structure
     * type Document should be used for this top-level element in the logical structure hierarchy. If the file
     * contains a well-formed document fragment, one of the structure types Part, Art, Sect, or Div may be used instead."
     * </p>
     * <p>
     * For PDF 2.0 and higher root tag is allowed to have only the Document role.
     * </p>
     */
    public void normalizeDocumentRootTag() {
        // in this method we could deal with existing document, so we don't won't to throw exceptions here
        boolean forbid = forbidUnknownRoles;
        forbidUnknownRoles = false;

        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();
        IRoleMappingResolver mapping = null;
        if (rootKids.size() > 0) {
            PdfStructElement firstKid = (PdfStructElement) rootKids.get(0);
            mapping = resolveMappingToStandardOrDomainSpecificRole(firstKid.getRole(), firstKid.getNamespace());
        }

        if (rootKids.size() == 1
                && mapping != null && mapping.currentRoleIsStandard()
                && isRoleAllowedToBeRoot(mapping.getRole())) {
            rootTagElement = (PdfStructElement) rootKids.get(0);
        } else {
            document.getStructTreeRoot().getPdfObject().remove(PdfName.K);
            rootTagElement = new RootTagNormalizer(this, rootTagElement, document).makeSingleStandardRootTag(rootKids);
        }
        forbidUnknownRoles = forbid;
    }

    /**
     * A utility method that prepares the current instance of the {@link TagStructureContext} for
     * the closing of document. Essentially it flushes all the "hanging" information to the document.
     */
    public void prepareToDocumentClosing() {
        waitingTagsManager.removeAllWaitingStates();
        actualizeNamespacesInStructTreeRoot();
    }

    /**
     * Method for internal usages.
     * Essentially, all it does is just making sure that for connected tags the properties are
     * up to date with the connected accessible elements properties.
     * @deprecated This method will be removed in iText 7.1. It's needless to call this method,
     * because properties are always up to date.
     */
    @Deprecated
    public void actualizeTagsProperties() {
        // nothing is needed to be done, properties are always up to date
    }

    /**
     * <p>
     * Gets {@link PdfStructElement} at which {@link TagTreePointer} points.
     * </p>
     * NOTE: Be aware that {@link PdfStructElement} is a low level class, use it carefully,
     * especially in conjunction with high level {@link TagTreePointer} and {@link TagStructureContext} classes.
     * @param pointer a {@link TagTreePointer} which points at desired {@link PdfStructElement}.
     * @return a {@link PdfStructElement} at which given {@link TagTreePointer} points.
     */
    public PdfStructElement getPointerStructElem(TagTreePointer pointer) {
        return pointer.getCurrentStructElem();
    }

    /**
     * Creates a new {@link TagTreePointer} which points at given {@link PdfStructElement}.
     * @param structElem a {@link PdfStructElement} for which {@link TagTreePointer} will be created.
     * @return a new {@link TagTreePointer}.
     */
    public TagTreePointer createPointerForStructElem(PdfStructElement structElem) {
        return new TagTreePointer(structElem, document);
    }

    PdfStructElement getRootTag() {
        if (rootTagElement == null) {
            normalizeDocumentRootTag();
        }
        return rootTagElement;
    }
    PdfDocument getDocument() {
        return document;
    }

    void ensureNamespaceRegistered(PdfNamespace namespace) {
        if (namespace != null) {
            PdfDictionary namespaceObj = namespace.getPdfObject();
            if (!namespaces.contains(namespaceObj)) {
                namespaces.add(namespaceObj);
            }
            nameToNamespace.put(namespace.getNamespaceName(), namespace);
        }
    }

    void throwExceptionIfRoleIsInvalid(IAccessibleElement element, PdfNamespace pointerCurrentNamespace) {
        AccessibilityProperties properties = element.getAccessibilityProperties();
        PdfNamespace namespace = properties != null ? properties.getNamespace() : null;
        if (namespace == null) {
            namespace = pointerCurrentNamespace;
        }
        throwExceptionIfRoleIsInvalid(element.getRole(), namespace);
    }

    void throwExceptionIfRoleIsInvalid(PdfName role, PdfNamespace namespace) {
        if (!checkIfRoleShallBeMappedToStandardRole(role, namespace)) {
            String exMessage = composeInvalidRoleException(role, namespace);
            if (forbidUnknownRoles) {
                throw new PdfException(exMessage);
            } else {
                Logger logger = LoggerFactory.getLogger(TagStructureContext.class);
                logger.warn(exMessage);
            }
        }
    }

    boolean targetTagStructureVersionIs2() {
        return PdfVersion.PDF_2_0.compareTo(tagStructureTargetVersion) <= 0;
    }

    private boolean isRoleAllowedToBeRoot(PdfName role) {
        if (targetTagStructureVersionIs2()) {
            return PdfName.Document.equals(role);
        } else {
            return allowedRootTagRoles.contains(role);
        }
    }

    private void setNamespaceForNewTagsBasedOnExistingRoot() {
        List<IPdfStructElem> rootKids = document.getStructTreeRoot().getKids();
        if (rootKids.size() > 0) {
            PdfStructElement firstKid = (PdfStructElement) rootKids.get(0);
            IRoleMappingResolver resolvedMapping = resolveMappingToStandardOrDomainSpecificRole(firstKid.getRole(), firstKid.getNamespace());
            if (resolvedMapping == null || !resolvedMapping.currentRoleIsStandard()) {

                Logger logger = LoggerFactory.getLogger(TagStructureContext.class);
                String nsStr;
                if (firstKid.getNamespace() != null) {
                    nsStr = firstKid.getNamespace().getNamespaceName();
                } else {
                    nsStr = StandardStructureNamespace.getDefault();
                }
                logger.warn(MessageFormat.format(LogMessageConstant.EXISTING_TAG_STRUCTURE_ROOT_IS_NOT_STANDARD, firstKid.getRole().getValue(), nsStr));
            }
            if (resolvedMapping == null || !StandardStructureNamespace.PDF_1_7.equals(resolvedMapping.getNamespace().getNamespaceName())) {
                documentDefaultNamespace = fetchNamespace(StandardStructureNamespace.PDF_2_0);
            }
        } else {
            documentDefaultNamespace = fetchNamespace(StandardStructureNamespace.PDF_2_0);
        }
    }

    private String composeInvalidRoleException(PdfName role, PdfNamespace namespace) {
        return composeExceptionBasedOnNamespacePresence(role.toString(), namespace,
                PdfException.RoleIsNotMappedToAnyStandardRole, PdfException.RoleInNamespaceIsNotMappedToAnyStandardRole);
    }

    private String composeTooMuchTransitiveMappingsException(PdfName role, PdfNamespace namespace) {
        return composeExceptionBasedOnNamespacePresence(role.toString(), namespace,
                LogMessageConstant.CANNOT_RESOLVE_ROLE_TOO_MUCH_TRANSITIVE_MAPPINGS,
                LogMessageConstant.CANNOT_RESOLVE_ROLE_IN_NAMESPACE_TOO_MUCH_TRANSITIVE_MAPPINGS);
    }

    private void initRegisteredNamespaces() {
        PdfStructTreeRoot structTreeRoot = document.getStructTreeRoot();
        for (PdfNamespace namespace : structTreeRoot.getNamespaces()) {
            namespaces.add(namespace.getPdfObject());
            nameToNamespace.put(namespace.getNamespaceName(), namespace);
        }
    }

    private void actualizeNamespacesInStructTreeRoot() {
        if (namespaces.size() > 0) {
            PdfStructTreeRoot structTreeRoot = getDocument().getStructTreeRoot();
            PdfArray rootNamespaces = structTreeRoot.getNamespacesObject();
            Set<PdfDictionary> newNamespaces = new LinkedHashSet<>(namespaces);
            for (int i = 0; i < rootNamespaces.size(); ++i) {
                newNamespaces.remove(rootNamespaces.getAsDictionary(i));
            }
            for (PdfDictionary newNs : newNamespaces) {
                rootNamespaces.add(newNs);
            }
            if (!newNamespaces.isEmpty()) {
                structTreeRoot.setModified();
            }
        }
    }

    private void removePageTagFromParent(IPdfStructElem pageTag, IPdfStructElem parent) {
        if (parent instanceof PdfStructElement) {
            PdfStructElement structParent = (PdfStructElement) parent;
            if (!structParent.isFlushed()) {
                structParent.removeKid(pageTag);
                PdfDictionary parentStructDict = structParent.getPdfObject();
                if (waitingTagsManager.getObjForStructDict(parentStructDict) == null && parent.getKids().size() == 0
                        && parentStructDict != getRootTag().getPdfObject()) {
                    removePageTagFromParent(structParent, parent.getParent());
                    PdfIndirectReference indRef = parentStructDict.getIndirectReference();
                    if (indRef != null) {
                        // TODO how about possible references to structure element from refs or structure destination for instance?
                        indRef.setFree();
                    }
                }
            } else {
                if (pageTag instanceof PdfMcr) {
                    throw new PdfException(PdfException.CannotRemoveTagBecauseItsParentIsFlushed);
                }
            }
        } else {
            // it is StructTreeRoot
            // should never happen as we always should have only one root tag and we don't remove it
        }
    }

    private void flushParentIfBelongsToPage(PdfStructElement parent, PdfPage currentPage) {
        if (parent.isFlushed() || waitingTagsManager.getObjForStructDict(parent.getPdfObject()) != null
                || parent.getPdfObject() == getRootTag().getPdfObject()) {
            return;
        }

        List<IPdfStructElem> kids = parent.getKids();
        boolean allKidsBelongToPage = true;
        for (IPdfStructElem kid : kids) {
            if (kid instanceof PdfMcr) {
                PdfDictionary kidPage = ((PdfMcr) kid).getPageObject();
                if (!kidPage.isFlushed() && !kidPage.equals(currentPage.getPdfObject())) {
                    allKidsBelongToPage = false;
                    break;
                }
            } else if (kid instanceof PdfStructElement) {
                // If kid is structElem and was already flushed then in kids list there will be null for it instead of
                // PdfStructElement. And therefore if we get into this if-clause it means that some StructElem wasn't flushed.
                allKidsBelongToPage = false;
                break;
            }
        }

        if (allKidsBelongToPage) {
            IPdfStructElem parentsParent = parent.getParent();
            parent.flush();
            if (parentsParent instanceof PdfStructElement) {
                flushParentIfBelongsToPage((PdfStructElement)parentsParent, currentPage);
            }
        }

        return;
    }

    private String composeExceptionBasedOnNamespacePresence(String role, PdfNamespace namespace, String withoutNsEx, String withNsEx) {
        if (namespace == null) {
            return MessageFormat.format(withoutNsEx, role);
        } else {
            String nsName = namespace.getNamespaceName();
            PdfIndirectReference ref = namespace.getPdfObject().getIndirectReference();
            if (ref != null) {
                nsName = nsName + " (" +
                        Integer.toString(ref.getObjNumber()) + " " + Integer.toString(ref.getGenNumber()) +
                        " obj)";
            }
            return MessageFormat.format(withNsEx, role, nsName);
        }
    }
}
