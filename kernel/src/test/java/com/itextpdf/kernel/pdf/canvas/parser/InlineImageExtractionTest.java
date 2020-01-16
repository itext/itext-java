/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Indexed;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class InlineImageExtractionTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/canvas/parser/InlineImageExtractionTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/canvas/parser/InlineImageExtractionTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void extractSingleInlineImageWithIndexedColorSpaceTest() throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfReader(sourceFolder + "inlineImageExtraction.pdf"));
        InlineImageEventListener eventListener = new InlineImageEventListener();

        PdfCanvasProcessor canvasProcessor = new PdfCanvasProcessor(eventListener);
        canvasProcessor.processPageContent(pdf.getFirstPage());
        pdf.close();

        List<PdfStream> inlineImages = eventListener.getInlineImages();

        Assert.assertEquals(1, inlineImages.size());

        byte[] imgBytes = inlineImages.get(0).getBytes();
        byte[] cmpImgBytes = Files.readAllBytes(Paths.get(sourceFolder, "imgtest.dat"));
        Assert.assertArrayEquals(cmpImgBytes, imgBytes);

        PdfDictionary expectedDict = new PdfDictionary();
        expectedDict.put(PdfName.BitsPerComponent, new PdfNumber(8));
        expectedDict.put(PdfName.Height, new PdfNumber(50));
        expectedDict.put(PdfName.Width, new PdfNumber(50));
        String indexedCsLookupData = "\u007F\u007F\u007Fïïï\u000F\u000F\u000F???¿¿¿ÏÏÏ///\u001F\u001F\u001F___ßßß"
                + "\u009F\u009F\u009FOOO¯¯¯ooo\u008F\u008F\u008F°°µ::<ââàuuy,,-ÜÜâ\u000E\u000E\u000Fúúû\u001D\u001D\u001E"
                + "ððõXXZ::?\u0004\u0004\u0004226!!$IIK\u0019\u0019\u001Býýþ\u0092\u0092\u0097õõø\f\f\r"
                + "))-÷÷úììòÍÍÓ66;\b\b\t\u0084\u0084\u0088¡¡¦îîô\u0014\u0014\u0016òòö\u0010\u0010\u0012¾¾Äffiüüýóó÷..2ûûü"
                + "ööù%%)ííó\u001D\u001D\u001F>>Døøúññö\u000E\u000E\u000Eééç\u008D\u008D\u008CÓÓÒCCI©©¨\u009B\u009B\u009A"
                + "òòñôôózz|888÷÷÷ììëÝÝãµµ¸bbb\u0095\u0095\u0098··¶ûûûºº¼\u0089\u0089\u008Bååãêêë==>ÑÑÖ***qqpààåZZ\\õõõ"
                + "\u007F\u007F~\u008E\u008E\u008E\u001E\u001E\u001FÀÀÅååèÆÆÅççåÇÇÊ\u001C\u001C\u001C]]^±±¶TTTççêÉÉÇFFFáá"
                + "æÅÅÄyy{ÍÍÎÐÐÕ^^^vvyîîí\u0087\u0087\u008A}}}xxzÊÊËjjl--.ëëò\u0000\u0000\u0000ÿÿÿ{{{|||}}}~~~\u007F\u007F"
                + "\u007F\u0080\u0080\u0080\u0081\u0081\u0081\u0082\u0082\u0082\u0083\u0083\u0083\u0084\u0084\u0084\u0085"
                + "\u0085\u0085\u0086\u0086\u0086\u0087\u0087\u0087\u0088\u0088\u0088\u0089\u0089\u0089\u008A\u008A\u008A"
                + "\u008B\u008B\u008B\u008C\u008C\u008C\u008D\u008D\u008D\u008E\u008E\u008E\u008F\u008F\u008F\u0090\u0090"
                + "\u0090\u0091\u0091\u0091\u0092\u0092\u0092\u0093\u0093\u0093\u0094\u0094\u0094\u0095\u0095\u0095\u0096"
                + "\u0096\u0096\u0097\u0097\u0097\u0098\u0098\u0098\u0099\u0099\u0099\u009A\u009A\u009A\u009B\u009B\u009B"
                + "\u009C\u009C\u009C\u009D\u009D\u009D\u009E\u009E\u009E\u009F\u009F\u009F   ¡¡¡¢¢¢£££¤¤¤¥¥¥¦¦¦§§§¨¨¨©©©"
                + "ªªª«««¬¬¬\u00AD\u00AD\u00AD®®®¯¯¯°°°±±±²²²³³³´´´µµµ¶¶¶···¸¸¸¹¹¹ººº»»»¼¼¼½½½¾¾¾¿¿¿ÀÀÀÁÁÁÂÂÂÃÃÃÄÄÄÅÅÅÆÆÆ"
                + "ÇÇÇÈÈÈÉÉÉÊÊÊËËËÌÌÌÍÍÍÎÎÎÏÏÏÐÐÐÑÑÑÒÒÒÓÓÓÔÔÔÕÕÕÖÖÖ×××ØØØÙÙÙÚÚÚÛÛÛÜÜÜÝÝÝÞÞÞßßßàààáááâââãããäääåååæææçççèèè"
                + "éééêêêëëëìììíííîîîïïïðððñññòòòóóóôôôõõõööö÷÷÷øøøùùùúúúûûûüüüýýýþþþÿÿÿ";
        Indexed expectedIndexedCs = new Indexed(PdfName.DeviceRGB, 255, new PdfString(indexedCsLookupData));
        expectedDict.put(PdfName.ColorSpace, expectedIndexedCs.getPdfObject());

        Assert.assertTrue(new CompareTool().compareDictionaries(inlineImages.get(0), expectedDict));
    }

    private static class InlineImageEventListener implements IEventListener {
        private List<PdfStream> inlineImages = new ArrayList<>();

        public List<PdfStream> getInlineImages() {
            return inlineImages;
        }

        public void eventOccurred(IEventData data, EventType type) {
            switch (type) {
                case RENDER_IMAGE:
                    ImageRenderInfo imageEventData = (ImageRenderInfo) data;
                    if (((ImageRenderInfo) data).isInline()) {
                        inlineImages.add(imageEventData.getImage().getPdfObject());
                    }
                    break;
            }
        }

        public Set<EventType> getSupportedEvents() {
            return new LinkedHashSet<>(Collections.singletonList(EventType.RENDER_IMAGE));
        }
    }
}
