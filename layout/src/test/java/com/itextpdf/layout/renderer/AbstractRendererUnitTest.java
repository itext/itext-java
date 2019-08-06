package com.itextpdf.layout.renderer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public abstract class AbstractRendererUnitTest extends ExtendedITextTest {

    // This also can be converted to a @Rule to have it all at hand in the future
    protected static Document createDocument() {
        return new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())));
    }

    protected static TextRenderer createLayoutedTextRenderer(String text, Document document) {
        TextRenderer renderer = (TextRenderer) new TextRenderer(new Text(text)).setParent(document.getRenderer());
        renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(1000, 1000))));
        return renderer;
    }

    protected static ImageRenderer createLayoutedImageRenderer(float width, float height, Document document) {
        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(width, height));
        Image img = new Image(xObject);
        ImageRenderer renderer = (ImageRenderer) new ImageRenderer(img).setParent(document.getRenderer());
        renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(1000, 1000))));
        return renderer;
    }

    protected static LayoutArea createLayoutArea(float width, float height) {
        return new LayoutArea(1, new Rectangle(width, height));
    }

}
