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
package com.itextpdf.svg.renderers;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.element.SvgImage;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.xobject.SvgImageXObject;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class SvgImageRendererTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/SvgImageRendererTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/SvgImageRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void svgWithSvgTest() throws IOException, InterruptedException {
        String svgFileName = SOURCE_FOLDER + "svgWithSvg.svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_svgWithSvg.pdf";
        String outFileName = DESTINATION_FOLDER + "svgWithSvg.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName, new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);
            ISvgNodeRenderer topSvgRenderer = result.getRootRenderer();
            Rectangle wh = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, 0.0F, new SvgDrawContext(null, null));
            SvgImageXObject svgImageXObject = new SvgImageXObject(wh,
                    result, new ResourceResolver(SOURCE_FOLDER));
            SvgImage svgImage = new SvgImage(svgImageXObject);
            document.add(svgImage);
            document.add(svgImage);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void customSvgImageTest() throws IOException, InterruptedException {
        String svgFileName = SOURCE_FOLDER + "svgImage.svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_svgImage.pdf";
        String outFileName = DESTINATION_FOLDER + "svgImage.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName, new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, new SvgConverterProperties().setBaseUri(svgFileName));
            ISvgNodeRenderer topSvgRenderer = result.getRootRenderer();
            Rectangle wh = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, 0.0F, new SvgDrawContext(null, null));
            SvgImageXObject svgImageXObject = new SvgImageXObject(wh,
                    result, new ResourceResolver(SOURCE_FOLDER));
            SvgImage svgImage = new SvgImage(svgImageXObject);
            document.add(svgImage);
            document.add(svgImage);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void noSpecifiedWidthHeightImageTest() throws IOException, InterruptedException {
        String svgFileName = SOURCE_FOLDER + "noWidthHeightSvgImage.svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_noWidthHeightSvg.pdf";
        String outFileName = DESTINATION_FOLDER + "noWidthHeightSvg.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName,
                new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg,
                    new SvgConverterProperties().setBaseUri(svgFileName));
            ISvgNodeRenderer topSvgRenderer = result.getRootRenderer();
            Rectangle wh = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, 0.0F, new SvgDrawContext(null, null));
            document.add(new SvgImage(new SvgImageXObject(wh, result, new ResourceResolver(SOURCE_FOLDER))));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void relativeSizedSvg1Test() throws IOException, InterruptedException {
        String svgName = "fixed_height_percent_width";

        String svgFileName = SOURCE_FOLDER + svgName + ".svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + svgName + ".pdf";
        String outFileName = DESTINATION_FOLDER + svgName + ".pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName,
                new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg,
                    new SvgConverterProperties().setBaseUri(svgFileName));

            SvgDrawContext svgDrawContext = new SvgDrawContext(new ResourceResolver(SOURCE_FOLDER), null);
            SvgImageXObject svgImageXObject = new SvgImageXObject(result, svgDrawContext, 12, document.getPdfDocument());
            SvgImage svgImage = new SvgImage(svgImageXObject);

            svgImage.setWidth(100);
            svgImage.setHeight(300);

            document.add(svgImage);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void relativeSizedSvg3Test() throws IOException, InterruptedException {
        String svgName = "viewbox_fixed_height_percent_width";

        String svgFileName = SOURCE_FOLDER + svgName + ".svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + svgName + ".pdf";
        String outFileName = DESTINATION_FOLDER + svgName + ".pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName,
                new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg,
                    new SvgConverterProperties().setBaseUri(svgFileName));

            SvgDrawContext svgDrawContext = new SvgDrawContext(new ResourceResolver(SOURCE_FOLDER), null);
            SvgImageXObject svgImageXObject = new SvgImageXObject(result, svgDrawContext, 12, document.getPdfDocument());
            SvgImage svgImage = new SvgImage(svgImageXObject);

            svgImage.setWidth(100);
            svgImage.setHeight(300);

            document.add(svgImage);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void relativeSizedSvg4Test() throws IOException, InterruptedException {
        String svgName = "viewbox_percent_height_percent_width";

        String svgFileName = SOURCE_FOLDER + svgName + ".svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + svgName + ".pdf";
        String outFileName = DESTINATION_FOLDER + svgName + ".pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName,
                new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg,
                    new SvgConverterProperties().setBaseUri(svgFileName));

            SvgDrawContext svgDrawContext = new SvgDrawContext(new ResourceResolver(SOURCE_FOLDER), null);
            SvgImageXObject svgImageXObject = new SvgImageXObject(result, svgDrawContext, 12, document.getPdfDocument());
            SvgImage svgImage = new SvgImage(svgImageXObject);

            document.add(svgImage);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void relativeSizedSvg5Test() throws IOException, InterruptedException {
        String svgName = "viewbox_percent_height_percent_width_prRatio_none";

        String svgFileName = SOURCE_FOLDER + svgName + ".svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + svgName + ".pdf";
        String outFileName = DESTINATION_FOLDER + svgName + ".pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName,
                new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg,
                    new SvgConverterProperties().setBaseUri(svgFileName));

            SvgDrawContext svgDrawContext = new SvgDrawContext(new ResourceResolver(SOURCE_FOLDER), null);
            SvgImageXObject svgImageXObject = new SvgImageXObject(result, svgDrawContext, 12, document.getPdfDocument());
            Div div = new Div();
            div.setWidth(100);
            div.setHeight(300);
            SvgImage svgImage = new SvgImage(svgImageXObject);
            svgImage.setWidth(UnitValue.createPercentValue(100));
            svgImage.setHeight(UnitValue.createPercentValue(100));
            div.add(svgImage);
            document.add(div);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
