package com.itextpdf.signatures.validation.mocks;

import com.itextpdf.kernel.validation.context.XrefTableValidationContext;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

public class MockResourceRetriever implements IResourceRetriever {

    private Function<URL, byte[]> getByteArrayByUrlHandler = u -> null;
    private Function<URL, InputStream> getInputStreamByUrlHandler = u -> null;

    @Override
    public InputStream getInputStreamByUrl(URL url) throws IOException {
        return getInputStreamByUrlHandler.apply(url);
    }

    @Override
    public byte[] getByteArrayByUrl(URL url) throws IOException {
        return getByteArrayByUrlHandler.apply(url);
    }

    public MockResourceRetriever onGetInputStreamByUrl(Function<URL, InputStream> handler) {
        getInputStreamByUrlHandler = handler;
        return this;
    }

    public MockResourceRetriever onGetByteArrayByUrl(Function<URL, byte[]> handler) {
        getByteArrayByUrlHandler = handler;
        return this;
    }
}