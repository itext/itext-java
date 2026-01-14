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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.commons.utils.TimerUtil;
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.function.LongUnaryOperator;

/**
 * This class provides API for managing the List of Trusted Lists (LOTL) and related resources.
 * It includes API for fetching, validating, and caching LOTL data, as well as managing the European Resource
 * Fetcher and Country-Specific LOTL Fetcher.
 * It also allows for setting custom resource retrievers and cache timeouts.
 */
// TODO DEVSIX-9710: Make this class abstract and remove EuropeanLotlService specific methods to EuropeanLotlService
public class LotlService implements AutoCloseable {

    private static final Object GLOBAL_SERVICE_LOCK = new Object();
    private static final String DEFAULT_USER_AGENT = "iText-lotl-retriever/1.0";
    protected final LotlFetchingProperties lotlFetchingProperties;

    // Global service
    static LotlService GLOBAL_SERVICE;

    private static final String ABSTRACT_CLASS_EXCEPTION = "Treat this method as abstract so you need to implement "
            + "it on your own. It will become abstract in the next major release.";
    private static final String NOT_USABLE_METHOD_EXCEPTION = "You are using the method which will be removed "
            + "in the next major release. You probably need to extend EuropeanLotlService but not LotlService.";

    private boolean cacheInitialized = false;
    private Timer cacheTimer = null;
    private IResourceRetriever resourceRetriever;
    private Function<TrustedCertificatesStore, XmlSignatureValidator> xmlSignatureValidatorFactory;
    private Supplier<LotlValidator> lotlValidatorFactory;

    private EuropeanLotlService defaultImpl;

    /**
     * Creates a new instance of {@link LotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     */
    public LotlService(LotlFetchingProperties lotlFetchingProperties) {
        this.lotlFetchingProperties = lotlFetchingProperties;

        if (this.getClass() == LotlService.class) {
            defaultImpl = new EuropeanLotlService(lotlFetchingProperties);
        } else {
            this.xmlSignatureValidatorFactory = trustedCertificatesStore -> buildXmlSignatureValidator(
                    trustedCertificatesStore);
            this.lotlValidatorFactory = () -> buildLotlValidator();
            this.resourceRetriever = new LoggableResourceRetriever();
            ((LoggableResourceRetriever) this.resourceRetriever).setRequestHeaders(
                    Collections.singletonMap("User-Agent",DEFAULT_USER_AGENT));
        }
    }

    /**
     * Initializes the global service with the provided {@link LotlFetchingProperties}.
     * This method must be called before using the {@link LotlService} to ensure that the cache is set up.
     * <p>
     * If you are using a custom implementation of {@link LotlService} you can use the instance method.
     *
     * @param lotlFetchingProperties the {@link LotlFetchingProperties} to use for initializing the cache
     */
    public static void initializeGlobalCache(LotlFetchingProperties lotlFetchingProperties) {
        synchronized (GLOBAL_SERVICE_LOCK) {
            if (GLOBAL_SERVICE == null) {
                GLOBAL_SERVICE = new EuropeanLotlService(lotlFetchingProperties);
                GLOBAL_SERVICE.initializeCache();
            } else {
                throw new PdfException(SignExceptionMessageConstant.CACHE_ALREADY_INITIALIZED);
            }
        }
    }

    /**
     * Gets global static instance of {@link LotlService}.
     *
     * @return global static instance of {@link LotlService}
     */
    public static LotlService getGlobalService() {
        return GLOBAL_SERVICE;
    }

    /**
     * Sets the cache for the {@link LotlService}.
     * <p>
     * This method allows you to provide a custom implementation of {@link LotlServiceCache} to be used
     * for caching LOTL data, pivot files, and country-specific LOTLs.
     *
     * @param cache the custom cache to be used for caching LOTL data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    // TODO DEVSIX-9710: Remove this method to EuropeanLotlService
    public LotlService withLotlServiceCache(LotlServiceCache cache) {
        if (defaultImpl != null) {
            return defaultImpl.withLotlServiceCache(cache);
        }

        throw new UnsupportedOperationException(NOT_USABLE_METHOD_EXCEPTION);
    }

    /**
     * Sets a custom resource retriever for fetching resources.
     * <p>
     * This method allows you to provide a custom implementation of {@link IResourceRetriever} to be used
     * for fetching resources such as the LOTL XML, pivot files, and country-specific LOTLs.
     * <p>
     * Multiple LOTL endpoints require a userAgent header to be sent. This should be taken into account
     * when providing a custom  {@link IResourceRetriever}.
     *
     * @param resourceRetriever the custom resource retriever to be used for fetching resources
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public final LotlService withCustomResourceRetriever(IResourceRetriever resourceRetriever) {
        if (defaultImpl == null) {
            this.resourceRetriever = resourceRetriever;
        } else {
            defaultImpl.withCustomResourceRetriever(resourceRetriever);
        }

        return this;
    }

    /**
     * Initializes the cache with the latest LOTL data and related resources.
     */
    public void initializeCache() {
        if (defaultImpl == null) {
            initializeCache(null);
        } else {
            defaultImpl.initializeCache();
        }
    }

    /**
     * Loads the cache from the provided input stream.
     * <p>
     * The input stream should contain serialized cache data, which can be created using the
     * {@link #serializeCache(OutputStream)} method.
     *
     * @param stream the input stream to read the cached data from
     */
    // TODO DEVSIX-9710: Make this method abstract
    public void loadFromCache(InputStream stream) {
        if (defaultImpl != null) {
            defaultImpl.loadFromCache(stream);
            return;
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * Get the validation results for the List of Trusted Lists (LOTL).
     *
     * @return a {@link ValidationReport} containing the results of the LOTL validation
     */
    // TODO DEVSIX-9710: Make this method abstract
    public ValidationReport getValidationResult() {
        if (defaultImpl != null) {
            return defaultImpl.getValidationResult();
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * Retrieves national trusted certificates.
     *
     * @return the list of the national trusted certificates
     */
    // TODO DEVSIX-9710: Make this method abstract
    public List<IServiceContext> getNationalTrustedCertificates() {
        if (defaultImpl != null) {
            return defaultImpl.getNationalTrustedCertificates();
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * Initializes the cache with the latest LOTL data and related resources.
     * <p>
     * Important: By default when providing a stream, we will still set up a timer to refresh the cache periodically.
     * If you don't want this behavior, please set
     * {@link  LotlFetchingProperties#setRefreshIntervalCalculator(LongUnaryOperator)} to int.Max.
     *
     * @param stream InputStream to read the cached data from. If null, the data will be fetched from the network.
     *               The data can be serialized using {@link #serializeCache(OutputStream)} method.
     */
    public void initializeCache(InputStream stream) {
        if (defaultImpl == null) {
            setupTimer();
            if (stream != null) {
                loadFromCache(stream);
            } else {
                loadFromNetwork();
            }
            cacheInitialized = true;
        } else {
            defaultImpl.initializeCache(stream);
        }
    }

    /**
     * Sets the pivot fetcher for the LOTL service.
     *
     * @param pivotFetcher the pivot fetcher to be used for fetching and validating pivot files
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    // TODO DEVSIX-9710: Remove this method to EuropeanLotlService
    public LotlService withPivotFetcher(PivotFetcher pivotFetcher) {
        if (defaultImpl != null) {
            return defaultImpl.withPivotFetcher(pivotFetcher);
        }

        throw new UnsupportedOperationException(NOT_USABLE_METHOD_EXCEPTION);
    }

    /**
     * Sets the country-specific LOTL fetcher for the LOTL service.
     *
     * @param countrySpecificLotlFetcher the country-specific LOTL fetcher to be used for fetching and validating
     *                                   country-specific LOTLs
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    // TODO DEVSIX-9710: Remove this method to EuropeanLotlService
    public LotlService withCountrySpecificLotlFetcher(CountrySpecificLotlFetcher countrySpecificLotlFetcher) {
        if (defaultImpl != null) {
            return defaultImpl.withCountrySpecificLotlFetcher(countrySpecificLotlFetcher);
        }

        throw new UnsupportedOperationException(NOT_USABLE_METHOD_EXCEPTION);
    }

    /**
     * Sets the European List of Trusted Lists (LOTL) byte fetcher for the LOTL service.
     *
     * @param fetcher the fetcher to be used for fetching the LOTL XML data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    // TODO DEVSIX-9710: Remove this method to EuropeanLotlService
    public LotlService withEuropeanLotlFetcher(EuropeanLotlFetcher fetcher) {
        if (defaultImpl != null) {
            return defaultImpl.withEuropeanLotlFetcher(fetcher);
        }

        throw new UnsupportedOperationException(NOT_USABLE_METHOD_EXCEPTION);
    }

    /**
     * Sets up factory which is responsible for {@link XmlSignatureValidator} creation.
     *
     * @param xmlSignatureValidatorFactory factory responsible for {@link XmlSignatureValidator} creation
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withXmlSignatureValidator(
            Function<TrustedCertificatesStore, XmlSignatureValidator> xmlSignatureValidatorFactory) {
        if (defaultImpl == null) {
            this.xmlSignatureValidatorFactory = xmlSignatureValidatorFactory;
        } else {
            defaultImpl.withXmlSignatureValidator(xmlSignatureValidatorFactory);
        }

        return this;
    }

    /**
     * Sets up factory which is responsible for {@link LotlValidator} creation.
     *
     * @param lotlValidatorFactory factory responsible for {@link LotlValidator} creation
     *
     * @return this same instance of {@link LotlService}
     */
    public LotlService withLotlValidator(Supplier<LotlValidator> lotlValidatorFactory) {
        if (defaultImpl == null) {
            this.lotlValidatorFactory = lotlValidatorFactory;
        } else {
            defaultImpl.withLotlValidator(lotlValidatorFactory);
        }

        return this;
    }

    /**
     * Sets the European Resource Fetcher for the {@link LotlService}.
     *
     * @param europeanResourceFetcher the European Resource Fetcher to be used for fetching EU journal certificates
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    // TODO DEVSIX-9710: Remove this method to EuropeanLotlService
    public LotlService withEuropeanResourceFetcher(EuropeanResourceFetcher europeanResourceFetcher) {
        if (defaultImpl != null) {
            return defaultImpl.withEuropeanResourceFetcher(europeanResourceFetcher);
        }

        throw new UnsupportedOperationException(NOT_USABLE_METHOD_EXCEPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (defaultImpl == null) {
            cancelTimer();
        } else {
            defaultImpl.close();
        }
    }

    /**
     * Serializes the current state of the cache to the provided output stream.
     *
     * @param outputStream the output stream to which the cache will be serialized
     *
     * @throws IOException if an I/O error occurs during serialization
     */
    // TODO DEVSIX-9710: Make this method abstract
    public void serializeCache(OutputStream outputStream) throws IOException {
        if (defaultImpl != null) {
            defaultImpl.serializeCache(outputStream);
            return;
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * Loads the cache from the network by fetching the latest LOTL data and related resources.
     * <p>
     * This method fetches the main LOTL file, EU journal certificates, pivot files, and country-specific LOTLs,
     * validates them, and stores them in the cache.
     * <p>
     * Note: This method is called during cache initialization and should not be called directly in normal
     * operation.
     */
    // TODO DEVSIX-9710: Make this method abstract
    protected void loadFromNetwork() {
        if (defaultImpl != null) {
            defaultImpl.loadFromNetwork();
            return;
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * This method is intended to refresh the cache, it will try to download the latest LOTL data and update the
     * cache accordingly.
     */
    // TODO DEVSIX-9710: Make this method abstract
    protected void tryAndRefreshCache() {
        if (defaultImpl != null) {
            defaultImpl.tryAndRefreshCache();
            return;
        }

        throw new UnsupportedOperationException(ABSTRACT_CLASS_EXCEPTION);
    }

    /**
     * Sets up a timer to periodically refresh the LOTL cache.
     * <p>
     * The timer will use the refresh interval calculated based on the stale-ness of the cache.
     * If the cache is null, it will create a new instance of {@link InMemoryLotlServiceCache}.
     */
    protected void setupTimer() {
        if (defaultImpl == null) {
            long staleNessInMillis = lotlFetchingProperties.getCacheStalenessInMilliseconds();
            TimerUtil.stopTimer(cacheTimer);
            LongUnaryOperator cacheRefreshTimer = lotlFetchingProperties.getRefreshIntervalCalculator();
            long refreshInterval = cacheRefreshTimer.applyAsLong(staleNessInMillis);
            cacheTimer = TimerUtil.newTimerWithRecurringTask(() -> tryAndRefreshCache(), refreshInterval,
                    refreshInterval);
        } else {
            defaultImpl.setupTimer();
        }
    }

    /**
     * Cancels timer, if it was already set up.
     */
    protected void cancelTimer() {
        if (defaultImpl == null) {
            if (cacheTimer != null) {
                TimerUtil.stopTimer(cacheTimer);
            }
        } else {
            defaultImpl.cancelTimer();
        }
    }

    boolean isCacheInitialized() {
        if (defaultImpl == null) {
            return cacheInitialized;
        } else {
            return defaultImpl.isCacheInitialized();
        }
    }

    /**
     * Gets the resource retriever used by the LOTL service.
     *
     * @return the {@link IResourceRetriever} instance used for fetching resources
     */
    IResourceRetriever getResourceRetriever() {
        if (defaultImpl == null) {
            return resourceRetriever;
        } else {
            return defaultImpl.getResourceRetriever();
        }
    }

    /**
     * Retrieves explicitly added or automatically created {@link XmlSignatureValidator} instance.
     *
     * @return explicitly added or automatically created {@link XmlSignatureValidator} instance.
     */
    XmlSignatureValidator getXmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        if (defaultImpl == null) {
            return xmlSignatureValidatorFactory.apply(trustedCertificatesStore);
        } else {
            return defaultImpl.getXmlSignatureValidator(trustedCertificatesStore);
        }
    }

    LotlFetchingProperties getLotlFetchingProperties() {
        if (defaultImpl == null) {
            return lotlFetchingProperties;
        } else {
            return defaultImpl.getLotlFetchingProperties();
        }
    }

    /**
     * Retrieves explicitly added or automatically created {@link LotlValidator} instance.
     *
     * @return explicitly added or automatically created {@link LotlValidator} instance
     */
    LotlValidator getLotlValidator() {
        if (defaultImpl == null) {
            return lotlValidatorFactory.get();
        } else {
            return defaultImpl.getLotlValidator();
        }
    }

    private LotlValidator buildLotlValidator() {
        return new LotlValidator(this);
    }

    private static XmlSignatureValidator buildXmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        return new XmlSignatureValidator(trustedCertificatesStore);
    }


    private static final class LoggableResourceRetriever extends DefaultResourceRetriever {
        private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LotlService.class);

        public LoggableResourceRetriever() {
            // Default constructor
        }

        @Override
        public byte[] getByteArrayByUrl(URL url) throws IOException {
            LOGGER.info(MessageFormatUtil.format("Fetching resource from URL: {0}", url));
            return super.getByteArrayByUrl(url);
        }
    }
}
