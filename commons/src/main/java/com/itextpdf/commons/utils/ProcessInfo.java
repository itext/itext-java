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

/**
 * Class contains a process information, such as process exit code and process output.
 */
public final class ProcessInfo {

    private final int exitCode;
    private final String processStdOutput;
    private final String processErrOutput;

    /**
     * Create a new instance, containing a process information,
     * such as process exit code, process standard and error outputs.
     *
     * @param exitCode      exit code of the process.
     * @param processStdOutput the standard output of the process.
     * @param processErrOutput the error output of the process.
     */
    public ProcessInfo(int exitCode, String processStdOutput, String processErrOutput) {
        this.exitCode = exitCode;
        this.processStdOutput = processStdOutput;
        this.processErrOutput = processErrOutput;
    }

    /**
     * Getter for a process exit code.
     *
     * @return Returns a process exit code.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Getter for a standard process output.
     *
     * @return Returns a process standard output string.
     */
    public String getProcessStdOutput() {
        return processStdOutput;
    }

    /**
     * Getter for an error process output.
     *
     * @return Returns a process error output string.
     */
    public String getProcessErrOutput() {
        return processErrOutput;
    }
}
