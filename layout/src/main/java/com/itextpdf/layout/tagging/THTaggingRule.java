package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;

import java.util.List;

/**
 * Used to automatically add scope attribute to TH cells.
 * <p>
 * This behavior is enabled by default. In the future, we maybe want to expand this with a heuristic
 * which determines the scope based on the position of all the TH cells in the table.
 * <p>
 * If the scope attribute is already present, it will not be modified.
 * If the scope attribute is not present, it will be added with the value "Column".
 * If the scope attribute is present with the value "None", it will be removed.
 */
class THTaggingRule implements ITaggingRule {


    /**
     * Creates a new {@link THTaggingRule} instance.
     */
    THTaggingRule() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey taggingHintKey) {
        if (taggingHintKey.getAccessibilityProperties() == null) {
            throw new IllegalArgumentException("TaggingHintKey should have accessibility properties");
        }
        List<PdfStructureAttributes> attributesList = taggingHintKey.getAccessibilityProperties().getAttributesList();

        for (PdfStructureAttributes attributes : attributesList) {
            PdfName scopeValue = attributes.getPdfObject().getAsName(PdfName.Scope);
            if (scopeValue != null && !PdfName.None.equals(scopeValue)) {
                return true;
            }
            if (PdfName.None.equals(scopeValue)) {
                attributes.removeAttribute(PdfName.Scope.getValue());
                return true;
            }
        }
        if (taggingHintKey.getTagPointer() == null) {
            return true;
        }

        AccessibilityProperties properties = taggingHintKey.getAccessibilityProperties();
        PdfStructureAttributes atr = new PdfStructureAttributes(StandardRoles.TABLE);
        atr.addEnumAttribute(PdfName.Scope.getValue(), PdfName.Column.getValue());
        properties.addAttributes(atr);
        taggingHintKey.getTagPointer().applyProperties(properties);
        return true;
    }
}
