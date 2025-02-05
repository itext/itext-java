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
package com.itextpdf.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This class is used for testing when logger output should be tested as well.
 * By default any logger output that is not expected, i.e. marked with {@link com.itextpdf.test.annotations.LogMessage},
 * will result in crash.
 */
@ExtendWith(LogListener.class)
public abstract class ExtendedITextTest extends ITextTest {

    /**
     * This method is called before each test method is executed
     */
    @BeforeEach
    public void beforeTestMethodAction(){
    }

    /**
     * This method is called after each test method is executed
     */
    @AfterEach
    public void afterTestMethodAction(){
    }
}
