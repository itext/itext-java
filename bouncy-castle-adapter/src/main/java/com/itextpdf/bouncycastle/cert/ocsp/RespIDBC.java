/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.bouncycastle.asn1.x509.SubjectPublicKeyInfoBC;
import com.itextpdf.bouncycastle.operator.OperatorCreationExceptionBC;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRespID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IResponderID;

import java.util.Objects;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/**
 * Wrapper class for {@link RespID}.
 */
public class RespIDBC implements IRespID {
    private final RespID respID;

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param respID {@link RespID} to be wrapped
     */
    public RespIDBC(RespID respID) {
        this.respID = respID;
    }

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param x500Name X500Name wrapper to create {@link RespID}
     */
    public RespIDBC(IX500Name x500Name) {
        this(new RespID(((X500NameBC) x500Name).getX500Name()));
    }

    /**
     * Creates new wrapper instance for {@link RespID}.
     *
     * @param subjectPublicKeyInfo {@link org.bouncycastle.asn1.x509.SubjectPublicKeyInfo} wrapper
     *
     * @throws AbstractOCSPException in case of {@link OCSPException}
     * @throws AbstractOperatorCreationException in case of {@link OperatorCreationException}
     */
    public RespIDBC(SubjectPublicKeyInfoBC subjectPublicKeyInfo) throws AbstractOCSPException,
            AbstractOperatorCreationException {
        try {
            this.respID = new RespID(subjectPublicKeyInfo.getSubjectPublicKeyInfo(),
                    new JcaDigestCalculatorProviderBuilder().build().get(CertificateID.HASH_SHA1));
        } catch (OCSPException e) {
            throw new OCSPExceptionBC(e);
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBC(e);
        }
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RespID}.
     */
    public RespID getRespID() {
        return respID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IResponderID toASN1Primitive() {
        return new ResponderIDBC(respID.toASN1Primitive());
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
        RespIDBC that = (RespIDBC) o;
        return Objects.equals(respID, that.respID);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(respID);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return respID.toString();
    }
}
