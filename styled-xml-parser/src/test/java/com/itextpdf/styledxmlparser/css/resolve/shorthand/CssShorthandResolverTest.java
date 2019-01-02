/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(UnitTest.class)
public class CssShorthandResolverTest extends ExtendedITextTest {


    @Test
    public void fontTest01() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia, serif";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,serif"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest02() {
        String shorthandExpression = "bold Georgia, serif";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,serif"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest03() {
        String shorthandExpression = "inherit";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: inherit",
                "font-variant: inherit",
                "font-weight: inherit",
                "font-size: inherit",
                "line-height: inherit",
                "font-family: inherit"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest04() {
        String shorthandExpression = "bold Georgia, serif, \"Times New Roman\"";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,serif,\"Times New Roman\""
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest05() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia, \"Times New Roman\", serif";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\",serif"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest06() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia    ,   \"Times New Roman\"   ,    serif";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\",serif"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest07() {
        String shorthandExpression = "italic normal bold 12px/30px Georgia    ,   \"Times New Roman\"   ";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: italic",
                "font-variant: initial",
                "font-weight: bold",
                "font-size: 12px",
                "line-height: 30px",
                "font-family: georgia,\"Times New Roman\""
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest08() {
        String shorthandExpression = "Georgia,'Times New Roman'";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: initial",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,'Times New Roman'"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    @Test
    public void fontTest09() {
        String shorthandExpression = "Georgia  ,   'Times New Roman', serif";
        Set<String> expectedResolvedProperties = new HashSet<>( Arrays.asList(
                "font-style: initial",
                "font-variant: initial",
                "font-weight: initial",
                "font-size: initial",
                "line-height: initial",
                "font-family: georgia,'Times New Roman',serif"
        ) );

        IShorthandResolver resolver = ShorthandResolverFactory.getShorthandResolver( CommonCssConstants.FONT );
        assertNotNull( resolver );
        List<CssDeclaration> resolvedShorthandProps = resolver.resolveShorthand( shorthandExpression );
        compareResolvedProps( resolvedShorthandProps, expectedResolvedProperties );
    }

    private void compareResolvedProps(List<CssDeclaration> actual, Set<String> expected) {
        Set<String> actualSet = new HashSet<>();
        for (CssDeclaration cssDecl : actual) {
            actualSet.add( cssDecl.toString() );
        }

        boolean areDifferent = false;

        StringBuilder sb = new StringBuilder( "Resolved styles are different from expected!" );
        Set<String> expCopy = new TreeSet<>( expected );
        Set<String> actCopy = new TreeSet<>( actualSet );
        expCopy.removeAll( actualSet );
        actCopy.removeAll( expected );
        if (!expCopy.isEmpty()) {
            areDifferent = true;
            sb.append( "\nExpected but not found properties:\n" );
            for (String expProp : expCopy) {
                sb.append( expProp ).append( '\n' );
            }
        }
        if (!actCopy.isEmpty()) {
            areDifferent = true;
            sb.append( "\nNot expected but found properties:\n" );
            for (String actProp : actCopy) {
                sb.append( actProp ).append( '\n' );
            }
        }

        if (areDifferent) {
            fail( sb.toString() );
        }
    }
}
