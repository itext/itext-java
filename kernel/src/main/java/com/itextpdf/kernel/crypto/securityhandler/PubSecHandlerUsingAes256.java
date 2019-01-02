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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import java.security.Key;
import java.security.cert.Certificate;

public class PubSecHandlerUsingAes256 extends PubSecHandlerUsingAes128 {
    private static final long serialVersionUID = -9158784716845784422L;

    public PubSecHandlerUsingAes256(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        super(encryptionDictionary, certs, permissions, encryptMetadata, embeddedFilesOnly);
    }

    public PubSecHandlerUsingAes256(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate, String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess, boolean encryptMetadata) {
        super(encryptionDictionary, certificateKey, certificate, certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // in AES256 we don't recalculate nextObjectKey
    }

    @Override
    protected String getDigestAlgorithm() {
        return "SHA-256";
    }

    protected void initKey(byte[] globalKey, int keyLength) {
        nextObjectKey = globalKey;
        nextObjectKeySize = 32;
    }

    @Override
    protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        encryptionDictionary.put(PdfName.Filter, PdfName.Adobe_PubSec);
        encryptionDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_s5);

        encryptionDictionary.put(PdfName.R, new PdfNumber(5));
        encryptionDictionary.put(PdfName.V, new PdfNumber(5));

        PdfArray recipients = createRecipientsArray();
        PdfDictionary stdcf = new PdfDictionary();
        stdcf.put(PdfName.Recipients, recipients);
        if (!encryptMetadata) {
            stdcf.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
        }
        stdcf.put(PdfName.CFM, PdfName.AESV3);
        stdcf.put(PdfName.Length, new PdfNumber(256));
        PdfDictionary cf = new PdfDictionary();
        cf.put(PdfName.DefaultCryptFilter, stdcf);
        encryptionDictionary.put(PdfName.CF, cf);
        if (embeddedFilesOnly) {
            encryptionDictionary.put(PdfName.EFF, PdfName.DefaultCryptFilter);
            encryptionDictionary.put(PdfName.StrF, PdfName.Identity);
            encryptionDictionary.put(PdfName.StmF, PdfName.Identity);
        } else {
            encryptionDictionary.put(PdfName.StrF, PdfName.DefaultCryptFilter);
            encryptionDictionary.put(PdfName.StmF, PdfName.DefaultCryptFilter);
        }
    }
}
