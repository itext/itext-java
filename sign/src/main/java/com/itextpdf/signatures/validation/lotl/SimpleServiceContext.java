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
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.signatures.SignJsonSerializerHelper;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class SimpleServiceContext implements IServiceContext, IJsonSerializable {
    private static final String JSON_KEY_CERTIFICATES = "certificates";

    private List<Certificate> certificates;

    SimpleServiceContext() {
        //Empty constructor needed for deserialization.
        this.certificates = new ArrayList<>();
    }

    SimpleServiceContext(Certificate certificate) {
        this.certificates = new ArrayList<>();
        certificates.add(certificate);
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

        return jsonObject;
    }

    /**
     * Deserializes {@link JsonValue} into {@link SimpleServiceContext}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link SimpleServiceContext}
     */
    public static SimpleServiceContext fromJson(JsonValue jsonValue) {
        JsonObject simpleServiceContextJson = (JsonObject) jsonValue;
        JsonArray certificatesJson =
                (JsonArray) simpleServiceContextJson.getField(JSON_KEY_CERTIFICATES);
        List<Certificate> certificatesFromJson = certificatesJson.getValues().stream().map(certificateJson ->
                SignJsonSerializerHelper.deserializeCertificate(certificateJson)).collect(Collectors.toList());
        SimpleServiceContext simpleServiceContextFromJson = new SimpleServiceContext();
        simpleServiceContextFromJson.certificates = certificatesFromJson;
        return simpleServiceContextFromJson;
    }

    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList<>(certificates);
    }

    @Override
    public void addCertificate(Certificate certificate) {
        if (certificates == null) {
            certificates = new ArrayList<>();
        }

        certificates.add(certificate);
    }
}
