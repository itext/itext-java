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

/**
 * @since   09.11.2006
 */
public class QName
{
	/** XML namespace prefix */
	private String prefix;
	/** XML localname */
	private String localName;

	
	/**
	 * Splits a qname into prefix and localname.
	 * @param qname a QName
	 */
	public QName(String qname)
	{
		int colon = qname.indexOf(':');
		
		if (colon >= 0)
		{
			prefix = qname.substring(0, colon);
			localName = qname.substring(colon + 1);
		}
		else
		{
			prefix = "";
			localName = qname;
		}
	}
	
	
	/** Constructor that initializes the fields
	 * @param prefix the prefix  
	 * @param localName the name
	 */
	public QName(String prefix, String localName)
	{
		this.prefix = prefix;
		this.localName = localName;
	}

	
	/**
	 * @return Returns whether the QName has a prefix.
	 */
	public boolean hasPrefix()
	{
		return prefix != null  &&  prefix.length() > 0;
	}
	

	/**
	 * @return the localName
	 */
	public String getLocalName()
	{
		return localName;
	}


	/**
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return prefix;
	}
}