/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;

public abstract class SecurityHandler implements Serializable {

    private static final long serialVersionUID = 7980424575363686173L;
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


    protected transient MessageDigest md5;
    /**
     * Work area to prepare the object/generation bytes
     */
    protected byte[] extra = new byte[5];

    protected SecurityHandler() {
        safeInitMessageDigest();
    }

    /**
     * Note: For most of the supported security handlers algorithm to calculate encryption key for particular object
     * is the same.
     *
     * @param objNumber number of particular object for encryption
     * @param objGeneration generation of particular object for encryption
     */
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // added by ujihara
        md5.reset();
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

    public abstract IDecryptor getDecryptor();

    private void safeInitMessageDigest() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        safeInitMessageDigest();
    }
}
