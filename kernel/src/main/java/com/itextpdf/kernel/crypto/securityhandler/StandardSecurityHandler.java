package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;

public abstract class StandardSecurityHandler extends SecurityHandler {

    protected static final int permsMask1ForRevision2 = 0xffffffc0;
    protected static final int permsMask1ForRevision3OrGreater = 0xfffff0c0;
    protected static final int permsMask2 = 0xfffffffc;

    protected long permissions;
    protected boolean usedOwnerPassword = true;

    public long getPermissions() {
        return permissions;
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
            ownerPassword = md5.digest(PdfEncryption.generateNewDocumentId());
        }
        return ownerPassword;
    }

    /**
     * Gets bytes of String-value without considering encoding.
     *
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
