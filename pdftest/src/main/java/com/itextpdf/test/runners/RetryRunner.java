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
            } catch (Throwable e) {
                System.out.println("Test Failed on attempt #" + (failedAttempts + 1));
                failedAttempts++;
                caughtThrowable = e;
            }
        }
        notifier.addFailure(caughtThrowable);
    }
}
