package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;


@Category(UnitTest.class)
public class PdfTextArrayTest extends ExtendedITextTest {

    @Test
    public void addNZeroTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assert.assertFalse(textArray.add(0.0f));
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void additiveInverseTest() {
        PdfTextArray textArray = new PdfTextArray();
        float number = 10;
        textArray.add(number);
        textArray.add(number * -1);
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addEmptyStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assert.assertFalse(textArray.add(""));
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNewStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String content = "content";
        Assert.assertTrue(textArray.add(content));
        Assert.assertEquals(new PdfString(content), textArray.get(0));
    }

    @Test
    public void appendStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String[] stringArray = new String[] {"one", "element"};
        for (String string : stringArray)
            textArray.add(string);
        PdfString expected = new PdfString(stringArray[0] + stringArray[1]);
        Assert.assertEquals(expected, textArray.get(0));
    }

    @Test
    public void addStringWithFontTest() throws IOException {
        PdfTextArray textArray = new PdfTextArray();
        String string = "font";
        textArray.add(string, PdfFontFactory.createFont());
        Assert.assertEquals(new PdfString(string), textArray.get(0));
    }

    @Test
    public void addAllNullTest() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll((PdfArray) null);
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNumbersTest() {
        PdfTextArray textArray = new PdfTextArray();
        float a = 5f;
        float b = 10f;
        textArray.add(new PdfNumber(a));
        textArray.add(new PdfNumber(b));
        Assert.assertEquals(a + b, textArray.getAsNumber(0).floatValue(), 0.0001);
    }

    @Test
    public void addCollectionTest() {
        PdfArray collection = new PdfArray();
        collection.add(new PdfString("str"));
        collection.add(new PdfNumber(11));

        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll(collection);
        Assert.assertEquals(collection.list, textArray.list);
    }
}
