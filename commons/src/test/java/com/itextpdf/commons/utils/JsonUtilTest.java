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
package com.itextpdf.commons.utils;

import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class JsonUtilTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/commons/utils/JsonUtilTest/";

    private static boolean isRunOnJava = false;

    // Android-Conversion-Skip-Block-Start (cutting area is used to understand whether code is running on Android or not)
    static {
        isRunOnJava = true;
    }
    // Android-Conversion-Skip-Block-End

    @Test
    public void utf8CharsetStringTest() {
        Assertions.assertEquals("\"©\"", JsonUtil.serializeToString("©"));
    }

    @Test
    public void utf8CharsetStreamTest() throws UnsupportedEncodingException {
        final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        JsonUtil.serializeToStream(byteArray, "©");
        Assertions.assertEquals("\"©\"",
                EncodingUtil.convertToString(byteArray.toByteArray(), "UTF-8"));
    }

    @Test
    public void serializeInstanceWithEnumStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "classWithEnum.json";

        final ClassWithEnum classWithEnum = createClassWithEnumObject();
        String resultString = JsonUtil.serializeToString(classWithEnum);

        String cmpString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(cmpString, resultString));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
    public void serializeInstanceWithEnumStreamTest() throws IOException {
        String cmp;
        if (isRunOnJava) {
            cmp = SOURCE_FOLDER + "classWithEnum.json";
        } else {
            // Test is run on Android, so field order will be different from Java.
            cmp = SOURCE_FOLDER + "classWithEnumAndroid.json";
        }

        try (InputStream inputStream = FileUtil.getInputStreamForFile(cmp);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToStream(serializationResult, createClassWithEnumObject());
            serializationResult.flush();

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeToMinimalInstanceWithEnumStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "minimalClassWithEnum.json";

        final ClassWithEnum classWithEnum = createClassWithEnumObject();
        String resultString = JsonUtil.serializeToMinimalString(classWithEnum);

        String compareString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(compareString, resultString));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
    public void serializeToMinimalInstanceWithEnumStreamTest() throws IOException {
        String cmp;
        if (isRunOnJava) {
            cmp = SOURCE_FOLDER + "minimalClassWithEnum.json";
        } else {
            // Test is run on Android, so field order will be different from Java.
            cmp = SOURCE_FOLDER + "minimalClassWithEnumAndroid.json";
        }

        try (InputStream inputStream = FileUtil.getInputStreamForFile(cmp);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToMinimalStream(serializationResult, createClassWithEnumObject());
            serializationResult.flush();

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeStringWithLineBreakStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "stringsWithLineBreaks.json";

        String[] stringsForSerialization = createStringWithLineBreaks();
        String resultString = JsonUtil.serializeToString(stringsForSerialization);

        String cmpString = getJsonStringFromFile(cmp);
        Assertions.assertEquals(cmpString,resultString);
    }

    @Test
    public void serializeStringWithLineBreakStreamTest() throws IOException {
        String path = SOURCE_FOLDER + "stringsWithLineBreaks.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(path);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToStream(serializationResult, createStringWithLineBreaks());

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeToMinimalStringWithLineBreakStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "minimalStringsWithLineBreaks.json";

        String[] stringsForSerialization = createStringWithLineBreaks();
        String resultString = JsonUtil.serializeToMinimalString(stringsForSerialization);

        String cmpString = getJsonStringFromFile(cmp);
        Assertions.assertEquals(cmpString,resultString);
    }

    @Test
    public void serializeToMinimalStringWithLineBreakStreamTest() throws IOException {
        String path = SOURCE_FOLDER + "minimalStringsWithLineBreaks.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(path);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToMinimalStream(serializationResult, createStringWithLineBreaks());

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeComplexStructureStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "complexStructure.json";

        final ComplexStructure complexStructure = createComplexStructureObject();
        String resultString = JsonUtil.serializeToString(complexStructure);

        String compareString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(compareString, resultString));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
    public void serializeComplexStructureStreamTest() throws IOException {
        String cmp;
        if (isRunOnJava) {
            cmp = SOURCE_FOLDER + "complexStructure.json";
        } else {
            // Test is run on Android, so field order will be different from Java.
            cmp = SOURCE_FOLDER + "complexStructureAndroid.json";
        }

        try (InputStream inputStream = FileUtil.getInputStreamForFile(cmp);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToStream(serializationResult, createComplexStructureObject());

            Assertions.assertNotEquals(0, serializationResult.size());
            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeToMinimalComplexStructureStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "minimalComplexStructure.json";

        final ComplexStructure complexStructure = createComplexStructureObject();
        String resultString = JsonUtil.serializeToMinimalString(complexStructure);

        String compareString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(compareString, resultString));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
    public void serializeToMinimalComplexStructureStreamTest() throws IOException {
        String cmp;
        if (isRunOnJava) {
            cmp = SOURCE_FOLDER + "minimalComplexStructure.json";
        } else {
            // Test is run on Android, so field order will be different from Java.
            cmp = SOURCE_FOLDER + "minimalComplexStructureAndroid.json";
        }

        try (InputStream inputStream = FileUtil.getInputStreamForFile(cmp);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToMinimalStream(serializationResult, createComplexStructureObject());

            Assertions.assertNotEquals(0, serializationResult.size());
            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeWithNullFieldsStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "serializeWithNullFields.json";

        final ClassWithDefaultValue complexStructure =
                createClassWithDefaultValueObject(null, 4, null);
        String resultString = JsonUtil.serializeToString(complexStructure);

        String compareString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(compareString, resultString));
    }

    @Test
    public void serializeWithNullFieldsStreamTest() throws IOException {
        String path = SOURCE_FOLDER + "serializeWithNullFields.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(path);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToStream(serializationResult,
                    createClassWithDefaultValueObject(null, 4, null));

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    public void serializeToMinimalWithNullFieldsStringTest() throws IOException {
        String cmp = SOURCE_FOLDER + "minimalSerializeWithNullFields.json";

        final ClassWithDefaultValue complexStructure =
                createClassWithDefaultValueObject(null, 4, null);
        String resultString = JsonUtil.serializeToMinimalString(complexStructure);

        String compareString = getJsonStringFromFile(cmp);
        Assertions.assertTrue(JsonUtil.areTwoJsonObjectEquals(compareString, resultString));
    }

    @Test
    public void serializeToMinimalWithNullFieldsStreamTest() throws IOException {
        String path = SOURCE_FOLDER + "minimalSerializeWithNullFields.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(path);
                ByteArrayOutputStream baos = convertInputStreamToOutput(inputStream);
                ByteArrayOutputStream serializationResult = new ByteArrayOutputStream()) {
            JsonUtil.serializeToMinimalStream(serializationResult,
                    createClassWithDefaultValueObject(null, 4, null));

            Assertions.assertArrayEquals(baos.toByteArray(), serializationResult.toByteArray());
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = CommonsLogMessageConstant.UNABLE_TO_DESERIALIZE_JSON, logLevel =
                    LogLevelConstants.WARN)
    })
    public void deserializeInvalidJsonFileStringTest() throws IOException {
        String source = SOURCE_FOLDER + "invalidJson.json";

        String jsonString = getJsonStringFromFile(source);

        String resultStr = JsonUtil.<String>deserializeFromString(jsonString, String.class);
        Assertions.assertNull(resultStr);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = CommonsLogMessageConstant.UNABLE_TO_DESERIALIZE_JSON, logLevel =
                    LogLevelConstants.WARN)
    })
    public void deserializeInvalidJsonFileStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "invalidJson.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            String resultStr = JsonUtil.<String>deserializeFromStream(inputStream, String.class);
            Assertions.assertNull(resultStr);
        }
    }

    @Test
    public void deserializeWithDefaultValueStringTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithDefaultValue.json";

        String jsonString = getJsonStringFromFile(source);

        ClassWithDefaultValue instance =
                JsonUtil.<ClassWithDefaultValue>deserializeFromString(jsonString, ClassWithDefaultValue.class);
        Assertions.assertEquals(createClassWithDefaultValueObject(null, 2, 5.0), instance);
    }

    @Test
    public void deserializeWithDefaultValueStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithDefaultValue.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            ClassWithDefaultValue instance =
                    JsonUtil.<ClassWithDefaultValue>deserializeFromStream(inputStream, ClassWithDefaultValue.class);
            Assertions.assertEquals(createClassWithDefaultValueObject(null, 2, 5.0), instance);
        }
    }

    @Test
    public void deserializeComplexStructureStringTest() throws IOException {
        String source = SOURCE_FOLDER + "complexStructure.json";

        String jsonString = getJsonStringFromFile(source);

        ComplexStructure complexStructure =
                JsonUtil.<ComplexStructure>deserializeFromString(jsonString, ComplexStructure.class);

        Assertions.assertEquals(createComplexStructureObject(), complexStructure);
    }

    @Test
    public void deserializeComplexStructureStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "complexStructure.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            ComplexStructure complexStructure =
                    JsonUtil.<ComplexStructure>deserializeFromStream(inputStream, ComplexStructure.class);

            Assertions.assertEquals(createComplexStructureObject(), complexStructure);
        }
    }

    @Test
    public void deserializeInstanceWithEnumStringTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithEnum.json";

        String jsonString = getJsonStringFromFile(source);

        ClassWithEnum classWithEnum = JsonUtil.<ClassWithEnum>deserializeFromString(jsonString, ClassWithEnum.class);

        Assertions.assertEquals(createClassWithEnumObject(), classWithEnum);
    }

    @Test
    public void deserializeInstanceWithEnumStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithEnum.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            ClassWithEnum classWithEnum = JsonUtil
                    .<ClassWithEnum>deserializeFromStream(inputStream, ClassWithEnum.class);

            Assertions.assertEquals(createClassWithEnumObject(), classWithEnum);
        }
    }

    @Test
    public void deserializeWithUnknownPropertiesStringTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithUnknownProperties.json";

        String jsonString = getJsonStringFromFile(source);

        ClassWithDefaultValue instance =
                JsonUtil.<ClassWithDefaultValue>deserializeFromString(jsonString, ClassWithDefaultValue.class);

        Assertions.assertEquals(
                createClassWithDefaultValueObject("some small string", 8, 26.0), instance);
    }

    @Test
    public void deserializeWithUnknownPropertiesStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithUnknownProperties.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            ClassWithDefaultValue instance =
                    JsonUtil.<ClassWithDefaultValue>deserializeFromStream(inputStream, ClassWithDefaultValue.class);

            Assertions.assertNotNull(instance);
            Assertions.assertEquals(
                    createClassWithDefaultValueObject("some small string", 8, 26.0), instance);
        }
    }

    @Test
    public void deserializeWithDefaultValueTypeReferenceStreamTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithDefaultValue.json";

        try (InputStream inputStream = FileUtil.getInputStreamForFile(source)) {
            ClassWithDefaultValue instance =
                    JsonUtil.<ClassWithDefaultValue>deserializeFromStream(inputStream,
                            new TypeReference<ClassWithDefaultValue>() {
                            });
            Assertions.assertEquals(createClassWithDefaultValueObject(null, 2, 5.0), instance);
        }
    }

    @Test
    public void deserializeWithDefaultValueTypeReferenceStringTest() throws IOException {
        String source = SOURCE_FOLDER + "classWithDefaultValue.json";

        String jsonString = getJsonStringFromFile(source);

        ClassWithDefaultValue instance =
                JsonUtil.<ClassWithDefaultValue>deserializeFromString(jsonString,
                        new TypeReference<ClassWithDefaultValue>() {
                        });
        Assertions.assertEquals(createClassWithDefaultValueObject(null, 2, 5.0), instance);
    }

    private String getJsonStringFromFile(String pathToFile) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(pathToFile));
        // Use String(byte[]) because there is autoporting for this
        // construction by sharpen by call JavaUtil#GetStringForBytes
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    private static ByteArrayOutputStream convertInputStreamToOutput(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        result.flush();
        return result;
    }

    private static ComplexStructure createComplexStructureObject() {
        final ComplexStructure complexStructure = new ComplexStructure();
        complexStructure.map.put("FirstMapKey", 15);
        complexStructure.map.put("SecondMapKey", 8);
        complexStructure.str = "StringFieldValue";
        ChildInComplexStructure child = new ChildInComplexStructure();
        child.arrayStr = new String[] {"someStr1", "someStr2"};
        GrandsonComplexStructure grandson = new GrandsonComplexStructure();
        grandson.integer = 13;
        grandson.name = "someName";
        child.grandsons = new GrandsonComplexStructure[] {grandson, new GrandsonComplexStructure()};
        complexStructure.childsMap.put("ChildMapkey", child);
        complexStructure.childsMap.put("ChildMapKey2", new ChildInComplexStructure());

        return complexStructure;
    }

    private static ClassWithDefaultValue createClassWithDefaultValueObject(String firstString, int value,
            Double doubleValue) {
        return new ClassWithDefaultValue(firstString, value, doubleValue);
    }

    private static ClassWithEnum createClassWithEnumObject() {
        final ClassWithEnum classWithEnum = new ClassWithEnum();
        classWithEnum.enumArray = new SomeEnum[] {SomeEnum.FIRST_VALUE, SomeEnum.FIRST_VALUE, SomeEnum.SECOND_VALUE};
        classWithEnum.firstValue = SomeEnum.SECOND_VALUE;

        return classWithEnum;
    }

    private static String[] createStringWithLineBreaks() {
        return new String[] {"String\n\rtest", "  \n   \t"};
    }


    private static class ComplexStructure {
        public Map<String, Integer> map = new LinkedHashMap<>();
        public String str = "";
        public Map<String, ChildInComplexStructure> childsMap = new LinkedHashMap<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ComplexStructure that = (ComplexStructure) o;
            return MapUtil.equals(map, that.map) && Objects.equals(str, that.str) && MapUtil
                    .equals(childsMap, that.childsMap);
        }

        @Override
        public int hashCode() {
            int result = map != null ? MapUtil.getHashCode(map) : 0;
            result = 31 * result + (str != null ? str.hashCode() : 0);
            result = 31 * result + (childsMap != null ? MapUtil.getHashCode(childsMap) : 0);
            return result;
        }
    }

    private static class ChildInComplexStructure {
        public String[] arrayStr = new String[] {""};
        public GrandsonComplexStructure[] grandsons = new GrandsonComplexStructure[] {new GrandsonComplexStructure()};

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ChildInComplexStructure that = (ChildInComplexStructure) o;
            return Arrays.equals(arrayStr, that.arrayStr) && Arrays.equals(grandsons, that.grandsons);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(arrayStr);
            result = 31 * result + Arrays.hashCode(grandsons);
            return result;
        }
    }

    private static class GrandsonComplexStructure {
        public int integer = 0;
        public String name = "";


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GrandsonComplexStructure that = (GrandsonComplexStructure) o;
            return integer == that.integer && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            int result = integer;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    private static class ClassWithEnum {
        public SomeEnum firstValue;
        public SomeEnum[] enumArray = new SomeEnum[] {};

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ClassWithEnum that = (ClassWithEnum) o;
            return firstValue == that.firstValue && Arrays.equals(enumArray, that.enumArray);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(firstValue);
            result = 31 * result + Arrays.hashCode(enumArray);
            return result;
        }
    }

    private static enum SomeEnum {
        FIRST_VALUE,
        SECOND_VALUE,
        THIRD_VALUE
    }

    private static class ClassWithDefaultValue {
        public String firstString = "defaultValue";
        public Integer integer = 3;
        public Double doubleValue = 0.0;

        public ClassWithDefaultValue(
                @JsonProperty("firstString") String firstString,
                @JsonProperty("integer") Integer integer,
                @JsonProperty("doubleValue") Double doubleValue
        ) {
            if (firstString != null) {
                this.firstString = firstString;
            }
            if (integer != null) {
                this.integer = integer;
            }
            this.doubleValue = doubleValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ClassWithDefaultValue that = (ClassWithDefaultValue) o;
            return Objects.equals(firstString, that.firstString) && Objects.equals(integer, that.integer)
                    && Objects.equals(doubleValue, that.doubleValue);
        }

        @Override
        public int hashCode() {

            int result = (firstString == null ? 0 : firstString.hashCode());
            result = 31 * result + (integer == null ? 0 : integer.hashCode());
            result = 31 * result + (doubleValue == null ? 0 : doubleValue.hashCode());
            return result;
        }
    }
}
