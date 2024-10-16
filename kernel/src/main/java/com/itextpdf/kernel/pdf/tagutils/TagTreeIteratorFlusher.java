/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.Set;

/**
 * Class that flushes struct elements while iterating over struct tree root with {@link TagTreeIterator}.
 */
public class TagTreeIteratorFlusher extends AbstractAvoidDuplicatesTagTreeIteratorHandler {

    private Set<PdfDictionary> waitingTags;
    private boolean waitingTagsUsed = false;

    /**
     * Creates a new instance of {@link TagTreeIteratorFlusher}.
     */
    public TagTreeIteratorFlusher() {
        // Empty constructor
    }

    /**
     * Sets waiting tags for {@link TagTreeIteratorFlusher}.
     *
     * @param waitingTags waiting tags to set
     *
     * @return this same {@link TagTreeIteratorFlusher} instance
     */
    public ITagTreeIteratorHandler setWaitingTags(Set<PdfDictionary> waitingTags) {
        this.waitingTags = waitingTags;
        this.waitingTagsUsed = true;
        return this;
    }

    @Override
    public boolean accept(IStructureNode node) {
        if (waitingTagsUsed) {
            return super.accept(node) && node instanceof PdfStructElem &&
                    (waitingTags == null || !waitingTags.contains(((PdfStructElem) node).getPdfObject()));
        }
        return super.accept(node);
    }

    @Override
    public void processElement(IStructureNode elem) {
        if (elem instanceof PdfStructElem && !((PdfStructElem) elem).isFlushed()) {
            ((PdfStructElem) elem).flush();
        }
    }
}
