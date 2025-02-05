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
package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateHolder;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/**
 * Wrapper class for {@link JcaX509CertificateHolder}.
 */
public class JcaX509CertificateHolderBCFips extends X509CertificateHolderBCFips implements IJcaX509CertificateHolder {
    /**
     * Creates new wrapper instance for {@link JcaX509CertificateHolder}.
     *
     * @param certificateHolder {@link JcaX509CertificateHolder} to be wrapped
     */
    public JcaX509CertificateHolderBCFips(JcaX509CertificateHolder certificateHolder) {
        super(certificateHolder);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509CertificateHolder}.
     */
    public JcaX509CertificateHolder getJcaCertificateHolder() {
        return (JcaX509CertificateHolder) getCertificateHolder();
    }
}
