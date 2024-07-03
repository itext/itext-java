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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfA1ActionCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Test
    public void actionCheck01() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Launch);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Launch.getValue()), e.getMessage());
    }

    @Test
    public void actionCheck02() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Hide);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Hide.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck03() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Sound);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Sound.getValue()), e.getMessage());
    }

    @Test
    public void actionCheck04() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Movie);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.Movie.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck05() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ResetForm);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.ResetForm.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck06() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.ImportData);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.ImportData.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck07() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.JavaScript);
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.JavaScript.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck08() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary openActions = new PdfDictionary();
        openActions.put(PdfName.S, PdfName.Named);
        openActions.put(PdfName.N, new PdfName("CustomName"));
        doc.getCatalog().put(PdfName.OpenAction, openActions);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.NAMED_ACTION_TYPE_0_IS_NOT_ALLOWED, "CustomName"),
                e.getMessage());
    }

    @Test
    public void actionCheck09() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        page.setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED, PdfName.JavaScript.getValue()),
                e.getMessage());
    }

    @Test
    public void actionCheck10() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfDictionary action = new PdfDictionary();
        action.put(PdfName.S, PdfName.SetState);
        page.setAdditionalAction(PdfName.C, new PdfAction(action));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.DEPRECATED_SETSTATE_AND_NOOP_ACTIONS_ARE_NOT_ALLOWED, e.getMessage());
    }

    @Test
    public void actionCheck11() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        doc.getCatalog().setAdditionalAction(PdfName.C, PdfAction.createJavaScript("js"));

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY, e.getMessage());
    }
}
