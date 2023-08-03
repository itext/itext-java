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

import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPError;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;


/**
 * @since   11.08.2006
 */
class ParameterAsserts implements XMPConst
{
	/**
	 * private constructor
	 */
	private ParameterAsserts()
	{
		// EMPTY
	}

	
	/**
	 * Asserts that an array name is set.
	 * @param arrayName an array name
	 * @throws XMPException Array name is null or empty
	 */
	public static void assertArrayName(String arrayName) throws XMPException
	{
		if (arrayName == null  ||  arrayName.length() == 0)
		{
			throw new XMPException("Empty array name", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a property name is set.
	 * @param propName a property name or path
	 * @throws XMPException Property name is null or empty
	 */
	public static void assertPropName(String propName) throws XMPException
	{
		if (propName == null  ||  propName.length() == 0)
		{
			throw new XMPException("Empty property name", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a schema namespace is set.
	 * @param schemaNS a schema namespace
	 * @throws XMPException Schema is null or empty
	 */
	public static void assertSchemaNS(String schemaNS) throws XMPException
	{
		if (schemaNS == null  ||  schemaNS.length() == 0)
		{
			throw new XMPException("Empty schema namespace URI", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a prefix is set.
	 * @param prefix a prefix
	 * @throws XMPException Prefix is null or empty
	 */
	public static void assertPrefix(String prefix) throws XMPException
	{
		if (prefix == null  ||  prefix.length() == 0)
		{
			throw new XMPException("Empty prefix", XMPError.BADPARAM);
		}
	}
	
	
	/**
	 * Asserts that a specific language is set.
	 * @param specificLang a specific lang
	 * @throws XMPException Specific language is null or empty
	 */
	public static void assertSpecificLang(String specificLang) throws XMPException
	{
		if (specificLang == null  ||  specificLang.length() == 0)
		{
			throw new XMPException("Empty specific language", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a struct name is set.
	 * @param structName a struct name
	 * @throws XMPException Struct name is null or empty
	 */
	public static void assertStructName(String structName) throws XMPException
	{
		if (structName == null  ||  structName.length() == 0)
		{
			throw new XMPException("Empty array name", XMPError.BADPARAM);
		}
	}


	/**
	 * Asserts that any string parameter is set.
	 * @param param any string parameter
	 * @throws XMPException Thrown if the parameter is null or has length 0.
	 */
	public static void assertNotNull(Object param) throws XMPException
	{
		if (param == null)
		{
			throw new XMPException("Parameter must not be null", XMPError.BADPARAM);
		}
		else if ((param instanceof String)  &&  ((String) param).length() == 0)
		{
			throw new XMPException("Parameter must not be null or empty", XMPError.BADPARAM);
		}
	}


	/**
	 * Asserts that the xmp object is of this implemention
	 * ({@link XMPMetaImpl}). 
	 * @param xmp the XMP object
	 * @throws XMPException A wrong implentaion is used.
	 */
	public static void assertImplementation(XMPMeta xmp) throws XMPException
	{
		if (xmp == null)
		{
			throw new XMPException("Parameter must not be null",
					XMPError.BADPARAM);
		}
		else if (!(xmp instanceof XMPMetaImpl))
		{
			throw new XMPException("The XMPMeta-object is not compatible with this implementation",
					XMPError.BADPARAM);
		}
	}
}