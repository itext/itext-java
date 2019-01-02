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
package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA2CatalogCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2CatalogCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2CatalogCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void catalogCheck01() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck02() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck03() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_catalogCheck03.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_catalogCheck03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName2"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void catalogCheck04() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY_SHALL_CONTAIN_NAME_ENTRY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        configs.add(config);
        config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName2"));
        configs.add(config);
        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck05() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_catalogCheck05.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_catalogCheck05.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);
        ocgs.add(orderItem1);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void catalogCheck06() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck07() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        ocgs.add(orderItem);
        ocgs.add(orderItem1);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck08() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();
        PdfDictionary ocProperties = new PdfDictionary();
        PdfDictionary d = new PdfDictionary();
        d.put(PdfName.Name, new PdfString("CustomName"));
        PdfArray configs = new PdfArray();
        PdfDictionary config = new PdfDictionary();
        config.put(PdfName.Name, new PdfString("CustomName1"));
        PdfArray order = new PdfArray();
        PdfDictionary orderItem = new PdfDictionary();
        orderItem.put(PdfName.Name, new PdfString("CustomName2"));
        order.add(orderItem);
        PdfDictionary orderItem1 = new PdfDictionary();
        orderItem1.put(PdfName.Name, new PdfString("CustomName3"));
        order.add(orderItem1);
        config.put(PdfName.Order, order);

        PdfArray ocgs = new PdfArray();
        PdfDictionary orderItem2 = new PdfDictionary();
        orderItem2.put(PdfName.Name, new PdfString("CustomName4"));
        ocgs.add(orderItem2);
        PdfDictionary orderItem3 = new PdfDictionary();
        orderItem3.put(PdfName.Name, new PdfString("CustomName5"));
        ocgs.add(orderItem3);

        ocProperties.put(PdfName.OCGs, ocgs);

        configs.add(config);


        ocProperties.put(PdfName.D, d);
        ocProperties.put(PdfName.Configs, configs);

        doc.getCatalog().put(PdfName.OCProperties, ocProperties);

        doc.close();
    }

    @Test
    public void catalogCheck09() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATEPRESENTATIONS_NAMES_ENTRY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        PdfDictionary names = new PdfDictionary();
        names.put(PdfName.AlternatePresentations, new PdfDictionary());

        doc.getCatalog().put(PdfName.Names, names);

        doc.close();
    }

    @Test
    public void catalogCheck10() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_REQUIREMENTS_ENTRY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.addNewPage();

        doc.getCatalog().put(PdfName.Requirements, new PdfArray());

        doc.close();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
