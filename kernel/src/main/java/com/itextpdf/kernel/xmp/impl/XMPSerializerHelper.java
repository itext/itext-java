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

package com.itextpdf.kernel.xmp.impl;

import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * Serializes the <code>XMPMeta</code>-object to an <code>OutputStream</code> according to the
 * <code>SerializeOptions</code>. 
 * 
 * @since   11.07.2006
 */
public class XMPSerializerHelper
{
	/**
	 * Static method to serialize the metadata object. For each serialisation, a new XMPSerializer
	 * instance is created, either XMPSerializerRDF or XMPSerializerPlain so thats its possible to 
	 * serialialize the same XMPMeta objects in two threads.
	 * 
	 * @param xmp a metadata implementation object
	 * @param output the output stream to serialize to
	 * @param options serialization options, can be <code>null</code> for default.
	 * @throws XMPException
	 */
	public static void serialize(XMPMetaImpl xmp, OutputStream output,
		SerializeOptions options)
		throws XMPException
	{
		options = options != null ? options : new SerializeOptions();		
		
		// sort the internal data model on demand
		if (options.getSort())
		{
			xmp.sort();
		}
		new XMPSerializerRDF().serialize(xmp, output, options);
	}		
	

	/**
	 * Serializes an <code>XMPMeta</code>-object as RDF into a string.
	 * <em>Note:</em> Encoding is forced to UTF-16 when serializing to a
	 * string to ensure the correctness of &quot;exact packet size&quot;.
	 * 
	 * @param xmp a metadata implementation object
	 * @param options Options to control the serialization (see
	 *            {@link SerializeOptions}).
	 * @return Returns a string containing the serialized RDF.
	 * @throws XMPException on serializsation errors.
	 */
	public static String serializeToString(XMPMetaImpl xmp, SerializeOptions options)
		throws XMPException
	{
		// forces the encoding to be UTF-16 to get the correct string length
		options = options != null ? options : new SerializeOptions();		
		options.setEncodeUTF16BE(true);

		ByteArrayOutputStream output = new ByteArrayOutputStream(2048);
		serialize(xmp, output, options);

		try
		{
			return output.toString(options.getEncoding());
		}
		catch (UnsupportedEncodingException e)
		{
			// cannot happen as UTF-8/16LE/BE is required to be implemented in
			// Java
			return output.toString();
		}
	}
	
	
	/**
	 * Serializes an <code>XMPMeta</code>-object as RDF into a byte buffer.
	 * 
	 * @param xmp a metadata implementation object
	 * @param options Options to control the serialization (see {@link SerializeOptions}).
	 * @return Returns a byte buffer containing the serialized RDF.
	 * @throws XMPException on serializsation errors.
	 */
	public static byte[] serializeToBuffer(XMPMetaImpl xmp, SerializeOptions options)
			throws XMPException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		serialize(xmp, out, options);
		return out.toByteArray();
	}
}