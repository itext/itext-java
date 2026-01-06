/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.validation.lotl.CountrySpecificLotlFetcher.Result;
import com.itextpdf.signatures.validation.lotl.criteria.CertSubjectDNAttributeCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.Criteria;
import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;
import com.itextpdf.signatures.validation.lotl.criteria.ExtendedKeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.KeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.PolicySetCriteria;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
public class LotlCacheDataV1Test extends ExtendedITextTest {

    // Test data constants
    private static final byte[] TEST_LOTL_XML = {1, 2, 3};
    private static final String TEST_TYPE = "TestType";
    private static final String TEST_MESSAGE = "TestMessage";
    private static final String TEST_TYPE_2 = "TestType2";
    private static final String TEST_MESSAGE_2 = "TestMessage2";
    private static final String TEST_CAUSE_MESSAGE = "TestCause";
    private static final String TEST_SERVICE_TYPE = "TestServiceType";
    private static final String TEST_SERVICE_TYPE_2 = "TestServiceType2";
    private static final String TEST_SERVICE_STATUS = "someStatus";
    private static final String TEST_SERVICE_STATUS_2 = "someStatus2";
    private static final String TEST_PUBLICATION = "2024/C 123/01";
    private static final String TEST_URL_1 = "http://testurl1.com";
    private static final String TEST_URL_2 = "http://testurl2.com";
    private static final String BE_COUNTRY_CODE = "BE";
    private static final String FR_COUNTRY_CODE = "FR";
    private static final String BE_LOTL_URL = "https://example.com/be/lotl";
    private static final String FR_LOTL_URL = "https://example.com/fr/lotl";
    private static final String APPLICATION_XML = "application/xml";
    private static final String BE_CACHE_KEY = "BE_someUrl";
    private static final String FR_CACHE_KEY = "FR_someUrl";
    private static final String EXTENSION_URI = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt"
            + "/ForElectronicSignatureCreation";
    private static final String TIMESTAMP_KEY_1 = "key1";
    private static final String TIMESTAMP_KEY_2 = "key2";
    private static final long TIMESTAMP_VALUE_1 = 123456789L;
    private static final long TIMESTAMP_VALUE_2 = 987654321L;
    private static final LocalDateTime TEST_DATE_TIME_1 = LocalDateTime.of(2024, 1, 1, 0, 0, 1);
    private static final LocalDateTime TEST_DATE_TIME_2 = LocalDateTime.of(2024, 1, 1, 0, 0, 2);

    @Test
    public void testSerializationEuropeanLotlFetcherAndValidationReport() throws IOException {
        // Arrange
        EuropeanLotlFetcher.Result lotlCache = new EuropeanLotlFetcher.Result();
        lotlCache.setLotlXml(TEST_LOTL_XML);
        ReportItem originalReportItem = createTestReportItem(TEST_TYPE, TEST_MESSAGE);
        lotlCache.getLocalReport().addReportItem(originalReportItem);

        LotlCacheDataV1 original = new LotlCacheDataV1(lotlCache, null, null, null, null);

        // Act & Assert
        byte[] serializedData = serializeAndGetBytes(original);
        assertNotNull(serializedData, "Serialized data should not be null");

        LotlCacheDataV1 deserialized = deserializeFromBytes(serializedData);
        assertNotNull(deserialized, "Deserialized object should not be null");

        // Verify
        EuropeanLotlFetcher.Result deserializedLotlCache = deserialized.getLotlCache();
        assertNotNull(deserializedLotlCache, "Deserialized LotlCache should not be null");
        assertArrayEquals(lotlCache.getLotlXml(), deserializedLotlCache.getLotlXml(), "LotlXml should match");
        assertEquals(lotlCache.getLocalReport().getLogs().size(),
                deserializedLotlCache.getLocalReport().getLogs().size(), "Report items size should match");

        ReportItem deserializedReportItem = deserializedLotlCache.getLocalReport().getLogs().get(0);
        assertReportItemsMatch(originalReportItem, deserializedReportItem);
    }

    @Test
    public void testSerializationEuropeanLotlFetcherAndValidationReportWithCause() throws IOException {
        // Arrange
        EuropeanLotlFetcher.Result lotlCache = new EuropeanLotlFetcher.Result();
        lotlCache.setLotlXml(TEST_LOTL_XML);
        Exception cause = new IllegalArgumentException(TEST_CAUSE_MESSAGE);
        ReportItem originalReportItem = createTestReportItemWithCause(TEST_TYPE, TEST_MESSAGE, cause);
        lotlCache.getLocalReport().addReportItem(originalReportItem);

        LotlCacheDataV1 original = new LotlCacheDataV1(lotlCache, null, null, null, null);

        // Act & Assert
        byte[] serializedData = serializeAndGetBytes(original);
        assertNotNull(serializedData, "Serialized data should not be null");

        LotlCacheDataV1 deserialized = deserializeFromBytes(serializedData);
        assertNotNull(deserialized, "Deserialized object should not be null");

        // Verify
        EuropeanLotlFetcher.Result deserializedLotlCache = deserialized.getLotlCache();
        assertNotNull(deserializedLotlCache, "Deserialized LotlCache should not be null");
        assertArrayEquals(lotlCache.getLotlXml(), deserializedLotlCache.getLotlXml(), "LotlXml should match");
        assertEquals(lotlCache.getLocalReport().getLogs().size(),
                deserializedLotlCache.getLocalReport().getLogs().size(), "Report items size should match");

        ReportItem deserializedReportItem = deserializedLotlCache.getLocalReport().getLogs().get(0);
        assertEquals(originalReportItem.getCheckName(), deserializedReportItem.getCheckName(),
                "Report item type should match");
        assertEquals(originalReportItem.getMessage(), deserializedReportItem.getMessage(),
                "Report item message should match");
        assertEquals(originalReportItem.getStatus(), deserializedReportItem.getStatus(),
                "Report item status should match");
        assertEquals(originalReportItem.getExceptionCause().getMessage(),
                deserializedReportItem.getExceptionCause().getMessage(),
                "Report item cause should match");
    }

    @Test
    public void shouldSerializePivotFetcherWithUrls() throws IOException {
        // Arrange
        PivotFetcher.Result pivotCache = new PivotFetcher.Result();
        List<String> urls = Arrays.asList(TEST_URL_1, TEST_URL_2);
        pivotCache.setPivotUrls(urls);

        ValidationReport report = new ValidationReport();
        ReportItem originalReportItem = createTestReportItem(TEST_TYPE, TEST_MESSAGE);
        report.addReportItem(originalReportItem);
        pivotCache.setLocalReport(report);

        LotlCacheDataV1 original = new LotlCacheDataV1(null, pivotCache, null, null, null);

        // Act & Assert
        byte[] serializedData = serializeAndGetBytes(original);
        assertNotNull(serializedData, "Serialized data should not be null");

        LotlCacheDataV1 deserialized = deserializeFromBytes(serializedData);
        assertNotNull(deserialized, "Deserialized object should not be null");

        // Verify
        PivotFetcher.Result deserializedPivotCache = deserialized.getPivotCache();
        assertNotNull(deserializedPivotCache, "Deserialized PivotCache should not be null");
        assertEquals(pivotCache.getPivotUrls().size(), deserializedPivotCache.getPivotUrls().size(),
                "Pivot URLs size should match");

        List<String> originalUrls = pivotCache.getPivotUrls();
        List<String> deserializedUrls = deserializedPivotCache.getPivotUrls();
        for (int i = 0; i < originalUrls.size(); i++) {
            assertEquals(originalUrls.get(i), deserializedUrls.get(i),
                    "Pivot URL at index " + i + " should match");
        }

        ValidationReport deserializedReport = deserialized.getPivotCache().getLocalReport();
        assertNotNull(deserializedReport, "Deserialized Report should not be null");
        assertEquals(report.getLogs().size(), deserializedReport.getLogs().size(), "Report items size should match");
        ReportItem deserializedReportItem = deserializedReport.getLogs().get(0);
        assertReportItemsMatch(originalReportItem, deserializedReportItem);
    }

    @Test
    public void europeanResourceFetcherSerializationDeserializationTest() throws IOException {
        EuropeanResourceFetcher.Result europeanCache = new EuropeanResourceFetcher.Result();

        ReportItem ogReportItem = new ReportItem(TEST_TYPE, TEST_MESSAGE, ReportItemStatus.INFO);
        europeanCache.getLocalReport().addReportItem(ogReportItem);
        europeanCache.setCurrentlySupportedPublication(TEST_PUBLICATION);
        ArrayList<java.security.cert.Certificate> certs = new ArrayList<>();
        // Adding dummy certificates for testing purposes
        certs.add(null);
        europeanCache.setCertificates(certs);

        LotlCacheDataV1 original = new LotlCacheDataV1(null, null, europeanCache, null, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);
        byte[] serializedData = bos.toByteArray();
        assertNotNull(serializedData, "Serialized data should not be null");
        LotlCacheDataV1 deserialized = LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
        assertNotNull(deserialized, "Deserialized object should not be null");
        EuropeanResourceFetcher.Result deserializedEuropeanCache = deserialized.getEuropeanResourceFetcherCache();
        assertNotNull(deserializedEuropeanCache, "Deserialized EuropeanCache should not be null");
        assertEquals(europeanCache.getCurrentlySupportedPublication(),
                deserializedEuropeanCache.getCurrentlySupportedPublication(),
                "CurrentlySupportedPublication should match");
        assertEquals(europeanCache.getCertificates().size(), deserializedEuropeanCache.getCertificates().size(),
                "Certificates size should match");

        List<java.security.cert.Certificate> originalCerts = europeanCache.getCertificates();
        List<java.security.cert.Certificate> deserializedCerts = deserializedEuropeanCache.getCertificates();
        for (int i = 0; i < originalCerts.size(); i++) {
            assertEquals(originalCerts.get(i), deserializedCerts.get(i),
                    "Certificate at index " + i + " should match");
        }
    }

    @Test
    public void testCountrySpecificResultsWithMinimalServiceContext() throws IOException {
        CountrySpecificLotlFetcher.Result countrySpecificCache = new CountrySpecificLotlFetcher.Result();
        CountrySpecificLotl be = new CountrySpecificLotl(BE_COUNTRY_CODE, BE_LOTL_URL, APPLICATION_XML);

        ValidationReport report = new ValidationReport();
        ReportItem ogReportItem = new ReportItem(TEST_TYPE, TEST_MESSAGE, ReportItemStatus.INFO);
        report.addReportItem(ogReportItem);
        countrySpecificCache.setCountrySpecificLotl(be);
        countrySpecificCache.setLocalReport(report);

        IServiceContext context = new SimpleServiceContext();
        context.addCertificate(null);

        List<IServiceContext> contexts = new ArrayList<>();
        contexts.add(context);
        countrySpecificCache.setContexts(contexts);
        HashMap<String, CountrySpecificLotlFetcher.Result> map = new HashMap<>();
        map.put(BE_CACHE_KEY, countrySpecificCache);
        LotlCacheDataV1 original = new LotlCacheDataV1(null, null, null, map, null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);

        byte[] serializedData = bos.toByteArray();
        assertNotNull(serializedData, "Serialized data should not be null");
        LotlCacheDataV1 deserialized = LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
        assertNotNull(deserialized, "Deserialized object should not be null");
        Map<String, Result> deserializedMap = deserialized.getCountrySpecificLotlCache();
        assertNotNull(deserializedMap, "Deserialized Map should not be null");
        assertEquals(1, deserializedMap.size(), "Map size should match");
        CountrySpecificLotlFetcher.Result deserializedCountrySpecificCache = deserializedMap.get(BE_CACHE_KEY);
        assertNotNull(deserializedCountrySpecificCache, "Deserialized CountrySpecificCache should not be null");

        assertEquals(countrySpecificCache.getCountrySpecificLotl().getSchemeTerritory(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getSchemeTerritory(),
                "Country code should match");
        assertEquals(countrySpecificCache.getCountrySpecificLotl().getTslLocation(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getTslLocation(),
                "TslLocation should match");
        assertEquals(countrySpecificCache.getCountrySpecificLotl().getMimeType(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getMimeType(),
                "MimeType should match");

        ValidationReport deserializedReport = deserializedCountrySpecificCache.getLocalReport();
        assertNotNull(deserializedReport, "Deserialized Report should not be null");
        assertEquals(report.getLogs().size(), deserializedReport.getLogs().size(),
                "Report items size should match");
        ReportItem deserializedReportItem = deserializedReport.getLogs().get(0);
        assertEquals(ogReportItem.getCheckName(), deserializedReportItem.getCheckName(),
                "Report item type should match");
        assertEquals(ogReportItem.getMessage(), deserializedReportItem.getMessage(),
                "Report item message should match");
        assertEquals(ogReportItem.getStatus(), deserializedReportItem.getStatus(),
                "Report item status should match");
        assertEquals(ogReportItem.getExceptionCause(), deserializedReportItem.getExceptionCause(),
                "Report item cause should match");
        assertEquals(1, deserializedCountrySpecificCache.getContexts().size(), "Contexts size should match");
        IServiceContext deserializedContext = deserializedCountrySpecificCache.getContexts().get(0);
        assertNotNull(deserializedContext, "Context should not be null");
        assertEquals(1, deserializedContext.getCertificates().size(),
                "Certificates size should match");
        assertNull(deserializedContext.getCertificates().get(0),
                "Certificate should be null");

    }

    @Test
    public void testCountrySpecificResultsFullServiceContext() throws IOException {
        CountrySpecificLotlFetcher.Result countrySpecificCache = new CountrySpecificLotlFetcher.Result();
        CountrySpecificLotl be = new CountrySpecificLotl(BE_COUNTRY_CODE, BE_LOTL_URL, APPLICATION_XML);

        ValidationReport report = new ValidationReport();
        ReportItem ogReportItem = new ReportItem(TEST_TYPE, TEST_MESSAGE, ReportItemStatus.INFO);
        report.addReportItem(ogReportItem);
        countrySpecificCache.setCountrySpecificLotl(be);
        countrySpecificCache.setLocalReport(report);

        CertSubjectDNAttributeCriteria criteria1 = new CertSubjectDNAttributeCriteria();
        criteria1.addRequiredAttributeId("attr1");
        criteria1.addRequiredAttributeId("attr2");
        ExtendedKeyUsageCriteria criteria2 = new ExtendedKeyUsageCriteria();
        criteria2.addRequiredExtendedKeyUsage("keyUsage1");
        criteria2.addRequiredExtendedKeyUsage("keyUsage2");
        PolicySetCriteria criteria3 = new PolicySetCriteria();
        criteria3.addRequiredPolicyId("policyId1");
        criteria3.addRequiredPolicyId("policyId2");
        KeyUsageCriteria criteria4 = new KeyUsageCriteria();
        criteria4.addKeyUsageBit("nonRepudiation", "true");
        criteria4.addKeyUsageBit("dataEncipherment", "true");
        criteria4.addKeyUsageBit("keyCertSign", "true");
        criteria4.addKeyUsageBit("encipherOnly", "false");
        CriteriaList innerCriteriaList = new CriteriaList("atLeastOne");
        innerCriteriaList.addCriteria(criteria1);
        innerCriteriaList.addCriteria(criteria2);
        innerCriteriaList.addCriteria(criteria3);
        innerCriteriaList.addCriteria(criteria4);

        CriteriaList criteriaList = new CriteriaList("all");
        criteriaList.addCriteria(innerCriteriaList);
        criteriaList.addCriteria(criteria1);

        QualifierExtension qualifierExtension = new QualifierExtension();
        qualifierExtension.addQualifier("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCQSCDManagedOnBehalf");
        qualifierExtension.addQualifier("http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/QCForLegalPerson");
        qualifierExtension.setCriteriaList(criteriaList);

        ServiceChronologicalInfo chronologicalInfo = new ServiceChronologicalInfo(TEST_SERVICE_STATUS,
                TEST_DATE_TIME_1);
        chronologicalInfo.addQualifierExtension(qualifierExtension);
        chronologicalInfo.addQualifierExtension(qualifierExtension);

        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(null);
        context.setServiceType(TEST_SERVICE_TYPE);
        context.getServiceChronologicalInfos().add(chronologicalInfo);

        ServiceChronologicalInfo ogChronologicalInfo = new ServiceChronologicalInfo(TEST_SERVICE_STATUS,
                TEST_DATE_TIME_1);
        AdditionalServiceInformationExtension ogChronologicalInfoExtension =
                new AdditionalServiceInformationExtension();
        ogChronologicalInfoExtension.setUri(EXTENSION_URI);
        ogChronologicalInfo.addServiceExtension(ogChronologicalInfoExtension);
        context.getServiceChronologicalInfos().add(ogChronologicalInfo);

        List<IServiceContext> contexts = new ArrayList<>();
        contexts.add(context);
        countrySpecificCache.setContexts(contexts);
        HashMap<String, CountrySpecificLotlFetcher.Result> map = new HashMap<>();
        map.put(BE_CACHE_KEY, countrySpecificCache);
        LotlCacheDataV1 original = new LotlCacheDataV1(null, null, null, map, null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);

        byte[] serializedData = bos.toByteArray();
        assertNotNull(serializedData, "Serialized data should not be null");
        LotlCacheDataV1 deserialized = LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
        assertNotNull(deserialized, "Deserialized object should not be null");
        Map<String, Result> deserializedMap = deserialized.getCountrySpecificLotlCache();
        assertNotNull(deserializedMap, "Deserialized Map should not be null");
        assertEquals(1, deserializedMap.size(), "Map size should match");
        CountrySpecificLotlFetcher.Result deserializedCountrySpecificCache = deserializedMap.get(BE_CACHE_KEY);
        assertNotNull(deserializedCountrySpecificCache, "Deserialized CountrySpecificCache should not be null");

        assertEquals(countrySpecificCache.getCountrySpecificLotl().getSchemeTerritory(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getSchemeTerritory(),
                "Country code should match");
        assertEquals(countrySpecificCache.getCountrySpecificLotl().getTslLocation(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getTslLocation(),
                "TslLocation should match");
        assertEquals(countrySpecificCache.getCountrySpecificLotl().getMimeType(),
                deserializedCountrySpecificCache.getCountrySpecificLotl().getMimeType(),
                "MimeType should match");

        ValidationReport deserializedReport = deserializedCountrySpecificCache.getLocalReport();
        assertNotNull(deserializedReport, "Deserialized Report should not be null");
        assertEquals(report.getLogs().size(), deserializedReport.getLogs().size(),
                "Report items size should match");
        ReportItem deserializedReportItem = deserializedReport.getLogs().get(0);
        assertEquals(ogReportItem.getCheckName(), deserializedReportItem.getCheckName(),
                "Report item type should match");
        assertEquals(ogReportItem.getMessage(), deserializedReportItem.getMessage(),
                "Report item message should match");
        assertEquals(ogReportItem.getStatus(), deserializedReportItem.getStatus(),
                "Report item status should match");
        assertEquals(ogReportItem.getExceptionCause(), deserializedReportItem.getExceptionCause(),
                "Report item cause should match");

        IServiceContext deserializedContext = deserializedCountrySpecificCache.getContexts().get(0);
        assertNotNull(deserializedContext, "Deserialized Context should not be null");
        assertTrue(deserializedContext instanceof CountryServiceContext,
                "Context should be of type CountryServiceContext");
        CountryServiceContext deserializedCountryContext = (CountryServiceContext) deserializedContext;
        assertEquals(context.getServiceType(), deserializedCountryContext.getServiceType(),
                "ServiceType should match");

        List<ServiceChronologicalInfo> originalChronoInfos = context.getServiceChronologicalInfos();
        List<ServiceChronologicalInfo> deserializedChronoInfos =
                deserializedCountryContext.getServiceChronologicalInfos();
        assertEquals(originalChronoInfos.size(), deserializedChronoInfos.size(),
                "ServiceChronologicalInfos size should match");

        ServiceChronologicalInfo firstOriginalChronoInfo = originalChronoInfos.get(0);
        ServiceChronologicalInfo firstDeserializedChronoInfo = deserializedChronoInfos.get(0);
        assertEquals(firstOriginalChronoInfo.getServiceStatus(), firstDeserializedChronoInfo.getServiceStatus(),
                "ServiceStatus should match");
        assertEquals(firstOriginalChronoInfo.getServiceStatusStartingTime(),
                firstDeserializedChronoInfo.getServiceStatusStartingTime(),
                "ServiceStatusStartingTime should match");
        assertEquals(firstOriginalChronoInfo.getServiceExtensions().size(),
                firstDeserializedChronoInfo.getServiceExtensions().size(),
                "Extensions size should match");

        List<QualifierExtension> originalQualifierExtensions = firstOriginalChronoInfo.getQualifierExtensions();
        List<QualifierExtension> deserializedQualifierExtensions = firstDeserializedChronoInfo.getQualifierExtensions();
        assertEquals(originalQualifierExtensions.size(), deserializedQualifierExtensions.size(),
                "QualifierExtensions size should match");
        QualifierExtension originalQualifierExtension = originalQualifierExtensions.get(0);
        QualifierExtension deserializedQualifierExtension = deserializedQualifierExtensions.get(0);
        assertEquals(originalQualifierExtension.getQualifiers().size(),
                deserializedQualifierExtension.getQualifiers().size(),
                "Qualifiers size should match");
        for (int i = 0; i < originalQualifierExtension.getQualifiers().size(); i++) {
            assertEquals(originalQualifierExtension.getQualifiers().get(i),
                    deserializedQualifierExtension.getQualifiers().get(i),
                    "Qualifier at index " + i + " should match");
        }
        CriteriaList originalCriteriaList = originalQualifierExtension.getCriteriaList();
        CriteriaList deserializedCriteriaList = deserializedQualifierExtension.getCriteriaList();

        assertNotNull(deserializedCriteriaList, "Deserialized CriteriaList should not be null");
        assertEquals(originalCriteriaList.getCriteriaList().size(), deserializedCriteriaList.getCriteriaList().size());

        for (int i = 0; i < originalCriteriaList.getCriteriaList().size(); i++) {
            Criteria originalCriteria = originalCriteriaList.getCriteriaList().get(i);
            Criteria deserializedCriteria = deserializedCriteriaList.getCriteriaList().get(i);
            assertEquals(originalCriteria.getClass(), deserializedCriteria.getClass(),
                    "Criteria class at index " + i + " should match");
            if (originalCriteria instanceof CriteriaList) {
                CriteriaList originalInnerList = (CriteriaList) originalCriteria;
                CriteriaList deserializedInnerList = (CriteriaList) deserializedCriteria;
                assertEquals(originalInnerList.getCriteriaList().size(), deserializedInnerList.getCriteriaList().size(),
                        "Inner CriteriaList size at index " + i + " should match");

                for (int i1 = 0; i1 < originalInnerList.getCriteriaList().size(); i1++) {
                    Criteria originalInnerCriteria = originalInnerList.getCriteriaList().get(i1);
                    Criteria deserializedInnerCriteria = deserializedInnerList.getCriteriaList().get(i1);
                    assertEquals(originalInnerCriteria.getClass(), deserializedInnerCriteria.getClass(),
                            "Inner Criteria class at index " + i + "," + i1 + " should match");
                    if (originalInnerCriteria instanceof CertSubjectDNAttributeCriteria) {
                        CertSubjectDNAttributeCriteria originalCertCriteria =
                                (CertSubjectDNAttributeCriteria) originalInnerCriteria;
                        CertSubjectDNAttributeCriteria deserializedCertCriteria =
                                (CertSubjectDNAttributeCriteria) deserializedInnerCriteria;
                        assertEquals(originalCertCriteria.getRequiredAttributeIds().size(),
                                deserializedCertCriteria.getRequiredAttributeIds().size(),
                                "RequiredAttributeIds size at index " + i + "," + i1 + " should match");
                        for (int j = 0; j < originalCertCriteria.getRequiredAttributeIds().size(); j++) {
                            assertEquals(originalCertCriteria.getRequiredAttributeIds().get(j),
                                    deserializedCertCriteria.getRequiredAttributeIds().get(j),
                                    "RequiredAttributeId at index " + i + "," + i1 + "," + j + " should match");
                        }
                    } else if (originalInnerCriteria instanceof ExtendedKeyUsageCriteria) {
                        ExtendedKeyUsageCriteria originalExtKeyUsageCriteria =
                                (ExtendedKeyUsageCriteria) originalInnerCriteria;
                        ExtendedKeyUsageCriteria deserializedExtKeyUsageCriteria =
                                (ExtendedKeyUsageCriteria) deserializedInnerCriteria;
                        assertEquals(originalExtKeyUsageCriteria.getRequiredExtendedKeyUsages().size(),
                                deserializedExtKeyUsageCriteria.getRequiredExtendedKeyUsages().size(),
                                "RequiredExtendedKeyUsages size at index " + i + "," + i1 + " should match");
                        for (int j = 0; j < originalExtKeyUsageCriteria.getRequiredExtendedKeyUsages().size(); j++) {
                            assertEquals(originalExtKeyUsageCriteria.getRequiredExtendedKeyUsages().get(j),
                                    deserializedExtKeyUsageCriteria.getRequiredExtendedKeyUsages().get(j),
                                    "RequiredExtendedKeyUsage at index " + i + "," + i1 + "," + j + " should match");
                        }
                    } else if (originalInnerCriteria instanceof PolicySetCriteria) {
                        PolicySetCriteria originalPolicySetCriteria =
                                (PolicySetCriteria) originalInnerCriteria;
                        PolicySetCriteria deserializedPolicySetCriteria =
                                (PolicySetCriteria) deserializedInnerCriteria;
                        assertEquals(originalPolicySetCriteria.getRequiredPolicyIds().size(),
                                deserializedPolicySetCriteria.getRequiredPolicyIds().size(),
                                "RequiredPolicyIds size at index " + i + "," + i1 + " should match");
                        for (int j = 0; j < originalPolicySetCriteria.getRequiredPolicyIds().size(); j++) {
                            assertEquals(originalPolicySetCriteria.getRequiredPolicyIds().get(j),
                                    deserializedPolicySetCriteria.getRequiredPolicyIds().get(j),
                                    "RequiredPolicyId at index " + i + "," + i1 + "," + j + " should match");
                        }
                    } else if (originalInnerCriteria instanceof KeyUsageCriteria) {
                        KeyUsageCriteria originalKeyUsageCriteria =
                                (KeyUsageCriteria) originalInnerCriteria;
                        KeyUsageCriteria deserializedKeyUsageCriteria =
                                (KeyUsageCriteria) deserializedInnerCriteria;
                        assertEquals(originalKeyUsageCriteria.getKeyUsageBits().length,
                                deserializedKeyUsageCriteria.getKeyUsageBits().length,
                                "KeyUsageBits size at index " + i + "," + i1 + " should match");
                        Boolean[] keyUsageBits = originalKeyUsageCriteria.getKeyUsageBits();
                        Boolean[] deserializedKeyUsageBits = deserializedKeyUsageCriteria.getKeyUsageBits();
                        for (int j = 0; j < keyUsageBits.length; j++) {
                            Boolean key = keyUsageBits[j];
                            Boolean deserializedKey = deserializedKeyUsageBits[j];
                            assertEquals(key, deserializedKey,
                                    "KeyUsageBit at index " + i + "," + i1 + "," + j + " should match");
                        }
                    } else {
                        fail("Unexpected Criteria type: " + originalInnerCriteria.getClass().getSimpleName());
                    }
                }
            }
        }

        assertEquals(1, deserializedCountrySpecificCache.getContexts().size(), "Contexts size should match");
        assertNotNull(deserializedContext, "Context should not be null");
        assertEquals(1, deserializedContext.getCertificates().size(),
                "Certificates size should match");
        assertNull(deserializedContext.getCertificates().get(0),
                "Certificate should be null");

        ServiceChronologicalInfo deserializedChronoInfo = deserializedChronoInfos.get(1);
        assertEquals(ogChronologicalInfo.getServiceStatus(), deserializedChronoInfo.getServiceStatus(),
                "ServiceStatus should match");
        assertEquals(ogChronologicalInfo.getServiceStatusStartingTime(),
                deserializedChronoInfo.getServiceStatusStartingTime(),
                "ServiceStatusStartingTime should match");
        assertEquals(1, deserializedChronoInfo.getServiceExtensions().size(), "Extensions size should match");
        AdditionalServiceInformationExtension deserializedExtension = deserializedChronoInfo.getServiceExtensions()
                .get(0);
        assertEquals(ogChronologicalInfoExtension.getUri(), deserializedExtension.getUri(), "URI should match");

    }

    @Test
    public void testCountrySpecificResultsFullServiceContextMultipleCountries() throws IOException {
        CountrySpecificLotlFetcher.Result countrySpecificCache = new CountrySpecificLotlFetcher.Result();
        CountrySpecificLotl be = new CountrySpecificLotl(BE_COUNTRY_CODE, BE_LOTL_URL, APPLICATION_XML);
        ValidationReport report = new ValidationReport();
        ReportItem ogReportItem = new ReportItem(TEST_TYPE, TEST_MESSAGE, ReportItemStatus.INFO);
        report.addReportItem(ogReportItem);
        countrySpecificCache.setCountrySpecificLotl(be);
        countrySpecificCache.setLocalReport(report);
        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(null);
        context.setServiceType(TEST_SERVICE_TYPE);
        ServiceChronologicalInfo ogChronologicalInfo = new ServiceChronologicalInfo(TEST_SERVICE_STATUS,
                TEST_DATE_TIME_1);
        AdditionalServiceInformationExtension ogChronologicalInfoExtension =
                new AdditionalServiceInformationExtension();
        ogChronologicalInfoExtension.setUri(EXTENSION_URI);
        ogChronologicalInfo.addServiceExtension(ogChronologicalInfoExtension);
        context.getServiceChronologicalInfos().add(ogChronologicalInfo);

        CountrySpecificLotlFetcher.Result countrySpecificCache2 = new CountrySpecificLotlFetcher.Result();
        CountrySpecificLotl fr = new CountrySpecificLotl(FR_COUNTRY_CODE, FR_LOTL_URL, APPLICATION_XML);
        ValidationReport report2 = new ValidationReport();
        ReportItem ogReportItem2 = new ReportItem(TEST_TYPE_2, TEST_MESSAGE_2, ReportItemStatus.INFO);
        report2.addReportItem(ogReportItem2);
        countrySpecificCache2.setCountrySpecificLotl(fr);
        countrySpecificCache2.setLocalReport(report2);
        CountryServiceContext context2 = new CountryServiceContext();
        context2.addCertificate(null);
        context2.setServiceType(TEST_SERVICE_TYPE_2);

        context2.getServiceChronologicalInfos()
                .add(new ServiceChronologicalInfo(TEST_SERVICE_STATUS_2, TEST_DATE_TIME_2));

        List<IServiceContext> contexts = new ArrayList<>();
        contexts.add(context);
        countrySpecificCache.setContexts(contexts);
        HashMap<String, CountrySpecificLotlFetcher.Result> map = new HashMap<>();
        map.put(BE_CACHE_KEY, countrySpecificCache);
        map.put(FR_CACHE_KEY, countrySpecificCache2);
        LotlCacheDataV1 original = new LotlCacheDataV1(null, null, null, map, null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);

        byte[] serializedData = bos.toByteArray();
        assertNotNull(serializedData, "Serialized data should not be null");
        AssertUtil.doesNotThrow(() -> {
            LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
        }, "Deserialization should not throw an exception");
    }

    @Test
    public void testTimestampSerialization() throws IOException {
        Map<String, Long> timestamps = new HashMap<>();
        timestamps.put(TIMESTAMP_KEY_1, TIMESTAMP_VALUE_1);
        timestamps.put(TIMESTAMP_KEY_2, TIMESTAMP_VALUE_2);
        LotlCacheDataV1 original = new LotlCacheDataV1(null, null, null, null, timestamps);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);
        byte[] serializedData = bos.toByteArray();
        assertNotNull(serializedData, "Serialized data should not be null");
        LotlCacheDataV1 deserialized = LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
        assertNotNull(deserialized, "Deserialized object should not be null");
        Map<String, Long> deserializedTimestamps = deserialized.getTimeStamps();
        assertNotNull(deserializedTimestamps, "Deserialized timestamps should not be null");
        assertEquals(timestamps.size(), deserializedTimestamps.size(), "Timestamps size should match");
        for (String key : timestamps.keySet()) {
            assertTrue(deserializedTimestamps.containsKey(key), "Deserialized timestamps should contain key: " + key);
            assertEquals(timestamps.get(key), deserializedTimestamps.get(key),
                    "Timestamp value for key " + key + " should match");
        }
    }

    // Helper methods for common test operations
    private byte[] serializeAndGetBytes(LotlCacheDataV1 original) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        original.serialize(bos);
        return bos.toByteArray();
    }

    private LotlCacheDataV1 deserializeFromBytes(byte[] serializedData) {
        return LotlCacheDataV1.deserialize(new ByteArrayInputStream(serializedData));
    }

    private ReportItem createTestReportItem(String type, String message) {
        return new ReportItem(type, message, ReportItemStatus.INFO);
    }

    private ReportItem createTestReportItemWithCause(String type, String message, Exception cause) {
        return new ReportItem(type, message, cause, ReportItemStatus.INFO);
    }

    private void assertReportItemsMatch(ReportItem expected, ReportItem actual) {
        assertEquals(expected.getCheckName(), actual.getCheckName(), "Report item type should match");
        assertEquals(expected.getMessage(), actual.getMessage(), "Report item message should match");
        assertEquals(expected.getStatus(), actual.getStatus(), "Report item status should match");
        assertEquals(expected.getExceptionCause(), actual.getExceptionCause(), "Report item cause should match");
    }
}
