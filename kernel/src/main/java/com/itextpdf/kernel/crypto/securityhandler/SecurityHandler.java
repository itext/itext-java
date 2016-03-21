package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.Decryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import java.security.MessageDigest;

public abstract class SecurityHandler {

    /**
     * The global encryption key
     */
    protected byte[] mkey = new byte[0];

    /**
     * The encryption key for a particular object/generation.
     * It is recalculated with {@link #setHashKeyForNextObject(int,int)} for every object individually based in its object/generation.
     */
    protected byte[] nextObjectKey;
    /**
     * The encryption key length for a particular object/generation
     * It is recalculated with {@link #setHashKeyForNextObject(int,int)} for every object individually based in its object/generation.
     */
    protected int nextObjectKeySize;


    protected MessageDigest md5;
    /**
     * Work area to prepare the object/generation bytes
     */
    protected byte extra[] = new byte[5];

    public SecurityHandler() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
    }

    /**
     * Note: For most of the supported security handlers algorithm to calculate encryption key for particular object
     * is the same.
     * @param objNumber
     * @param objGeneration
     */
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        md5.reset(); // added by ujihara
        extra[0] = (byte) objNumber;
        extra[1] = (byte) (objNumber >> 8);
        extra[2] = (byte) (objNumber >> 16);
        extra[3] = (byte) objGeneration;
        extra[4] = (byte) (objGeneration >> 8);
        md5.update(mkey);
        md5.update(extra);
        nextObjectKey = md5.digest();
        nextObjectKeySize = mkey.length + 5;
        if (nextObjectKeySize > 16) {
            nextObjectKeySize = 16;
        }
    }

    public abstract OutputStreamEncryption getEncryptionStream(java.io.OutputStream os);
    public abstract Decryptor getDecryptor();
}
