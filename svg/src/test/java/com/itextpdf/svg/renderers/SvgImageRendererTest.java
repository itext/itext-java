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
package com.itextpdf.svg.renderers;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.element.SvgImage;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.xobject.SvgImageXObject;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class SvgImageRendererTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/SvgImageRendererTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/SvgImageRendererTest/";

    @BeforeClass
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
            float[] wh = SvgConverter.extractWidthAndHeight(topSvgRenderer);
            SvgImageXObject svgImageXObject = new SvgImageXObject(new Rectangle(0, 0, wh[0], wh[1]),
                    result, new ResourceResolver(SOURCE_FOLDER));
            SvgImage svgImage = new SvgImage(svgImageXObject);
            document.add(svgImage);
            document.add(svgImage);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void customSvgImageTest() throws IOException, InterruptedException {
        String svgFileName = SOURCE_FOLDER + "svgImage.svg";
        String cmpFileName = SOURCE_FOLDER + "cmp_svgImage.pdf";
        String outFileName = DESTINATION_FOLDER + "svgImage.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName, new WriterProperties().setCompressionLevel(0))))) {
            INode parsedSvg = SvgConverter.parse(FileUtil.getInputStreamForFile(svgFileName));
            ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, null);
            ISvgNodeRenderer topSvgRenderer = result.getRootRenderer();
            float[] wh = SvgConverter.extractWidthAndHeight(topSvgRenderer);
            SvgImageXObject svgImageXObject = new SvgImageXObject(new Rectangle(0, 0, wh[0], wh[1]),
                    result, new ResourceResolver(SOURCE_FOLDER));
            SvgImage svgImage = new SvgImage(svgImageXObject);
            document.add(svgImage);
            document.add(svgImage);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
