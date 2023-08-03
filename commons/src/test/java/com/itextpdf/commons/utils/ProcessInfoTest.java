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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ProcessInfoTest extends ExtendedITextTest {

    @Test
    public void getExitCodeTest() {
        int exitCode = 1;
        ProcessInfo processInfo = new ProcessInfo(exitCode, null,  null);

        Assert.assertEquals(exitCode, processInfo.getExitCode());
    }

    @Test
    public void getProcessStdOutput() {
        String stdOutput = "output";
        ProcessInfo processInfo = new ProcessInfo(0, stdOutput,  null);

        Assert.assertEquals(stdOutput, processInfo.getProcessStdOutput());
    }

    @Test
    public void getProcessErrOutput() {
        String stdOutput = "output";
        ProcessInfo processInfo = new ProcessInfo(0, null,  stdOutput);

        Assert.assertEquals(stdOutput, processInfo.getProcessErrOutput());
    }
}
