package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AccessibilityPropertiesTest extends ExtendedITextTest {

    @Test
    public void setAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assert.assertNotNull(properties.setRole(StandardRoles.DIV));
        Assert.assertNotNull(properties.setLanguage("EN-GB"));
        Assert.assertNotNull(properties.setActualText("actualText"));
        Assert.assertNotNull(properties.setAlternateDescription("Description"));
        Assert.assertNotNull(properties.setExpansion("expansion"));
        Assert.assertNotNull(properties.setPhoneme("phoneme"));
        Assert.assertNotNull(properties.setPhoneticAlphabet("Phonetic Alphabet"));
        Assert.assertNotNull(properties.setNamespace(new PdfNamespace("Namespace")));
        Assert.assertNotNull(properties.getRefsList());
        Assert.assertNotNull(properties.clearRefs());
        Assert.assertNotNull(properties.addAttributes(new PdfStructureAttributes("attributes")));
        Assert.assertNotNull(properties.addAttributes(0, new PdfStructureAttributes("attributes")));
        Assert.assertNotNull(properties.clearAttributes());
        Assert.assertNotNull(properties.getAttributesList());
        Assert.assertNotNull(properties.addRef(new TagTreePointer(createTestDocument())));
    }

    @Test
    public void getAccessibilityPropertiesTest() {
        AccessibilityProperties properties = new AccessibilityProperties() {
        };
        Assert.assertNull(properties.getRole());
        Assert.assertNull(properties.getLanguage());
        Assert.assertNull(properties.getActualText());
        Assert.assertNull(properties.getAlternateDescription());
        Assert.assertNull(properties.getExpansion());
        Assert.assertNull(properties.getPhoneme());
        Assert.assertNull(properties.getPhoneticAlphabet());
        Assert.assertNull(properties.getNamespace());
    }

    private static PdfDocument createTestDocument() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.setTagged();
        return pdfDoc;
    }
}
