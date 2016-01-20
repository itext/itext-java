package com.itextpdf.core.parser;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfImageXObjectTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/core/parser/PdfImageXObjectTest/";

    private void testFile(String filename, int page, String objectid) throws Exception{
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + filename));
        try{
            PdfResources resources = pdfDocument.getPage(page).getResources();
            PdfDictionary xobjets = resources.getResource(PdfName.XObject);
            PdfObject obj = xobjets.get(new PdfName(objectid));
            if (obj == null) {
                throw new NullPointerException("Reference " + objectid + " not found - Available keys are " + xobjets.keySet());
            }
            PdfImageXObject img = new PdfImageXObject((PdfStream)(obj.isIndirectReference() ? ((PdfIndirectReference)obj).getRefersTo() : obj));
            byte[] result = img.getImageBytes(true);
            Assert.assertNotNull(result);
            int zeroCount = 0;
            for (byte b : result) {
                if (b == 0) zeroCount++;
            }
            Assert.assertTrue(zeroCount > 0);
        } finally {
            pdfDocument.close();
        }
    }

    @Test
    public void testMultiStageFilters() throws Exception{
        testFile("multistagefilter1.pdf", 1, "Obj13");
    }

    @Test
    public void testAscii85Filters() throws Exception{
        testFile("ASCII85_RunLengthDecode.pdf", 1, "Im9");
    }

    @Test
    public void testCcittFilters() throws Exception{
        testFile("ccittfaxdecode.pdf", 1, "background0");
    }

    @Test
    public void testFlateDecodeFilters() throws Exception{
        testFile("flatedecode_runlengthdecode.pdf", 1, "Im9");
    }

    @Test
    public void testDctDecodeFilters() throws Exception{
        testFile("dctdecode.pdf", 1, "im1");
    }

    @Test
    public void testjbig2Filters() throws Exception{
        testFile("jbig2decode.pdf", 1, "2");
    }

}
