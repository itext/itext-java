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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.ARCFOUREncryption;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.crypto.OutputStreamStandardEncryption;
import com.itextpdf.kernel.crypto.StandardDecryptor;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import java.io.OutputStream;

public class StandardHandlerUsingStandard40 extends StandardSecurityHandler {
    protected static final byte[] pad = {(byte) 0x28, (byte) 0xBF, (byte) 0x4E,
            (byte) 0x5E, (byte) 0x4E, (byte) 0x75, (byte) 0x8A, (byte) 0x41,
            (byte) 0x64, (byte) 0x00, (byte) 0x4E, (byte) 0x56, (byte) 0xFF,
            (byte) 0xFA, (byte) 0x01, (byte) 0x08, (byte) 0x2E, (byte) 0x2E,
            (byte) 0x00, (byte) 0xB6, (byte) 0xD0, (byte) 0x68, (byte) 0x3E,
            (byte) 0x80, (byte) 0x2F, (byte) 0x0C, (byte) 0xA9, (byte) 0xFE,
            (byte) 0x64, (byte) 0x53, (byte) 0x69, (byte) 0x7A};
    protected static final byte[] metadataPad = {(byte) 255, (byte) 255,
            (byte) 255, (byte) 255};
    private static final long serialVersionUID = -7951837491441953183L;

    protected byte[] documentId;
    // stores key length of the main key
    protected int keyLength;

    protected ARCFOUREncryption arcfour = new ARCFOUREncryption();

    public StandardHandlerUsingStandard40(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
                                      int permissions, boolean encryptMetadata, boolean embeddedFilesOnly, byte[] documentId) {
        initKeyAndFillDictionary(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
    }

    public StandardHandlerUsingStandard40(PdfDictionary encryptionDictionary, byte[] password, byte[] documentId, boolean encryptMetadata) {
        initKeyAndReadDictionary(encryptionDictionary, password, documentId, encryptMetadata);
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamStandardEncryption(os, nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public IDecryptor getDecryptor() {
        return new StandardDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    public byte[] computeUserPassword(byte[] ownerPassword, PdfDictionary encryptionDictionary) {
        byte[] ownerKey = getIsoBytes(encryptionDictionary.getAsString(PdfName.O));
        byte[] userPad = computeOwnerKey(ownerKey, padPassword(ownerPassword));
        for (int i = 0; i < userPad.length; i++) {
            boolean match = true;
            for (int j = 0; j < userPad.length - i; j++) {
                if (userPad[i + j] != pad[j]) {
                    match = false;
                    break;
                }
            }
            if (!match) continue;
            byte[] userPassword = new byte[i];
            System.arraycopy(userPad, 0, userPassword, 0, i);
            return userPassword;
        }
        return userPad;
    }

    protected void calculatePermissions(int permissions) {
        permissions |= PERMS_MASK_1_FOR_REVISION_2;
        permissions &= PERMS_MASK_2;
        this.permissions = permissions;
    }

    protected byte[] computeOwnerKey(byte[] userPad, byte[] ownerPad) {
        byte[] ownerKey = new byte[32];
        byte[] digest = md5.digest(ownerPad);
        arcfour.prepareARCFOURKey(digest, 0, 5);
        arcfour.encryptARCFOUR(userPad, ownerKey);
        return ownerKey;
    }

    protected void computeGlobalEncryptionKey(byte[] userPad, byte[] ownerKey, boolean encryptMetadata) {
        mkey = new byte[keyLength / 8];

        // fixed by ujihara in order to follow PDF reference
        md5.reset();
        md5.update(userPad);
        md5.update(ownerKey);

        byte[] ext = new byte[4];
        ext[0] = (byte) permissions;
        ext[1] = (byte) (permissions >> 8);
        ext[2] = (byte) (permissions >> 16);
        ext[3] = (byte) (permissions >> 24);
        md5.update(ext, 0, 4);
        if (documentId != null)
            md5.update(documentId);
        if (!encryptMetadata)
            md5.update(metadataPad);

        byte[] digest = new byte[mkey.length];
        System.arraycopy(md5.digest(), 0, digest, 0, mkey.length);
        System.arraycopy(digest, 0, mkey, 0, mkey.length);
    }

    protected byte[] computeUserKey() {
        byte[] userKey = new byte[32];
        arcfour.prepareARCFOURKey(mkey);
        arcfour.encryptARCFOUR(pad, userKey);
        return userKey;
    }

    protected void setSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        encryptionDictionary.put(PdfName.R, new PdfNumber(2));
        encryptionDictionary.put(PdfName.V, new PdfNumber(1));
    }


    protected boolean isValidPassword(byte[] uValue, byte[] userKey) {
        return !equalsArray(uValue, userKey, 32);
    }

    private void initKeyAndFillDictionary(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
                                          int permissions, boolean encryptMetadata, boolean embeddedFilesOnly, byte[] documentId) {
        ownerPassword = generateOwnerPasswordIfNullOrEmpty(ownerPassword);
        calculatePermissions(permissions);

        this.documentId = documentId;
        keyLength = getKeyLength(encryptionDictionary);
        // PDF reference 3.5.2 Standard Security Handler, Algorithm 3.3-1
        // If there is no owner password, use the user password instead.
        byte[] userPad = padPassword(userPassword);
        byte[] ownerPad = padPassword(ownerPassword);

        byte[] ownerKey = computeOwnerKey(userPad, ownerPad);
        computeGlobalEncryptionKey(userPad, ownerKey, encryptMetadata);
        byte[] userKey = computeUserKey();

        setStandardHandlerDicEntries(encryptionDictionary, userKey, ownerKey);
        setSpecificHandlerDicEntries(encryptionDictionary, encryptMetadata, embeddedFilesOnly);
    }

    private void initKeyAndReadDictionary(PdfDictionary encryptionDictionary, byte[] password, byte[] documentId, boolean encryptMetadata) {
        byte[] uValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.U));
        byte[] oValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.O));

        PdfNumber pValue = (PdfNumber) encryptionDictionary.get(PdfName.P);
        this.permissions = pValue.longValue();

        this.documentId = documentId;
        keyLength = getKeyLength(encryptionDictionary);
        byte[] paddedPassword = padPassword(password);
        checkPassword(encryptMetadata, uValue, oValue, paddedPassword);
    }

    private void checkPassword(boolean encryptMetadata, byte[] uValue, byte[] oValue, byte[] paddedPassword) {
        byte[] userKey;// assume password - is owner password
        byte[] userPad = computeOwnerKey(oValue, paddedPassword);
        computeGlobalEncryptionKey(userPad, oValue, encryptMetadata);
        userKey = computeUserKey();
        if (isValidPassword(uValue, userKey)) { // computed user key should be equal to uValue
            // assume password - is user password
            computeGlobalEncryptionKey(paddedPassword, oValue, encryptMetadata);
            userKey = computeUserKey();
            if (isValidPassword(uValue, userKey)) { // computed user key should be equal to uValue
                throw new BadPasswordException(PdfException.BadUserPassword);
            }
            usedOwnerPassword = false;
        }
    }


    private byte[] padPassword(byte[] password) {
        byte[] userPad = new byte[32];
        if (password == null) {
            System.arraycopy(pad, 0, userPad, 0, 32);
        } else {
            System.arraycopy(password, 0, userPad, 0, Math.min(
                    password.length, 32));
            if (password.length < 32)
                System.arraycopy(pad, 0, userPad, password.length,
                        32 - password.length);
        }

        return userPad;
    }

    private int getKeyLength(PdfDictionary encryptionDict) {
        Integer keyLength = encryptionDict.getAsInt(PdfName.Length);
        return keyLength != null ? (int) keyLength : 40;
    }
}
