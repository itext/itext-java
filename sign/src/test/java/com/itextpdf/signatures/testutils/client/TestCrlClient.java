/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TestCrlClient implements ICrlClient {

    private final List<TestCrlBuilder> crlBuilders;

    public TestCrlClient() {
        crlBuilders = new ArrayList<>();
    }

    public TestCrlClient addBuilderForCertIssuer(TestCrlBuilder crlBuilder) {
        crlBuilders.add(crlBuilder);
        return this;
    }

    public TestCrlClient addBuilderForCertIssuer(X509Certificate issuerCert, PrivateKey issuerPrivateKey)
            throws CertificateEncodingException, IOException {
        Date yesterday = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1);
        crlBuilders.add(new TestCrlBuilder(issuerCert, issuerPrivateKey, yesterday));
        return this;
    }

    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        return crlBuilders.stream()
                .map(testCrlBuilder -> {
                    try {
                        return testCrlBuilder.makeCrl();
                    } catch (Exception ignore) {
                        throw new PdfException(ignore);
                    }
                })
                .collect(Collectors.toList());
    }
}
