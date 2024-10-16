/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

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

import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfResourcesTest extends ExtendedITextTest {
    @Test
    public void resourcesTest1() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        PdfName n1 = resources.addExtGState(egs1);
        Assertions.assertEquals("Gs1", n1.getValue());
        PdfName n2 = resources.addExtGState(egs2);
        Assertions.assertEquals("Gs2", n2.getValue());
        n1 = resources.addExtGState(egs1);
        Assertions.assertEquals("Gs1", n1.getValue());

        document.close();
    }

    @Test
    public void resourcesTest2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(baos));
        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        resources.addExtGState(egs1);
        resources.addExtGState(egs2);
        document.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
        document = new PdfDocument(reader, new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream()));
        page = document.getPage(1);
        resources = page.getResources();
        Set<PdfName> names = resources.getResourceNames();
        Assertions.assertEquals(2, names.size());

        String[] expectedNames = { "Gs1", "Gs2"};
        int i = 0;
        for (PdfName name : names) {
            Assertions.assertEquals(expectedNames[i++], name.getValue());
        }

        PdfExtGState egs3 = new PdfExtGState();
        PdfName n3 = resources.addExtGState(egs3);
        Assertions.assertEquals("Gs3", n3.getValue());
        PdfDictionary egsResources = page.getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.ExtGState);
        PdfDictionary e1 = egsResources.getAsDictionary(new PdfName("Gs1"));
        PdfName n1 = resources.addExtGState(e1);
        Assertions.assertEquals("Gs1", n1.getValue());
        PdfDictionary e2 = egsResources.getAsDictionary(new PdfName("Gs2"));
        PdfName n2 = resources.addExtGState(e2);
        Assertions.assertEquals("Gs2", n2.getValue());
        PdfDictionary e4 = (PdfDictionary) e2.clone();
        PdfName n4 = resources.addExtGState(e4);
        Assertions.assertEquals("Gs4", n4.getValue());
        document.close();
    }

    @Test
    public void getNonExistentResourcesCategory() {
        PdfResources resources = new PdfResources();
        Set<PdfName> unknownResCategory = resources.getResourceNames(new PdfName("UnknownResCategory"));

        // Assert returned value is properly functioning
        PdfName randomResName = new PdfName("NonExistentResourceName");
        Assertions.assertFalse(unknownResCategory.contains(randomResName));
        Assertions.assertFalse(unknownResCategory.remove(randomResName));
        Assertions.assertTrue(unknownResCategory.isEmpty());
    }

    @Test
    public void resourcesCategoryDictionarySetModifiedTest() {
        PdfDictionary pdfDict = new PdfDictionary();
        pdfDict.put(PdfName.ExtGState, new PdfDictionary());
        PdfResources resources = new PdfResources(pdfDict);

        PdfObject resourceCategoryDict = resources.getPdfObject().get(PdfName.ExtGState);
        resourceCategoryDict.setIndirectReference(new PdfIndirectReference(null, 1));

        Assertions.assertFalse(resourceCategoryDict.isModified());
        resources.addExtGState(new PdfExtGState());
        // Check that when changing an existing resource category dictionary, the flag PdfObject.MODIFIED will be set for it
        Assertions.assertTrue(resourceCategoryDict.isModified());
    }
}
