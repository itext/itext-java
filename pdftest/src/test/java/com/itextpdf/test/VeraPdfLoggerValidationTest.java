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
package com.itextpdf.test;

import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;
import com.itextpdf.test.utils.FileUtil;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

// Android-Conversion-Skip-File (TODO DEVSIX-7377 introduce pdf\a validation on Android)
@Category(UnitTest.class)
public class VeraPdfLoggerValidationTest extends ExtendedITextTest {

    static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdftest/cmp/VeraPdfLoggerValidationTest/";
    static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdftest/VeraPdfLoggerValidationTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkValidatorLogsNoOutputTest() throws IOException {
        String source = "pdfA2b_checkValidatorLogsTest.pdf";
        String target = "checkValidatorLogsNoOutputTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + source, DESTINATION_FOLDER + target);
        Assert.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + target));
    }

    @Test
    public void checkValidatorLogsWithWarningTest() throws IOException {
        String source = "pdfA2b_checkValidatorLogsTest_with_warnings.pdf";
        String target = "checkValidatorLogsWitWarningTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + source, DESTINATION_FOLDER + target);
        String expectedWarningsForFileWithWarnings = "The following warnings and errors were logged during validation:\n"
                + "WARNING: Invalid embedded cff font. Charset range exceeds number of glyphs\n"
                + "WARNING: Missing OutputConditionIdentifier in an output intent dictionary\n"
                + "WARNING: The Top DICT does not begin with ROS operator";
        Assert.assertEquals(expectedWarningsForFileWithWarnings, new VeraPdfValidator().validate(DESTINATION_FOLDER + target));
    }

    @Test
    public void checkValidatorLogsCleanupTest() throws IOException {
        String fileNameWithWarnings = "pdfA2b_checkValidatorLogsTest_with_warnings.pdf";
        String fileNameWithoutWarnings = "pdfA2b_checkValidatorLogsTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + fileNameWithWarnings, DESTINATION_FOLDER + fileNameWithWarnings);
        FileUtil.copy(SOURCE_FOLDER + fileNameWithoutWarnings, DESTINATION_FOLDER + fileNameWithoutWarnings);

        String expectedWarningsForFileWithWarnings = "The following warnings and errors were logged during validation:\n"
                + "WARNING: Invalid embedded cff font. Charset range exceeds number of glyphs\n"
                + "WARNING: Missing OutputConditionIdentifier in an output intent dictionary\n"
                + "WARNING: The Top DICT does not begin with ROS operator";
        Assert.assertEquals(expectedWarningsForFileWithWarnings, new VeraPdfValidator().validate(DESTINATION_FOLDER + fileNameWithWarnings));

        //We check that the logs are empty after the first check
        Assert.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + fileNameWithoutWarnings));
    }

    @Test
    public void checkValidatorLogsForFileContainingErrorsTest() throws IOException {
        String source = "pdfA2b_checkValidatorLogsTest_with_errors.pdf";
        String target = "checkValidatorLogsForFileContainingErrorsTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + source, DESTINATION_FOLDER + target);

        String expectedResponseForErrors = "VeraPDF verification failed. See verification results: file:";
        String result =  new VeraPdfValidator().validate(DESTINATION_FOLDER + target);
        Assert.assertTrue(result.startsWith(expectedResponseForErrors));
    }
}
