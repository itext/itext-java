/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.processors.impl.font;


import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FontFaceTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/font/FontFaceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/processors/impl/font/FontFaceTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
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
    //TODO: In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See BlocksMetadataPadding001Test in io for decompression details
    public void w3cProblemTest01() throws IOException, InterruptedException {
        runTest("w3cProblemTest01");
    }

    @Test
    @Ignore("DEVSIX-1612")
    //TODO: In w3c test suite this font is labeled as invalid though and its loading failed in browser, though iText parses its as correct one and LOADS!
    //See DirectoryTableOrder002Test in io for decompression details
    public void w3cProblemTest02() throws IOException, InterruptedException {
        runTest("w3cProblemTest02");
    }

    @Test
    //TODO: silently omitted, decompression should fail.
    //See HeaderFlavor001Test in io for decompression details
    public void w3cProblemTest03() throws IOException, InterruptedException {
        runTest("w3cProblemTest03");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = com.itextpdf.io.LogMessageConstant.FONT_SUBSET_ISSUE)})
    //TODO: silently omitted, decompression should fail. Browser loads font but don't draw glyph.
    //See HeaderFlavor002Test in io for decompression details
    public void w3cProblemTest04() throws IOException, InterruptedException {
        //NOTE, iText fails on subsetting as expected.
        runTest("w3cProblemTest04");
    }

    @Test
    //TODO: In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See HeaderReserved001Test in io for decompression details
    public void w3cProblemTest05() throws IOException, InterruptedException {
        runTest("w3cProblemTest05");
    }

    @Test
    //TODO: In w3c test suite this font is labeled as invalid though it correctly parsers both in browser and iText
    //See TabledataHmtxTransform003Test in io for decompression details
    public void w3cProblemTest06() throws IOException, InterruptedException {
        runTest("w3cProblemTest06");
    }

    @Test
    @Ignore("DEVSIX-1612")
    //TODO: In w3c test suite this font is labeled as invalid though and its loading failed in browser, though iText parses its as correct one and LOADS!
    //See ValidationOff012Test in io for decompression details
    public void w3cProblemTest07() throws IOException, InterruptedException {
        runTest("w3cProblemTest07");
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
    @Ignore("DEVSIX-1759 - unicode in font family and different result in dotnet")
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
        String baseUri = FileUtil.getParentDirectory(new File(sourceFolder + svgFile + ".svg"));
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
        String baseUri = FileUtil.getParentDirectory(new File(sourceFolder + svgFile + ".svg"));
        ISvgConverterProperties properties = new SvgConverterProperties().setBaseUri(baseUri).setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(new MediaDeviceDescription(MediaType.ALL));
        convertToSinglePage(new FileInputStream(sourceFolder + svgFile + ".svg"), new FileOutputStream(destinationFolder + fileName + ".pdf"), properties, writerprops);
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

    // TODO DEVSIX-2113
    // This test passes correctly when baseUri is set manually. Remove SvgConverterProperties and use convertToSinglePage(File, File) method instead.
    // It must produce the same pdf as the one with a pre-defined baseUri does
    @Test
    public void resolveFontsDefaultUri() throws IOException, InterruptedException {
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.setBaseUri(sourceFolder);
        String fileName = "fontSelectorTest02";
        convertToSinglePage(new File(sourceFolder + fileName + ".svg"), new File(destinationFolder + fileName + ".pdf"), properties);
        compare(fileName, sourceFolder, destinationFolder);
    }

    private void runTest(String fileName) throws IOException, InterruptedException {
        convert(sourceFolder + fileName + ".svg", destinationFolder + fileName + ".pdf");
        compare(fileName, sourceFolder, destinationFolder);
    }
}
