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
package com.itextpdf.nativeimage;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

class BouncyCastleTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/nativeimage/BouncyCastleTest/";

    @Test
    void readEncryptedDocument() throws IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "encrypted.pdf",
                new ReaderProperties().setPassword("123".getBytes()));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfDictionary form = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);

        PdfDictionary field = form.getAsArray(PdfName.Fields).getAsDictionary(0);

        Assertions.assertEquals("ch", field.getAsString(PdfName.T).toUnicodeString());
        Assertions.assertEquals("SomeStringValueInDictionary",
                field.getAsDictionary(new PdfName("TestDic")).getAsString(new PdfName("TestString")).toUnicodeString());
        Assertions.assertEquals("SomeStringValueInArray",
                field.getAsArray(new PdfName("TestArray")).getAsString(0).toUnicodeString());

        pdfDocument.close();
    }

    // By some reason it fails in native mode (works on java) on build agent so here we disable
    @DisabledInNativeImage
    @Test
    void readSignature() throws IOException, GeneralSecurityException {
        String filePath = SOURCE_FOLDER + "isa.pdf";
        String signatureName = "Signature1";

        PdfDocument document = new PdfDocument(new PdfReader(filePath));
        SignatureUtil sigUtil = new SignatureUtil(document);
        PdfPKCS7 pdfPKCS7 = sigUtil.readSignatureData(signatureName);

        Assertions.assertTrue(pdfPKCS7.verifySignatureIntegrityAndAuthenticity());
        Assertions.assertFalse(sigUtil.signatureCoversWholeDocument(signatureName));

        document.close();
    }
}
