package com.itextpdf.kernel.pdf.function;

import java.io.IOException;

@FunctionalInterface
public interface IOutputConversionFunction {
    byte[] convert(double[] input) throws IOException;
}
