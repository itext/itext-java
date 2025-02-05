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
package com.itextpdf.bouncycastlefips.pkcs;

import com.itextpdf.bouncycastlefips.asn1.pcks.PrivateKeyInfoBCFips;
import com.itextpdf.bouncycastlefips.operator.InputDecryptorProviderBCFips;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPrivateKeyInfo;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;
import com.itextpdf.commons.bouncycastle.pkcs.IPKCS8EncryptedPrivateKeyInfo;

import java.util.Objects;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

/**
 * Wrapper class for {@link PKCS8EncryptedPrivateKeyInfo}.
 */
public class PKCS8EncryptedPrivateKeyInfoBCFips implements IPKCS8EncryptedPrivateKeyInfo {
    private final PKCS8EncryptedPrivateKeyInfo privateKeyInfo;

    /**
     * Creates new wrapper instance for {@link PKCS8EncryptedPrivateKeyInfo}.
     *
     * @param privateKeyInfo {@link PKCS8EncryptedPrivateKeyInfo} to be wrapped
     */
    public PKCS8EncryptedPrivateKeyInfoBCFips(PKCS8EncryptedPrivateKeyInfo privateKeyInfo) {
        this.privateKeyInfo = privateKeyInfo;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PKCS8EncryptedPrivateKeyInfo}.
     */
    public PKCS8EncryptedPrivateKeyInfo getPrivateKeyInfo() {
        return privateKeyInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPrivateKeyInfo decryptPrivateKeyInfo(IInputDecryptorProvider decryptorProvider) throws PKCSExceptionBCFips {
        try {
            return new PrivateKeyInfoBCFips(privateKeyInfo.decryptPrivateKeyInfo(
                    ((InputDecryptorProviderBCFips) decryptorProvider).getDecryptorProvider()));
        } catch (PKCSException e) {
            throw new PKCSExceptionBCFips(e);
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
        PKCS8EncryptedPrivateKeyInfoBCFips that = (PKCS8EncryptedPrivateKeyInfoBCFips) o;
        return Objects.equals(privateKeyInfo, that.privateKeyInfo);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(privateKeyInfo);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return privateKeyInfo.toString();
    }
}
