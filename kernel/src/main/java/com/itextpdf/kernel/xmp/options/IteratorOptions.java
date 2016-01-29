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


/**
 * Options for <code>XMPIterator</code> construction.
 * 
 * @since 24.01.2006
 */
public final class IteratorOptions extends Options
{
	/** Just do the immediate children of the root, default is subtree. */
	public static final int JUST_CHILDREN = 0x0100;
	/** Just do the leaf nodes, default is all nodes in the subtree.
	 *  Bugfix #2658965: If this option is set the Iterator returns the namespace 
	 *  of the leaf instead of the namespace of the base property. */
	public static final int JUST_LEAFNODES = 0x0200;
	/** Return just the leaf part of the path, default is the full path. */
	public static final int JUST_LEAFNAME = 0x0400;
//	/** Include aliases, default is just actual properties. <em>Note:</em> Not supported. 
//	 *  @deprecated it is commonly preferred to work with the base properties */
//	public static final int INCLUDE_ALIASES = 0x0800;
	/** Omit all qualifiers. */
	public static final int OMIT_QUALIFIERS = 0x1000;


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustChildren()
	{
		return getOption(JUST_CHILDREN);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustLeafname()
	{
		return getOption(JUST_LEAFNAME);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustLeafnodes()
	{
		return getOption(JUST_LEAFNODES);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isOmitQualifiers()
	{
		return getOption(OMIT_QUALIFIERS);
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustChildren(boolean value)
	{
		setOption(JUST_CHILDREN, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustLeafname(boolean value)
	{
		setOption(JUST_LEAFNAME, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustLeafnodes(boolean value)
	{
		setOption(JUST_LEAFNODES, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setOmitQualifiers(boolean value)
	{
		setOption(OMIT_QUALIFIERS, value);
		return this;
	}


	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case JUST_CHILDREN : 	return "JUST_CHILDREN";
			case JUST_LEAFNODES :	return "JUST_LEAFNODES";
			case JUST_LEAFNAME :	return "JUST_LEAFNAME";
			case OMIT_QUALIFIERS :	return "OMIT_QUALIFIERS";
			default: 				return null;
		}
	}


	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
			JUST_CHILDREN |
			JUST_LEAFNODES |
			JUST_LEAFNAME |
			OMIT_QUALIFIERS;
	}
}