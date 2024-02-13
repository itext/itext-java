package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

/**
 * Utility class that contains utility methods used  when working with the TagTreeHandler
 */
public final class TagTreeHandlerUtil {

    private TagTreeHandlerUtil() {
        //Empty constructor.
    }

    /**
     * Gets the {@link PdfStructElem} if the element matches the provided role and the structureNode is indeed an
     * {@link PdfStructElem}
     *
     * @param role          The role that needs to be matched.
     * @param structureNode The structure node.
     *
     * @return The {@link PdfStructElem}  if the structure matches the role.
     */
    public static PdfStructElem getElementIfRoleMatches(PdfName role, IStructureNode structureNode) {
        if (structureNode == null) {
            return null;
        }
        if (!(structureNode instanceof PdfStructElem)) {
            return null;
        }
        if (!role.equals(structureNode.getRole())) {
            return null;
        }
        return (PdfStructElem) structureNode;
    }
}
