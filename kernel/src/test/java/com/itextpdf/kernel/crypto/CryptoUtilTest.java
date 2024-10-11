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
package com.itextpdf.kernel.crypto;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class CryptoUtilTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void createBerStreamTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IASN1OutputStream stream = CryptoUtil.createAsn1OutputStream(baos, FACTORY.createASN1Encoding().getBer());
        Assertions.assertNotNull(stream);
    }

    @Test
    public void createDerStreamTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IASN1OutputStream stream = CryptoUtil.createAsn1OutputStream(baos, FACTORY.createASN1Encoding().getDer());
        Assertions.assertNotNull(stream);
    }

    @Test
    public void createUnsupportedEncodingStreamTest() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> CryptoUtil.createAsn1OutputStream(baos, "DL")
        );
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNSUPPORTED_ASN1_ENCODING, "DL"),
                e.getMessage());
    }
}
