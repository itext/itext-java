package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;

/**
 * This class takes any PDF and returns exactly the same but
 * encrypted. All the content, links, outlines, etc, are kept.
 * It is also possible to change the info dictionary.
 */
public final class PdfEncryptor {

    private PdfEncryptor() {
    }

    /**
     * Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * {@code PdfWriter}. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS,
     * ALLOW_FILL_IN, ALLOW_SCREENREADERS, ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING.
     * The permissions can be combined by ORing them.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40,
     *                       STANDARD_ENCRYPTION_128 or ENCRYPTION_AES_128.
     * @param newInfo        an optional {@code String} map to add or change
     *                       the info dictionary. Entries with {@code null}
     *                       values delete the key in the original info dictionary
     * @throws PdfException on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, final byte userPassword[], final byte ownerPassword[], final int permissions, final int encryptionType, HashMap<String, String> newInfo) {
        PdfWriter writer = new PdfWriter(os);
        writer.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
        PdfDocument document = new PdfDocument(reader, writer);
        document.getDocumentInfo().setMoreInfo(newInfo);
        document.close();
    }

    /**
     * Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * {@code PdfWriter}. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS,
     * ALLOW_FILL_IN, ALLOW_SCREENREADERS, ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING.
     * The permissions can be combined by ORing them.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40,
     *                       STANDARD_ENCRYPTION_128 or ENCRYPTION_AES_128.
     * @throws PdfException on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, final byte userPassword[], final byte ownerPassword[], final int permissions, final int encryptionType) {
        encrypt(reader, os, userPassword, ownerPassword, permissions, encryptionType, null);
    }

    /**
     * Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * {@code PdfWriter}. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS,
     * ALLOW_FILL_IN, ALLOW_SCREENREADERS, ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING.
     * The permissions can be combined by ORing them.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40,
     *                       STANDARD_ENCRYPTION_128 or ENCRYPTION_AES_128.
     * @param newInfo        an optional {@code String} map to add or change
     *                       the info dictionary. Entries with {@code null}
     *                       values delete the key in the original info dictionary
     * @on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, final Certificate[] certs, final int[] permissions, final int encryptionType, HashMap<String, String> newInfo) {
        PdfWriter writer = new PdfWriter(os);
        writer.setEncryption(certs, permissions, encryptionType);
        PdfDocument document = new PdfDocument(reader, writer);
        document.getDocumentInfo().setMoreInfo(newInfo);
        document.close();
    }

    /**
     * Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * {@code PdfWriter}. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS,
     * ALLOW_FILL_IN, ALLOW_SCREENREADERS, ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING.
     * The permissions can be combined by ORing them.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40,
     *                       STANDARD_ENCRYPTION_128 or ENCRYPTION_AES_128.
     * @on error
     */
    public static void encrypt(PdfReader reader, OutputStream os, final Certificate[] certs, final int[] permissions, final int encryptionType) {
        encrypt(reader, os, certs, permissions, encryptionType, null);
    }

    /**
     * Give you a verbose analysis of the permissions.
     *
     * @param permissions the permissions value of a PDF file
     * @return a String that explains the meaning of the permissions value
     */
    public static String getPermissionsVerbose(int permissions) {
        StringBuilder buf = new StringBuilder("Allowed:");
        if ((PdfWriter.ALLOW_PRINTING & permissions) == PdfWriter.ALLOW_PRINTING) buf.append(" Printing");
        if ((PdfWriter.ALLOW_MODIFY_CONTENTS & permissions) == PdfWriter.ALLOW_MODIFY_CONTENTS)
            buf.append(" Modify contents");
        if ((PdfWriter.ALLOW_COPY & permissions) == PdfWriter.ALLOW_COPY) buf.append(" Copy");
        if ((PdfWriter.ALLOW_MODIFY_ANNOTATIONS & permissions) == PdfWriter.ALLOW_MODIFY_ANNOTATIONS)
            buf.append(" Modify annotations");
        if ((PdfWriter.ALLOW_FILL_IN & permissions) == PdfWriter.ALLOW_FILL_IN) buf.append(" Fill in");
        if ((PdfWriter.ALLOW_SCREENREADERS & permissions) == PdfWriter.ALLOW_SCREENREADERS)
            buf.append(" Screen readers");
        if ((PdfWriter.ALLOW_ASSEMBLY & permissions) == PdfWriter.ALLOW_ASSEMBLY) buf.append(" Assembly");
        if ((PdfWriter.ALLOW_DEGRADED_PRINTING & permissions) == PdfWriter.ALLOW_DEGRADED_PRINTING)
            buf.append(" Degraded printing");
        return buf.toString();
    }

    /**
     * Tells you if printing is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if printing is allowed
     */
    public static boolean isPrintingAllowed(int permissions) {
        return (PdfWriter.ALLOW_PRINTING & permissions) == PdfWriter.ALLOW_PRINTING;
    }

    /**
     * Tells you if modifying content is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if modifying content is allowed
     */
    public static boolean isModifyContentsAllowed(int permissions) {
        return (PdfWriter.ALLOW_MODIFY_CONTENTS & permissions) == PdfWriter.ALLOW_MODIFY_CONTENTS;
    }

    /**
     * Tells you if copying is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if copying is allowed
     */
    public static boolean isCopyAllowed(int permissions) {
        return (PdfWriter.ALLOW_COPY & permissions) == PdfWriter.ALLOW_COPY;
    }

    /**
     * Tells you if modifying annotations is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if modifying annotations is allowed
     */
    public static boolean isModifyAnnotationsAllowed(int permissions) {
        return (PdfWriter.ALLOW_MODIFY_ANNOTATIONS & permissions) == PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
    }

    /**
     * Tells you if filling in fields is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if filling in fields is allowed
     */
    public static boolean isFillInAllowed(int permissions) {
        return (PdfWriter.ALLOW_FILL_IN & permissions) == PdfWriter.ALLOW_FILL_IN;
    }

    /**
     * Tells you if repurposing for screenreaders is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if repurposing for screenreaders is allowed
     */
    public static boolean isScreenReadersAllowed(int permissions) {
        return (PdfWriter.ALLOW_SCREENREADERS & permissions) == PdfWriter.ALLOW_SCREENREADERS;
    }

    /**
     * Tells you if document assembly is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if document assembly is allowed
     */
    public static boolean isAssemblyAllowed(int permissions) {
        return (PdfWriter.ALLOW_ASSEMBLY & permissions) == PdfWriter.ALLOW_ASSEMBLY;
    }

    /**
     * Tells you if degraded printing is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if degraded printing is allowed
     */
    public static boolean isDegradedPrintingAllowed(int permissions) {
        return (PdfWriter.ALLOW_DEGRADED_PRINTING & permissions) == PdfWriter.ALLOW_DEGRADED_PRINTING;
    }

    /**
     * Gets the content from a recipient.
     */
    public static byte[] getContent(RecipientInformation recipientInfo, PrivateKey certificateKey, String certificateKeyProvider) throws CMSException {
        Recipient jceKeyTransRecipient = new JceKeyTransEnvelopedRecipient(certificateKey).setProvider(certificateKeyProvider);
        return recipientInfo.getContent(jceKeyTransRecipient);
    }
}
