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
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IResponseBytes;

import org.bouncycastle.asn1.ocsp.OCSPResponse;

/**
 * Wrapper class for {@link OCSPResponse}.
 */
public class OCSPResponseBC extends ASN1EncodableBC implements IOCSPResponse {
    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param ocspResponse {@link OCSPResponse} to be wrapped
     */
    public OCSPResponseBC(OCSPResponse ocspResponse) {
        super(ocspResponse);
    }

    /**
     * Creates new wrapper instance for {@link OCSPResponse}.
     *
     * @param respStatus    OCSPResponseStatus wrapper
     * @param responseBytes ResponseBytes wrapper
     */
    public OCSPResponseBC(IOCSPResponseStatus respStatus, IResponseBytes responseBytes) {
        super(new OCSPResponse(
                ((OCSPResponseStatusBC) respStatus).getOcspResponseStatus(),
                ((ResponseBytesBC) responseBytes).getResponseBytes()));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResponse}.
     */
    public OCSPResponse getOcspResponse() {
        return (OCSPResponse) getEncodable();
    }
}
