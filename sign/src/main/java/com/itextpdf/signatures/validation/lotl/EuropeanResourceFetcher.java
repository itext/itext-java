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

import com.itextpdf.commons.json.IJsonSerializable;
import com.itextpdf.commons.json.JsonArray;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonString;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.SignJsonSerializerHelper;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.SafeCalling;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.signatures.validation.lotl.LotlValidator.JOURNAL_CERT_NOT_PARSABLE;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION;

/**
 * This class fetches the European Union Journal certificates from the trusted list configuration.
 * It reads the PEM certificates and returns them in a structured result.
 */
public class EuropeanResourceFetcher {
    private static final String JSON_KEY_CERTIFICATES = "certificates";
    private static final String JSON_KEY_LOCAL_REPORT = "localReport";
    private static final String JSON_KEY_CURRENTLY_SUPPORTED_PUBLICATION = "currentlySupportedPublication";

    /**
     * Default constructor for EuropeanResourceFetcher.
     * Initializes the fetcher without any specific configuration.
     */
    public EuropeanResourceFetcher() {
        // Default constructor
    }

    /**
     * Fetches the European Union Journal certificates.
     *
     * @return a Result object containing a list of certificates and any report items
     */
    public Result getEUJournalCertificates() {
        Result result = new Result();
        EuropeanTrustedListConfigurationFactory factory =
                EuropeanTrustedListConfigurationFactory.getFactory().get();
        result.setCurrentlySupportedPublication(factory.getCurrentlySupportedPublication());

        SafeCalling.onExceptionLog(
                () -> result.setCertificates(factory.getCertificates()),
                result.getLocalReport(),
                e -> new ReportItem(LOTL_VALIDATION, JOURNAL_CERT_NOT_PARSABLE, e, ReportItem.ReportItemStatus.INFO));
        return result;
    }

    /**
     * Represents the result of fetching European Union Journal certificates.
     * Contains a list of report items and a list of certificates.
     */
    public static class Result implements IJsonSerializable {
        private ValidationReport localReport;
        private List<Certificate> certificates;
        private String currentlySupportedPublication;

        /**
         * Create a new Instance of {@link Result}.
         */
        public Result() {
            this.localReport = new ValidationReport();
            certificates = new ArrayList<>();
        }

        /**
         * Gets the list of report items.
         *
         * @return a ValidationReport object containing report items
         */
        public ValidationReport getLocalReport() {
            return localReport;
        }

        /**
         * Gets the list of certificates.
         *
         * @return a list of Certificate objects
         */
        public List<Certificate> getCertificates() {
            return certificates;
        }

        /**
         * Gets string constant representing currently used Official Journal publication.
         *
         * @return {@link String} constant representing currently used Official Journal publication
         */
        public String getCurrentlySupportedPublication() {
            return currentlySupportedPublication;
        }

        /**
         * Sets the list of certificates.
         *
         * @param certificates a list of Certificate objects to set
         */
        public void setCertificates(List<Certificate> certificates) {
            this.certificates = certificates;
        }

        /**
         * Sets string constant representing currently used Official Journal publication.
         *
         * @param currentlySuppostedPublication {@link String}
         * constant representing currently used Official Journal publication
         */
        public void setCurrentlySupportedPublication(String currentlySuppostedPublication) {
            this.currentlySupportedPublication = currentlySuppostedPublication;
        }

        /**
         * {@inheritDoc}.
         *
         * @return {@inheritDoc}
         */
        @Override
        public JsonValue toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(JSON_KEY_LOCAL_REPORT, localReport.toJson());

            JsonArray certificatesJson = new JsonArray(certificates.stream().map(certificate ->
                    SignJsonSerializerHelper.serializeCertificate(certificate)).collect(Collectors.toList()));
            jsonObject.add(JSON_KEY_CERTIFICATES, certificatesJson);

            jsonObject.add(JSON_KEY_CURRENTLY_SUPPORTED_PUBLICATION, new JsonString(currentlySupportedPublication));
            return jsonObject;
        }

        /**
         * Deserializes {@link JsonValue} into {@link EuropeanResourceFetcher.Result}.
         *
         * @param jsonValue {@link JsonValue} to deserialize
         *
         * @return deserialized {@link EuropeanResourceFetcher.Result}
         */
        public static EuropeanResourceFetcher.Result fromJson(JsonValue jsonValue) {
            JsonObject europeanResourceFetcherResultJson = (JsonObject) jsonValue;
            JsonObject localReportJson =
                    (JsonObject) europeanResourceFetcherResultJson.getField(JSON_KEY_LOCAL_REPORT);
            ValidationReport validationReportFromJson = ValidationReport.fromJson(localReportJson);

            JsonArray certificatesJson =
                    (JsonArray) europeanResourceFetcherResultJson.getField(JSON_KEY_CERTIFICATES);
            List<Certificate> certificatesFromJson = certificatesJson.getValues().stream().map(certificateJson ->
                    SignJsonSerializerHelper.deserializeCertificate(certificateJson)).collect(Collectors.toList());
            String currentlySupportedPublicationFromJson = ((JsonString) europeanResourceFetcherResultJson.getField(
                    JSON_KEY_CURRENTLY_SUPPORTED_PUBLICATION)).getValue();
            EuropeanResourceFetcher.Result resultFromJson = new Result();
            resultFromJson.localReport = validationReportFromJson;
            resultFromJson.certificates = certificatesFromJson;
            resultFromJson.currentlySupportedPublication = currentlySupportedPublicationFromJson;
            return resultFromJson;
        }
    }
}
