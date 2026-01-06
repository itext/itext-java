/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.bouncycastlefips.asn1.x509.qualified;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.qualified.IQCStatement;
import org.bouncycastle.asn1.x509.qualified.QCStatement;

/**
 * Wrapper class for QCStatement.
 */
public class QCStatementBCFips extends ASN1EncodableBCFips implements IQCStatement {
    /**
     * Creates a new wrapper instance for {@link QCStatement}.
     *
     * @param qcStatement {@link QCStatement} to be wrapped
     */
    public QCStatementBCFips(QCStatement qcStatement) {
        super(qcStatement);
    }

    /**
     * Gets actual {@link QCStatement} object being wrapped.
     *
     * @return wrapped {@link QCStatement}.
     */
    public QCStatement getQCStatement() {
        return (QCStatement) getEncodable();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IASN1ObjectIdentifier getStatementId() {
        return new ASN1ObjectIdentifierBCFips(getQCStatement().getStatementId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IASN1Encodable getStatementInfo() {
        return new ASN1EncodableBCFips(getQCStatement().getStatementInfo());
    }
}
