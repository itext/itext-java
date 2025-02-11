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
package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * This interface represents the wrapper for JcaX509CertificateConverter that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaX509CertificateConverter {
    /**
     * Calls actual {@code getCertificate} method for the wrapped JcaX509CertificateConverter object.
     *
     * @param certificateHolder X509CertificateHolder wrapper
     *
     * @return received X509Certificate.
     *
     * @throws CertificateException indicates certificate problems.
     */
    X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException;

    /**
     * Calls actual {@code setProvider} method for the wrapped JcaX509CertificateConverter object.
     *
     * @param provider provider to set
     *
     * @return {@link IJcaX509CertificateConverter} this wrapped object.
     */
    IJcaX509CertificateConverter setProvider(Provider provider);
}
