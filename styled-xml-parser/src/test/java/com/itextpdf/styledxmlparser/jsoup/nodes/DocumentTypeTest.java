/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup.nodes;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.parser.Parser;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Tests for the DocumentType node
 */
@Tag("UnitTest")
public class DocumentTypeTest extends ExtendedITextTest {
    @Test
    public void constructorValidationOkWithBlankName() {
        AssertUtil.doesNotThrow(() -> new DocumentType("","", ""));
    }

    @Test
    public void constructorValidationThrowsExceptionOnNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DocumentType("html", null, null));
    }

    @Test
    public void constructorValidationOkWithBlankPublicAndSystemIds() {
        AssertUtil.doesNotThrow(() -> new DocumentType("html","", ""));
    }

    @Test public void outerHtmlGeneration() {
        DocumentType html5 = new DocumentType("html", "", "");
        Assertions.assertEquals("<!doctype html>", html5.outerHtml());

        DocumentType publicDocType = new DocumentType("html", "-//IETF//DTD HTML//", "");
        Assertions.assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml());

        DocumentType systemDocType = new DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd");
        Assertions.assertEquals("<!DOCTYPE html SYSTEM \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">", systemDocType.outerHtml());

        DocumentType combo = new DocumentType("notHtml", "--public", "--system");
        Assertions.assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml());
        Assertions.assertEquals("notHtml", combo.name());
        Assertions.assertEquals("--public", combo.publicId());
        Assertions.assertEquals("--system", combo.systemId());
    }

    @Test public void testRoundTrip() {
        String base = "<!DOCTYPE html>";
        Assertions.assertEquals("<!doctype html>", htmlOutput(base));
        Assertions.assertEquals(base, xmlOutput(base));

        String publicDoc = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        Assertions.assertEquals(publicDoc, htmlOutput(publicDoc));
        Assertions.assertEquals(publicDoc, xmlOutput(publicDoc));

        String systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">";
        Assertions.assertEquals(systemDoc, htmlOutput(systemDoc));
        Assertions.assertEquals(systemDoc, xmlOutput(systemDoc));

        String legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">";
        Assertions.assertEquals(legacyDoc, htmlOutput(legacyDoc));
        Assertions.assertEquals(legacyDoc, xmlOutput(legacyDoc));
    }

    private String htmlOutput(String in) {
        DocumentType type = (DocumentType) Jsoup.parse(in).childNode(0);
        return type.outerHtml();
    }

    private String xmlOutput(String in) {
        return Jsoup.parse(in, "", Parser.xmlParser()).childNode(0).outerHtml();
    }
}
