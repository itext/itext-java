package com.itextpdf.test;

import com.itextpdf.test.annotations.WrapToTest;
import java.util.ArrayList;
import java.util.List;

public class RunnerSearchConfig {

    private List<String> searchPackages = new ArrayList<>();
    private List<String> searchClasses = new ArrayList<>();
    private List<String> ignoredPaths = new ArrayList<>();
    private boolean isToMarkTestsWithoutAnnotationAsIgnored;

    /**
     * Add package to search path which is checked for wrapped sample classes.
     * Tests run only if they have {@link WrapToTest} annotation.
     * @param fullName full name of package to be checked.
     */
    public RunnerSearchConfig addPackageToRunnerSearchPath(String fullName) {
        searchPackages.add(fullName);
        return this;
    }

    /**
     * Add class to runner if it has {@link WrapToTest} annotation.
     * @param fullName full name of class to be checked.
     */
    public RunnerSearchConfig addClassToRunnerSearchPath(String fullName) {
        searchClasses.add(fullName);
        return this;
    }

    /**
     * Add package or class to ignore list. Items from this list won't be checked for wrapped sample classes.
     * @param name full or partial name of the package or class to be omitted by this runner.
     *             E.g. "highlevel.appendix" or "com.itextpdf.com.itextpdf.highlevel.appendix.TableProperties".
     */
    public RunnerSearchConfig ignorePackageOrClass(String name) {
        ignoredPaths.add(name);
        return this;
    }

    /**
     * If a class was found in search path, and it has DEST field and main method, but it doesn't have
     * WrapToTest annotation, this test will be marked as ignored with corresponding message in case this option is used.
     */
    public RunnerSearchConfig markTestsWithoutAnnotationAsIgnored() {
        isToMarkTestsWithoutAnnotationAsIgnored = true;
        return this;
    }

    public List<String> getSearchPackages() { return searchPackages; }
    public List<String> getSearchClasses() { return searchClasses; }
    public List<String> getIgnoredPaths() { return ignoredPaths; }
    public boolean isToMarkTestsWithoutAnnotationAsIgnored() { return isToMarkTestsWithoutAnnotationAsIgnored; }
}
