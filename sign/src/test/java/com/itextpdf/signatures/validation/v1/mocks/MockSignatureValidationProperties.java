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

import com.itextpdf.signatures.validation.v1.SignatureValidationProperties;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.extensions.CertificateExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This mock class wrapper a real SignatureValidationProperties instance.
 * It will track the calls made to it.
 * You can override a response by adding it with the add{someproperty}Response methods.
 * These will be served first, when there are no more responses left, the wrapped properties
 * will be returned.
 */
public class MockSignatureValidationProperties extends SignatureValidationProperties {

    private final SignatureValidationProperties wrappedProperties;
    public List<ValidationContext> continueAfterFailureCalls = new ArrayList<>();
    public List<ValidationContext> freshnessCalls = new ArrayList<>();
    public List<ValidationContext> requiredExtensionsCalls = new ArrayList<>();
    public List<ValidationContext> revocationOnlineFetchingCalls = new ArrayList<>();
    private final List<Boolean> continueAfterFailureResponses = new ArrayList<>();
    private int continueAfterFailureResponsesIndex = 0;

    private final List<Duration> freshnessResponses = new ArrayList<>();
    private int freshnessResponsesIndex = 0;

    private final List<List<CertificateExtension>> requiredExtensionsResponses = new ArrayList<>();
    private int requiredExtensionsResponsesIndex = 0;

    private final List<SignatureValidationProperties.OnlineFetching> revocationOnlineFetchingResponses = new ArrayList<>();
    private int revocationOnlineFetchingResponsesIndex = 0;


    public MockSignatureValidationProperties(SignatureValidationProperties properties) {
        this.wrappedProperties = properties;
    }

    public boolean getContinueAfterFailure(ValidationContext validationContext) {
        continueAfterFailureCalls.add(validationContext);
        if (continueAfterFailureResponsesIndex < continueAfterFailureResponses.size()) {
            return continueAfterFailureResponses.get(continueAfterFailureResponsesIndex++);
        }
        return wrappedProperties.getContinueAfterFailure(validationContext);
    }

    public Duration getFreshness(ValidationContext validationContext) {
        freshnessCalls.add(validationContext);
        if (freshnessResponsesIndex < freshnessResponses.size()) {
            return freshnessResponses.get(freshnessResponsesIndex++);
        }
        return wrappedProperties.getFreshness(validationContext);
    }

    public List<CertificateExtension> getRequiredExtensions(ValidationContext validationContext) {
        requiredExtensionsCalls.add(validationContext);
        if (requiredExtensionsResponsesIndex < requiredExtensionsResponses.size()) {
            return requiredExtensionsResponses.get(requiredExtensionsResponsesIndex++);
        }
        return wrappedProperties.getRequiredExtensions(validationContext);
    }

    public OnlineFetching getRevocationOnlineFetching(ValidationContext validationContext) {
        revocationOnlineFetchingCalls.add(validationContext);
        if (revocationOnlineFetchingResponsesIndex < revocationOnlineFetchingResponses.size()) {
            return revocationOnlineFetchingResponses.get(revocationOnlineFetchingResponsesIndex++);
        }
        return wrappedProperties.getRevocationOnlineFetching(validationContext);
    }

    public MockSignatureValidationProperties addContinueAfterFailureResponse(boolean value) {
        continueAfterFailureResponses.add(value);
        return this;
    }

    public MockSignatureValidationProperties addFreshnessResponse(Duration freshness) {
        freshnessResponses.add(freshness);
        return this;
    }

    public MockSignatureValidationProperties addRequiredExtensionsResponses(
            List<CertificateExtension> requiredExtensions) {
        requiredExtensionsResponses.add(requiredExtensions);
        return this;
    }

    public MockSignatureValidationProperties addRevocationOnlineFetchingResponse(OnlineFetching value) {
        revocationOnlineFetchingResponses.add(value);
        return this;
    }

}
