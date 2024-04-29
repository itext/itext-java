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

import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class MockChainValidator extends CertificateChainValidator {

    public List<ValidationCallBack> verificationCalls = new ArrayList<ValidationCallBack>();

    MockChainValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public ValidationReport validate(ValidationReport result, ValidationContext context, X509Certificate certificate, Date verificationDate) {
        verificationCalls.add(new ValidationCallBack(certificate, verificationDate));
        return result;
    }

    public static class ValidationCallBack {
        public X509Certificate certificate;
        public Date checkDate;

        public ValidationCallBack(X509Certificate certificate, Date checkDate) {
            this.certificate = certificate;
            this.checkDate = checkDate;
        }
    }
}
