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
