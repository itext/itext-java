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
package com.itextpdf.test.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UriUtilTest {

    @Test
    void parseQueryParams01() {
        String query = "param1=value1&param2=value2";
        assertEquals(2, UriUtil.parseQueryParams(query).size());
    }

    @Test
    void parseQueryParams02() {
        String query = "param1=value1&param2=value2";
        assertEquals("value1", UriUtil.parseQueryParams(query).get("param1"));
    }

    @Test
    void parseQueryParams03() {
        String query = "param1=value1&param2=value2";
        assertEquals("value2", UriUtil.parseQueryParams(query).get("param2"));
    }

    @Test
    void parseQueryParams04() {
        String query = "param1=value1&param2=value2";
        assertNull(UriUtil.parseQueryParams(query).get("param3"));
    }

    @Test
    void parseQueryParams05() {
        String query = "param1=value1&param2=value2&param3=value3";
        assertEquals("value3", UriUtil.parseQueryParams(query).get("param3"));
    }
}