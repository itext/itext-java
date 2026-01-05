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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.io.resolver.resource.IAdvancedResourceRetriever;
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClientBouncyCastle;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.validation.dataorigin.CertificateOrigin;
import com.itextpdf.signatures.validation.lotl.LotlFetchingProperties;
import com.itextpdf.signatures.validation.lotl.LotlService;
import com.itextpdf.signatures.validation.lotl.LotlTrustedStore;
import com.itextpdf.signatures.validation.lotl.QualifiedValidator;
import com.itextpdf.signatures.validation.report.pades.PAdESLevelReportGenerator;
import com.itextpdf.signatures.validation.report.xml.XmlReportAggregator;
import com.itextpdf.signatures.validation.report.xml.AdESReportAggregator;
import com.itextpdf.signatures.validation.report.xml.EventsToAdESReportAggratorConvertor;
import com.itextpdf.signatures.validation.report.xml.NullAdESReportAggregator;
import com.itextpdf.signatures.validation.report.xml.PadesValidationReport;

import java.io.Writer;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
    @Deprecated
    private Supplier<com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever> resourceRetrieverFactory;
    private Supplier<IAdvancedResourceRetriever> advancedResourceRetrieverFactory;
    private Supplier<DocumentRevisionsValidator> documentRevisionsValidatorFactory;
    private Supplier<IOcspClientBouncyCastle> ocspClientFactory;
    private Supplier<ICrlClient> crlClientFactory;
    private Supplier<LotlTrustedStore> lotlTrustedStoreFactory;
    private Supplier<LotlService> lotlServiceFactory;
    private QualifiedValidator qualifiedValidator;

    /**
     * This set is used to catch recursions while CRL/OCSP responses validation.
     * There might be loops when
     * Revocation data for cert 0 is signed by cert 0. Or
     * Revocation data for cert 0 is signed by cert 1 and revocation data for cert 1 is signed by cert 0.
     * Some more complex loops are possible, and they all are supposed to be caught by this set
     * and the methods to manipulate this set.
     */
    private Set<X509Certificate> certificatesChainBeingValidated = new HashSet<>();

    private Collection<Certificate> trustedCertificates;
    private Collection<Certificate> knownCertificates;
    private boolean trustEuropeanLotl = false;
    private final EventManager eventManager;
    private AdESReportAggregator adESReportAggregator = new NullAdESReportAggregator();
    private boolean padesValidationRequested = false;
    @Deprecated
    private boolean deprecatedResourceRetrieverToUse = false;

    /**
     * Creates a ValidatorChainBuilder using default implementations
     */
    public ValidatorChainBuilder() {
        lotlTrustedStoreFactory = () -> buildLotlTrustedStore();
        certificateRetrieverFactory = () -> buildIssuingCertificateRetriever();
        certificateChainValidatorFactory = () -> buildCertificateChainValidator();
        revocationDataValidatorFactory = () -> buildRevocationDataValidator();
        ocspValidatorFactory = () -> buildOCSPValidator();
        crlValidatorFactory = () -> buildCRLValidator();
        resourceRetrieverFactory = () -> new com.itextpdf.styledxmlparser.resolver.resource.DefaultResourceRetriever();
        advancedResourceRetrieverFactory = () -> new DefaultResourceRetriever();
        documentRevisionsValidatorFactory = () -> buildDocumentRevisionsValidator();
        ocspClientFactory = () -> new OcspClientBouncyCastle()
                .withResourceRetriever(advancedResourceRetrieverFactory.get());
        crlClientFactory = () -> new CrlClientOnline().withResourceRetriever(advancedResourceRetrieverFactory.get());
        lotlServiceFactory = () -> buildLotlService();
        qualifiedValidator = new NullQualifiedValidator();
        eventManager = EventManager.createNewInstance();
    }

    /**
     * Establishes trust in European Union List of Trusted Lists.
     * <p>
     * This feature by default relies on remote resource fetching and third-party EU trusted lists posted online.
     * iText has no influence over these resources maintained by third-party authorities.
     * <p>
     * If this feature is enabled, {@link LotlService} is created and used to retrieve,
     * validate and establish trust in EU List of Trusted Lists.
     * <p>
     * In order to properly work, apart from enabling it, user needs to call
     * {@link LotlService#initializeGlobalCache(LotlFetchingProperties)} method, which performs initial initialization.
     * <p>
     * Additionally, in order to successfully use this feature, a user needs to provide a source for trusted
     * certificates which will be used for LOTL files validation.
     * One can either add an explicit dependency to "eu-trusted-lists-resources" iText module or configure own source of
     * trusted certificates. When iText dependency is used it is required to make sure that the newest version of the
     * dependency is selected, otherwise LOTL validation will fail.
     * <p>
     * The required certificates for LOTL files validations are published in the Official Journal of the European Union.
     * Your own source of trusted certificates can be configured by using
     * {@link EuropeanTrustedListConfigurationFactory#setFactory(Supplier)}.
     *
     * @param trustEuropeanLotl {@code true} if European Union LOTLs are expected to be trusted, {@code false} otherwise
     *
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder trustEuropeanLotl(boolean trustEuropeanLotl) {
        this.trustEuropeanLotl = trustEuropeanLotl;
        return this;
    }

    /**
     * Checks if European Union List of Trusted Lists is supposed to be trusted.
     *
     * @return {@code true} if European Union LOTLs are expected to be trusted, {@code false} otherwise
     */
    public boolean isEuropeanLotlTrusted() {
        return this.trustEuropeanLotl;
    }

    /**
     * Create a new {@link SignatureValidator} instance with the current configuration.
     * This method can be used to create multiple validators.
     *
     * @param document {@link PdfDocument} instance which will be validated
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withCRLValidatorFactory(Supplier<CRLValidator> crlValidatorFactory) {
        this.crlValidatorFactory = crlValidatorFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of
     * {@link com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever} for use in the validation chain.
     *
     * @param resourceRetrieverFactory the ResourceRetrieverFactory method to use
     *
     * @return the current {@link ValidatorChainBuilder}
     *
     * @deprecated in favor of {@link #withAdvancedResourceRetriever}
     */
    @Deprecated
    public ValidatorChainBuilder withResourceRetriever(
            Supplier<com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever> resourceRetrieverFactory) {
        this.resourceRetrieverFactory = resourceRetrieverFactory;
        deprecatedResourceRetrieverToUse = true;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link IAdvancedResourceRetriever} for use in the validation chain.
     *
     * <p>
     * Resource retriever created by this factory will be automatically used in the default CRL client,
     * default OCSP client and default CA issuer certificate retriever. If some custom client is set
     * and one needs to use their custom resource retriever for it, it's their responsibility to pass
     * custom resource retriever to their custom client.
     *
     * <p>
     * Note that resource retriever created by this factory will <b>not</b> be used for {@link LotlService} because
     * the global instance of {@link LotlService} is used by default. If one needs to use their custom resource
     * retriever for {@link LotlService}, they can pass it using {@link LotlService#withCustomResourceRetriever}
     * method.
     *
     * @param resourceRetrieverFactory the resource retriever factory method to use
     *
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withAdvancedResourceRetriever(
            Supplier<IAdvancedResourceRetriever> resourceRetrieverFactory) {
        this.advancedResourceRetrieverFactory = resourceRetrieverFactory;
        return this;
    }

    /**
     * Use this factory method to create instances of {@link OCSPValidator} for use in the validation chain.
     *
     * @param ocspValidatorFactory the OCSPValidatorFactory method to use
     *
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
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
     * @return the current {@link ValidatorChainBuilder}
     *
     * @deprecated This method will be removed in a later version, use {@link #withAdESLevelReportGenerator} instead.
     */
    @Deprecated
    public ValidatorChainBuilder withAdESReportAggregator(AdESReportAggregator adESReportAggregator) {
        this.adESReportAggregator = adESReportAggregator;
        eventManager.register(
                new EventsToAdESReportAggratorConvertor(adESReportAggregator));
        return this;
    }

    /**
     * Use this reportEventListener to generate an AdES xml report.
     *
     * <p>
     * Generated {@link PadesValidationReport} report could be provided to
     * {@link com.itextpdf.signatures.validation.report.xml.XmlReportGenerator#generate(PadesValidationReport, Writer)}.
     *
     * @param reportEventListener the AdESReportEventListener to use
     *
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withAdESLevelReportGenerator(XmlReportAggregator reportEventListener) {
        eventManager.register(reportEventListener);
        return this;
    }

    /**
     * Use this PAdES level report generator to generate PAdES report.
     * <p>
     * If called multiple times, multiple {@link PAdESLevelReportGenerator} objects will be registered.
     *
     * @param reportGenerator the PAdESLevelReportGenerator to use
     *
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withPAdESLevelReportGenerator(PAdESLevelReportGenerator reportGenerator) {
        padesValidationRequested = true;
        eventManager.register(reportGenerator);
        return this;
    }

    /**
     * Checks whether PAdES compliance validation was requested.
     *
     * @return {@code true} if PAdES compliance validation was requested, {@code false} otherwise
     */
    public boolean padesValidationRequested() {
        return padesValidationRequested;
    }

    /**
     * Sets {@link QualifiedValidator} instance to be used during signature qualification validation.
     * The results of this validation can be obtained from this same instance.
     * The feature is only executed if European LOTL is used. See {@link #trustEuropeanLotl(boolean)}.
     * <p>
     * This validator needs to be updated per each document validation, or the results need to be obtained.
     * Otherwise, the exception will be thrown.
     * <p>
     * If no instance is provided, the qualification validation is not executed.
     *
     * @param qualifiedValidator {@link QualifiedValidator} instance which performs the validation
     *
     * @return the current {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withQualifiedValidator(QualifiedValidator qualifiedValidator) {
        this.qualifiedValidator = qualifiedValidator;
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
     * Returns the EventManager to be used for all events fired during validation.
     *
     * @return the EventManager to be used for all events fired during validation
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Retrieves the explicitly added or automatically created {@link AdESReportAggregator} instance.
     * Default is the {@link NullAdESReportAggregator}.
     *
     * @return the explicitly added or automatically created {@link AdESReportAggregator} instance.
     * @deprecated The AdESReportAggregator system is replaced by the {@link XmlReportAggregator} system.
     */
    @Deprecated
    public AdESReportAggregator getAdESReportAggregator() {
        return adESReportAggregator;
    }

    /**
     * Retrieves the explicitly added or automatically created
     * {@link com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever} instance.
     *
     * @return the explicitly added or automatically created
     *          {@link com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever} instance
     *
     * @deprecated as user should not normally need this getter
     */
    @Deprecated
    public com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever getResourceRetriever() {
        return resourceRetrieverFactory.get();
    }

    /**
     * Gets {@link QualifiedValidator} instance.
     *
     * @return {@link QualifiedValidator} instance
     */
    public QualifiedValidator getQualifiedValidator() {
        return qualifiedValidator;
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

    /**
     * Sets up factory which is responsible for {@link LotlTrustedStore} creation.
     *
     * @param lotlTrustedStoreFactory factory responsible for {@link LotlTrustedStore} creation
     * @return this same instance of {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withLotlTrustedStoreFactory(Supplier<LotlTrustedStore> lotlTrustedStoreFactory) {
        this.lotlTrustedStoreFactory = lotlTrustedStoreFactory;
        return this;
    }

    /**
     * Retrieves explicitly added or automatically created {@link LotlTrustedStore} instance.
     *
     * @return explicitly added or automatically created {@link LotlTrustedStore} instance
     */
    public LotlTrustedStore getLotlTrustedStore() {
        return this.lotlTrustedStoreFactory.get();
    }

    /**
     * Sets up factory which is responsible for {@link LotlService} creation.
     *
     * @param lotlServiceFactory factory responsible for {@link LotlService} creation
     *
     * @return this same instance of {@link ValidatorChainBuilder}
     */
    public ValidatorChainBuilder withLotlService(Supplier<LotlService> lotlServiceFactory) {
        this.lotlServiceFactory = lotlServiceFactory;
        return this;
    }

    /**
     * Retrieves explicitly added or automatically created {@link LotlService} instance.
     *
     * @return explicitly added or automatically created {@link LotlService} instance
     */
    public LotlService getLotlService() {
        return this.lotlServiceFactory.get();
    }

    void addCertificateBeingValidated(X509Certificate certificate) {
        certificatesChainBeingValidated.add(certificate);
    }

    void removeCertificateBeingValidated(X509Certificate certificate) {
        certificatesChainBeingValidated.remove(certificate);
    }

    boolean isCertificateBeingValidated(X509Certificate certificate) {
        return certificatesChainBeingValidated.contains(certificate);
    }

    private static LotlService buildLotlService() {
        return LotlService.getGlobalService();
    }

    private IssuingCertificateRetriever buildIssuingCertificateRetriever() {
        IssuingCertificateRetriever certRetriever;
        if (deprecatedResourceRetrieverToUse) {
            certRetriever = new IssuingCertificateRetriever(this.resourceRetrieverFactory.get());
        } else {
            certRetriever = new IssuingCertificateRetriever()
                    .withResourceRetriever(advancedResourceRetrieverFactory.get());
        }

        if (trustedCertificates != null) {
            certRetriever.setTrustedCertificates(trustedCertificates);
        }
        if (knownCertificates != null) {
            certRetriever.addKnownCertificates(knownCertificates, CertificateOrigin.OTHER);
        }

        certRetriever.addKnownCertificates(lotlTrustedStoreFactory.get().getCertificates(), CertificateOrigin.OTHER);
        return certRetriever;
    }

    private LotlTrustedStore buildLotlTrustedStore() {
        return new LotlTrustedStore(this);
    }
}
