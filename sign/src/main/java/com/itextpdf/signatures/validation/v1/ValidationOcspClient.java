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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.validation.v1.RevocationDataValidator.OcspResponseValidationInfo;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OCSP client which is expected to be used in case OCSP responses shall be linked with generation date.
 */
public class ValidationOcspClient implements IOcspClient {

    private final Map<IBasicOCSPResp, OcspResponseValidationInfo> responses = new HashMap<>();

    /**
     * Create new {@link ValidationOcspClient} instance.
     */
    public ValidationOcspClient() {
        // Empty constructor in order for default one to not be removed if another one is added.
    }

    /**
     * Add OCSP response which is linked with generation date.
     *
     * @param response         {@link IBasicOCSPResp} response to be added
     * @param date             {@link Date} to be linked with the response
     * @param context {@link TimeBasedContext} time based context which corresponds to generation date
     */
    public void addResponse(IBasicOCSPResp response, Date date, TimeBasedContext context) {
        responses.put(response, new OcspResponseValidationInfo(null, response, date, context));
    }

    /**
     * Get all the OCSP responses linked with generation dates.
     *
     * @return all the OCSP responses linked with generation dates
     */
    public Map<IBasicOCSPResp, OcspResponseValidationInfo> getResponses() {
        return Collections.unmodifiableMap(responses);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        if (responses.isEmpty()) {
            return null;
        }
        try {
            // This method is never actually expected to be used, that's why we just return single latest response.
            return responses.entrySet().stream().sorted((r1, r2) ->
                            r2.getKey().getProducedAt().compareTo(r1.getKey().getProducedAt()))
                    .collect(Collectors.toList()).get(0).getKey().getEncoded();
        } catch (IOException e) {
            return null;
        }
    }
}