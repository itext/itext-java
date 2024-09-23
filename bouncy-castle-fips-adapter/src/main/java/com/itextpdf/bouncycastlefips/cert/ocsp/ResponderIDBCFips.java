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
package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.bouncycastlefips.asn1.x500.X500NameBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IResponderID;

import org.bouncycastle.asn1.ocsp.ResponderID;

public class ResponderIDBCFips implements IResponderID {
    private final ResponderID responderID;

    /**
     * Creates new wrapper instance for {@link ResponderID}.
     *
     * @param responderID {@link ResponderID} to be wrapped
     */
    public ResponderIDBCFips(ResponderID responderID) {
        this.responderID = responderID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX500Name getName() {
        return new X500NameBCFips(responderID.getName());
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link ResponderID}.
     */
    public ResponderID getResponderID() {
        return responderID;
    }
}
