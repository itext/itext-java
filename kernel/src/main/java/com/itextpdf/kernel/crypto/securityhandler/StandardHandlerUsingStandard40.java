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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.ARCFOUREncryption;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.crypto.OutputStreamStandardEncryption;
import com.itextpdf.kernel.crypto.StandardDecryptor;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
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

    protected byte[] documentId;
    // stores key length of the main key
    protected int keyLength;

    protected ARCFOUREncryption arcfour = new ARCFOUREncryption();

    private static final int DEFAULT_KEY_LENGTH = 40;

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
        this.permissions = pValue.intValue();

        this.documentId = documentId;
        keyLength = getKeyLength(encryptionDictionary);
        byte[] paddedPassword = padPassword(password);
        checkPassword(encryptMetadata, uValue, oValue, paddedPassword);
    }

    private void checkPassword(boolean encryptMetadata, byte[] uValue, byte[] oValue, byte[] paddedPassword) {
        // assume password - is owner password
        byte[] userKey;
        byte[] userPad = computeOwnerKey(oValue, paddedPassword);
        computeGlobalEncryptionKey(userPad, oValue, encryptMetadata);
        userKey = computeUserKey();
        // computed user key should be equal to uValue
        if (isValidPassword(uValue, userKey)) {
            // assume password - is user password
            computeGlobalEncryptionKey(paddedPassword, oValue, encryptMetadata);
            userKey = computeUserKey();
            // computed user key should be equal to uValue
            if (isValidPassword(uValue, userKey)) {
                throw new BadPasswordException(KernelExceptionMessageConstant.BAD_USER_PASSWORD);
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
        return keyLength != null ? (int) keyLength : DEFAULT_KEY_LENGTH;
    }
}
