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

import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockRevocationDataValidator extends RevocationDataValidator {

    public List<ICrlClient> crlClientsAdded = new ArrayList<>();
    public List<IOcspClient> ocspClientsAdded = new ArrayList<>();

    public List<RevocationDataValidatorCall> calls = new ArrayList<>();

    /**
     * Creates new {@link RevocationDataValidator} instance to validate certificate revocation data.
     */
    MockRevocationDataValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public RevocationDataValidator addCrlClient(ICrlClient crlClient) {
        crlClientsAdded.add(crlClient);
        return this;
    }

    @Override
    public RevocationDataValidator addOcspClient(IOcspClient ocspClient) {
        ocspClientsAdded.add(ocspClient);
        return this;
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                         Date validationDate) {
        calls.add(new RevocationDataValidatorCall(report, context, certificate, validationDate));
    }

    public final static class RevocationDataValidatorCall {
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final Date validationDate;

        public RevocationDataValidatorCall(ValidationReport report, ValidationContext context,
                                           X509Certificate certificate, Date validationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.validationDate = validationDate;
        }
    }
}
