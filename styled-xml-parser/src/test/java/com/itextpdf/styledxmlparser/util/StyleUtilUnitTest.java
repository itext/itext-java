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
package com.itextpdf.styledxmlparser.util;

import com.itextpdf.styledxmlparser.css.resolve.CssInheritance;
import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class StyleUtilUnitTest extends ExtendedITextTest {

    private static Set<IStyleInheritance> inheritanceRules;

    @BeforeAll
    public static void before() {
        inheritanceRules = new HashSet<>();
        inheritanceRules.add(new CssInheritance());
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

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assertions.assertTrue(equal);
    }


    @Test
    public void mergeParentDeclarationsMeasurementInheritTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="font-size";
        String parentPropValue ="16cm";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,parentPropValue);

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assertions.assertTrue(equal);
    }


    @Test
    public void mergeParentDeclarationsRelativeMeasurementInheritTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="font-size";
        String parentPropValue ="80%";
        String parentFontSize="16";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,"9.6pt");

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assertions.assertTrue(equal);
    }

}
