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
package com.itextpdf.test;

import com.itextpdf.test.pdfa.VeraPdfValidator;
import com.itextpdf.test.utils.FileUtil;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

// Android-Conversion-Skip-File (TODO DEVSIX-7377 introduce pdf\a validation on Android)
@Tag("UnitTest")
public class VeraPdfLoggerValidationTest extends ExtendedITextTest {

    static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdftest/cmp/VeraPdfLoggerValidationTest/";
    static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdftest/VeraPdfLoggerValidationTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkValidatorLogsNoOutputTest() throws IOException {
        String source = "pdfA2b_checkValidatorLogsTest.pdf";
        String target = "checkValidatorLogsNoOutputTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + source, DESTINATION_FOLDER + target);
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + target));
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
        ignoreRunningWhenNative((isNative) -> {
            Assertions.assertEquals(expectedWarningsForFileWithWarnings, new VeraPdfValidator().validate(DESTINATION_FOLDER + target));
        });
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
        ignoreRunningWhenNative((isNative) -> {
            Assertions.assertEquals(expectedWarningsForFileWithWarnings, new VeraPdfValidator().validate(DESTINATION_FOLDER + fileNameWithWarnings));
            //We check that the logs are empty after the first check
            Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + fileNameWithoutWarnings));
        });
    }

    @Test
    public void checkValidatorLogsForFileContainingErrorsTest() throws IOException {
        String source = "pdfA2b_checkValidatorLogsTest_with_errors.pdf";
        String target = "checkValidatorLogsForFileContainingErrorsTest.pdf";
        FileUtil.copy(SOURCE_FOLDER + source, DESTINATION_FOLDER + target);

        String expectedResponseForErrors = "VeraPDF verification failed. See verification results: file:";
        ignoreRunningWhenNative((isNative) -> {
            String result = new VeraPdfValidator().validate(DESTINATION_FOLDER + target);
            Assertions.assertTrue(result.startsWith(expectedResponseForErrors));
        });
    }

    private static final boolean isNative = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    public static void ignoreRunningWhenNative(Consumer<Object> test) {
        // VeraPdf doesn't work in native mode so skip VeraPdf validation
        if (isNative) {
            return;
        }
        test.accept(isNative);
    }

}
