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

import com.itextpdf.commons.datastructures.ConcurrentHashSet;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.lotl.CountrySpecificLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides services for managing the single country List of Trusted Lists (LOTL) and related resources.
 * It includes methods for fetching, validating, and caching LOTL data.
 *
 * <p>
 * You should use this service if you have only a country specific LOTL file with certificates you trust.
 * First, you create an instance of {@link CountrySpecificLotl} and then pass it to the constructor of
 * {@link SingleFileLotlService} together with fetching properties and the certificates to validate LOTL file.
 * Then this instance can be passed to {@link com.itextpdf.signatures.validation.ValidatorChainBuilder#withLotlService}
 * so that the certificates from the LOTL file are used for signature validation.
 */
public class SingleFileLotlService extends LotlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileLotlService.class);

    private final CountrySpecificLotl countrySpecificLotl;
    private final List<Certificate> certificates;
    private final ConcurrentHashSet<IServiceContext> contexts = new ConcurrentHashSet<>();
    private ValidationReport report;

    /**
     * Creates a new instance of {@link SingleFileLotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     * @param countrySpecificLotl {@link CountrySpecificLotl} for a LOTL file to serve
     * @param certificates a list of certificates to validate LOTL file
     */
    public SingleFileLotlService(LotlFetchingProperties lotlFetchingProperties,
            CountrySpecificLotl countrySpecificLotl, List<String> certificates) {
        super(lotlFetchingProperties);
        this.countrySpecificLotl = countrySpecificLotl;
        this.certificates = getGenerallyTrustedCertificates(certificates);

        // TODO DEVSIX-9710: Split LotlFetchingProperties onto classes relevant for specific services
        if (!lotlFetchingProperties.getSchemaNames().isEmpty()) {
            LOGGER.warn(SignLogMessageConstant.SCHEMA_NAMES_CONFIGURATION_PROPERTY_IGNORED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromCache(InputStream in) {
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        try {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading from cache input stream", e);
        }

        String jsonString = sb.toString();
        JsonValue json = JsonValue.fromJson(jsonString);
        if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;
            this.report = convertJsonToReport((JsonObject) jsonObject.getFields().get("report"));
            List<IServiceContext> cachedContexts = convertJsonToServiceContexts(
                    (JsonObject) jsonObject.getFields().get("serviceContexts"));
            this.contexts.clear();
            this.contexts.addAll(cachedContexts);
        } else {
            throw new IllegalArgumentException("Invalid JSON format in cache input stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serializeCache(OutputStream outputStream) throws IOException {
        // TODO: DEVSIX-9626
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationReport getValidationResult() {
        return this.report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IServiceContext> getNationalTrustedCertificates() {
        return new ArrayList<>(contexts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadFromNetwork() {
        CountrySpecificLotlFetcher lotlr = new CountrySpecificLotlFetcher(this);
        ValidationReport localReport = new ValidationReport();
        Map<String, Result> f = lotlr.validateCountrySpecificFiles(certificates,
                Collections.singletonList(countrySpecificLotl), this);
        contexts.clear();
        for (Result value : f.values()) {
            localReport.merge(value.getLocalReport());
            contexts.addAll(value.getContexts());
        }
        this.report = localReport;
    }

    private static ValidationReport convertJsonToReport(JsonObject jsonObject) {
        ValidationReport cachedReport = new ValidationReport();
        // Implement the logic to convert JsonObject to ValidationReport
        return cachedReport;
    }

    private static List<IServiceContext> convertJsonToServiceContexts(JsonObject jsonObject) {
        List<IServiceContext> cachedContexts = new ArrayList<>();
        // Implement the logic to convert JsonObject to List<IServiceContext>
        return cachedContexts;
    }

    private static List<Certificate> getGenerallyTrustedCertificates(List<String> certificates) {
        final ArrayList<Certificate> result = new ArrayList<>();
        for (String certificateString : certificates) {
            Certificate certificate = CertificateUtil.readCertificatesFromPem(
                    new ByteArrayInputStream(certificateString.getBytes(
                            StandardCharsets.UTF_8)))[0];
            result.add(certificate);
        }

        return result;
    }
}
