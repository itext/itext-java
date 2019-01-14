/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
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
            LoggerFactory.getLogger(PdfEncryptedPayloadFileSpecFactory.class).error(LogMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_AFRELATIONSHIP_FILED_EQUAL_TO_ENCRYPTED_PAYLOAD);
        }
        PdfDictionary ef = dictionary.getAsDictionary(PdfName.EF);
        if (ef == null || (ef.getAsStream(PdfName.F) == null) && (ef.getAsStream(PdfName.UF) == null)) {
            throw new PdfException(PdfException.EncryptedPayloadFileSpecShallHaveEFDictionary);
        }
        if (!PdfName.Filespec.equals(dictionary.getAsName(PdfName.Type))) {
            throw new PdfException(PdfException.EncryptedPayloadFileSpecShallHaveTypeEqualToFilespec);
        }
        if (!dictionary.isIndirect()) {
            throw new PdfException(PdfException.EncryptedPayloadFileSpecShallBeIndirect);
        }
        PdfFileSpec fileSpec = PdfFileSpec.wrapFileSpecObject(dictionary);
        if (PdfEncryptedPayload.extractFrom(fileSpec) == null) {
            throw new PdfException(PdfException.EncryptedPayloadFileSpecDoesntHaveEncryptedPayloadDictionary);
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
