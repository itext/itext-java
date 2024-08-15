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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;

/**
 * Class that provides methods for searching mcr in tag tree.
 */
public final class McrCheckUtil {

    /**
     * Creates a new {@link McrCheckUtil} instance.
     */
    private McrCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if tag structure of TR element contains any mcr.
     *
     * @param elementTR PdfDictionary of TR element.
     *
     * @return true if mcr found.
     */
    public static boolean isTrContainsMcr(PdfDictionary elementTR) {
        TagTreeIterator tagTreeIterator = new TagTreeIterator(new PdfStructElem(elementTR));
        McrCheckUtil.McrTagHandler handler = new McrCheckUtil.McrTagHandler();
        tagTreeIterator.addHandler(handler);
        tagTreeIterator.traverse();
        return handler.tagTreeHaveMcr();
    }

    /**
     * Search for mcr elements in the TagTree.
     */
    private static class McrTagHandler implements ITagTreeIteratorHandler {

        private boolean haveMcr = false;

        /**
         * Method returns if tag tree has mcr in it.
         */
        public boolean tagTreeHaveMcr() {
            return haveMcr;
        }

        /**
         * Creates a new {@link  McrTagHandler} instance.
         */
        public McrTagHandler() {
            //empty constructor
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean nextElement(IStructureNode elem) {
            if ((elem instanceof PdfMcr)) {
                haveMcr = true;
            }
            return true;
        }
    }
}
