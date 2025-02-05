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
package com.itextpdf.bouncycastlefips.openssl;

import com.itextpdf.bouncycastlefips.asn1.pcks.PrivateKeyInfoBCFips;
import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.bouncycastlefips.pkcs.PKCS8EncryptedPrivateKeyInfoBCFips;
import com.itextpdf.commons.bouncycastle.openssl.IPEMParser;

import java.io.IOException;
import java.util.Objects;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

/**
 * Wrapper class for {@link PEMParser}.
 */
public class PEMParserBCFips implements IPEMParser {
    private final PEMParser parser;

    /**
     * Creates new wrapper instance for {@link PEMParser}.
     *
     * @param parser {@link PEMParser} to be wrapped
     */
    public PEMParserBCFips(PEMParser parser) {
        this.parser = parser;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link PEMParser}.
     */
    public PEMParser getParser() {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readObject() throws IOException {
        Object readObject = parser.readObject();
        if (readObject instanceof X509CertificateHolder) {
            return new X509CertificateHolderBCFips((X509CertificateHolder) readObject);
        }
        if (readObject instanceof PKCS8EncryptedPrivateKeyInfo) {
            return new PKCS8EncryptedPrivateKeyInfoBCFips((PKCS8EncryptedPrivateKeyInfo) readObject);
        }
        if (readObject instanceof PrivateKeyInfo) {
            return new PrivateKeyInfoBCFips((PrivateKeyInfo) readObject);
        }
        return readObject;
    }

    /**
     * Delegates {@code close} method call to the wrapped parser.
     */
    @Override
    public void close() throws IOException {
        parser.close();
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
        PEMParserBCFips that = (PEMParserBCFips) o;
        return Objects.equals(parser, that.parser);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(parser);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return parser.toString();
    }
}
