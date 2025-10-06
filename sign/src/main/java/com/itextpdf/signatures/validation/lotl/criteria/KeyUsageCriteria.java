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
package com.itextpdf.signatures.validation.lotl.criteria;

import java.security.cert.X509Certificate;

/**
 * Key Usage Criteria implementation from a TL.
 */
public class KeyUsageCriteria implements Criteria {

    private final Boolean[] requiredKeyUsage = new Boolean[9];

    /**
     * Creates a new instance of {@link KeyUsageCriteria}.
     */
    public KeyUsageCriteria() {
        // Empty constructor
    }

    /**
     * Adds required key usage bit by its name and value.
     *
     * @param name name of the required key usage bit.
     * @param value {@code boolean} value for a required key usage bit.
     */
    public void addKeyUsageBit(String name, String value) {
        boolean booleanValue = "true".equals(value);
        switch (name) {
            case "digitalSignature":
                requiredKeyUsage[0] = booleanValue;
                break;
            case "nonRepudiation":
                requiredKeyUsage[1] = booleanValue;
                break;
            case "keyEncipherment":
                requiredKeyUsage[2] = booleanValue;
                break;
            case "dataEncipherment":
                requiredKeyUsage[3] = booleanValue;
                break;
            case "keyAgreement":
                requiredKeyUsage[4] = booleanValue;
                break;
            case "keyCertSign":
                requiredKeyUsage[5] = booleanValue;
                break;
            case "crlSign":
                requiredKeyUsage[6] = booleanValue;
                break;
            case "encipherOnly":
                requiredKeyUsage[7] = booleanValue;
                break;
            case "decipherOnly":
                requiredKeyUsage[8] = booleanValue;
                break;
        }
    }

    /**
     * Gets required key usage bits.
     *
     * @return required key usage bits
     */
    public Boolean[] getKeyUsageBits() {
        return requiredKeyUsage;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkCriteria(X509Certificate certificate) {
        boolean[] keyUsage = certificate.getKeyUsage();
        if (keyUsage == null || keyUsage.length != requiredKeyUsage.length) {
            return false;
        }
        for (int i = 0; i < keyUsage.length; ++i) {
            if (requiredKeyUsage[i] == null) {
                continue;
            }
            if (keyUsage[i] != requiredKeyUsage[i]) {
                return false;
            }
        }
        return true;
    }
}
