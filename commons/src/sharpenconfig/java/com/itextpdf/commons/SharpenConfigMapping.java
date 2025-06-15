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
package com.itextpdf.commons;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.MemberKind;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

public class SharpenConfigMapping implements MappingConfiguration {
    private boolean useBCWrappersConfig = false;
    @Override
    public int getMappingPriority() {
        return 15;
    }

    @Override
    public String getModuleName() {
        return "commons";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapNamespace("com.itextpdf", "iText");
        configurator.mapNamespace("licensekey", "License");
        configurator.mapNamespace("property", "Properties");
        configurator.mapNamespace("org.bouncycastle", "Org.BouncyCastle");
        configurator.mapNamespace("xpath", "XPath");
        configurator.mapMethod("org.junit.Assert.assertArrayEquals(double[],double[],double)", "iText.Test.TestUtil.AreEqual");
        configurator.mapMethod("org.junit.Assert.assertArrayEquals(float[],float[],float)", "iText.Test.TestUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertArrayEquals(double[],double[],double)", "iText.Test.TestUtil.AreEqual");
        configurator.mapMethod("org.junit.jupiter.api.Assertions.assertArrayEquals(float[],float[],float)", "iText.Test.TestUtil.AreEqual");
        mapStandardJavaToUtil(configurator);
        mapBouncyCastle(configurator);
        configurator.mapMethod("java.text.Normalizer.normalize", "iText.Commons.Utils.StringUtil.Normalize");
        configurator.mapType("java.util.regex.Matcher", "iText.Commons.Utils.Matcher");
        configurator.mapMethod("java.util.regex.Pattern.compile", "iText.Commons.Utils.StringUtil.RegexCompile");
        configurator.mapMethod("java.util.regex.Pattern.matcher", "iText.Commons.Utils.Matcher.Match");
        configurator.mapMethod("java.lang.String.replaceAll", "iText.Commons.Utils.StringUtil.ReplaceAll");
        configurator.mapMethod("java.lang.String.split", "iText.Commons.Utils.StringUtil.Split");
        configurator.mapMethod("java.util.regex.Pattern.split", "iText.Commons.Utils.StringUtil.Split");
        configurator.mapType("java.io.FileFilter", "iText.Commons.Utils.FileUtil.IFileFilter");
        configurator.mapType("java.util.EnumSet<>", "iText.Commons.Utils.Collections.EnumSet");
        configurator.addCustomUsingForMethodInvocation("java.util.Map.computeIfAbsent",  Collections.singletonList("iText.Commons.Utils.Collections"));
        configurator.addCustomUsingForMethodInvocation("java.util.Map.getOrDefault",  Collections.singletonList("iText.Commons.Utils.Collections"));
        configurator.mapMethod("java.lang.Integer.toHexString", "iText.Commons.Utils.JavaUtil.IntegerToHexString", false);
        configurator.mapMethod("java.lang.Integer.toOctalString", "iText.Commons.Utils.JavaUtil.IntegerToOctalString", false);
        configurator.mapMethod("java.lang.Integer.toString", "iText.Commons.Utils.JavaUtil.IntegerToString", false);
        configurator.mapMethod("java.lang.Integer.compare", "iText.Commons.Utils.JavaUtil.IntegerCompare", false);
        configurator.mapMethod("java.lang.Float.compare", "iText.Commons.Utils.JavaUtil.FloatCompare", false);
        configurator.mapMethod("java.lang.Double.compare", "iText.Commons.Utils.JavaUtil.DoubleCompare", false);
        configurator.mapType("java.util.Properties", "iText.Commons.Utils.Properties");
        configurator.mapType("java.util.LinkedHashMap<,>", "iText.Commons.Utils.LinkedDictionary");
        configurator.mapType("java.util.LinkedHashSet<>", "iText.Commons.Utils.LinkedHashSet");
        configurator.mapMethod("java.lang.Math.round", "iText.Commons.Utils.MathematicUtil.Round", false);
        configurator.mapMethod("java.lang.Math.random", "iText.Commons.Utils.JavaUtil.Random", false);
        configurator.mapMethod("java.util.Arrays.copyOf", "iText.Commons.Utils.JavaUtil.ArraysCopyOf", false);
        configurator.mapMethod("java.util.Arrays.copyOfRange", "iText.Commons.Utils.JavaUtil.ArraysCopyOfRange", false);
        configurator.mapMethod("java.lang.Character.digit", "iText.Commons.Utils.JavaUtil.CharacterDigit", false);
        configurator.mapMethod("java.lang.Character.UnicodeScript.of", "iText.Commons.Utils.UnicodeScriptUtil.Of", false);
        configurator.mapType("java.lang.Character.UnicodeScript", "iText.Commons.Utils.UnicodeScript?");
        configurator.mapType("java.util.concurrent.atomic.AtomicLong", "iText.Commons.Utils.AtomicLong");
        configurator.mapType("java.lang.FunctionalInterface", "iText.Commons.Utils.FunctionalInterfaceAttribute");
        configurator.mapMethod("java.util.Arrays.stream", "iText.Commons.Utils.JavaUtil.ArraysToEnumerable");
        configurator.mapType("java.util.IdentityHashMap<,>", "iText.Commons.Utils.IdentityDictionary");
        configurator.mapType("java.security.cert.X509CRLEntry", "iText.Commons.Bouncycastle.Cert.IX509CrlEntry");
        configurator.mapType("java.io.PrintStream", "iText.Commons.Utils.FormattingStreamWriter");
        configurator.mapType("java.io.PrintWriter", "iText.Commons.Utils.FormattingStreamWriter");
        configurator.mapMethod("java.nio.charset.Charset.forName", "iText.Commons.Utils.EncodingUtil.GetEncoding", false);
        configurator.mapField("java.nio.charset.StandardCharsets.ISO_8859_1", "iText.Commons.Utils.EncodingUtil.ISO_8859_1");
        configurator.mapMethod("java.lang.String.valueOf(char[])", "iText.Commons.Utils.JavaUtil.GetStringForChars", false);
        configurator.mapMethod("java.lang.String.valueOf(char[],int,int)", "iText.Commons.Utils.JavaUtil.GetStringForChars", false);
        configurator.mapMethod("java.lang.String.String(byte[])", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int,java.lang.String)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],java.lang.String)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],int,int,java.nio.charset.Charset)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.String.String(byte[],java.nio.charset.Charset)", "iText.Commons.Utils.JavaUtil.getStringForBytes", false);
        configurator.mapMethod("java.lang.Float.intBitsToFloat(int)", "iText.Commons.Utils.JavaUtil.IntBitsToFloat", false);
        configurator.mapMethod("java.lang.Float.floatToIntBits(float)", "iText.Commons.Utils.JavaUtil.FloatToIntBits", false);
        configurator.mapMethod("java.lang.Double.longBitsToDouble(long)", "iText.Commons.Utils.JavaUtil.LongBitsToDouble", false);
        configurator.mapMethod("java.lang.Double.doubleToLongBits(double)", "iText.Commons.Utils.JavaUtil.DoubleToLongBits", false);
        configurator.mapType("java.util.StringTokenizer", "iText.Commons.Utils.StringTokenizer");
        configurator.mapMethod("java.util.Arrays.equals", "iText.Commons.Utils.JavaUtil.ArraysEquals", false);
        configurator.mapMethod("java.util.Objects.hash", "iText.Commons.Utils.JavaUtil.ArraysHashCode", false);
        configurator.mapMethod("java.util.Arrays.hashCode", "iText.Commons.Utils.JavaUtil.ArraysHashCode", false);
        configurator.mapMethod("java.util.Arrays.toString", "iText.Commons.Utils.JavaUtil.ArraysToString", false);
        configurator.mapMethod("java.util.Arrays.asList", "iText.Commons.Utils.JavaUtil.ArraysAsList", false);
        configurator.mapMethod("java.util.Arrays.<>asList", "iText.Commons.Utils.JavaUtil.ArraysAsList", false);
        configurator.mapMethod("java.util.Arrays.binarySearch", "iText.Commons.Utils.JavaUtil.ArraysBinarySearch", false);
        configurator.mapMethod("java.util.Arrays.fill", "iText.Commons.Utils.JavaUtil.Fill", false);
        configurator.mapMethod("java.util.Arrays.sort", "iText.Commons.Utils.JavaUtil.Sort", false);
        configurator.mapMethod("java.lang.Character.isValidCodePoint", "iText.Commons.Utils.JavaUtil.IsValidCodePoint", false);
        configurator.mapMethod("java.lang.Character.toCodePoint", "iText.Commons.Utils.JavaUtil.ToCodePoint", false);
        configurator.mapType("java.util.Collections", "iText.Commons.Utils.JavaCollectionsUtil");
        configurator.mapMethod("java.util.Collections.sort", "iText.Commons.Utils.JavaCollectionsUtil.Sort", false);
        configurator.mapMethod("java.util.AbstractMap.equals", "iText.Commons.Utils.JavaUtil.DictionariesEquals", false);
        configurator.mapMethod("java.util.AbstractMap.hashCode", "iText.Commons.Utils.JavaUtil.DictionaryHashCode", false);
        configurator.mapMethod("java.util.AbstractSet.equals", "iText.Commons.Utils.JavaUtil.SetEquals", false);
        configurator.mapMethod("java.util.AbstractSet.hashCode", "iText.Commons.Utils.JavaUtil.SetHashCode", false);
        configurator.mapMethodParametersOrder("com.itextpdf.commons.utils.JsonUtil.deserializeFromString", "1");
        configurator.mapMethodParametersOrder("com.itextpdf.commons.utils.JsonUtil.deserializeFromStream", "1");
        configurator.mapMethod("com.itextpdf.commons.utils.EncodingUtil.fromBase64", "System.Convert.FromBase64String", false);
        configurator.mapMethod("com.itextpdf.commons.utils.EncodingUtil.toBase64", "System.Convert.ToBase64String", false);
        configurator.mapType("com.itextpdf.commons.utils.ThrowingSupplier<>", "System.Func");
        configurator.mapType("com.itextpdf.commons.utils.Action", "System.Action");
        configurator.mapType("com.itextpdf.commons.utils.ThrowingAction", "System.Action");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.Action");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.ThrowingAction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.IOThrowingAction");
        configurator.mapFunctionalInterfaceToDelegate("com.itextpdf.commons.utils.ThrowingSupplier");
        configurator.keepInternalProtected("com.itextpdf.commons.ecosystem.TestConfigurationEvent.doAction");
        configurator.addCustomUsingDeclaration("com.itextpdf.kernel.colors.DeviceRgb", Arrays.asList("iText.Commons.Utils"));
        configurator.mapMethod("com.itextpdf.commons.utils.SystemUtil.getPropertyOrEnvironmentVariable", "iText.Commons.Utils.SystemUtil.GetEnvironmentVariable", false);
        configurator.mapMethod("com.itextpdf.io.util.TextUtil.charToString", "iText.Commons.Utils.JavaUtil.CharToString", false);
        configurator.removeField("com.itextpdf.commons.actions.contexts.ContextManager.SECURITY_ERROR_LOGGING_INTERVAL");
        configurator.removeField("com.itextpdf.commons.actions.contexts.ContextManager.securityErrorLastLogged");
        configurator.mapType("org.slf4j.LoggerFactory", "iText.Commons.ITextLogManager");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.getCurrentTimeCalendar", "GetCurrentTime");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addDaysToCalendar", "AddDays");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addDaysToDate", "AddDays");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.addYearsToDate", "AddYears");
        configurator.mapMethod("com.itextpdf.commons.utils.DateTimeUtil.getCurrentTimeDate", "GetCurrentUtcTime");
        

        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate", "iText.Commons.Bouncycastle.Asn1.X509.ITbsCertificateStructure");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream", "iText.Commons.Bouncycastle.Asn1.IDerOutputStream");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509CRLHolder", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams", "iText.Commons.Bouncycastle.Asn1.Pkcs.IRsassaPssParameters");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.asn1.IASN1OutputStream.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createBasicOCSPResp", "CreateBasicOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPResp", "CreateOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPRespBuilderInstance", "CreateOCSPResponseStatus");
        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createJcaX509CertificateHolder");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream", "iText.Commons.Bouncycastle.Asn1.IAsn1InputStream");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.AbstractCMSException", "iText.Commons.Bouncycastle.Cms.AbstractCmsException");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException", "iText.Commons.Bouncycastle.Cert.Ocsp.AbstractOcspException");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509v3CertificateBuilder", "iText.Commons.Bouncycastle.Cert.IX509V3CertificateGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilderBC", "iText.Commons.Bouncycastle.Cert.X509V3CertificateGeneratorBC");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.IX509v2CRLBuilder", "iText.Commons.Bouncycastle.Cert.IX509V2CrlGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPRespBuilder", "iText.Commons.Bouncycastle.Cert.Ocsp.IBasicOcspRespGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq", "iText.Commons.Bouncycastle.Cert.Ocsp.IOcspRequest");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReqBuilder", "iText.Commons.Bouncycastle.Cert.Ocsp.IOcspReqGenerator");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive", "iText.Commons.Bouncycastle.Asn1.IAsn1Object");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1String", "iText.Commons.Bouncycastle.Asn1.IDerStringBase");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID", "iText.Commons.Bouncycastle.Cert.Ocsp.ICertID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.ICertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.IUnknownCertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus", "iText.Commons.Bouncycastle.Cert.Ocsp.IRevokedCertStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Encoding", "iText.Commons.Bouncycastle.Asn1.IAsn1Encoding");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1UTCTime", "iText.Commons.Bouncycastle.Asn1.IDerUtcTime");
        configurator.mapType("com.itextpdf.commons.bouncycastle.openssl.IPEMParser", "iText.Commons.Bouncycastle.Openssl.IPemReader");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1BitString", "iText.Commons.Bouncycastle.Asn1.IDerBitString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated", "iText.Commons.Bouncycastle.Asn1.IDerEnumerated");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1GeneralizedTime", "iText.Commons.Bouncycastle.Asn1.IDerGeneralizedTime");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier", "iText.Commons.Bouncycastle.Asn1.IDerObjectIdentifier");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp", "iText.Commons.Bouncycastle.Cert.Ocsp.ISingleResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse", "iText.Commons.Bouncycastle.Asn1.Ocsp.IBasicOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPObjectIdentifiers", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspObjectIdentifiers");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus", "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponseStatus");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IPKCSObjectIdentifiers", "iText.Commons.Bouncycastle.Asn1.Pkcs.IPkcsObjectIdentifiers");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable", "iText.Commons.Bouncycastle.Asn1.IAsn1Encodable");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector", "iText.Commons.Bouncycastle.Asn1.IAsn1EncodableVector");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence", "iText.Commons.Bouncycastle.Asn1.IAsn1Sequence");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Set", "iText.Commons.Bouncycastle.Asn1.IAsn1Set");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString", "iText.Commons.Bouncycastle.Asn1.IAsn1OctetString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject", "iText.Commons.Bouncycastle.Asn1.IAsn1TaggedObject");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.cmp.IPKIFailureInfo", "iText.Commons.Bouncycastle.Asn1.Cmp.IPkiFailureInfo");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERIA5String", "iText.Commons.Bouncycastle.Asn1.IDerIA5String");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERNull", "iText.Commons.Bouncycastle.Asn1.IDerNull");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDEROctetString", "iText.Commons.Bouncycastle.Asn1.IDerOctetString");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERSequence", "iText.Commons.Bouncycastle.Asn1.IDerSequence");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERSet", "iText.Commons.Bouncycastle.Asn1.IDerSet");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IDERTaggedObject", "iText.Commons.Bouncycastle.Asn1.IDerTaggedObject");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID", "iText.Commons.Bouncycastle.Asn1.Ess.IEssCertID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2", "iText.Commons.Bouncycastle.Asn1.Ess.IEssCertIDv2");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.tsp.ITSTInfo", "iText.Commons.Bouncycastle.Asn1.Tsp.ITstInfo");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump", "iText.Commons.Bouncycastle.Asn1.Util.IAsn1Dump");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.IASN1Integer", "iText.Commons.Bouncycastle.Asn1.IDerInteger");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions", "iText.Commons.Bouncycastle.Asn1.X509.IX509Extensions");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ICRLDistPoint", "iText.Commons.Bouncycastle.Asn1.X509.ICrlDistPoint");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.ICRLReason", "iText.Commons.Bouncycastle.Asn1.X509.ICrlReason");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.ICMSEnvelopedData", "iText.Commons.Bouncycastle.Cms.ICmsEnvelopedData");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.x509.IKeyPurposeId", "iText.Commons.Bouncycastle.Asn1.X509.IKeyPurposeID");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cms.IRecipientId", "iText.Commons.Bouncycastle.Cms.IRecipientID");


        configurator.mapMethod("com.itextpdf.commons.utils.ZipFileReader.close", "Dispose");
        configurator.mapMethod("com.itextpdf.commons.utils.ZipFileWriter.close", "Dispose");
        configurator.mapMethod("com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation.correctWavFile", "iText.Commons.Utils.JavaUtil.CorrectWavFile", false);
        configurator.addCustomUsingDeclaration("com.itextpdf.layout.renderer.TypographyUtils", Arrays.asList("System.IO", "System.Reflection", "Versions.Attributes", "Microsoft.Extensions.Logging", "iText.Commons"));
        configurator.mapType("org.junit.jupiter.api.condition.DisabledInNativeImage", "iText.Commons.Utils.NoopAnnotation");
        configurator.addFullName("iText.Commons.Utils.NoopAnnotation");

        configurator.mapType("java.security.cert.Certificate", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("java.security.cert.X509Certificate", "iText.Commons.Bouncycastle.Cert.IX509Certificate");
        configurator.mapType("java.security.InvalidAlgorithmParameterException", "System.Exception");
        configurator.mapType("java.security.cert.CRL", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("java.security.cert.CRLException", "iText.Commons.Bouncycastle.Security.AbstractCrlException");
        configurator.mapType("java.security.cert.X509CRL", "iText.Commons.Bouncycastle.Cert.IX509Crl");
        configurator.mapType("java.security.cert.CRLReason", "iText.Commons.Bouncycastle.Cert.CRLReason");
        configurator.mapType("java.security.PrivateKey", "iText.Commons.Bouncycastle.Crypto.IPrivateKey");
        configurator.mapType("java.security.Signature", "iText.Commons.Bouncycastle.Crypto.ISigner");
        configurator.mapType("java.security.PublicKey", "iText.Commons.Bouncycastle.Crypto.IPublicKey");
        configurator.mapType("javax.security.auth.x500.X500Principal", "iText.Commons.Bouncycastle.Asn1.X500.IX500Name");
        configurator.mapType("java.security.Key", "iText.Commons.Bouncycastle.Crypto.IPrivateKey");
        configurator.mapType("java.security.KeyStore", "System.Collections.Generic.List<IX509Certificate>");
        configurator.mapType("java.security.GeneralSecurityException",
                "iText.Commons.Bouncycastle.Security.AbstractGeneralSecurityException");
        configurator.mapType("java.security.SignatureException",
                "iText.Commons.Bouncycastle.Security.AbstractGeneralSecurityException");
        configurator.mapType("java.security.cert.CertificateExpiredException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateExpiredException");
        configurator.mapType("java.security.cert.CertificateNotYetValidException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateNotYetValidException");
        configurator.mapType("java.security.cert.CertificateParsingException",
                "iText.Commons.Bouncycastle.Security.AbstractCertificateParsingException");
        configurator.mapType("java.security.KeyPairGenerator", "iText.Commons.Bouncycastle.Crypto.Generators.IRsaKeyPairGenerator");
        configurator.mapType("java.security.KeyPair", "iText.Commons.Bouncycastle.Crypto.IAsymmetricCipherKeyPair");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp",
                "iText.Commons.Bouncycastle.Asn1.Ocsp.IBasicOcspResponse");
        configurator.mapType("com.itextpdf.bouncycastle.cert.ocsp.BasicOCSPRespBC",
                "iText.Bouncycastle.Asn1.Ocsp.BasicOCSPResponseBC");
        configurator.mapType("com.itextpdf.bouncycastlefips.cert.ocsp.BasicOCSPRespBCFips",
                "iText.Bouncycastlefips.Asn1.Ocsp.BasicOCSPResponseBCFips");
        configurator.mapType("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp",
                "iText.Commons.Bouncycastle.Asn1.Ocsp.IOcspResponse");
        configurator.mapType("com.itextpdf.commons.bouncycastle.asn1.pkcs.IRSASSAPSSParams",
                "iText.Commons.Bouncycastle.Asn1.Pkcs.IRsassaPssParameters");

        configurator.mapMethod("java.math.BigInteger.intValue", "GetIntValue");
        configurator.mapMethod("java.security.cert.X509Certificate.getIssuerX500Principal", "GetIssuerDN");
        configurator.mapMethod("java.security.cert.X509Certificate.getSubjectX500Principal", "GetSubjectDN");
        configurator.mapMethod("java.security.cert.X509CRL.getIssuerX500Principal", "GetIssuerDN");
        configurator.mapMethod("java.security.cert.X509Extension.getCriticalExtensionOIDs", "GetCriticalExtensionOids");
        configurator.mapMethod("java.security.MessageDigest.getAlgorithm", "GetAlgorithmName");

        configurator.mapMethod("java.security.KeyPair.getPrivate", "GetPrivateKey");
        configurator.mapMethod("java.security.KeyPair.getPublic", "GetPublicKey");

        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPRespBuilder", "");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPRespBuilder.build", "CreateOCSPResponse");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp.getCerts", "GetOcspCerts");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createExtension()", "CreateExtensions");
        configurator.mapMethod("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createX509CertificateHolder", "CreateX509Certificate");

        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createBasicOCSPResp"
                + "(com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse)");
        configurator.unwrapInvocation("com.itextpdf.commons.bouncycastle.IBouncyCastleFactory.createOCSPResp"
                + "(com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponse)");

        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException(java.lang.String,java.lang.Throwable)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException(java.lang.String)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain(
                "java.security.GeneralSecurityException.GeneralSecurityException()",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateGeneralSecurityException",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain("java.math.BigInteger.valueOf(long)",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateBigInteger().ValueOf",
                MemberKind.Method);
        configurator.mapMemberToInvocationsChain("java.math.BigInteger.BigInteger(int,byte[])",
                "iText.Bouncycastleconnector.BouncyCastleFactoryCreator.GetFactory().CreateBigInteger",
                MemberKind.Method);

        configurator.removeMethod("java.security.Security.addProvider");
        configurator.mapStringLiteral("com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT", "Either itext7.bouncy-castle-adapter or itext7.bouncy-castle-fips-adapter dependency must be added in order to use BouncyCastleFactoryCreator");

        configurator.mapMemberToInvocationsChain("org.bouncycastle.asn1.esf.SigPolicyQualifiers.SigPolicyQualifiers" + "(org.bouncycastle.asn1.esf.SigPolicyQualifierInfo[])", "", MemberKind.Method);
        if (useBCWrappersConfig) {
            BCWrappersConfigurationUtils.applyMappingConfiguration(configurator);
        }
    }

    private void mapStandardJavaToUtil(MappingConfigurator configurator) {
        configurator.mapType("java.lang.System", "iText");
        configurator.mapMethod("java.lang.Class.getDeclaredField", "iText.GetDeclaredField");
        configurator.mapMethod("java.lang.Class.getDeclaredMethod", "iText.GetDeclaredMethod");
        configurator.mapMethod("java.lang.Class.getDeclaredMethods", "iText.GetDeclaredMethods");
        configurator.mapMethod("java.lang.reflect.Array.getLength", "iText.GetArrayLength");
        configurator.mapMethod("java.lang.reflect.Method.getParameterTypes", "iText.GetParameterTypes");
        configurator.mapMethod("java.lang.reflect.Array.get", "iText.GetArrayValue");
        configurator.mapMethod("java.lang.reflect.Array.set", "iText.SetArrayValue");
        configurator.mapMethod("java.lang.Object.wait", "iText.wait");
        configurator.mapMethod("java.lang.Object.notify", "iText.notify");
        configurator.mapMethod("java.lang.Object.notifyAll", "iText.notifyAll");
        configurator.mapMethod("java.lang.System.identityHashCode", "iText.IdentityHashCode");
        configurator.mapMethod("java.lang.Throwable.printStackTrace", "iText.printStackTrace");
        String[] streamImplementations = new String[]{"java.util.stream.Stream", "java.util.stream.IntStream", "java.util.stream.DoubleStream", "java.util.stream.LongStream"};
        for (String name : streamImplementations) {
            configurator.addCustomUsingForMethodInvocation(name + ".sorted", Collections.singletonList("iText.Commons.Utils.Collections"));
        }
    }

    private void mapBouncyCastle(MappingConfigurator configurator) {
        configurator.mapType("java.security.NoSuchAlgorithmException", "Org.BouncyCastle.Security.SecurityUtilityException");
        configurator.mapType("java.security.NoSuchProviderException", "Org.BouncyCastle.Security.NoSuchProviderException");
        configurator.mapType("java.security.SignatureException", "Org.BouncyCastle.Security.SignatureException");
        configurator.mapType("java.security.InvalidKeyException", "Org.BouncyCastle.Security.InvalidKeyException");
        configurator.mapType("java.security.cert.CertificateEncodingException", "Org.BouncyCastle.Security.Certificates.CertificateEncodingException");
        configurator.mapType("java.security.cert.CertificateParsingException", "Org.BouncyCastle.Security.Certificates.CertificateParsingException");
        configurator.mapType("java.math.BigInteger", "Org.BouncyCastle.Math.BigInteger");
        configurator.mapType("java.security.PrivateKey", "Org.BouncyCastle.Crypto.ICipherParameters");
        configurator.mapType("java.security.cert.Certificate", "Org.BouncyCastle.X509.X509Certificate");
        configurator.mapType("java.security.cert.X509Certificate", "Org.BouncyCastle.X509.X509Certificate");
        configurator.mapType("java.security.cert.CRL", "Org.BouncyCastle.X509.X509Crl");
        configurator.mapType("java.security.cert.X509CRL", "Org.BouncyCastle.X509.X509Crl");
        configurator.mapType("java.security.Signature", "Org.BouncyCastle.Crypto.ISigner");
        configurator.mapType("java.security.PublicKey", "Org.BouncyCastle.Crypto.AsymmetricKeyParameter");
        configurator.mapType("java.security.Key", "Org.BouncyCastle.Crypto.ICipherParameters");
        configurator.mapType("java.security.KeyStore", "System.Collections.Generic.List<X509Certificate>");
        configurator.mapType("java.security.GeneralSecurityException", "Org.BouncyCastle.Security.GeneralSecurityException");
        configurator.mapType("java.security.InvalidAlgorithmParameterException", "System.Exception");
        configurator.mapType("java.security.KeyPairGenerator", "Org.BouncyCastle.Crypto.Generators.RsaKeyPairGenerator");
        configurator.mapType("java.security.KeyPair", "Org.BouncyCastle.Crypto.AsymmetricCipherKeyPair");
        configurator.mapType("java.security.cert.CertificateExpiredException", "Org.BouncyCastle.Security.Certificates.CertificateExpiredException");
        configurator.mapType("java.security.cert.CertificateNotYetValidException", "Org.BouncyCastle.Security.Certificates.CertificateNotYetValidException");
        configurator.mapType("java.security.cert.CertificateParsingException", "Org.BouncyCastle.Security.Certificates.CertificateParsingException");

        configurator.mapType("org.bouncycastle.crypto.BlockCipher", "Org.BouncyCastle.Crypto.IBlockCipher");
        configurator.mapType("org.bouncycastle.crypto.engines.AESFastEngine", "Org.BouncyCastle.Crypto.Engines.AesFastEngine");
        configurator.mapType("org.bouncycastle.crypto.params.KeyParameter", "Org.BouncyCastle.Crypto.Parameters.KeyParameter");
        configurator.mapType("org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher", "Org.BouncyCastle.Crypto.Paddings.PaddedBufferedBlockCipher");
        configurator.mapType("org.bouncycastle.crypto.modes.CBCBlockCipher", "Org.BouncyCastle.Crypto.Modes.CbcBlockCipher");
        configurator.mapType("org.bouncycastle.crypto.modes.GCMBlockCipher", "Org.BouncyCastle.Crypto.Modes.GcmBlockCipher");
        configurator.mapType("org.bouncycastle.crypto.params.ParametersWithIV", "Org.BouncyCastle.Crypto.Parameters.ParametersWithIV");
        configurator.mapType("org.bouncycastle.crypto.params.AEADParameters", "Org.BouncyCastle.Crypto.Parameters.AeadParameters");
        configurator.mapType("org.bouncycastle.asn1.DEROctetString", "Org.BouncyCastle.Asn1.DerOctetString");
        configurator.mapType("org.bouncycastle.asn1.ASN1Encoding", "Org.BouncyCastle.Asn1.Asn1Encodable");
        configurator.mapType("org.bouncycastle.asn1.DERSet", "Org.BouncyCastle.Asn1.DerSet");
        configurator.mapType("org.bouncycastle.asn1.ASN1Set", "Org.BouncyCastle.Asn1.Asn1Set");
        configurator.mapType("org.bouncycastle.asn1.ASN1Primitive", "Org.BouncyCastle.Asn1.Asn1Object");
        configurator.mapType("org.bouncycastle.asn1.ASN1Encodable", "Org.BouncyCastle.Asn1.Asn1Encodable");
        configurator.mapType("org.bouncycastle.asn1.ASN1InputStream", "Org.BouncyCastle.Asn1.Asn1InputStream");
        configurator.mapType("org.bouncycastle.asn1.x509.CRLReason", "Org.BouncyCastle.Asn1.X509.CrlReason");
        configurator.mapType("org.bouncycastle.asn1.x509.KeyPurposeId", "Org.BouncyCastle.Asn1.X509.KeyPurposeID");
        configurator.mapType("org.bouncycastle.asn1.x509.TBSCertificate", "Org.BouncyCastle.Asn1.X509.TbsCertificateStructure");
        configurator.mapType("org.bouncycastle.asn1.x509.AlgorithmIdentifier", "Org.BouncyCastle.Asn1.X509.AlgorithmIdentifier");
        configurator.mapType("org.bouncycastle.asn1.cms.ContentInfo", "Org.BouncyCastle.Asn1.Cms.ContentInfo");
        configurator.mapType("org.bouncycastle.asn1.cms.IssuerAndSerialNumber", "Org.BouncyCastle.Asn1.Cms.IssuerAndSerialNumber");
        configurator.mapType("org.bouncycastle.asn1.cms.RecipientIdentifier", "Org.BouncyCastle.Asn1.Cms.RecipientIdentifier");
        configurator.mapType("org.bouncycastle.asn1.cms.KeyTransRecipientInfo", "Org.BouncyCastle.Asn1.Cms.KeyTransRecipientInfo");
        configurator.mapType("org.bouncycastle.asn1.cms.RecipientInfo", "Org.BouncyCastle.Asn1.Cms.RecipientInfo");
        configurator.mapType("org.bouncycastle.asn1.cms.EnvelopedData", "Org.BouncyCastle.Asn1.Cms.EnvelopedData");
        configurator.mapType("org.bouncycastle.asn1.cms.EncryptedContentInfo", "Org.BouncyCastle.Asn1.Cms.EncryptedContentInfo");
        configurator.mapType("org.bouncycastle.asn1.cmp.PKIFailureInfo", "Org.BouncyCastle.Asn1.Cmp.PkiFailureInfo");
        configurator.mapType("org.bouncycastle.asn1.ASN1ObjectIdentifier", "Org.BouncyCastle.Asn1.DerObjectIdentifier");
        configurator.mapType("org.bouncycastle.asn1.ASN1Sequence", "Org.BouncyCastle.Asn1.Asn1Sequence");
        configurator.mapType("org.bouncycastle.asn1.ASN1String", "Org.BouncyCastle.Asn1.DerStringBase");
        configurator.mapType("org.bouncycastle.asn1.ASN1TaggedObject", "Org.BouncyCastle.Asn1.Asn1TaggedObject");
        configurator.mapType("org.bouncycastle.asn1.DEROctetString", "Org.BouncyCastle.Asn1.DerOctetString");
        configurator.mapType("org.bouncycastle.asn1.ASN1EncodableVector", "Org.BouncyCastle.Asn1.Asn1EncodableVector");
        configurator.mapType("org.bouncycastle.asn1.DERIA5String", "Org.BouncyCastle.Asn1.DerIA5String");
        configurator.mapType("org.bouncycastle.asn1.ASN1OctetString", "Org.BouncyCastle.Asn1.Asn1OctetString");
        configurator.mapType("org.bouncycastle.asn1.ASN1BitString", "Org.BouncyCastle.Asn1.DerBitString");
        configurator.mapType("org.bouncycastle.asn1.ASN1GeneralizedTime", "Org.BouncyCastle.Asn1.DerGeneralizedTime");
        configurator.mapType("org.bouncycastle.asn1.ASN1UTCTime", "Org.BouncyCastle.Asn1.DerUtcTime");
        configurator.mapType("org.bouncycastle.asn1.ASN1Integer", "Org.BouncyCastle.Asn1.DerInteger");
        configurator.mapType("org.bouncycastle.asn1.ASN1OutputStream", "Org.BouncyCastle.Asn1.Asn1OutputStream");
        configurator.mapType("org.bouncycastle.asn1.DERNull", "Org.BouncyCastle.Asn1.DerNull");
        configurator.mapType("org.bouncycastle.asn1.ASN1Enumerated", " Org.BouncyCastle.Asn1.DerEnumerated");
        configurator.mapType("org.bouncycastle.asn1.DERTaggedObject", " Org.BouncyCastle.Asn1.DerTaggedObject");
        configurator.mapType("org.bouncycastle.asn1.DERSequence", " Org.BouncyCastle.Asn1.DerSequence");
        configurator.mapType("org.bouncycastle.asn1.x509.Extension", "Org.BouncyCastle.Asn1.X509.X509Extensions");
        configurator.mapType("org.bouncycastle.asn1.x509.CRLDistPoint", "Org.BouncyCastle.Asn1.X509.CrlDistPoint");
        configurator.mapType("org.bouncycastle.asn1.x509.DistributionPoint", "Org.BouncyCastle.Asn1.X509.DistributionPoint");
        configurator.mapType("org.bouncycastle.asn1.x509.DistributionPointName", "Org.BouncyCastle.Asn1.X509.DistributionPointName");
        configurator.mapType("org.bouncycastle.asn1.x509.GeneralNames", "Org.BouncyCastle.Asn1.X509.GeneralNames");
        configurator.mapType("org.bouncycastle.asn1.x509.GeneralName", "Org.BouncyCastle.Asn1.X509.GeneralName");
        configurator.mapType("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers", "Org.BouncyCastle.Asn1.Ocsp.OcspObjectIdentifiers");
        configurator.mapType("org.bouncycastle.asn1.ocsp.BasicOCSPResponse", "Org.BouncyCastle.Asn1.Ocsp.BasicOcspResponse");
        configurator.mapType("org.bouncycastle.asn1.ocsp.BasicOCSPResponseBC", "Org.BouncyCastle.Asn1.Ocsp.BasicOcspResponseBC");
        configurator.mapType("org.bouncycastle.asn1.ocsp.OCSPResponseBC", "Org.BouncyCastle.Asn1.Ocsp.OcspResponseBC");
        configurator.mapType("org.bouncycastle.asn1.tsp.MessageImprint", "Org.BouncyCastle.Asn1.Tsp.MessageImprint");
        configurator.mapType("org.bouncycastle.asn1.tsp.TSTInfo", "Org.BouncyCastle.Asn1.Tsp.TstInfo");
        configurator.mapType("org.bouncycastle.tsp.TimeStampRequest", "Org.BouncyCastle.Tsp.TimeStampRequest");
        configurator.mapType("org.bouncycastle.tsp.TimeStampRequestGenerator", "Org.BouncyCastle.Tsp.TimeStampRequestGenerator");
        configurator.mapType("org.bouncycastle.tsp.TimeStampResponse", "Org.BouncyCastle.Tsp.TimeStampResponse");
        configurator.mapType("org.bouncycastle.tsp.TimeStampToken", "Org.BouncyCastle.Tsp.TimeStampToken");
        configurator.mapType("org.bouncycastle.tsp.TimeStampTokenInfo", "Org.BouncyCastle.Tsp.TimeStampTokenInfo");
        configurator.mapType("org.bouncycastle.tsp.TSPException", "Org.BouncyCastle.Tsp.TspException");
        configurator.mapType("org.bouncycastle.cert.CertIOException", "Org.BouncyCastle.Security.Certificates.CertificateEncodingException");
        configurator.mapType("org.bouncycastle.cert.ocsp.OCSPResp", "Org.BouncyCastle.Ocsp.OcspResp");
        configurator.mapType("org.bouncycastle.cert.ocsp.OCSPException", "Org.BouncyCastle.Ocsp.OcspException");
        configurator.mapType("org.bouncycastle.cert.ocsp.RevokedStatus", "Org.BouncyCastle.Ocsp.RevokedStatus");
        configurator.mapType("org.bouncycastle.cert.ocsp.UnknownStatus", "Org.BouncyCastle.Ocsp.UnknownStatus");
        configurator.mapType("org.bouncycastle.ocsp.OCSPRespStatus", "Org.BouncyCastle.Ocsp.OcspRespStatus");
        configurator.mapType("org.bouncycastle.ocsp.OCSPRespStatusBC", "Org.BouncyCastle.Ocsp.OcspRespStatusBC");
        configurator.mapType("org.bouncycastle.ocsp.RevokedStatus", "Org.BouncyCastle.Ocsp.RevokedStatus");
        configurator.mapType("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers");
        configurator.mapType("org.bouncycastle.asn1.pkcs.RSASSAPSSparams", "Org.BouncyCastle.Asn1.Pkcs.RsassaPssParameters");
        configurator.mapType("org.bouncycastle.asn1.ess.SigningCertificate", "Org.BouncyCastle.Asn1.Ess.SigningCertificate");
        configurator.mapType("org.bouncycastle.asn1.ess.SigningCertificateV2", "Org.BouncyCastle.Asn1.Ess.SigningCertificateV2");
        configurator.mapType("org.bouncycastle.asn1.ess.ESSCertIDv2", "Org.BouncyCastle.Asn1.Ess.EssCertIDv2");
        configurator.mapType("org.bouncycastle.asn1.ess.ESSCertID", "Org.BouncyCastle.Asn1.Ess.EssCertID");
        configurator.mapType("org.bouncycastle.jce.X509Principal", "Org.BouncyCastle.Asn1.X509.X509Name");
        configurator.mapType("org.bouncycastle.asn1.DERObjectIdentifier", "Org.BouncyCastle.Asn1.DerObjectIdentifier");
        configurator.mapType("org.bouncycastle.asn1.ocsp.OCSPResponseStatus", "Org.BouncyCastle.Asn1.Ocsp.OcspResponseStatus");
        configurator.mapType("org.bouncycastle.asn1.ocsp.OCSPResponse", "Org.BouncyCastle.Asn1.Ocsp.OcspResponse");
        configurator.mapType("org.bouncycastle.asn1.x500.X500Name", "Org.BouncyCastle.Asn1.X509.X509Name");
        configurator.mapType("org.bouncycastle.cert.ocsp.OCSPRespBuilder", "Org.BouncyCastle.Ocsp.OCSPRespGenerator");
        configurator.mapType("org.bouncycastle.cert.ocsp.RespID", "Org.BouncyCastle.Ocsp.RespID");
        configurator.mapType("org.bouncycastle.operator.DigestCalculator", "Org.BouncyCastle.Crypto.IDigestFactory");
        configurator.mapType("org.bouncycastle.jcajce.provider.digest.MD5.Digest", "Org.BouncyCastle.Crypto.Digests.MD5Digest");
        configurator.mapType("org.bouncycastle.asn1.util.ASN1Dump", "Org.BouncyCastle.Asn1.Utilities.Asn1Dump");
        configurator.mapType("org.bouncycastle.cms.CMSException", "Org.BouncyCastle.Cms.CmsException");
        configurator.mapType("org.bouncycastle.cms.CMSEnvelopedData", "Org.BouncyCastle.Cms.CmsEnvelopedData");
        configurator.mapType("org.bouncycastle.operator.ContentSigner", "Org.BouncyCastle.Crypto.Operators.Asn1SignatureFactory");
        configurator.mapType("org.bouncycastle.asn1.cms.Attribute", "Org.BouncyCastle.Asn1.Cms.Attribute");
        configurator.addFullName("Org.BouncyCastle.Asn1.Cms.Attribute");
        configurator.addFullName("Org.BouncyCastle.Asn1.Cms.AttributeTable");
        configurator.mapType("org.bouncycastle.asn1.cms.AttributeTable", "Org.BouncyCastle.Asn1.Cms.AttributeTable");
        configurator.mapMethod("org.bouncycastle.asn1.ASN1Encodable.toASN1Primitive", "ToAsn1Object");
        configurator.mapMethod("org.bouncycastle.asn1.ASN1OutputStream.close", "Dispose");
        configurator.mapMethod("org.bouncycastle.asn1.ASN1Set.getObjects", "GetEnumerator");
        configurator.mapMethod("org.bouncycastle.asn1.ASN1Sequence.getObjects", "GetEnumerator");
        configurator.mapMethod("org.bouncycastle.asn1.ASN1GeneralizedTime.getDate", "ToDateTime");
        configurator.mapMethod("org.bouncycastle.cert.ocsp.OCSPRespBuilder.build", "Generate");
        configurator.mapProperty("org.bouncycastle.asn1.DERInteger.getValue", "Value");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Integer.getValue", "Value");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1BitString.intValue", "IntValue");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Enumerated.intValueExact", "IntValueExact");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Enumerated.getValue", "Value");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Set.size", "Count");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Sequence.size", "Count");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1EncodableVector.size", "Count");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Sequence.getValue", "Value");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1Sequence.getId", "Id");
        configurator.mapProperty("org.bouncycastle.asn1.DERObjectIdentifier.getId", "Id");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1ObjectIdentifier.getId", "Id");
        configurator.mapProperty("org.bouncycastle.asn1.DERBitString.intValue", "IntValue");
        configurator.mapProperty("org.bouncycastle.asn1.ASN1TaggedObjectParser.getTagNo", "TagNo");
        configurator.mapProperty("org.bouncycastle.asn1.x509.DistributionPoint.getDistributionPoint", "DistributionPointName");
        configurator.mapProperty("org.bouncycastle.asn1.x509.DistributionPointName.getType", "PointType");
        configurator.mapProperty("org.bouncycastle.asn1.x509.GeneralName.getTagNo", "TagNo");
        configurator.mapProperty("org.bouncycastle.asn1.x509.DistributionPointName.getName", "Name");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificateStructure.getSubjectPublicKeyInfo", "SubjectPublicKeyInfo");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificate.getSubjectPublicKeyInfo", "SubjectPublicKeyInfo");
        configurator.mapProperty("org.bouncycastle.asn1.x509.SubjectPublicKeyInfo.getAlgorithm", "AlgorithmID");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificateStructure.getIssuer", "Issuer");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificateStructure.getSerialNumber", "SerialNumber");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificate.getIssuer", "Issuer");
        configurator.mapProperty("org.bouncycastle.asn1.x509.TBSCertificate.getSerialNumber", "SerialNumber");
        configurator.mapProperty("org.bouncycastle.asn1.x509.AlgorithmIdentifier.getAlgorithm", "Algorithm");
        configurator.mapProperty("org.bouncycastle.asn1.x509.AlgorithmIdentifier.getParameters", "Parameters");
        configurator.mapProperty("org.bouncycastle.asn1.cms.Attribute.getAttrValues", "AttrValues");
        configurator.mapProperty("org.bouncycastle.asn1.ess.ESSCertIDv2.getHashAlgorithm", "HashAlgorithm");
        configurator.mapProperty("org.bouncycastle.asn1.tsp.TSTInfo.getMessageImprint", "MessageImprint");
        configurator.mapProperty("org.bouncycastle.asn1.tsp.TSTInfo.getGenTime", "GenTime");
        configurator.mapProperty("org.bouncycastle.asn1.tsp.MessageImprint.getHashAlgorithm", "HashAlgorithm");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.OCSPResp.getStatus", "Status");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.SingleResp.getNextUpdate", "NextUpdate");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.SingleResp.getThisUpdate", "ThisUpdate");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.BasicOCSPResp.getResponses", "Responses");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.BasicOCSPResp.getProducedAt", "ProducedAt");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.CertificateID.getSerialNumber", "SerialNumber");
        configurator.mapProperty("org.bouncycastle.cert.ocsp.CertificateID.getHashAlgOID", "HashAlgOid");
        configurator.mapProperty("org.bouncycastle.tsp.TimeStampResponse.getTimeStampToken", "TimeStampToken");
        configurator.mapProperty("org.bouncycastle.tsp.TimeStampToken.getTimeStampInfo", "TimeStampInfo");
        configurator.mapProperty("org.bouncycastle.tsp.TimeStampTokenInfo.getGenTime", "GenTime");
        configurator.mapProperty("org.bouncycastle.tsp.TimeStampTokenInfo.toASN1Structure", "TstInfo");
        configurator.mapProperty("org.bouncycastle.tsp.TimeStampTokenInfo.getHashAlgorithm", "HashAlgorithm");
        configurator.mapIndexer("org.bouncycastle.asn1.ASN1Sequence.getObjectAt");
        configurator.mapIndexer("org.bouncycastle.asn1.ASN1Set.getObjectAt");
        configurator.mapIndexer("org.bouncycastle.asn1.cms.AttributeTable.get");
        configurator.mapField("org.bouncycastle.asn1.DERNull.INSTANCE", "Org.BouncyCastle.Asn1.DerNull.Instance");
        configurator.mapField("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.data", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers.Data");
        configurator.mapField("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.envelopedData", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers.EnvelopedData");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.cRLDistributionPoints", "X509Extensions.CrlDistributionPoints");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.authorityInfoAccess", "X509Extensions.AuthorityInfoAccess");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.basicConstraints", "X509Extensions.BasicConstraints");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.keyUsage", "X509Extensions.KeyUsage");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.extendedKeyUsage", "X509Extensions.ExtendedKeyUsage");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier", "X509Extensions.AuthorityKeyIdentifier");
        configurator.mapField("org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier", "X509Extensions.SubjectKeyIdentifier");
        configurator.mapField("org.bouncycastle.asn1.x509.DistributionPointName.FULL_NAME", "DistributionPointName.FullName");
        configurator.mapField("org.bouncycastle.asn1.x509.GeneralName.uniformResourceIdentifier", "GeneralName.UniformResourceIdentifier");
        configurator.mapField("org.bouncycastle.asn1.x509.KeyPurposeId.id_kp_OCSPSigning", "KeyPurposeID.IdKPOcspSigning");
        configurator.mapField("org.bouncycastle.asn1.x509.KeyUsage.digitalSignature", "KeyUsage.DigitalSignature");
        configurator.mapField("org.bouncycastle.asn1.x509.KeyUsage.nonRepudiation", "KeyUsage.NonRepudiation");
        configurator.mapField("org.bouncycastle.ocsp.OCSPRespStatus.SUCCESSFUL", "Successful");
        configurator.mapField("org.bouncycastle.cert.ocsp.CertificateStatus.GOOD", "Good");
        configurator.mapField("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers.id_pkix_ocsp_basic", "PkixOcspBasic");
        configurator.mapField("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers.id_pkix_ocsp_nocheck", "PkixOcspNocheck");
        configurator.mapField("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers.id_pkix_ocsp_nonce", "PkixOcspNonce");
        configurator.mapField("org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers.id_pkix_ocsp_archive_cutoff", "PkixOcspArchiveCutoff");
        configurator.mapField("org.bouncycastle.asn1.ASN1Encoding.DER", "Org.BouncyCastle.Asn1.Asn1Encodable.Der");
        configurator.mapField("org.bouncycastle.asn1.ASN1Encoding.BER", "Org.BouncyCastle.Asn1.Asn1Encodable.Ber");
        configurator.mapField("org.bouncycastle.cert.ocsp.CertificateID.HASH_SHA1", "Org.BouncyCastle.Ocsp.CertificateID.HashSha1");
        configurator.mapField("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.id_aa_signatureTimeStampToken", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers.IdAASignatureTimeStampToken");
        configurator.mapField("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.id_spq_ets_uri", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers.IdSpqEtsUri");
        configurator.mapField("org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.id_aa_ets_sigPolicyId", "Org.BouncyCastle.Asn1.Pkcs.PkcsObjectIdentifiers.IdAAEtsSigPolicyID");
        configurator.mapField("org.bouncycastle.cert.ocsp.OCSPRespBuilder.SUCCESSFUL", "Org.BouncyCastle.Asn1.Ocsp.OcspResponseStatus.Successful");
        configurator.mapField("org.bouncycastle.asn1.ocsp.OCSPResponseStatus.SUCCESSFUL", "Org.BouncyCastle.Asn1.Ocsp.OcspResponseStatus.Successful");
        configurator.mapField("org.bouncycastle.cert.ocsp.OCSPResp.SUCCESSFUL", "Org.BouncyCastle.Asn1.Ocsp.OcspResponseStatus.Successful");
        configurator.mapField("org.bouncycastle.asn1.x509.CRLReason.keyCompromise", "Org.BouncyCastle.Asn1.X509.CrlReason.KeyCompromise");
        configurator.mapField("org.bouncycastle.asn1.x509.CRLReason.removeFromCRL", "Org.BouncyCastle.Asn1.X509.CrlReason.RemoveFromCrl");
        configurator.mapType("org.bouncycastle.cert.ocsp.OCSPReq", "Org.BouncyCastle.Ocsp.OcspReq");
        configurator.mapType("org.bouncycastle.cert.ocsp.SingleResp", "Org.BouncyCastle.Ocsp.SingleResp");
        configurator.mapType("org.bouncycastle.cert.ocsp.CertificateID", "Org.BouncyCastle.Ocsp.CertificateID");
        configurator.mapType("org.bouncycastle.cert.ocsp.CertificateStatus", "Org.BouncyCastle.Ocsp.CertificateStatus");
        configurator.mapType("org.bouncycastle.cert.ocsp.BasicOCSPResp", "Org.BouncyCastle.Ocsp.BasicOcspResp");
        configurator.mapProperty("org.bouncycastle.asn1.x509.DistributionPoint.getCRLIssuer", "CrlIssuer");
        configurator.mapProperty("org.bouncycastle.asn1.x509.DistributionPoint.getReasons", "Reasons");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.getDistributionPoint", "DistributionPoint");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.onlyContainsUserCerts", "OnlyContainsUserCerts");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.onlyContainsCACerts", "OnlyContainsCACerts");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.isIndirectCRL", "IsIndirectCrl");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.onlyContainsAttributeCerts", "OnlyContainsAttributeCerts");
        configurator.mapProperty("org.bouncycastle.asn1.x509.IssuingDistributionPoint.getOnlySomeReasons", "OnlySomeReasons");
    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {

    }

    @Override
    public void applyConfigModuleSettings(ModulesConfigurator configurator) {
        useBCWrappersConfig = configurator.getModuleOption(UseBCWrappersModuleOption.getInstance());
    }

    @Override
    public void setConfigModuleSettings(ModulesConfigurator modulesConfigurator) {
        modulesConfigurator.setModuleOption(UseBCWrappersModuleOption.getInstance(), true);
    }

    @Override
    public Collection<ModuleOption> getAvailableModuleSettings() {
        return Collections.singletonList(UseBCWrappersModuleOption.getInstance());
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredSourceFiles() {
        return null;
    }

    @Override
    public Collection<String> getIgnoredResources() {
        return null;
    }

    @Override
    public List<SimpleImmutableEntry<String, String>> getOverwrittenResources() {
        return null;
    }
}
