/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.actions.session;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ClosingSessionTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void producerTest() {
        ClosingSession session = new ClosingSession(null);

        List<String> producer = new ArrayList<>();
        producer.add("producer0");
        producer.add("producer1");

        Assert.assertNull(session.getProducer());

        session.setProducer(producer);

        Assert.assertEquals(producer, session.getProducer());
    }

    @Test
    public void propertiesTest() {
        ClosingSession session = new ClosingSession(null);

        Assert.assertNull(session.getProperty("test"));
        Assert.assertNull(session.getProperty("test-map"));

        session.setProperty("test", "test-value");

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("key", "value");
        testMap.put("int-key", 0);
        session.setProperty("test-map", testMap);

        Assert.assertEquals("test-value", session.getProperty("test"));
        Assert.assertEquals(testMap, session.getProperty("test-map"));
    }

    @Test
    public void nullConstructorTest() throws IOException {
        ClosingSession session = new ClosingSession(null);

        Assert.assertNull(session.getDocument());
    }

    @Test
    public void documentConstructorTest() throws IOException {

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ClosingSession sessionWithDocument = new ClosingSession(document);
            Assert.assertEquals(document, sessionWithDocument.getDocument());
        }
    }
}
