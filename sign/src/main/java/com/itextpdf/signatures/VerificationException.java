/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import com.itextpdf.io.util.MessageFormatUtil;

/**
 * An exception that is thrown when something is wrong with a certificate.
 */
public class VerificationException extends GeneralSecurityException {

    private static final long serialVersionUID = 2978604513926438256L;

    /**
     * Creates a VerificationException
     *
     * @param cert is a failed certificate
     * @param message is a reason of failure
     */
    public VerificationException(Certificate cert, String message) {
        super(MessageFormatUtil.format("Certificate {0} failed: {1}", cert == null ? "Unknown" : ((X509Certificate) cert).getSubjectDN().getName(), message));
    }
}
