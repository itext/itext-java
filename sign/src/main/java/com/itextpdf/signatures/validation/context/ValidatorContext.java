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
package com.itextpdf.signatures.validation.context;

import com.itextpdf.signatures.validation.CRLValidator;
import com.itextpdf.signatures.validation.CertificateChainValidator;
import com.itextpdf.signatures.validation.OCSPValidator;
import com.itextpdf.signatures.validation.RevocationDataValidator;
import com.itextpdf.signatures.validation.DocumentRevisionsValidator;

/**
 * This enum lists all possible contexts related to the validator in which the validation is taking place.
 */
public enum ValidatorContext {
    /**
     * This value is expected to be used in {@link OCSPValidator} context.
     */
    OCSP_VALIDATOR,
    /**
     * This value is expected to be used in {@link CRLValidator} context.
     */
    CRL_VALIDATOR,
    /**
     * This value is expected to be used in {@link RevocationDataValidator} context.
     */
    REVOCATION_DATA_VALIDATOR,
    /**
     * This value is expected to be used in {@link CertificateChainValidator} context.
     */
    CERTIFICATE_CHAIN_VALIDATOR,
    /**
     * This value is expected to be used in SignatureValidator context.
     */
    SIGNATURE_VALIDATOR,
    /**
     * This value is expected to be used in {@link DocumentRevisionsValidator} context.
     */
    DOCUMENT_REVISIONS_VALIDATOR
}
