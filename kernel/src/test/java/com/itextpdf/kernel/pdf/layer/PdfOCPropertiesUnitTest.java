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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Tag("UnitTest")
public class PdfOCPropertiesUnitTest {

    //TODO DEVSIX-8490 remove this test when implemented
    @Test
    public void removeOrderDuplicatesTest() throws IOException {
        byte[] docBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary ocgDic = new PdfDictionary();
                ocgDic.makeIndirect(document);
                PdfArray orderArray = new PdfArray();
                for (int i = 0; i < 3; i++) {
                    orderArray.add(ocgDic);
                }

                PdfDictionary ocgDic2 = new PdfDictionary();
                ocgDic.makeIndirect(document);
                for (int i = 0; i < 3; i++) {
                    PdfArray layerArray = new PdfArray();
                    layerArray.add(new PdfString("layerName" + i));
                    layerArray.add(ocgDic2);
                    orderArray.add(layerArray);
                }

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray OCGsArray = new PdfArray();
                OCGsArray.add(ocgDic);
                OCGsArray.add(ocgDic2);

                PdfDictionary OCPropertiesDic = new PdfDictionary();
                OCPropertiesDic.put(PdfName.D, DDictionary);
                OCPropertiesDic.put(PdfName.OCGs, OCGsArray);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, OCPropertiesDic);

                document.getCatalog().getOCProperties(false);

            }
            docBytes = outputStream.toByteArray();
        }

        try (PdfDocument docReopen = new PdfDocument(new PdfReader(new ByteArrayInputStream(docBytes)))) {
            PdfArray resultArray = docReopen.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties)
                    .getAsDictionary(PdfName.D).getAsArray(PdfName.Order);
            Assertions.assertEquals(2, resultArray.size());
        }
    }

    //TODO DEVSIX-8490 remove this test when implemented
    @Test
    public void removeOrderDuplicateHasChildTest() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (PdfDocument document = new PdfDocument(new PdfWriter(outputStream))) {
                PdfDictionary ocgDic = new PdfDictionary();
                PdfDictionary ocgDicChild1 = new PdfDictionary();
                PdfDictionary ocgDicChild2 = new PdfDictionary();
                ocgDic.makeIndirect(document);

                PdfArray orderArray = new PdfArray();
                PdfArray childArray1 = new PdfArray();
                childArray1.add(ocgDicChild1);
                PdfArray childArray2 = new PdfArray();
                childArray2.add(ocgDicChild2);

                orderArray.add(ocgDic);
                orderArray.add(childArray1);
                orderArray.add(ocgDic);
                orderArray.add(childArray2);

                PdfDictionary DDictionary = new PdfDictionary();
                DDictionary.put(PdfName.Order, orderArray);
                PdfArray OCGsArray = new PdfArray();
                OCGsArray.add(ocgDic);
                OCGsArray.add(ocgDicChild1);
                OCGsArray.add(ocgDicChild2);

                PdfDictionary OCPropertiesDic = new PdfDictionary();
                OCPropertiesDic.put(PdfName.D, DDictionary);
                OCPropertiesDic.put(PdfName.OCGs, OCGsArray);
                document.getCatalog().getPdfObject().put(PdfName.OCProperties, OCPropertiesDic);

                PdfIndirectReference ref = ocgDic.getIndirectReference();
                PdfCatalog catalog = document.getCatalog();
                Exception e = Assertions.assertThrows(PdfException.class, () -> catalog.getOCProperties(false));
                Assertions.assertEquals(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.UNABLE_TO_REMOVE_DUPLICATE_LAYER, ref.toString()), e.getMessage());
            }
        }
    }
}
