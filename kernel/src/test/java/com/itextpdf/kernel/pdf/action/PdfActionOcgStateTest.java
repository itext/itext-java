package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfActionOcgStateTest extends ExtendedITextTest {
    @Test
    public void pdfActionOcgStateUsageTest() {
        PdfName stateName = PdfName.ON;

        PdfDictionary ocgDict1 = new PdfDictionary();
        ocgDict1.put(PdfName.Type, PdfName.OCG);
        ocgDict1.put(PdfName.Name, new PdfName("ocg1"));

        PdfDictionary ocgDict2 = new PdfDictionary();
        ocgDict2.put(PdfName.Type, PdfName.OCG);
        ocgDict2.put(PdfName.Name, new PdfName("ocg2"));

        List<PdfDictionary> dicts = new ArrayList<>();
        dicts.add(ocgDict1);
        dicts.add(ocgDict2);

        PdfActionOcgState ocgState = new PdfActionOcgState(stateName, dicts);

        Assert.assertEquals(stateName, ocgState.getState());
        Assert.assertEquals(dicts, ocgState.getOcgs());

        List<PdfObject> states = ocgState.getObjectList();
        Assert.assertEquals(3, states.size());
        Assert.assertEquals(stateName, states.get(0));
        Assert.assertEquals(ocgDict1, states.get(1));
        Assert.assertEquals(ocgDict2, states.get(2));
    }
}
