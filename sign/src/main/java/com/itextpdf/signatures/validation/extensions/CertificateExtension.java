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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * Class representing certificate extension with all the information required for validation.
 */
public class CertificateExtension {

    public static final String EXCEPTION_OCCURRED = " but an exception occurred {0}:{1}.";
    public static final String EXTENSION_NOT_FOUND = " but no extension with that id was found.";
    public static final String FOUND_VALUE = " but found value ";
    public static final String EXPECTED_EXTENSION_ID_AND_VALUE = "Expected extension with id {0} and value {1}"
            + " {1} {2}";
    private final String extensionOid;
    private final IASN1Primitive extensionValue;
    private String errorMessage = "";

    /**
     * Create new instance of {@link CertificateExtension} using provided extension OID and value.
     *
     * @param extensionOid   {@link String}, which represents extension OID
     * @param extensionValue {@link IASN1Primitive}, which represents extension value
     */
    public CertificateExtension(String extensionOid, IASN1Primitive extensionValue) {
        this.extensionOid = extensionOid;
        this.extensionValue = extensionValue;
    }

    /**
     * Get extension value
     *
     * @return {@link IASN1Primitive}, which represents extension value
     */
    public IASN1Primitive getExtensionValue() {
        return extensionValue;
    }

    /**
     * Get extension OID
     *
     * @return {@link String}, which represents extension OID
     */
    public String getExtensionOid() {
        return extensionOid;
    }

    /**
     * Returns a message with extra information about the check.
     * @return a message with extra information about the check.
     */
    public String getMessage() {
        return MessageFormatUtil.format(EXPECTED_EXTENSION_ID_AND_VALUE,
                getExtensionOid(), getExtensionValue().toString(), errorMessage);
    }

    /**
     * Check if this extension is present in the provided certificate.
     * <p>
     * This method doesn't always require complete extension value equality,
     * instead whenever possible it checks that this extension is present in the certificate.
     *
     * @param certificate {@link X509Certificate} in which this extension shall be present
     *
     * @return {@code true} if extension if present, {@code false} otherwise
     */
    public boolean existsInCertificate(X509Certificate certificate) {
        IASN1Primitive providedExtensionValue;
        try {
            providedExtensionValue = CertificateUtil.getExtensionValue(certificate, extensionOid);
        } catch (IOException | RuntimeException e) {
            errorMessage = MessageFormatUtil.format(EXCEPTION_OCCURRED,
                    e.getClass().getName(),e.getMessage());
            return false;
        }
        if (providedExtensionValue == null) {
            if (extensionValue == null) {
                return true;
            }
            errorMessage = EXTENSION_NOT_FOUND;
            return false;
        }
        if (Objects.equals(providedExtensionValue, extensionValue)) {
            return true;
        }
        errorMessage = FOUND_VALUE + MessageFormatUtil.format(" but found value {0}.", extensionValue.toString());
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateExtension that = (CertificateExtension) o;
        return Objects.equals(extensionOid, that.extensionOid) && Objects.equals(extensionValue, that.extensionValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object) extensionOid, extensionValue);
    }

}
