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
package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentVerifierProviderBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.SingleResp;

/**
 * Wrapper class for {@link BasicOCSPResp}.
 */
public class BasicOCSPRespBCFips implements IBasicOCSPResp {
    private final BasicOCSPResp basicOCSPResp;

    /**
     * Creates new wrapper instance for {@link BasicOCSPResp}.
     *
     * @param basicOCSPResp {@link BasicOCSPResp} to be wrapped
     */
    public BasicOCSPRespBCFips(BasicOCSPResp basicOCSPResp) {
        this.basicOCSPResp = basicOCSPResp;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link BasicOCSPResp}.
     */
    public BasicOCSPResp getBasicOCSPResp() {
        return basicOCSPResp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISingleResp[] getResponses() {
        SingleResp[] resps = basicOCSPResp.getResponses();
        ISingleResp[] respsBCFips = new ISingleResp[resps.length];
        for (int i = 0; i < respsBCFips.length; i++) {
            respsBCFips[i] = new SingleRespBCFips(resps[i]);
        }
        return respsBCFips;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSignatureValid(IContentVerifierProvider provider) throws OCSPExceptionBCFips {
        try {
            return basicOCSPResp.isSignatureValid(((ContentVerifierProviderBCFips) provider).getProvider());
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX509CertificateHolder[] getCerts() {
        X509CertificateHolder[] certs = basicOCSPResp.getCerts();
        if (certs == null) {
            return new IX509CertificateHolder[0];
        }
        IX509CertificateHolder[] certsBCFips = new IX509CertificateHolder[certs.length];
        for (int i = 0; i < certs.length; i++) {
            certsBCFips[i] = new X509CertificateHolderBCFips(certs[i]);
        }
        return certsBCFips;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() throws IOException {
        return basicOCSPResp.getEncoded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getProducedAt() {
        return basicOCSPResp.getProducedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable getExtensionParsedValue(IASN1ObjectIdentifier objectIdentifier) {
        Extension extension =
                basicOCSPResp.getExtension(((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier());
        return new ASN1EncodableBCFips(extension == null ? null : extension.getParsedValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRespID getResponderId() {
        return new RespIDBCFips(basicOCSPResp.getResponderId());
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicOCSPRespBCFips that = (BasicOCSPRespBCFips) o;
        return Objects.equals(basicOCSPResp, that.basicOCSPResp);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(basicOCSPResp);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return basicOCSPResp.toString();
    }
}
