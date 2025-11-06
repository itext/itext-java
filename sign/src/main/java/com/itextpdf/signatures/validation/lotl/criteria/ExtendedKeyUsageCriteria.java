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

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Extended Key Usage Criteria implementation from a TL.
 */
public class ExtendedKeyUsageCriteria implements Criteria {
    private final List<String> requiredExtendedKeyUsages = new ArrayList<>();

    /**
     * Creates new instance of {@link ExtendedKeyUsageCriteria}.
     */
    public ExtendedKeyUsageCriteria() {
        // Empty constructor
    }

    /**
     * Adds required extended key usage.
     *
     * @param requiredExtendedKeyUsage {@link String} required extended key usage.
     */
    public void addRequiredExtendedKeyUsage(String requiredExtendedKeyUsage) {
        requiredExtendedKeyUsages.add(requiredExtendedKeyUsage);
    }

    /**
     * Gets the required extended key usages.
     *
     * @return the required extended key usages
     */
    public List<String> getRequiredExtendedKeyUsages() {
        return new ArrayList<>(requiredExtendedKeyUsages);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkCriteria(X509Certificate certificate) {
        try {
            List extendedKeyUsage = certificate.getExtendedKeyUsage();
            for (String requiredExtendedKeyUsage : requiredExtendedKeyUsages) {
                if (!extendedKeyUsage.contains(requiredExtendedKeyUsage)) {
                    return false;
                }
            }
        } catch (CertificateParsingException ignored) {
            return false;
        }
        return true;
    }
}
