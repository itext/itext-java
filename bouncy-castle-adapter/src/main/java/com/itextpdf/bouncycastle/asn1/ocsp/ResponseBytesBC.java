/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DEROctetStringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.ResponseBytes;

/**
 * Wrapper class for {@link ResponseBytes}.
 */
public class ResponseBytesBC extends ASN1EncodableBC implements IResponseBytes {
    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param responseBytes {@link ResponseBytes} to be wrapped
     */
    public ResponseBytesBC(ResponseBytes responseBytes) {
        super(responseBytes);
    }

    /**
     * Creates new wrapper instance for {@link ResponseBytes}.
     *
     * @param asn1ObjectIdentifier ASN1ObjectIdentifier wrapper
     * @param derOctetString       DEROctetString wrapper
     */
    public ResponseBytesBC(IASN1ObjectIdentifier asn1ObjectIdentifier, IDEROctetString derOctetString) {
        super(new ResponseBytes(
                ((ASN1ObjectIdentifierBC) asn1ObjectIdentifier).getASN1ObjectIdentifier(),
                ((DEROctetStringBC) derOctetString).getDEROctetString()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ResponseBytes}.
     */
    public ResponseBytes getResponseBytes() {
        return (ResponseBytes) getEncodable();
    }
}
