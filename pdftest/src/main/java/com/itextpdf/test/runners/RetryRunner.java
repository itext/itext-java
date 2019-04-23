/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.test.runners;

import org.junit.AssumptionViolatedException;
import org.junit.Ignore;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * This class is used for flaky test retry after failure.
 * In current implementation we use specified retryCount = 3
 */
public class RetryRunner extends BlockJUnit4ClassRunner {
    private final int retryCount = 3;
    private int failedAttempts = 0;

    public RetryRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    /**
     *
     * @param notifier RunNotifier
     */
    public void run(RunNotifier notifier) {
        EachTestNotifier eachTestNotifier = new EachTestNotifier(notifier, getDescription());
        Statement statement = classBlock(notifier);
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException ave) {
            eachTestNotifier.fireTestIgnored();
        } catch (StoppedByUserException sue) {
            throw sue;
        } catch (Throwable throwable) {
            retry(eachTestNotifier, statement, throwable);
        }
    }

    /**
     *
     * @param method FrameworkMethod
     * @param notifier RunNotifier
     */
    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestUnit(methodBlock(method), description, notifier);
        }
    }

    /**
     * Runs an atomic test
     *
     * @param statement Statement
     * @param description Description
     * @param notifier RunNotifier
     */
    protected final void runTestUnit(Statement statement, Description description,
                                     RunNotifier notifier) {
        failedAttempts = 0;
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException avee) {
            eachNotifier.addFailedAssumption(avee);
        } catch (Throwable e) {
            retry(eachNotifier, statement, e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    /**
     * Retry method, counts failed attempts, adds logging messages
     * @param notifier EachTestNotifier
     * @param statement Statement
     * @param currentThrowable Throwable
     */
    private void retry(EachTestNotifier notifier, Statement statement, Throwable currentThrowable) {
        Throwable caughtThrowable = currentThrowable;
        while (retryCount > failedAttempts) {
            try {
                statement.evaluate();
                break;
            } catch (Throwable e) {
                System.out.println("Test Failed on attempt #" + (failedAttempts + 1));
                failedAttempts++;
                caughtThrowable = e;
            }
        }
        notifier.addFailure(caughtThrowable);
    }
}
