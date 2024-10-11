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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Collections;

@Tag("UnitTest")
public class PdfCanvasProcessorUnitTest extends ExtendedITextTest {

    @Test
    public void beginMarkerContentOperatorTest() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new FilteredEventListener()) {
            @Override
            protected void beginMarkedContent(PdfName tag, PdfDictionary dict) {
                Assertions.assertNull(dict);
            }
        };
        IContentOperator contentOperator = processor.registerContentOperator("BMC", null);
        processor.registerContentOperator("BMC", contentOperator);
        contentOperator.invoke(processor, null, Collections.singletonList((PdfObject) null));
    }
}
