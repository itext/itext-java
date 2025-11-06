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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class corresponding to CertSubjectDNAttribute criteria in TL.
 */
public class CertSubjectDNAttributeCriteria implements Criteria {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final List<String> requiredAttributeIds = new ArrayList<>();

    /**
     * Creates a new instance of {@link CertSubjectDNAttributeCriteria}.
     */
    public CertSubjectDNAttributeCriteria() {
        // Empty constructor
    }

    /**
     * Adds required attribute ID into the criteria.
     *
     * @param requiredAttributeId required attribute ID
     */
    public void addRequiredAttributeId(String requiredAttributeId) {
        requiredAttributeIds.add(requiredAttributeId);
    }

    /**
     * Gets the required attribute IDs.
     *
     * @return required attribute IDs
     */
    public List<String> getRequiredAttributeIds() {
        return new ArrayList<>(requiredAttributeIds);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkCriteria(X509Certificate certificate) {
        try {
            List<String> subjectAttributes =
                    Arrays.stream(FACTORY.createJcaX509CertificateHolder(certificate).getSubjectAttributeTypes())
                            .map(asn1Attribute -> asn1Attribute.getId()).collect(Collectors.toList());
            for (String requiredAttributeId : requiredAttributeIds) {
                if (!subjectAttributes.contains(requiredAttributeId)) {
                    return false;
                }
            }
            return true;
        } catch (CertificateEncodingException ignored) {
            return false;
        }
    }
}
