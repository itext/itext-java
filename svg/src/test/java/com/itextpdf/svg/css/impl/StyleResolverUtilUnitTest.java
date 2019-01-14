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
package com.itextpdf.svg.css.impl;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

@Category(UnitTest.class)
public class StyleResolverUtilUnitTest {

    @Test
    public void mergeParentDeclarationsFillTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="fill";
        String parentPropValue ="blue";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,parentPropValue);
        StyleResolverUtil sru = new StyleResolverUtil();

        sru.mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }

    @Test
    public void mergeParentDeclarationsMeasurementDoNotInheritTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="font-size";
        styles.put(styleProperty,"12px");
        String parentPropValue ="16cm";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,"12px");
        StyleResolverUtil sru = new StyleResolverUtil();

        sru.mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }


    @Test
    public void mergeParentDeclarationsMeasurementInheritTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="font-size";
        String parentPropValue ="16cm";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,parentPropValue);
        StyleResolverUtil sru = new StyleResolverUtil();

        sru.mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }


    @Test
    public void mergeParentDeclarationsRelativeMeasurementInheritTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="font-size";
        String parentPropValue ="80%";
        String parentFontSize="16";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,"9.6pt");
        StyleResolverUtil sru = new StyleResolverUtil();

        sru.mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }

    @Test
    public void mergeParentDeclarationsTextDecorationsTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="text-decoration";

        styles.put(styleProperty,"strikethrough");
        String parentPropValue ="underline";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,"strikethrough underline");
        StyleResolverUtil sru = new StyleResolverUtil();

        sru.mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }




}
