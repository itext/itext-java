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
 * Unit Tests are used to check individual units of source code. A unit test
 * will only use the Testing Framework and the individual unit that is being
 * tested, but does not depend on any other functionality of the Software Under
 * Test.
 * <p>
 * A simple rule to determine if you have written a unit test: if your test
 * produces a PDF file and uses the CompareTool to verify it with a sample file,
 * then it is <strong>not</strong> a unit test.
 * <p>
 * Typically a unit test will run very fast.
 * 
 * @author Amedee Van Gasse
 *
 */
public interface UnitTest {
}
