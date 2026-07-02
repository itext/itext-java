/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.renderer.FootnoteAnchorRenderer;
import com.itextpdf.layout.renderer.FootnoteRenderer;

import java.util.Collections;

/**
 * The class is a helper which is used to correctly create structure
 * tree for Footnote elements.
 */
public final class FootnoteTaggingHelper {
    private FootnoteTaggingHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Adjusts the tag roles of the {@link Footnote} element when required by the PdfVersion targeted.
     *
     * @param hintOwner     the {@link FootnoteRenderer} to repair
     * @param taggingHelper the {@link LayoutTaggingHelper} instance to use
     */
    public static void repairFootnoteTagIfNeeded(FootnoteRenderer hintOwner, LayoutTaggingHelper taggingHelper) {
        TaggingHintKey hint = LayoutTaggingHelper.getOrCreateHintKey(hintOwner);
        if (taggingHelper != null && hint.isAccessible()) {
            PdfVersion targetVersion = taggingHelper.getPdfDocument().getTagStructureContext().
                    getTagStructureTargetVersion();
            if (targetVersion.compareTo(PdfVersion.PDF_2_0) >= 0 &&
                    StandardRoles.NOTE.equals(hint.getAccessibleElement().getAccessibilityProperties().getRole())) {
                hint.setOverriddenRole(StandardRoles.FENOTE);
            } else {
                if (hint.getAccessibleElement().getAccessibilityProperties().getStructureElementId() == null) {
                    hint.getAccessibleElement().getAccessibilityProperties().setStructureElementIdString(
                            taggingHelper.createStructureElementId("footnote_"));
                }
            }
        }
    }

    /**
     * Wraps the FootnoteAnchor content element with a dummy element.
     *
     * @param footnoteAnchorContent the FootnoteAnchor content element to wrap.
     *  @param taggingHelper the {@link LayoutTaggingHelper} instance to use
     */
    public static void wrapAnchorInsideFootnoteIntoLbl(IPropertyContainer footnoteAnchorContent,
            LayoutTaggingHelper taggingHelper) {
        if (taggingHelper != null) {
            TaggingHintKey footnoteAnchorContentHint = LayoutTaggingHelper.getHintKey(footnoteAnchorContent);

            TaggingDummyElement lblParentForFootnoteAnchorContent = new TaggingDummyElement(StandardRoles.LBL);
            TaggingHintKey lblHint = LayoutTaggingHelper.getOrCreateHintKey(lblParentForFootnoteAnchorContent);

            taggingHelper.replaceKidHint(footnoteAnchorContentHint, Collections.singletonList(lblHint));
            taggingHelper.addKidsHint(lblHint, Collections.singletonList(footnoteAnchorContentHint));
        }
    }

    /**
     * Adjusts the tag roles when required by the PdfVersion targeted.
     *
     * @param hintOwner     the {@link FootnoteAnchorRenderer} to repair
     * @param taggingHelper the {@link LayoutTaggingHelper} instance to use
     */
    public static void repairFootnoteAnchorTagIfNeeded(FootnoteAnchorRenderer hintOwner,
            LayoutTaggingHelper taggingHelper) {
        TaggingHintKey hint = LayoutTaggingHelper.getOrCreateHintKey(hintOwner);
        if (taggingHelper != null && hint.isAccessible()) {
            PdfVersion targetVersion = taggingHelper.getPdfDocument().getTagStructureContext().
                    getTagStructureTargetVersion();
            if (targetVersion.compareTo(PdfVersion.PDF_2_0) >= 0
                    && StandardRoles.REFERENCE.equals(hint.getAccessibleElement().getAccessibilityProperties().
                    getRole())) {
                hint.setOverriddenRole(StandardRoles.LBL);
            }
        }
    }
}
