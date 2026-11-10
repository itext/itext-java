/*
    This file is part of jsoup, see NOTICE.txt in the root of the repository.
    It may contain modifications beyond the original version.
*/
package com.itextpdf.styledxmlparser.jsoup;

import java.io.IOException;

public class UncheckedIOException extends RuntimeException {
    public UncheckedIOException(IOException cause) {
        super(cause.getMessage(), cause);
    }

    public UncheckedIOException(String message) {
        super(message, new IOException(message));
    }

    public IOException ioException() {
        return (IOException) getCause();
    }
}
