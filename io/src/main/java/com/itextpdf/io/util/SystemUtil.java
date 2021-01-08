/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class SystemUtil {

    private final static String SPLIT_REGEX = "((\".+?\"|[^'\\s]|'.+?')+)\\s*";

    /**
     * @deprecated To be removed in iText version 7.2. For time-based seed, please use {@link #getTimeBasedSeed()} instead.
     */
    @Deprecated
    public static long getSystemTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long getTimeBasedSeed() {
        return System.currentTimeMillis();
    }

    public static int getTimeBasedIntSeed() {
        return (int) System.currentTimeMillis();
    }

    /**
     * Should be used in relative constructs (for example to check how many milliseconds have passed).
     * <p>
     * Shouldn't be used in the Date creation since the value returned by this method is different in С#.
     * For getting current time consistently use {@link DateTimeUtil#getCurrentTimeDate()}.
     *
     * @return relative time in milliseconds.
     */
    public static long getRelativeTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * Gets either java property or environment variable with given name.
     *
     * @param name the name of either java property or environment variable.
     * @return property or variable value or null if there is no such.
     */
    public static String getPropertyOrEnvironmentVariable(String name) {
        String s = System.getProperty(name);
        if (s == null) {
            s = System.getenv(name);
        }
        return s;
    }

    public static boolean runProcessAndWait(String exec, String params) throws IOException, InterruptedException {
        return runProcessAndWait(exec, params, null);
    }

    public static boolean runProcessAndWait(String exec, String params, String workingDirPath) throws IOException, InterruptedException {
        return runProcessAndGetExitCode(exec, params, workingDirPath) == 0;
    }

    public static int runProcessAndGetExitCode(String exec, String params) throws IOException, InterruptedException {
        return runProcessAndGetExitCode(exec, params, null);
    }

    public static int runProcessAndGetExitCode(String exec, String params, String workingDirPath) throws IOException, InterruptedException {
        Process p = runProcess(exec, params, workingDirPath);
        System.out.println(getProcessOutput(p));
        return p.waitFor();
    }

    public static String runProcessAndGetOutput(String command, String params) throws IOException {
        return getProcessOutput(runProcess(command, params, null));
    }

    public static StringBuilder runProcessAndCollectErrors(String execPath, String params) throws IOException {
        return printProcessErrorsOutput(runProcess(execPath, params, null));
    }

    static Process runProcess(String execPath, String params, String workingDirPath) throws IOException {
        List<String> cmdList = prepareProcessArguments(execPath, params);
        String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
        if (workingDirPath != null) {
            File workingDir = new File(workingDirPath);
            return Runtime.getRuntime().exec(cmdArray, null, workingDir);
        } else {
            return Runtime.getRuntime().exec(cmdArray);
        }
    }

    static List<String> prepareProcessArguments(String exec, String params) {
        List<String> cmdList;
        if (new File(exec).exists()) {
            cmdList = new ArrayList<>(Collections.singletonList(exec));
        } else {
            cmdList = new ArrayList<>(splitIntoProcessArguments(exec));
        }
        cmdList.addAll(splitIntoProcessArguments(params));
        return cmdList;
    }

    static List<String> splitIntoProcessArguments(String line) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile(SPLIT_REGEX).matcher(line);
        while (m.find()) {
            list.add(m.group(1).replace("'", "").replace("\"", "").trim());
        }
        return list;
    }

    static String getProcessOutput(Process p) throws IOException {
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bri.readLine()) != null) {
            result.append(line);
        }
        bri.close();
        if (result.length() > 0) {
            result.append('\n');
        }
        while ((line = bre.readLine()) != null) {
            result.append(line);
        }
        bre.close();
        return result.toString();
    }

    static StringBuilder printProcessErrorsOutput(Process p) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line;
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
            builder.append(line);
        }
        bre.close();
        return builder;
    }
}
