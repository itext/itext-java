/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
public class BackgroundPositionShorthandResolverTest extends ExtendedITextTest {

    @Test
    public void initialValueTest() {
        String shorthandExpression = "initial";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: initial",
                "background-position-y: initial"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY))
    public void fullEmptyValueTest() {
        String shorthandExpression = " ";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY))
    public void emptyValueTest() {
        String shorthandExpression = "50pt,  , 20pt";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidXValueTest() {
        String shorthandExpression = "left right";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void notExistingXValueTest() {
        String shorthandExpression = "30jacoco 50pt";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidYValueTest() {
        String shorthandExpression = "top bottom";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void notExistingYValueTest() {
        String shorthandExpression = "50pt 30jacoco";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidNumericValueTest() {
        String shorthandExpression = "50px left top";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidNotShortNumericValueTest() {
        String shorthandExpression = "50pt 30px 10pt";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidTopPxShortNumericValueTest() {
        String shorthandExpression = "top 50px";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void pxCenterShortNumericValueTest() {
        String shorthandExpression = "50px center";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: 50px",
                "background-position-y: center"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidPxLeftShortNumericValueTest() {
        String shorthandExpression = "50px left";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void topPxLeftLargeNumericValueTest() {
        String shorthandExpression = "top 50px left";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left",
                "background-position-y: top 50px"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void topLeftPxLargeNumericValueTest() {
        String shorthandExpression = "top left 50px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left 50px",
                "background-position-y: top"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void topPxLeftPxLargeNumericValueTest() {
        String shorthandExpression = "top 10px left 50px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left 50px",
                "background-position-y: top 10px"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidCenterPxTopLargeNumericValueTest() {
        String shorthandExpression = "center 50px top";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void doubleHorizontalWithCenterValueTest() {
        String shorthandExpression = "center left";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left",
                "background-position-y: center"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidDoubleHorizontalWithCenterAndVerticalValueTest() {
        String shorthandExpression = "center top left";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void leftCenterValueTest() {
        String shorthandExpression = "left center";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left",
                "background-position-y: center"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION))
    public void invalidLeftTopCenterValueTest() {
        String shorthandExpression = "left bottom center";
        Set<String> expectedResolvedProperties = new HashSet<>();
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void MultiValueMissedValueTest() {
        String shorthandExpression = "left,top";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-position-x: left,center",
                "background-position-y: center,top"
        ));
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(CommonCssConstants.BACKGROUND_POSITION);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }
}
