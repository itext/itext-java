package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;

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
            throw new IllegalArgumentException(LayoutExceptionMessageConstant.TAGGING_HINTKEY_SHOULD_HAVE_ACCES);
        }
        final List<PdfStructureAttributes> attributesList = taggingHintKey.getAccessibilityProperties().getAttributesList();

        for (PdfStructureAttributes attributes : attributesList) {
            final PdfName scopeValue = attributes.getPdfObject().getAsName(PdfName.Scope);
            // the scope None is used to build complicated tables where TD cells don't refer to
            // the TH cell in the TD cells column or row
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

        final AccessibilityProperties properties = taggingHintKey.getAccessibilityProperties();
        final PdfStructureAttributes atr = new PdfStructureAttributes(StandardRoles.TABLE);
        atr.addEnumAttribute(PdfName.Scope.getValue(), PdfName.Column.getValue());
        properties.addAttributes(atr);
        taggingHintKey.getTagPointer().applyProperties(properties);
        return true;
    }
}
