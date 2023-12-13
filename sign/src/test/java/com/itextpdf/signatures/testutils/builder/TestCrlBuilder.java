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
package com.itextpdf.signatures.testutils.builder;


import com.itextpdf.io.util.DateTimeUtil;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class TestCrlBuilder {

    private static final String SIGN_ALG = "SHA256withRSA";

    private X509v2CRLBuilder crlBuilder;
    private Date nextUpdate = DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), 30);

    public TestCrlBuilder(X509Certificate caCert, Date thisUpdate) throws CertificateEncodingException {
        X500Name issuerDN = new X500Name(PrincipalUtil.getIssuerX509Principal(caCert).getName());
        crlBuilder = new X509v2CRLBuilder(issuerDN, thisUpdate);
    }

    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    /**
     * See CRLReason
     */
    public void addCrlEntry(X509Certificate certificate, Date revocationDate, int reason) {
        crlBuilder.addCRLEntry(certificate.getSerialNumber(), revocationDate, reason);
    }

    public byte[] makeCrl(PrivateKey caPrivateKey) throws IOException, OperatorCreationException {
        ContentSigner signer = new JcaContentSignerBuilder(SIGN_ALG).setProvider(BouncyCastleProvider.PROVIDER_NAME).build(caPrivateKey);
        crlBuilder.setNextUpdate(nextUpdate);
        X509CRLHolder crl = crlBuilder.build(signer);
        return crl.getEncoded();
    }
}
