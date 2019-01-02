/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.io.util.MessageFormatUtil;

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
        //TODO RND-1019
        // this method produces
        // a behavior that is not consistant in java vs .Net
        //when resolving some characters ex. scaped backwards lash
        base = base.trim();
        baseUrl = baseUriAsUrl(UriEncodeUtil.encode(base));
        if (baseUrl == null) {
            baseUrl = uriAsFileUrl(base);
        }

        if (baseUrl == null) {
            // TODO styledxmlparserException?
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
