package com.itextpdf.kernel.pdf;

import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SmartModePdfObjectsSerializerTest {

    @Test
    public void smartModeObjectSelfReferencingTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfDictionary dict1 = new PdfDictionary();
        dict1.makeIndirect(document);

        PdfDictionary dict2 = new PdfDictionary();
        dict2.makeIndirect(document);

        PdfArray array = new PdfArray();
        array.makeIndirect(document);
        array.add(new PdfString(new byte[10000]));
        array.add(new PdfDictionary(dict2));

        dict1.put(new PdfName("FirstDict"), array.getIndirectReference());
        dict2.put(new PdfName("SecondDict"), dict1.getIndirectReference());

        SmartModePdfObjectsSerializer serializer = new SmartModePdfObjectsSerializer();
        SerializedObjectContent serializedObject = serializer.serializeObject(dict1);

        //It is essential to serialize object with huge amount of memory
        StringBuilder stringBytes = new StringBuilder().append("$D$N/FirstDict$A$S");
        String end = "$D$\\D$\\A$\\D";
        for (int i = 0; i < 10000; i++) {
            stringBytes.append("\0");
        }
        stringBytes.append(end);

        SerializedObjectContent expected = new SerializedObjectContent(
                stringBytes.toString().getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(expected, serializedObject);
    }
}