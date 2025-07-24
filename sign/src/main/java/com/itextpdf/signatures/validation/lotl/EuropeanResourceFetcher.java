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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.signatures.validation.lotl.LotlValidator.JOURNAL_CERT_NOT_PARSABLE;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION;


/**
 * This class fetches the European Union Journal certificates from the trusted list configuration.
 * It reads the PEM certificates and returns them in a structured result.
 */
public class EuropeanResourceFetcher {


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

        try {
            result.setCertificates(factory.getCertificates());
        } catch (Exception e) {
            result.getLocalReport().addReportItem(
                    new ReportItem(LOTL_VALIDATION, JOURNAL_CERT_NOT_PARSABLE, e,
                            ReportItem.ReportItemStatus.INFO));
        }
        return result;
    }

    /**
     * Represents the result of fetching European Union Journal certificates.
     * Contains a list of report items and a list of certificates.
     */
    public static class Result {
        private final ValidationReport localReport;
        private List<Certificate> certificates;

        Result() {
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
         * Sets the list of certificates.
         *
         * @param certificates a list of Certificate objects to set
         */
        public void setCertificates(List<Certificate> certificates) {
            this.certificates = certificates;
        }
    }

}
