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
package com.itextpdf.bouncycastle.asn1.tsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.tsp.IMessageImprint;
import com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo;

import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.tsp.TSTInfo;

/**
 * Wrapper class for {@link TSTInfo}.
 */
public class TSTInfoBC extends ASN1EncodableBC implements ITSTInfo {
    /**
     * Creates new wrapper instance for {@link TSTInfo}.
     *
     * @param tstInfo {@link TSTInfo} to be wrapped
     */
    public TSTInfoBC(TSTInfo tstInfo) {
        super(tstInfo);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TSTInfo}.
     */
    public TSTInfo getTstInfo() {
        return (TSTInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMessageImprint getMessageImprint() {
        return new MessageImprintBC(getTstInfo().getMessageImprint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getGenTime() throws ParseException {
        return getTstInfo().getGenTime().getDate();
    }
}
