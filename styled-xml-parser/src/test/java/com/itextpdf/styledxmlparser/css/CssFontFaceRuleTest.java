package com.itextpdf.styledxmlparser.css;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class CssFontFaceRuleTest extends ExtendedITextTest {

    @Test
    public void verifyThatToStringProducesValidCss() {
        CssFontFaceRule fontFaceRule = new CssFontFaceRule();
        List<CssDeclaration> declarations = new ArrayList<>();
        declarations.add(new CssDeclaration(CommonCssConstants.FONT_FAMILY, "test-font-family"));
        declarations.add(new CssDeclaration(CommonCssConstants.FONT_WEIGHT, CommonCssConstants.BOLD));
        fontFaceRule.addBodyCssDeclarations(declarations);

        String expectedCss = "@font-face {\n" +
                             "    font-family: test-font-family;\n" +
                             "    font-weight: bold;\n" +
                             "}";
        Assert.assertEquals(expectedCss, fontFaceRule.toString());
    }

}
