package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Category(UnitTest.class)
public class TableRendererTest  extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, count=13)})
    public void calculateColumnWidthsNotPointValue() throws FileNotFoundException {
        junitExpectedException.expect(NullPointerException.class);

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream("table_out.pdf")));
        Document doc = new Document(pdfDoc);
        LayoutArea area = new LayoutArea(1, new Rectangle(0,0,100,100));
        LayoutContext layoutContext = new LayoutContext(area);
        Rectangle layoutBox = area.getBBox().clone();


        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 10, 80}));
        table.setProperty(Property.MARGIN_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.MARGIN_LEFT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_RIGHT, UnitValue.createPercentValue(7));
        table.setProperty(Property.PADDING_LEFT, UnitValue.createPercentValue(7));
        table.addCell("Col a");
        table.addCell("Col b");
        table.addCell("Col c");
        table.addCell("Value a");
        table.addCell("Value b");
        table.addCell("This is a long description for column c. " +
                "It needs much more space hence we made sure that the third column is wider.");
        doc.add(table);

        TableRenderer tableRenderer = (TableRenderer) table.getRenderer();

        tableRenderer.calculateColumnWidths(layoutBox);

    }

}
