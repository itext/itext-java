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
package com.itextpdf.signatures.validation.lotl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.validation.lotl.PivotFetcher.Result;
import com.itextpdf.signatures.validation.lotl.criteria.CertSubjectDNAttributeCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.Criteria;
import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;
import com.itextpdf.signatures.validation.lotl.criteria.ExtendedKeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.KeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.PolicySetCriteria;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class has a version suffix because it is used for serialization/deserialization of the cache data.
// Future changes to the class that are not backward compatible should result in a new version of the
// class (e.g., LotlCacheDataV2) so that older cache files can still be read.
// Note that the version suffix is only for the class name; it does not affect the serialization format.
class LotlCacheDataV1 {

    private static final String JSON_KEY_BASE64_ENCODED = "base64Encoded";
    private static final String JSON_KEY_REPORT_ITEMS = "reportItems";
    private static final String JSON_KEY_REPORT_ITEM_CHECKNAME = "checkName";
    private static final String JSON_KEY_REPORT_ITEM_MESSAGE = "message";
    private static final String JSON_KEY_REPORT_CAUSE = "exceptionCause";
    private static final String JSON_KEY_REPORT_STATUS = "status";
    private static final String JSON_KEY_CERTIFICATE = "certificate";
    private static final String JSON_KEY_CERTIFICATES = "certificates";
    private static final String JSON_KEY_QUALIFIER_EXTENSIONS = "qualifierExtensions";
    private static final String JSON_KEY_QUALIFIERS = "qualifiers";
    private static final String JSON_KEY_CRITERIA_LIST = "criteriaList";
    private static final String JSON_KEY_CRITERIAS = "criterias";
    private static final String JSON_KEY_CRITERIA_ASSERT_VALUE = "criteriaAssertValue";
    private static final String JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA = "certSubjectDNAttributeCriteria";
    private static final String JSON_KEY_CRITERIA_REQUIRED_ATTRIBUTE_IDS = "requiredAttributeIDs";
    private static final String JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA = "extendedKeyUsageCriteria";
    private static final String JSON_KEY_CRITERIA_REQUIRED_EXTENDED_KEY_USAGES = "requiredExtendedKeyUsages";
    private static final String JSON_KEY_CRITERIA_POLICY_SET_CRITERIA = "policySetCriteria";
    private static final String JSON_KEY_CRITERIA_REQUIRED_POLICY_IDS = "requiredPolicyIDs";
    private static final String JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA = "keyUsageCriteria";
    private static final String JSON_KEY_CRITERIA_KEY_USAGE_BITS = "requiredKeyUsageBits";
    private static final String JSON_KEY_SERVICE_TYPE = "serviceType";
    private static final String JSON_KEY_SERVICE_EXTENSIONS = "serviceExtensions";
    private static final String JSON_KEY_SERVICE_CHRONOLOGICAL_INFOS = "serviceChronologicalInfos";
    private static final String JSON_KEY_SERVICE_STATUS = "serviceStatus";
    private static final String JSON_KEY_SERVICE_STATUS_STARTING_TIME = "serviceStatusStartingTime";
    private static final String JSON_KEY_URI = "uri";
    private static final String JSON_KEY_LOTL_CACHE = "lotlCache";
    private static final String JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE = "europeanResourceFetcherCache";
    private static final String JSON_KEY_PIVOT_CACHE = "pivotCache";
    private static final String JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE = "countrySpecificLotlCache";
    private static final String JSON_KEY_TIME_STAMPS = "timeStamps";
    private static final String JSON_KEY_LOCAL_REPORT = "localReport";
    private static final String JSON_KEY_CURRENTLY_SUPPORTED_PUBLICATION = "currentlySupportedPublication";

    private static final String DATE_TIME_FORMATTER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private EuropeanLotlFetcher.Result lotlCache;
    private EuropeanResourceFetcher.Result europeanResourceFetcherCache;
    private Result pivotCache;
    private Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache;

    private Map<String, Long> timeStamps = new HashMap<>();

    public LotlCacheDataV1() {
        //empty constructor for deserialization
    }

    LotlCacheDataV1(EuropeanLotlFetcher.Result lotlCache, Result pivotCache,
            EuropeanResourceFetcher.Result europeanResourceFetcherCache,
            Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache, Map<String, Long> timeStamps) {
        this.countrySpecificLotlCache = countrySpecificLotlCache;
        this.lotlCache = lotlCache;
        this.europeanResourceFetcherCache = europeanResourceFetcherCache;
        this.pivotCache = pivotCache;
        this.timeStamps = timeStamps;
    }

    public static LotlCacheDataV1 deserialize(InputStream inputStream) {
        try {
            return createObjectMapper().readValue(inputStream, LotlCacheDataV1.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serialize(OutputStream os) throws IOException {
        createObjectMapper().writer().writeValue(os, this);
    }

    public EuropeanResourceFetcher.Result getEuropeanResourceFetcherCache() {
        return europeanResourceFetcherCache;
    }

    public Map<String, Long> getTimeStamps() {
        return timeStamps;
    }

    public Result getPivotCache() {
        return pivotCache;
    }

    public Map<String, CountrySpecificLotlFetcher.Result> getCountrySpecificLotlCache() {
        return countrySpecificLotlCache;
    }

    public EuropeanLotlFetcher.Result getLotlCache() {
        return lotlCache;
    }

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(Feature.AUTO_CLOSE_TARGET);
        objectMapper.writer(new MinimalPrettyPrinter());

        SimpleModule module = new SimpleModule();
        module.addSerializer(X509Certificate.class, new X509CertificateSerializer());
        module.addSerializer(ValidationReport.class, new ValidationReportSerializer());
        module.addSerializer(EuropeanResourceFetcher.Result.class, new EuropeanResultFetcherSerializer());
        module.addSerializer(LotlCacheDataV1.class, new LotlCacheDataV1Serializer());
        module.addSerializer(ServiceChronologicalInfo.class, new ServiceChronologicalInfoSerializer());

        objectMapper.configOverride(CountryServiceContext.class)
                .setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("currentChronologicalInfo"));

        module.addDeserializer(Certificate.class, new X509CertificateDeserializer());
        module.addDeserializer(X509Certificate.class, new X509CertificateDeserializer());
        module.addDeserializer(ValidationReport.class, new ValidationReportDeserializer());
        module.addDeserializer(ReportItem.class, new ReportItemDeserializer());
        module.addDeserializer(IServiceContext.class, new IServiceContextDeserializer());
        module.addDeserializer(ServiceChronologicalInfo.class, new ServiceChronologicalInfoDeserializer());

        objectMapper.registerModule(module);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    static class LotlCacheDataV1Serializer extends JsonSerializer<LotlCacheDataV1> {
        public LotlCacheDataV1Serializer() {
            //empty constructor
        }

        @Override
        public void serialize(LotlCacheDataV1 lotlCacheData, JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeObjectField(JSON_KEY_LOTL_CACHE, lotlCacheData.getLotlCache());
            jsonGenerator.writeObjectField(JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE,
                    lotlCacheData.getEuropeanResourceFetcherCache());
            jsonGenerator.writeObjectField(JSON_KEY_PIVOT_CACHE, lotlCacheData.getPivotCache());

            if (lotlCacheData.getCountrySpecificLotlCache() != null) {
                jsonGenerator.writeObjectFieldStart(JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE);
                List<String> keys = new ArrayList<>(lotlCacheData.getCountrySpecificLotlCache().keySet());
                //sort the keys
                Collections.sort(keys);
                for (String key : keys) {
                    CountrySpecificLotlFetcher.Result entry = lotlCacheData.getCountrySpecificLotlCache().get(key);
                    jsonGenerator.writeObjectField(key, entry);
                }
                jsonGenerator.writeEndObject();
            } else {
                jsonGenerator.writeNullField(JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE);
            }

            if (lotlCacheData.getTimeStamps() != null) {
                jsonGenerator.writeObjectFieldStart(JSON_KEY_TIME_STAMPS);
                for (Map.Entry<String, Long> entry : lotlCacheData.getTimeStamps().entrySet()) {
                    jsonGenerator.writeNumberField(entry.getKey(), entry.getValue());
                }
                jsonGenerator.writeEndObject();
            } else {
                jsonGenerator.writeNullField(JSON_KEY_TIME_STAMPS);
            }
            jsonGenerator.writeEndObject();
        }
    }

    static class X509CertificateDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<X509Certificate> {
        public X509CertificateDeserializer() {
            //empty constructor
        }

        @Override
        public X509Certificate deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);
            String base64Encoded = node.get(JSON_KEY_BASE64_ENCODED).asText();
            byte[] decoded = Base64.getDecoder().decode(base64Encoded);
            try {
                return (X509Certificate) CertificateUtil.generateCertificate(new ByteArrayInputStream(decoded));
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class ValidationReportDeserializer extends JsonDeserializer<ValidationReport> {
        public ValidationReportDeserializer() {
            //empty constructor
        }

        @Override
        public ValidationReport deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);

            ValidationReport report = new ValidationReport();

            JsonNode itemsNode = node.get(JSON_KEY_REPORT_ITEMS);
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    ReportItem item = codec.treeToValue(itemNode, ReportItem.class);
                    report.addReportItem(item);
                }
            }
            return report;
        }
    }

    static class ReportItemDeserializer extends JsonDeserializer<ReportItem> {
        public ReportItemDeserializer() {
            //empty constructor
        }

        @Override
        public ReportItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);

            String checkName =
                    node.has(JSON_KEY_REPORT_ITEM_CHECKNAME) ? node.get(JSON_KEY_REPORT_ITEM_CHECKNAME).asText() : null;
            String message =
                    node.has(JSON_KEY_REPORT_ITEM_MESSAGE) ? node.get(JSON_KEY_REPORT_ITEM_MESSAGE).asText() : null;
            ReportItem.ReportItemStatus status = node.has(JSON_KEY_REPORT_STATUS) ? ReportItem.ReportItemStatus.valueOf(
                    node.get(JSON_KEY_REPORT_STATUS).asText()) : null;

            Exception cause = null;
            if (node.has(JSON_KEY_REPORT_CAUSE) && !node.get(JSON_KEY_REPORT_CAUSE).isNull()) {
                JsonNode causeNode = node.get(JSON_KEY_REPORT_CAUSE);
                cause = codec.treeToValue(causeNode, Exception.class);
            }

            ReportItem item;
            if (node.has(JSON_KEY_CERTIFICATE) && !node.get(JSON_KEY_CERTIFICATE).isNull()) {
                X509Certificate certificate = codec.treeToValue(node.get(JSON_KEY_CERTIFICATE), X509Certificate.class);
                item = new CertificateReportItem(certificate, checkName, message, cause, status);
            } else {
                item = new ReportItem(checkName, message, cause, status);
            }

            return item;
        }
    }

    static class ServiceChronologicalInfoSerializer extends JsonSerializer<ServiceChronologicalInfo> {
        public ServiceChronologicalInfoSerializer() {
            //empty constructor
        }

        @Override
        public void serialize(ServiceChronologicalInfo info, JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(JSON_KEY_SERVICE_STATUS, info.getServiceStatus());
            if (info.getServiceStatusStartingTime() != null) {
                jsonGenerator.writeStringField(JSON_KEY_SERVICE_STATUS_STARTING_TIME,
                        info.getServiceStatusStartingTime()
                                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN)));
            } else {
                jsonGenerator.writeNullField(JSON_KEY_SERVICE_STATUS_STARTING_TIME);
            }

            if (info.getServiceExtensions() != null) {
                jsonGenerator.writeArrayFieldStart(JSON_KEY_SERVICE_EXTENSIONS);
                for (AdditionalServiceInformationExtension extension : info.getServiceExtensions()) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField(JSON_KEY_URI, extension.getUri());
                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndArray();
            } else {
                jsonGenerator.writeNullField(JSON_KEY_SERVICE_EXTENSIONS);
            }

            // Qualifier extensions
            jsonGenerator.writeArrayFieldStart(JSON_KEY_QUALIFIER_EXTENSIONS);
            for (QualifierExtension extension : info.getQualifierExtensions()) {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeArrayFieldStart(JSON_KEY_QUALIFIERS);
                for (String qualifier : extension.getQualifiers()) {
                    jsonGenerator.writeString(qualifier);
                }
                jsonGenerator.writeEndArray();

                serializeCriteriaList(extension.getCriteriaList(), jsonGenerator);

                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }

        private static void serializeCriteriaList(CriteriaList criteriaList, JsonGenerator jsonGenerator)
                throws IOException {
            if (criteriaList == null) {
                return;
            }
            jsonGenerator.writeFieldName(JSON_KEY_CRITERIA_LIST);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(JSON_KEY_CRITERIA_ASSERT_VALUE, criteriaList.getAssertValue());

            jsonGenerator.writeArrayFieldStart(JSON_KEY_CRITERIAS);
            for (Criteria criteria : criteriaList.getCriteriaList()) {
                jsonGenerator.writeStartObject();

                if (criteria instanceof CriteriaList) {
                    serializeCriteriaList((CriteriaList) criteria, jsonGenerator);
                } else if (criteria instanceof CertSubjectDNAttributeCriteria) {
                    writeStringList(JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA,
                            JSON_KEY_CRITERIA_REQUIRED_ATTRIBUTE_IDS,
                            ((CertSubjectDNAttributeCriteria) criteria).getRequiredAttributeIds(), jsonGenerator);
                } else if (criteria instanceof ExtendedKeyUsageCriteria) {
                    writeStringList(JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA,
                            JSON_KEY_CRITERIA_REQUIRED_EXTENDED_KEY_USAGES,
                            ((ExtendedKeyUsageCriteria) criteria).getRequiredExtendedKeyUsages(), jsonGenerator);
                } else if (criteria instanceof KeyUsageCriteria) {
                    List<String> keyUsages = new ArrayList<>();
                    for (Boolean keyUsage : ((KeyUsageCriteria) criteria).getKeyUsageBits()) {
                        if (keyUsage == null) {
                            keyUsages.add("null");
                        } else {
                            keyUsages.add(keyUsage ? "true" : "false");
                        }
                    }

                    writeStringList(JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA,
                            JSON_KEY_CRITERIA_KEY_USAGE_BITS, keyUsages, jsonGenerator);

                } else if (criteria instanceof PolicySetCriteria) {
                    writeStringList(JSON_KEY_CRITERIA_POLICY_SET_CRITERIA,
                            JSON_KEY_CRITERIA_REQUIRED_POLICY_IDS,
                            ((PolicySetCriteria) criteria).getRequiredPolicyIds(), jsonGenerator);
                }

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }

        private static void writeStringList(String parentFieldName, String arrayFieldName, List<String> stringList,
                JsonGenerator jsonGenerator)
                throws IOException {
            jsonGenerator.writeFieldName(parentFieldName);
            jsonGenerator.writeStartObject();

            jsonGenerator.writeArrayFieldStart(arrayFieldName);
            for (String keyUsage : stringList) {
                jsonGenerator.writeString(keyUsage);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }
    }

    static class EuropeanResultFetcherSerializer extends JsonSerializer<EuropeanResourceFetcher.Result> {
        public EuropeanResultFetcherSerializer() {
            //empty constructor
        }

        @Override
        public void serialize(EuropeanResourceFetcher.Result result, JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeObjectField(JSON_KEY_LOCAL_REPORT, result.getLocalReport());

            jsonGenerator.writeArrayFieldStart(JSON_KEY_CERTIFICATES);

            for (Certificate certificate : result.getCertificates()) {
                jsonGenerator.writeObject(certificate);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeStringField(JSON_KEY_CURRENTLY_SUPPORTED_PUBLICATION,
                    result.getCurrentlySupportedPublication());
            jsonGenerator.writeEndObject();
        }
    }

    static class ValidationReportSerializer extends JsonSerializer<ValidationReport> {
        public ValidationReportSerializer() {
            //empty constructor
        }

        @Override
        public void serialize(ValidationReport report, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStartObject();
            gen.writeArrayFieldStart(JSON_KEY_REPORT_ITEMS);

            List<ReportItem> sortedItems = new ArrayList<>(report.getLogs());
            // Sort the items by check name to ensure consistent order
            sortedItems.sort((item1, item2) -> {
                if (item1.getCheckName() == null && item2.getCheckName() == null) {
                    return 0;
                } else if (item1.getCheckName() == null) {
                    return -1;
                } else if (item2.getCheckName() == null) {
                    return 1;
                } else {
                    return item1.getCheckName().compareTo(item2.getCheckName());
                }
            });

            for (ReportItem item : sortedItems) {
                gen.writeObject(item);
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    static class X509CertificateSerializer extends JsonSerializer<X509Certificate> {
        public X509CertificateSerializer() {
            //empty constructor
        }

        @Override
        public void serialize(X509Certificate cert, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStartObject();
            byte[] encoded;
            try {
                encoded = cert.getEncoded();
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
            String base64Encoded = Base64.getEncoder().encodeToString(encoded);
            gen.writeStringField(JSON_KEY_BASE64_ENCODED, base64Encoded);
            gen.writeEndObject();
        }
    }

    static class IServiceContextDeserializer extends JsonDeserializer<IServiceContext> {
        public IServiceContextDeserializer() {
            //empty constructor
        }

        @Override
        public IServiceContext deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);
            List<Certificate> certificates = new ArrayList<>();
            JsonNode certsNode = node.get(JSON_KEY_CERTIFICATES);
            if (certsNode != null && certsNode.isArray()) {
                for (JsonNode certNode : certsNode) {
                    X509Certificate cert = codec.treeToValue(certNode, X509Certificate.class);
                    certificates.add(cert);
                }
            }
            //if it contains a JSON_KEY_SERVICE_TYPE field, it is a CountryServiceContext
            if (node.has(JSON_KEY_SERVICE_TYPE)) {
                CountryServiceContext context = new CountryServiceContext();
                ArrayList<ServiceChronologicalInfo> infos = new ArrayList<>();
                JsonNode infosNode = node.get(JSON_KEY_SERVICE_CHRONOLOGICAL_INFOS);
                if (infosNode != null && infosNode.isArray()) {
                    for (JsonNode infoNode : infosNode) {
                        ServiceChronologicalInfo info = codec.treeToValue(infoNode, ServiceChronologicalInfo.class);
                        infos.add(info);
                    }
                }
                for (Certificate certificate : certificates) {
                    context.addCertificate(certificate);
                }
                context.getServiceChronologicalInfos().addAll(infos);
                context.setServiceType(node.get(JSON_KEY_SERVICE_TYPE).asText());

                return context;

            }
            SimpleServiceContext context = new SimpleServiceContext();
            for (Certificate certificate : certificates) {
                context.addCertificate(certificate);
            }
            return context;
        }
    }

    static class ServiceChronologicalInfoDeserializer extends JsonDeserializer<ServiceChronologicalInfo> {
        public ServiceChronologicalInfoDeserializer() {
            //empty constructor
        }

        @Override
        public ServiceChronologicalInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);

            String serviceStatus =
                    node.has(JSON_KEY_SERVICE_STATUS) ? node.get(JSON_KEY_SERVICE_STATUS).asText() : null;
            LocalDateTime serviceStatusStartingTime =
                    node.has(JSON_KEY_SERVICE_STATUS_STARTING_TIME) ? LocalDateTime.parse(
                            node.get(JSON_KEY_SERVICE_STATUS_STARTING_TIME).asText().split("\\.")[0],
                            DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_PATTERN)) : null;
            ServiceChronologicalInfo info = new ServiceChronologicalInfo();
            info.setServiceStatus(serviceStatus);
            info.setServiceStatusStartingTime(serviceStatusStartingTime);

            if (node.has(JSON_KEY_SERVICE_EXTENSIONS)) {
                JsonNode extensionsNode = node.get(JSON_KEY_SERVICE_EXTENSIONS);
                if (extensionsNode != null && extensionsNode.isArray()) {
                    for (JsonNode extensionNode : extensionsNode) {
                        AdditionalServiceInformationExtension extension = codec.treeToValue(extensionNode,
                                AdditionalServiceInformationExtension.class);
                        info.getServiceExtensions().add(extension);
                    }
                }
            }

            if (node.has(JSON_KEY_QUALIFIER_EXTENSIONS)) {
                JsonNode extensionsNode = node.get(JSON_KEY_QUALIFIER_EXTENSIONS);
                if (extensionsNode != null && extensionsNode.isArray()) {
                    for (JsonNode extensionNode : extensionsNode) {
                        JsonNode qualifiersNode = extensionNode.get(JSON_KEY_QUALIFIERS);
                        if (qualifiersNode == null || !qualifiersNode.isArray()) {
                            continue;
                        }

                        QualifierExtension qualifierExtension = new QualifierExtension();
                        for (JsonNode qualifierNode : qualifiersNode) {
                            if (qualifierNode != null && qualifierNode.isTextual()) {
                                qualifierExtension.addQualifier(qualifierNode.asText());
                            }
                        }

                        JsonNode criteriaListNode = extensionNode.get(JSON_KEY_CRITERIA_LIST);
                        if (criteriaListNode == null) {
                            continue;
                        }

                        CriteriaList criteriaList = readCriteriaListNode(criteriaListNode);
                        if (criteriaList == null) {
                            continue;
                        }

                        qualifierExtension.setCriteriaList(criteriaList);
                        info.addQualifierExtension(qualifierExtension);
                    }
                }
            }

            return info;
        }

        private static CriteriaList readCriteriaListNode(JsonNode criteriaListNode) {
            JsonNode assertValueNode = criteriaListNode.get(JSON_KEY_CRITERIA_ASSERT_VALUE);
            if (assertValueNode == null || !assertValueNode.isTextual()) {
                return null;
            }

            CriteriaList criteriaList = new CriteriaList(assertValueNode.asText());

            JsonNode criteriasNode = criteriaListNode.get(JSON_KEY_CRITERIAS);
            if (criteriasNode == null || !criteriasNode.isArray()) {
                return null;
            }

            for (JsonNode criteriaNode : criteriasNode) {
                JsonNode citeriaNode = criteriaNode.get(JSON_KEY_CRITERIA_LIST);
                if (citeriaNode != null ) {
                    Criteria innerCriteriaList = readCriteriaListNode(citeriaNode);
                    if (innerCriteriaList != null) {
                        criteriaList.addCriteria(innerCriteriaList);
                    }
                    continue;
                }

                citeriaNode = criteriaNode.get(JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA);
                if (citeriaNode != null) {
                    CertSubjectDNAttributeCriteria criteria = new CertSubjectDNAttributeCriteria();
                    JsonNode requiredAttributeIDsNode = citeriaNode.get(JSON_KEY_CRITERIA_REQUIRED_ATTRIBUTE_IDS);
                    if (requiredAttributeIDsNode != null && requiredAttributeIDsNode.isArray()) {
                        for (JsonNode attributeIDNode : requiredAttributeIDsNode) {
                            criteria.addRequiredAttributeId(attributeIDNode.asText());
                        }
                    }
                    criteriaList.addCriteria(criteria);
                    continue;
                }
                citeriaNode = criteriaNode.get(JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA);
                if (citeriaNode != null) {
                    ExtendedKeyUsageCriteria criteria = new ExtendedKeyUsageCriteria();
                    JsonNode requiredExtendedKeyUsagesNode = citeriaNode.get(
                            JSON_KEY_CRITERIA_REQUIRED_EXTENDED_KEY_USAGES);
                    if (requiredExtendedKeyUsagesNode != null && requiredExtendedKeyUsagesNode.isArray()) {
                        for (JsonNode extendedKeyUsageNode : requiredExtendedKeyUsagesNode) {
                            criteria.addRequiredExtendedKeyUsage(extendedKeyUsageNode.asText());
                        }
                    }
                    criteriaList.addCriteria(criteria);
                    continue;
                }

                citeriaNode = criteriaNode.get(JSON_KEY_CRITERIA_POLICY_SET_CRITERIA);
                if (citeriaNode != null) {
                    PolicySetCriteria criteria = new PolicySetCriteria();
                    JsonNode requiredPolicyIDsNode = citeriaNode.get(JSON_KEY_CRITERIA_REQUIRED_POLICY_IDS);
                    if (requiredPolicyIDsNode != null && requiredPolicyIDsNode.isArray()) {
                        for (JsonNode policyIDNode : requiredPolicyIDsNode) {
                            criteria.addRequiredPolicyId(policyIDNode.asText());
                        }
                    }
                    criteriaList.addCriteria(criteria);
                    continue;
                }

                citeriaNode = criteriaNode.get(JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA);
                if (citeriaNode != null) {
                    KeyUsageCriteria criteria = new KeyUsageCriteria();
                    JsonNode requiredKeyUsageBitsNode = citeriaNode.get(JSON_KEY_CRITERIA_KEY_USAGE_BITS);
                    if (requiredKeyUsageBitsNode != null && requiredKeyUsageBitsNode.isArray()) {
                        int counter = 0;
                        for (JsonNode keyUsageBitNode : requiredKeyUsageBitsNode) {
                            String text = keyUsageBitNode.asText();
                            criteria.getKeyUsageBits()[counter] =
                                    "null".equals(text) ? null : Boolean.parseBoolean(text);
                            counter++;
                        }
                    }
                    criteriaList.addCriteria(criteria);
                    continue;
                }
                throw new RuntimeException("Unknown criteria type in JSON");

            }
            return criteriaList;
        }
    }
}
