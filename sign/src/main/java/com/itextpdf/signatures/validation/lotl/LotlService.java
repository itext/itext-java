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
 * This class provides services for managing the List of Trusted Lists (Lotl) and related resources.
 * It includes methods for fetching, validating, and caching Lotl data, as well as managing the European Resource
 * Fetcher and Country-Specific Lotl Fetcher.
 * It also allows for setting custom resource retrievers and cache timeouts.
 */
//TODO AAAAAAAAAAA: Make this class abstract and remove EuropeanLotlService specific methods to EuropeanLotlService
public class LotlService implements AutoCloseable {

    private static final Object GLOBAL_SERVICE_LOCK = new Object();
    private static final String DEFAULT_USER_AGENT = "iText-lotl-retriever/1.0";
    // Global service
    static LotlService GLOBAL_SERVICE;
    protected final LotlFetchingProperties lotlFetchingProperties;

    private boolean cacheInitialized = false;
    private Timer cacheTimer = null;
    private IResourceRetriever resourceRetriever;
    private Function<TrustedCertificatesStore, XmlSignatureValidator> xmlSignatureValidatorFactory;
    private Supplier<LotlValidator> lotlValidatorFactory;


    /**
     * Creates a new instance of {@link LotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     */
    public LotlService(LotlFetchingProperties lotlFetchingProperties) {
        this.lotlFetchingProperties = lotlFetchingProperties;
        this.xmlSignatureValidatorFactory = trustedCertificatesStore -> buildXmlSignatureValidator(
                trustedCertificatesStore);
        this.lotlValidatorFactory = () -> buildLotlValidator();
        this.resourceRetriever = new LoggableResourceRetriever();
        ((LoggableResourceRetriever) this.resourceRetriever).setRequestHeaders(
                Collections.singletonMap("User-Agent",DEFAULT_USER_AGENT));
    }

    /**
     * Initializes the global cache with the provided LotlFetchingProperties.
     * This method must be called before using the LotlService to ensure that the cache is set up.
     * <p>
     * If you are using a custom implementation of {@link LotlService} you can use the instance method.
     *
     * @param lotlFetchingProperties the LotlFetchingProperties to use for initializing the cache
     */
    public static void initializeGlobalCache(LotlFetchingProperties lotlFetchingProperties) {
        synchronized (GLOBAL_SERVICE_LOCK) {
            if (GLOBAL_SERVICE == null) {
                GLOBAL_SERVICE = new LotlService(lotlFetchingProperties);
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
     * Sets the cache for the LotlService.
     * <p>
     * This method allows you to provide a custom implementation of {@link LotlServiceCache} to be used
     * for caching Lotl data, pivot files, and country-specific Lotls.
     *
     * @param cache the custom cache to be used for caching Lotl data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    //TODO AAAAAA: Remove this method to EuropeanLotlService
    public LotlService withLotlServiceCache(LotlServiceCache cache) {
        if (this instanceof EuropeanLotlService){
            ((EuropeanLotlService) this).cache = cache;
            return this;
        } else {
            throw new IllegalArgumentException(
                    "Due to bad programming practices, lotl service cache can be set only for EuropeanLotlService");
        }
    }

    /**
     * Sets a custom resource retriever for fetching resources.
     * <p>
     * This method allows you to provide a custom implementation of {@link IResourceRetriever} to be used
     * for fetching resources such as the Lotl XML, pivot files, and country-specific Lotls.
     * <p>
     * Multiple lotl endpoints require a userAgent header to be sent. This should be taken into account
     * when providing a custom  {@link IResourceRetriever}.
     *
     * @param resourceRetriever the custom resource retriever to be used for fetching resources
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public final LotlService withCustomResourceRetriever(IResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
        return this;
    }

    /**
     * Initializes the cache with the latest Lotl data and related resources.
     */
    public void initializeCache() {
        initializeCache(null);
    }


    //TODO AAAAAA: Make this method abstract
    public void loadFromCache(InputStream in) {
        throw new PdfException("Not implemented yet");
    }

    //TODO AAAAAA: Make this method abstract
    public ValidationReport getValidationResult() {
        throw new IllegalArgumentException("Due to bad programming practices, something something");
    }


    //TODO AAAAAA: Make this method abstract
    public List<IServiceContext> getNationalTrustedCertificates() {
        throw new IllegalArgumentException("Due to bad programming practices, something something");
    }

    /**
     * Initializes the cache with the latest Lotl data and related resources.
     * <p>
     * Important: By default when providing a stream, we will still set up a timer to refresh the cache periodically.
     * If you don't want this behavior, please set
     * {@link  LotlFetchingProperties#setRefreshIntervalCalculator(LongUnaryOperator)} to int.Max.
     *
     * @param stream InputStream to read the cached data from. If null, the data will be fetched from the network.
     *               The data can be serialized using {@link #serializeCache(OutputStream)} method.
     */
    public void initializeCache(InputStream stream) {
        setupTimer();
        if (stream != null) {
            loadFromCache(stream);
        } else {
            loadFromNetwork();
        }
        cacheInitialized = true;
    }

    /**
     * Sets the pivot fetcher for the Lotl service.
     *
     * @param pivotFetcher the pivot fetcher to be used for fetching and validating pivot files
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    //TODO AAAAAA: Remove this method to EuropeanLotlService
    public LotlService withPivotFetcher(PivotFetcher pivotFetcher) {
        if (this instanceof EuropeanLotlService) {
            ((EuropeanLotlService) this).pivotFetcher = pivotFetcher;
        } else {
            throw new IllegalArgumentException(
                    "Due to bad programming practices, pivot fetcher can be set only for EuropeanLotlService");
        }
        return this;
    }

    /**
     * Sets the country-specific Lotl fetcher for the Lotl service.
     *
     * @param countrySpecificLotlFetcher the country-specific Lotl fetcher to be used for fetching and validating
     *                                   country-specific Lotls
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    //TODO AAAAAA: Remove this method to EuropeanLotlService
    public LotlService withCountrySpecificLotlFetcher(CountrySpecificLotlFetcher countrySpecificLotlFetcher) {
        if (this instanceof EuropeanLotlService) {
            ((EuropeanLotlService) this).countrySpecificLotlFetcher = countrySpecificLotlFetcher;
        } else {
            throw new IllegalArgumentException(
                    "Due to bad programming practices, country specific lotl fetcher can be set only for "
                            + "EuropeanLotlService");
        }
        return this;
    }

    /**
     * Sets the European List of Trusted Lists (Lotl) byte fetcher for the Lotl service.
     *
     * @param fetcher the fetcher to be used for fetching the Lotl XML data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    //TODO AAAAAA: Remove this method to EuropeanLotlService
    public LotlService withEuropeanLotlFetcher(EuropeanLotlFetcher fetcher) {
        if (this instanceof EuropeanLotlService) {
            ((EuropeanLotlService) this).lotlByteFetcher = fetcher;
        } else {
            throw new IllegalArgumentException(
                    "Due to bad programming practices, european lotl fetcher can be set only for EuropeanLotlService");
        }
        return this;
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
        this.xmlSignatureValidatorFactory = xmlSignatureValidatorFactory;
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
        this.lotlValidatorFactory = lotlValidatorFactory;
        return this;
    }

    /**
     * Sets the European Resource Fetcher for the LotlService.
     *
     * @param europeanResourceFetcher the European Resource Fetcher to be used for fetching EU journal certificates
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    //TODO AAAAAA: Remove this method to EuropeanLotlService
    public LotlService withEuropeanResourceFetcher(EuropeanResourceFetcher europeanResourceFetcher) {
        if (this instanceof EuropeanLotlService) {
            ((EuropeanLotlService) this).europeanResourceFetcher = europeanResourceFetcher;
        } else {
            throw new IllegalArgumentException(
                    "Due to bad programming practices, european resource fetcher can be set only for "
                            + "EuropeanLotlService");
        }
        return this;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void close() {
        cancelTimer();
    }

    /**
     * Serializes the current state of the cache to the provided output stream.
     *
     * @param outputStream the output stream to which the cache will be serialized.
     *
     * @throws IOException if an I/O error occurs during serialization.
     */
    public void serializeCache(OutputStream outputStream) throws IOException {
        throw new RuntimeException("We are sorry, this method is not implemented yet. I CRY EVERYTIME");
    }

    //TODO AAAAAA: Make this method abstract
    protected void loadFromNetwork() {
        throw new IllegalArgumentException("Think of this method as abstract, ok? But instead of compile time checking we do "
                + "runtime checking. Because why not it's what javascript does");
    }

    //TODO AAAAAA: Make this method abstract
    protected void tryAndRefreshCache() {
        throw new IllegalArgumentException("Think of this method as abstract, ok? But instead of compile time checking we do "
                + "runtime checking. Because why not it's what javascript does");
    }

    /**
     * Sets up a timer to periodically refresh the LOTL cache.
     * <p>
     * The timer will use the refresh interval calculated based on the stale-ness of the cache.
     * If the cache is null, it will create a new instance of {@link InMemoryLotlServiceCache}.
     */
    protected void setupTimer() {
        long staleNessInMillis = lotlFetchingProperties.getCacheStalenessInMilliseconds();
        TimerUtil.stopTimer(cacheTimer);
        LongUnaryOperator cacheRefreshTimer = lotlFetchingProperties.getRefreshIntervalCalculator();
        long refreshInterval = cacheRefreshTimer.applyAsLong(staleNessInMillis);
        cacheTimer = TimerUtil.newTimerWithRecurringTask(() -> tryAndRefreshCache(), refreshInterval, refreshInterval);
    }

    /**
     * Cancels timer, if it was already set up.
     */
    protected void cancelTimer() {
        if (cacheTimer != null) {
            TimerUtil.stopTimer(cacheTimer);
        }
    }


    boolean isCacheInitialized() {
        return cacheInitialized;
    }


    /**
     * Gets the resource retriever used by the Lotl service.
     *
     * @return the {@link IResourceRetriever} instance used for fetching resources
     */
    IResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    /**
     * Retrieves explicitly added or automatically created {@link XmlSignatureValidator} instance.
     *
     * @return explicitly added or automatically created {@link XmlSignatureValidator} instance.
     */
    XmlSignatureValidator getXmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        return xmlSignatureValidatorFactory.apply(trustedCertificatesStore);
    }

    LotlFetchingProperties getLotlFetchingProperties() {
        return lotlFetchingProperties;
    }

    /**
     * Retrieves explicitly added or automatically created {@link LotlValidator} instance.
     *
     * @return explicitly added or automatically created {@link LotlValidator} instance
     */
    LotlValidator getLotlValidator() {
        return lotlValidatorFactory.get();
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
