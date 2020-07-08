package com.itextpdf.forms.xfdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Category(UnitTest.class)
public class XfdfWriterUnitTest extends ExtendedITextTest {

    @Test
    public void fieldEmptyValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");
        fieldObject.setValue("");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertNull(childOfFields.getFirstChild());
    }

    @Test
    public void fieldNullValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertNull(childOfFields.getFirstChild());
    }

    @Test
    public void fieldWithValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");
        fieldObject.setValue("testValue");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertEquals("value", childOfFields.getFirstChild().getNodeName());
        Assert.assertEquals("testValue", childOfFields.getFirstChild().getTextContent());
    }
}
