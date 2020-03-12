/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfA3CatalogCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    //TODO Remove expected exception when DEVSIX-3206 will be fixed
    public void checkAbsenceOfConfigEntry() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);
        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    //TODO Remove expected exception when DEVSIX-3206 will be fixed
    public void checkAbsenceOfOrderEntry() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");

        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B,
                new PdfOutputIntent("Custom", "",
                        "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        PdfDictionary ocProperties = new PdfDictionary();

        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));

        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        PdfArray configs = new PdfArray();

        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));

        configs.add(config);

        ocProperties.put(PdfName.OCGs, ocgs);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);
        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }
}
