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
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA2CatalogCheckTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2CatalogCheckTest/";
    public static final String destinationFolder = "./target/test/PdfA2CatalogCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void catalogCheck01() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.ValueOfNameEntryShallBeUniqueAmongAllOptionalContentConfigurationDictionaries);

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
    public void catalogCheck02() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.ValueOfNameEntryShallBeUniqueAmongAllOptionalContentConfigurationDictionaries);

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
    public void catalogCheck03() throws IOException, XMPException, InterruptedException {
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
    public void catalogCheck04() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.OptionalContentConfigurationDictionaryShallContainNameEntry);

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
    public void catalogCheck05() throws IOException, XMPException, InterruptedException {
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
    public void catalogCheck06() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.OrderArrayShallContainReferencesToAllOcgs);

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
    public void catalogCheck07() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.OrderArrayShallContainReferencesToAllOcgs);

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
    public void catalogCheck08() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.OrderArrayShallContainReferencesToAllOcgs);

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
    public void catalogCheck09() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.CatalogDictionaryShallNotContainAlternatepresentationsNamesEntry);

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
    public void catalogCheck10() throws FileNotFoundException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.CatalogDictionaryShallNotContainRequirementsEntry);

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
