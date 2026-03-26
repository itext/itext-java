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
import com.itextpdf.commons.json.JsonNull;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonString;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.TimestampConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class representing ServiceHistory entry in a country specific Trusted List.
 */
public class ServiceChronologicalInfo implements IJsonSerializable {
    static final String GRANTED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/granted";
    static final String GRANTED_NATIONALLY =
            "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/recognisedatnationallevel";
    static final String ACCREDITED = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/accredited";
    static final String SET_BY_NATIONAL_LAW = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/setbynationallaw";
    static final String UNDER_SUPERVISION = "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/undersupervision";
    static final String SUPERVISION_IN_CESSATION =
            "http://uri.etsi.org/TrstSvc/TrustedList/Svcstatus/supervisionincessation";
    private static final Set<String> VALID_STATUSES = new HashSet<>();
    private static final String JSON_KEY_QUALIFIER_EXTENSIONS = "qualifierExtensions";
    private static final String JSON_KEY_SERVICE_EXTENSIONS = "serviceExtensions";
    private static final String JSON_KEY_SERVICE_STATUS = "serviceStatus";
    private static final String JSON_KEY_SERVICE_STATUS_STARTING_TIME = "serviceStatusStartingTime";

    private final List<AdditionalServiceInformationExtension> serviceExtensions = new ArrayList<>();
    private final List<QualifierExtension> qualifierExtensions = new ArrayList<>();

    private static final List<String> statusDateFormats = Arrays.asList(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss"
    );
    private String serviceStatus;
    //Local time is used here because it is required to use UTC in a trusted lists, so no offset shall be presented.
    private LocalDateTime serviceStatusStartingTime;

    static {
        VALID_STATUSES.add(GRANTED);
        VALID_STATUSES.add(GRANTED_NATIONALLY);
        VALID_STATUSES.add(ACCREDITED);
        VALID_STATUSES.add(SET_BY_NATIONAL_LAW);
        VALID_STATUSES.add(UNDER_SUPERVISION);
        VALID_STATUSES.add(SUPERVISION_IN_CESSATION);
    }

    ServiceChronologicalInfo() {
        // empty constructor
    }

    ServiceChronologicalInfo(String serviceStatus, LocalDateTime serviceStatusStartingTime) {
        this.serviceStatus = serviceStatus;
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    /**
     * Gets service status corresponding to this Service Chronological Info instance.
     *
     * @return service status
     */
    public String getServiceStatus() {
        return serviceStatus;
    }

    /**
     * Gets service status starting time corresponding to this Service Chronological Info instance.
     *
     * @return service status starting time
     */
    public LocalDateTime getServiceStatusStartingTime() {
        return serviceStatusStartingTime;
    }

    /**
     * Gets list of {@link AdditionalServiceInformationExtension} corresponding to this Service Chronological Info.
     *
     * @return list of {@link AdditionalServiceInformationExtension}
     */
    public List<AdditionalServiceInformationExtension> getServiceExtensions() {
        return serviceExtensions;
    }

    /**
     * Gets list of {@link QualifierExtension} corresponding to this Service Chronological Info.
     *
     * @return list of {@link QualifierExtension}
     */
    public List<QualifierExtension> getQualifierExtensions() {
        return qualifierExtensions;
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JSON_KEY_SERVICE_STATUS, new JsonString(getServiceStatus()));
        if (getServiceStatusStartingTime() != TimestampConstants.UNDEFINED_TIMESTAMP_DATE) {
            for (String formattingPattern : statusDateFormats) {
                try {
                    jsonObject.add(JSON_KEY_SERVICE_STATUS_STARTING_TIME, new JsonString(
                            DateTimeUtil.parseLocalDateTime(getServiceStatusStartingTime(), formattingPattern)));
                } catch (Exception ignored) {
                    // Try other format
                }
            }
        } else {
            jsonObject.add(JSON_KEY_SERVICE_STATUS_STARTING_TIME, JsonNull.JSON_NULL);
        }

        if (getServiceExtensions() != null) {
            List<JsonValue> jsonExtensionValues = getServiceExtensions().stream().map(extension -> extension.toJson())
                    .collect(Collectors.toList());
            jsonObject.add(JSON_KEY_SERVICE_EXTENSIONS, new JsonArray(jsonExtensionValues));
        } else {
            jsonObject.add(JSON_KEY_SERVICE_EXTENSIONS, JsonNull.JSON_NULL);
        }

        JsonArray qualifierExtensionsJson = new JsonArray(getQualifierExtensions().stream().map(
                qualifierExtension -> qualifierExtension.toJson()).collect(Collectors.toList()));
        jsonObject.add(JSON_KEY_QUALIFIER_EXTENSIONS, qualifierExtensionsJson);
        return jsonObject;
    }

    /**
     * Deserializes {@link JsonValue} into {@link ServiceChronologicalInfo}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link ServiceChronologicalInfo}
     */
    public static ServiceChronologicalInfo fromJson(JsonValue jsonValue) {
        JsonObject serviceChronologicalInfoJson = (JsonObject) jsonValue;
        String serviceStatusFromJson =
                ((JsonString) serviceChronologicalInfoJson.getField(JSON_KEY_SERVICE_STATUS)).getValue();

        JsonValue serviceStatusStartingTimeJson =
                serviceChronologicalInfoJson.getField(JSON_KEY_SERVICE_STATUS_STARTING_TIME);
        LocalDateTime serviceStatusStartingTimeFromJson = (LocalDateTime) TimestampConstants.UNDEFINED_TIMESTAMP_DATE;
        if (JsonNull.JSON_NULL != serviceStatusStartingTimeJson) {
            for (String formattingPattern : statusDateFormats) {
                try {
                    serviceStatusStartingTimeFromJson = DateTimeUtil.parseToLocalDateTime(
                            ((JsonString) serviceStatusStartingTimeJson).getValue(), formattingPattern);
                } catch (Exception ignored) {
                    // Try other format
                }
            }
        }
        ServiceChronologicalInfo serviceChronologicalInfoFromJson =
                new ServiceChronologicalInfo(serviceStatusFromJson, serviceStatusStartingTimeFromJson);

        JsonValue serviceExtensionsJson =
                serviceChronologicalInfoJson.getField(JSON_KEY_SERVICE_EXTENSIONS);
        if (JsonNull.JSON_NULL != serviceExtensionsJson) {
            List<AdditionalServiceInformationExtension> serviceExtensionsFromJson =
                    ((JsonArray) serviceExtensionsJson).getValues().stream().map(serviceExtensionJson ->
                            AdditionalServiceInformationExtension.fromJson(serviceExtensionJson))
                            .collect(Collectors.toList());
            for (AdditionalServiceInformationExtension informationExtensionFromJson : serviceExtensionsFromJson) {
                serviceChronologicalInfoFromJson.addServiceExtension(informationExtensionFromJson);
            }
        }

        JsonArray qualifierExtensionsJson =
                (JsonArray) serviceChronologicalInfoJson.getField(JSON_KEY_QUALIFIER_EXTENSIONS);
        List<QualifierExtension> qualifierExtensionsFromJson = qualifierExtensionsJson.getValues().stream().map(
                qualifierExtensionJson -> QualifierExtension.fromJson(qualifierExtensionJson))
                .collect(Collectors.toList());
        for (QualifierExtension qualifierExtensionFromJson : qualifierExtensionsFromJson) {
            serviceChronologicalInfoFromJson.addQualifierExtension(qualifierExtensionFromJson);
        }
        return serviceChronologicalInfoFromJson;
    }

    static boolean isStatusValid(String status) {
        return VALID_STATUSES.contains(status);
    }

    void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    void setServiceStatusStartingTime(String timeString) {
        for (String statusDateFormat : statusDateFormats) {
            try {
                this.serviceStatusStartingTime = DateTimeUtil.parseToLocalDateTime(timeString, statusDateFormat);
                return;
            } catch (Exception e) {
                //try next format
            }
        }
    }

    void setServiceStatusStartingTime(LocalDateTime serviceStatusStartingTime) {
        this.serviceStatusStartingTime = serviceStatusStartingTime;
    }

    void addServiceExtension(AdditionalServiceInformationExtension extension) {
        serviceExtensions.add(extension);
    }

    void addQualifierExtension(QualifierExtension qualifierExtension) {
        qualifierExtensions.add(qualifierExtension);
    }
}
