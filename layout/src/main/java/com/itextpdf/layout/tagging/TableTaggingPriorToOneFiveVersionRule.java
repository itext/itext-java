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
package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import java.util.HashSet;
import java.util.Set;

class TableTaggingPriorToOneFiveVersionRule implements ITaggingRule {
    private Set<TaggingHintKey> finishForbidden = new HashSet<>();

    @Override
    public boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey taggingHintKey) {
        if (taggingHintKey.getAccessibleElement() != null) {
            String role = taggingHintKey.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.THEAD.equals(role) || StandardRoles.TFOOT.equals(role)) {
                finishForbidden.add(taggingHintKey);
                return false;
            }
        }

        for (TaggingHintKey hint : taggingHelper.getAccessibleKidsHint(taggingHintKey)) {
            String role = hint.getAccessibleElement().getAccessibilityProperties().getRole();
            if (StandardRoles.TBODY.equals(role) || StandardRoles.THEAD.equals(role) || StandardRoles.TFOOT.equals(role)) {
                // THead and TFoot are not finished thanks to this rule logic, TBody not finished because it's dummy and Table itself not finished
                removeTagUnavailableInPriorToOneDotFivePdf(hint, taggingHelper);
            }
        }
        return true;
    }

    private void removeTagUnavailableInPriorToOneDotFivePdf(TaggingHintKey taggingHintKey, LayoutTaggingHelper taggingHelper) {
        taggingHelper.replaceKidHint(taggingHintKey, taggingHelper.getAccessibleKidsHint(taggingHintKey));
        PdfDocument pdfDocument = taggingHelper.getPdfDocument();
        WaitingTagsManager waitingTagsManager = pdfDocument.getTagStructureContext().getWaitingTagsManager();
        TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
        if (waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, taggingHintKey)) {
            waitingTagsManager.removeWaitingState(taggingHintKey);
            tagPointer.removeTag();
        }
        if (finishForbidden.remove(taggingHintKey)) {
            taggingHintKey.setFinished();
        }
    }
}
