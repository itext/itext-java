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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Tag("UnitTest")
public class PdfOCPropertiesUnitTest {

    @Test
    public void orderArrayOcgWithTwoParentsTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary parentOcg1 = new PdfDictionary();
                parentOcg1.put(PdfName.Name, new PdfString("Parent1"));
                parentOcg1.put(PdfName.Type, PdfName.OCG);
                parentOcg1.makeIndirect(document);
                PdfArray orderArray = new PdfArray();
                orderArray.add(parentOcg1);

                PdfDictionary childOcg = new PdfDictionary();
                childOcg.put(PdfName.Name, new PdfString("child"));
                childOcg.put(PdfName.Type, PdfName.OCG);
                childOcg.makeIndirect(document);
                PdfArray childArray = new PdfArray();
                childArray.add(childOcg);
                orderArray.add(childArray);

                PdfDictionary parentOcg2 = new PdfDictionary();
                parentOcg2.put(PdfName.Name, new PdfString("Parent2"));
                parentOcg2.put(PdfName.Type, PdfName.OCG);
                parentOcg2.makeIndirect(document);
                orderArray.add(parentOcg2);
                orderArray.add(new PdfArray(childArray));

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray ocgArray = new PdfArray();
                ocgArray.add(parentOcg1);
                ocgArray.add(parentOcg2);
                ocgArray.add(childOcg);

                PdfDictionary OCPropertiesDic = new PdfDictionary();
                OCPropertiesDic.put(PdfName.D, DDictionary);
                OCPropertiesDic.put(PdfName.OCGs, ocgArray);
                OCPropertiesDic.makeIndirect(document);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, OCPropertiesDic);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            List<PdfLayer> layers = docReopen.getCatalog().getOCProperties(false).getLayers();
            Assertions.assertEquals(3, layers.size());
            Assertions.assertEquals(1, layers.get(0).getChildren().size());
            Assertions.assertEquals(2, layers.get(1).getParents().size());
            Assertions.assertEquals(1, layers.get(2).getChildren().size());
        }
    }

    @Test
    public void orderArrayOcgWithTwoTitleParentsTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary childOcg = new PdfDictionary();
                childOcg.put(PdfName.Name, new PdfString("child"));
                childOcg.put(PdfName.Type, PdfName.OCG);
                childOcg.makeIndirect(document);
                PdfArray childArray = new PdfArray();
                childArray.add(childOcg);

                PdfArray titleOcg1 = new PdfArray();
                titleOcg1.add(new PdfString("parent title layer 1"));
                titleOcg1.add(childArray);
                PdfArray orderArray = new PdfArray();
                orderArray.add(titleOcg1);

                PdfArray titleOcg2 = new PdfArray();
                titleOcg2.add(new PdfString("parent title 2"));
                titleOcg2.add(childArray);
                orderArray.add(titleOcg2);

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);

                PdfArray ocgArray = new PdfArray();
                ocgArray.add(childOcg);

                PdfDictionary ocPropertiesDic = new PdfDictionary();
                ocPropertiesDic.put(PdfName.D, DDictionary);
                ocPropertiesDic.put(PdfName.OCGs, ocgArray);
                ocPropertiesDic.makeIndirect(document);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, ocPropertiesDic);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)),
                new PdfWriter(new ByteArrayOutputStream()))) {
            List<PdfLayer> layers = docReopen.getCatalog().getOCProperties(false).getLayers();
            Assertions.assertEquals(3, layers.size());
            Assertions.assertEquals(1, layers.get(0).getChildren().size());
            Assertions.assertEquals(2, layers.get(1).getParents().size());
            Assertions.assertEquals(1, layers.get(2).getChildren().size());
        }
    }

    @Test
    public void orderArrayTitleOcgWithTwoParentsTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary parentOcg1 = new PdfDictionary();
                parentOcg1.put(PdfName.Name, new PdfString("Parent1"));
                parentOcg1.put(PdfName.Type, PdfName.OCG);
                parentOcg1.makeIndirect(document);
                PdfArray orderArray = new PdfArray();
                orderArray.add(parentOcg1);

                PdfArray titleChildOcg = new PdfArray();
                titleChildOcg.add(new PdfString("child title layer"));
                PdfArray childArray = new PdfArray();
                childArray.add(titleChildOcg);
                orderArray.add(childArray);

                PdfDictionary parentOcg2 = new PdfDictionary();
                parentOcg2.put(PdfName.Name, new PdfString("Parent2"));
                parentOcg2.put(PdfName.Type, PdfName.OCG);
                parentOcg2.makeIndirect(document);
                orderArray.add(parentOcg2);
                orderArray.add(new PdfArray(childArray));

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray ocgArray = new PdfArray();
                ocgArray.add(parentOcg1);
                ocgArray.add(parentOcg2);

                PdfDictionary ocPropertiesDic = new PdfDictionary();
                ocPropertiesDic.put(PdfName.D, DDictionary);
                ocPropertiesDic.put(PdfName.OCGs, ocgArray);
                ocPropertiesDic.makeIndirect(document);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, ocPropertiesDic);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)),
                new PdfWriter(new ByteArrayOutputStream()))) {
            List<PdfLayer> layers = docReopen.getCatalog().getOCProperties(false).getLayers();
            Assertions.assertEquals(3, layers.size());
            Assertions.assertEquals(1, layers.get(0).getChildren().size());
            Assertions.assertEquals(2, layers.get(1).getParents().size());
            Assertions.assertEquals(1, layers.get(2).getChildren().size());
        }
    }

    @Test
    public void orderArrayDuplicatedOcgTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary ocg = new PdfDictionary();
                ocg.put(PdfName.Name, new PdfString("ocg"));
                ocg.put(PdfName.Type, PdfName.OCG);
                ocg.makeIndirect(document);
                PdfArray orderArray = new PdfArray();
                orderArray.add(ocg);
                orderArray.add(ocg);
                orderArray.add(ocg);

                PdfDictionary parentOcg = new PdfDictionary();
                parentOcg.put(PdfName.Name, new PdfString("Parent"));
                parentOcg.put(PdfName.Type, PdfName.OCG);
                parentOcg.makeIndirect(document);
                orderArray.add(parentOcg);
                orderArray.add(new PdfArray(ocg));

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray ocgArray = new PdfArray();
                ocgArray.add(ocg);
                ocgArray.add(parentOcg);

                PdfDictionary ocPropertiesDic = new PdfDictionary();
                ocPropertiesDic.put(PdfName.D, DDictionary);
                ocPropertiesDic.put(PdfName.OCGs, ocgArray);
                ocPropertiesDic.makeIndirect(document);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, ocPropertiesDic);
            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)),
                new PdfWriter(new ByteArrayOutputStream()))) {
            List<PdfLayer> layers = docReopen.getCatalog().getOCProperties(false).getLayers();
            Assertions.assertEquals(2, layers.size());
            Assertions.assertEquals(1, layers.get(0).getParents().size());
            Assertions.assertEquals(1, layers.get(1).getChildren().size());
        }
    }

    @Test
    public void orderArrayDuplicatedTitleOcgTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfArray orderArray = new PdfArray();
                PdfArray titleOcg = new PdfArray();
                titleOcg.add(new PdfString("title layer"));
                orderArray.add(titleOcg);

                PdfDictionary parentOcg = new PdfDictionary();
                parentOcg.put(PdfName.Name, new PdfString("Parent"));
                parentOcg.put(PdfName.Type, PdfName.OCG);
                parentOcg.makeIndirect(document);
                orderArray.add(parentOcg);

                PdfArray nestedOcg = new PdfArray();
                nestedOcg.add(titleOcg);
                orderArray.add(nestedOcg);
                orderArray.add(titleOcg);

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray ocgArray = new PdfArray();
                ocgArray.add(parentOcg);

                PdfDictionary ocPropertiesDic = new PdfDictionary();
                ocPropertiesDic.put(PdfName.D, DDictionary);
                ocPropertiesDic.put(PdfName.OCGs, ocgArray);
                ocPropertiesDic.makeIndirect(document);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, ocPropertiesDic);
            }
            docBytes = outputStream.toByteArray();
        }
        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)),
                new PdfWriter(new ByteArrayOutputStream()))) {
            List<PdfLayer> layers = docReopen.getCatalog().getOCProperties(false).getLayers();
            Assertions.assertEquals(2, layers.size());
            Assertions.assertEquals("title layer", layers.get(0).getTitle());
            Assertions.assertEquals(1, layers.get(0).getParents().size());
            Assertions.assertEquals(1, layers.get(1).getChildren().size());
        }
    }
}
