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
package com.itextpdf.signatures.validation.mocks;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.validation.CRLValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockCrlValidator extends CRLValidator {

    public final List<CRLValidateCall> calls = new ArrayList<>();
    private Consumer<CRLValidateCall> onCallHandler;

    /**
     * Creates new {@link CRLValidator} instance.
     */
    public MockCrlValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate, X509CRL crl,
                         Date validationDate, Date responseGenerationDate) {
        CRLValidateCall call = new CRLValidateCall(report, context, certificate, crl, validationDate, responseGenerationDate);
        calls.add(call);
        if (onCallHandler != null) {
            onCallHandler.accept(calls.get(calls.size() - 1));
        }
    }

    public void onCallDo(Consumer<CRLValidateCall> c) {
        onCallHandler = c;
    }

    public final static class CRLValidateCall {
        public final Date timeStamp = DateTimeUtil.getCurrentTimeDate();
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final X509CRL crl;
        public final Date validationDate;
        public final Date responseGenerationDate;

        public CRLValidateCall(ValidationReport report, ValidationContext context, X509Certificate certificate,
                               X509CRL crl, Date validationDate, Date responseGenerationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.crl = crl;
            this.validationDate = validationDate;
            this.responseGenerationDate = responseGenerationDate;
        }
    }
}