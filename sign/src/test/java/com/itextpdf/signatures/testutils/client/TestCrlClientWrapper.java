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
package com.itextpdf.signatures.testutils.client;

import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.ICrlClient;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestCrlClientWrapper implements ICrlClient {

    private final ICrlClient wrappedClient;
    private final List<CrlClientCall> calls = new ArrayList<>();

    public TestCrlClientWrapper(ICrlClient wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) throws CertificateEncodingException {
        Collection<byte[]> crlBytesCollection = wrappedClient.getEncoded(checkCert, url);
        List<X509CRL> crlResponses = new ArrayList<>();
        for (byte[] crlBytes : crlBytesCollection) {
            try {
                crlResponses.add((X509CRL) CertificateUtil.parseCrlFromStream(
                        new ByteArrayInputStream(crlBytes)));
            } catch (Exception e) {
                throw new RuntimeException("Deserializing CRL response failed",e);
            }
        }
        calls.add(new CrlClientCall(checkCert, url, crlResponses));
        return crlBytesCollection;
    }

    public List<CrlClientCall> getCalls() {
        return calls;
    }

    public static class CrlClientCall {
        public final X509Certificate checkCert;
        public final String url;
        public final List<X509CRL> responses;

        public CrlClientCall(X509Certificate checkCert, String url, List<X509CRL> responses) {
            this.checkCert = checkCert;
            this.url = url;
            this.responses = responses;
        }
    }
}
