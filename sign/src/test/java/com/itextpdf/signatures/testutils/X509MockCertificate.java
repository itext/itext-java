/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures.testutils;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.*;

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
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {

    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {

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
    public byte[] getTBSCertificate() throws CertificateEncodingException {
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
    public byte[] getEncoded() throws CertificateEncodingException {
        return new byte[0];
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

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
    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        return this.extendedKeyUsage;
    }
}
