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
