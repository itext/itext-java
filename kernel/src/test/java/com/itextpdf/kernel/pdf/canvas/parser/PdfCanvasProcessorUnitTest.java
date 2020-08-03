package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredEventListener;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;

@Category(UnitTest.class)
public class PdfCanvasProcessorUnitTest extends ExtendedITextTest {

    @Test
    public void beginMarkerContentOperatorTest() {
        PdfCanvasProcessor processor = new PdfCanvasProcessor(new FilteredEventListener()) {
            @Override
            protected void beginMarkedContent(PdfName tag, PdfDictionary dict) {
                Assert.assertNull(dict);
            }
        };
        IContentOperator contentOperator = processor.registerContentOperator("BMC", null);
        processor.registerContentOperator("BMC", contentOperator);
        contentOperator.invoke(processor, null, Collections.singletonList((PdfObject) null));
    }
}
