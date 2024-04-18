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
package com.itextpdf.signatures.testutils.builder;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder;
import com.itextpdf.commons.bouncycastle.cert.IX509v2CRLBuilder;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.testutils.TimeTestUtil;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class TestCrlBuilder {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SIGN_ALG = "SHA256withRSA";

    private final PrivateKey issuerPrivateKey;
    private final IX509v2CRLBuilder crlBuilder;
    private Date nextUpdate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 30);

    public TestCrlBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey, Date thisUpdate)
            throws CertificateEncodingException {
        this.crlBuilder = FACTORY.createX509v2CRLBuilder(FACTORY.createX500Name(issuerCert), thisUpdate);
        this.issuerPrivateKey = issuerPrivateKey;
    }
    
    public TestCrlBuilder(X509Certificate issuerCert, PrivateKey issuerPrivateKey)
            throws CertificateEncodingException {
        this(issuerCert, issuerPrivateKey, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1));
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

    public void addCrlEntry(X509Certificate certificate, int reason) {
        crlBuilder.addCRLEntry(certificate.getSerialNumber(), nextUpdate, reason);
    }

    public void addExtension(IASN1ObjectIdentifier objectIdentifier, boolean isCritical,
                             IASN1Encodable extension) throws IOException {
        crlBuilder.addExtension(objectIdentifier, isCritical, extension);
    }

    public byte[] makeCrl() throws IOException, AbstractOperatorCreationException {
        IContentSigner signer =
                FACTORY.createJcaContentSignerBuilder(SIGN_ALG).setProvider(FACTORY.getProviderName())
                        .build(issuerPrivateKey);
        crlBuilder.setNextUpdate(nextUpdate);
        IX509CRLHolder crl = crlBuilder.build(signer);
        return crl.getEncoded();
    }
}
