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
package com.itextpdf.signatures.validation;

import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.Date;

public class CustomDateChainValidator extends CertificateChainValidator {
    private Date validationDate;

    public CustomDateChainValidator(ValidatorChainBuilder builder, Date validationDate) {
        super(builder);
        this.validationDate = validationDate;
    }

    @Override
    public ValidationReport validate(ValidationReport result, ValidationContext context,
            X509Certificate certificate, Date validationDate) {
        return super.validate(result, context, certificate, this.validationDate);
    }
}
