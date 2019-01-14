/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf;

import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.Map;

import com.itextpdf.kernel.counter.event.IMetaInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

/**
 * This class takes any PDF and returns exactly the same but
 * encrypted. All the content, links, outlines, etc, are kept.
 * It is also possible to change the info dictionary.
 */
public final class PdfEncryptor {

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
     */
    public static byte[] getContent(RecipientInformation recipientInfo, PrivateKey certificateKey, String certificateKeyProvider) throws CMSException {
        Recipient jceKeyTransRecipient = new JceKeyTransEnvelopedRecipient(certificateKey).setProvider(certificateKeyProvider);
        return recipientInfo.getContent(jceKeyTransRecipient);
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
        PdfWriter writer = new PdfWriter(os, writerProperties);
        StampingProperties stampingProperties = new StampingProperties();
        stampingProperties.setEventCountingMetaInfo(metaInfo);
        PdfDocument document = new PdfDocument(reader, writer, stampingProperties);
        document.getDocumentInfo().setMoreInfo(newInfo);
        document.close();
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
