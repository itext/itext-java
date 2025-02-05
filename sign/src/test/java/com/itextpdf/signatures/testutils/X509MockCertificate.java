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
package com.itextpdf.signatures.testutils;

import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Mock implementation of X509Certificate. Setters have been provided for data that is being tested in tests.
 */
public class X509MockCertificate extends X509Certificate {

    private boolean hasUnsupportedCriticalExtension;
    private Set<String> criticalExtensionOIDs;
    private boolean[] keyUsage;
    private List<String> extendedKeyUsage;

    public X509MockCertificate() {
        this.criticalExtensionOIDs = new HashSet<>();
    }

    @Override
    public void checkValidity() {

    }

    @Override
    public void checkValidity(Date date) {

    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public BigInteger getSerialNumber() {
        return null;
    }

    @Override
    public Principal getIssuerDN() {
        return null;
    }

    @Override
    public Principal getSubjectDN() {
        return null;
    }

    @Override
    public Date getNotBefore() {
        return null;
    }

    @Override
    public Date getNotAfter() {
        return null;
    }

    @Override
    public byte[] getTBSCertificate() {
        return new byte[0];
    }

    @Override
    public byte[] getSignature() {
        return new byte[0];
    }

    @Override
    public String getSigAlgName() {
        return null;
    }

    @Override
    public String getSigAlgOID() {
        return null;
    }

    @Override
    public byte[] getSigAlgParams() {
        return new byte[0];
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return new boolean[0];
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return new boolean[0];
    }

    public X509MockCertificate setKeyUsage(boolean...keyUsage) {
        this.keyUsage = keyUsage;
        return this;
    }

    @Override
    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }

    @Override
    public int getBasicConstraints() {
        return 0;
    }

    @Override
    public byte[] getEncoded() {
        return new byte[0];
    }

    @Override
    public void verify(PublicKey key) {

    }

    @Override
    public void verify(PublicKey key, String sigProvider) {

    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    public X509MockCertificate setHasUnsupportedCriticalExtension(boolean hasUnsupportedCriticalExtension) {
        this.hasUnsupportedCriticalExtension = hasUnsupportedCriticalExtension;
        return this;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.hasUnsupportedCriticalExtension;
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return this.criticalExtensionOIDs;
    }

    public X509MockCertificate setCriticalExtensionOIDs(String...oids) {
        this.criticalExtensionOIDs = new HashSet<>(Arrays.asList(oids));;
        return this;
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return new byte[0];
    }

    public X509MockCertificate setExtendedKeyUsage(String...extendedKeyUsage) {
        this.extendedKeyUsage = Arrays.asList(extendedKeyUsage);
        return this;
    }

    @Override
    public List<String> getExtendedKeyUsage() {
        return this.extendedKeyUsage;
    }
}
