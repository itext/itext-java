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

package com.itextpdf.kernel.xmp.options;

import com.itextpdf.kernel.xmp.XMPMetaFactory;


/**
 * Options for {@link XMPMetaFactory#parse(java.io.InputStream, ParseOptions)}.
 * 
 * @since 24.01.2006
 */
public final class ParseOptions extends Options
{
	/** Require a surrounding &quot;x:xmpmeta&quot; element in the xml-document. */
	public static final int REQUIRE_XMP_META = 0x0001;
	/** Do not reconcile alias differences, throw an exception instead. */
	public static final int STRICT_ALIASING = 0x0004;
	/** Convert ASCII control characters 0x01 - 0x1F (except tab, cr, and lf) to spaces. */
	public static final int FIX_CONTROL_CHARS = 0x0008;
	/** If the input is not unicode, try to parse it as ISO-8859-1. */
	public static final int ACCEPT_LATIN_1 = 0x0010;
	/** Do not carry run the XMPNormalizer on a packet, leave it as it is. */
	public static final int OMIT_NORMALIZATION = 0x0020;

	
	/**
	 * Sets the options to the default values.
	 */
	public ParseOptions()
	{
		setOption(FIX_CONTROL_CHARS | ACCEPT_LATIN_1, true);
	}
	
	
	/**
	 * @return Returns the requireXMPMeta.
	 */
	public boolean getRequireXMPMeta()
	{
		return getOption(REQUIRE_XMP_META);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public ParseOptions setRequireXMPMeta(boolean value)
	{
		setOption(REQUIRE_XMP_META, value);
		return this;
	}

	
	/**
	 * @return Returns the strictAliasing.
	 */
	public boolean getStrictAliasing()
	{
		return getOption(STRICT_ALIASING);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public ParseOptions setStrictAliasing(boolean value)
	{
		setOption(STRICT_ALIASING, value);
		return this;
	}
	

	/**
	 * @return Returns the strictAliasing.
	 */
	public boolean getFixControlChars()
	{
		return getOption(FIX_CONTROL_CHARS);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public ParseOptions setFixControlChars(boolean value)
	{
		setOption(FIX_CONTROL_CHARS, value);
		return this;
	}
	
	
	/**
	 * @return Returns the strictAliasing.
	 */
	public boolean getAcceptLatin1()
	{
		return getOption(ACCEPT_LATIN_1);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public ParseOptions setOmitNormalization(boolean value)
	{
		setOption(OMIT_NORMALIZATION, value);
		return this;
	}


	/**
	 * @return Returns the option "omit normalization".
	 */
	public boolean getOmitNormalization()
	{
		return getOption(OMIT_NORMALIZATION);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public ParseOptions setAcceptLatin1(boolean value)
	{
		setOption(ACCEPT_LATIN_1, value);
		return this;
	}

	
	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case REQUIRE_XMP_META :		return "REQUIRE_XMP_META";
			case STRICT_ALIASING :		return "STRICT_ALIASING";
			case FIX_CONTROL_CHARS:		return "FIX_CONTROL_CHARS";
			case ACCEPT_LATIN_1:		return "ACCEPT_LATIN_1";
			case OMIT_NORMALIZATION:	return "OMIT_NORMALIZATION";
			default: 					return null;
		}
	}

	
	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
			REQUIRE_XMP_META |
			STRICT_ALIASING |
			FIX_CONTROL_CHARS |
			ACCEPT_LATIN_1 |
			OMIT_NORMALIZATION;
	}
}