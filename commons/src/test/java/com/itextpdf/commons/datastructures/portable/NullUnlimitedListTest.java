/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.commons.datastructures.portable;


import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.commons.datastructures.NullUnlimitedList;
import com.itextpdf.commons.datastructures.SimpleArrayList;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Tag("UnitTest")
public class NullUnlimitedListTest extends ExtendedITextTest {


    @Test
    public void nullUnlimitedListAddTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add("bye");
        Assertions.assertEquals(2, list.size());
        list.add(-1, "hello");
        list.add(3, "goodbye");
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void nullUnlimitedListIndexOfTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add(null);
        list.add("bye");
        list.add(null);
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(1, list.indexOf(null));
    }

    @Test
    public void nullUnlimitedListRemoveTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add("bye");
        Assertions.assertEquals(2, list.size());
        list.remove(-1);
        list.remove(2);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void testIsEmpty() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        Assertions.assertTrue(list.isEmpty());
        list.add("hey");
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void testSameBehaviour01() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> Assertions.assertEquals(1, list.indexOf(null)));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour02() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> Assertions.assertEquals(4, list.size()));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour03() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> list.set(1, "4"));
        actionList.add((list) -> Assertions.assertEquals(list.get(1), "4"));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour04() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add(null));
        actionList.add((list) -> Assertions.assertEquals(1, list.indexOf(null)));

        executeActions(actionList);
    }


    @Test
    public void testSameBehaviour05() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> Assertions.assertEquals(-1, list.indexOf(null)));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour06() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> list.add("5"));
        actionList.add((list) -> Assertions.assertEquals(4, list.indexOf("5")));

        executeActions(actionList);
    }


    @Test
    public void testSameBehaviour07() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> list.add("5"));
        actionList.add((list) -> Assertions.assertEquals(-1, list.indexOf("6")));

        executeActions(actionList);
    }


    @Test
    public void testSameBehaviour08() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> list.add(2, "5"));
        actionList.add((list) -> Assertions.assertEquals(5, list.size()));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour09() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> list.set(2, null));
        actionList.add((list) -> Assertions.assertEquals(4, list.size()));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour10() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> list.remove(2));
        actionList.add((list) -> Assertions.assertEquals(3, list.size()));

        executeActions(actionList);
    }

    @Test
    public void testSameBehaviour11() {
        List<Consumer<ISimpleList<String>>> actionList = new ArrayList<>();
        actionList.add((list) -> Assertions.assertTrue(list.isEmpty()));
        actionList.add((list) -> list.add("1"));
        actionList.add((list) -> Assertions.assertFalse(list.isEmpty()));
        actionList.add((list) -> list.add("2"));
        actionList.add((list) -> list.add("3"));
        actionList.add((list) -> list.add("4"));
        actionList.add((list) -> Assertions.assertFalse(list.isEmpty()));

        executeActions(actionList);
    }

    public void executeActions(List<Consumer<ISimpleList<String>>> actionList) {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        SimpleArrayList<String> list2 = new SimpleArrayList<>();
        for (Consumer<ISimpleList<String>> action : actionList) {
            action.accept(list);
            action.accept(list2);
        }
    }


}