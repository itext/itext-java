/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.test.annotations.type;

/**
 * Tests that cover more functionality than a simple Unit Test. Contrary to Unit
 * Tests, they don't use mocked objects but real objects. These tests are
 * intended to check end-to-end functionality of a feature.
 * <p>
 * For example, if a test creates a Document, manipulates it, writes the result
 * to disk, and then compares the file with a reference document, then it is
 * definitely not a Unit Test but most likely an Integration Test.
 * 
 * @author Amedee Van Gasse
 *
 */
public interface IntegrationTest extends SlowTest {
}
