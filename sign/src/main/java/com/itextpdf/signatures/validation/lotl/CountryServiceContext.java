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
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.SignJsonSerializerHelper;

import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing TSPService entry in a country specific Trusted List.
 */
public class CountryServiceContext implements IServiceContext, IJsonSerializable {
    private static final String JSON_KEY_CERTIFICATES = "certificates";
    private static final String JSON_KEY_SERVICE_TYPE = "serviceType";
    private static final String JSON_KEY_SERVICE_CHRONOLOGICAL_INFOS = "serviceChronologicalInfos";

    private List<Certificate> certificates = new ArrayList<>();
    //It is expected that service statuses are ordered starting from the newest one.
    private List<ServiceChronologicalInfo> serviceChronologicalInfos = new ArrayList<>();
    private String serviceType;

    CountryServiceContext() {
        // Empty constructor.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList<>(certificates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCertificate(Certificate certificate) {
        certificates.add(certificate);
    }

    /**
     * Gets list of {@link ServiceChronologicalInfo} objects corresponding to this country service context.
     *
     * @return list of {@link ServiceChronologicalInfo} objects
     */
    public List<ServiceChronologicalInfo> getServiceChronologicalInfos() {
        return serviceChronologicalInfos;
    }

    /**
     * Gets service type {@link String} corresponding to this country service context.
     *
     * @return service type {@link String}
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Gets {@link ServiceChronologicalInfo} corresponding to this country service context and DateTime in milliseconds.
     *
     * @param milliseconds DateTime in milliseconds
     *
     * @return corresponding {@link ServiceChronologicalInfo} instance
     */
    public ServiceChronologicalInfo getServiceChronologicalInfoByDate(long milliseconds) {
        return getServiceChronologicalInfoByDate(DateTimeUtil.getTimeFromMillis(milliseconds));
    }

    /**
     * Gets {@link ServiceChronologicalInfo} corresponding to this country service context and {@link LocalDateTime}.
     *
     * @param time {@link LocalDateTime} date time
     *
     * @return corresponding {@link ServiceChronologicalInfo} instance
     */
    public ServiceChronologicalInfo getServiceChronologicalInfoByDate(LocalDateTime time) {
        for (ServiceChronologicalInfo serviceChronologicalInfo : serviceChronologicalInfos) {
            if (!time.isBefore(serviceChronologicalInfo.getServiceStatusStartingTime())) {
                return serviceChronologicalInfo;
            }
        }

        return null;
    }

    /**
     * Gets the latest {@link ServiceChronologicalInfo} instance.
     *
     * @return the latest {@link ServiceChronologicalInfo} instance
     */
    public ServiceChronologicalInfo getCurrentChronologicalInfo() {
        return serviceChronologicalInfos.get(0);
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObject jsonObject = new JsonObject();

        JsonArray certificatesJson = new JsonArray();
        for (Certificate certificate : certificates) {
            certificatesJson.add(SignJsonSerializerHelper.serializeCertificate(certificate));
        }
        jsonObject.add(JSON_KEY_CERTIFICATES, certificatesJson);
        jsonObject.add(JSON_KEY_SERVICE_CHRONOLOGICAL_INFOS,
                new JsonArray(serviceChronologicalInfos.stream().map(
                        serviceChronologicalInfo -> serviceChronologicalInfo.toJson())
                        .collect(Collectors.toList())));
        jsonObject.add(JSON_KEY_SERVICE_TYPE, new JsonString(serviceType));

        return jsonObject;
    }

    /**
     * Deserializes {@link JsonValue} into {@link CountryServiceContext}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link CountryServiceContext}
     */
    public static CountryServiceContext fromJson(JsonValue jsonValue) {
        JsonObject countryServiceContextJson = (JsonObject) jsonValue;
        JsonArray certificatesJson =
                (JsonArray) countryServiceContextJson.getField(JSON_KEY_CERTIFICATES);
        List<Certificate> certificatesFromJson = certificatesJson.getValues().stream().map(certificateJson ->
                SignJsonSerializerHelper.deserializeCertificate(certificateJson)).collect(Collectors.toList());
        CountryServiceContext countryServiceContextFromJson = new CountryServiceContext();
        countryServiceContextFromJson.certificates = certificatesFromJson;

        JsonArray serviceChronologicalInfosJson =
                (JsonArray) countryServiceContextJson.getField(JSON_KEY_SERVICE_CHRONOLOGICAL_INFOS);
        countryServiceContextFromJson.serviceChronologicalInfos =
                serviceChronologicalInfosJson.getValues().stream().map(
                        serviceChronologicalInfoJson ->
                                ServiceChronologicalInfo.fromJson(serviceChronologicalInfoJson))
                        .collect(Collectors.toList());

        countryServiceContextFromJson.serviceType =
                ((JsonString) countryServiceContextJson.getField(JSON_KEY_SERVICE_TYPE)).getValue();
        return countryServiceContextFromJson;
    }

    void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    void addServiceChronologicalInfo(ServiceChronologicalInfo serviceChronologicalInfo) {
        serviceChronologicalInfos.add(serviceChronologicalInfo);
    }

    int getServiceChronologicalInfosSize() {
        return serviceChronologicalInfos.size();
    }
}
