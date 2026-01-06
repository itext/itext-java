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
package com.itextpdf.commons.json;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class JsonTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/commons/json/JsonTest/";

    @Test
    public void utf8CharsetStringTest() {
        Assertions.assertEquals("\"©\"", new JsonString("©").toJson());
    }

    @Test
    public void roundNumberTest() {
        Assertions.assertEquals("-4", new JsonNumber(-4).toJson());
    }

    @Test
    public void serializeSimpleJsonTest() throws IOException {
        String cmp = SOURCE_FOLDER + "simple.json";

        JsonValue simpleJson = createSimpleJson();
        String resultString = simpleJson.toJson();
        JsonValue resultJson = JsonValue.fromJson(resultString);
        JsonValue cmpJson = JsonValue.fromJson(getJsonStringFromFile(cmp));

        Assertions.assertEquals(cmpJson, resultJson);
        Assertions.assertEquals(cmpJson, simpleJson);
    }

    @Test
    public void serializeStringWithLineBreaksTest() throws IOException {
        String cmp = SOURCE_FOLDER + "stringsWithLineBreaks.json";

        String[] stringsForSerialization = createStringWithLineBreaks();
        List<JsonValue> list = new ArrayList<>();
        for (String str : stringsForSerialization) {
            list.add(new JsonString(str));
        }

        JsonValue strings = new JsonArray(list);
        String resultString = strings.toJson();
        JsonValue resultJson = JsonValue.fromJson(resultString);
        JsonValue cmpJson = JsonValue.fromJson(getJsonStringFromFile(cmp));

        Assertions.assertEquals(cmpJson, resultJson);
        Assertions.assertEquals(cmpJson, strings);
    }

    @Test
    public void serializeComplexStructureTest() throws IOException {
        String cmp = SOURCE_FOLDER + "complexStructure.json";
        String cmpString = getJsonStringFromFile(cmp);

        JsonValue complexStructure = createComplexStructureObject();
        String resultString = complexStructure.toJson();
        JsonValue resultJson = JsonValue.fromJson(resultString);
        JsonValue cmpJson = JsonValue.fromJson(cmpString);

        Assertions.assertEquals(cmpString, resultString);
        Assertions.assertEquals(cmpJson, resultJson);
        Assertions.assertEquals(cmpJson, complexStructure);
    }

    @Test
    public void deserializeInvalidJsonFileStringTest() throws IOException {
        String source = SOURCE_FOLDER + "invalidJson.json";

        String jsonString = getJsonStringFromFile(source);
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(jsonString));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void nullStringTest() {
        JsonObject cmpObj = new JsonObject();
        cmpObj.add("nullString", JsonNull.JSON_NULL);

        JsonObject obj = new JsonObject();
        obj.add("nullString", new JsonString(null));

        JsonValue serDeserObj = JsonValue.fromJson(obj.toJson());
        Assertions.assertEquals(cmpObj, serDeserObj);
    }

    @Test
    public void emptyStringTest() {
        JsonString emptyString = new JsonString("");
        String json = emptyString.toJson();
        JsonValue deserialized = JsonValue.fromJson(json);
        Assertions.assertEquals(emptyString, deserialized);
    }

    @Test
    public void specialCharactersInStringTest() {
        String[] specialStrings = {
                "\"quoted\"",
                "\\backslash\\",
                "/forward/slash/",
                "\b\f\n\r\t",
                "Mixed: \"quotes\" and \\backslash\\ and \n newlines",
                "\u0000\u0001\u001F"
        };

        for (String str : specialStrings) {
            JsonString jsonString = new JsonString(str);
            String serialized = jsonString.toJson();
            JsonValue deserialized = JsonValue.fromJson(serialized);
            Assertions.assertEquals(jsonString, deserialized, "Failed for: " + str);
        }
    }

    @Test
    public void specialNumberValuesTest() {
        JsonNumber nanNumber = new JsonNumber(Double.NaN);
        Exception e = Assertions.assertThrows(ITextException.class, () -> nanNumber.toJson());
        Assertions.assertTrue(e.getMessage().contains("Failed to serialize json into string"));

        JsonNumber infNumber = new JsonNumber(Double.POSITIVE_INFINITY);
        e = Assertions.assertThrows(ITextException.class, () -> infNumber.toJson());
        Assertions.assertTrue(e.getMessage().contains("Failed to serialize json into string"));

        JsonNumber negInfNumber = new JsonNumber(Double.NEGATIVE_INFINITY);
        e = Assertions.assertThrows(ITextException.class, () -> negInfNumber.toJson());
        Assertions.assertTrue(e.getMessage().contains("Failed to serialize json into string"));
    }

    @Test
    public void emptyArrayTest() {
        JsonArray emptyArray = new JsonArray();
        String serialized = emptyArray.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(emptyArray, deserialized);
        Assertions.assertEquals("[]", serialized);
    }

    @Test
    public void emptyObjectTest() {
        JsonObject emptyObject = new JsonObject();
        String serialized = emptyObject.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(emptyObject, deserialized);
        Assertions.assertEquals("{}", serialized);
    }

    @Test
    public void arrayWithMixedTypesTest() {
        JsonArray mixedArray = new JsonArray();
        mixedArray.add(new JsonString("string"));
        mixedArray.add(new JsonNumber(42));
        mixedArray.add(JsonBoolean.of(true));
        mixedArray.add(JsonBoolean.of(false));
        mixedArray.add(JsonNull.JSON_NULL);
        mixedArray.add(new JsonObject());
        mixedArray.add(new JsonArray());

        String serialized = mixedArray.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(mixedArray, deserialized);
    }

    @Test
    public void duplicateKeysInObjectTest() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonString("first"));
        obj.add("key", new JsonString("second"));

        String serialized = obj.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);

        // The second value should have overwritten the first
        JsonObject deserializedObj = (JsonObject) deserialized;
        Assertions.assertEquals(1, deserializedObj.getFields().size());
        Assertions.assertEquals(new JsonString("second"), deserializedObj.getFields().get("key"));
    }

    @Test
    public void malformedJsonMissingQuoteTest() {
        String malformedJson = "{\"key: \"value\"}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonTrailingCommaTest() {
        String malformedJson = "{\"key\": \"value\",}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonUnterminatedArrayTest() {
        String malformedJson = "[1, 2, 3";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonUnterminatedObjectTest() {
        String malformedJson = "{\"key\": \"value\"";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonInvalidNumberTest() {
        String malformedJson = "{\"key\": 12.34.56}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void emptyJsonStringTest() {
        String emptyJson = "";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(emptyJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void whitespaceOnlyJsonTest() {
        String whitespaceJson = "   \t\n   ";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(whitespaceJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void validWhitespaceAroundValuesTest() {
        String jsonWithWhitespace = "  {  \"key\"  :  \"value\"  }  ";
        JsonValue deserialized = JsonValue.fromJson(jsonWithWhitespace);

        JsonObject expected = new JsonObject();
        expected.add("key", new JsonString("value"));
        Assertions.assertEquals(expected, deserialized);
    }

    @Test
    public void keyWithSpecialCharactersTest() {
        JsonObject obj = new JsonObject();
        obj.add("key with spaces", new JsonString("value1"));
        obj.add("key\"with\"quotes", new JsonString("value2"));
        obj.add("key\\with\\backslash", new JsonString("value3"));
        obj.add("key\nwith\nnewlines", new JsonString("value4"));
        obj.add("key/with/slashes", new JsonString("value5"));

        String serialized = obj.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(obj, deserialized);
    }

    @Test
    public void booleanSerializationTest() {
        JsonBoolean trueValue = JsonBoolean.of(true);
        JsonBoolean falseValue = JsonBoolean.of(false);

        String serializedTrue = trueValue.toJson();
        String serializedFalse = falseValue.toJson();

        Assertions.assertEquals("true", serializedTrue);
        Assertions.assertEquals("false", serializedFalse);

        JsonValue deserializedTrue = JsonValue.fromJson(serializedTrue);
        JsonValue deserializedFalse = JsonValue.fromJson(serializedFalse);

        Assertions.assertEquals(trueValue, deserializedTrue);
        Assertions.assertEquals(falseValue, deserializedFalse);
    }

    @Test
    public void stringWithOnlyEscapedCharactersTest() {
        String onlyEscaped = "\"\\\b\f\n\r\t";
        JsonString jsonString = new JsonString(onlyEscaped);
        String serialized = jsonString.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(jsonString, deserialized);
    }

    @Test
    public void leadingZerosInNumbersTest() {
        // JSON does not allow leading zeros, but we can test what happens after deserialization
        String jsonWithNumber = "{\"value\": 007}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(jsonWithNumber));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void arrayWithOnlyNullsTest() {
        JsonArray arrayOfNulls = new JsonArray();
        for (int i = 0; i < 100; i++) {
            arrayOfNulls.add(JsonNull.JSON_NULL);
        }

        String serialized = arrayOfNulls.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(arrayOfNulls, deserialized);
    }

    @Test
    public void stringWithAllControlCharactersTest() {
        StringBuilder sb = new StringBuilder();
        // Test all control characters (0x00 to 0x1F)
        for (int i = 0; i <= 0x1F; i++) {
            sb.append((char) i);
        }

        String controlChars = sb.toString();
        JsonString jsonString = new JsonString(controlChars);
        String serialized = jsonString.toJson();
        JsonValue deserialized = JsonValue.fromJson(serialized);
        Assertions.assertEquals(jsonString, deserialized);
    }

    @Test
    public void malformedJsonMissingColonTest() {
        String malformedJson = "{\"key\" \"value\"}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonExtraCommaInArrayTest() {
        String malformedJson = "[1, 2,, 3]";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonSingleQuotesTest() {
        String malformedJson = "{'key': 'value'}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void malformedJsonUnescapedNewlineInStringTest() {
        String malformedJson = "{\"key\": \"value with\nnewline\"}";
        Exception e = Assertions.assertThrows(ITextException.class, () -> JsonValue.fromJson(malformedJson));
        Assertions.assertTrue(e.getMessage().contains("Failed to parse json string"));
    }

    @Test
    public void jsonWithCommentsTest() {
        String jsonWithComments = "{ /* comment */ \"key\": \"value\" }";
        JsonObject cmpObj = new JsonObject();
        cmpObj.add("key", new JsonString("value"));
        Assertions.assertEquals(cmpObj, JsonValue.fromJson(jsonWithComments));
    }

    @Test
    public void consecutiveBackslashesTest() {
        String[] backslashStrings = {
                "\\",
                "\\\\",
                "\\\\\\",
                "\\\\\\\\",
                "path\\to\\file",
                "\\\\server\\share"
        };

        for (String str : backslashStrings) {
            JsonString jsonString = new JsonString(str);
            String serialized = jsonString.toJson();
            JsonValue deserialized = JsonValue.fromJson(serialized);
            Assertions.assertEquals(jsonString, deserialized, "Failed for: " + str);
        }
    }

    private String getJsonStringFromFile(String pathToFile) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(pathToFile));
        // Use String(byte[]) because there is autoporting for this
        // construction by sharpen by call JavaUtil#GetStringForBytes
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    private static JsonValue createComplexStructureObject() {
        JsonArray arr1 = new JsonArray();
        arr1.add(new JsonString("someStr1"));
        arr1.add(new JsonString("someStr2"));
        JsonObject obj4 = new JsonObject();
        obj4.add("arrayStr", arr1);

        JsonObject obj6 = new JsonObject();
        obj6.add("integer", new JsonNumber(13));
        obj6.add("name", new JsonString("someName"));
        JsonArray arr2 = new JsonArray();
        arr2.add(obj6);
        JsonObject obj7 = new JsonObject();
        obj7.add("integer", new JsonNumber(0));
        obj7.add("name", new JsonString(""));
        arr2.add(obj7);
        obj4.add("grandsons", arr2);

        JsonArray arr3 = new JsonArray();
        arr3.add(new JsonString(""));
        JsonObject obj8 = new JsonObject();
        obj8.add("arrayStr", arr3);
        JsonArray arr4 = new JsonArray();
        arr4.add(obj7);
        obj8.add("grandsons", arr4);

        Map<String, JsonValue> fields3 = new LinkedHashMap<>();
        fields3.put("ChildMapkey", obj4);
        fields3.put("ChildMapKey2", obj8);
        JsonObject obj3 = new JsonObject(fields3);

        Map<String, JsonValue> fields2 = new LinkedHashMap<>();
        fields2.put("FirstMapKey", new JsonNumber(15.1234));
        fields2.put("SecondMapKey", new JsonNumber(8));
        fields2.put("NullInstance", JsonNull.JSON_NULL);
        fields2.put("TrueInstance", JsonBoolean.of(true));
        fields2.put("FalseInstance", JsonBoolean.of(false));
        JsonObject obj2 = new JsonObject(fields2);

        Map<String, JsonValue> fields1 = new LinkedHashMap<>();
        fields1.put("map", obj2);
        fields1.put("str", new JsonString("StringFieldValue"));
        fields1.put("childsMap", obj3);

        return new JsonObject(fields1);
    }

    private static JsonValue createSimpleJson() {
        List<JsonValue> list = new ArrayList<>();
        list.add(new JsonString("FIRST_VALUE"));
        list.add(new JsonString("FIRST_VALUE"));
        list.add(new JsonString("SECOND_VALUE"));

        Map<String, JsonValue> fields = new LinkedHashMap<>();
        fields.put("firstValue", new JsonString("SECOND_VALUE"));
        fields.put("enumArray", new JsonArray(list));

        return new JsonObject(fields);
    }

    private static String[] createStringWithLineBreaks() {
        return new String[] {"String\n\rtest", "  \n   \t"};
    }
}
