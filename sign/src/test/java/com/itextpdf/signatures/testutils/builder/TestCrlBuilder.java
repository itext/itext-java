/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
