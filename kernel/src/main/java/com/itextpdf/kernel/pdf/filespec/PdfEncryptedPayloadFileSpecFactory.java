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
