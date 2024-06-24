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

import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.function.Consumer;

import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.v1.extensions.DynamicBasicConstraintsExtension;
import com.itextpdf.signatures.validation.v1.extensions.ExtendedKeyUsageExtension;
import com.itextpdf.signatures.validation.v1.extensions.KeyUsage;
import com.itextpdf.signatures.validation.v1.extensions.KeyUsageExtension;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Class which stores properties, which are related to signature validation process.
 */
public class SignatureValidationProperties {
    public static final boolean DEFAULT_CONTINUE_AFTER_FAILURE = true;
    public static final Duration DEFAULT_FRESHNESS_PRESENT_CRL = Duration.ofDays(30);
    public static final Duration DEFAULT_FRESHNESS_PRESENT_OCSP = Duration.ofDays(30);
    public static final Duration DEFAULT_FRESHNESS_HISTORICAL = Duration.ofMinutes(1);
    public static final OnlineFetching DEFAULT_ONLINE_FETCHING = OnlineFetching.FETCH_IF_NO_OTHER_DATA_AVAILABLE;

    private final HashMap<ValidationContext, ContextProperties> properties = new HashMap<>();
    private final List<IOcspClient> ocspClients = new ArrayList<>();
    private final List<ICrlClient> crlClients = new ArrayList<>();

    /**
     * Create {@link SignatureValidationProperties} with default values.
     */
    public SignatureValidationProperties() {
        setContinueAfterFailure(ValidatorContexts.all(),CertificateSources.all(), DEFAULT_CONTINUE_AFTER_FAILURE);
        setRevocationOnlineFetching(ValidatorContexts.all(),CertificateSources.all(), TimeBasedContexts.all(),
                DEFAULT_ONLINE_FETCHING);

        setFreshness(ValidatorContexts.all(),CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.HISTORICAL), DEFAULT_FRESHNESS_HISTORICAL);
        setFreshness(ValidatorContexts.all(),CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.PRESENT),DEFAULT_FRESHNESS_PRESENT_OCSP);
        setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.PRESENT), DEFAULT_FRESHNESS_PRESENT_CRL);

        setRequiredExtensions(CertificateSources.of(CertificateSource.CRL_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.CRL_SIGN)));
        setRequiredExtensions(CertificateSources.of(CertificateSource.OCSP_ISSUER),
                Collections.<CertificateExtension>singletonList(new ExtendedKeyUsageExtension(
                        Collections.<String>singletonList(ExtendedKeyUsageExtension.OCSP_SIGNING))));
        setRequiredExtensions(CertificateSources.of(CertificateSource.SIGNER_CERT),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(KeyUsage.NON_REPUDIATION)));
        List<CertificateExtension> certIssuerRequiredExtensions = new ArrayList<>();
        certIssuerRequiredExtensions.add(new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN));
        certIssuerRequiredExtensions.add(new DynamicBasicConstraintsExtension());
        setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER), certIssuerRequiredExtensions);
        setRequiredExtensions(CertificateSources.of(CertificateSource.TIMESTAMP),
                Collections.<CertificateExtension>singletonList(new ExtendedKeyUsageExtension(
                        Collections.<String>singletonList(ExtendedKeyUsageExtension.TIME_STAMPING))));

        ocspClients.add(new ValidationOcspClient());
        crlClients.add(new ValidationCrlClient());
    }

    /**
     * Returns the freshness setting for the provided validation context or the default context
     * in milliseconds.
     *
     * @param validationContext the validation context for which to retrieve the freshness setting
     *
     * @return the freshness setting for the provided validation context or the default context in milliseconds
     */
    public Duration getFreshness(ValidationContext validationContext) {
        return this.<Duration>getParametersValueFor(validationContext.getValidatorContext(),
                validationContext.getCertificateSource(), validationContext.getTimeBasedContext(),
                p -> p.getFreshness());
    }

    /**
     * Sets the freshness setting for the specified validator,
     * time based and certificate source contexts in milliseconds.
     * <p>
     * This parameter specifies how old revocation data can be, compared to validation time, in order to be trustworthy.
     *
     * @param validatorContexts  the validators for which to apply the setting
     * @param certificateSources the certificate sources to
     * @param timeBasedContexts  the date comparison context  for which to apply the setting
     * @param value              the settings value in milliseconds
     *
     * @return this same {@link SignatureValidationProperties} instance.
     */
    public final SignatureValidationProperties setFreshness(ValidatorContexts validatorContexts,
                                                      CertificateSources certificateSources,
                                                      TimeBasedContexts timeBasedContexts, Duration value) {
        setParameterValueFor(validatorContexts.getSet(), certificateSources.getSet(), timeBasedContexts.getSet(),
                p -> p.setFreshness(value));
        return this;
    }

    /**
     * Returns the Continue after failure setting for the provided context or the default context.
     *
     * @param validationContext the context for which to retrieve the Continue after failure setting
     *
     * @return the Continue after failure setting for the provided context or the default context
     */
    public boolean getContinueAfterFailure(ValidationContext validationContext) {
        return this.<Boolean>getParametersValueFor(validationContext.getValidatorContext(),
                validationContext.getCertificateSource(), validationContext.getTimeBasedContext(),
                p -> p.getContinueAfterFailure());
    }

    /**
     * Sets the Continue after failure setting for the provided context.
     * <p>
     * This parameter specifies if validation is expected to continue after first failure is encountered.
     * Only {@link ValidationResult#INVALID} is considered to be a failure.
     *
     * @param validatorContexts  the validators for which to set the Continue after failure setting
     * @param certificateSources the certificateSources for which to set the Continue after failure setting
     * @param value              the Continue after failure setting
     *
     * @return this same {@link SignatureValidationProperties} instance.
     */
    public final SignatureValidationProperties setContinueAfterFailure(ValidatorContexts validatorContexts,
            CertificateSources certificateSources, boolean value) {
        setParameterValueFor(validatorContexts.getSet(), certificateSources.getSet(), TimeBasedContexts.all().getSet(),
                p -> p.setContinueAfterFailure(value));
        return this;
    }

    /**
     * Sets the onlineFetching property representing possible online fetching permissions.
     *
     * @param validationContext the context for which to retrieve the online fetching setting
     *
     * @return the online fetching setting.
     */
    public OnlineFetching getRevocationOnlineFetching(ValidationContext validationContext) {
        return this.<OnlineFetching>getParametersValueFor(validationContext.getValidatorContext(),
                validationContext.getCertificateSource(), validationContext.getTimeBasedContext(),
                p -> p.getOnlineFetching());
    }

    /**
     * Sets the onlineFetching property representing possible online fetching permissions.
     *
     * @param validatorContexts  the validators for which to set this value
     * @param certificateSources the certificate source for which to set this value
     * @param timeBasedContexts  time perspective context, at which validation is happening
     * @param onlineFetching     onlineFetching property value to set
     *
     * @return this same {@link SignatureValidationProperties} instance.
     */
    public final SignatureValidationProperties setRevocationOnlineFetching(ValidatorContexts validatorContexts,
            CertificateSources certificateSources, TimeBasedContexts timeBasedContexts,
            OnlineFetching onlineFetching) {
        setParameterValueFor(validatorContexts.getSet(), certificateSources.getSet(), timeBasedContexts.getSet(),
                p -> p.setOnlineFetching(onlineFetching));
        return this;
    }

    /**
     * Returns required extension for the provided validation context.
     *
     * @param validationContext the validation context for which to retrieve required extensions
     *
     * @return required extensions for the provided validation context
     */
    public List<CertificateExtension> getRequiredExtensions(ValidationContext validationContext) {
        return this.<List<CertificateExtension>>getParametersValueFor(validationContext.getValidatorContext(),
                validationContext.getCertificateSource(), validationContext.getTimeBasedContext(),
                p -> p.getRequiredExtensions());
    }

    /**
     * Set list of extensions which are required to be set to a certificate depending on certificate source.
     * <p>
     * By default, required extensions are set to be compliant with common validation norms.
     * Changing those can result in falsely positive validation result.
     *
     * @param certificateSources {@link CertificateSource} for extensions to be present
     * @param requiredExtensions list of required {@link CertificateExtension}
     *
     * @return this same {@link SignatureValidationProperties} instance
     */
    public final SignatureValidationProperties setRequiredExtensions(CertificateSources certificateSources,
            List<CertificateExtension> requiredExtensions) {
        // make a defensive copy of requiredExtensions and already wrap it with unmodifiableList so that we don't have
        // to do this every time it is retrieved. Now we are protected against changes in passed list and from
        // changes in the returned list
        List<CertificateExtension> copy = Collections.<CertificateExtension>unmodifiableList(
                new ArrayList<>(requiredExtensions));
        setParameterValueFor(ValidatorContexts.all().getSet(), certificateSources.getSet(),
                TimeBasedContexts.all().getSet(), p -> p.setRequiredExtensions(copy));
        return this;
    }

    /**
     * Gets all {@link ICrlClient} instances which will be used to retrieve CRL responses during the validation.
     *
     * @return all {@link ICrlClient} instances which will be used to retrieve CRL responses during the validation
     */
    public List<ICrlClient> getCrlClients() {
        return Collections.unmodifiableList(crlClients);
    }

    /**
     * Adds new {@link ICrlClient} instance which will be used to retrieve CRL responses during the validation.
     *
     * @param crlClient {@link ICrlClient} instance which will be used to retrieve CRL responses during the validation
     *
     * @return this same {@link SignatureValidationProperties} instance
     */
    public final SignatureValidationProperties addCrlClient(ICrlClient crlClient) {
        crlClients.add(crlClient);
        return this;
    }

    /**
     * Gets all {@link IOcspClient} instances which will be used to retrieve OCSP responses during the validation.
     *
     * @return all {@link IOcspClient} instances which will be used to retrieve OCSP responses during the validation
     */
    public List<IOcspClient> getOcspClients() {
        return Collections.unmodifiableList(ocspClients);
    }

    /**
     * Adds new {@link IOcspClient} instance which will be used to retrieve OCSP response during the validation.
     *
     * @param ocspClient {@link IOcspClient} instance which will be used to retrieve OCSP response during the validation
     *
     * @return this same {@link SignatureValidationProperties} instance
     */
    public final SignatureValidationProperties addOcspClient(IOcspClient ocspClient) {
        ocspClients.add(ocspClient);
        return this;
    }

    /**
     * This method executes the setter method for every combination of selected validators and certificateSources
     *
     * @param validatorContexts  the validators to execute the setter on
     * @param certificateSources the certificate sources to execute the setter on
     * @param setter             the setter to execute
     */
    final void setParameterValueFor(EnumSet<ValidatorContext> validatorContexts,
            EnumSet<CertificateSource> certificateSources, EnumSet<TimeBasedContext> timeBasedContexts,
            Consumer<ContextProperties> setter) {
        for (ValidatorContext validatorContext : validatorContexts) {
            for (CertificateSource certificateSource : certificateSources) {
                for (TimeBasedContext timeBasedContext : timeBasedContexts) {
                    ValidationContext vc = new ValidationContext(validatorContext, certificateSource, timeBasedContext);
                    ContextProperties cProperties = properties.computeIfAbsent(vc, unused -> new ContextProperties());
                    setter.accept(cProperties);
                }
            }
        }
    }

    /**
     * This method executes the getter method to the most granular parameters set down until the getter returns
     * a non-null value
     *
     * @param validatorContext the validator for which the value is to be retrieved
     * @param certSource       the certificate source for which the value is to be retrieved
     * @param getter           the getter to get the value from the parameters set
     * @param <T>              the type of the return value of this method and the getter method
     *
     * @return the first non-null value returned.
     */
    <T> T getParametersValueFor(ValidatorContext validatorContext, CertificateSource certSource,
            TimeBasedContext timeBasedContext, Function<ContextProperties, T> getter) {
        // all three match
        ValidationContext c = new ValidationContext(validatorContext, certSource, timeBasedContext);
        if (properties.containsKey(c)) {
            return getter.apply(properties.get(c));
        }
        return null;
    }

    /**
     * Enum representing possible online fetching permissions.
     */
    public enum OnlineFetching {
        /**
         * Permission to always fetch revocation data online.
         */
        ALWAYS_FETCH,
        /**
         * Permission to fetch revocation data online if no other revocation data available.
         */
        FETCH_IF_NO_OTHER_DATA_AVAILABLE,
        /**
         * Forbids fetching revocation data online.
         */
        NEVER_FETCH
    }

    static class ContextProperties {
        private Duration freshness;
        private Boolean continueAfterFailure;
        private OnlineFetching onlineFetching;
        private List<CertificateExtension> requiredExtensions;

        public ContextProperties() {
            // Empty constructor.
        }

        public Boolean getContinueAfterFailure() {
            return continueAfterFailure;
        }

        public void setContinueAfterFailure(Boolean continueAfterFailure) {
            this.continueAfterFailure = continueAfterFailure;
        }

        public Duration getFreshness() {
            return freshness;
        }

        public void setFreshness(Duration value) {
            freshness = value;
        }

        public OnlineFetching getOnlineFetching() {
            return onlineFetching;
        }

        public void setOnlineFetching(OnlineFetching onlineFetching) {
            this.onlineFetching = onlineFetching;
        }

        public List<CertificateExtension> getRequiredExtensions() {
            return requiredExtensions;
        }

        public void setRequiredExtensions(List<CertificateExtension> requiredExtensions) {
            this.requiredExtensions = requiredExtensions;
        }
    }
}