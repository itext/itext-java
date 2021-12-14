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
