//Copyright (c) 2006, Adobe Systems Incorporated
//All rights reserved.
//
//        Redistribution and use in source and binary forms, with or without
//        modification, are permitted provided that the following conditions are met:
//        1. Redistributions of source code must retain the above copyright
//        notice, this list of conditions and the following disclaimer.
//        2. Redistributions in binary form must reproduce the above copyright
//        notice, this list of conditions and the following disclaimer in the
//        documentation and/or other materials provided with the distribution.
//        3. All advertising materials mentioning features or use of this software
//        must display the following acknowledgement:
//        This product includes software developed by the Adobe Systems Incorporated.
//        4. Neither the name of the Adobe Systems Incorporated nor the
//        names of its contributors may be used to endorse or promote products
//        derived from this software without specific prior written permission.
//
//        THIS SOFTWARE IS PROVIDED BY ADOBE SYSTEMS INCORPORATED ''AS IS'' AND ANY
//        EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//        DISCLAIMED. IN NO EVENT SHALL ADOBE SYSTEMS INCORPORATED BE LIABLE FOR ANY
//        DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//        http://www.adobe.com/devnet/xmp/library/eula-xmp-library-java.html

package com.itextpdf.kernel.xmp;

import com.itextpdf.kernel.xmp.impl.XMPMetaImpl;
import com.itextpdf.kernel.xmp.impl.XMPMetaParser;
import com.itextpdf.kernel.xmp.impl.XMPSchemaRegistryImpl;
import com.itextpdf.kernel.xmp.impl.XMPSerializerHelper;
import com.itextpdf.kernel.xmp.options.ParseOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Creates <code>XMPMeta</code>-instances from an <code>InputStream</code>
 *
 * @since 30.01.2006
 */
public final class XMPMetaFactory {
    private static final Object staticLock = new Object();

    /**
     * The singleton instance of the <code>XMPSchemaRegistry</code>.
     */
    private static XMPSchemaRegistry schema = new XMPSchemaRegistryImpl();
    /**
     * cache for version info
     */
    private static XMPVersionInfo versionInfo = null;

    /**
     * Hides public constructor
     */
    private XMPMetaFactory() {
        // EMPTY
    }

    /**
     * @return Returns the singleton instance of the <code>XMPSchemaRegistry</code>.
     */
    public static XMPSchemaRegistry getSchemaRegistry() {
        return schema;
    }

    /**
     * @return Returns an empty <code>XMPMeta</code>-object.
     */
    public static XMPMeta create() {
        return new XMPMetaImpl();
    }

    /**
     * Parsing with default options.
     *
     * @param in an <code>InputStream</code>
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     * @see XMPMetaFactory#parse(java.io.InputStream, ParseOptions)
     */
    public static XMPMeta parse(InputStream in) throws XMPException {
        return parse(in, null);
    }

    /**
     * These functions support parsing serialized RDF into an XMP object, and serailizing an XMP
     * object into RDF. The input for parsing may be any valid Unicode
     * encoding. ISO Latin-1 is also recognized, but its use is strongly discouraged. Serialization
     * is always as UTF-8.
     * <br>
     * <code>parseFromBuffer()</code> parses RDF from an <code>InputStream</code>. The encoding
     * is recognized automatically.
     *
     * @param in      an <code>InputStream</code>
     * @param options Options controlling the parsing.<br>
     *                The available options are:
     *                <ul>
     *                <li> XMP_REQUIRE_XMPMETA - The &lt;x:xmpmeta&gt; XML element is required around
     *                <tt>&lt;rdf:RDF&gt;</tt>.
     *                <li> XMP_STRICT_ALIASING - Do not reconcile alias differences, throw an exception.
     *                </ul>
     *                <em>Note:</em>The XMP_STRICT_ALIASING option is not yet implemented.
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     */
    public static XMPMeta parse(InputStream in, ParseOptions options)
            throws XMPException {
        return XMPMetaParser.parse(in, options);
    }

    /**
     * Parsing with default options.
     *
     * @param packet a String contain an XMP-file.
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     * @see XMPMetaFactory#parse(java.io.InputStream)
     */
    public static XMPMeta parseFromString(String packet) throws XMPException {
        return parseFromString(packet, null);
    }

    /**
     * Creates an <code>XMPMeta</code>-object from a string.
     *
     * @param packet  a String contain an XMP-file.
     * @param options Options controlling the parsing.
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     * @see XMPMetaFactory#parseFromString(String, ParseOptions)
     */
    public static XMPMeta parseFromString(String packet, ParseOptions options)
            throws XMPException {
        return XMPMetaParser.parse(packet, options);
    }

    /**
     * Parsing with default options.
     *
     * @param buffer a String contain an XMP-file.
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     * @see XMPMetaFactory#parseFromBuffer(byte[], ParseOptions)
     */
    public static XMPMeta parseFromBuffer(byte[] buffer) throws XMPException {
        return parseFromBuffer(buffer, null);
    }

    /**
     * Creates an <code>XMPMeta</code>-object from a byte-buffer.
     *
     * @param buffer  a String contain an XMP-file.
     * @param options Options controlling the parsing.
     * @return Returns the <code>XMPMeta</code>-object created from the input.
     * @throws XMPException If the file is not well-formed XML or if the parsing fails.
     * @see XMPMetaFactory#parse(java.io.InputStream, ParseOptions)
     */
    public static XMPMeta parseFromBuffer(byte[] buffer,
                                          ParseOptions options) throws XMPException {
        return XMPMetaParser.parse(buffer, options);
    }

    /**
     * Serializes an <code>XMPMeta</code>-object as RDF into an <code>OutputStream</code>
     * with default options.
     *
     * @param xmp a metadata object
     * @param out an <code>OutputStream</code> to write the serialized RDF to.
     * @throws XMPException on serializsation errors.
     */
    public static void serialize(XMPMeta xmp, OutputStream out) throws XMPException {
        serialize(xmp, out, null);
    }

    /**
     * Serializes an <code>XMPMeta</code>-object as RDF into an <code>OutputStream</code>.
     *
     * @param xmp     a metadata object
     * @param options Options to control the serialization (see {@link SerializeOptions}).
     * @param out     an <code>OutputStream</code> to write the serialized RDF to.
     * @throws XMPException on serializsation errors.
     */
    public static void serialize(XMPMeta xmp, OutputStream out, SerializeOptions options)
            throws XMPException {
        assertImplementation(xmp);
        XMPSerializerHelper.serialize((XMPMetaImpl) xmp, out, options);
    }

    /**
     * Serializes an <code>XMPMeta</code>-object as RDF into a byte buffer.
     *
     * @param xmp     a metadata object
     * @param options Options to control the serialization (see {@link SerializeOptions}).
     * @return Returns a byte buffer containing the serialized RDF.
     * @throws XMPException on serializsation errors.
     */
    public static byte[] serializeToBuffer(XMPMeta xmp, SerializeOptions options)
            throws XMPException {
        assertImplementation(xmp);
        return XMPSerializerHelper.serializeToBuffer((XMPMetaImpl) xmp, options);
    }

    /**
     * Serializes an <code>XMPMeta</code>-object as RDF into a string. <em>Note:</em> Encoding
     * is ignored when serializing to a string.
     *
     * @param xmp     a metadata object
     * @param options Options to control the serialization (see {@link SerializeOptions}).
     * @return Returns a string containing the serialized RDF.
     * @throws XMPException on serializsation errors.
     */
    public static String serializeToString(XMPMeta xmp, SerializeOptions options)
            throws XMPException {
        assertImplementation(xmp);
        return XMPSerializerHelper.serializeToString((XMPMetaImpl) xmp, options);
    }

    /**
     * @param xmp Asserts that xmp is compatible to <code>XMPMetaImpl</code>.s
     */
    private static void assertImplementation(XMPMeta xmp) {
        if (!(xmp instanceof XMPMetaImpl)) {
            throw new UnsupportedOperationException("The serializing service works only" +
                    "with the XMPMeta implementation of this library");
        }
    }

    /**
     * Resets the schema registry to its original state (creates a new one).
     * Be careful this might break all existing XMPMeta-objects and should be used
     * only for testing purpurses.
     */
    public static void reset() {
        schema = new XMPSchemaRegistryImpl();
    }

    /**
     * Obtain version information. The XMPVersionInfo singleton is created the first time
     * its requested.
     *
     * @return Returns the version information.
     */
    public static XMPVersionInfo getVersionInfo() {
        synchronized (staticLock) {
            if (versionInfo == null) {
                try {
                    final int major = 5;
                    final int minor = 1;
                    final int micro = 0;
                    final int engBuild = 3;
                    final boolean debug = false;

                    // Adobe XMP Core 5.0-jc001 DEBUG-<branch>.<changelist>, 2009 Jan 28 15:22:38-CET
                    final String message = "Adobe XMP Core 5.1.0-jc003";


                    versionInfo = new XMPVersionInfo() {
                        public int getMajor() {
                            return major;
                        }

                        public int getMinor() {
                            return minor;
                        }

                        public int getMicro() {
                            return micro;
                        }

                        public boolean isDebug() {
                            return debug;
                        }

                        public int getBuild() {
                            return engBuild;
                        }

                        public String getMessage() {
                            return message;
                        }

                        public String toString() {
                            return message;
                        }
                    };

                } catch (Throwable e) {
                    // EMTPY, severe error would be detected during the tests
                    System.out.println(e);
                }
            }
            return versionInfo;
        }
    }
}
