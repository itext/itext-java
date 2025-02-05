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
package com.itextpdf.test.annotations;

import com.itextpdf.test.LogListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Container annotation for {@link LogMessage} objects. This type triggers the
 * {@link LogListener} algorithm which checks whether log messages were called
 * the required number of times.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMessages {
    /**
     * Container for the log messages.
     * @return an array of {@link LogMessage} objects
     */
    LogMessage[] messages();
    
    /**
     * Defines whether the {@link LogListener} algorithm should be ignored. If
     * ignored, no checks will be done on the log messages for this test.
     *
     * Note that even if you set this parameter to {@code true}, you still have to specify
     * all potential log messages in {@link LogMessage} annotations for now, otherwise
     * {@link LogListener} will report issues.
     * 
     * Defaults to {@code false}.
     * 
     * @return whether to ignore the {@link LogListener} algorithm
     */
    boolean ignore() default false;
}
