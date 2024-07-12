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
package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime;

import org.bouncycastle.asn1.ASN1GeneralizedTime;

import java.text.ParseException;
import java.util.Date;

/**
 * Wrapper class for {@link ASN1GeneralizedTime}.
 */
public class ASN1GeneralizedTimeBCFips extends ASN1PrimitiveBCFips implements IASN1GeneralizedTime {
    /**
     * Creates new wrapper instance for {@link ASN1GeneralizedTime}.
     *
     * @param asn1GeneralizedTime {@link ASN1GeneralizedTime} to be wrapped
     */
    public ASN1GeneralizedTimeBCFips(ASN1GeneralizedTime asn1GeneralizedTime) {
        super(asn1GeneralizedTime);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ASN1GeneralizedTime}.
     */
    public ASN1GeneralizedTime getASN1GeneralizedTime() {
        return (ASN1GeneralizedTime) getEncodable();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @throws ParseException {@inheritDoc}
     */
    @Override
    public Date getDate() throws ParseException {
        return getASN1GeneralizedTime().getDate();
    }
}
