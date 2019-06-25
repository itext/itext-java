package com.itextpdf.styledxmlparser.jsoup;

import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Category(UnitTest.class)
public class JsoupXmlParserTest {
  @Test
  public void testXmlDeclarationAndComment() throws IOException {
    String xml = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
            "<!-- just declaration and comment -->";
    InputStream stream =  new ByteArrayInputStream(xml.getBytes());
    IDocumentNode node = new JsoupXmlParser().parse(stream, "UTF-8");
    // only text (whitespace) child node shall be fetched.
    Assert.assertEquals(1, node.childNodes().size());
  }
}
