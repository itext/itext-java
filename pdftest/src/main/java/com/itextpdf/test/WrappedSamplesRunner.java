package com.itextpdf.test;

import com.itextpdf.test.annotations.WrapToTest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class WrappedSamplesRunner {

    @Parameterized.Parameter
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
            if (!searchPath.isEmpty())  searchPath += ".";
            List<RunnerParams> samplesParamsList = getClassNamesRecursively(classesFolder, searchPath, searchConfig);
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
        Assume.assumeTrue(sampleClassParams.ignoreMessage, sampleClassParams.ignoreMessage == null);

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
            Assert.fail(errorMessage);
        System.out.println("Test complete.");
    }

    /**
     * Compares two PDF files using iText's CompareTool.
     * @param outPath path to the working folder where comparison results and temp files will be created
     * @param dest the PDF that resulted from the test
     * @param cmp the reference PDF
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
     * @param name	the name of the variable
     * @return	the value of the variable
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
     * @param	className	the class you want to test
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

    private void runMain() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Method mainMethod = getMain(sampleClass);
        if (mainMethod == null) {
            throw new IllegalArgumentException("Class marked with WrapToTest annotation must have main method.");
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
        if (!c.isAnnotationPresent(WrapToTest.class)) {
            if (searchConfig.isToMarkTestsWithoutAnnotationAsIgnored() && isLookLikeTest(c)) {
                params.ignoreMessage = MessageFormat.format("Class {0} seems to be a test but it doesn't have WrapToTest annotation.", className);
                return params;
            }
            return null;
        }

        WrapToTest annot = c.getAnnotation(WrapToTest.class);
        if (!annot.ignoreWithMessage().isEmpty()) {
            params.ignoreMessage = annot.ignoreWithMessage();
        }
        return params;
    }

    private static boolean isLookLikeTest(Class<?> c) {
        return getStringField(c, "DEST") != null && getMain(c) != null;
    }

    private static boolean isIgnoredClassOrPackage(String fullName, RunnerSearchConfig searchConfig) {
        for (String ignoredPath : searchConfig.getIgnoredPaths()) {
            if (fullName.contains(ignoredPath)) {
                return true;
            }
        }
        return false;
    }

    private static class RunnerParams {
        String className;
        String ignoreMessage;

        @Override
        public String toString() {
            return className;
        }
    }
}
