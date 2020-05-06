package com.itextpdf.styledxmlparser.util;

import com.itextpdf.styledxmlparser.css.resolve.CssInheritance;
import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class StyleUtilUnitTest extends ExtendedITextTest {

    private static Set<IStyleInheritance> inheritanceRules;

    @BeforeClass
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

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

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

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }

    @Test
    public void mergeParentDeclarationsTextDecorationsTest(){
        Map<String, String> styles = new HashMap<>();
        String styleProperty="text-decoration-line";

        styles.put(styleProperty,"line-through");
        String parentPropValue ="underline";
        String parentFontSize="0";

        Map<String,String> expectedStyles = new HashMap<String,String>();
        expectedStyles.put(styleProperty,"line-through underline");

        styles = StyleUtil
                .mergeParentStyleDeclaration(styles,styleProperty,parentPropValue,parentFontSize, inheritanceRules);

        boolean equal = styles.size() == expectedStyles.size();

        for (Map.Entry<String, String> kvp : expectedStyles.entrySet()) {
            equal &= kvp.getValue().equals(styles.get(kvp.getKey()));
        }
        Assert.assertTrue(equal);
    }
}
