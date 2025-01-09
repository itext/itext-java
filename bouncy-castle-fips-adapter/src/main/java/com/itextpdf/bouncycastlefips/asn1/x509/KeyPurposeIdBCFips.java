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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId;

import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * Wrapper class for {@link KeyPurposeId}.
 */
public class KeyPurposeIdBCFips extends ASN1EncodableBCFips implements IKeyPurposeId {
    private static final KeyPurposeIdBCFips INSTANCE = new KeyPurposeIdBCFips(null);

    private static final KeyPurposeIdBCFips ID_KP_OCSP_SIGNING = new KeyPurposeIdBCFips(KeyPurposeId.id_kp_OCSPSigning);

    /**
     * Creates new wrapper instance for {@link KeyPurposeId}.
     *
     * @param keyPurposeId {@link KeyPurposeId} to be wrapped
     */
    public KeyPurposeIdBCFips(KeyPurposeId keyPurposeId) {
        super(keyPurposeId);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link KeyPurposeIdBCFips} instance.
     */
    public static KeyPurposeIdBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link KeyPurposeId}.
     */
    public KeyPurposeId getKeyPurposeId() {
        return (KeyPurposeId) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IKeyPurposeId getIdKpOCSPSigning() {
        return ID_KP_OCSP_SIGNING;
    }
}
