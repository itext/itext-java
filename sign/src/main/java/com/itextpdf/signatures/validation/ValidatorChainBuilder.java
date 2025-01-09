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
package com.itextpdf.signatures.validation;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClientBouncyCastle;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.validation.report.xml.AdESReportAggregator;
import com.itextpdf.signatures.validation.report.xml.NullAdESReportAggregator;
import com.itextpdf.signatures.validation.report.xml.PadesValidationReport;
import com.itextpdf.styledxmlparser.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;

import java.io.Writer;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * A builder class to construct all necessary parts of a validation chain.
 * The builder can be reused to create multiple instances of a validator.
 */
public class ValidatorChainBuilder {
    private SignatureValidationProperties properties = new SignatureValidationProperties();
    private Supplier<IssuingCertificateRetriever> certificateRetrieverFactory;
    private Supplier<CertificateChainValidator> certificateChainValidatorFactory;
    private Supplier<RevocationDataValidator> revocationDataValidatorFactory;
    private Supplier<OCSPValidator> ocspValidatorFactory;
    private Supplier<CRLValidator> crlValidatorFactory;
    private Supplier<IResourceRetriever> resourceRetrieverFactory;
    private Supplier<DocumentRevisionsValidator> documentRevisionsValidatorFactory;
    private Supplier<IOcspClientBouncyCastle> ocspClientFactory;
    private Supplier<ICrlClient> crlClientFactory;

    private Collection<Certificate> trustedCertificates;
    private Collection<Certificate> knownCertificates;
    private AdESReportAggregator adESReportAggregator = new NullAdESReportAggregator();

    /**
     * Creates a ValidatorChainBuilder using default implementations
     */
    public ValidatorChainBuilder() {
        certificateRetrieverFactory = () -> buildIssuingCertificateRetriever();
        certificateChainValidatorFactory = () -> buildCertificateChainValidator();
        revocationDataValidatorFactory = () -> buildRevocationDataValidator();
        ocspValidatorFactory = () -> buildOCSPValidator();
        crlValidatorFactory = () -> buildCRLValidator();
        resourceRetrieverFactory = () -> new DefaultResourceRetriever();
        documentRevisionsValidatorFactory = () -> buildDocumentRevisionsValidator();
        ocspClientFactory = () -> new OcspClientBouncyCastle();
        crlClientFactory = () -> new CrlClientOnline();
    }

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
     * Use this factory method to create instances of {@link IResourceRetriever} for use in the validation chain.
     *
     * @param resourceRetrieverFactory the ResourceRetrieverFactory method to use.
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withResourceRetriever(Supplier<IResourceRetriever> resourceRetrieverFactory) {
        this.resourceRetrieverFactory = resourceRetrieverFactory;
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
     * Use this factory to create instances of {@link IOcspClientBouncyCastle} for use in the validation chain.
     *
     * @param ocspClientFactory the IOcspClient factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withOcspClient(Supplier<IOcspClientBouncyCastle> ocspClientFactory) {
        this.ocspClientFactory = ocspClientFactory;
        return this;
    }

    /**
     * Use this factory to create instances of {@link ICrlClient} for use in the validation chain.
     *
     * @param crlClientFactory the ICrlClient factory method to use
     *
     * @return the current ValidatorChainBuilder.
     */
    public ValidatorChainBuilder withCrlClient(Supplier<ICrlClient> crlClientFactory) {
        this.crlClientFactory = crlClientFactory;
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
        this.trustedCertificates = new ArrayList<>(trustedCertificates);
        return this;
    }

    /**
     * Use this AdES report aggregator to enable AdES compliant report generation.
     *
     * <p>
     * Generated {@link PadesValidationReport} report could be provided to
     * {@link com.itextpdf.signatures.validation.report.xml.XmlReportGenerator#generate(PadesValidationReport, Writer)}.
     *
     * @param adESReportAggregator the report aggregator to use
     *
     * @return the current ValidatorChainBuilder
     */
    public ValidatorChainBuilder withAdESReportAggregator(AdESReportAggregator adESReportAggregator) {
        this.adESReportAggregator = adESReportAggregator;
        return this;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     *
     * @return the explicitly added or automatically created {@link IssuingCertificateRetriever} instance.
     */
    public IssuingCertificateRetriever getCertificateRetriever() {
        return certificateRetrieverFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link SignatureValidationProperties} instance.
     *
     * @return the explicitly added or automatically created {@link SignatureValidationProperties} instance.
     */
    public SignatureValidationProperties getProperties() {
        return properties;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link AdESReportAggregator} instance.
     * Default is the {@link NullAdESReportAggregator}.
     *
     * @return the explicitly added or automatically created {@link AdESReportAggregator} instance.
     */
    public AdESReportAggregator getAdESReportAggregator() {
        return adESReportAggregator;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     *
     * @return the explicitly added or automatically created {@link DocumentRevisionsValidator} instance.
     */
    DocumentRevisionsValidator getDocumentRevisionsValidator() {
        return documentRevisionsValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CertificateChainValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CertificateChainValidator} instance.
     */
    CertificateChainValidator getCertificateChainValidator() {
        return certificateChainValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link RevocationDataValidator} instance.
     *
     * @return the explicitly added or automatically created {@link RevocationDataValidator} instance.
     */
    RevocationDataValidator getRevocationDataValidator() {
        return revocationDataValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link ICrlClient} instance.
     *
     * @return the explicitly added or automatically created {@link ICrlClient} instance.
     */
    ICrlClient getCrlClient() {
        return crlClientFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link IOcspClientBouncyCastle} instance.
     *
     * @return the explicitly added or automatically created {@link IOcspClientBouncyCastle} instance.
     */
    IOcspClientBouncyCastle getOcspClient() {
        return ocspClientFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link IResourceRetriever} instance.
     *
     * @return the explicitly added or automatically created {@link IResourceRetriever} instance.
     */
    public IResourceRetriever getResourceRetriever() {
        return resourceRetrieverFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link CRLValidator} instance.
     *
     * @return the explicitly added or automatically created {@link CRLValidator} instance.
     */
    CRLValidator getCRLValidator() {
        return crlValidatorFactory.get();
    }

    /**
     * Retrieves the explicitly added or automatically created {@link OCSPValidator} instance.
     *
     * @return the explicitly added or automatically created {@link OCSPValidator} instance.
     */
    OCSPValidator getOCSPValidator() {
        return ocspValidatorFactory.get();
    }

    private IssuingCertificateRetriever buildIssuingCertificateRetriever() {
        IssuingCertificateRetriever result = new IssuingCertificateRetriever(this.resourceRetrieverFactory.get());
        if (trustedCertificates != null) {
            result.setTrustedCertificates(trustedCertificates);
        }
        if (knownCertificates != null) {
            result.addKnownCertificates(knownCertificates);
        }
        return result;
    }
}
