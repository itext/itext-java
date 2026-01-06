/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.events;

/**
 * This enumeration alleviates the need for instanceof on all IValidationEvents.
 */
public enum EventType {
    /**
     * Event triggered for every signature validation being started.
     */
    SIGNATURE_VALIDATION_STARTED,
    /**
     * Event triggered for every validation success, including timestamp validation.
     */
    SIGNATURE_VALIDATION_SUCCESS,
    /**
     * Event triggered for every validation failure, including timestamp validation.
     */
    SIGNATURE_VALIDATION_FAILURE,
    /**
     * Event triggered for every timestamp validation started.
     */
    PROOF_OF_EXISTENCE_FOUND,
    /**
     * Event triggered for every certificate issuer that is retrieved via Authority Information Access extension,
     * or any other external source.
     */
    CERTIFICATE_ISSUER_NOT_FROM_DOCUMENT,
    /**
     * Event triggered for every certificate issuer available in the document
     * that was not in the most recent DSS.
     */
    CERTIFICATE_ISSUER_NOT_FROM_DSS,
    /**
     * Event triggered when revocation data coming not from the latest DSS is needed to perform signature validation.
     */
    REVOCATION_NOT_FROM_DSS,
    /**
     * Event triggered when revocation data from a timestamped DSS is not enough to perform signature validation.
     */
    DSS_NOT_TIMESTAMPED,
    /**
     * Event triggered when the most recent DSS has been processed.
     */
    DSS_ENTRY_PROCESSED,
    /**
     * Event triggered when the certificate chain was validated successfully.
     */
    CERTIFICATE_CHAIN_SUCCESS,
    /**
     * Event triggered when the certificate chain validated failed.
     */
    CERTIFICATE_CHAIN_FAILURE,
    /**
     * Event triggered when a certificate is proven not te be revoked by a CRL response.
     */
    CRL_VALIDATION_SUCCESS,
    /**
     * Event triggered when a certificate is revoked by a CRL response.
     */
    CRL_VALIDATION_FAILURE,
    /**
     * Event triggered when a certificate is proven not te be revoked by a OCSP response.
     */
    OCSP_VALIDATION_SUCCESS,
    /**
     * Event triggered when a certificate is revoked by a OCSP response.
     */
    OCSP_VALIDATION_FAILURE,
    /**
     * Event triggered for every algorithm being used during signature validation.
     */
    ALGORITHM_USAGE
}
