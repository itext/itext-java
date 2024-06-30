/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.IssuingCertificateRetriever;

import java.security.cert.Certificate;
import java.util.Collection;

/**
 * A builder class to construct all necessary parts of a validation chain.
 * The builder can be reused to create multiple instances of a validator.
 */
public class ValidatorChainBuilder {
    private SignatureValidationProperties properties;
    private IssuingCertificateRetriever certificateRetriever;
    private CertificateChainValidator certificateChainValidator;
    private RevocationDataValidator revocationDataValidator;
    private OCSPValidator ocspValidator;
    private CRLValidator crlValidator;
    private DocumentRevisionsValidator documentRevisionsValidator;

    /**
     * Create a new {@link SignatureValidator} instance with the current configuration.
     * This method can be used to create multiple validators.
     *
     * @param document {@link PdfDocument} instance which will be validated
     *
     * @return a new instance of a signature validator.
     */
    public SignatureValidator buildSignatureValidator(PdfDocument document) {
        return new SignatureValidator(document, this);
    }

    /**
     * Create a bew {@link DocumentRevisionsValidator} instance with the current configuration.
     * This method can be used to create multiple validators.
     *
     * @return a new instance of a document revisions validator.
     */
    public DocumentRevisionsValidator buildDocumentRevisionsValidator() {
        return new DocumentRevisionsValidator(this);
    }

    /**
     * Create a new {@link CertificateChainValidator} instance.
     * This method can be used to create multiple validators.
     *
     * @return a new instance of a CertificateChainValidator.
     */
    public CertificateChainValidator buildCertificateChainValidator() {
        return new CertificateChainValidator(this);
    }

    /**
     * Create a new {@link RevocationDataValidator} instance
     * This method can be used to create multiple validators.
     *
     * @return a new instance of a RevocationDataValidator.
     */
    public RevocationDataValidator buildRevocationDataValidator() {
        return new RevocationDataValidator(this);
    }

    /**
     * Create a new {@link OCSPValidator} instance.
     * This method can be used to create multiple validators.
     *
     * @return a new instance of a OCSPValidator.
     */
    public OCSPValidator buildOCSPValidator() {
        return new OCSPValidator(this);
    }

    /**
     * Create a new {@link CRLValidator} instance.
     * This method can be used to create multiple validators.
     *
     * @return a new instance of a CRLValidator.
     */
    public CRLValidator buildCRLValidator() {
        return new CRLValidator(this);
    }

    /**
     * Use this instance of a {@link DocumentRevisionsValidator} in the validation chain.
     *
     * @param documentRevisionsValidator the document revisions validator instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withDocumentRevisionsValidator(DocumentRevisionsValidator documentRevisionsValidator) {
        this.documentRevisionsValidator = documentRevisionsValidator;
        return this;
    }

    /**
     * Use this instance of a {@link CRLValidator} in the validation chain.
     *
     * @param crlValidator the CRLValidator instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withCRLValidator(CRLValidator crlValidator) {
        this.crlValidator = crlValidator;
        return this;
    }

    /**
     * Use this instance of a {@link OCSPValidator} in the validation chain.
     *
     * @param ocspValidator the OCSPValidator instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withOCSPValidator(OCSPValidator ocspValidator) {
        this.ocspValidator = ocspValidator;
        return this;
    }

    /**
     * Use this instance of a {@link RevocationDataValidator} in the validation chain.
     *
     * @param revocationDataValidator the RevocationDataValidator instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withRevocationDataValidator(RevocationDataValidator revocationDataValidator) {
        this.revocationDataValidator = revocationDataValidator;
        return this;
    }

    /**
     * Use this instance of a {@link CertificateChainValidator} in the validation chain.
     *
     * @param certificateChainValidator the CertificateChainValidator instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withCertificateChainValidator(CertificateChainValidator certificateChainValidator) {
        this.certificateChainValidator = certificateChainValidator;
        return this;
    }

    /**
     * Use this instance of a {@link SignatureValidationProperties} in the validation chain.
     *
     * @param properties the SignatureValidationProperties instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withSignatureValidationProperties(SignatureValidationProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Use this instance of a {@link IssuingCertificateRetriever} in the validation chain.
     *
     * @param certificateRetriever the IssuingCertificateRetriever instance to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withIssuingCertificateRetriever(IssuingCertificateRetriever certificateRetriever) {
        this.certificateRetriever = certificateRetriever;
        return this;
    }

    /**
     * Adds known certificates to the {@link IssuingCertificateRetriever}.
     *
     * @param knownCertificates the list of known certificates to add
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withKnownCertificates(Collection<Certificate> knownCertificates) {
        getCertificateRetriever().addKnownCertificates(knownCertificates);
        return this;
    }

    /**
     * Sets the trusted certificates to the {@link IssuingCertificateRetriever}.
     *
     * @param trustedCertificates the list of trusted certificates to set
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withTrustedCertificates(Collection<Certificate> trustedCertificates) {
        getCertificateRetriever().setTrustedCertificates(trustedCertificates);
        return this;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     *
     * @return the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     */
    DocumentRevisionsValidator getDocumentRevisionsValidator() {
        if (documentRevisionsValidator == null) {
            documentRevisionsValidator = buildDocumentRevisionsValidator();
        }
        return documentRevisionsValidator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CertificateChainValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CertificateChainValidator} instance.
     */
    CertificateChainValidator getCertificateChainValidator() {
        if (certificateChainValidator == null) {
            certificateChainValidator = buildCertificateChainValidator();
        }
        return certificateChainValidator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link RevocationDataValidator} instance.
     *
     * @return the explicitly added or automatically created {@link RevocationDataValidator} instance.
     */
    RevocationDataValidator getRevocationDataValidator() {
        if (revocationDataValidator == null) {
            revocationDataValidator = buildRevocationDataValidator();
        }
        return revocationDataValidator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CRLValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CRLValidator} instance.
     */
    CRLValidator getCRLValidator() {
        if (crlValidator == null) {
            crlValidator = buildCRLValidator();
        }
        return crlValidator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link OCSPValidator} instance.
     *
     * @return the explicitly added or automatically created {@link OCSPValidator} instance.
     */
    OCSPValidator getOCSPValidator() {
        if (ocspValidator == null) {
            ocspValidator = buildOCSPValidator();
        }
        return ocspValidator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     *
     * @return the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     */
    public IssuingCertificateRetriever getCertificateRetriever() {
        if (certificateRetriever == null) {
            certificateRetriever = new IssuingCertificateRetriever();
        }
        return certificateRetriever;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link SignatureValidationProperties} instance.
     *
     * @return the explicitly added or automatically created {@link SignatureValidationProperties} instance.
     */
    public SignatureValidationProperties getProperties() {
        if (properties == null) {
            properties = new SignatureValidationProperties();
        }
        return properties;
    }
}
