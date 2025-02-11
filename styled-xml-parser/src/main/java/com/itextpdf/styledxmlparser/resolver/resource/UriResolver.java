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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.commons.utils.MessageFormatUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities class to resolve URIs.
 */
public class UriResolver {

    /**
     * The base url.
     */
    private URL baseUrl;

    /**
     * Indicates if the Uri refers to a local resource.
     */
    private boolean isLocalBaseUri;

    /**
     * Creates a new {@link UriResolver} instance.
     *
     * @param baseUri the base URI
     */
    public UriResolver(String baseUri) {
        if (baseUri == null) throw new IllegalArgumentException("baseUri");
        resolveBaseUrlOrPath(baseUri);
    }

    /**
     * Gets the base URI.
     *
     * @return the base uri
     */
    public String getBaseUri() {
        return baseUrl.toExternalForm();
    }

    /**
     * Resolve a given URI against the base URI.
     *
     * @param uriString the given URI
     * @return the resolved URI
     * @throws MalformedURLException the malformed URL exception
     */
    public URL resolveAgainstBaseUri(String uriString) throws MalformedURLException {
        URL resolvedUrl = null;
        uriString = uriString.trim();
        // decode and then encode uri string in order to process unsafe characters correctly
        uriString = UriEncodeUtil.encode(uriString);
        if (isLocalBaseUri) {
            if (!uriString.startsWith("file:")) {
                try {
                    Path path = Paths.get(uriString);
                    // In general this check is for windows only, in order to handle paths like "c:/temp/img.jpg".
                    // What concerns unix paths, we already removed leading slashes,
                    // therefore we can't meet here an absolute path.
                    if (path.isAbsolute()) {
                        resolvedUrl = path.toUri().toURL();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (resolvedUrl == null) {
            resolvedUrl = new URL(baseUrl, uriString);
        }
        return resolvedUrl;
    }

    /**
     * Check if baseURI is local
     *
     * @return true if baseURI is local, otherwise false
     */
    public boolean isLocalBaseUri() {
        return isLocalBaseUri;
    }

    /**
     * Resolves the base URI to an URL or path.
     *
     * @param base the base URI
     */
    private void resolveBaseUrlOrPath(String base) {
        base = base.trim();
        baseUrl = baseUriAsUrl(UriEncodeUtil.encode(base));
        if (baseUrl == null) {
            baseUrl = uriAsFileUrl(base);
        }

        if (baseUrl == null) {
            throw new IllegalArgumentException(MessageFormatUtil.format("Invalid base URI: {0}", base));
        }
    }

    /**
     * Resolves a base URI as an URL.
     *
     * @param baseUriString the base URI
     * @return the URL, or null if not successful
     */
    private URL baseUriAsUrl(String baseUriString) {
        URL baseAsUrl = null;
        try {
            URI baseUri = new URI(baseUriString);
            if (baseUri.isAbsolute()) {
                baseAsUrl = baseUri.toURL();

                if ("file".equals(baseUri.getScheme())) {
                    isLocalBaseUri = true;
                }
            }
        } catch (Exception ignored) {
        }
        return baseAsUrl;
    }

    /**
     * Resolves a base URI as a file URL.
     *
     * @param baseUriString the base URI
     * @return the file URL
     */
    private URL uriAsFileUrl(String baseUriString) {
        URL baseAsFileUrl = null;
        try {
            Path path = Paths.get(baseUriString);
            if (isPathRooted(path, baseUriString)) {
                String str = "file:///" + encode(path, path.toAbsolutePath().normalize().toString());
                baseAsFileUrl = new URI(str).toURL();
            } else {
                String str = encode(path, baseUriString);
                URL base = Paths.get("").toUri().toURL();
                baseAsFileUrl = new URL(base, str);
            }
            isLocalBaseUri = true;
        } catch (Exception ignored) {
        }

        return baseAsFileUrl;
    }

    private String encode(Path path, String str) {
        str = str.replace("\\", "/");
        str = UriEncodeUtil.encode(str);
        if (Files.isDirectory(path) && !str.endsWith("/")) {
            str += "/";
        }
        str = str.replaceFirst("/*\\\\*", "");
        return str;
    }

    private boolean isPathRooted(Path path, String str) {
        return path.isAbsolute() || str.startsWith("/");
    }
}
