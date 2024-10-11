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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.HashMap;

@Tag("BouncyCastleIntegrationTest")
public class StandardHandlerUsingAesGcmTest extends ExtendedITextTest {
    public static final String SRC =
            "./src/test/resources/com/itextpdf/kernel/crypto/securityhandler/StandardHandlerUsingAesGcmTest/";
    public static final String DEST =
            "./target/test/com/itextpdf/kernel/crypto/securityhandler/StandardHandlerUsingAesGcmTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final byte[] OWNER_PASSWORD = "supersecret".getBytes(StandardCharsets.UTF_8);
    private static final byte[] USER_PASSWORD = "secret".getBytes(StandardCharsets.UTF_8);

    @BeforeAll
    public static void setUp() {
        createOrClearDestinationFolder(DEST);
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
                    ignore = true)})
    public void simpleEncryptDecryptTest() throws Exception {
        String srcFile = SRC + "simpleDocument.pdf";
        String encryptedCmpFile = SRC + "cmp_encryptedSimpleDocument.pdf";
        String outFile = DEST + "simpleEncryptDecrypt.pdf";

        // Set usage permissions.
        int perms = EncryptionConstants.ALLOW_PRINTING | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        WriterProperties wProps = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, perms, EncryptionConstants.ENCRYPTION_AES_GCM);
        // Instantiate input/output document.
        try (PdfDocument docIn = new PdfDocument(new PdfReader(srcFile));
             PdfDocument docOut = new PdfDocument(new PdfWriter(outFile, wProps))) {
            // Copy one page from input to output.
            docIn.copyPagesTo(1, 1, docOut);
        }

        new CToolNoDeveloperExtension().compareByContent(outFile, srcFile, DEST, "diff", USER_PASSWORD, null);
        new CompareTool().compareByContent(outFile, encryptedCmpFile, DEST, "diff", USER_PASSWORD, USER_PASSWORD);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = VersionConforming.NOT_SUPPORTED_AES_GCM),
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, ignore = true)})
    public void simpleEncryptDecryptPdf15Test() throws Exception {
        String srcFile = SRC + "simpleDocument.pdf";
        String outFile = DEST + "notSupportedVersionDocument.pdf";

        int perms = EncryptionConstants.ALLOW_PRINTING | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        WriterProperties wProps = new WriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, perms, EncryptionConstants.ENCRYPTION_AES_GCM);
        PdfDocument ignored = new PdfDocument(new PdfReader(srcFile), new PdfWriter(outFile, wProps));
        ignored.close();
        new CToolNoDeveloperExtension().compareByContent(outFile, srcFile, DEST, "diff", USER_PASSWORD, null);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void knownOutputTest() throws Exception {
        String srcFile = SRC + "encryptedDocument.pdf";
        String outFile = DEST + "encryptedDocument.pdf";
        String cmpFile = SRC + "simpleDocument.pdf";
        try (PdfDocument ignored = new PdfDocument(new PdfReader(srcFile,
                new ReaderProperties().setPassword(OWNER_PASSWORD)), new PdfWriter(outFile))) {
            // We need to copy the source file to the destination folder to be able to compare pdf files in android.
        }
        new CompareTool().compareByContent(outFile, cmpFile, DEST, "diff", USER_PASSWORD, null);
    }

    // In all these tampered files, the stream content of object 14 has been modified.
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    @Test
    public void macTamperedTest() throws IOException {
        String srcFile = SRC + "encryptedDocumentTamperedMac.pdf";
        assertTampered(srcFile);
    }

    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    @Test
    public void initVectorTamperedTest() throws IOException {
        String srcFile = SRC + "encryptedDocumentTamperedIv.pdf";
        assertTampered(srcFile);
    }

    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    @Test
    public void ciphertextTamperedTest() throws IOException {
        String srcFile = SRC + "encryptedDocumentTamperedCiphertext.pdf";
        assertTampered(srcFile);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true),
        @LogMessage(messageTemplate = IoLogMessageConstant.ENCRYPTION_ENTRIES_P_AND_ENCRYPT_METADATA_NOT_CORRESPOND_PERMS_ENTRY)})
    public void pdfEncryptionWithEmbeddedFilesTest() {
        byte[] documentId = new byte[]{(byte)88, (byte)189, (byte)192, (byte)48, (byte)240, (byte)200, (byte)87,
                (byte)183, (byte)244, (byte)119, (byte)224, (byte)109, (byte)226, (byte)173, (byte)32, (byte)90};
        byte[] password = new byte[]{(byte)115, (byte)101, (byte)99, (byte)114, (byte)101, (byte)116};
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        encMap.put(PdfName.P, new PdfNumber(-1852));
        encMap.put(PdfName.EFF, PdfName.FlateDecode);
        encMap.put(PdfName.StmF, PdfName.Identity);
        encMap.put(PdfName.StrF, PdfName.Identity);
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        PdfDictionary cfmDict = new PdfDictionary();
        cfmDict.put(PdfName.CFM, PdfName.AESV4);
        embeddedFilesDict.put(PdfName.StdCF, cfmDict);
        encMap.put(PdfName.CF, embeddedFilesDict);
        encMap.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
        encMap.put(PdfName.O, new PdfString("\u0006¡Ê\u009A<@\u009DÔG\u0013&\u008C5r\u0096\u0081i!\u0091\u000Fªìh=±\u0091\u0006Að¨\u008D\"¼\u0018?õ\u001DNó»{y\u0091)\u0090vâý"));
        encMap.put(PdfName.U, new PdfString("ôY\u009DÃ\u0017Ý·Ü\u0097vØ\fJ\u0099c\u0004áÝ¹ÔB\u0084·9÷\u008F\u009D-¿xnkþ\u0086Æ\u0088º\u0086ÜTÿëÕï\u0018\u009D\u0016-"));
        encMap.put(PdfName.OE, new PdfString("5Ë\u009EUÔº\u0007 Nøß\u0094ä\u001DÄ_wnù\u001AKò-\u007F\u00ADQ²Ø \u001FSJ"));
        encMap.put(PdfName.UE, new PdfString("\u000B:\rÆ\u0004\u0094Ûìkþ,ôBS9ü\u001E³\u0088\u001D(\u0098ºÀ\u0010½\u0082.'`kñ"));
        encMap.put(PdfName.Perms, new PdfString("\u008F»\u0080.òç\u0011\u001Et\u0012\u00905\u001B\u0019\u0014«"));
        PdfDictionary dictionary = new PdfDictionary(encMap);
        PdfEncryption encryption = new PdfEncryption(dictionary, password, documentId);
        Assertions.assertTrue(encryption.isEmbeddedFilesOnly());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    public void pdfEncryptionWithMetadataTest() {
        byte[] documentId = new byte[]{(byte)88, (byte)189, (byte)192, (byte)48, (byte)240, (byte)200, (byte)87,
                (byte)183, (byte)244, (byte)119, (byte)224, (byte)109, (byte)226, (byte)173, (byte)32, (byte)90};
        byte[] password = new byte[]{(byte)115, (byte)101, (byte)99, (byte)114, (byte)101, (byte)116};
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        encMap.put(PdfName.P, new PdfNumber(-1852));
        encMap.put(PdfName.StmF, PdfName.StdCF);
        encMap.put(PdfName.StrF, PdfName.StdCF);
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        PdfDictionary cfmDict = new PdfDictionary();
        cfmDict.put(PdfName.CFM, PdfName.AESV4);
        embeddedFilesDict.put(PdfName.StdCF, cfmDict);
        encMap.put(PdfName.CF, embeddedFilesDict);
        encMap.put(PdfName.EncryptMetadata, PdfBoolean.TRUE);
        encMap.put(PdfName.O, new PdfString("\u0006¡Ê\u009A<@\u009DÔG\u0013&\u008C5r\u0096\u0081i!\u0091\u000Fªìh=±\u0091\u0006Að¨\u008D\"¼\u0018?õ\u001DNó»{y\u0091)\u0090vâý"));
        encMap.put(PdfName.U, new PdfString("ôY\u009DÃ\u0017Ý·Ü\u0097vØ\fJ\u0099c\u0004áÝ¹ÔB\u0084·9÷\u008F\u009D-¿xnkþ\u0086Æ\u0088º\u0086ÜTÿëÕï\u0018\u009D\u0016-"));
        encMap.put(PdfName.OE, new PdfString("5Ë\u009EUÔº\u0007 Nøß\u0094ä\u001DÄ_wnù\u001AKò-\u007F\u00ADQ²Ø \u001FSJ"));
        encMap.put(PdfName.UE, new PdfString("\u000B:\rÆ\u0004\u0094Ûìkþ,ôBS9ü\u001E³\u0088\u001D(\u0098ºÀ\u0010½\u0082.'`kñ"));
        encMap.put(PdfName.Perms, new PdfString("\u008F»\u0080.òç\u0011\u001Et\u0012\u00905\u001B\u0019\u0014«"));
        PdfDictionary dictionary = new PdfDictionary(encMap);
        PdfEncryption encryption = new PdfEncryption(dictionary, password, documentId);
        Assertions.assertTrue(encryption.isMetadataEncrypted());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    public void encryptPdfWithMissingCFTest() {
        byte[] documentId = new byte[]{(byte)88, (byte)189, (byte)192, (byte)48, (byte)240, (byte)200, (byte)87,
                (byte)183, (byte)244, (byte)119, (byte)224, (byte)109, (byte)226, (byte)173, (byte)32, (byte)90};
        byte[] password = new byte[]{(byte)115, (byte)101, (byte)99, (byte)114, (byte)101, (byte)116};
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, password, documentId));
        Assertions.assertEquals(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION, e.getMessage());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    public void encryptPdfWithMissingStdCFTest() {
        byte[] documentId = new byte[]{(byte)88, (byte)189, (byte)192, (byte)48, (byte)240, (byte)200, (byte)87,
                (byte)183, (byte)244, (byte)119, (byte)224, (byte)109, (byte)226, (byte)173, (byte)32, (byte)90};
        byte[] password = new byte[]{(byte)115, (byte)101, (byte)99, (byte)114, (byte)101, (byte)116};
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        PdfDictionary cfmDict = new PdfDictionary();
        cfmDict.put(PdfName.CFM, PdfName.AESV4);
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, password, documentId));
        Assertions.assertEquals(KernelExceptionMessageConstant.STDCF_NOT_FOUND_ENCRYPTION, e.getMessage());
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    public void encryptPdfWithMissingCFMTest() {
        byte[] documentId = new byte[]{(byte)88, (byte)189, (byte)192, (byte)48, (byte)240, (byte)200, (byte)87,
                (byte)183, (byte)244, (byte)119, (byte)224, (byte)109, (byte)226, (byte)173, (byte)32, (byte)90};
        byte[] password = new byte[]{(byte)115, (byte)101, (byte)99, (byte)114, (byte)101, (byte)116};
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        encMap.put(PdfName.P, new PdfNumber(-1852));
        encMap.put(PdfName.StmF, PdfName.StdCF);
        encMap.put(PdfName.StrF, PdfName.StdCF);
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        PdfDictionary cfmDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.StdCF, cfmDict);
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, password, documentId));
        Assertions.assertEquals(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND, e.getMessage());
    }

    private void assertTampered(String outFile) throws IOException {
        try (PdfDocument pdfDoc =
                     new PdfDocument(new PdfReader(outFile, new ReaderProperties().setPassword(USER_PASSWORD)))) {
            PdfObject obj = pdfDoc.getPdfObject(14);
            if (obj != null && obj.isStream()) {
                // Get decoded stream bytes.
                Assertions.assertThrows(Exception.class, () -> ((PdfStream) obj).getBytes());
            }
        }
    }
}

// Outside test class for porting
class CToolNoDeveloperExtension extends CompareTool {
    @Override
    protected boolean compareObjects(PdfObject outObj, PdfObject cmpObj, ObjectPath currentPath, CompareResult compareResult) {
        if (outObj != null && outObj.isDictionary()) {
            if (((PdfDictionary) outObj).get(PdfName.ISO_) != null) {
                return true;
            }
        }
        if (cmpObj != null && cmpObj.isDictionary()) {
            if (((PdfDictionary) cmpObj).get(PdfName.ISO_) != null) {
                return true;
            }
        }

        return super.compareObjects(outObj, cmpObj, currentPath, compareResult);
    }
}
