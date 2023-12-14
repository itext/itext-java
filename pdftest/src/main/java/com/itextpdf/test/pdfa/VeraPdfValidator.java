/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.test.pdfa;

import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.TaskType;
import org.verapdf.processor.plugins.PluginsCollectionConfig;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.reports.BatchSummary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;

public class VeraPdfValidator {

    public String validate(String filePath) {
        String errorMessage = null;

        try {
            File xmlReport = new File(filePath.substring(0, filePath.length() - ".pdf".length()) + ".xml");
            VeraGreenfieldFoundryProvider.initialise();

            // Initializes default VeraPDF configurations
            ProcessorConfig customProfile = ProcessorFactory.defaultConfig();
            FeatureExtractorConfig featuresConfig = customProfile.getFeatureConfig();
            ValidatorConfig valConfig = customProfile.getValidatorConfig();
            PluginsCollectionConfig plugConfig = customProfile.getPluginsCollectionConfig();
            MetadataFixerConfig metaConfig = customProfile.getFixerConfig();
            ProcessorConfig resultConfig = ProcessorFactory.fromValues(valConfig, featuresConfig,
                    plugConfig, metaConfig, EnumSet.of(TaskType.VALIDATE));

            // Creates validation processor
            BatchProcessor processor = ProcessorFactory.fileBatchProcessor(resultConfig);

            BatchSummary summary = processor.process(Collections.singletonList(new File(filePath)),
                    ProcessorFactory.getHandler(FormatOption.XML, true,
                            new FileOutputStream(String.valueOf(xmlReport)), 125, false));

            String xmlReportPath = "file://" + xmlReport.toURI().normalize().getPath();

            if (summary.getFailedParsingJobs() != 0) {
                errorMessage = "An error occurred while parsing current file. See report:  " + xmlReportPath;
            } else if (summary.getFailedEncryptedJobs() != 0) {
                errorMessage = "VeraPDF execution failed - specified file is encrypted. See report:  " + xmlReportPath;
            } else if (summary.getValidationSummary().getNonCompliantPdfaCount() != 0) {
                errorMessage = "VeraPDF verification failed. See verification results:  " + xmlReportPath;
            } else {
                System.out.println("VeraPDF verification finished. See verification report: " + xmlReportPath);
            }
        } catch (IOException | VeraPDFException exc) {
            errorMessage = "VeraPDF execution failed:\n" + exc.getMessage();
        }

        return errorMessage;
    }
}
