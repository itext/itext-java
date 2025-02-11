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
package com.itextpdf.kernel.pdf;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cms.AbstractCMSException;
import com.itextpdf.commons.bouncycastle.cms.IRecipient;
import com.itextpdf.commons.bouncycastle.cms.IRecipientInformation;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.Map;

/**
 * This class takes any PDF and returns exactly the same but
 * encrypted. All the content, links, outlines, etc, are kept.
 * It is also possible to change the info dictionary.
 */
public final class PdfEncryptor {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private IMetaInfo metaInfo;
    private EncryptionProperties properties;

    public PdfEncryptor() {
    }

    /**
     * Entry point to encrypt a PDF document.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param properties     encryption properties. See {@link EncryptionProperties}.
     * @param newInfo        an optional {@code String} map to add or change
     *                       the info dictionary. Entries with {@code null}
     *                       values delete the key in the original info dictionary
     */
    public static void encrypt(PdfReader reader, OutputStream os, EncryptionProperties properties, Map<String, String> newInfo) {
        new PdfEncryptor().setEncryptionProperties(properties).encrypt(reader, os, newInfo);
    }

    /**
     * Entry point to encrypt a PDF document.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param properties     encryption properties. See {@link EncryptionProperties}.
     */
    public static void encrypt(PdfReader reader, OutputStream os, EncryptionProperties properties) {
        encrypt(reader, os, properties, null);
    }

    /**
     * Give you a verbose analysis of the permissions.
     *
     * @param permissions the permissions value of a PDF file
     * @return a String that explains the meaning of the permissions value
     */
    public static String getPermissionsVerbose(int permissions) {
        StringBuilder buf = new StringBuilder("Allowed:");
        if ((EncryptionConstants.ALLOW_PRINTING & permissions) == EncryptionConstants.ALLOW_PRINTING) buf.append(" Printing");
        if ((EncryptionConstants.ALLOW_MODIFY_CONTENTS & permissions) == EncryptionConstants.ALLOW_MODIFY_CONTENTS)
            buf.append(" Modify contents");
        if ((EncryptionConstants.ALLOW_COPY & permissions) == EncryptionConstants.ALLOW_COPY) buf.append(" Copy");
        if ((EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS & permissions) == EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS)
            buf.append(" Modify annotations");
        if ((EncryptionConstants.ALLOW_FILL_IN & permissions) == EncryptionConstants.ALLOW_FILL_IN) buf.append(" Fill in");
        if ((EncryptionConstants.ALLOW_SCREENREADERS & permissions) == EncryptionConstants.ALLOW_SCREENREADERS)
            buf.append(" Screen readers");
        if ((EncryptionConstants.ALLOW_ASSEMBLY & permissions) == EncryptionConstants.ALLOW_ASSEMBLY) buf.append(" Assembly");
        if ((EncryptionConstants.ALLOW_DEGRADED_PRINTING & permissions) == EncryptionConstants.ALLOW_DEGRADED_PRINTING)
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
        return (EncryptionConstants.ALLOW_PRINTING & permissions) == EncryptionConstants.ALLOW_PRINTING;
    }

    /**
     * Tells you if modifying content is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if modifying content is allowed
     */
    public static boolean isModifyContentsAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_MODIFY_CONTENTS & permissions) == EncryptionConstants.ALLOW_MODIFY_CONTENTS;
    }

    /**
     * Tells you if copying is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if copying is allowed
     */
    public static boolean isCopyAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_COPY & permissions) == EncryptionConstants.ALLOW_COPY;
    }

    /**
     * Tells you if modifying annotations is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if modifying annotations is allowed
     */
    public static boolean isModifyAnnotationsAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS & permissions) == EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS;
    }

    /**
     * Tells you if filling in fields is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if filling in fields is allowed
     */
    public static boolean isFillInAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_FILL_IN & permissions) == EncryptionConstants.ALLOW_FILL_IN;
    }

    /**
     * Tells you if repurposing for screenreaders is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if repurposing for screenreaders is allowed
     */
    public static boolean isScreenReadersAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_SCREENREADERS & permissions) == EncryptionConstants.ALLOW_SCREENREADERS;
    }

    /**
     * Tells you if document assembly is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if document assembly is allowed
     */
    public static boolean isAssemblyAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_ASSEMBLY & permissions) == EncryptionConstants.ALLOW_ASSEMBLY;
    }

    /**
     * Tells you if degraded printing is allowed.
     *
     * @param permissions the permissions value of a PDF file
     * @return true if degraded printing is allowed
     */
    public static boolean isDegradedPrintingAllowed(int permissions) {
        return (EncryptionConstants.ALLOW_DEGRADED_PRINTING & permissions) == EncryptionConstants.ALLOW_DEGRADED_PRINTING;
    }

    /**
     * Gets the content from a recipient.
     *
     * @param recipientInfo          recipient information
     * @param certificateKey         private certificate key
     * @param certificateKeyProvider the name of the certificate key provider
     * @return content from a recipient info
     * @throws AbstractCMSException if the content cannot be recovered.
     */
    public static byte[] getContent(IRecipientInformation recipientInfo, PrivateKey certificateKey,
            String certificateKeyProvider) throws AbstractCMSException {
        String algorithm = certificateKey.getAlgorithm();
        IRecipient jceKeyRecipient;
        if (algorithm.contains("RSA")) {
            jceKeyRecipient = BOUNCY_CASTLE_FACTORY.createJceKeyTransEnvelopedRecipient(certificateKey)
                    .setProvider(certificateKeyProvider);
        } else if (algorithm.contains("EC")) {
            jceKeyRecipient = BOUNCY_CASTLE_FACTORY.createJceKeyAgreeEnvelopedRecipient(certificateKey)
                    .setProvider(certificateKeyProvider);
        } else {
            throw new PdfException(MessageFormatUtil.format(KernelExceptionMessageConstant.ALGORITHM_IS_NOT_SUPPORTED,
                    algorithm));
        }
        return recipientInfo.getContent(jceKeyRecipient);
    }

    /**
     * Sets the {@link IMetaInfo} that will be used during {@link PdfDocument} creation.
     *
     * @param metaInfo meta info to set
     * @return this {@link PdfEncryptor} instance
     */
    public PdfEncryptor setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    /**
     * Sets the {@link EncryptionProperties}
     * @param properties the properties to set
     * @return this {@link PdfEncryptor} instance
     */
    public PdfEncryptor setEncryptionProperties(EncryptionProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Entry point to encrypt a PDF document.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     * @param newInfo        an optional {@code String} map to add or change
     *                       the info dictionary. Entries with {@code null}
     *                       values delete the key in the original info dictionary
     */
    public void encrypt(PdfReader reader, OutputStream os, Map<String, String> newInfo) {
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.encryptionProperties = properties;
        StampingProperties stampingProperties = new StampingProperties();
        stampingProperties.setEventCountingMetaInfo(metaInfo);
        try (PdfWriter writer = new PdfWriter(os, writerProperties);
                PdfDocument document = new PdfDocument(reader, writer, stampingProperties)) {
            document.getDocumentInfo().setMoreInfo(newInfo);
        } catch (IOException e) {
            //The close() method of OutputStream throws an exception, but we don't need to do anything in this case,
            // because OutputStream#close() does nothing.
        }
    }

    /**
     * Entry point to encrypt a PDF document.
     *
     * @param reader         the read PDF
     * @param os             the output destination
     */
    public void encrypt(PdfReader reader, OutputStream os) {
        encrypt(reader, os, (Map<String, String>) null);
    }
}
