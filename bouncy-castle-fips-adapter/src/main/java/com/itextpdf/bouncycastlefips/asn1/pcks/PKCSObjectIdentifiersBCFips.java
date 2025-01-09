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
package com.itextpdf.bouncycastlefips.asn1.pcks;

import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers;

import java.util.Objects;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

/**
 * Wrapper class for {@link PKCSObjectIdentifiers}.
 */
public class PKCSObjectIdentifiersBCFips implements IPKCSObjectIdentifiers {
    private static final PKCSObjectIdentifiersBCFips INSTANCE = new PKCSObjectIdentifiersBCFips(null);

    private static final ASN1ObjectIdentifierBCFips ID_AA_ETS_SIG_POLICY_ID = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.id_aa_ets_sigPolicyId);

    private static final ASN1ObjectIdentifierBCFips ID_AA_SIGNATURE_TIME_STAMP_TOKEN = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

    private static final ASN1ObjectIdentifierBCFips ID_SPQ_ETS_URI =
            new ASN1ObjectIdentifierBCFips(PKCSObjectIdentifiers.id_spq_ets_uri);

    private static final ASN1ObjectIdentifierBCFips ENVELOPED_DATA = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.envelopedData);

    private static final ASN1ObjectIdentifierBCFips DATA = new ASN1ObjectIdentifierBCFips(
            PKCSObjectIdentifiers.data);

    private final PKCSObjectIdentifiers pkcsObjectIdentifiers;

    /**
     * Creates new wrapper instance for {@link PKCSObjectIdentifiers}.
     *
     * @param pkcsObjectIdentifiers {@link PKCSObjectIdentifiers} to be wrapped
     */
    public PKCSObjectIdentifiersBCFips(PKCSObjectIdentifiers pkcsObjectIdentifiers) {
        this.pkcsObjectIdentifiers = pkcsObjectIdentifiers;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link PKCSObjectIdentifiersBCFips} instance.
     */
    public static PKCSObjectIdentifiersBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PKCSObjectIdentifiers}.
     */
    public PKCSObjectIdentifiers getPkcsObjectIdentifiers() {
        return pkcsObjectIdentifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdAaSignatureTimeStampToken() {
        return ID_AA_SIGNATURE_TIME_STAMP_TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdAaEtsSigPolicyId() {
        return ID_AA_ETS_SIG_POLICY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getIdSpqEtsUri() {
        return ID_SPQ_ETS_URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getEnvelopedData() {
        return ENVELOPED_DATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getData() {
        return DATA;
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
        PKCSObjectIdentifiersBCFips that = (PKCSObjectIdentifiersBCFips) o;
        return Objects.equals(pkcsObjectIdentifiers, that.pkcsObjectIdentifiers);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pkcsObjectIdentifiers);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return pkcsObjectIdentifiers.toString();
    }
}
