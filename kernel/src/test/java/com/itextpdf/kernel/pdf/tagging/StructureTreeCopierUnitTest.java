package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

@Category(UnitTest.class)
public class StructureTreeCopierUnitTest extends ExtendedITextTest {

    private static final Map<PdfName, PdfObject> td = new HashMap<>();
    private static final Map<PdfName, PdfObject> tr = new HashMap<>();
    private static final Map<PdfName, PdfObject> th = new HashMap<>();

    static {
        td.put(PdfName.S, PdfName.TD);
        tr.put(PdfName.S, PdfName.TR);
        th.put(PdfName.S, PdfName.TH);
    }

    @Test
    public void shouldTableElementBeCopiedTdTrTest() {
        PdfDictionary obj = new PdfDictionary(td);
        PdfDictionary parent = new PdfDictionary(tr);

        Assert.assertTrue(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }

    @Test
    public void shouldTableElementBeCopiedThTrTest() {
        PdfDictionary obj = new PdfDictionary(th);
        PdfDictionary parent = new PdfDictionary(tr);

        Assert.assertTrue(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }

    @Test
    public void shouldTableElementBeCopiedTdTdTest() {
        PdfDictionary obj = new PdfDictionary(td);
        PdfDictionary parent = new PdfDictionary(td);

        Assert.assertFalse(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }

    @Test
    public void shouldTableElementBeCopiedTrTdTest() {
        PdfDictionary obj = new PdfDictionary(tr);
        PdfDictionary parent = new PdfDictionary(td);

        Assert.assertFalse(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }

    @Test
    public void shouldTableElementBeCopiedTrTrTest() {
        PdfDictionary obj = new PdfDictionary(tr);
        PdfDictionary parent = new PdfDictionary(tr);

        Assert.assertFalse(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }

    @Test
    public void shouldTableElementBeCopiedThThTest() {
        PdfDictionary obj = new PdfDictionary(th);
        PdfDictionary parent = new PdfDictionary(th);

        Assert.assertFalse(StructureTreeCopier.shouldTableElementBeCopied(obj, parent));
    }
}
