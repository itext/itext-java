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
package com.itextpdf.kernel.crypto.pdfencryption;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("BouncyCastleIntegrationTest")
public class UnicodeBasedPasswordEncryptionTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/pdfencryption/UnicodeBasedPasswordEncryptionTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/pdfencryption/UnicodeBasedPasswordEncryptionTest/";

    private static Map<String, SaslPreparedString> nameToSaslPrepared;

    PdfEncryptionTestUtils encryptionUtil = new PdfEncryptionTestUtils(destinationFolder,sourceFolder);

    static {
        // values are calculated with com.ibm.icu.text.StringPrep class in icu4j v58.2 lib
        nameToSaslPrepared = new LinkedHashMap<>();

        //الرحيم
        nameToSaslPrepared.put("arabic01",
                new SaslPreparedString("\u0627\u0644\u0631\u062D\u064A\u0645",
                                       "\u0627\u0644\u0631\u062D\u064A\u0645"));
        //ال,ر11حيم
        nameToSaslPrepared.put("arabic02",
                new SaslPreparedString("\u0627\u0644,\u063111\u062D\u064A\u0645",
                                       "\u0627\u0644,\u063111\u062D\u064A\u0645"));
        // لـه
        nameToSaslPrepared.put("arabic03",
                new SaslPreparedString("\u0644\u0640\u0647",
                                       "\u0644\u0640\u0647"));
        // ﻻ
        nameToSaslPrepared.put("arabic04",
                new SaslPreparedString("\ufefb",
                                       "\u0644\u0627"));
        // لا
        nameToSaslPrepared.put("arabic05",
                new SaslPreparedString("\u0644\u0627",
                                       "\u0644\u0627"));
        // शांति    देवनागरी
        nameToSaslPrepared.put("devanagari01",
                new SaslPreparedString("\u0936\u093e\u0902\u0924\u093f    \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940",
                                       "\u0936\u093E\u0902\u0924\u093F    \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940"));
        // की प्राचीनतम
        nameToSaslPrepared.put("devanagari02",
                new SaslPreparedString("\u0915\u0940 \u092A\u094D\u0930\u093E\u091A\u0940\u0928\u0924\u092E",
                                       "\u0915\u0940 \u092A\u094D\u0930\u093E\u091A\u0940\u0928\u0924\u092E"));
        // ਗ੍ਰੰਥ ਸਾਹਿਬ
        nameToSaslPrepared.put("gurmukhi01",
                new SaslPreparedString("\u0A17\u0A4D\u0A30\u0A70\u0A25 \u0A38\u0A3E\u0A39\u0A3F\u0A2C",
                                       "\u0A17\u0A4D\u0A30\u0A70\u0A25 \u0A38\u0A3E\u0A39\u0A3F\u0A2C"));
        // ញ្ចូ
        nameToSaslPrepared.put("khmer01",
                new SaslPreparedString("\u1789\u17D2\u1785\u17BC",
                                       "\u1789\u17D2\u1785\u17BC"));
        //இலக்கிய நடை கூட மக்களால்
        nameToSaslPrepared.put("tamil01",
                new SaslPreparedString("\u0B87\u0BB2\u0B95\u0BCD\u0B95\u0BBF\u0BAF \u0BA8\u0B9F\u0BC8 \u0B95\u0BC2\u0B9F \u0BAE\u0B95\u0BCD\u0B95\u0BB3\u0BBE\u0BB2\u0BCD",
                                       "\u0B87\u0BB2\u0B95\u0BCD\u0B95\u0BBF\u0BAF \u0BA8\u0B9F\u0BC8 \u0B95\u0BC2\u0B9F \u0BAE\u0B95\u0BCD\u0B95\u0BB3\u0BBE\u0BB2\u0BCD"));
        // ประเทศไทย
        nameToSaslPrepared.put("thai01",
                new SaslPreparedString("\u0E1B\u0E23\u0E30\u0E40\u0E17\u0E28\u0E44\u0E17\u0E22",
                                       "\u0E1B\u0E23\u0E30\u0E40\u0E17\u0E28\u0E44\u0E17\u0E22"));
        nameToSaslPrepared.put("unicodeBom01",
                new SaslPreparedString("\uFEFFab\uFEFFc",
                                       "abc"));
        nameToSaslPrepared.put("emoji01",
                new SaslPreparedString("\u267B",
                                       "\u267B"));
        nameToSaslPrepared.put("rfc4013Example01",
                new SaslPreparedString("I\u00ADX",
                                       "IX"));
        nameToSaslPrepared.put("rfc4013Example02",
                new SaslPreparedString("user",
                                       "user"));
        nameToSaslPrepared.put("rfc4013Example03",
                new SaslPreparedString("\u00AA",
                                       "a"));
        // match rfc4013Example01
        nameToSaslPrepared.put("rfc4013Example04",
                new SaslPreparedString("\u2168",
                                       "IX"));
        nameToSaslPrepared.put("nonAsciiSpace01",
                new SaslPreparedString("\u2008 \u2009 \u200A \u200B",
                                       "       "));
        // normalization tests
        nameToSaslPrepared.put("nfkcNormalization01",
                new SaslPreparedString("\u09C7\u09BE",
                                       "\u09CB"));
        nameToSaslPrepared.put("nfkcNormalization02",
                new SaslPreparedString("\u30AD\u3099\u30AB\u3099",
                                       "\u30AE\u30AC"));
        nameToSaslPrepared.put("nfkcNormalization03",
                new SaslPreparedString("\u3310",
                                       "\u30AE\u30AC"));
        nameToSaslPrepared.put("nfkcNormalization04",
                new SaslPreparedString("\u1100\u1161\u11A8",
                                       "\uAC01"));
        nameToSaslPrepared.put("nfkcNormalization05",
                new SaslPreparedString("\uF951",
                                       "\u964B"));

        /*

        // Arabic
        bidirectional check fail:  "\u0627\u0644\u0631\u0651\u064E\u200C\u062D\u0652\u0645\u064E\u0640\u0670\u0646\u0650"
        bidirectional check fail:  "1\u0627\u0644\u0631\u062D\u064A\u06452"

        // RFC4013 examples
        bidirectional check fail:  "\u0627\u0031"
        prohibited character fail: "\u0007"

        // unassigned code point for Unicode 3.2
        "\uD83E\uDD14"
        "\u038Ba\u038Db\u03A2c\u03CF"

        */
    }

    private static class SaslPreparedString {
        String unicodeInputString;
        String preparedString;

        SaslPreparedString(String unicodeInputString, String preparedString) {
            this.unicodeInputString = unicodeInputString;
            this.preparedString = preparedString;
        }
    }

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, 
            ignore = true))
    public void aes256EncryptedPdfWithUnicodeBasedPassword() throws IOException, InterruptedException {
        String fileNameTemplate = "unicodePassword_";
        for (Map.Entry<String, SaslPreparedString> entry : nameToSaslPrepared.entrySet()) {
            String filename = fileNameTemplate + entry.getKey() + ".pdf";
            byte[] ownerPassword = entry.getValue().preparedString.getBytes(StandardCharsets.UTF_8);
            encryptAes256AndCheck(filename, ownerPassword);
        }
    }

    // TODO after DEVSIX-1220 finished:
    // 1.  Create with both inputString and prepareString.
    // 1.1 Check opening both of these documents with both strings.
    // 2.  Try encrypt document with invalid input string.
    // 3.  Try open encrypted document with password that contains unassigned code points and ensure error is due to wrong password instead of the invalid input string.

    private void encryptAes256AndCheck(String filename, byte[] ownerPassword) throws IOException, InterruptedException {
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(PdfEncryptionTestUtils.USER, ownerPassword, permissions, EncryptionConstants.ENCRYPTION_AES_256)
                .setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename, writerProperties.addXmpMetadata());
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY, PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfPage page = document.addNewPage();
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(page, PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);

        page.flush();
        document.close();

        encryptionUtil.checkDecryptedWithPasswordContent(destinationFolder + filename, ownerPassword, PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", ownerPassword, ownerPassword);
        if (compareResult != null) {
            fail(compareResult);
        }
    }
}
