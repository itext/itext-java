/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CommentTest extends ExtendedITextTest {
    private Comment comment = new Comment(" This is one heck of a comment! ");
    private Comment decl = new Comment("?xml encoding='ISO-8859-1'?");

    @Test
    public void nodeName() {
        Assertions.assertEquals("#comment", comment.nodeName());
    }

    @Test
    public void getData() {
        Assertions.assertEquals(" This is one heck of a comment! ", comment.getData());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("<!-- This is one heck of a comment! -->", comment.toString());

        Document doc = Jsoup.parse("<div><!-- comment--></div>");
        Assertions.assertEquals("<div>\n <!-- comment-->\n</div>", doc.body().html());

        doc = Jsoup.parse("<p>One<!-- comment -->Two</p>");
        Assertions.assertEquals("<p>One<!-- comment -->Two</p>", doc.body().html());
        Assertions.assertEquals("OneTwo", doc.text());
    }

    @Test
    public void testHtmlNoPretty() {
        Document doc = Jsoup.parse("<!-- a simple comment -->");
        doc.outputSettings().prettyPrint(false);
        Assertions.assertEquals("<!-- a simple comment --><html><head></head><body></body></html>", doc.html());
        Node node = doc.childNode(0);
        Comment c1 = (Comment) node;
        Assertions.assertEquals("<!-- a simple comment -->", c1.outerHtml());
    }

    @Test
    public void testClone() {
        Comment c1 = (Comment) comment.clone();
        Assertions.assertNotSame(comment, c1);
        Assertions.assertEquals(comment.getData(), comment.getData());
        c1.setData("New");
        Assertions.assertEquals("New", c1.getData());
        Assertions.assertNotEquals(c1.getData(), comment.getData());
    }

    @Test
    public void isXmlDeclaration() {
        Assertions.assertFalse(comment.isXmlDeclaration());
        Assertions.assertTrue(decl.isXmlDeclaration());
    }

    @Test
    public void asXmlDeclaration() {
        XmlDeclaration xmlDeclaration = decl.asXmlDeclaration();
        Assertions.assertNotNull(xmlDeclaration);
    }
}
