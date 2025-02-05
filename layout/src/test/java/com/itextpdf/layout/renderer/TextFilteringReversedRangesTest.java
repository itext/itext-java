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
package com.itextpdf.layout.renderer;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;

@Tag("UnitTest")
public class TextFilteringReversedRangesTest extends ExtendedITextTest {

    @Test
    public void test01() {
        ArrayList<Integer> removedIds = new ArrayList<>();
        removedIds.add(0);

        int[] range = new int[] {0, 1};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assertions.assertArrayEquals(new int[] {0, 0}, range);
    }

    @Test
    public void test02() {
        ArrayList<Integer> removedIds = new ArrayList<>();
        removedIds.add(10);

        int[] range = new int[] {0, 5};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assertions.assertArrayEquals(new int[] {0, 5}, range);
    }

    @Test
    public void test03() {
        ArrayList<Integer> removedIds = new ArrayList<>();
        removedIds.add(0);
        removedIds.add(3);
        removedIds.add(10);

        int[] range = new int[] {0, 5};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assertions.assertArrayEquals(new int[] {0, 3}, range);
    }

    @Test
    public void test04() {
        ArrayList<Integer> removedIds = new ArrayList<>();
        removedIds.add(1);

        int[] range = new int[] {0, 1};
        TextRenderer.updateRangeBasedOnRemovedCharacters(removedIds, range);
        Assertions.assertArrayEquals(new int[] {0, 0}, range);
    }

}
