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

package com.itextpdf.kernel.xmp.impl.xpath;


/**
 * A segment of a parsed <code>XMPPath</code>.
 *  
 * @since   23.06.2006
 */
public class XMPPathSegment
{
	/** name of the path segment */
	private String name;
	/** kind of the path segment */
	private int kind;
	/** flag if segment is an alias */
	private boolean alias;
	/** alias form if applicable */
	private int aliasForm;


	/**
	 * Constructor with initial values.
	 * 
	 * @param name the name of the segment
	 */
	public XMPPathSegment(String name)
	{
		this.name = name;
	}


	/**
	 * Constructor with initial values.
	 * 
	 * @param name the name of the segment
	 * @param kind the kind of the segment
	 */
	public XMPPathSegment(String name, int kind)
	{
		this.name = name;
		this.kind = kind;
	}


	/**
	 * @return Returns the kind.
	 */
	public int getKind()
	{
		return kind;
	}


	/**
	 * @param kind The kind to set.
	 */
	public void setKind(int kind)
	{
		this.kind = kind;
	}


	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @param alias the flag to set
	 */
	public void setAlias(boolean alias)
	{
		this.alias = alias;
	}


	/**
	 * @return Returns the alias.
	 */
	public boolean isAlias()
	{
		return alias;
	}
	
	
	/** 
	 * @return Returns the aliasForm if this segment has been created by an alias.
	 */ 
	public int getAliasForm()
	{
		return aliasForm;
	}

	
	/**
	 * @param aliasForm the aliasForm to set
	 */
	public void setAliasForm(int aliasForm)
	{
		this.aliasForm = aliasForm;
	}
	
	
	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		switch (kind)
		{
			case XMPPath.STRUCT_FIELD_STEP:
			case XMPPath.ARRAY_INDEX_STEP: 
			case XMPPath.QUALIFIER_STEP: 
			case XMPPath.ARRAY_LAST_STEP: 
				return name;
			case XMPPath.QUAL_SELECTOR_STEP: 
			case XMPPath.FIELD_SELECTOR_STEP: 
			return name;

		default:
			// no defined step
			return name;
		}
	}
}
