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
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.SafeCalling;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.net.URL;
import java.util.Arrays;

/**
 * Fetches the European List of Trusted Lists (Lotl) from a predefined URL.
 * <p>
 * This class is used to retrieve the Lotl XML file, which contains information about trusted lists in the European
 * Union.
 */
public class EuropeanLotlFetcher {
    private final LotlService service;

    /**
     * Constructs a new instance of {@link EuropeanLotlFetcher} with the specified LotlService.
     *
     * @param service the LotlService used to retrieve resources
     */
    public EuropeanLotlFetcher(LotlService service) {
        this.service = service;
    }


    /**
     * Loads the List of Trusted Lists (Lotl) from the predefined URL.
     *
     * @return the byte array containing the Lotl data.
     */
    public Result fetch() {
        final Result result = new Result();
        final EuropeanTrustedListConfigurationFactory factory = EuropeanTrustedListConfigurationFactory.getFactory()
                .get();
        SafeCalling.onExceptionLog(
                () -> {
                    final URL url = UrlUtil.toURL(factory.getTrustedListUri());
                    result.setLotlXml(service.getResourceRetriever().getByteArrayByUrl(url));
                    if (result.getLotlXml() == null || result.getLotlXml().length == 0) {
                        ReportItem reportItem = new ReportItem(LotlValidator.LOTL_VALIDATION,
                                MessageFormatUtil.format(
                                        SignExceptionMessageConstant.FAILED_TO_GET_EU_LOTL,
                                        factory.getTrustedListUri()),
                                ReportItem.ReportItemStatus.INVALID);
                        result.getLocalReport().addReportItem(reportItem);
                    }
                },
                result.getLocalReport(),
                e -> new ReportItem(LotlValidator.LOTL_VALIDATION,
                        MessageFormatUtil.format(
                                SignExceptionMessageConstant.FAILED_TO_GET_EU_LOTL, factory.getTrustedListUri()),
                        e, ReportItem.ReportItemStatus.INVALID));
        return result;
    }

    /**
     * Represents the result of fetching the List of Trusted Lists (Lotl).
     */
    public static class Result {
        private final ValidationReport localReport = new ValidationReport();
        private byte[] lotlXml;

        /**
         * Creates a new instance of {@link Result} with the provided Lotl XML data.
         *
         * @param lotlXml the byte array containing the Lotl XML data
         */
        public Result(byte[] lotlXml) {
            setLotlXml(lotlXml);
        }

        /**
         * Creates a new instance of {@link Result} with an empty report items list.
         */
        public Result() {
            //empty constructor
        }

        /**
         * Returns the Lotl XML data.
         *
         * @return the byte array containing the Lotl XML data
         */
        public byte[] getLotlXml() {
            if (lotlXml == null) {
                return null;
            }
            return Arrays.copyOf(lotlXml, lotlXml.length);
        }

        /**
         * Sets the Lotl XML data.
         *
         * @param lotlXml the byte array containing the Lotl XML data to set
         */
        final void setLotlXml(byte[] lotlXml) {
            if (lotlXml != null) {
                this.lotlXml = Arrays.copyOf(lotlXml, lotlXml.length);
            }
        }

        /**
         * Gets the list of report items generated during the fetching process.
         *
         * @return a list of {@link ReportItem} objects containing information about the fetching process
         */
        public ValidationReport getLocalReport() {
            return localReport;
        }

        boolean hasValidXml() {
            return lotlXml != null && lotlXml.length > 0;
        }
    }
}
