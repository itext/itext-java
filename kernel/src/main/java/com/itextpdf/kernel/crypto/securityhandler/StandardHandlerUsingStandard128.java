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

import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

public class StandardHandlerUsingStandard128 extends StandardHandlerUsingStandard40 {

    private static final long serialVersionUID = 7184848757909055679L;

    public StandardHandlerUsingStandard128(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
                                           int permissions, boolean encryptMetadata, boolean embeddedFilesOnly, byte[] documentId) {
        super(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
    }

    public StandardHandlerUsingStandard128(PdfDictionary encryptionDictionary, byte[] password, byte[] documentId, boolean encryptMetadata) {
        super(encryptionDictionary, password, documentId, encryptMetadata);
    }

    @Override
    protected void calculatePermissions(int permissions) {
        permissions |= PERMS_MASK_1_FOR_REVISION_3_OR_GREATER;
        permissions &= PERMS_MASK_2;
        this.permissions = permissions;
    }

    @Override
    protected byte[] computeOwnerKey(byte[] userPad, byte[] ownerPad) {
        byte[] ownerKey = new byte[32];
        byte[] digest = md5.digest(ownerPad);
        byte[] mkey = new byte[keyLength / 8];
        // only use for the input as many bit as the key consists of
        for (int k = 0; k < 50; ++k) {
            md5.update(digest, 0, mkey.length);
            System.arraycopy(md5.digest(), 0, digest, 0, mkey.length);
        }
        System.arraycopy(userPad, 0, ownerKey, 0, 32);
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < mkey.length; ++j)
                mkey[j] = (byte) (digest[j] ^ i);
            arcfour.prepareARCFOURKey(mkey);
            arcfour.encryptARCFOUR(ownerKey);
        }
        return ownerKey;
    }

    @Override
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
        // only use the really needed bits as input for the hash
        for (int k = 0; k < 50; ++k) {
            System.arraycopy(md5.digest(digest), 0, digest, 0, mkey.length);
        }

        System.arraycopy(digest, 0, mkey, 0, mkey.length);
    }

    @Override
    protected byte[] computeUserKey() {
        byte[] userKey = new byte[32];
        md5.update(pad);
        byte[] digest = md5.digest(documentId);
        System.arraycopy(digest, 0, userKey, 0, 16);
        for (int k = 16; k < 32; ++k)
            userKey[k] = 0;
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < mkey.length; ++j)
                digest[j] = (byte) (mkey[j] ^ i);
            arcfour.prepareARCFOURKey(digest, 0, mkey.length);
            arcfour.encryptARCFOUR(userKey, 0, 16);
        }
        return userKey;
    }

    @Override
    protected void setSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        if (encryptMetadata) {
            encryptionDictionary.put(PdfName.R, new PdfNumber(3));
            encryptionDictionary.put(PdfName.V, new PdfNumber(2));
        } else {
            encryptionDictionary.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
            encryptionDictionary.put(PdfName.R, new PdfNumber(4));
            encryptionDictionary.put(PdfName.V, new PdfNumber(4));
            PdfDictionary stdcf = new PdfDictionary();
            stdcf.put(PdfName.Length, new PdfNumber(16));
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
            stdcf.put(PdfName.CFM, PdfName.V2);
            PdfDictionary cf = new PdfDictionary();
            cf.put(PdfName.StdCF, stdcf);
            encryptionDictionary.put(PdfName.CF, cf);
        }
    }

    @Override
    protected boolean isValidPassword(byte[] uValue, byte[] userKey) {
        return !equalsArray(uValue, userKey, 16);
    }
}
