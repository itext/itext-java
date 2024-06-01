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

import com.itextpdf.signatures.validation.v1.CertificateChainValidator;
import com.itextpdf.signatures.validation.v1.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockChainValidator extends CertificateChainValidator {

    public List<ValidationCallBack> verificationCalls = new ArrayList<ValidationCallBack>();
    private Consumer<ValidationCallBack> onCallHandler;

    public MockChainValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate, Date verificationDate) {
        ValidationCallBack call = new ValidationCallBack(certificate, context, result, verificationDate);
        if (onCallHandler != null) {
            onCallHandler.accept(call);
        }
        verificationCalls.add(call);
        return result;
    }

    public void onCallDo(Consumer<ValidationCallBack> c) {
        onCallHandler = c;
    }

    public final static class ValidationCallBack {

        public final X509Certificate certificate;
        public final ValidationContext context;
        public final ValidationReport report;
        public final Date checkDate;

        public ValidationCallBack(X509Certificate certificate, ValidationContext context, ValidationReport report, Date checkDate) {
            this.certificate = certificate;
            this.context = context;
            this.report = report;
            this.checkDate = checkDate;
        }
    }
}