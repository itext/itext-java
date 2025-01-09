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
package com.itextpdf.svg.css;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class FontAttributesTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/FontAttributesTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/FontAttributesTest/";
    public static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/svg/css/fonts/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    //TODO DEVSIX-8764: update cmp file after supporting
    public void lighterBolderFontWeightTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-weight-lighter-bolder");
    }

    @Test
    public void fontSizeAdjustTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-size-adjust");
    }

    //TODO DEVSIX-2507: Update cmp file after supporting
    @Test
    public void fontSizeAdjustTspanTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-size-adjust-tspan");
    }

    @Test
    public void fontStretchTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-stretch");
    }

    //TODO DEVSIX-2507: Update cmp file after supporting
    @Test
    public void fontStretchTspanTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-stretch-tspan");
    }

    @Test
    public void fontVariantTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"font-variant");
    }

    @Test
    //TODO DEVSIX-2507: update cmp after supporting
    public void lengthAdjustTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"length-adjust");
    }

    @Test
    public void letterWordSpacingTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"letter-word-spacing");
    }

    @Test
    public void unicodeBidiTest() throws IOException, InterruptedException {
        // Set up font provider
        String fontPath = FONTS_FOLDER + "NotoSansArabic-Regular.ttf";
        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(FontProgramFactory.createFont(fontPath));
        SvgConverterProperties properties = new SvgConverterProperties().setBaseUri(null)
                .setFontProvider(new BasicFontProvider()).setMediaDeviceDescription(
                        new MediaDeviceDescription(MediaType.ALL));
        properties.setFontProvider(fontProvider);
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"unicode-bidi", properties);
    }

    @Test
    //TODO DEVSIX-4114: update cmp file after supporting
    public void writingModeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"writing-mode");
    }
}
