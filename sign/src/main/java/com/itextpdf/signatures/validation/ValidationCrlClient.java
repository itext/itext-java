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
package com.itextpdf.signatures.validation;

import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.validation.RevocationDataValidator.CrlValidationInfo;
import com.itextpdf.signatures.validation.context.TimeBasedContext;

import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CRL client which is expected to be used in case CRL responses shall be linked with generation date.
 */
public class ValidationCrlClient implements ICrlClient {
    private final Map<X509CRL, CrlValidationInfo> crls = new HashMap<>();

    /**
     * Create new {@link ValidationCrlClient} instance.
     */
    public ValidationCrlClient() {
        // Empty constructor in order for default one to not be removed if another one is added.
    }

    /**
     * Add CRL response which is linked with generation date.
     *
     * @param response {@link X509CRL} response to be added
     * @param date     {@link Date} to be linked with the response
     * @param context  {@link TimeBasedContext} time based context which corresponds to generation date
     */
    public void addCrl(X509CRL response, Date date, TimeBasedContext context) {
        // We need to have these data stored in Map in order to replace duplicates.
        crls.put(response, new CrlValidationInfo(response, date, context));
    }

    /**
     * Get all the CRL responses linked with generation dates.
     *
     * @return all the CRL responses linked with generation dates
     */
    public Map<X509CRL, CrlValidationInfo> getCrls() {
        return Collections.unmodifiableMap(crls);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) throws CertificateEncodingException {
        Collection<byte[]> byteResponses = new ArrayList<>();
        for (X509CRL response : crls.keySet()) {
            try {
                byteResponses.add(response.getEncoded());
            } catch (CRLException | RuntimeException ignored) {
                // Do nothing.
            }
        }
        return byteResponses;
    }
}
