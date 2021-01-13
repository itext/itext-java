package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexFlowShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.GapShorthandResolver;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.List;

@Category(UnitTest.class)
public class GapShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void initialWithSpacesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String initialWithSpacesShorthand = "  initial  ";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialWithSpacesShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 3))
    public void containsInitialOrInheritOrUnsetShorthandTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String containsInitialShorthand = "10px initial ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInitialShorthand));

        String containsInheritShorthand = "inherit 10%";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsInheritShorthand));

        String containsUnsetShorthand = "0 unset";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(containsUnsetShorthand));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new GapShorthandResolver();
        String emptyShorthand = "";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void gapWithOneValidValueTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void gapWithOneInvalidValueTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void gapWithTwoValidValuesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10px 15px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("15px", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void gapWithValidAndInvalidValuesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10px 15";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void gapWithInvalidAndValidValuesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10 15px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(0, resolvedShorthand.size());
    }

    @Test
    public void gapWithZeroNumberTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "0 10px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.ROW_GAP, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("0", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.COLUMN_GAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("10px", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void gapWithThreeValuesTest() {
        IShorthandResolver resolver = new GapShorthandResolver();

        String shorthand = "10px 15px 20px";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.<CssDeclaration>emptyList(), resolvedShorthand);
    }
}
