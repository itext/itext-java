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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.utils.Action;
import com.itextpdf.commons.utils.ThrowingAction;
import com.itextpdf.commons.utils.ThrowingSupplier;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.util.function.Function;
import java.util.function.Supplier;

final class SafeCalling {

    private SafeCalling() {}

    /**
     * Adds a report item to the report when an exception is thrown in the action.
     * @param action            The action to perform
     * @param report            The report to add the ReportItem to
     * @param reportItemCreator A callback to generate a ReportItem
     */
    public static void onExceptionLog(ThrowingAction action, ValidationReport report,
                                      Function<Exception, ReportItem> reportItemCreator) {
        try {
            action.execute();
        } catch (Exception e) {
            report.addReportItem(reportItemCreator.apply(e));
        }
    }

    /**
     * Adds a report item to the report when an exception is thrown in the action.
     * @param action            The action to perform
     * @param defaultValue      The value to return when an exception is thrown
     * @param report            The report to add the ReportItem to
     * @param reportItemCreator A callback to generate a ReportItem
     *
     * @return The returned value from the action
     * @param <T>
     */
    public static <T> T onExceptionLog(ThrowingSupplier<T> action, T defaultValue, ValidationReport report,
                                       Function<Exception, ReportItem> reportItemCreator) {
        try {
            return action.get();
        } catch (Exception e) {
            report.addReportItem(reportItemCreator.apply(e));
        }
        return defaultValue;
    }

    /**
     * Adds a report item to the report when an exception is thrown in the action.
     * @param action            The action to perform
     * @param report            The report to add the ReportItem to
     * @param reportItemCreator A callback to generate a ReportItem
     */
    public static void onRuntimeExceptionLog(Action action, ValidationReport report,
                                             Function<Exception, ReportItem> reportItemCreator) {
        try {
            action.execute();
        } catch (RuntimeException e) {
            report.addReportItem(reportItemCreator.apply(e));
        }
    }


    /**
     * Adds a report item to the report when an exception is thrown in the action.
     * @param action            The action to perform
     * @param defaultValue      The value to return when an exception is thrown
     * @param report            The report to add the ReportItem to
     * @param reportItemCreator A callback to generate a ReportItem
     *
     * @return The returned value from the action
     * @param <T>
     */
    public static <T> T onRuntimeExceptionLog(Supplier<T> action, T defaultValue, ValidationReport report,
                                              Function<Exception, ReportItem> reportItemCreator) {
        try {
            return action.get();
        } catch (RuntimeException e) {
            report.addReportItem(reportItemCreator.apply(e));
        }
        return defaultValue;
    }
}
