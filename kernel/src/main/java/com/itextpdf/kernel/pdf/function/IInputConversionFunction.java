package com.itextpdf.kernel.pdf.function;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface IInputConversionFunction {
    double[] convert(byte[] input) throws IOException;
}
