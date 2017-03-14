package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;
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

    PdfStructElem makeSingleStandardRootTag(List<IPdfStructElem> rootKids) {
        if (rootTagElement == null) {
            createNewRootTag();
        } else {
            document.getStructTreeRoot().addKid(rootTagElement);
            ensureExistingRootTagIsDocument();
        }

        addStructTreeRootKidsToTheRootTag(rootKids);

        return rootTagElement;
    }

    private void createNewRootTag() {
        IRoleMappingResolver mapping;
        PdfNamespace docDefaultNs = context.getDocumentDefaultNamespace();
        mapping = context.resolveMappingToStandardOrDomainSpecificRole(PdfName.Document, docDefaultNs);
        if (mapping == null || mapping.currentRoleIsStandard() && !PdfName.Document.equals(mapping.getRole())) {
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
        mapping = context.getRoleMappingResolver(rootTagElement.getRole(), rootTagElement.getNamespace());
        boolean isDocBeforeResolving = mapping.currentRoleIsStandard() && PdfName.Document.equals(mapping.getRole());

        mapping = context.resolveMappingToStandardOrDomainSpecificRole(rootTagElement.getRole(), rootTagElement.getNamespace());
        boolean isDocAfterResolving = mapping != null && mapping.currentRoleIsStandard() && PdfName.Document.equals(mapping.getRole());

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

    private void addStructTreeRootKidsToTheRootTag(List<IPdfStructElem> rootKids) {
        int originalRootKidsIndex = 0;
        boolean isBeforeOriginalRoot = true;
        for (IPdfStructElem elem : rootKids) {
            // StructTreeRoot kids are always PdfStructElem, so we are save here to cast it
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
                PdfString kidNamespaceName = kid.getNamespace().getNamespaceName();
                kidIsDocument = StandardStructureNamespace.PDF_1_7.equals(kidNamespaceName) || StandardStructureNamespace.PDF_2_0.equals(kidNamespaceName);
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
        TagTreePointer tagPointer = new TagTreePointer(parent);
        tagPointer.addTag(0, wrapTagRole);

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
            if (mapping.getNamespace() != null && !StandardStructureNamespace.PDF_1_7.equals(mapping.getNamespace().getNamespaceName())) {
                mappingRole += " in \"" + mapping.getNamespace().getNamespaceName() + "\" namespace";
            }
        } else {
            mappingRole += "not standard role";
        }

        Logger logger = LoggerFactory.getLogger(RootTagNormalizer.class);
        logger.warn(MessageFormat.format(LogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, origRootTagNs, mappingRole));
    }
}
