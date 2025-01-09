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
package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IGeneralName;

import org.bouncycastle.asn1.x509.GeneralName;

/**
 * Wrapper class for {@link GeneralName}.
 */
public class GeneralNameBCFips extends ASN1EncodableBCFips implements IGeneralName {
    private static final GeneralNameBCFips INSTANCE = new GeneralNameBCFips(null);

    private static final int UNIFORM_RESOURCE_IDENTIFIER = GeneralName.uniformResourceIdentifier;

    /**
     * Creates new wrapper instance for {@link GeneralName}.
     *
     * @param generalName {@link GeneralName} to be wrapped
     */
    public GeneralNameBCFips(GeneralName generalName) {
        super(generalName);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link GeneralNameBCFips} instance.
     */
    public static GeneralNameBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link GeneralName}.
     */
    public GeneralName getGeneralName() {
        return (GeneralName) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTagNo() {
        return getGeneralName().getTagNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniformResourceIdentifier() {
        return UNIFORM_RESOURCE_IDENTIFIER;
    }
}
