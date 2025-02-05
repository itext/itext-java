/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import java.text.MessageFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RootTagNormalizer {


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
            IRoleMappingResolver mapping = kid.getRole() == null ? null
                    : context.resolveMappingToStandardOrDomainSpecificRole(kid.getRole().getValue(), rootTagElement.getNamespace());
            boolean kidIsDocument = mapping != null && mapping.currentRoleIsStandard() && StandardRoles.DOCUMENT.equals(mapping.getRole());
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
        new TagTreePointer(oldRoot, document).removeTag();
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
        logger.warn(MessageFormat.format(IoLogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, origRootTagNs, mappingRole));
    }
}
