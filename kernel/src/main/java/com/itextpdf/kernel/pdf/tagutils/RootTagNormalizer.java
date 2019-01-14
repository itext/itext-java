/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RootTagNormalizer implements Serializable {

    private static final long serialVersionUID = -4392164598496387910L;

    private TagStructureContext context;
    private PdfStructElem rootTagElement;
    private PdfDocument document;

    RootTagNormalizer(TagStructureContext context, PdfStructElem rootTagElement, PdfDocument document) {
        this.context = context;
        this.rootTagElement = rootTagElement;
        this.document = document;
    }

    PdfStructElem makeSingleStandardRootTag(List<IStructureNode> rootKids) {
        document.getStructTreeRoot().makeIndirect(document);
        if (rootTagElement == null) {
            createNewRootTag();
        } else {
            rootTagElement.makeIndirect(document);
            document.getStructTreeRoot().addKid(rootTagElement);
            ensureExistingRootTagIsDocument();
        }

        addStructTreeRootKidsToTheRootTag(rootKids);

        return rootTagElement;
    }

    private void createNewRootTag() {
        IRoleMappingResolver mapping;
        PdfNamespace docDefaultNs = context.getDocumentDefaultNamespace();
        mapping = context.resolveMappingToStandardOrDomainSpecificRole(StandardRoles.DOCUMENT, docDefaultNs);
        if (mapping == null || mapping.currentRoleIsStandard() && !StandardRoles.DOCUMENT.equals(mapping.getRole())) {
            logCreatedRootTagHasMappingIssue(docDefaultNs, mapping);
        }
        rootTagElement = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));
        if (context.targetTagStructureVersionIs2()) {
            rootTagElement.setNamespace(docDefaultNs);
            context.ensureNamespaceRegistered(docDefaultNs);
        }
    }

    private void ensureExistingRootTagIsDocument() {
        IRoleMappingResolver mapping;
        mapping = context.getRoleMappingResolver(rootTagElement.getRole().getValue(), rootTagElement.getNamespace());
        boolean isDocBeforeResolving = mapping.currentRoleIsStandard() && StandardRoles.DOCUMENT.equals(mapping.getRole());

        mapping = context.resolveMappingToStandardOrDomainSpecificRole(rootTagElement.getRole().getValue(), rootTagElement.getNamespace());
        boolean isDocAfterResolving = mapping != null && mapping.currentRoleIsStandard() && StandardRoles.DOCUMENT.equals(mapping.getRole());

        if (isDocBeforeResolving && !isDocAfterResolving) {
            logCreatedRootTagHasMappingIssue(rootTagElement.getNamespace(), mapping);
        } else if (!isDocAfterResolving) {
            wrapAllKidsInTag(rootTagElement, rootTagElement.getRole(), rootTagElement.getNamespace());
            rootTagElement.setRole(PdfName.Document);
            if (context.targetTagStructureVersionIs2()) {
                rootTagElement.setNamespace(context.getDocumentDefaultNamespace());
                context.ensureNamespaceRegistered(context.getDocumentDefaultNamespace());
            }
        }
    }

    private void addStructTreeRootKidsToTheRootTag(List<IStructureNode> rootKids) {
        int originalRootKidsIndex = 0;
        boolean isBeforeOriginalRoot = true;
        for (IStructureNode elem : rootKids) {
            // StructTreeRoot kids are always PdfStructElement, so we are save here to cast it
            PdfStructElem kid = (PdfStructElem) elem;
            if (kid.getPdfObject() == rootTagElement.getPdfObject()) {
                isBeforeOriginalRoot = false;
                continue;
            }

            // This boolean is used to "flatten" possible deep "stacking" of the tag structure in case of the multiple pages copying operations.
            // This could happen due to the wrapping of all the kids in the createNewRootTag or ensureExistingRootTagIsDocument methods.
            // And therefore, we don't need here to resolve mappings, because we exactly know which role we set.
            boolean kidIsDocument = PdfName.Document.equals(kid.getRole());
            if (kidIsDocument && kid.getNamespace() != null && context.targetTagStructureVersionIs2()) {
                // we flatten only tags of document role in standard structure namespace
                String kidNamespaceName = kid.getNamespace().getNamespaceName();
                kidIsDocument = StandardNamespaces.PDF_1_7.equals(kidNamespaceName) || StandardNamespaces.PDF_2_0.equals(kidNamespaceName);
            }

            if (isBeforeOriginalRoot) {
                rootTagElement.addKid(originalRootKidsIndex, kid);
                originalRootKidsIndex += kidIsDocument ? kid.getKids().size() : 1;
            } else {
                rootTagElement.addKid(kid);
            }
            if (kidIsDocument) {
                removeOldRoot(kid);
            }
        }
    }

    private void wrapAllKidsInTag(PdfStructElem parent, PdfName wrapTagRole, PdfNamespace wrapTagNs) {
        int kidsNum = parent.getKids().size();
        TagTreePointer tagPointer = new TagTreePointer(parent, document);
        tagPointer.addTag(0, wrapTagRole.getValue());

        if (context.targetTagStructureVersionIs2()) {
            tagPointer.getProperties().setNamespace(wrapTagNs);
        }

        TagTreePointer newParentOfKids = new TagTreePointer(tagPointer);
        tagPointer.moveToParent();
        for (int i = 0; i < kidsNum; ++i) {
            tagPointer.relocateKid(1, newParentOfKids);
        }
    }

    private void removeOldRoot(PdfStructElem oldRoot) {
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer
                .setCurrentStructElem(oldRoot)
                .removeTag();
    }

    private void logCreatedRootTagHasMappingIssue(PdfNamespace rootTagOriginalNs, IRoleMappingResolver mapping) {
        String origRootTagNs = "";
        if (rootTagOriginalNs != null && rootTagOriginalNs.getNamespaceName() != null) {
            origRootTagNs = " in \"" + rootTagOriginalNs.getNamespaceName() + "\" namespace";
        }

        String mappingRole = " to ";
        if (mapping != null) {
            mappingRole += "\"" + mapping.getRole() + "\"";
            if (mapping.getNamespace() != null && !StandardNamespaces.PDF_1_7.equals(mapping.getNamespace().getNamespaceName())) {
                mappingRole += " in \"" + mapping.getNamespace().getNamespaceName() + "\" namespace";
            }
        } else {
            mappingRole += "not standard role";
        }

        Logger logger = LoggerFactory.getLogger(RootTagNormalizer.class);
        logger.warn(MessageFormat.format(LogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, origRootTagNs, mappingRole));
    }
}
