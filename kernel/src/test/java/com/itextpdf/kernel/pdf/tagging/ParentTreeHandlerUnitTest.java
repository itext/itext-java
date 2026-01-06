/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ParentTreeHandlerUnitTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    KernelLogMessageConstant.DUPLICATE_STRUCT_PARENT_INDEX_IN_TAGGED_OBJECT_REFERENCES, count = 1)
    })
    public void duplicateStructParentIndexFromForeignStructTreeIsIgnoredTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.setTagged();
            PdfPage page = pdfDoc.addNewPage();

            PdfStructTreeRoot structTreeRoot = pdfDoc.getStructTreeRoot();
            ParentTreeHandler handler = structTreeRoot.getParentTreeHandler();

            int structParentIndex = 10;

            PdfStructElem validParent = new PdfStructElem(pdfDoc, PdfName.Span, page);
            structTreeRoot.addKid(validParent);

            PdfDictionary validObj = new PdfDictionary();
            validObj.put(PdfName.StructParent, new PdfNumber(structParentIndex));

            PdfDictionary validObjRefDict = new PdfDictionary();
            validObjRefDict.put(PdfName.Pg, page.getPdfObject());
            validObjRefDict.put(PdfName.Obj, validObj);

            PdfObjRef validObjRef = new PdfObjRef(validObjRefDict, validParent);

            PdfDictionary foreignStructTreeRoot = new PdfDictionary();
            PdfDictionary foreignParentDict = new PdfDictionary();
            foreignParentDict.put(PdfName.P, foreignStructTreeRoot);
            PdfStructElem foreignParent = new PdfStructElem(foreignParentDict);

            PdfDictionary foreignObj = new PdfDictionary();
            foreignObj.put(PdfName.StructParent, new PdfNumber(structParentIndex));

            PdfDictionary foreignObjRefDict = new PdfDictionary();
            foreignObjRefDict.put(PdfName.Pg, page.getPdfObject());
            foreignObjRefDict.put(PdfName.Obj, foreignObj);

            PdfObjRef foreignObjRef = new PdfObjRef(foreignObjRefDict, foreignParent);

            handler.registerMcr(validObjRef);
            handler.registerMcr(foreignObjRef);

            PdfObjRef resolved = handler.findObjRefByStructParentIndex(page.getPdfObject(), structParentIndex);
            Assertions.assertNotNull(resolved);
            Assertions.assertSame(validObjRef.getPdfObject(), resolved.getPdfObject());
        }
    }
}
