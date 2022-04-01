package com.itextpdf.styledxmlparser.css;

import com.itextpdf.styledxmlparser.css.parse.CssRuleSetParser;
import com.itextpdf.styledxmlparser.css.selector.CssSelector;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssRuleSetTest extends ExtendedITextTest {

    @Test
    public void addCssRuleSetWithNormalImportantDeclarationsTest() {
        String src =
                "float:right; clear:right !important;width:22.0em!important; margin:0 0 1.0em 1.0em; "
                        + "background:#f9f9f9; "
                        + "border:1px solid #aaa;padding:0.2em ! important;border-spacing:0.4em 0; text-align:center "
                        + "!important; "
                        + "line-height:1.4em; font-size:88%!  important;";

        String[] expectedNormal = new String[] {
                "float: right",
                "margin: 0 0 1.0em 1.0em",
                "background: #f9f9f9",
                "border: 1px solid #aaa",
                "border-spacing: 0.4em 0",
                "line-height: 1.4em"
        };

        String[] expectedImportant = new String[] {
                "clear: right",
                "width: 22.0em",
                "padding: 0.2em",
                "text-align: center",
                "font-size: 88%"
        };

        List<CssDeclaration> declarations = CssRuleSetParser.parsePropertyDeclarations(src);
        CssSelector selector = new CssSelector("h1");
        CssRuleSet cssRuleSet = new CssRuleSet(selector, declarations);
        List<CssDeclaration> normalDeclarations = cssRuleSet.getNormalDeclarations();
        for (int i = 0; i < expectedNormal.length; i++) {
            Assert.assertEquals(expectedNormal[i], normalDeclarations.get(i).toString());
        }
        List<CssDeclaration> importantDeclarations = cssRuleSet.getImportantDeclarations();
        for (int i = 0; i < expectedImportant.length; i++) {
            Assert.assertEquals(expectedImportant[i], importantDeclarations.get(i).toString());
        }
    }
}
