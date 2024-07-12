/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

public abstract class WrappedSamplesRunner {

    public RunnerParams sampleClassParams;

    protected Class<?> sampleClass;
    private String errorMessage;

    public static Collection<Object[]> generateTestsList() {
        return generateTestsList(new RunnerSearchConfig().addPackageToRunnerSearchPath(""));
    }
    public static Collection<Object[]> generateTestsList(RunnerSearchConfig searchConfig) {
        List<Object[]> params = new ArrayList<Object[]>();
        for (String searchPath : searchConfig.getSearchPackages()) {
            File classesFolder = Paths.get("target/classes", searchPath.replace(".", "/")).toFile();
            File testClassesFolder = Paths.get("target/test-classes", searchPath.replace(".", "/")).toFile();
            if (!searchPath.isEmpty())  searchPath += ".";
            List<RunnerParams> samplesParamsList = getClassNamesRecursively(classesFolder, searchPath, searchConfig);
            samplesParamsList.addAll(getClassNamesRecursively(testClassesFolder, searchPath, searchConfig));
            for (RunnerParams sampleParam : samplesParamsList) {
                params.add(new RunnerParams[] {sampleParam});
            }
        }
        for (String className : searchConfig.getSearchClasses()) {
            params.add(new RunnerParams[] {checkIfTestAndCreateParams(className, searchConfig)});
        }

        return params;
    }

    public void runSamples() throws Exception {
        Assumptions.assumeTrue(sampleClassParams.ignoreMessage == null, sampleClassParams.ignoreMessage);

        initClass();
        System.out.println("Starting test " + sampleClassParams);

        runMain();

        String dest = getDest();
        String cmp = getCmpPdf(dest);
        if (dest == null || dest.isEmpty()) {
            throw new IllegalArgumentException("Can't verify results, DEST field must not be empty!");
        }

        String outPath = getOutPath(dest);
        new File(outPath).mkdirs();

        System.out.println("Test executed successfully, comparing results...");
        comparePdf(outPath, dest, cmp);

        if (errorMessage != null)
            Assertions.fail(errorMessage);
        System.out.println("Test complete.");
    }

    /**
     * Compares two PDF files using iText's CompareTool.
     * @param outPath       The path to the working folder where comparison results and temp files will be created
     * @param dest          The PDF that resulted from the test
     * @param cmp           The reference PDF
     * @throws Exception    If there is a problem opening the compare files
     */
    protected abstract void comparePdf(String outPath, String dest, String cmp) throws Exception;

    /**
     * Gets the path to the resulting PDF from the sample class;
     * @return	a path to a resulting PDF
     */
    protected String getDest() {
        return getStringField(sampleClass, "DEST");
    }

    protected String getCmpPdf(String dest) {
        if (dest == null)
            return null;
        int i = dest.lastIndexOf("/");
        return "./cmpfiles/" + dest.substring(8, i + 1) + "cmp_" + dest.substring(i + 1);
    }

    protected String getOutPath(String dest) {
        return "./target/" + new File(dest).getParent();
    }

    /**
     * Returns a string value that is stored as a static variable
     * inside an example class.
     * @param c     The example class
     * @param name	The name of the variable
     * @return	    The value of the variable
     */
    protected static String getStringField(Class<?> c, String name) {
        try {
            Field field = c.getField(name);
            if (field == null)
                return null;
            Object obj = field.get(null);
            if (obj == null || ! (obj instanceof String))
                return null;
            return (String)obj;
        }
        catch(Exception e) {
            return null;
        }
    }

    /**
     * Creates a Class object for the example you want to test.
     */
    protected void initClass() {
        if (sampleClass == null) {
            try {
                sampleClass = Class.forName(sampleClassParams.className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(sampleClassParams.className + " not found");
            }
        }
    }

    /**
     * Helper method to construct error messages.
     * @param	error	part of an error message.
     */
    protected void addError(String error) {
        if (error != null && error.length() > 0) {
            if (errorMessage == null)
                errorMessage = "";
            else
                errorMessage += "\n";

            errorMessage += error;
        }
    }

    protected void runMain() throws IllegalAccessException, InvocationTargetException {
        Method mainMethod = getMain(sampleClass);
        if (mainMethod == null) {
            throw new IllegalArgumentException("Class must have main method.");
        }
        mainMethod.invoke(null, new Object[] {null});
    }

    private static Method getMain(Class<?> c) {
        try {
            return c.getDeclaredMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static List<RunnerParams> getClassNamesRecursively(File path, String currentFullName, RunnerSearchConfig searchConfig) {
        List<RunnerParams> runnerParams = new ArrayList<RunnerParams>();
        File[] files = path.listFiles();
        if (files == null)
            return runnerParams;

        for (File file : files) {
            if (file.isDirectory()) {
                String[] splitted = file.getAbsolutePath().replace("\\", "/").split("/");
                String packageName = splitted[splitted.length - 1];
                runnerParams.addAll(getClassNamesRecursively(file, currentFullName + packageName + ".", searchConfig));
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    String className = currentFullName + fileName.replace(".class", "");
                    RunnerParams params = checkIfTestAndCreateParams(className, searchConfig);
                    if (params != null) {
                        runnerParams.add(params);
                    }
                }
            }
        }
        return runnerParams;
    }

    private static RunnerParams checkIfTestAndCreateParams(String className, RunnerSearchConfig searchConfig) {
        if (isIgnoredClassOrPackage(className, searchConfig)) {
            return null;
        }

        Class<?> c;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(MessageFormat.format("Cannot find class {0}", className), e);
        }

        RunnerParams params = new RunnerParams();
        params.className = className;

        return params;
    }

    private static boolean isIgnoredClassOrPackage(String fullName, RunnerSearchConfig searchConfig) {
        for (String ignoredPath : searchConfig.getIgnoredPaths()) {
            File currentFile = getFileByLocation("target/classes", ignoredPath);

            if (currentFile == null) {
                currentFile = getFileByLocation("target/test-classes", ignoredPath);
            }

            if (currentFile != null) {
                if ((currentFile.isDirectory() && fullName.contains(ignoredPath))
                        || (currentFile.isFile() && fullName.equals(ignoredPath))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static File getFileByLocation(String targetSubDirectory, String filePath) {
        File currentFile = Paths.get(targetSubDirectory, filePath.replace(".", "/")).toFile();
        if (currentFile.exists()) {
            return currentFile;
        }

        currentFile = Paths.get(targetSubDirectory, (filePath.replace(".", "/")) + ".class").toFile();
        if (currentFile.exists()) {
            return currentFile;
        }

        return null;
    }

    protected static class RunnerParams {
        String className;
        String ignoreMessage;

        @Override
        public String toString() {
            return className;
        }
    }
}
