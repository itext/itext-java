package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.test.ExtendedITextTest;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class DefaultAccessibilityPropertiesTest extends ExtendedITextTest {

    private static final String TEST_ROLE1 = "test role 1";
    private static final String TEST_ROLE2 = "test role 2";
    private DefaultAccessibilityProperties sut;

    @BeforeEach
    public void setUp() {
        sut = new DefaultAccessibilityProperties(TEST_ROLE1);
    }

    @Test
    public void copyConstructorTest() {
        sut.setLanguage("en");
        sut.setActualText("actual text");
        sut.setAlternateDescription("alternate description");
        sut.setExpansion("expansion");
        sut.setPhoneme("phoneme");
        sut.setPhoneticAlphabet("phonetic alphabet");
        PdfNamespace namespace = new PdfNamespace("namespace");
        sut.setNamespace(namespace);
        sut.setStructureElementIdString("structure element id");

        DefaultAccessibilityProperties copy = new DefaultAccessibilityProperties(sut);

        Assertions.assertEquals(TEST_ROLE1, copy.getRole());
        Assertions.assertEquals("en", copy.getLanguage());
        Assertions.assertEquals("actual text", copy.getActualText());
        Assertions.assertEquals("alternate description", copy.getAlternateDescription());
        Assertions.assertEquals("expansion", copy.getExpansion());
        Assertions.assertEquals("phoneme", copy.getPhoneme());
        Assertions.assertEquals("phonetic alphabet", copy.getPhoneticAlphabet());
        Assertions.assertEquals(namespace, copy.getNamespace());
        Assertions.assertArrayEquals("structure element id".getBytes(StandardCharsets.UTF_8), copy.getStructureElementId());

    }
    
    @Test
    public void getRoleTest() {
        Assertions.assertEquals(TEST_ROLE1, sut.getRole());
    }

    @Test
    public void setRoleTest() {
        sut.setRole(TEST_ROLE2);
        Assertions.assertEquals(TEST_ROLE2, sut.getRole());
    }

    @Test
    public void languageTest() {
        sut.setLanguage("en");
        Assertions.assertEquals("en", sut.getLanguage());
    }


    @Test
    public void actualTextTest() {
        sut.setActualText("actual text");
        Assertions.assertEquals("actual text", sut.getActualText());
    }

    @Test
    public void alternateDescriptionTest() {
        sut.setAlternateDescription("alternate description");
        Assertions.assertEquals("alternate description", sut.getAlternateDescription());
    }

    @Test
    public void expansionTest() {
        sut.setExpansion("expansion");
        Assertions.assertEquals("expansion", sut.getExpansion());
    }

    @Test
    public void addAttributesAfterTest() {
        PdfStructureAttributes attrib1 = new PdfStructureAttributes(TEST_ROLE1);
        attrib1.addIntAttribute("int", 11);
        attrib1.addEnumAttribute("string", "value1");

        PdfStructureAttributes attrib2 = new PdfStructureAttributes(TEST_ROLE2);
        attrib1.addIntAttribute("int", 22);
        attrib1.addEnumAttribute("string", "value2");

        sut.addAttributes(-1, attrib1);
        sut.addAttributes(-1, attrib2);

        Assertions.assertEquals(attrib1, sut.getAttributesList().get(0));
        Assertions.assertEquals(attrib2, sut.getAttributesList().get(1));
    }

    //TODO DEVSIX-10054 fix index 0 behaviour
    @Test
    public void insertAttributesTest() {
        PdfStructureAttributes attrib1 = new PdfStructureAttributes(TEST_ROLE1);
        attrib1.addIntAttribute("int", 11);
        attrib1.addEnumAttribute("string", "value1");

        PdfStructureAttributes attrib2 = new PdfStructureAttributes(TEST_ROLE2);
        attrib1.addIntAttribute("int", 22);
        attrib1.addEnumAttribute("string", "value2");

        PdfStructureAttributes attrib3 = new PdfStructureAttributes(TEST_ROLE1);
        attrib1.addIntAttribute("int", 33);
        attrib1.addEnumAttribute("string", "value3");

        sut.addAttributes(0, attrib1);
        sut.addAttributes(0, attrib2);
        sut.addAttributes(1, attrib3);

        Assertions.assertEquals(attrib1, sut.getAttributesList().get(0));
        Assertions.assertEquals(attrib2, sut.getAttributesList().get(2));
        Assertions.assertEquals(attrib3, sut.getAttributesList().get(1));
    }

    @Test
    public void clearAttributesTest() {
        PdfStructureAttributes attrib1 = new PdfStructureAttributes(TEST_ROLE1);
        attrib1.addIntAttribute("int", 11);
        attrib1.addEnumAttribute("string", "value1");

        PdfStructureAttributes attrib2 = new PdfStructureAttributes(TEST_ROLE2);
        attrib1.addIntAttribute("int", 22);
        attrib1.addEnumAttribute("string", "value2");

        sut.addAttributes(-1, attrib1);
        sut.addAttributes(-1, attrib2);

        sut.clearAttributes();
        Assertions.assertTrue(sut.getAttributesList().isEmpty());
    }

    @Test
    public void getAttributesListTest() {
        PdfStructureAttributes attrib1 = new PdfStructureAttributes(TEST_ROLE1);
        attrib1.addIntAttribute("int", 11);
        attrib1.addEnumAttribute("string", "value1");

        PdfStructureAttributes attrib2 = new PdfStructureAttributes(TEST_ROLE2);
        attrib1.addIntAttribute("int", 22);
        attrib1.addEnumAttribute("string", "value2");

        sut.addAttributes(-1, attrib1);
        sut.addAttributes(-1, attrib2);

        Assertions.assertEquals(2, sut.getAttributesList().size());
        Assertions.assertEquals(attrib1, sut.getAttributesList().get(0));
        Assertions.assertEquals(attrib2, sut.getAttributesList().get(1));
    }

    @Test
    public void phonemeTest() {
        sut.setPhoneme("phoneme");
        Assertions.assertEquals("phoneme", sut.getPhoneme());
    }

    @Test
    public void phoneticAlphabetTest() {
        sut.setPhoneticAlphabet("phonetic alphabet");
        Assertions.assertEquals("phonetic alphabet", sut.getPhoneticAlphabet());
    }

    @Test
    public void namespaceTest() {
        PdfNamespace namespace = new PdfNamespace("namespace");
        sut.setNamespace(namespace);
        Assertions.assertEquals(namespace, sut.getNamespace());
    }

    @Test
    public void structureElementIdStringTest() {
        sut.setStructureElementIdString("structure element id");
        Assertions.assertArrayEquals("structure element id".getBytes(StandardCharsets.UTF_8),
                sut.getStructureElementId());
    }

    @Test
    public void setStructureElementId() {
        sut.setStructureElementId("structure element id".getBytes(StandardCharsets.UTF_8));
        Assertions.assertArrayEquals("structure element id".getBytes(StandardCharsets.UTF_8),
                sut.getStructureElementId());
    }
}