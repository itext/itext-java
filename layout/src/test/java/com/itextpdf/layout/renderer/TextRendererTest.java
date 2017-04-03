package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.ByteBufferOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TextRendererTest extends ExtendedITextTest {

    @Test
    public void nextRendererTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteBufferOutputStream()));
        pdfDoc.addNewPage();
        Document doc = new Document(pdfDoc);
        RootRenderer documentRenderer = doc.getRenderer();

        Text text = new Text("hello");
        text.setNextRenderer(new TextRenderer(text));

        IRenderer textRenderer1 = text.getRenderer().setParent(documentRenderer);
        IRenderer textRenderer2 = text.getRenderer().setParent(documentRenderer);

        LayoutArea area = new LayoutArea(1, new Rectangle(100, 100, 100, 100));
        LayoutContext layoutContext = new LayoutContext(area);

        doc.close();

        LayoutResult result1 = textRenderer1.layout(layoutContext);
        LayoutResult result2 = textRenderer2.layout(layoutContext);

        Assert.assertEquals(result1.getOccupiedArea(), result2.getOccupiedArea());
    }

}
