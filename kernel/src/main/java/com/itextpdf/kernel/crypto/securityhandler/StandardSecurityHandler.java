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

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;

public abstract class StandardSecurityHandler extends SecurityHandler {

    protected static final int PERMS_MASK_1_FOR_REVISION_2 = 0xffffffc0;
    protected static final int PERMS_MASK_1_FOR_REVISION_3_OR_GREATER = 0xfffff0c0;
    protected static final int PERMS_MASK_2 = 0xfffffffc;
    private static final long serialVersionUID = 5414978568831015690L;

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
