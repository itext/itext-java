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
package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.x509.ExtensionsBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.operator.ContentSignerBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Date;
import java.util.Objects;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.OCSPException;

/**
 * Wrapper class for {@link BasicOCSPRespBuilder}.
 */
public class BasicOCSPRespBuilderBCFips implements IBasicOCSPRespBuilder {
    private final BasicOCSPRespBuilder basicOCSPRespBuilder;

    /**
     * Creates new wrapper instance for {@link BasicOCSPRespBuilder}.
     *
     * @param basicOCSPRespBuilder {@link BasicOCSPRespBuilder} to be wrapped
     */
    public BasicOCSPRespBuilderBCFips(BasicOCSPRespBuilder basicOCSPRespBuilder) {
        this.basicOCSPRespBuilder = basicOCSPRespBuilder;
    }

    /**
     * Creates new wrapper instance for {@link BasicOCSPRespBuilder}.
     *
     * @param respID RespID wrapper to create {@link BasicOCSPRespBuilder} to be wrapped
     */
    public BasicOCSPRespBuilderBCFips(IRespID respID) {
        this(new BasicOCSPRespBuilder(((RespIDBCFips) respID).getRespID()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link BasicOCSPRespBuilder}.
     */
    public BasicOCSPRespBuilder getBasicOCSPRespBuilder() {
        return basicOCSPRespBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPRespBuilder setResponseExtensions(IExtensions extensions) {
        basicOCSPRespBuilder.setResponseExtensions(((ExtensionsBCFips) extensions).getExtensions());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPRespBuilder addResponse(ICertificateID certID, ICertificateStatus certificateStatus, Date time,
            Date time1, IExtensions extensions) {
        basicOCSPRespBuilder.addResponse(
                ((CertificateIDBCFips) certID).getCertificateID(),
                ((CertificateStatusBCFips) certificateStatus).getCertificateStatus(), time, time1,
                ((ExtensionsBCFips) extensions).getExtensions());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBasicOCSPResp build(IContentSigner signer, IX509CertificateHolder[] chain, Date time)
            throws OCSPExceptionBCFips {
        try {
            X509CertificateHolder[] certificateHolders = new X509CertificateHolder[chain.length];
            for (int i = 0; i < chain.length; ++i) {
                certificateHolders[i] = ((X509CertificateHolderBCFips) chain[i]).getCertificateHolder();
            }
            return new BasicOCSPRespBCFips(basicOCSPRespBuilder.build(
                    ((ContentSignerBCFips) signer).getContentSigner(), certificateHolders, time));
        } catch (OCSPException e) {
            throw new OCSPExceptionBCFips(e);
        }
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
        BasicOCSPRespBuilderBCFips that = (BasicOCSPRespBuilderBCFips) o;
        return Objects.equals(basicOCSPRespBuilder, that.basicOCSPRespBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(basicOCSPRespBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return basicOCSPRespBuilder.toString();
    }
}
