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

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
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
