/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
     * @return this RunnerSearchConfig
     */
    public RunnerSearchConfig addPackageToRunnerSearchPath(String fullName) {
        searchPackages.add(fullName);
        return this;
    }

    /**
     * Add class to runner if it has {@link WrapToTest} annotation.
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

    /**
     * If a class was found in search path, and it has DEST field and main method, but it doesn't have
     * WrapToTest annotation, this test will be marked as ignored with corresponding message in case this option is used.
     * @return this RunnerSearchConfig
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
