package com.itextpdf.test.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In current implementation it has no specified retries count
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD})
public @interface RetryOnFailure {}
