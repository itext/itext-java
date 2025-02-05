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
