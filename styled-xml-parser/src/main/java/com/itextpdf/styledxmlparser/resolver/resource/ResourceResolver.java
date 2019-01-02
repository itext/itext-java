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


import com.itextpdf.io.codec.Base64;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utilities class to resolve resources.
 */
// TODO handle <base href=".."> tag?
public class ResourceResolver {

    /** The {@link UriResolver} instance. */
    private UriResolver uriResolver;

    /** The {@link SimpleImageCache} instance. */
    // TODO provide a way to configure capacity, manually reset or disable the image cache?
    private SimpleImageCache imageCache;

    /** Identifier string used when loading in base64 images**/
    public static final String BASE64IDENTIFIER = "base64";

    /**
     * Creates {@link ResourceResolver} instance. If {@code baseUri} is a string that represents an absolute URI with any schema
     * except "file" - resources url values will be resolved exactly as "new URL(baseUrl, uriString)". Otherwise base URI
     * will be handled as path in local file system.
     * <p>
     * If empty string or relative URI string is passed as base URI, then it will be resolved against current working
     * directory of this application instance.
     * </p>
     *
     * @param baseUri base URI against which all relative resource URIs will be resolved.
     */
    public ResourceResolver(String baseUri) {
        if (baseUri == null) baseUri = "";
        this.uriResolver = new UriResolver(baseUri);
        this.imageCache = new SimpleImageCache();
    }

    /**
     * Retrieve {@link PdfImageXObject}.
     *
     * @param src either link to file or base64 encoded stream.
     * @return PdfImageXObject on success, otherwise null.
     * @deprecated will return {@link PdfXObject in pdfHTML 3.0.0}
     */
    @Deprecated
    public PdfImageXObject retrieveImage(String src) {
        PdfXObject image = retrieveImageExtended(src);
        if (image instanceof PdfImageXObject) {
            return (PdfImageXObject) image;
        } else {
            return null;
        }
    }

    /**
     * Retrieve image as either {@link PdfImageXObject}, or {@link com.itextpdf.kernel.pdf.xobject.PdfFormXObject}.
     *
     * @param src either link to file or base64 encoded stream.
     * @return PdfImageXObject on success, otherwise null.
     */
    public PdfXObject retrieveImageExtended(String src) {
        if (src != null) {
            if (src.contains(BASE64IDENTIFIER)) {
                PdfXObject imageXObject = tryResolveBase64ImageSource(src);
                if (imageXObject != null) {
                    return imageXObject;
                }
            }

            PdfXObject imageXObject = tryResolveUrlImageSource(src);
            if (imageXObject != null) {
                return imageXObject;
            }
        }

        Logger logger = LoggerFactory.getLogger(ResourceResolver.class);
        logger.error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI, uriResolver.getBaseUri(), src));
        return null;
    }

    /**
     * Open an {@link InputStream} to a style sheet URI.
     *
     * @param uri the URI
     * @return the {@link InputStream}
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public InputStream retrieveStyleSheet(String uri) throws IOException {
        return uriResolver.resolveAgainstBaseUri(uri).openStream();
    }

    /**
     * Deprecated: use retrieveBytesFromResource instead
     * Replaced by retrieveBytesFromResource for the sake of method name clarity.
     *
     * Retrieve a resource as a byte array from a source that
     * can either be a link to a file, or a base64 encoded {@link String}.
     *
     * @param src either link to file or base64 encoded stream.
     * @return byte[] on success, otherwise null.
     */
    @Deprecated
    public byte[] retrieveStream(String src) {
        try (InputStream stream = retrieveResourceAsInputStream(src)) {
            if (stream != null) {
                return StreamUtil.inputStreamToArray(stream);
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(ResourceResolver.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, uriResolver.getBaseUri(), src), e);
            return null;
        }
    }

    /**
     * Retrieve a resource as a byte array from a source that
     * can either be a link to a file, or a base64 encoded {@link String}.
     *
     * @param src either link to file or base64 encoded stream.
     * @return byte[] on success, otherwise null.
     */
    public byte[] retrieveBytesFromResource(String src) {
        try (InputStream stream = retrieveResourceAsInputStream(src)) {
            if (stream != null) {
                return StreamUtil.inputStreamToArray(stream);
            } else {
                return null;
            }
        } catch (IOException ioe) {
            Logger logger = LoggerFactory.getLogger(ResourceResolver.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, uriResolver.getBaseUri(), src), ioe);
            return null;
        }
    }

    /**
     * Retrieve the resource found in src as an InputStream
     * @param src path to the resource
     * @return InputStream for the resource
     */
    public InputStream retrieveResourceAsInputStream(String src){
        if (src.contains(BASE64IDENTIFIER)) {
            try {
                String fixedSrc = src.replaceAll("\\s", "");
                fixedSrc = fixedSrc.substring(fixedSrc.indexOf(BASE64IDENTIFIER) + 7);
                return new ByteArrayInputStream(Base64.decode(fixedSrc));
            } catch (Exception ignored) {
            }
        }

        try {
            return uriResolver.resolveAgainstBaseUri(src).openStream();
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(ResourceResolver.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, uriResolver.getBaseUri(), src), e);
            return null;
        }
    }


    /**
     * Resolves a given URI against the base URI.
     *
     * @param uri the uri
     * @return the url
     * @throws MalformedURLException the malformed URL exception
     */
    public URL resolveAgainstBaseUri(String uri) throws MalformedURLException {
        return uriResolver.resolveAgainstBaseUri(uri);
    }

    /**
     * Resets the simple image cache.
     */
    public void resetCache() {
        imageCache.reset();
    }

    /**
     * Check if the type of image located at the passed is supported by the {@link ImageDataFactory}
     * @param src location of the image resource
     * @return true if the image type is supported, false otherwise
     */
    public boolean isImageTypeSupportedByImageDataFactory(String src) {
        try {
            URL url = uriResolver.resolveAgainstBaseUri(src);
            url = UrlUtil.getFinalURL(url);
            return ImageDataFactory.isSupportedType(url);
        } catch(Exception e){
            return false;
        }
    }

    protected PdfXObject tryResolveBase64ImageSource(String src) {
        try {
            String fixedSrc = src.replaceAll("\\s", "");
            fixedSrc = fixedSrc.substring(fixedSrc.indexOf(BASE64IDENTIFIER) + 7);
            PdfXObject imageXObject = imageCache.getImage(fixedSrc);
            if (imageXObject == null) {
                imageXObject = new PdfImageXObject(ImageDataFactory.create(Base64.decode(fixedSrc)));
                imageCache.putImage(fixedSrc, imageXObject);
            }

            return imageXObject;
        } catch (Exception ignored) {
        }
        return null;
    }

    protected PdfXObject tryResolveUrlImageSource(String src) {
        try {
            URL url = uriResolver.resolveAgainstBaseUri(src);
            url = UrlUtil.getFinalURL(url);
            String imageResolvedSrc = url.toExternalForm();
            PdfXObject imageXObject = imageCache.getImage(imageResolvedSrc);
            if (imageXObject == null) {
                imageXObject = createImageByUrl(url);
                imageCache.putImage(imageResolvedSrc, imageXObject);
            }
            return imageXObject;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Throws exception if error occurred
     */
    protected PdfXObject createImageByUrl(URL url) throws Exception {
        return new PdfImageXObject(ImageDataFactory.create(url));
    }
}
