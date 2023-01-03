/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class CertificateInfoTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String EXPECTED_EXCEPTION_MESSAGE = FACTORY.getBouncyCastleFactoryTestUtil().getCertificateInfoTestConst();

    @Test
    public void X500InvalidDirectoryConstructorTest() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new CertificateInfo.X500Name("some_dir"));
    }

    @Test
    public void X500ValidDirectoryConstructorTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=dir,another=dir,some=value");

        Map<String, List<String>> values = name.getFields();
        Assert.assertEquals(2, values.size());

        Assert.assertEquals(Arrays.asList("dir", "value"), values.get("SOME"));
        Assert.assertEquals(Collections.singletonList("dir"), values.get("ANOTHER"));
    }

    @Test
    public void X500GetFieldTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assert.assertEquals("value", name.getField("SOME"));
        Assert.assertEquals("dir", name.getField("ANOTHER"));
    }

    @Test
    public void X500GetFieldArrayTest() {
        CertificateInfo.X500Name name = new CertificateInfo.X500Name("some=value,another=dir,some=dir");

        Assert.assertEquals(Arrays.asList("value", "dir"), name.getFieldArray("SOME"));
        Assert.assertEquals(Collections.singletonList("dir"), name.getFieldArray("ANOTHER"));
    }

    @Test
    public void X509NameTokenizerNextTokenComplicatedTest() {
        CertificateInfo.X509NameTokenizer tokenizer = new CertificateInfo.X509NameTokenizer("quoted\",\"comma=escaped\\,comma_escaped\\\"quote");
        String token = tokenizer.nextToken();

        Assert.assertEquals("quoted,comma=escaped,comma_escaped\"quote", token);
        Assert.assertNull(tokenizer.nextToken());
    }

    @Test
    public void getIssuerFieldsExceptionTest() {
        Exception exception =
                Assert.assertThrows(PdfException.class, () -> CertificateInfo.getIssuer(new byte[] {4, 8, 15, 16, 23, 42}));
        Assert.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getCause().getMessage());
    }

    @Test
    public void getSubjectExceptionTest() {
        Exception exception =
                Assert.assertThrows(PdfException.class, () -> CertificateInfo.getSubject(new byte[] {4, 8, 15, 16, 23, 42}));
        Assert.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getCause().getMessage());
    }
}
