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
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonString;
import com.itextpdf.commons.json.JsonValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for additional service information extension.
 */
public class AdditionalServiceInformationExtension implements IJsonSerializable {
    static final String FOR_E_SIGNATURES = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSignatures";
    static final String FOR_E_SEALS = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForeSeals";
    static final String FOR_WSA = "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication";
    private static final String JSON_KEY_URI = "uri";
    private static final Set<String> INVALID_SCOPES = new HashSet<>();
    private String uri;

    static {
        INVALID_SCOPES.add(FOR_WSA);
    }

    /**
     * Creates empty instance of {@link AdditionalServiceInformationExtension}.
     */
    public AdditionalServiceInformationExtension() {
        // Empty constructor.
    }

    AdditionalServiceInformationExtension(String uri) {
        this.uri = uri;
    }

    /**
     * Gets URI representing a value of {@link AdditionalServiceInformationExtension}.
     *
     * @return URI representing a value of {@link AdditionalServiceInformationExtension}
     */
    public String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    boolean isScopeValid() {
        return !INVALID_SCOPES.contains(uri);
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObject extensionJson = new JsonObject();
        extensionJson.add(JSON_KEY_URI, new JsonString(getUri()));
        return extensionJson;
    }

    /**
     * Deserializes {@link JsonValue} into {@link AdditionalServiceInformationExtension}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link AdditionalServiceInformationExtension}
     */
    public static AdditionalServiceInformationExtension fromJson(JsonValue jsonValue) {
        return new AdditionalServiceInformationExtension(
                ((JsonString) ((JsonObject) jsonValue).getField(JSON_KEY_URI)).getValue());
    }
}
