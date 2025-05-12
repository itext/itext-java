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
package com.itextpdf.signatures;

import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 12;
    }

    @Override
    public String getModuleName() {
        return "signatures";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.ignoreVarForNullableGenericsConversion("com.itextpdf.signatures.PdfSigner.preClose.entry");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PrivateKeySignature.PrivateKeySignature(" + "java.security.PrivateKey,java.lang.String,java.lang.String)", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PrivateKeySignature.PrivateKeySignature(" + "java.security.PrivateKey,java.lang.String,java.lang.String," + "java.lang.String,com.itextpdf.signatures.IApplicableSignatureParams)", "1, 2, 3, 5");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignUtils.getSignatureHelper", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignUtils.verifyCertificateSignature", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.CertificateVerification.verifyOcspCertificates", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.CertificateVerification.verifyTimestampCertificates", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignUtils.isSignatureValid", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignUtils.readAllCerts", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PdfPKCS7.PdfPKCS7(java.security.PrivateKey," + "java.security.cert.Certificate[],java.lang.String,java.lang.String,boolean)", "1, 2, 3, 5");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PdfPKCS7.PdfPKCS7(byte[],byte[],java.lang.String)", "1, 2");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignatureUtil.verifySignature(java.lang.String,java.lang.String)", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignatureUtil.readSignatureData(java.lang.String,java.lang.String)", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.testutils.PemFileHelper.initStore(java.lang.String,char[],java.security.Provider)", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.SignUtils.generateCertificate(java.io.InputStream,java.security.Provider)", "1");
        configurator.mapMethod("com.itextpdf.signatures.SignatureUtil.verifySignature(java.lang.String)", "VerifySignature");
        configurator.removeMethod("com.itextpdf.signatures.SignatureUtil.verifySignature(java.lang.String)", true);
        configurator.mapMethod("com.itextpdf.signatures.SignatureUtil.readSignatureData(java.lang.String)", "ReadSignatureData");
        configurator.removeMethod("com.itextpdf.signatures.SignatureUtil.readSignatureData(java.lang.String)", true);
        configurator.removeField("com.itextpdf.signatures.PrivateKeySignature.provider");
        configurator.removeField("com.itextpdf.signatures.PdfPKCS7.provider");
        configurator.removeField("com.itextpdf.signatures.LtvVerification.securityProviderCode");
        configurator.removeField("com.itextpdf.signatures.LtvVerifier.securityProviderCode");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.signatures.PdfSigner$ISignatureDataProvider");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.signatures.testutils.SignaturesCompareTool$SequenceComparator");

        configurator.mapType("com.itextpdf.signatures.BouncyCastleDigest", "Org.BouncyCastle.Security.DigestUtilities");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PdfPKCS7.PdfPKCS7(java.security.PrivateKey," + "java.security.cert.Certificate[],java.lang.String,java.lang.String,com.itextpdf.signatures.IExternalDigest,boolean)", "1, 2, 3, 6");

        configurator.mapType("com.itextpdf.signatures.BouncyCastleDigest", "iText.Signatures.BouncyCastleDigest");
        configurator.mapMethodParametersOrder("com.itextpdf.signatures.PdfPKCS7.PdfPKCS7(java.security.PrivateKey," +
                        "java.security.cert.Certificate[],java.lang.String,java.lang.String,com.itextpdf.signatures.IExternalDigest,boolean)",
                "1, 2, 3, 5, 6");

        configurator.removeField("com.itextpdf.signatures.verify.CertificateVerificationClassTest.PROVIDER");
        configurator.removeField("com.itextpdf.signatures.verify.LtvVerifierTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.verify.LtvVerifierTest.PROVIDER");
        configurator.removeField("com.itextpdf.signatures.verify.OcspCertificateVerificationTest.PROVIDER");
        configurator.removeField("com.itextpdf.signatures.LtvVerificationTest.BOUNCY_CASTLE_FACTORY");
        configurator.removeField("com.itextpdf.signatures.PdfSignatureAppearanceUnitTest.BOUNCY_CASTLE_FACTORY");
        configurator.removeField("com.itextpdf.signatures.PdfSignerUnitTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.SignatureUtilTest.BOUNCY_CASTLE_FACTORY");
        configurator.removeField("com.itextpdf.signatures.verify.pdfinsecurity.SignatureWrappingAttackTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.verify.pdfinsecurity.IncrementalSavingAttackTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.TimestampSigTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.TaggedPdfSigningTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.SimpleSigningTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.SignDeferredTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.SequentialSignaturesTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.PdfSignatureAppearanceTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.PdfASigningTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.PadesSignatureLevelTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.LtvWithTwoSignaturesTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.LtvSigTest.FACTORY");
        configurator.removeField("com.itextpdf.signatures.sign.AnnotationsSigningTest.FACTORY");
    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {

    }

    @Override
    public void applyConfigModuleSettings(ModulesConfigurator configurator) {

    }

    @Override
    public Collection<ModuleOption> getAvailableModuleSettings() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredSourceFiles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredResources() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<SimpleImmutableEntry<String, String>> getOverwrittenResources() {
        return Collections.EMPTY_LIST;
    }
}
