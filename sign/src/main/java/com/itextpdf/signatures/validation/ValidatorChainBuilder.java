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
package com.itextpdf.signatures.validation;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.IssuingCertificateRetriever;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * A builder class to construct all necessary parts of a validation chain.
 * The builder can be reused to create multiple instances of a validator.
 */
public class ValidatorChainBuilder {
    private SignatureValidationProperties properties;
    private Supplier<IssuingCertificateRetriever> certificateRetrieverFactory;
    private Supplier<CertificateChainValidator> certificateChainValidatorFactory;
    private Supplier<RevocationDataValidator> revocationDataValidatorFactory;
    private Supplier<OCSPValidator> ocspValidatorFactory;
    private Supplier<CRLValidator> crlValidatorFactory;
    private Supplier<DocumentRevisionsValidator> documentRevisionsValidatorFactory;
    private Collection<Certificate> trustedCertificates;
    private Collection<Certificate> knownCertificates;

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
     * Use this factory method to create instances of {@link DocumentRevisionsValidator}
     * for use in the validation chain.
     *
     * @param documentRevisionsValidatorFactory the document revisions validator factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withDocumentRevisionsValidatorFactory(
            Supplier<DocumentRevisionsValidator> documentRevisionsValidatorFactory) {
        this.documentRevisionsValidatorFactory = documentRevisionsValidatorFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link CRLValidator} for use in the validation chain.
     *
     * @param crlValidatorFactory the CRLValidatorFactory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withCRLValidatorFactory(Supplier<CRLValidator> crlValidatorFactory) {
        this.crlValidatorFactory = crlValidatorFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link OCSPValidator} for use in the validation chain.
     *
     * @param ocspValidatorFactory the OCSPValidatorFactory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withOCSPValidatorFactory(Supplier<OCSPValidator> ocspValidatorFactory) {
        this.ocspValidatorFactory = ocspValidatorFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link RevocationDataValidator} for use in the validation chain.
     *
     * @param revocationDataValidatorFactory the RevocationDataValidator factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withRevocationDataValidatorFactory(
            Supplier<RevocationDataValidator> revocationDataValidatorFactory) {
        this.revocationDataValidatorFactory = revocationDataValidatorFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link CertificateChainValidator} for use in the validation chain.
     *
     * @param certificateChainValidatorFactory the CertificateChainValidator factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withCertificateChainValidatorFactory(
            Supplier<CertificateChainValidator> certificateChainValidatorFactory) {
        this.certificateChainValidatorFactory = certificateChainValidatorFactory;
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
     * Use this factory method to create instances of {@link IssuingCertificateRetriever}
     * for use in the validation chain.
     *
     * @param certificateRetrieverFactory the IssuingCertificateRetriever factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withIssuingCertificateRetrieverFactory(
            Supplier<IssuingCertificateRetriever> certificateRetrieverFactory) {
        this.certificateRetrieverFactory = certificateRetrieverFactory;
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
        this.knownCertificates = new ArrayList<>(knownCertificates);
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
        this.trustedCertificates =  new ArrayList<>(trustedCertificates);
        return this;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     *
     * @return the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     */
    public IssuingCertificateRetriever getCertificateRetriever() {
        if (certificateRetrieverFactory == null) {
            return buildIssuingCertificateRetriever();
        }
        return certificateRetrieverFactory.get();
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

    /**
     * Retrieves the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     *
     * @return the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     */
    DocumentRevisionsValidator getDocumentRevisionsValidator() {
        if (documentRevisionsValidatorFactory == null) {
            return buildDocumentRevisionsValidator();
        }
        return documentRevisionsValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CertificateChainValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CertificateChainValidator} instance.
     */
    CertificateChainValidator getCertificateChainValidator() {
        if (certificateChainValidatorFactory == null) {
            return buildCertificateChainValidator();
        }
        return certificateChainValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link RevocationDataValidator} instance.
     *
     * @return the explicitly added or automatically created {@link RevocationDataValidator} instance.
     */
    RevocationDataValidator getRevocationDataValidator() {
        if (revocationDataValidatorFactory == null) {
            return buildRevocationDataValidator();
        }
        return revocationDataValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CRLValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CRLValidator} instance.
     */
    CRLValidator getCRLValidator() {
        if (crlValidatorFactory == null) {
            return buildCRLValidator();
        }
        return crlValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link OCSPValidator} instance.
     *
     * @return the explicitly added or automatically created {@link OCSPValidator} instance.
     */
    OCSPValidator getOCSPValidator() {
        if (ocspValidatorFactory == null) {
            return buildOCSPValidator();
        }
        return ocspValidatorFactory.get();
    }

    private IssuingCertificateRetriever buildIssuingCertificateRetriever() {
        IssuingCertificateRetriever result = new IssuingCertificateRetriever();
        if (trustedCertificates != null) {
            result.setTrustedCertificates(trustedCertificates);
        }
        if (knownCertificates != null) {
            result.addKnownCertificates(knownCertificates);
        }
        return result;
    }
}
