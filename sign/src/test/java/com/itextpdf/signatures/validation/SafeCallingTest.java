/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
public class SafeCallingTest extends ExtendedITextTest {

    @Test
    public void safeCallingAvoidExceptionThrowsException() {
        Exception e = assertThrows(SafeCallingAvoidantException.class, () -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new SafeCallingAvoidantException("Test exception");
                    },
                    new ValidationReport(),
                    reportItem -> new ReportItem(
                            "test", "test", ReportItemStatus.INFO
                    )
            );
        });
        assertEquals("Test exception", e.getMessage());
    }


    @Test
    public void safeCallingWithCreatorAvoidExceptionThrowsException() {
        Exception e = assertThrows(SafeCallingAvoidantException.class, () -> {
            SafeCalling.onExceptionLog(
                    () -> {
                        throw new SafeCallingAvoidantException("Test exception");
                    },
                    new ReportItem("test", "test", ReportItemStatus.INFO),
                    new ValidationReport(),
                    reportItem -> new ReportItem(
                            "test", "test", ReportItemStatus.INFO
                    )
            );
        });
        assertEquals("Test exception", e.getMessage());
    }

    @Test
    public void safeCallingAvoidExceptionDoesNotThrowException() {
        Exception exception = assertThrows(Exception.class, () -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new SafeCallingAvoidantException("Test exception");
                    },
                    new ValidationReport(),
                    reportItem -> new ReportItem(
                            "test", "test", ReportItemStatus.INFO
                    )
            );
        });
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    public void safeCallingWithFuncAvoidExceptionDoesNotThrowException() {
        Exception exception = assertThrows(Exception.class, () -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new SafeCallingAvoidantException("Test exception");
                    },
                    new ReportItem("test", "test", ReportItemStatus.INFO),
                    new ValidationReport(),
                    reportItem -> new ReportItem(
                            "test", "test", ReportItemStatus.INFO
                    )
            );
        });
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    public void safCallingWithPdfExceptionDoesNotThrowException() {
        AssertUtil.doesNotThrow(() -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new PdfException("Test exception");
                    },
                    new ValidationReport(),
                    (Exception reportItem) -> new ReportItem("test", "test", ReportItemStatus.INFO)
            );
        });
    }

    @Test
    public void npeExceptionDoesNotThrowException() {
        AssertUtil.doesNotThrow(() -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new NullPointerException("Test exception");
                    },
                    new ValidationReport(),
                    (Exception reportItem) -> new ReportItem("test", "test", ReportItemStatus.INFO)
            );
        });
    }

    @Test
    public void illegalArgumentExceptionDoesNotThrowException() {
        AssertUtil.doesNotThrow(() -> {
            SafeCalling.onRuntimeExceptionLog(
                    () -> {
                        throw new IllegalArgumentException("Test exception");
                    },
                    new ValidationReport(),
                    (Exception reportItem) -> new ReportItem("test", "test", ReportItemStatus.INFO)
            );
        });
    }
}
