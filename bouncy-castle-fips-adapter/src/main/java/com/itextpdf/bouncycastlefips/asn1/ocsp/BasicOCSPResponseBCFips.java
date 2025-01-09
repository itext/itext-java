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
package com.itextpdf.bouncycastlefips.asn1.ocsp;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;

import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;

/**
 * Wrapper class for {@link BasicOCSPResponse}.
 */
public class BasicOCSPResponseBCFips extends ASN1EncodableBCFips implements IBasicOCSPResponse {
    /**
     * Creates new wrapper instance for {@link BasicOCSPResponse}.
     *
     * @param basicOCSPResponse {@link BasicOCSPResponse} to be wrapped
     */
    public BasicOCSPResponseBCFips(BasicOCSPResponse basicOCSPResponse) {
        super(basicOCSPResponse);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link BasicOCSPResponse}.
     */
    public BasicOCSPResponse getBasicOCSPResponse() {
        return (BasicOCSPResponse) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getProducedAtDate() throws ParseException {
        return getBasicOCSPResponse().getTbsResponseData().getProducedAt().getDate();
    }
}
