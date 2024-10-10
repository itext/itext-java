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

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;

import java.security.MessageDigest;

public abstract class StandardSecurityHandler extends SecurityHandler {

    protected static final int PERMS_MASK_1_FOR_REVISION_2 = 0xffffffc0;
    protected static final int PERMS_MASK_1_FOR_REVISION_3_OR_GREATER = 0xffffe0c0;
    protected static final int PERMS_MASK_2 = 0xfffffffc;

    protected int permissions;
    protected boolean usedOwnerPassword = true;

    public int getPermissions() {
        return permissions;
    }

    /**
     * Updates encryption dictionary with the security permissions provided.
     *
     * @param permissions new permissions to set
     * @param encryptionDictionary encryption dictionary to update
     */
    public void setPermissions(int permissions, PdfDictionary encryptionDictionary) {
        this.permissions = permissions;
        encryptionDictionary.put(PdfName.P, new PdfNumber(permissions));
    }

    public boolean isUsedOwnerPassword() {
        return usedOwnerPassword;
    }

    protected void setStandardHandlerDicEntries(PdfDictionary encryptionDictionary, byte[] userKey, byte[] ownerKey) {
        encryptionDictionary.put(PdfName.Filter, PdfName.Standard);
        encryptionDictionary.put(PdfName.O, new PdfLiteral(StreamUtil.createEscapedString(ownerKey)));
        encryptionDictionary.put(PdfName.U, new PdfLiteral(StreamUtil.createEscapedString(userKey)));
        encryptionDictionary.put(PdfName.P, new PdfNumber(permissions));
    }

    protected byte[] generateOwnerPasswordIfNullOrEmpty(byte[] ownerPassword) {
        if (ownerPassword == null || ownerPassword.length == 0) {
            try {
                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                ownerPassword = sha256.digest(PdfEncryption.generateNewDocumentId());
            } catch (Exception e) {
                throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
            }
        }
        return ownerPassword;
    }

    /**
     * Gets bytes of String-value without considering encoding.
     *
     * @param string a {@link PdfString} to get bytes from it
     * @return byte array
     */
    protected byte[] getIsoBytes(PdfString string) {
        return ByteUtils.getIsoBytes(string.getValue());
    }

    protected static boolean equalsArray(byte[] ar1, byte[] ar2, int size) {
        for (int k = 0; k < size; ++k) {
            if (ar1[k] != ar2[k]) {
                return false;
            }
        }
        return true;
    }
}
