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
import com.itextpdf.signatures.CertificateUtil;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Policy Set Criteria implementation from a TL.
 */
public class PolicySetCriteria implements Criteria {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String POLICY_SET_EXTENSION_ID = "2.5.29.32";
    private final List<String> requiredPolicyIdentifiers = new ArrayList<>();

    /**
     * Creates new instance of {@link PolicySetCriteria}.
     */
    public PolicySetCriteria() {
        // Empty constructor
    }

    /**
     * Adds required policy id.
     *
     * @param requiredPolicyId required policy id
     */
    public void addRequiredPolicyId(String requiredPolicyId) {
        requiredPolicyIdentifiers.add(requiredPolicyId);
    }

    /**
     * Gets the required policy IDs.
     *
     * @return the required policy IDs
     */
    public List<String> getRequiredPolicyIds() {
        return new ArrayList<>(requiredPolicyIdentifiers);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkCriteria(X509Certificate certificate) {
        byte[] policyExtension = CertificateUtil.getExtensionValueByOid(certificate, POLICY_SET_EXTENSION_ID);
        if (policyExtension == null) {
            return false;
        }
        try {
            List<String> policyIds = FACTORY.getPoliciesIds(policyExtension);
            for (String requiredPolicyIdentifier : requiredPolicyIdentifiers) {
                if (!policyIds.contains(requiredPolicyIdentifier)) {
                    return false;
                }
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }
}
