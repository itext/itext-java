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
package com.itextpdf.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Utility class for running actions in parallel using multiple threads.
 * This class provides a method to execute a list of Callable actions in parallel
 * and collect their results.
 */
public class MultiThreadingUtil {

    private MultiThreadingUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Runs a list of Callable actions in parallel using a fixed thread pool.
     *
     * @param actions         the list of Callable actions to be executed
     * @param numberOfThreads the number of threads to use for parallel execution
     * @param <T>             the type of the result returned by the Callable actions
     *
     * @return a list of results from the executed actions
     */
    public static <T> List<T> runActionsParallel(List<Callable<T>> actions, int numberOfThreads) {
        final int amountOfThreads = Math.max(
                Math.min(numberOfThreads, Runtime.getRuntime().availableProcessors()), 1);
        ExecutorService service = Executors.newFixedThreadPool(amountOfThreads);
        try {
            List<T> results = new ArrayList<>();
            List<Future<T>> f = service.invokeAll(actions);
            for (Future<T> tFuture : f) {
                try {
                    T future = tFuture.get();
                    if (future != null) {
                        results.add(future);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error while executing action in parallel", e);
                }
            }
            return results;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while executing actions in parallel", e);
        } finally {
            service.shutdown();
        }
    }

}
