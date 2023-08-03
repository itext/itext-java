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

import java.util.logging.Level;
import java.util.stream.Collectors;
import org.verapdf.component.LogsSummary;
import org.verapdf.component.LogsSummaryImpl;
import org.verapdf.core.VeraPDFException;
import org.verapdf.features.FeatureExtractorConfig;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.metadata.fixer.MetadataFixerConfig;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.pdfa.validation.validators.ValidatorFactory;
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

// Android-Conversion-Skip-File (TODO DEVSIX-7377 introduce pdf\a validation on Android)
public class VeraPdfValidator {

    public String validate(String filePath) {
        String errorMessage = null;

        try {
            File xmlReport = new File(filePath.substring(0, filePath.length() - ".pdf".length()) + ".xml");
            VeraGreenfieldFoundryProvider.initialise();

            // Initializes default VeraPDF configurations
            ProcessorConfig customProfile = ProcessorFactory.defaultConfig();
            FeatureExtractorConfig featuresConfig = customProfile.getFeatureConfig();
            ValidatorConfig valConfig = ValidatorFactory.createConfig(PDFAFlavour.NO_FLAVOUR, false, -1, false, true,
                    Level.WARNING);
            PluginsCollectionConfig plugConfig = customProfile.getPluginsCollectionConfig();
            MetadataFixerConfig metaConfig = customProfile.getFixerConfig();
            ProcessorConfig resultConfig = ProcessorFactory.fromValues(valConfig, featuresConfig,
                    plugConfig, metaConfig, EnumSet.of(TaskType.VALIDATE));

            // Creates validation processor
            BatchProcessor processor = ProcessorFactory.fileBatchProcessor(resultConfig);

            BatchSummary summary = processor.process(Collections.singletonList(new File(filePath)),
                    ProcessorFactory.getHandler(FormatOption.XML, true,
                            new FileOutputStream(String.valueOf(xmlReport)), false));

            LogsSummary logsSummary = LogsSummaryImpl.getSummary();
            String xmlReportPath = "file://" + xmlReport.toURI().normalize().getPath();

            if (summary.getFailedParsingJobs() != 0) {
                errorMessage = "An error occurred while parsing current file. See report:  " + xmlReportPath;
            } else if (summary.getFailedEncryptedJobs() != 0) {
                errorMessage = "VeraPDF execution failed - specified file is encrypted. See report:  " + xmlReportPath;
            } else if (summary.getValidationSummary().getNonCompliantPdfaCount() != 0) {
                errorMessage = "VeraPDF verification failed. See verification results:  " + xmlReportPath;
            } else {
                System.out.println("VeraPDF verification finished. See verification report: " + xmlReportPath);

                if (logsSummary.getLogsCount() != 0) {
                    errorMessage = "The following warnings and errors were logged during validation:";
                    errorMessage += logsSummary.getLogs().stream()
                            .map(log -> "\n" + log.getLevel() + ": " + log.getMessage())
                            .sorted()
                            .collect(Collectors.joining());
                }
            }
        } catch (IOException | VeraPDFException exc) {
            errorMessage = "VeraPDF execution failed:\n" + exc.getMessage();
        }

        return errorMessage;
    }
}
