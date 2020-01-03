/*

This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
Authors: Bruno Lowagie, Paulo Soares, et al.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation with the addition of the
following permission added to Section 15 as permitted in Section 7(a):
FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
OF THIRD PARTY RIGHTS

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License
along with this program; if not, see http://www.gnu.org/licenses or write to
the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
Boston, MA, 02110-1301 USA, or download the license from the following URL:
http://itextpdf.com/terms-of-use/

The interactive user interfaces in modified source and object code versions
of this program must display Appropriate Legal Notices, as required under
Section 5 of the GNU Affero General Public License.

In accordance with Section 7(b) of the GNU Affero General Public License,
a covered work must retain the producer line in every PDF that is created
or manipulated using iText.

You can be released from the requirements of the license by purchasing
a commercial license. Buying such a license is mandatory as soon as you
develop commercial activities involving the iText software without
disclosing the source code of your own applications.
These activities include: offering paid services to customers as an ASP,
serving PDFs on the fly in a web application, shipping iText with a closed
source product.

For more information, please contact iText Software Corp. at this
address: sales@itextpdf.com
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

            String xmlReportPath = xmlReport.toURI().normalize().getPath();

            if (summary.getFailedParsingJobs() != 0) {
                errorMessage = "An error occurred while parsing current file. See report:  file:///" + xmlReportPath;
            } else if (summary.getFailedEncryptedJobs() != 0) {
                errorMessage = "VeraPDF execution failed - specified file is encrypted. See report:  file:///" + xmlReportPath;
            } else if (summary.getValidationSummary().getNonCompliantPdfaCount() != 0) {
                errorMessage = "VeraPDF verification failed. See verification results:  file:///" + xmlReportPath;
            } else {
                System.out.println("VeraPDF verification finished. See verification report: file:///" + xmlReportPath);
            }
        } catch (IOException | VeraPDFException exc) {
            errorMessage = "VeraPDF execution failed:\n" + exc.getMessage();
        }

        return errorMessage;
    }
}
