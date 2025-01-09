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
package com.itextpdf.svg.processors.impl.font;


import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class FontFaceTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/font/FontFaceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/processors/impl/font/FontFaceTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    // TODO fix cmp file after DEVSIX-2256 is finished. Right now unicode range is not processed correctly
    public void unicodeRangeTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "unicodeRangeTest");
    }

    @Test
    public void droidSerifSingleQuotesTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifSingleQuotesTest");
    }

    @Test
    public void droidSerifWebFontTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifWebFontTest");
    }

    @Test
    public void droidSerifLocalFontTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifLocalFontTest");
    }

    @Test
    public void droidSerifLocalLocalFontTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifLocalLocalFontTest");
    }

    @Test
    public void droidSerifLocalWithMediaFontTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifLocalWithMediaFontTest");
    }

    @Test
    public void droidSerifLocalWithMediaRuleFontTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "droidSerifLocalWithMediaRuleFontTest");
    }

    @Test
    public void fontSelectorTest01() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "fontSelectorTest01");
    }

    @Test
    public void fontFaceGrammarTest() throws IOException, InterruptedException {
        convertAndCompare
                (sourceFolder, destinationFolder, "fontFaceGrammarTest");
    }


    @Test
    public void fontFaceWoffTest01() throws IOException, InterruptedException {
        runTest("fontFaceWoffTest01");
    }

    @Test
    public void fontFaceWoffTest02() throws IOException, InterruptedException {
        runTest("fontFaceWoffTest02");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNABLE_TO_RETRIEVE_FONT)
    })
    //TODO (DEVSIX-2064) Cannot retrieve NotoSansCJK-Regular
    public void fontFaceTtcTest() throws IOException, InterruptedException {
        runTest("fontFaceTtcTest");
    }

    @Test
    public void fontFaceWoff2SimpleTest() throws IOException, InterruptedException {
        runTest("fontFaceWoff2SimpleTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNABLE_TO_RETRIEVE_FONT)
    })
    //TODO (DEVSIX-2064) Cannot retrieve NotoSansCJK-Regular
    public void fontFaceWoff2TtcTest() throws IOException, InterruptedException {
        runTest("fontFaceWoff2TtcTest");
    }

    @Test
    //TODO(DEVSIX-5755): In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See BlocksMetadataPadding001Test in io for decompression details
    public void w3cProblemTest01() throws IOException, InterruptedException {
        runTest("w3cProblemTest01");
    }

    @Test
    public void w3cProblemTest02() throws IOException, InterruptedException {
        try {
            runTest("w3cProblemTest02");
        } catch (NegativeArraySizeException e) {
            return;
        }

        Assertions.fail("In w3c test suite this font is labeled as invalid, "
                + "so the invalid negative value is expected while creating a glyph.");
    }

    @Test
    //TODO(DEVSIX-5756): silently omitted, decompression should fail.
    //See HeaderFlavor001Test in io for decompression details
    public void w3cProblemTest03() throws IOException, InterruptedException {
        runTest("w3cProblemTest03");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.FONT_SUBSET_ISSUE)})
    //TODO(DEVSIX-5756): silently omitted, decompression should fail. Browser loads font but don't draw glyph.
    //See HeaderFlavor002Test in io for decompression details
    public void w3cProblemTest04() throws IOException, InterruptedException {
        //NOTE, iText fails on subsetting as expected.
        runTest("w3cProblemTest04");
    }

    @Test
    //TODO(DEVSIX-5755): In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See HeaderReserved001Test in io for decompression details
    public void w3cProblemTest05() throws IOException, InterruptedException {
        runTest("w3cProblemTest05");
    }

    @Test
    //TODO(DEVSIX-5755): In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See TabledataHmtxTransform003Test in io for decompression details
    public void w3cProblemTest06() throws IOException, InterruptedException {
        runTest("w3cProblemTest06");
    }

    @Test
    public void w3cProblemTest07() throws IOException, InterruptedException {
        try {
            runTest("w3cProblemTest07");
        } catch (NegativeArraySizeException e) {
            return;
        }

        Assertions.fail("In w3c test suite this font is labeled as invalid, "
                + "so the invalid negative value is expected while creating a glyph.");
    }

    @Test
    public void incorrectFontNameTest01() throws IOException, InterruptedException {
        runTest("incorrectFontNameTest01");
    }

    @Test
    // The result of te test is FAIL. However we consider it to be correct.
    // Although the font-family specified by the paragraph's class doesn't match the one of fontface,
    // font's full name contains specified font-family and iText takes it into account.
    public void incorrectFontNameTest02() throws IOException, InterruptedException {
        runTest("incorrectFontNameTest02");
    }

    @Test
    //Checks that font used in previous two files is correct
    public void incorrectFontNameTest03() throws IOException, InterruptedException {
        runTest("incorrectFontNameTest03");
    }

    @Test
    public void incorrectFontNameTest04() throws IOException, InterruptedException {
        runTest("incorrectFontNameTest04");
    }

    @Test
    @Disabled("DEVSIX-1759 - unicode in font family and different result in dotnet")
    public void fontFamilyTest01() throws IOException, InterruptedException {
        runTest("fontFamilyTest01");
    }

    @Test
    public void resolveFontsWithoutWriterProperties() throws IOException, InterruptedException {
        String fileName = "fontSelectorTest";
        ISvgConverterProperties properties = new SvgConverterProperties().setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(new File(sourceFolder + fileName + ".svg"), new File(destinationFolder + fileName + ".pdf"), properties);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithoutConverterPropertiesAndWriterProperties() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithoutConverterPropertiesAndWriterProperties";
        String svgFile = "fontSelectorTest";
        convertToSinglePage(new File(sourceFolder + svgFile + ".svg"), new File(destinationFolder + fileName + ".pdf"));
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithAllProperties() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithAllProperties";
        String svgFile = "fontSelectorTest";
        WriterProperties writerprops = new WriterProperties().setCompressionLevel(0);
        String baseUri = FileUtil.getParentDirectoryUri(new File(sourceFolder + svgFile + ".svg"));
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(baseUri).setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(new File(sourceFolder + svgFile + ".svg"), new File(destinationFolder + fileName + ".pdf"), properties, writerprops);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithWriterProperties() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithWriterProperties";
        String svgFile = "fontSelectorTest";
        WriterProperties writerprops = new WriterProperties().setCompressionLevel(0);
        convertToSinglePage(new File(sourceFolder + svgFile + ".svg"), new File(destinationFolder + fileName + ".pdf"), writerprops);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithConverterPropsAndWriterProps() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithConverterPropsAndWriterProps";
        String svgFile = "fontSelectorTest";
        WriterProperties writerprops = new WriterProperties().setCompressionLevel(0);
        String baseUri = FileUtil.getParentDirectoryUri(new File(sourceFolder + svgFile + ".svg"));
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(baseUri).setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(FileUtil.getInputStreamForFile(sourceFolder + svgFile + ".svg"),
                FileUtil.getFileOutputStream(destinationFolder + fileName + ".pdf"), properties, writerprops);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithConverterPropertiesAndEmptyUri() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithConverterPropertiesAndEmptyUri";
        String svgFile = "fontSelectorTest";
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri("").setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(new File(sourceFolder + svgFile + ".svg"), new File(destinationFolder + fileName + ".pdf"), properties);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsWithConverterPropertiesAndNullUri() throws IOException, InterruptedException {
        String fileName = "resolveFonts_WithConverterPropertiesAndNullUri";
        String svgFile = "fontSelectorTest";
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(null).setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(new File(sourceFolder + svgFile + ".svg"), new File(destinationFolder + fileName + ".pdf"), properties);
        compare(fileName, sourceFolder, destinationFolder);
    }

    @Test
    public void resolveFontsDefaultUri() throws IOException, InterruptedException {
        String fileName = "fontSelectorTest02";
        convertToSinglePage(new File(sourceFolder + fileName + ".svg"), new File(destinationFolder + fileName + ".pdf"));
        compare(fileName, sourceFolder, destinationFolder);
    }

    private void runTest(String fileName) throws IOException, InterruptedException {
        convert(sourceFolder + fileName + ".svg", destinationFolder + fileName + ".pdf");
        compare(fileName, sourceFolder, destinationFolder);
    }
}
