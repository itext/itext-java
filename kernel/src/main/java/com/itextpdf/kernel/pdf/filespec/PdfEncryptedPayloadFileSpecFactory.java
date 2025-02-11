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
package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryptedPayload;
import com.itextpdf.kernel.pdf.PdfName;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class PdfEncryptedPayloadFileSpecFactory {

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param mimeType            mime-type of the file
     * @param fileParameter       Pdfdictionary containing file parameters
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, byte[] fileStore, PdfEncryptedPayload encryptedPayload, PdfName mimeType, PdfDictionary fileParameter) {
        return addEncryptedPayloadDictionary(PdfFileSpec.createEmbeddedFileSpec(doc, fileStore, generateDescription(encryptedPayload), generateFileDisplay(encryptedPayload), mimeType, fileParameter, PdfName.EncryptedPayload), encryptedPayload);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param fileParameter       Pdfdictionary containing file parameters
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, byte[] fileStore, PdfEncryptedPayload encryptedPayload, PdfDictionary fileParameter) {
        return create(doc, fileStore, encryptedPayload, null, fileParameter);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param fileStore           byte[] containing the file
     * @param encryptedPayload    the encrypted payload dictionary
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, byte[] fileStore, PdfEncryptedPayload encryptedPayload) {
        return create(doc, fileStore, encryptedPayload, null, null);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param filePath            path to the encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param mimeType            mime-type of the file
     * @param fileParameter       Pdfdictionary containing file parameters
     * @return PdfFileSpec containing the file specification of the encrypted payload
     * @throws java.io.IOException in case of any I/O error
     */
    public static PdfFileSpec create(PdfDocument doc, String filePath, PdfEncryptedPayload encryptedPayload, PdfName mimeType, PdfDictionary fileParameter) throws IOException {
        return addEncryptedPayloadDictionary(PdfFileSpec.createEmbeddedFileSpec(doc, filePath, generateDescription(encryptedPayload), generateFileDisplay(encryptedPayload), mimeType, fileParameter, PdfName.EncryptedPayload), encryptedPayload);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param filePath            path to the encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param mimeType            mime-type of the file
     * @return PdfFileSpec containing the file specification of the encrypted payload
     * @throws java.io.IOException in case of any I/O error
     */
    public static PdfFileSpec create(PdfDocument doc, String filePath, PdfEncryptedPayload encryptedPayload, PdfName mimeType) throws IOException {
        return create(doc, filePath, encryptedPayload, mimeType, null);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param filePath            path to the encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @return PdfFileSpec containing the file specification of the encrypted payload
     * @throws java.io.IOException in case of any I/O error
     */
    public static PdfFileSpec create(PdfDocument doc, String filePath, PdfEncryptedPayload encryptedPayload) throws IOException {
        return create(doc, filePath, encryptedPayload, null, null);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param is                  stream containing encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param mimeType            mime-type of the file
     * @param fileParameter       Pdfdictionary containing file parameters
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, InputStream is, PdfEncryptedPayload encryptedPayload, PdfName mimeType, PdfDictionary fileParameter) {
        return addEncryptedPayloadDictionary(PdfFileSpec.createEmbeddedFileSpec(doc, is, generateDescription(encryptedPayload), generateFileDisplay(encryptedPayload), mimeType, fileParameter, PdfName.EncryptedPayload), encryptedPayload);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param is                  stream containing encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @param mimeType            mime-type of the file
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, InputStream is, PdfEncryptedPayload encryptedPayload, PdfName mimeType) {
        return create(doc, is, encryptedPayload, mimeType, null);
    }

    /**
     * Embed a encrypted payload to a PdfDocument.
     *
     * @param doc                 PdfDocument to add the file to
     * @param is                  stream containing encrypted file
     * @param encryptedPayload    the encrypted payload dictionary
     * @return PdfFileSpec containing the file specification of the encrypted payload
     */
    public static PdfFileSpec create(PdfDocument doc, InputStream is, PdfEncryptedPayload encryptedPayload) {
        return create(doc, is, encryptedPayload, null, null);
    }

    public static PdfFileSpec wrap(PdfDictionary dictionary) {
        if (!PdfName.EncryptedPayload.equals(dictionary.getAsName(PdfName.AFRelationship))) {
            LoggerFactory.getLogger(PdfEncryptedPayloadFileSpecFactory.class)
                    .error(IoLogMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_AFRELATIONSHIP_FILED_EQUAL_TO_ENCRYPTED_PAYLOAD);
        }
        PdfDictionary ef = dictionary.getAsDictionary(PdfName.EF);
        if (ef == null || (ef.getAsStream(PdfName.F) == null) && (ef.getAsStream(PdfName.UF) == null)) {
            throw new PdfException(KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_EF_DICTIONARY);
        }
        if (!PdfName.Filespec.equals(dictionary.getAsName(PdfName.Type))) {
            throw new PdfException(
                    KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_TYPE_EQUAL_TO_FILESPEC);
        }
        if (!dictionary.isIndirect()) {
            throw new PdfException(KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_BE_INDIRECT);
        }
        PdfFileSpec fileSpec = PdfFileSpec.wrapFileSpecObject(dictionary);
        if (PdfEncryptedPayload.extractFrom(fileSpec) == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_DOES_NOT_HAVE_ENCRYPTED_PAYLOAD_DICTIONARY);
        }
        return fileSpec;
    }



    // Note as stated by spec the desscription and file display
    // shall not be derived from the encrypted payload's actual file name
    // to avoid potential disclosure of sensitive information
    public static String generateDescription(PdfEncryptedPayload ep) {
        String result = "This embedded file is encrypted using " + ep.getSubtype().getValue();
        PdfName version = ep.getVersion();
        if (version != null) {
            result += " , version: " + version.getValue();
        }
        return result;
    }

    public static String generateFileDisplay(PdfEncryptedPayload ep) {
        return ep.getSubtype().getValue() + "Protected.pdf";
    }

    private static PdfFileSpec addEncryptedPayloadDictionary(PdfFileSpec fs, PdfEncryptedPayload ep) {
        ((PdfDictionary)fs.getPdfObject()).put(PdfName.EP, ep.getPdfObject());
        return fs;
    }
}
