package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class BorderRadiusShorthandResolverTest extends ExtendedITextTest {

    @Test
    public void borderRadiusSlashTest() {
        String shorthandExpression = "20px 40px 40px / 20px 40px 40px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-bottom-left-radius: 40px 40px",
                "border-bottom-right-radius: 40px 40px",
                "border-top-left-radius: 20px 20px",
                "border-top-right-radius: 40px 40px"
        ));

        IShorthandResolver borderRadiusResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_RADIUS);
        assertNotNull(borderRadiusResolver);
        List<CssDeclaration> resolvedShorthandProps = borderRadiusResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void borderRadiusSingleTest() {
        String shorthandExpression = " 20px ";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "border-bottom-left-radius: 20px",
                "border-bottom-right-radius: 20px",
                "border-top-left-radius: 20px",
                "border-top-right-radius: 20px"
        ));

        IShorthandResolver borderRadiusResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BORDER_RADIUS);
        assertNotNull(borderRadiusResolver);
        List<CssDeclaration> resolvedShorthandProps = borderRadiusResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }
}
