/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures.testutils.client;

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

public class TestCrlClient implements ICrlClient {

    private final TestCrlBuilder crlBuilder;
    private final PrivateKey caPrivateKey;

    public TestCrlClient(TestCrlBuilder crlBuilder, PrivateKey caPrivateKey) throws CertificateEncodingException {
        this.crlBuilder = crlBuilder;
        this.caPrivateKey = caPrivateKey;
    }

    public TestCrlClient(X509Certificate caCert, PrivateKey caPrivateKey) throws CertificateEncodingException {
        this.crlBuilder = new TestCrlBuilder(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -1));
        this.caPrivateKey = caPrivateKey;
    }

    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        Collection<byte[]> crls = null;
        try {
            byte[] crl = crlBuilder.makeCrl(caPrivateKey);
            crls = Collections.singletonList(crl);
        } catch (Exception ignore) {
            throw new PdfException(ignore);
        }
        return crls;
    }
}
