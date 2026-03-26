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
package com.itextpdf.signatures;

import com.itextpdf.commons.json.JsonArray;
import com.itextpdf.commons.json.JsonNull;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonString;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.commons.utils.EncodingUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.validation.lotl.criteria.CertSubjectDNAttributeCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.Criteria;
import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;
import com.itextpdf.signatures.validation.lotl.criteria.ExtendedKeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.KeyUsageCriteria;
import com.itextpdf.signatures.validation.lotl.criteria.PolicySetCriteria;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for JSON AST serialization/deserialization.
 */
public final class SignJsonSerializerHelper {
    private static final String JSON_KEY_BASE64_ENCODED = "base64Encoded";
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

    private SignJsonSerializerHelper() {
        // Empty.
    }

    /**
     * Serializes {@link Certificate} object to JSON AST.
     *
     * @param certificate {@link Certificate} object to serialize
     *
     * @return serialized certificate as JSON AST
     */
    public static JsonValue serializeCertificate(Certificate certificate) {
        if (certificate == null) {
            return JsonNull.JSON_NULL;
        }
        byte[] encoded;
        try {
            encoded = certificate.getEncoded();
        } catch (Exception e) {
            throw new PdfException(e);
        }
        String base64Encoded = EncodingUtil.toBase64(encoded);
        JsonObject certificateJson = new JsonObject();
        certificateJson.add(JSON_KEY_BASE64_ENCODED, new JsonString(base64Encoded));
        return certificateJson;
    }

    /**
     * Deserializes JSON AST object into certificate.
     *
     * @param certificateJson {@link JsonValue} JSON AST to deserialize
     *
     * @return {@link X509Certificate} deserialized certificate
     */
    public static X509Certificate deserializeCertificate(JsonValue certificateJson) {
        if (certificateJson == JsonNull.JSON_NULL) {
            return null;
        }
        String base64Encoded = ((JsonString) ((JsonObject) certificateJson).getField(JSON_KEY_BASE64_ENCODED))
                .getValue();
        byte[] decoded = EncodingUtil.fromBase64(base64Encoded);
        try {
            return (X509Certificate) CertificateUtil.generateCertificate(new ByteArrayInputStream(decoded));
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Serializes {@link CriteriaList} object to JSON AST.
     *
     * @param criteriaList {@link CriteriaList} to serialize
     *
     * @return serialized {@link CriteriaList} as JSON AST
     */
    public static JsonValue serializeCriteriaList(CriteriaList criteriaList) {
        JsonObject criteriaListJson = new JsonObject();
        criteriaListJson.add(JSON_KEY_CRITERIA_ASSERT_VALUE,
                new JsonString(criteriaList.getAssertValue()));

        JsonArray criteriasJson = new JsonArray();
        for (Criteria criteria : criteriaList.getCriteriaList()) {
            if (criteria instanceof CriteriaList) {
                JsonObject innerCriteriaListJson = new JsonObject();
                innerCriteriaListJson.add(JSON_KEY_CRITERIA_LIST,
                        serializeCriteriaList((CriteriaList) criteria));
                criteriasJson.add(innerCriteriaListJson);
            } else if (criteria instanceof CertSubjectDNAttributeCriteria) {
                JsonObject certSubjectDNAttributeCriteriaJson = new JsonObject();
                JsonObject criteriaRequiredAttributeIdsJson = new JsonObject();
                criteriaRequiredAttributeIdsJson.add(JSON_KEY_CRITERIA_REQUIRED_ATTRIBUTE_IDS,
                        new JsonArray(((CertSubjectDNAttributeCriteria) criteria).getRequiredAttributeIds().stream()
                                .map(requiredAttributeId -> (JsonValue) new JsonString(requiredAttributeId))
                                .collect(Collectors.toList())));
                certSubjectDNAttributeCriteriaJson.add(JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA,
                        criteriaRequiredAttributeIdsJson);
                criteriasJson.add(certSubjectDNAttributeCriteriaJson);
            } else if (criteria instanceof ExtendedKeyUsageCriteria) {
                JsonObject extendedKeyUsageCriteriaJson = new JsonObject();
                JsonObject criteriaRequiredExtendedKeyUsageJson = new JsonObject();
                criteriaRequiredExtendedKeyUsageJson.add(JSON_KEY_CRITERIA_REQUIRED_EXTENDED_KEY_USAGES,
                        new JsonArray(((ExtendedKeyUsageCriteria) criteria).getRequiredExtendedKeyUsages().stream()
                                .map(requiredExtendedKeyUsage -> (JsonValue) new JsonString(requiredExtendedKeyUsage))
                                .collect(Collectors.toList())));
                extendedKeyUsageCriteriaJson.add(JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA,
                        criteriaRequiredExtendedKeyUsageJson);
                criteriasJson.add(extendedKeyUsageCriteriaJson);
            } else if (criteria instanceof KeyUsageCriteria) {
                List<String> keyUsages = new ArrayList<>();
                for (Boolean keyUsage : ((KeyUsageCriteria) criteria).getKeyUsageBits()) {
                    if (keyUsage == null) {
                        keyUsages.add("null");
                    } else {
                        keyUsages.add((boolean) keyUsage ? "true" : "false");
                    }
                }

                JsonObject keyUsageCriteriaJson = new JsonObject();
                JsonObject criteriaRequiredKeyUsageJson = new JsonObject();
                criteriaRequiredKeyUsageJson.add(JSON_KEY_CRITERIA_KEY_USAGE_BITS,
                        new JsonArray(keyUsages.stream().map(
                                requiredExtendedKeyUsage -> (JsonValue) new JsonString(requiredExtendedKeyUsage))
                                .collect(Collectors.toList())));
                keyUsageCriteriaJson.add(JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA,
                        criteriaRequiredKeyUsageJson);
                criteriasJson.add(keyUsageCriteriaJson);
            } else if (criteria instanceof PolicySetCriteria) {
                JsonObject policySetCriteriaJson = new JsonObject();
                JsonObject criteriaRequiredPolicyIdsJson = new JsonObject();
                criteriaRequiredPolicyIdsJson.add(JSON_KEY_CRITERIA_REQUIRED_POLICY_IDS,
                        new JsonArray(((PolicySetCriteria) criteria).getRequiredPolicyIds().stream()
                                .map(requiredPolicyId -> (JsonValue) new JsonString(requiredPolicyId))
                                .collect(Collectors.toList())));
                policySetCriteriaJson.add(JSON_KEY_CRITERIA_POLICY_SET_CRITERIA,
                        criteriaRequiredPolicyIdsJson);
                criteriasJson.add(policySetCriteriaJson);
            }
        }
        criteriaListJson.add(JSON_KEY_CRITERIAS, criteriasJson);
        return criteriaListJson;
    }

    /**
     * Deserializes JSON AST object in {@link CriteriaList}.
     *
     * @param criteriaListJson {@link JsonObject} to create {@link CriteriaList} from
     *
     * @return deserialized {@link CriteriaList}
     */
    public static CriteriaList deserializeCriteriaList(JsonObject criteriaListJson) {
        JsonString assertValueJson = (JsonString) criteriaListJson.getField(JSON_KEY_CRITERIA_ASSERT_VALUE);

        CriteriaList criteriaList = new CriteriaList(assertValueJson.getValue());

        JsonArray criteriasJson = (JsonArray) criteriaListJson.getField(JSON_KEY_CRITERIAS);

        for (JsonValue criteriaJson : criteriasJson.getValues()) {
            JsonObject criteriaJsonObject = (JsonObject) criteriaJson;
            if (criteriaJsonObject.getField(JSON_KEY_CRITERIA_LIST) != null) {
                Criteria innerCriteriaList = deserializeCriteriaList(
                        (JsonObject) criteriaJsonObject.getField(JSON_KEY_CRITERIA_LIST));
                criteriaList.addCriteria(innerCriteriaList);
                continue;
            }

            if (criteriaJsonObject.getField(JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA) != null) {
                CertSubjectDNAttributeCriteria criteriaFromJson = new CertSubjectDNAttributeCriteria();
                JsonArray requiredAttributeIDsJson = (JsonArray) ((JsonObject) criteriaJsonObject.getField(
                        JSON_KEY_CRITERIA_CERT_SUBJECT_DN_ATTRIBUTE_CRITERIA))
                        .getField(JSON_KEY_CRITERIA_REQUIRED_ATTRIBUTE_IDS);
                for (JsonValue attributeIDJson : requiredAttributeIDsJson.getValues()) {
                    criteriaFromJson.addRequiredAttributeId(((JsonString) attributeIDJson).getValue());
                }
                criteriaList.addCriteria(criteriaFromJson);
                continue;
            }

            if (criteriaJsonObject.getField(JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA) != null) {
                ExtendedKeyUsageCriteria criteriaFromJson = new ExtendedKeyUsageCriteria();
                JsonArray requiredExtendedKeyUsagesJson = (JsonArray) ((JsonObject) criteriaJsonObject.getField(
                        JSON_KEY_CRITERIA_EXTENDED_KEY_USAGE_CRITERIA))
                        .getField(JSON_KEY_CRITERIA_REQUIRED_EXTENDED_KEY_USAGES);
                for (JsonValue extendedKeyUsageJson : requiredExtendedKeyUsagesJson.getValues()) {
                    criteriaFromJson.addRequiredExtendedKeyUsage(((JsonString) extendedKeyUsageJson).getValue());
                }
                criteriaList.addCriteria(criteriaFromJson);
                continue;
            }

            if (criteriaJsonObject.getField(JSON_KEY_CRITERIA_POLICY_SET_CRITERIA) != null) {
                PolicySetCriteria criteriaFromJson = new PolicySetCriteria();
                JsonArray requiredPolicyIDsJson = (JsonArray) ((JsonObject) criteriaJsonObject.getField(
                        JSON_KEY_CRITERIA_POLICY_SET_CRITERIA))
                        .getField(JSON_KEY_CRITERIA_REQUIRED_POLICY_IDS);
                for (JsonValue policyIdJson : requiredPolicyIDsJson.getValues()) {
                    criteriaFromJson.addRequiredPolicyId(((JsonString) policyIdJson).getValue());
                }
                criteriaList.addCriteria(criteriaFromJson);
                continue;
            }

            if (criteriaJsonObject.getField(JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA) != null) {
                KeyUsageCriteria criteriaFromJson = new KeyUsageCriteria();
                JsonArray requiredKeyUsageBitsJson = (JsonArray) ((JsonObject) criteriaJsonObject.getField(
                        JSON_KEY_CRITERIA_KEY_USAGE_CRITERIA))
                        .getField(JSON_KEY_CRITERIA_KEY_USAGE_BITS);
                int counter = 0;
                for (JsonValue keyUsageBitsJson : requiredKeyUsageBitsJson.getValues()) {
                    String text = ((JsonString) keyUsageBitsJson).getValue();
                    criteriaFromJson.getKeyUsageBits()[counter] =
                            "null".equals(text) ? null : (Boolean) "true".equals(text);
                    counter++;
                }
                criteriaList.addCriteria(criteriaFromJson);
            }
        }
        return criteriaList;
    }
}
