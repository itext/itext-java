package com.itextpdf.core.utils;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class TaggedPdfReaderToolTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/utils/TaggedPdfReaderToolTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/utils/TaggedPdfReaderToolTest/";

    @Before
    public void setUp() {
        File dest = new File(destinationFolder);
        dest.mkdirs();
        File[] files = dest.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Test
    public void taggedPdfReaderToolTest01() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        String filename = "iphone_user_guide.pdf";

        String outXmlPath = destinationFolder + "outXml01.xml";
        String cmpXmlPath = sourceFolder + "cmpXml01.xml";

        PdfReader reader = new PdfReader(sourceFolder + filename);
        PdfDocument document = new PdfDocument(reader);

        FileOutputStream outXml = new FileOutputStream(outXmlPath);

        TaggedPdfReaderTool tool = new TaggedPdfReaderTool(document);
        tool.setRootTag("root");
        tool.convertToXml(outXml);
        outXml.close();

        document.close();

        CompareTool compareTool = new CompareTool();
        if (!compareTool.compareXmls(outXmlPath, cmpXmlPath)) {
            Assert.fail("Resultant xml is different.");
        }
    }
}
