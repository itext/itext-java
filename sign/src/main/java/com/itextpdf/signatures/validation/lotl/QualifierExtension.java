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

import com.itextpdf.commons.json.IJsonSerializable;
import com.itextpdf.commons.json.JsonArray;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonString;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.validation.lotl.criteria.CriteriaList;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing Qualifications entry from a country specific Trusted List.
 */
public class QualifierExtension implements IJsonSerializable {
    private static final String JSON_KEY_QUALIFIERS = "qualifiers";
    private static final String JSON_KEY_CRITERIA_LIST = "criteriaList";

    private final List<String> qualifiers = new ArrayList<>();
    private CriteriaList criteriaList;

    QualifierExtension() {
    }

    /**
     * Gets list of qualifiers from this extension.
     *
     * @return list of qualifiers
     */
    public List<String> getQualifiers() {
        return Collections.unmodifiableList(qualifiers);
    }

    /**
     * Checks criteria for this Qualifier extension.
     *
     * @param certificate {@link X509Certificate} for which criteria shall be meet
     *
     * @return {@code true} if criteria were meet, {@code false} otherwise
     */
    public boolean checkCriteria(X509Certificate certificate) {
        return criteriaList.checkCriteria(certificate);
    }

    void setCriteriaList(CriteriaList criteriaList) {
        this.criteriaList = criteriaList;
    }

    CriteriaList getCriteriaList() {
        return criteriaList;
    }

    void addQualifier(String qualifier) {
        this.qualifiers.add(qualifier);
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObject qualifiersJson = new JsonObject();
        qualifiersJson.add(JSON_KEY_QUALIFIERS, new JsonArray(getQualifiers().stream().map(
                qualifier -> (JsonValue) new JsonString(qualifier)).collect(Collectors.toList())));

        if (getCriteriaList() != null) {
            qualifiersJson.add(JSON_KEY_CRITERIA_LIST, getCriteriaList().toJson());
        }
        return qualifiersJson;
    }

    /**
     * Deserializes {@link JsonValue} into {@link QualifierExtension}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link QualifierExtension}
     */
    public static QualifierExtension fromJson(JsonValue jsonValue) {
        QualifierExtension qualifierExtensionFromJson = new QualifierExtension();
        JsonObject qualifierExtensionJsonObject = (JsonObject) jsonValue;
        JsonArray qualifiersArray = (JsonArray) qualifierExtensionJsonObject.getField(JSON_KEY_QUALIFIERS);
        List<String> qualifiersFromJson = qualifiersArray.getValues().stream().map(
                qualifierJson -> ((JsonString) qualifierJson).getValue()).collect(Collectors.toList());
        for (String qualifierFromJson : qualifiersFromJson) {
            qualifierExtensionFromJson.addQualifier(qualifierFromJson);
        }

        JsonObject criteriaListJson = (JsonObject) qualifierExtensionJsonObject.getField(JSON_KEY_CRITERIA_LIST);
        if (criteriaListJson != null) {
            qualifierExtensionFromJson.setCriteriaList(CriteriaList.fromJson(criteriaListJson));
        }
        return qualifierExtensionFromJson;
    }
}
