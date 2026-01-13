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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNABLE_TO_PARSE_OPERATOR_WRONG_NUMBER_OF_OPERANDS)
    })
    public void smallerNumberOfOperandsTmTest1() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new SimpleTextExtractionStrategy());
        List<PdfObject> operands = Collections.singletonList((PdfObject) new PdfLiteral("Tm"));
        PdfLiteral operator = new PdfLiteral("Tm");
        AssertUtil.doesNotThrow(() -> processor.invokeOperator(operator, operands));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNABLE_TO_PARSE_OPERATOR_WRONG_NUMBER_OF_OPERANDS)
    })
    public void smallerNumberOfOperandsTmTest2() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new SimpleTextExtractionStrategy());
        List<PdfObject> operands = new ArrayList<>();
        operands.add((PdfObject) new PdfNumber(1));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(1));
        operands.add((PdfObject) new PdfLiteral("Tm"));
        PdfLiteral operator = new PdfLiteral("Tm");
        AssertUtil.doesNotThrow(() -> processor.invokeOperator(operator, operands));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNABLE_TO_PARSE_OPERATOR_WRONG_NUMBER_OF_OPERANDS)
    })
    public void biggerNumberOfOperandsTmTest() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new SimpleTextExtractionStrategy());
        List<PdfObject> operands = new ArrayList<>();
        operands.add((PdfObject) new PdfNumber(1));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(1));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(0));
        operands.add((PdfObject) new PdfNumber(10));
        operands.add((PdfObject) new PdfLiteral("Tm"));
        PdfLiteral operator = new PdfLiteral("Tm");
        AssertUtil.doesNotThrow(() -> processor.invokeOperator(operator, operands));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNABLE_TO_PARSE_OPERATOR_WRONG_NUMBER_OF_OPERANDS)
    })
    public void smallerNumberOfOperandsMTest() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new LocationTextExtractionStrategy());
        List<PdfObject> operands = Collections.singletonList((PdfObject) new PdfLiteral("M"));
        PdfLiteral operator = new PdfLiteral("M");
        AssertUtil.doesNotThrow(() -> processor.invokeOperator(operator, operands));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.UNABLE_TO_PARSE_OPERATOR_WRONG_NUMBER_OF_OPERANDS)
    })
    public void biggerNumberOfOperandsMTest() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new LocationTextExtractionStrategy());
        List<PdfObject> operands = new ArrayList<>();
        operands.add((PdfObject) new PdfNumber(10));
        operands.add((PdfObject) new PdfNumber(10));
        operands.add((PdfObject) new PdfLiteral("M"));
        PdfLiteral operator = new PdfLiteral("M");
        AssertUtil.doesNotThrow(() -> processor.invokeOperator(operator, operands));
    }
}
