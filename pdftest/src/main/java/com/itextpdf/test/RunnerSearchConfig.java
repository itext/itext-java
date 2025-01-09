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

import java.util.ArrayList;
import java.util.List;

public class RunnerSearchConfig {

    private List<String> searchPackages = new ArrayList<>();
    private List<String> searchClasses = new ArrayList<>();
    private List<String> ignoredPaths = new ArrayList<>();

    /**
     * Add package to search path which is checked for wrapped sample classes.
     * @param fullName full name of package to be checked.
     * @return this RunnerSearchConfig
     */
    public RunnerSearchConfig addPackageToRunnerSearchPath(String fullName) {
        searchPackages.add(fullName);
        return this;
    }

    /**
     * Add class to runner.
     * @param fullName full name of class to be checked.
     * @return this RunnerSearchConfig
     */
    public RunnerSearchConfig addClassToRunnerSearchPath(String fullName) {
        searchClasses.add(fullName);
        return this;
    }

    /**
     * Add package or class to ignore list. Items from this list won't be checked for wrapped sample classes.
     * @param name full or partial name of the package or class to be omitted by this runner.
     *             E.g. "highlevel.appendix" or "com.itextpdf.com.itextpdf.highlevel.appendix.TableProperties".
     * @return this RunnerSearchConfig
     */
    public RunnerSearchConfig ignorePackageOrClass(String name) {
        ignoredPaths.add(name);
        return this;
    }

    public List<String> getSearchPackages() { return searchPackages; }
    public List<String> getSearchClasses() { return searchClasses; }
    public List<String> getIgnoredPaths() { return ignoredPaths; }
}
