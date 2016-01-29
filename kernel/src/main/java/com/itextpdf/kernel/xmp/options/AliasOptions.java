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

import com.itextpdf.kernel.xmp.XMPException;


/**
 * Options for XMPSchemaRegistryImpl#registerAlias.
 * 
 * @since 20.02.2006
 */
public final class AliasOptions extends Options
{
	/** This is a direct mapping. The actual data type does not matter. */
	public static final int PROP_DIRECT = 0;
	/** The actual is an unordered array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY = PropertyOptions.ARRAY;
	/** The actual is an ordered array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY_ORDERED = PropertyOptions.ARRAY_ORDERED;
	/** The actual is an alternate array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY_ALTERNATE = PropertyOptions.ARRAY_ALTERNATE;
	/**
	 * The actual is an alternate text array, the alias is to the 'x-default' element of the array.
	 */
	public static final int PROP_ARRAY_ALT_TEXT = PropertyOptions.ARRAY_ALT_TEXT;

	
	/**
	 * @see Options#Options()
	 */
	public AliasOptions()
	{
		// EMPTY
	}

	
	/**
	 * @param options the options to init with
	 * @throws XMPException If options are not consistant
	 */
	public AliasOptions(int options) throws XMPException
	{
		super(options);
	}


	/**
	 * @return Returns if the alias is of the simple form.
	 */
	public boolean isSimple()
	{
		return getOptions() == PROP_DIRECT;
	}

	
	/**
	 * @return Returns the option.
	 */
	public boolean isArray()
	{
		return getOption(PROP_ARRAY);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArray(boolean value)
	{
		setOption(PROP_ARRAY, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayOrdered()
	{
		return getOption(PROP_ARRAY_ORDERED);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayOrdered(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayAlternate()
	{
		return getOption(PROP_ARRAY_ALTERNATE);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayAlternate(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED | PROP_ARRAY_ALTERNATE, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayAltText()
	{
		return getOption(PROP_ARRAY_ALT_TEXT);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayAltText(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED | 
			PROP_ARRAY_ALTERNATE | PROP_ARRAY_ALT_TEXT, value);
		return this;
	}


	/**
	 * @return returns a {@link PropertyOptions}s object
	 * @throws XMPException If the options are not consistant. 
	 */
	public PropertyOptions toPropertyOptions() throws XMPException
	{
		return new PropertyOptions(getOptions());
	}


	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case PROP_DIRECT : 			return "PROP_DIRECT";
			case PROP_ARRAY :			return "ARRAY";
			case PROP_ARRAY_ORDERED :	return "ARRAY_ORDERED";
			case PROP_ARRAY_ALTERNATE :	return "ARRAY_ALTERNATE";
			case PROP_ARRAY_ALT_TEXT :	return "ARRAY_ALT_TEXT";
			default: 					return null;
		}
	}

	
	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
			PROP_DIRECT |
			PROP_ARRAY |
			PROP_ARRAY_ORDERED |
			PROP_ARRAY_ALTERNATE |
			PROP_ARRAY_ALT_TEXT;
	}
}	