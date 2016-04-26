/*
    $Id$

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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.AESCipherCBCnoPad;
import com.itextpdf.kernel.crypto.AesDecryptor;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.IVGenerator;
import com.itextpdf.kernel.crypto.OutputStreamAesEncryption;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import java.io.OutputStream;
import java.security.MessageDigest;

public class StandardHandlerUsingAes256 extends StandardSecurityHandler {

    private static final int VALIDATION_SALT_OFFSET = 32;
    private static final int KEY_SALT_OFFSET = 40;
    private static final int SALT_LENGTH = 8;
    private static final int OU_LENGTH = 48;

    protected boolean encryptMetadata;

    public StandardHandlerUsingAes256(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
                                      int permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        initKeyAndFillDictionary(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly);
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
            }
            byte[] uvs = IVGenerator.getIV(8);
            byte[] uks = IVGenerator.getIV(8);
            nextObjectKey = IVGenerator.getIV(32);
            nextObjectKeySize = 32;
            // Algorithm 3.8.1
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(userPassword, 0, Math.min(userPassword.length, 127));
            md.update(uvs);
            userKey = new byte[48];
            md.digest(userKey, 0, 32);
            System.arraycopy(uvs, 0, userKey, 32, 8);
            System.arraycopy(uks, 0, userKey, 40, 8);
            // Algorithm 3.8.2
            md.update(userPassword, 0, Math.min(userPassword.length, 127));
            md.update(uks);
            AESCipherCBCnoPad ac = new AESCipherCBCnoPad(true, md.digest());
            ueKey = ac.processBlock(nextObjectKey, 0, nextObjectKey.length);
            // Algorithm 3.9.1
            byte[] ovs = IVGenerator.getIV(8);
            byte[] oks = IVGenerator.getIV(8);
            md.update(ownerPassword, 0, Math.min(ownerPassword.length, 127));
            md.update(ovs);
            md.update(userKey);
            ownerKey = new byte[48];
            md.digest(ownerKey, 0, 32);
            System.arraycopy(ovs, 0, ownerKey, 32, 8);
            System.arraycopy(oks, 0, ownerKey, 40, 8);
            // Algorithm 3.9.2
            md.update(ownerPassword, 0, Math.min(ownerPassword.length, 127));
            md.update(oks);
            md.update(userKey);
            ac = new AESCipherCBCnoPad(true, md.digest());
            oeKey = ac.processBlock(nextObjectKey, 0, nextObjectKey.length);
            // Algorithm 3.10
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
            throw new PdfException(PdfException.PdfEncryption, ex);
        }
    }

    private void setAES256DicEntries(PdfDictionary encryptionDictionary, byte[] oeKey, byte[] ueKey, byte[] aes256Perms,
                                     boolean encryptMetadata, boolean embeddedFilesOnly) {
        int aes256Revision = 5;
        encryptionDictionary.put(PdfName.OE, new PdfLiteral(StreamUtil.createEscapedString(oeKey)));
        encryptionDictionary.put(PdfName.UE, new PdfLiteral(StreamUtil.createEscapedString(ueKey)));
        encryptionDictionary.put(PdfName.Perms, new PdfLiteral(StreamUtil.createEscapedString(aes256Perms)));
        encryptionDictionary.put(PdfName.R, new PdfNumber(aes256Revision));
        encryptionDictionary.put(PdfName.V, new PdfNumber(aes256Revision));
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
            if (password == null)
                password = new byte[0];
            byte[] oValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.O));
            byte[] uValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.U));
            byte[] oeValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.OE));
            byte[] ueValue = getIsoBytes(encryptionDictionary.getAsString(PdfName.UE));
            byte[] perms = getIsoBytes(encryptionDictionary.getAsString(PdfName.Perms));
            PdfNumber pValue = (PdfNumber) encryptionDictionary.get(PdfName.P);

            this.permissions = pValue.longValue();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password, 0, Math.min(password.length, 127));
            md.update(oValue, VALIDATION_SALT_OFFSET, SALT_LENGTH);
            md.update(uValue, 0, OU_LENGTH);
            byte[] hash = md.digest();
            usedOwnerPassword = compareArray(hash, oValue, 32);
            if (usedOwnerPassword) {
                md.update(password, 0, Math.min(password.length, 127));
                md.update(oValue, KEY_SALT_OFFSET, SALT_LENGTH);
                md.update(uValue, 0, OU_LENGTH);
                hash = md.digest();
                AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, hash);
                nextObjectKey = ac.processBlock(oeValue, 0, oeValue.length);
            } else {
                md.update(password, 0, Math.min(password.length, 127));
                md.update(uValue, VALIDATION_SALT_OFFSET, SALT_LENGTH);
                hash = md.digest();
                if (!compareArray(hash, uValue, 32))
                    throw new BadPasswordException(PdfException.BadUserPassword);
                md.update(password, 0, Math.min(password.length, 127));
                md.update(uValue, KEY_SALT_OFFSET, SALT_LENGTH);
                hash = md.digest();
                AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, hash);
                nextObjectKey = ac.processBlock(ueValue, 0, ueValue.length);
            }
            nextObjectKeySize = 32;

            AESCipherCBCnoPad ac = new AESCipherCBCnoPad(false, nextObjectKey);
            byte[] decPerms = ac.processBlock(perms, 0, perms.length);
            if (decPerms[9] != (byte) 'a' || decPerms[10] != (byte) 'd' || decPerms[11] != (byte) 'b')
                throw new BadPasswordException(PdfException.BadUserPassword);
            permissions = (decPerms[0] & 0xff) | ((decPerms[1] & 0xff) << 8)
                    | ((decPerms[2] & 0xff) << 16) | ((decPerms[2] & 0xff) << 24);
            encryptMetadata = decPerms[8] == (byte) 'T';
        } catch (BadPasswordException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PdfException(PdfException.PdfEncryption, ex);
        }
    }

    private static boolean compareArray(byte[] a, byte[] b, int len) {
        for (int k = 0; k < len; ++k) {
            if (a[k] != b[k]) {
                return false;
            }
        }
        return true;
    }
}
