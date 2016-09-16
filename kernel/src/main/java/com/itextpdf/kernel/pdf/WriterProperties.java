/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import java.io.Serializable;
import java.security.cert.Certificate;

public class WriterProperties implements Serializable {

    private static final long serialVersionUID = -8692165914703604764L;

    protected int compressionLevel;

    /**
     * Indicates if to use full compression (using object streams).
     */
    protected Boolean isFullCompression;

    /**
     * Indicates if the writer copy objects in a smart mode. If so PdfDictionary and PdfStream will be hashed
     * and reused if there's an object with the same content later.
     */
    protected boolean smartMode;
    protected boolean debugMode;
    protected boolean addXmpMetadata;
    protected PdfVersion pdfVersion;
    protected EncryptionProperties encryptionProperties;

    public WriterProperties() {
        smartMode = false;
        debugMode = false;
        compressionLevel = CompressionConstants.DEFAULT_COMPRESSION;
        isFullCompression = null;
        encryptionProperties = new EncryptionProperties();
    }

    /**
     * Defines pdf version for the created document. Default value is PDF_1_7.
     * @param version version for the document.
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties setPdfVersion(PdfVersion version) {
        this.pdfVersion = version;
        return this;
    }

    /**
     * Enables smart mode.
     * <p/>
     * In smart mode when resources (such as fonts, images,...) are
     * encountered, a reference to these resources is saved
     * in a cache, so that they can be reused.
     * This requires more memory, but reduces the file size
     * of the resulting PDF document.
     *
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties useSmartMode() {
        this.smartMode = true;
        return this;
    }

    /**
     * If true, default XMPMetadata based on {@link PdfDocumentInfo} will be added.
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties addXmpMetadata() {
        this.addXmpMetadata = true;
        return this;
    }

    /**
     * Defines the level of compression for the document.
     * See {@link CompressionConstants}
     * @param compressionLevel
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    /**
     * Defines if full compression mode is enabled. If enabled, not only the content of the pdf document will be
     * compressed, but also the pdf document inner structure.
     * @param fullCompressionMode true - to enable full compression mode, false to disable it
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties setFullCompressionMode(boolean fullCompressionMode) {
        this.isFullCompression = fullCompressionMode;
        return this;
    }

    /**
     * Sets the encryption options for the document. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     *
     * See {@link EncryptionConstants}.
     *
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionAlgorithm the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128,
     *                       ENCRYPTION_AES128 or ENCRYPTION_AES256
     *                       Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties setStandardEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionAlgorithm) {
        encryptionProperties.setStandardEncryption(userPassword, ownerPassword, permissions, encryptionAlgorithm);
        return this;
    }

    /**
     * Sets the certificate encryption options for the document. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     * The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     * Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     *
     * See {@link EncryptionConstants}.
     *
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionAlgorithm the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128,
     *                       ENCRYPTION_AES128 or ENCRYPTION_AES256.
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties setPublicKeyEncryption(Certificate[] certs, int[] permissions, int encryptionAlgorithm) {
        encryptionProperties.setPublicKeyEncryption(certs, permissions, encryptionAlgorithm);
        return this;
    }

    /**
     * This activates debug mode with pdfDebug tool.
     * It causes additional overhead of duplicating document bytes into memory, so use it careful.
     * NEVER use it in production or in any other cases except pdfDebug.
     *
     * @return this {@code WriterProperties} instance
     */
    public WriterProperties useDebugMode() {
        this.debugMode = true;
        return this;
    }

    boolean isStandardEncryptionUsed() {
        return encryptionProperties.isStandardEncryptionUsed();
    }

    boolean isPublicKeyEncryptionUsed() {
        return encryptionProperties.isPublicKeyEncryptionUsed();
    }
}
