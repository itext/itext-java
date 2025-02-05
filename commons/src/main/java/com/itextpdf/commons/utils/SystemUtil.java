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
package com.itextpdf.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
     * Gets seed as long value of current time in milliseconds.
     *
     * @return current time in millis as long.
     */
    public static long getTimeBasedSeed() {
        return System.currentTimeMillis();
    }

    /**
     * Gets seed as int value of current time in milliseconds.
     *
     * @return current time in millis as int.
     */
    public static int getTimeBasedIntSeed() {
        return (int) System.currentTimeMillis();
    }

    private SystemUtil() {
        // Empty constructor.
    }

    /**
     * Should be used in relative constructs (for example to check how many milliseconds have passed).
     *
     * <p>Shouldn't be used in the Date creation since the value returned by this method is different in С#.
     * For getting current time consistently use {@link DateTimeUtil#getCurrentTimeDate()}.
     *
     * @return relative time in milliseconds.
     */
    public static long getRelativeTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Gets free available memory for JDK.
     *
     * @return available memory in bytes.
     */
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

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working directory.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is the current working directory of the current process.
     *
     * @param exec a specified system command.
     * @param params a parameters for the specifed system command.
     *
     * @return true if subprocess was successfully executed, false otherwise.
     *
     * @throws IOException if any I/O error occurs.
     * @throws InterruptedException if process was interrupted.
     */
    public static boolean runProcessAndWait(String exec, String params) throws IOException, InterruptedException {
        return runProcessAndWait(exec, params, null);
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working directory.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is specified by workingDirPath.
     * If dir is null, the subprocess inherits the current working directory of the current process.
     *
     * @param exec a specified system command.
     * @param params a parameters for the specifed system command.
     * @param workingDirPath working dir for subprocess.
     *
     * @return true if subprocess was successfully executed, false otherwise.
     *
     * @throws IOException if any I/O error occurs.
     * @throws InterruptedException if process was interrupted.
     */
    public static boolean runProcessAndWait(String exec, String params,
                                            String workingDirPath) throws IOException, InterruptedException {
        return runProcessAndGetExitCode(exec, params, workingDirPath) == 0;
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working directory.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is the current working directory of the current process.
     *
     * @param exec a specified system command.
     * @param params a parameters for the specifed system command.
     *
     * @return exit code.
     *
     * @throws IOException if any I/O error occurs.
     * @throws InterruptedException if process was interrupted.
     */
    public static int runProcessAndGetExitCode(String exec, String params) throws IOException, InterruptedException {
        return runProcessAndGetExitCode(exec, params, null);
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working directory.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is specified by workingDirPath.
     * If dir is null, the subprocess inherits the current working directory of the current process.
     *
     * @param exec a specified system command.
     * @param params a parameters for the specifed system command.
     * @param workingDirPath working dir for subprocess.
     *
     * @return exit code.
     *
     * @throws IOException if any I/O error occurs.
     * @throws InterruptedException if process was interrupted.
     */
    public static int runProcessAndGetExitCode(String exec, String params,
                                               String workingDirPath) throws IOException, InterruptedException {
        Process p = runProcess(exec, params, workingDirPath);
        System.out.println(getProcessOutput(p));
        return p.waitFor();
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working
     * directory and returns output as a string.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is specified by workingDirPath.
     * If dir is null, the subprocess inherits the current working directory of the current process.
     *
     * @param command a specified system command.
     * @param params a parameters for the specifed system command.
     *
     * @return subprocess output result.
     *
     * @throws IOException if any I/O error occurs.
     */
    public static String runProcessAndGetOutput(String command, String params) throws IOException {
        return getProcessOutput(runProcess(command, params, null));
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working
     * directory and returns output errors as a string.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is specified by workingDirPath.
     * If dir is null, the subprocess inherits the current working directory of the current process.
     *
     * @param execPath a specified system command.
     * @param params a parameters for the specifed system command.
     *
     * @return subprocess errors as {@code StringBuilder}.
     *
     * @throws IOException if any I/O error occurs.
     */
    public static StringBuilder runProcessAndCollectErrors(String execPath, String params) throws IOException {
        return printProcessErrorsOutput(runProcess(execPath, params, null));
    }

    /**
     * Executes the specified command and arguments in a separate process with the specified environment and working
     * directory and returns process info.
     * This method checks that exec is a valid operating system command. Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty and non-null.
     * The subprocess inherits the environment settings of the current process.
     * A minimal set of system dependent environment variables may be required to start a process on some operating systems.
     * The working directory of the new subprocess is specified by workingDirPath.
     * If dir is null, the subprocess inherits the current working directory of the current process.
     *
     * @param command a specified system command.
     * @param params a parameters for the specifed system command.
     *
     * @return process info instance.
     *
     * @throws IOException if any I/O error occurs.
     * @throws InterruptedException if process was interrupted.
     */
    public static ProcessInfo runProcessAndGetProcessInfo(String command, String params) throws IOException,
            InterruptedException {
        Process p = runProcess(command, params, null);
        String processStdOutput = printProcessStandardOutput(p).toString();
        String processErrOutput = printProcessErrorsOutput(p).toString();
        return new ProcessInfo(p.waitFor(), processStdOutput, processErrOutput);
    }

    static Process runProcess(String execPath, String params, String workingDirPath) throws IOException {
        List<String> cmdList = prepareProcessArguments(execPath, params);
        String[] cmdArray = cmdList.toArray(new String[0]);
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
        return printProcessOutput(p.getErrorStream());
    }

    static StringBuilder printProcessStandardOutput(Process p) throws IOException {
        return printProcessOutput(p.getInputStream());
    }

    private static StringBuilder printProcessOutput(InputStream processStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader bre = new BufferedReader(new InputStreamReader(processStream));
        String line;
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
            builder.append(line);
        }
        bre.close();
        return builder;
    }
}
