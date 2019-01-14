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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Produces a blank (or empty) signature. Useful for deferred signing with
 * MakeSignature.signExternalContainer().
 * @author Paulo Soares
 */
public class ExternalBlankSignatureContainer implements IExternalSignatureContainer {

    /* The Signature dictionary. Should contain values for /Filter and /SubFilter at minimum. */
    private PdfDictionary sigDic;

    /**
     * Creates an ExternalBlankSignatureContainer.
     * @param sigDic PdfDictionary containing signature iformation. /SubFilter and /Filter aren't set in this constructor.
     */
    public ExternalBlankSignatureContainer(PdfDictionary sigDic) {
        this.sigDic = sigDic;
    }

    /**
     * Creates an ExternalBlankSignatureContainer. This constructor will create the PdfDictionary for the
     * signature information and will insert the  /Filter and /SubFilter values into this dictionary.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public ExternalBlankSignatureContainer(PdfName filter, PdfName subFilter) {
        sigDic = new PdfDictionary();
        sigDic.put(PdfName.Filter, filter);
        sigDic.put(PdfName.SubFilter, subFilter);
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        return new byte[0];
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {
        signDic.putAll(sigDic);
    }

}
