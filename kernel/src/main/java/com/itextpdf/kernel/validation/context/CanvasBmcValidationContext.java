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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

import java.util.Stack;

/**
 * Class for canvas begin marked content validation.
 */
public class CanvasBmcValidationContext implements IValidationContext {
    private final Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack;
    private final Tuple2<PdfName, PdfDictionary> currentBmc;

    /**
     * Instantiates a new {@link CanvasBmcValidationContext} based on tag structure stack and current BMC.
     *
     * @param tagStructureStack the tag structure stack
     * @param currentBmc the current BMC
     */
    public CanvasBmcValidationContext(Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack,
            Tuple2<PdfName, PdfDictionary> currentBmc) {
        this.tagStructureStack = tagStructureStack;
        this.currentBmc = currentBmc;
    }

    /**
     * Gets tag structure stack.
     *
     * @return tag structure stack
     */
    public Stack<Tuple2<PdfName, PdfDictionary>> getTagStructureStack() {
        return tagStructureStack;
    }

    /**
     * Gets current BMC.
     *
     * @return the current BMC
     */
    public Tuple2<PdfName, PdfDictionary> getCurrentBmc() {
        return currentBmc;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.CANVAS_BEGIN_MARKED_CONTENT;
    }
}
