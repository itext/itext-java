package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.FlexFlowShorthandResolver;
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
public class FlexFlowShorthandResolverTest extends ExtendedITextTest {
    @Test
    public void initialOrInheritOrUnsetValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String initialShorthand = CommonCssConstants.INITIAL;
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());

        String inheritShorthand = CommonCssConstants.INHERIT;
        resolvedShorthand = resolver.resolveShorthand(inheritShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INHERIT, resolvedShorthand.get(1).getExpression());

        String unsetShorthand = CommonCssConstants.UNSET;
        resolvedShorthand = resolver.resolveShorthand(unsetShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.UNSET, resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void initialWithSpacesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String initialWithSpacesShorthand = "  initial  ";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(initialWithSpacesShorthand);
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals(CommonCssConstants.INITIAL, resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY, count = 3))
    public void containsInitialOrInheritOrUnsetShorthandTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String containsInitialShorthand = "row initial ";
        Assert.assertEquals(Collections.emptyList(), resolver.resolveShorthand(containsInitialShorthand));

        String containsInheritShorthand = "inherit wrap";
        Assert.assertEquals(Collections.emptyList(), resolver.resolveShorthand(containsInheritShorthand));

        String containsUnsetShorthand = "wrap unset";
        Assert.assertEquals(Collections.emptyList(), resolver.resolveShorthand(containsUnsetShorthand));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, count = 2))
    public void emptyShorthandTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();
        String emptyShorthand = "";
        Assert.assertEquals(Collections.emptyList(), resolver.resolveShorthand(emptyShorthand));

        String shorthandWithSpaces = "    ";
        Assert.assertEquals(Collections.emptyList(), resolver.resolveShorthand(shorthandWithSpaces));
    }

    @Test
    public void shorthandWithOneDirectionValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("column", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("nowrap", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void shorthandWithOneWrapValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "wrap-reverse";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        // TODO DEVSIX-4933 flex-direction shall be "row" and flex-wrap shall be "wrap-reverse"
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("wrap-reverse", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("nowrap", resolvedShorthand.get(1).getExpression());
    }

    @Test
    public void shorthandWithOneInvalidValueTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        // TODO DEVSIX-4933 resulting List shall be empty
        Assert.assertEquals(2, resolvedShorthand.size());
        Assert.assertEquals(CommonCssConstants.FLEX_DIRECTION, resolvedShorthand.get(0).getProperty());
        Assert.assertEquals("invalid", resolvedShorthand.get(0).getExpression());
        Assert.assertEquals(CommonCssConstants.FLEX_WRAP, resolvedShorthand.get(1).getProperty());
        Assert.assertEquals("nowrap", resolvedShorthand.get(1).getExpression());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY))
    public void shorthandWithDirectionAndWrapValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "row-reverse wrap";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        // TODO DEVSIX-4933 flex-direction shall be "row-reverse" and flex-wrap shall be "wrap"
        Assert.assertEquals(Collections.emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY))
    public void shorthandWithWrapAndDirectionValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "wrap-reverse column";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        // TODO DEVSIX-4933 flex-direction shall be "column" and flex-wrap shall be "wrap-reverse"
        Assert.assertEquals(Collections.emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY))
    public void shorthandWithTwoDirectionValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column-reverse row";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY))
    public void shorthandWithTwoWrapValuesTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "nowrap wrap-reverse";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.emptyList(), resolvedShorthand);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_PROPERTY))
    public void shorthandWithTwoValuesAndSecondIsInvalidTest() {
        IShorthandResolver resolver = new FlexFlowShorthandResolver();

        String shorthand = "column-reverse invalid";
        List<CssDeclaration> resolvedShorthand = resolver.resolveShorthand(shorthand);

        Assert.assertEquals(Collections.emptyList(), resolvedShorthand);
    }
}
