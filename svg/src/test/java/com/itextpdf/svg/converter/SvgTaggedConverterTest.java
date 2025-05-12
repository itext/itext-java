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
package com.itextpdf.svg.converter;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.pdfua.PdfUAConfig;
import com.itextpdf.pdfua.PdfUADocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("IntegrationTest")
public class SvgTaggedConverterTest extends ExtendedITextTest {


    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/converter/SvgTaggedConverterTest/";
    public static final String DEST_FOLDER = "./target/test/com/itextpdf/svg/converter/SvgTaggedConverterTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DEST_FOLDER);
    }

    @Test
    public void simpleSvgTagged() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "simple.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_simple.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination, writerProperties));

        pdfDocument.addNewPage();

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1);
        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(
                destination, cmpFile, DEST_FOLDER, "diff_"));
    }


    @Test
    public void simpleUACompliantSvgTagged() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "simpleUACompliantSvgTagged.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);

        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        pdfDocument.addNewPage();

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void simpleSvgTaggedWithConverterProperties() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "simpleSvgTaggedWithConverterProperties.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        pdfDocument.addNewPage();
        SvgConverterProperties properties = new SvgConverterProperties();

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1, properties);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void simpleSvgTaggedWithConverterPropertiesEmptyAlternateDescriptionEmpty() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER +
                "simpleSvgTaggedWithConverterPropertiesEmptyAlternateDescriptionEmpty.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        pdfDocument.addNewPage();
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.getAccessibilityProperties().setAlternateDescription("");

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1, properties);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void simpleSvgTaggedWithConverterPropertiesEmptyAlternateDescriptionSomeContent() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER +
                "simpleSvgTaggedWithConverterPropertiesEmptyAlternateDescriptionSomeContent.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        pdfDocument.addNewPage();
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.getAccessibilityProperties().setAlternateDescription("Hello there, ");

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1, properties);
        pdfDocument.close();

    }


    @Test
    public void simpleSvgTaggedWithConverterPropertiesTaggedAsArtifact() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "simpleSvgTaggedWithConverterPropertiesTaggedAsArtifact.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        pdfDocument.addNewPage();
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);

        SvgConverter.drawOnDocument(FileUtil.getInputStreamForFile(source), pdfDocument, 1, properties);
        pdfDocument.close();

    }

    @Test
    public void convertToImage() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "convertToImage.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        Image image = SvgConverter.convertToImage(FileUtil.getInputStreamForFile(source), pdfDocument);
        image.getAccessibilityProperties().setAlternateDescription("Hello!");
        Document document = new Document(pdfDocument);
        document.add(image);

        pdfDocument.close();
    }

    @Test
    public void convertToImageWithProps() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "convertToImageWithProps.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        SvgConverterProperties props = new SvgConverterProperties();
        props.getAccessibilityProperties().setAlternateDescription("Bing bong");
        Image image = SvgConverter.convertToImage(FileUtil.getInputStreamForFile(source), pdfDocument, props);
        Assertions.assertEquals("Bing bong", image.getAccessibilityProperties().getAlternateDescription());
        Assertions.assertEquals(StandardRoles.FIGURE, image.getAccessibilityProperties().getRole());

        pdfDocument.close();
    }

    @Test
    public void convertToImageWithPropsArtifacts() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "convertToImageWithPropsArtifacts.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        SvgConverterProperties props = new SvgConverterProperties();
        props.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        Image image = SvgConverter.convertToImage(FileUtil.getInputStreamForFile(source), pdfDocument, props);
        Assertions.assertEquals(StandardRoles.ARTIFACT, image.getAccessibilityProperties().getRole());
        pdfDocument.close();
    }

    @Test
    public void drawOnPage01() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "drawOnPage01.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_drawOnPage01.pdf";


        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination, writerProperties));
        PdfPage page = pdfDocument.addNewPage();
        SvgConverter.drawOnPage(FileUtil.getInputStreamForFile(source), page);
        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(
                destination, cmpFile, DEST_FOLDER, "diff_"));
    }

    @Test
    public void drawOnPageWithUaCompliant() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "drawOnPage01.pdf";


        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        PdfPage page = pdfDocument.addNewPage();
        SvgConverter.drawOnPage(FileUtil.getInputStreamForFile(source), page);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDocument.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.IMAGE_SHALL_HAVE_ALT, e.getMessage());
    }

    @Test
    public void drawOnPage02() throws Exception {
        String source = SOURCE_FOLDER + "simple.svg";
        String destination = DEST_FOLDER + "drawOnPage02.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfUADocument pdfDocument = new PdfUADocument(
                new PdfWriter(destination, writerProperties),
                new PdfUAConfig(PdfUAConformance.PDF_UA_2, "ua title", "en-US"));

        SvgConverterProperties converterProperties = new SvgConverterProperties();
        converterProperties.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        PdfPage page = pdfDocument.addNewPage();
        SvgConverter.drawOnPage(FileUtil.getInputStreamForFile(source), page, converterProperties);
        pdfDocument.close();
    }
}
