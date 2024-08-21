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
package com.itextpdf.kernel.validation.context;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

import java.util.Stack;

/**
 * Class for canvas writing content validation.
 */
public class CanvasWritingContentValidationContext implements IValidationContext {
    private final Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack;

    /**
     * Instantiates a new {@link CanvasWritingContentValidationContext} based on tag structure stack.
     *
     * @param tagStructureStack the tag structure stack
     */
    public CanvasWritingContentValidationContext(Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack) {
        this.tagStructureStack = tagStructureStack;
    }

    /**
     * Gets the tag structure stack.
     *
     * @return the tag structure stack
     */
    public Stack<Tuple2<PdfName, PdfDictionary>> getTagStructureStack() {
        return tagStructureStack;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.CANVAS_WRITING_CONTENT;
    }
}
