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

import com.itextpdf.signatures.IssuingCertificateRetriever;

import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MockIssuingCertificateRetriever extends IssuingCertificateRetriever {
    public List<Certificate[]> retrieveMissingCertificatesCalls = new ArrayList<>();
    public List<CRL> getCrlIssuerCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> setTrustedCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> addKnownCertificatesCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedDoCalls = new ArrayList<>();

    private Function<Certificate[], Certificate[]> retrieveMissingCertificatesHandler;
    private Function<CRL, Certificate[]> getCrlIssuerCertificatesHandler;
    private Consumer<Collection<Certificate>> setTrustedCertificatesHandler;
    private Consumer<Collection<Certificate>> addKnownCertificatesHandler;
    private Function<Certificate, Boolean> isCertificateTrustedDoHandler;

    @Override
    public Certificate[] retrieveMissingCertificates(Certificate[] chain) {
        retrieveMissingCertificatesCalls.add(chain);
        if (retrieveMissingCertificatesHandler != null) {
            return retrieveMissingCertificatesHandler.apply(chain);
        }
        return new Certificate[0];
    }

    @Override
    public Certificate[] getCrlIssuerCertificates(CRL crl) {
        getCrlIssuerCertificatesCalls.add(crl);
        if (getCrlIssuerCertificatesHandler != null) {
            return getCrlIssuerCertificatesHandler.apply(crl);
        }
        return new Certificate[0];
    }

    @Override
    public void setTrustedCertificates(Collection<Certificate> certificates) {
        setTrustedCertificatesCalls.add(certificates);
        if (setTrustedCertificatesHandler != null) {
            setTrustedCertificatesHandler.accept(certificates);
        }
    }

    @Override
    public void addKnownCertificates(Collection<Certificate> certificates) {
        addKnownCertificatesCalls.add(certificates);
        if (addKnownCertificatesHandler != null) {
            addKnownCertificatesHandler.accept(certificates);
        }
    }

    @Override
    public boolean isCertificateTrusted(Certificate certificate) {
        isCertificateTrustedDoCalls.add(certificate);
        if (isCertificateTrustedDoHandler != null) {
            return isCertificateTrustedDoHandler.apply(certificate);
        }
        return true;
    }

    public MockIssuingCertificateRetriever onRetrieveMissingCertificatesDo(Function<Certificate[], Certificate[]> callback) {
        retrieveMissingCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever ongetCrlIssuerCertificatesDo(Function<CRL, Certificate[]> callback) {
        getCrlIssuerCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onSetTrustedCertificatesDo(Consumer<Collection<Certificate>> callback) {
        setTrustedCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onAddKnownCertificatesDo(Consumer<Collection<Certificate>> callback) {
        addKnownCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onIsCertificateTrustedDo(Function<Certificate, Boolean> callback) {
        isCertificateTrustedDoHandler = callback;
        return this;
    }
}
