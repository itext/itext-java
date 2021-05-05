package com.itextpdf.styledxmlparser.css.parse;

import com.itextpdf.io.IOException;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssRuleSetParserTest extends ExtendedITextTest {

    @Test
    public void parsePropertyDeclarationsTest() throws IOException {
        String src = "float:right; clear:right;width:22.0em; margin:0 0 1.0em 1.0em; background:#f9f9f9; "
                + "border:1px solid #aaa;padding:0.2em;border-spacing:0.4em 0; text-align:center; "
                + "line-height:1.4em; font-size:88%;";

        String[] expected = new String[] {
                "float: right",
                "clear: right",
                "width: 22.0em",
                "margin: 0 0 1.0em 1.0em",
                "background: #f9f9f9",
                "border: 1px solid #aaa",
                "padding: 0.2em",
                "border-spacing: 0.4em 0",
                "text-align: center",
                "line-height: 1.4em",
                "font-size: 88%"
        };

        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(src);
        Assert.assertEquals(expected.length, declarations.size());
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], declarations.get(i).toString());
        }
    }
}
