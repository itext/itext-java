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
import com.itextpdf.commons.json.JsonNull;
import com.itextpdf.commons.json.JsonNumber;
import com.itextpdf.commons.json.JsonObject;
import com.itextpdf.commons.json.JsonValue;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.lotl.PivotFetcher.Result;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class has a version suffix because it is used for serialization/deserialization of the cache data.
// Future changes to the class that are not backward compatible should result in a new version of the
// class (e.g., LotlCacheDataV2) so that older cache files can still be read.
// Note that the version suffix is only for the class name; it does not affect the serialization format.
class LotlCacheDataV1 implements IJsonSerializable {
    private static final String JSON_KEY_LOTL_CACHE = "lotlCache";
    private static final String JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE = "europeanResourceFetcherCache";
    private static final String JSON_KEY_PIVOT_CACHE = "pivotCache";
    private static final String JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE = "countrySpecificLotlCache";
    private static final String JSON_KEY_TIME_STAMPS = "timeStamps";

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
        try (InputStream is = inputStream;
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            byte[] json = buffer.toByteArray();
            return LotlCacheDataV1.fromJson(JsonValue.fromJson(new String(json, StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new PdfException(SignExceptionMessageConstant.COULD_NOT_INITIALIZE_FROM_FILE, e);
        }
    }

    public void serialize(OutputStream os) throws IOException {
        os.write(toJson().toJson().getBytes(StandardCharsets.UTF_8));
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

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObject lotlCacheDataJson = new JsonObject();
        if (lotlCache != null) {
            lotlCacheDataJson.add(JSON_KEY_LOTL_CACHE, lotlCache.toJson());
        } else {
            lotlCacheDataJson.add(JSON_KEY_LOTL_CACHE, JsonNull.JSON_NULL);
        }
        if (europeanResourceFetcherCache != null) {
            lotlCacheDataJson.add(JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE,
                    europeanResourceFetcherCache.toJson());
        } else {
            lotlCacheDataJson.add(JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE, JsonNull.JSON_NULL);
        }
        if (pivotCache != null) {
            lotlCacheDataJson.add(JSON_KEY_PIVOT_CACHE, pivotCache.toJson());
        } else {
            lotlCacheDataJson.add(JSON_KEY_PIVOT_CACHE, JsonNull.JSON_NULL);
        }

        if (getCountrySpecificLotlCache() != null) {
            List<String> keys = new ArrayList<>(getCountrySpecificLotlCache().keySet());
            //sort the keys
            Collections.sort(keys);
            JsonObject countrySpecificLotlCacheJson = new JsonObject();
            for (String key : keys) {
                CountrySpecificLotlFetcher.Result entry = getCountrySpecificLotlCache().get(key);
                countrySpecificLotlCacheJson.add(key, entry.toJson());
            }
            lotlCacheDataJson.add(JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE,
                    countrySpecificLotlCacheJson);
        } else {
            lotlCacheDataJson.add(JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE, JsonNull.JSON_NULL);
        }

        if (getTimeStamps() != null) {
            JsonObject timestampsJson = new JsonObject();
            for (Map.Entry<String, Long> entry : getTimeStamps().entrySet()) {
                timestampsJson.add(entry.getKey(), new JsonNumber((double) entry.getValue()));
            }
            lotlCacheDataJson.add(JSON_KEY_TIME_STAMPS, timestampsJson);
        } else {
            lotlCacheDataJson.add(JSON_KEY_TIME_STAMPS, JsonNull.JSON_NULL);
        }
        return lotlCacheDataJson;
    }

    /**
     * Deserializes {@link JsonValue} into {@link LotlCacheDataV1}.
     *
     * @param jsonValue {@link JsonValue} to deserialize
     *
     * @return deserialized {@link LotlCacheDataV1}
     */
    private static LotlCacheDataV1 fromJson(JsonValue jsonValue) {
        LotlCacheDataV1 lotlCacheDataFromJson = new LotlCacheDataV1();
        JsonObject lotlCacheDataJson = (JsonObject) jsonValue;

        JsonValue lotlCacheJson = lotlCacheDataJson.getField(JSON_KEY_LOTL_CACHE);
        if (JsonNull.JSON_NULL != lotlCacheJson) {
            lotlCacheDataFromJson.lotlCache = EuropeanLotlFetcher.Result.fromJson(lotlCacheJson);
        }

        JsonValue europeanResourceFetcherCacheJson =
                lotlCacheDataJson.getField(JSON_KEY_EUROPEAN_RESOURCE_FETCHER_CACHE);
        if (JsonNull.JSON_NULL != europeanResourceFetcherCacheJson) {
            lotlCacheDataFromJson.europeanResourceFetcherCache =
                    EuropeanResourceFetcher.Result.fromJson(europeanResourceFetcherCacheJson);
        }

        JsonValue pivotFetcherJson = lotlCacheDataJson.getField(JSON_KEY_PIVOT_CACHE);
        if (JsonNull.JSON_NULL != pivotFetcherJson) {
            lotlCacheDataFromJson.pivotCache = PivotFetcher.Result.fromJson(pivotFetcherJson);
        }

        JsonValue countrySpecificLotlCacheJson =
                lotlCacheDataJson.getField(JSON_KEY_COUNTRY_SPECIFIC_LOTL_CACHE);
        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCacheFromJson = new HashMap<>();
        if (JsonNull.JSON_NULL != countrySpecificLotlCacheJson) {
            JsonObject countrySpecificLotlCacheJsonObject = (JsonObject) countrySpecificLotlCacheJson;
            for (Map.Entry<String, JsonValue> countrySpecificLotlFetcherResultJson :
                    countrySpecificLotlCacheJsonObject.getFields().entrySet()) {
                countrySpecificLotlCacheFromJson.put(countrySpecificLotlFetcherResultJson.getKey(),
                        CountrySpecificLotlFetcher.Result.fromJson(countrySpecificLotlFetcherResultJson.getValue()));
            }
        }
        lotlCacheDataFromJson.countrySpecificLotlCache = countrySpecificLotlCacheFromJson;

        JsonValue timestampsJson = lotlCacheDataJson.getField(JSON_KEY_TIME_STAMPS);
        Map<String, Long> timestampsFromJson = new HashMap<>();
        if (JsonNull.JSON_NULL != timestampsJson) {
            JsonObject timestampsJsonObject = (JsonObject) timestampsJson;
            for (Map.Entry<String, JsonValue> timestampJson : timestampsJsonObject.getFields().entrySet()) {
                timestampsFromJson.put(timestampJson.getKey(),
                        (long) ((JsonNumber) timestampJson.getValue()).getValue());
            }
        }
        lotlCacheDataFromJson.timeStamps = timestampsFromJson;
        return lotlCacheDataFromJson;
    }
}
