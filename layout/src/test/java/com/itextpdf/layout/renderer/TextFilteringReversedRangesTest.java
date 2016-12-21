package com.itextpdf.layout.renderer;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class TextFilteringReversedRangesTest {

    @Test
    public void test01() {
        List<Integer> removedIds = new ArrayList<>();
        removedIds.add(0);

        int[] range = new int[] {0, 1};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assert.assertArrayEquals(new int[] {0, 0}, range);
    }

    @Test
    public void test02() {
        List<Integer> removedIds = new ArrayList<>();
        removedIds.add(10);

        int[] range = new int[] {0, 5};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assert.assertArrayEquals(new int[] {0, 5}, range);
    }

    @Test
    public void test03() {
        List<Integer> removedIds = new ArrayList<>();
        removedIds.add(0);
        removedIds.add(3);
        removedIds.add(10);

        int[] range = new int[] {0, 5};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assert.assertArrayEquals(new int[] {0, 3}, range);
    }

}
