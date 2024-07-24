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

import com.itextpdf.signatures.validation.TrustedCertificatesStore;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MockTrustedCertificatesStore extends TrustedCertificatesStore {


    private final TrustedCertificatesStore wrapped;
    public List<Certificate> isCertificateGenerallyTrustedCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedForOcspCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedForCrlCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedForTimestampCalls = new ArrayList<>();
    public List<Certificate> isCertificateTrustedForCACalls = new ArrayList<>();
    public List<String> getGenerallyTrustedCertificateCalls =  new ArrayList<>();
    public List<String> getCertificateTrustedForOcspCalls =  new ArrayList<>();
    public List<String> getCertificateTrustedForCrlCalls =  new ArrayList<>();
    public List<String> getCertificateTrustedForTimestampCalls =  new ArrayList<>();
    public List<String> getCertificateTrustedForCACalls =  new ArrayList<>();
    public List<String> getKnownCertificateCalls =  new ArrayList<>();
    public int getAllTrustedCertificatesCallCount = 0;

    private Function<Certificate, Boolean> isCertificateGenerallyTrustedHandler;
    private Function<Certificate, Boolean> isCertificateTrustedForOcspHandler;
    private Function<Certificate, Boolean> isCertificateTrustedForCrlHandler;
    private Function<Certificate, Boolean> isCertificateTrustedForTimestampHandler;
    private Function<Certificate, Boolean> isCertificateTrustedForCAHandler;
    private Function<String, Certificate> getGenerallyTrustedCertificateHandler;
    private Function<String, Certificate> getCertificateTrustedForOcspHandler;
    private Function<String, Certificate> getCertificateTrustedForCrlHandler;
    private Function<String, Certificate> getCertificateTrustedForTimestampHandler;
    private Function<String, Certificate> getCertificateTrustedForCAHandler;
    private Function<String, Certificate> getKnownCertificateHandler;
    private Supplier<Collection<Certificate>> getAllTrustedCertificatesHandler;



    public MockTrustedCertificatesStore() {
        this.wrapped = null;
    }
    public MockTrustedCertificatesStore(TrustedCertificatesStore wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isCertificateGenerallyTrusted(Certificate certificate) {
        isCertificateGenerallyTrustedCalls.add(certificate);
        if (isCertificateGenerallyTrustedHandler != null) {
            return isCertificateGenerallyTrustedHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateGenerallyTrusted(certificate);
        }
        return true;
    }

    @Override
    public boolean isCertificateTrustedForOcsp(Certificate certificate) {
        isCertificateTrustedForOcspCalls.add(certificate);
        if (isCertificateTrustedForOcspHandler != null) {
            return isCertificateTrustedForOcspHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateTrustedForOcsp(certificate);
        }
        return true;
    }

    @Override
    public boolean isCertificateTrustedForCrl(Certificate certificate) {
        isCertificateTrustedForCrlCalls.add(certificate);
        if (isCertificateTrustedForCrlHandler != null) {
            return isCertificateTrustedForCrlHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateTrustedForCrl(certificate);
        }
        return true;
    }
    @Override
    public boolean isCertificateTrustedForTimestamp(Certificate certificate) {
        isCertificateTrustedForTimestampCalls.add(certificate);
        if (isCertificateTrustedForTimestampHandler != null) {
            return isCertificateTrustedForTimestampHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateTrustedForTimestamp(certificate);
        }
        return true;
    }

    @Override
    public boolean isCertificateTrustedForCA(Certificate certificate) {
        isCertificateTrustedForCACalls.add(certificate);
        if (isCertificateTrustedForCAHandler != null) {
            return isCertificateTrustedForCAHandler.apply(certificate);
        }
        if (wrapped != null) {
            return wrapped.isCertificateTrustedForCA(certificate);
        }
        return true;
    }

    @Override
    public Certificate getGenerallyTrustedCertificate(String certificateName) {
        getGenerallyTrustedCertificateCalls.add(certificateName);
        if (getGenerallyTrustedCertificateHandler != null) {
            return getGenerallyTrustedCertificateHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getGenerallyTrustedCertificate(certificateName);
        }
        return null;
    }

    @Override
    public Certificate getCertificateTrustedForOcsp(String certificateName) {
        getCertificateTrustedForOcspCalls.add(certificateName);
        if (getCertificateTrustedForOcspHandler != null) {
            return getCertificateTrustedForOcspHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getCertificateTrustedForOcsp(certificateName);
        }
        return null;
    }

    @Override
    public Certificate getCertificateTrustedForCrl(String certificateName) {
        getCertificateTrustedForCrlCalls.add(certificateName);
        if (getCertificateTrustedForCrlHandler != null) {
            return getCertificateTrustedForCrlHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getCertificateTrustedForCrl(certificateName);
        }
        return null;
    }

    @Override
    public Certificate getCertificateTrustedForTimestamp(String certificateName) {
        getCertificateTrustedForTimestampCalls.add(certificateName);
        if (getCertificateTrustedForTimestampHandler != null) {
            return getCertificateTrustedForTimestampHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getCertificateTrustedForTimestamp(certificateName);
        }
        return null;
    }

    @Override
    public Certificate getCertificateTrustedForCA(String certificateName) {
        getCertificateTrustedForCACalls.add(certificateName);
        if (getCertificateTrustedForCAHandler != null) {
            return getCertificateTrustedForCAHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getCertificateTrustedForCA(certificateName);
        }
        return null;
    }

    @Override
    public Certificate getKnownCertificate(String certificateName) {
        getKnownCertificateCalls.add(certificateName);
        if (getKnownCertificateHandler != null) {
            return getKnownCertificateHandler.apply(certificateName);
        }
        if (wrapped != null) {
            return wrapped.getKnownCertificate(certificateName);
        }
        return null;
    }

    @Override
    public Collection<Certificate> getAllTrustedCertificates() {
        getAllTrustedCertificatesCallCount++;
        if (getAllTrustedCertificatesHandler != null) {
            return getAllTrustedCertificatesHandler.get();
        }
        if (wrapped != null) {
            return wrapped.getAllTrustedCertificates();
        }
        return null;
    }

    public MockTrustedCertificatesStore onIsCertificateGenerallyTrustedDo(Function<Certificate, Boolean> callBack) {
        isCertificateGenerallyTrustedHandler = callBack;
        return this;
    }


    public MockTrustedCertificatesStore onIsCertificateTrustedForOcspDo(Function<Certificate, Boolean> callBack) {
        isCertificateTrustedForOcspHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onIsCertificateTrustedForCrlDo(Function<Certificate, Boolean> callBack) {
        isCertificateTrustedForCrlHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onIsCertificateTrustedForTimestampDo(Function<Certificate, Boolean> callBack) {
        isCertificateTrustedForTimestampHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onIsCertificateTrustedForCADo(Function<Certificate, Boolean> callBack) {
        isCertificateTrustedForCAHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetGenerallyTrustedCertificateDo(Function<String, Certificate> callBack) {
        getGenerallyTrustedCertificateHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetCertificateTrustedForOcspDo(Function<String, Certificate> callBack) {
        getCertificateTrustedForOcspHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetCertificateTrustedForCrlDo(Function<String, Certificate> callBack) {
        getCertificateTrustedForCrlHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetCertificateTrustedForTimestampDo(Function<String, Certificate> callBack) {
        getCertificateTrustedForTimestampHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetCertificateTrustedForCADo(Function<String, Certificate> callBack) {
        getCertificateTrustedForCAHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetKnownCertificateDo(Function<String, Certificate> callBack) {
        getKnownCertificateHandler = callBack;
        return this;
    }
    public MockTrustedCertificatesStore onGetAllTrustedCertificatesDo(Supplier<Collection<Certificate>> callBack) {
        getAllTrustedCertificatesHandler = callBack;
        return this;
    }


}
