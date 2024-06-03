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
package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers;

import java.util.Objects;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;

/**
 * Wrapper class for {@link OCSPObjectIdentifiers}.
 */
public class OCSPObjectIdentifiersBC implements IOCSPObjectIdentifiers {
    private static final OCSPObjectIdentifiersBC INSTANCE = new OCSPObjectIdentifiersBC(null);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_BASIC =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_basic);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NONCE =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_NOCHECK =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck);

    private static final IASN1ObjectIdentifier ID_PKIX_OCSP_ARCHIVE_CUTOFF =
            new ASN1ObjectIdentifierBC(OCSPObjectIdentifiers.id_pkix_ocsp_archive_cutoff);

    private final OCSPObjectIdentifiers ocspObjectIdentifiers;

    /**
     * Creates new wrapper instance for {@link OCSPObjectIdentifiers}.
     *
     * @param ocspObjectIdentifiers {@link OCSPObjectIdentifiers} to be wrapped
     */
    public OCSPObjectIdentifiersBC(OCSPObjectIdentifiers ocspObjectIdentifiers) {
        this.ocspObjectIdentifiers = ocspObjectIdentifiers;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPObjectIdentifiersBC} instance.
     */
    public static OCSPObjectIdentifiersBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPObjectIdentifiers}.
     */
    public OCSPObjectIdentifiers getOCSPObjectIdentifiers() {
        return ocspObjectIdentifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdPkixOcspBasic() {
        return ID_PKIX_OCSP_BASIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdPkixOcspNonce() {
        return ID_PKIX_OCSP_NONCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdPkixOcspNoCheck() {
        return ID_PKIX_OCSP_NOCHECK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdPkixOcspArchiveCutoff() {
        return ID_PKIX_OCSP_ARCHIVE_CUTOFF;
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
        OCSPObjectIdentifiersBC that = (OCSPObjectIdentifiersBC) o;
        return Objects.equals(ocspObjectIdentifiers, that.ocspObjectIdentifiers);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ocspObjectIdentifiers);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return ocspObjectIdentifiers.toString();
    }
}
