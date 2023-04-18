package com.itextpdf.commons.datastructures;

import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BiMapTest {

    @Test
    public void sizeTest01() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void sizeTest02() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void isEmptyTest01() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertFalse(map.isEmpty());
    }


    @Test
    public void putTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertEquals(1, (int) map.getByKey("a"));
        Assert.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void putOnExistingKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("a", 2);
        Assert.assertEquals(2, (int) map.getByKey("a"));
        Assert.assertEquals("a", map.getByValue(2));
    }

    @Test
    public void putOnExistingValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 1);
        Assert.assertEquals(1, (int) map.getByKey("b"));
        Assert.assertEquals("b", map.getByValue(1));
    }

    @Test
    public void putOnExistingKeyAndValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("a", 1);
        Assert.assertEquals(1, (int) map.getByKey("a"));
        Assert.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void putMultipleValues() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        Assert.assertEquals(1, (int) map.getByKey("a"));
        Assert.assertEquals("a", map.getByValue(1));
        Assert.assertEquals(2, (int) map.getByKey("b"));
        Assert.assertEquals("b", map.getByValue(2));
        Assert.assertEquals(3, (int) map.getByKey("c"));
        Assert.assertEquals("c", map.getByValue(3));
        Assert.assertEquals(3, map.size());
    }


    @Test
    public void clearTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.clear();
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void containsKeyTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertTrue(map.containsKey("a"));
    }

    @Test
    public void containsValueTest() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertTrue(map.containsValue(1));
    }

    @Test
    public void getByValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertEquals(1, (int) map.getByKey("a"));
    }

    @Test
    public void getByKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        Assert.assertEquals("a", map.getByValue(1));
    }

    @Test
    public void removeByKey() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.removeByKey("a");
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void removeByValue() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.put("a", 1);
        map.removeByValue(1);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void removeOnEmptyMap() {
        BiMap<String, Integer> map = new BiMap<String, Integer>();
        map.removeByKey("a");
        map.removeByValue(1);
        Assert.assertEquals(0, map.size());
    }

}
