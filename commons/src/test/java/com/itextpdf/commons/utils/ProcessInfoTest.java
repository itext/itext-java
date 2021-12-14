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
