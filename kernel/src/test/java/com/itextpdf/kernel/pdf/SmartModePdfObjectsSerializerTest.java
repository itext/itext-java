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
package com.itextpdf.kernel.pdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SmartModePdfObjectsSerializerTest extends ExtendedITextTest {

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