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

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MockIssuingCertificateRetriever extends IssuingCertificateRetriever {
    private final IssuingCertificateRetriever wrapped;
    public List<Certificate[]> retrieveMissingCertificatesCalls = new ArrayList<>();
    public List<CRL> getCrlIssuerCertificatesCalls = new ArrayList<>();
    public List<CRL> getCrlIssuerCertificatesByNameCalls = new ArrayList<>();

    public List<Certificate> retrieveIssuerCertificateCalls = new ArrayList<>();
    public List<IBasicOCSPResp> retrieveOCSPResponderCertificateCalls = new ArrayList<>();
    public List<Collection<Certificate>> setTrustedCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> addKnownCertificatesCalls = new ArrayList<>();
    public List<Collection<Certificate>> addTrustedCertificatesCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedDoCalls = new ArrayList<>();
    public List<String> getIssuerCertByURICalls = new ArrayList<>();

    public int getTrustedCertificatesStoreCallCount = 0;

    private Function<Certificate[], Certificate[]> retrieveMissingCertificatesHandler;
    private Function<CRL, Certificate[]> getCrlIssuerCertificatesHandler;
    private Function<CRL, Certificate[][]> getCrlIssuerCertificatesByNameHandler;
    private Function<Certificate, Certificate> retrieveIssuerCertificateHandler;
    private Function<IBasicOCSPResp, Set<Certificate>> retrieveOCSPResponderCertificateHandler;
    private Consumer<Collection<Certificate>> setTrustedCertificatesHandler;
    private Consumer<Collection<Certificate>> addKnownCertificatesHandler;

    private Consumer<Collection<Certificate>> addTrustedCertificatesHandler;
    private Function<Certificate, Boolean> isCertificateTrustedDoHandler;

    private Supplier<TrustedCertificatesStore> getTrustedCertificatesStoreHandler;
    private  Function<String, InputStream> getIssuerCertByURIHandler;

    public MockIssuingCertificateRetriever() {
        wrapped = null;
    }

    public MockIssuingCertificateRetriever(IssuingCertificateRetriever fallback) {
        wrapped = fallback;
    }

    @Override
    public Certificate[] retrieveMissingCertificates(Certificate[] chain) {
        retrieveMissingCertificatesCalls.add(chain);
        if (retrieveMissingCertificatesHandler != null) {
            return retrieveMissingCertificatesHandler.apply(chain);
        }
        if (wrapped != null) {
            return wrapped.retrieveMissingCertificates(chain);
        }
        return new Certificate[0];
    }

    @Override
    public Certificate[] getCrlIssuerCertificates(CRL crl) {
        getCrlIssuerCertificatesCalls.add(crl);
        if (getCrlIssuerCertificatesHandler != null) {
            return getCrlIssuerCertificatesHandler.apply(crl);
        }
        if (wrapped != null) {
            return wrapped.getCrlIssuerCertificates(crl);
        }
        return new Certificate[0];
    }

    @Override
    public Certificate[][] getCrlIssuerCertificatesByName(CRL crl) {
        getCrlIssuerCertificatesByNameCalls.add(crl);
        if (getCrlIssuerCertificatesByNameHandler != null) {
            return getCrlIssuerCertificatesByNameHandler.apply(crl);
        }
        if (wrapped != null) {
            return wrapped.getCrlIssuerCertificatesByName(crl);
        }
        return new Certificate[0][];
    }

    @Override
    public List<X509Certificate> retrieveIssuerCertificate(Certificate certificate) {
        retrieveIssuerCertificateCalls.add(certificate);
        if (retrieveIssuerCertificateHandler != null) {
            return Collections.singletonList((X509Certificate) retrieveIssuerCertificateHandler.apply(certificate));
        }
        if (wrapped != null) {
            return wrapped.retrieveIssuerCertificate(certificate);
        }
        return null;
    }

    @Override
    public Set<Certificate> retrieveOCSPResponderByNameCertificate(IBasicOCSPResp ocspResp) {
        retrieveOCSPResponderCertificateCalls.add(ocspResp);
        if (retrieveOCSPResponderCertificateHandler != null) {
            return retrieveOCSPResponderCertificateHandler.apply(ocspResp);
        }
        if (wrapped != null) {
            return wrapped.retrieveOCSPResponderByNameCertificate(ocspResp);
        }
        return null;
    }

    @Override
    public void setTrustedCertificates(Collection<Certificate> certificates) {
        setTrustedCertificatesCalls.add(certificates);
        if (setTrustedCertificatesHandler != null) {
            setTrustedCertificatesHandler.accept(certificates);
            return;
        }
        if (wrapped != null) {
            wrapped.setTrustedCertificates(certificates);
        }
    }

    @Override
    public void addKnownCertificates(Collection<Certificate> certificates) {
        addKnownCertificatesCalls.add(certificates);
        if (addKnownCertificatesHandler != null) {
            addKnownCertificatesHandler.accept(certificates);
            return;
        }
        if (wrapped != null) {
            wrapped.addKnownCertificates(certificates);
        }
    }

    @Override
    public void addTrustedCertificates(Collection<Certificate> certificates) {
        addTrustedCertificatesCalls.add(certificates);
        if (addTrustedCertificatesHandler != null) {
            addTrustedCertificatesHandler.accept(certificates);
            return;
        }
        if (wrapped != null) {
            wrapped.addTrustedCertificates(certificates);
        }
    }

    @Override
    public boolean isCertificateTrusted(Certificate certificate) {
        isCertificateTrustedDoCalls.add(certificate);
        if (isCertificateTrustedDoHandler != null) {
            return isCertificateTrustedDoHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateTrusted(certificate);
        }
        return true;
    }

    @Override
    public TrustedCertificatesStore getTrustedCertificatesStore() {
        getTrustedCertificatesStoreCallCount++;
        if (getTrustedCertificatesStoreHandler != null) {
            return getTrustedCertificatesStoreHandler.get();
        }
        if (wrapped != null) {
            return wrapped.getTrustedCertificatesStore();
        }
        return null;
    }

    @Override
    protected InputStream getIssuerCertByURI(String uri) throws IOException {
        getIssuerCertByURICalls.add(uri);
        if (getIssuerCertByURIHandler != null) {
            return getIssuerCertByURIHandler.apply(uri);
        }
        return null;
    }

    public MockIssuingCertificateRetriever onRetrieveMissingCertificatesDo(Function<Certificate[], Certificate[]> callback) {
        retrieveMissingCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever ongetCrlIssuerCertificatesDo(Function<CRL, Certificate[]> callback) {
        getCrlIssuerCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever ongetCrlIssuerCertificatesByNameDo(Function<CRL, Certificate[][]> callback) {
        getCrlIssuerCertificatesByNameHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onRetrieveIssuerCertificateDo(Function<Certificate, Certificate> callback) {
        retrieveIssuerCertificateHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onRetrieveOCSPResponderCertificateDo(
            Function<IBasicOCSPResp, Set<Certificate>> callback) {
        retrieveOCSPResponderCertificateHandler = callback;
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

    public MockIssuingCertificateRetriever onAddTrustedCertificatesDo(Consumer<Collection<Certificate>> callback) {
        addTrustedCertificatesHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onIsCertificateTrustedDo(Function<Certificate, Boolean> callback) {
        isCertificateTrustedDoHandler = callback;
        return this;
    }

    public MockIssuingCertificateRetriever onGetTrustedCertificatesStoreDo(
            Supplier<TrustedCertificatesStore> callBack) {
        getTrustedCertificatesStoreHandler = callBack;
        return this;
    }

    public MockIssuingCertificateRetriever onGetIssuerCertByURIHandlerDo(Function<String, InputStream> callBack) {
        getIssuerCertByURIHandler = callBack;
        return this;
    }

}
