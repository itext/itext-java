/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/

package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CommentTest extends ExtendedITextTest {
    private Comment comment = new Comment(" This is one heck of a comment! ");
    private Comment decl = new Comment("?xml encoding='ISO-8859-1'?");

    @Test
    public void nodeName() {
        Assert.assertEquals("#comment", comment.nodeName());
    }

    @Test
    public void getData() {
        Assert.assertEquals(" This is one heck of a comment! ", comment.getData());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("<!-- This is one heck of a comment! -->", comment.toString());

        Document doc = Jsoup.parse("<div><!-- comment--></div>");
        Assert.assertEquals("<div>\n <!-- comment-->\n</div>", doc.body().html());

        doc = Jsoup.parse("<p>One<!-- comment -->Two</p>");
        Assert.assertEquals("<p>One<!-- comment -->Two</p>", doc.body().html());
        Assert.assertEquals("OneTwo", doc.text());
    }

    @Test
    public void testHtmlNoPretty() {
        Document doc = Jsoup.parse("<!-- a simple comment -->");
        doc.outputSettings().prettyPrint(false);
        Assert.assertEquals("<!-- a simple comment --><html><head></head><body></body></html>", doc.html());
        Node node = doc.childNode(0);
        Comment c1 = (Comment) node;
        Assert.assertEquals("<!-- a simple comment -->", c1.outerHtml());
    }

    @Test
    public void testClone() {
        Comment c1 = (Comment) comment.clone();
        Assert.assertNotSame(comment, c1);
        Assert.assertEquals(comment.getData(), comment.getData());
        c1.setData("New");
        Assert.assertEquals("New", c1.getData());
        Assert.assertNotEquals(c1.getData(), comment.getData());
    }

    @Test
    public void isXmlDeclaration() {
        Assert.assertFalse(comment.isXmlDeclaration());
        Assert.assertTrue(decl.isXmlDeclaration());
    }

    @Test
    public void asXmlDeclaration() {
        XmlDeclaration xmlDeclaration = decl.asXmlDeclaration();
        Assert.assertNotNull(xmlDeclaration);
    }
}
