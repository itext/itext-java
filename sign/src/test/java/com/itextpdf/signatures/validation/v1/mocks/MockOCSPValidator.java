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

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.validation.v1.OCSPValidator;
import com.itextpdf.signatures.validation.v1.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MockOCSPValidator extends OCSPValidator {

    public final List<OCSPValidatorCall> calls = new ArrayList<>();
    private Consumer<OCSPValidatorCall> onCallHandler;

    /**
     * Creates new {@link OCSPValidator} instance.
     */
    public MockOCSPValidator() {
        super(new ValidatorChainBuilder());
    }

    @Override
    public void validate(ValidationReport report, ValidationContext context, X509Certificate certificate,
                         ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate) {
        OCSPValidatorCall call = new OCSPValidatorCall(report, context, certificate, singleResp, ocspResp, validationDate);
        calls.add(call);
        if (onCallHandler != null) {
            onCallHandler.accept(call);
        }
    }

    public void onCallDo(Consumer<OCSPValidatorCall> c) {
        onCallHandler = c;
    }

    public final static class OCSPValidatorCall {

        public final Date timeStamp = DateTimeUtil.getCurrentTimeDate();
        public final ValidationReport report;
        public final ValidationContext context;
        public final X509Certificate certificate;
        public final ISingleResp singleResp;
        public final IBasicOCSPResp ocspResp;
        public final Date validationDate;

        public OCSPValidatorCall(ValidationReport report, ValidationContext context, X509Certificate certificate,
                                 ISingleResp singleResp, IBasicOCSPResp ocspResp, Date validationDate) {
            this.report = report;
            this.context = context;
            this.certificate = certificate;
            this.singleResp = singleResp;
            this.ocspResp = ocspResp;
            this.validationDate = validationDate;
        }
    }
}
