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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.crypto.AESCipherCBCnoPad;
import com.itextpdf.kernel.crypto.AesDecryptor;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.IVGenerator;
import com.itextpdf.kernel.crypto.OutputStreamAesEncryption;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfVersion;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardHandlerUsingAes256 extends StandardSecurityHandler {

    private static final int VALIDATION_SALT_OFFSET = 32;
    private static final int KEY_SALT_OFFSET = 40;
    private static final int SALT_LENGTH = 8;

    private boolean isPdf2;
    protected boolean encryptMetadata;


    public StandardHandlerUsingAes256(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
            int permissions, boolean encryptMetadata, boolean embeddedFilesOnly, PdfVersion version) {
        isPdf2 = version != null && version.compareTo(PdfVersion.PDF_2_0) >= 0;
        initKeyAndFillDictionary(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata,
                embeddedFilesOnly);
    }

    public StandardHandlerUsingAes256(PdfDictionary encryptionDictionary, byte[] password) {
        initKeyAndReadDictionary(encryptionDictionary, password);
    }

    public boolean isEncryptMetadata() {
        return encryptMetadata;
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // in AES256 we don't recalculate nextObjectKey
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamAesEncryption(os, nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public IDecryptor getDecryptor() {
        return new AesDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    private void initKeyAndFillDictionary(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
            int permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        ownerPassword = generateOwnerPasswordIfNullOrEmpty(ownerPassword);
        permissions |= PERMS_MASK_1_FOR_REVISION_3_OR_GREATER;
        permissions &= PERMS_MASK_2;

        try {
            byte[] userKey;
            byte[] ownerKey;

            byte[] ueKey;
            byte[] oeKey;
            byte[] aes256Perms;

            if (userPassword == null) {
                userPassword = new byte[0];
            } else if (userPassword.length > 127) {
                userPassword = Arrays.copyOf(userPassword, 127);
            }
            if (ownerPassword.length > 127) {
                ownerPassword = Arrays.copyOf(ownerPassword, 127);
            }

            // first 8 bytes are validation salt; second 8 bytes are key salt
            byte[] userValAndKeySalt = IVGenerator.getIV(16);
            byte[] ownerValAndKeySalt = IVGenerator.getIV(16);

            nextObjectKey = IVGenerator.getIV(32);
            nextObjectKeySize = 32;

            byte[] hash;

            // Algorithm 8.1
            hash = computeHash(userPassword, userValAndKeySalt, 0, 8);
            userKey = Arrays.copyOf(hash, 48);
            System.arraycopy(userValAndKeySalt, 0, userKey, 32, 16);

            // Algorithm 8.2
            hash = computeHash(userPassword, userValAndKeySalt, 8, 8);
            AESCipherCBCnoPad ac = new AESCipherCBCnoPad(true, hash);
            ueKey = ac.processBlock(nextObjectKey, 0, nextObjectKey.length);


            // Algorithm 9.1
            hash = computeHash(ownerPassword, ownerValAndKeySalt, 0, 8, userKey);
            ownerKey = Arrays.copyOf(hash, 48);
            System.arraycopy(ownerValAndKeySalt, 0, ownerKey, 32, 16);

            // Algorithm 9.2
            hash = computeHash(ownerPassword, ownerValAndKeySalt, 8, 8, userKey);
            ac = new AESCipherCBCnoPad(true, hash);
            oeKey = ac.processBlock(nextObjectKey, 0, nextObjectKey.length);


            // Algorithm 10
            byte[] permsp = IVGenerator.getIV(16);
            permsp[0] = (byte) permissions;
            permsp[1] = (byte) (permissions >> 8);
            permsp[2] = (byte) (permissions >> 16);
            permsp[3] = (byte) (permissions >> 24);
            permsp[4] = (byte) (255);
            permsp[5] = (byte) (255);
            permsp[6] = (byte) (255);
            permsp[7] = (byte) (255);
            permsp[8] = encryptMetadata ? (byte) 'T' : (byte) 'F';
            permsp[9] = (byte) 'a';
            permsp[10] = (byte) 'd';
            permsp[11] = (byte) 'b';
            ac = new AESCipherCBCnoPad(true, nextObjectKey);
            aes256Perms = ac.processBlock(permsp, 0, permsp.length);

            this.permissions = permissions;
            this.encryptMetadata = encryptMetadata;
            setStandardHandlerDicEntries(encryptionDictionary, userKey, ownerKey);
            setAES256DicEntries(encryptionDictionary, oeKey, ueKey, aes256Perms, encryptMetadata, embeddedFilesOnly);
        } catch (Exception ex) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, ex);
        }
    }

    protected void setAES256DicEntries(PdfDictionary encryptionDictionary, byte[] oeKey, byte[] ueKey, byte[] aes256Perms,
                                     boolean encryptMetadata, boolean embeddedFilesOnly) {
        int vAes256 = 5;
        int rAes256 = 5;
        int rAes256Pdf2 = 6;
        encryptionDictionary.put(PdfName.OE, new PdfLiteral(StreamUtil.createEscapedString(oeKey)));
        encryptionDictionary.put(PdfName.UE, new PdfLiteral(StreamUtil.createEscapedString(ueKey)));
        encryptionDictionary.put(PdfName.Perms, new PdfLiteral(StreamUtil.createEscapedString(aes256Perms)));
        encryptionDictionary.put(PdfName.R, new PdfNumber(isPdf2 ? rAes256Pdf2 : rAes256));
        encryptionDictionary.put(PdfName.V, new PdfNumber(vAes256));
        PdfDictionary stdcf = new PdfDictionary();
        stdcf.put(PdfName.Length, new PdfNumber(32));
        if (!encryptMetadata) {
            encryptionDictionary.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
        }
        if (embeddedFilesOnly) {
            stdcf.put(PdfName.AuthEvent, PdfName.EFOpen);
            encryptionDictionary.put(PdfName.EFF, PdfName.StdCF);
            encryptionDictionary.put(PdfName.StrF, PdfName.Identity);
            encryptionDictionary.put(PdfName.StmF, PdfName.Identity);
        } else {
            stdcf.put(PdfName.AuthEvent, PdfName.DocOpen);
            encryptionDictionary.put(PdfName.StrF, PdfName.StdCF);
            encryptionDictionary.put(PdfName.StmF, PdfName.StdCF);
        }
        stdcf.put(PdfName.CFM, PdfName.AESV3);
        PdfDictionary cf = new PdfDictionary();
        cf.put(PdfName.StdCF, stdcf);
        encryptionDictionary.put(PdfName.CF, cf);
    }

    private void initKeyAndReadDictionary(PdfDictionary encryptionDictionary, byte[] password) {
        try {
            if (password == null) {
                password = new byte[0];
            } else if (password.length > 127) {
                password = Arrays.copyOf(password, 127);
            }

            isPdf2 = encryptionDictionary.getAsNumber(PdfName.R).getValue() == 6
                    || encryptionDictionary.getAsNumber(PdfName.R).getValue() == 7;

            //truncate user and owner passwords to 48 bytes where the first 32 bytes
            //are a hash value, next 8 bytes are validation salt and final 8 bytes are the key salt
            byte[] oValue = truncateArray(getIsoBytes(encryptionDictionary.getAsString(PdfName.O)));
            byte[] uValue = truncateArray(getIsoBytes(encryptionDictionary.getAsString(PdfName.U)));
            byte[] oeValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.OE));
            byte[] ueValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.UE));
            byte[] perms = getIsoBytes(encryptionDictionary.getAsString(PdfName.Perms));
            PdfNumber pValue = (PdfNumber) encryptionDictionary.get(PdfName.P);

            this.permissions = pValue.longValue();

            byte[] hash;

            hash = computeHash(password, oValue, VALIDATION_SALT_OFFSET, SALT_LENGTH, uValue);
            usedOwnerPassword = equalsArray(hash, oValue, 32);

            if (usedOwnerPassword) {
                hash = computeHash(password, oValue, KEY_SALT_OFFSET, SALT_LENGTH, uValue);
                AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, hash);
                nextObjectKey = ac.processBlock(oeValue, 0, oeValue.length);
            } else {
                hash = computeHash(password, uValue, VALIDATION_SALT_OFFSET, SALT_LENGTH);
                if (!equalsArray(hash, uValue, 32)) {
                    throw new BadPasswordException(KernelExceptionMessageConstant.BAD_USER_PASSWORD);
                }
                hash = computeHash(password, uValue, KEY_SALT_OFFSET, SALT_LENGTH);
                AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, hash);
                nextObjectKey = ac.processBlock(ueValue, 0, ueValue.length);
            }
            nextObjectKeySize = 32;

            AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, nextObjectKey);
            byte[] decPerms = ac.processBlock(perms, 0, perms.length);
            if (decPerms[9] != (byte) 'a' || decPerms[10] != (byte) 'd' || decPerms[11] != (byte) 'b') {
                throw new BadPasswordException(KernelExceptionMessageConstant.BAD_USER_PASSWORD);
            }
            int permissionsDecoded = (decPerms[0] & 0xff) | ((decPerms[1] & 0xff) << 8)
                    | ((decPerms[2] & 0xff) << 16) | ((decPerms[3] & 0xff) << 24);
            boolean encryptMetadata = decPerms[8] == (byte) 'T';

            Boolean encryptMetadataEntry = encryptionDictionary.getAsBool(PdfName.EncryptMetadata);
            if (permissionsDecoded != permissions || encryptMetadataEntry != null && encryptMetadata != encryptMetadataEntry) {
                Logger logger = LoggerFactory.getLogger(StandardHandlerUsingAes256.class);
                logger.error(IoLogMessageConstant.ENCRYPTION_ENTRIES_P_AND_ENCRYPT_METADATA_NOT_CORRESPOND_PERMS_ENTRY);
            }
            this.permissions = permissionsDecoded;
            this.encryptMetadata = encryptMetadata;
        } catch (BadPasswordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, ex);
        }
    }

    private byte[] computeHash(byte[] password, byte[] salt, int saltOffset, int saltLen) throws NoSuchAlgorithmException {
        return computeHash(password, salt, saltOffset, saltLen, null);
    }

    private byte[] computeHash(byte[] password, byte[] salt, int saltOffset, int saltLen, byte[] userKey) throws NoSuchAlgorithmException {
        MessageDigest mdSha256 = MessageDigest.getInstance("SHA-256");

        mdSha256.update(password);
        mdSha256.update(salt, saltOffset, saltLen);
        if (userKey != null) {
            mdSha256.update(userKey);
        }
        byte[] k = mdSha256.digest();

        if (isPdf2) {
            // See 7.6.4.3.3 "Algorithm 2.B"

            MessageDigest mdSha384 = MessageDigest.getInstance("SHA-384");
            MessageDigest mdSha512 = MessageDigest.getInstance("SHA-512");

            int userKeyLen = userKey != null ? userKey.length : 0;
            int passAndUserKeyLen = password.length + userKeyLen;
            // k1 repetition length
            int k1RepLen;

            int roundNum = 0;
            while (true) {
                // a)
                k1RepLen = passAndUserKeyLen + k.length;
                byte[] k1 = new byte[k1RepLen * 64];
                System.arraycopy(password, 0, k1, 0, password.length);
                System.arraycopy(k, 0, k1, password.length, k.length);
                if (userKey != null) {
                    System.arraycopy(userKey, 0, k1, password.length + k.length, userKeyLen);
                }
                for (int i = 1; i < 64; ++i) {
                    System.arraycopy(k1, 0, k1, k1RepLen * i, k1RepLen);
                }

                // b)
                AESCipherCBCnoPad cipher = new AESCipherCBCnoPad(true, Arrays.copyOf(k, 16), Arrays.copyOfRange(k, 16, 32));
                byte[] e = cipher.processBlock(k1, 0, k1.length);

                // c)
                MessageDigest md = null;
                BigInteger i = new BigInteger(1, Arrays.copyOf(e, 16));
                int remainder = i.remainder(BigInteger.valueOf(3)).intValue();
                switch (remainder) {
                    case 0:
                        md = mdSha256;
                        break;
                    case 1:
                        md = mdSha384;
                        break;
                    case 2:
                        md = mdSha512;
                        break;
                }

                // d)
                k = md.digest(e);

                ++roundNum;
                if (roundNum > 63) {
                    // e)
                    // interpreting last byte as unsigned integer
                    int condVal = e[e.length - 1] & 0xFF;
                    if (condVal <= roundNum - 32) {
                        break;
                    }
                }
            }

            k = k.length == 32 ? k : Arrays.copyOf(k, 32);
        }

        return k;
    }

    private byte[] truncateArray(byte[] array) {
        if (array.length == 48) {
            return array;
        }
        for (int i = 48; i < array.length; ++i) {
            if (array[i] != 0) {
                throw new PdfException(KernelExceptionMessageConstant.BAD_PASSWORD_HASH);
            }
        }
        byte[] truncated = new byte[48];
        System.arraycopy(array, 0, truncated, 0, 48);
        return truncated;
    }
}
