/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
public class BackgroundShorthandResolverTest extends ExtendedITextTest {

    @Test
    public void backgroundTest01() {
        String shorthandExpression = "red url('img.gif') 25%/50px 150px repeat-y border-box content-box fixed";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: red",
                "background-image: url('img.gif')",
                "background-position: 25%",
                "background-size: 50px 150px",
                "background-repeat: repeat-y",
                "background-origin: border-box",
                "background-clip: content-box",
                "background-attachment: fixed"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundTest02() {
        String shorthandExpression = "url('img.gif') red 25%/50px 150px repeat-y fixed border-box content-box";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: red",
                "background-image: url('img.gif')",
                "background-position: 25%",
                "background-size: 50px 150px",
                "background-repeat: repeat-y",
                "background-origin: border-box",
                "background-clip: content-box",
                "background-attachment: fixed"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundTest03() {
        String shorthandExpression = "url('img.gif') 25%/50px 150px fixed border-box";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: url('img.gif')",
                "background-position: 25%",
                "background-size: 50px 150px",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: fixed"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    "Was not able to define one of the background CSS shorthand properties: rgdbq(150,90,60)"),
    })
    public void backgroundTest05() {
        String shorthandExpression = "rgdbq(150,90,60) url'smiley.gif') repeat-x scroll 20 60%";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundTest06() {
        String shorthandExpression = "DarkOliveGreen fixed center";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: darkolivegreen",
                "background-image: none",
                "background-position: center",
                "background-size: auto",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: fixed"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidSizeTest1() {
        String shorthandExpression = "50px/50";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidSizeTest2() {
        String shorthandExpression = "50px/repeat";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidSizeTest3() {
        String shorthandExpression = "50px/left";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashInvalidSizeTest4() {
        String shorthandExpression = "50px/url(img.jpg)";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashInvalidSizeTest5() {
        String shorthandExpression = "50px/";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION)
    })
    public void backgroundWithAnotherShorthandFailedTest() {
        String shorthandExpression = "no-repeat left right";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidPositionTest1() {
        String shorthandExpression = "50/50px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidPositionTest2() {
        String shorthandExpression = "cover/50px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashInvalidPositionTest3() {
        String shorthandExpression = "repeat/50px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashInvalidPositionTest4() {
        String shorthandExpression = "url(img.jpg)/50px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashInvalidPositionTest5() {
        String shorthandExpression = "/50px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES, count = 3)
    })
    public void backgroundIncorrectPositionTest() {
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());
        String[] incorrectPositions = new String[]{"cover", "auto", "contain"};
        for (final String incorrectPosition : incorrectPositions) {
            List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(incorrectPosition);
            CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
        }
    }

    @Test
    public void backgroundWithMultiSlashTest() {
        String shorthandExpression = "50px 5px/25px 5%";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: none",
                "background-position: 50px 5px",
                "background-size: 25px 5%",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithMultiSlashFailedOnSizeTest1() {
        String shorthandExpression = "50px 5px/25px 5";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithMultiSlashFailedOnSizeTest2() {
        String shorthandExpression = "50px 5px/25px left";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithMultiSlashFailedOnPositionTest1() {
        String shorthandExpression = "50 5px/25px 5%";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithMultiSlashFailedOnPositionTest2() {
        String shorthandExpression = "cover 5px/25px 5%";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithTwoSlashesTest1() {
        String shorthandExpression = "5px/25px 5%/20px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithTwoSlashesTest2() {
        String shorthandExpression = "5px/25px/5%";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundWithSlashAndSpaceTest1() {
        String shorthandExpression = "5px / 25px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: none",
                "background-position: 5px",
                "background-size: 25px",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundWithSlashAndSpaceTest2() {
        String shorthandExpression = "5px/ 25px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: none",
                "background-position: 5px",
                "background-size: 25px",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundWithSlashAndSpaceTest3() {
        String shorthandExpression = "5px /25px";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: none",
                "background-position: 5px",
                "background-size: 25px",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest1() {
        String shorthandExpression = "repeat / 25px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest2() {
        String shorthandExpression = "5px / repeat";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest3() {
        String shorthandExpression = "5px /repeat";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest4() {
        String shorthandExpression = "5px/ repeat-y";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest5() {
        String shorthandExpression = "repeat-x/ 20px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_PROPERTY)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest6() {
        String shorthandExpression = "no-repeat /20px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest7() {
        String shorthandExpression = "20px /";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundWithSlashAndSpaceIncorrectTest8() {
        String shorthandExpression = "/ 20px";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundInitialInheritUnsetTest() {
        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        String[] globalExpressions = new String[]{"initial", "inherit", "unset"};
        for (final String globalExpression : globalExpressions) {
            Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                    "background-color: " + globalExpression,
                    "background-image: " + globalExpression,
                    "background-position: " + globalExpression,
                    "background-size: " + globalExpression,
                    "background-repeat: " + globalExpression,
                    "background-origin: " + globalExpression,
                    "background-clip: " + globalExpression,
                    "background-attachment: " + globalExpression
            ));
            List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(globalExpression);
            CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void backgroundEmptyShorthandTest() {
        String shorthandExpression = "";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void backgroundEmptyShorthandWithSpaceTest() {
        String shorthandExpression = " ";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void multiBackgroundEmptyShorthandTest1() {
        String shorthandExpression = "none,,none";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void multiBackgroundEmptyShorthandTest2() {
        String shorthandExpression = "none,none,";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void multiBackgroundEmptyShorthandTest3() {
        String shorthandExpression = ",none,none";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY)
    })
    public void multiBackgroundEmptyShorthandWithSpaceTest() {
        String shorthandExpression = "none, ,none";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundDefaultValuesShorthandTest() {
        String shorthandExpression = "none";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: none",
                "background-position: 0% 0%",
                "background-size: auto",
                "background-repeat: repeat",
                "background-origin: padding-box",
                "background-clip: border-box",
                "background-attachment: scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.ONLY_THE_LAST_BACKGROUND_CAN_INCLUDE_BACKGROUND_COLOR)
    })
    public void backgroundColorNotLastTest() {
        String shorthandExpression = "url('img.gif') red, url('img2.gif')";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundColorInImageTest() {
        String shorthandExpression = "url('img.gif'), url('img2.gif') blue";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: blue",
                "background-image: url('img.gif'),url('img2.gif')",
                "background-position: 0% 0%,0% 0%",
                "background-size: auto,auto",
                "background-repeat: repeat,repeat",
                "background-origin: padding-box,padding-box",
                "background-clip: border-box,border-box",
                "background-attachment: scroll,scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundColorNotInImageTest() {
        String shorthandExpression = "url('img.gif'), url('img2.gif'), blue";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: blue",
                "background-image: url('img.gif'),url('img2.gif'),none",
                "background-position: 0% 0%,0% 0%,0% 0%",
                "background-size: auto,auto,auto",
                "background-repeat: repeat,repeat,repeat",
                "background-origin: padding-box,padding-box,padding-box",
                "background-clip: border-box,border-box,border-box",
                "background-attachment: scroll,scroll,scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION)
    })
    public void backgroundDoubleColorTest() {
        String shorthandExpression = "url('img.gif'), url('img2.gif') red blue";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundMultiImageTest() {
        String shorthandExpression = "url('img.gif'), url('img2.gif'), url('img3.gif')";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: transparent",
                "background-image: url('img.gif'),url('img2.gif'),url('img3.gif')",
                "background-position: 0% 0%,0% 0%,0% 0%",
                "background-size: auto,auto,auto",
                "background-repeat: repeat,repeat,repeat",
                "background-origin: padding-box,padding-box,padding-box",
                "background-clip: border-box,border-box,border-box",
                "background-attachment: scroll,scroll,scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION)
    })
    public void backgroundDoubleImageTest() {
        String shorthandExpression = "url('img.gif'), url('img2.gif') url('img3.gif')";
        Set<String> expectedResolvedProperties = new HashSet<>(new ArrayList<String>());

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundMultiImageWithOtherPropsTest() {
        String shorthandExpression = "url('img.gif') 5px/5% repeat-x border-box padding-box fixed," +
                " url('img2.gif') left/50px repeat-y border-box border-box local," +
                "url('img3.gif') center/cover no-repeat padding-box padding-box scroll red";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: red",
                "background-image: url('img.gif'),url('img2.gif'),url('img3.gif')",
                "background-position: 5px,left,center",
                "background-size: 5%,50px,cover",
                "background-repeat: repeat-x,repeat-y,no-repeat",
                "background-origin: border-box,border-box,padding-box",
                "background-clip: padding-box,border-box,padding-box",
                "background-attachment: fixed,local,scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }

    @Test
    public void backgroundMultiImageWithOtherPropsMissedTest() {
        String shorthandExpression = "url('img.gif') 5px/5% repeat-x fixed," +
                " repeat-y border-box border-box local," +
                "url('img3.gif') center/cover padding-box padding-box red";
        Set<String> expectedResolvedProperties = new HashSet<>(Arrays.asList(
                "background-color: red",
                "background-image: url('img.gif'),none,url('img3.gif')",
                "background-position: 5px,0% 0%,center",
                "background-size: 5%,auto,cover",
                "background-repeat: repeat-x,repeat-y,repeat",
                "background-origin: padding-box,border-box,padding-box",
                "background-clip: border-box,border-box,padding-box",
                "background-attachment: fixed,local,scroll"
        ));

        IShorthandResolver backgroundResolver =
                ShorthandResolverFactory.getShorthandResolver(com.itextpdf.styledxmlparser.css.CommonCssConstants.BACKGROUND);
        assertNotNull(backgroundResolver);
        List<CssDeclaration> resolvedShorthandProps = backgroundResolver.resolveShorthand(shorthandExpression);
        CssShorthandResolverTest.compareResolvedProps(resolvedShorthandProps, expectedResolvedProperties);
    }
}
