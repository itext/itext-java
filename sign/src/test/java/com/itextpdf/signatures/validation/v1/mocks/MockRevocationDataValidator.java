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
package com.itextpdf.signatures.validation.v1.mocks;

import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.validation.v1.RevocationDataValidator;
import com.itextpdf.signatures.validation.v1.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockRevocationDataValidator extends RevocationDataValidator {

    public List<ICrlClient> crlClientsAdded = new ArrayList<>();
    public List<IOcspClient> ocspClientsAdded = new ArrayList<>();

    public List<RevocationDataValidatorCall> calls = new ArrayList<>();
    private Consumer<RevocationDataValidatorCall> onValidateHandler;
    private Consumer<ICrlClient> onAddCrlClientHandler;
    private Consumer<IOcspClient> onAddOCSPClientHandler;

    /**
     * Creates new {@link RevocationDataValidator} instance to validate certificate revocation data.
     */
    public MockRevocationDataValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public RevocationDataValidator addCrlClient(ICrlClient crlClient) {
        crlClientsAdded.add(crlClient);
        if (onAddCrlClientHandler != null) {
            onAddCrlClientHandler.accept(crlClient);
        }
        return this;
    }

    @Override
    public RevocationDataValidator addOcspClient(IOcspClient ocspClient) {
        ocspClientsAdded.add(ocspClient);
        if (onAddOCSPClientHandler != null) {
            onAddOCSPClientHandler.accept(ocspClient);
        }
        return this;
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                         Date validationDate) {
        RevocationDataValidatorCall call = new RevocationDataValidatorCall(report, context, certificate, validationDate);
        calls.add(call);
        if (onValidateHandler != null) {
            onValidateHandler.accept(call);
        }
    }

    public MockRevocationDataValidator onValidateDo(Consumer<RevocationDataValidatorCall> callBack) {
        onValidateHandler = callBack;
        return this;
    }

    public MockRevocationDataValidator onAddCerlClientDo(Consumer<ICrlClient> callBack) {
        onAddCrlClientHandler = callBack;
        return this;
    }

    public MockRevocationDataValidator onAddOCSPClientDo(Consumer<IOcspClient> callBack) {
        onAddOCSPClientHandler = callBack;
        return this;
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
