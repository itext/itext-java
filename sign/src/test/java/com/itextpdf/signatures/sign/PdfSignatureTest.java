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
package com.itextpdf.signatures.sign;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.PdfSignatureApp;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfSignatureTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureTest/";

    @Test
    public void setByteRangeTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "simpleSignature.pdf");
        int[] byteRange = {0, 141, 16526, 2494};
        signature.setByteRange(byteRange);

        PdfArray expected = new PdfArray((new int[] {0, 141, 16526, 2494}));
        Assertions.assertArrayEquals(expected.toIntArray(), signature.getByteRange().toIntArray());
    }

    @Test
    public void setContentsTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "simpleSignature.pdf");

        byte[] newContents = new PdfString("new iText signature").getValueBytes();
        signature.setContents(newContents);

        Assertions.assertEquals("new iText signature", signature.getContents().getValue());
    }

    @Test
    public void setAndGetCertTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "adbe.x509.rsa_sha1_signature.pdf");

        byte[] certChain = new PdfString("Hello, iText!!").getValueBytes();
        signature.setCert(certChain);

        Assertions.assertEquals("Hello, iText!!", signature.getCertObject().toString());
    }

    @Test
    public void getCertObjectTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "adbe.x509.rsa_sha1_signature.pdf");

        Assertions.assertTrue(signature.getCertObject().isArray());
    }

    @Test
    public void setAndGetNameTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "simpleSignature.pdf");
        Assertions.assertNull(signature.getName());

        String name = "iText person";
        signature.setName(name);

        Assertions.assertEquals(name, signature.getName());
    }

    @Test
    public void setSignatureCreatorTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "noPropBuilds.pdf");
        Assertions.assertNull(signature.getPdfObject().getAsDictionary(PdfName.Prop_Build));

        signature.setSignatureCreator("iText.Name");
        String propBuild = signature.getPdfObject().getAsDictionary(PdfName.Prop_Build)
                .getAsDictionary(PdfName.App).getAsName(PdfName.Name).getValue();

        Assertions.assertEquals("iText.Name", propBuild);
    }

    @Test
    public void pdfSignatureAppDefaultConstructorTest() {
        PdfSignatureApp signatureApp = new PdfSignatureApp();
        Assertions.assertTrue(signatureApp.getPdfObject().isDictionary());
    }

    @Test
    public void certAsArrayNotStringTest() throws IOException {
        PdfSignature signature = getTestSignature(sourceFolder + "adbe.x509.rsa_sha1_signature.pdf");
        PdfObject certObject = signature.getCertObject();

        Assertions.assertTrue(certObject instanceof PdfArray);
        Assertions.assertNull(signature.getCert());
    }

    private static PdfSignature getTestSignature(String pathToPdf) throws IOException {
        try (PdfDocument doc = new PdfDocument(new PdfReader(pathToPdf))) {
            SignatureUtil sigUtil = new SignatureUtil(doc);
            return sigUtil.getSignature("Signature1");
        }
    }
}
