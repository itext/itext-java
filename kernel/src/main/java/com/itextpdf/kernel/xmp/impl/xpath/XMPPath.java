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

import java.util.ArrayList;
import java.util.List;


/**
 * Representates an XMP XMPPath with segment accessor methods.
 *
 * @since   28.02.2006
 */
public class XMPPath
{
	// Bits for XPathStepInfo options.
	
	/** Marks a struct field step , also for top level nodes (schema "fields"). */
	public static final int STRUCT_FIELD_STEP = 0x01;
	/** Marks a qualifier step. 
	 *  Note: Order is significant to separate struct/qual from array kinds! */
	public static final int QUALIFIER_STEP = 0x02; 		// 
	/** Marks an array index step */
	public static final int ARRAY_INDEX_STEP = 0x03;
	/** */
	public static final int ARRAY_LAST_STEP = 0x04;
	/** */
	public static final int QUAL_SELECTOR_STEP = 0x05;
	/** */
	public static final int FIELD_SELECTOR_STEP = 0x06;
	/** */
	public static final int SCHEMA_NODE = 0x80000000;	
	/** */
	public static final int STEP_SCHEMA = 0;
	/** */
	public static final int STEP_ROOT_PROP = 1;

	
	/** stores the segments of an XMPPath */
	private List segments = new ArrayList(5);
	
	
	/**
	 * Append a path segment
	 * 
	 * @param segment the segment to add
	 */
	public void add(XMPPathSegment segment)
	{	
		segments.add(segment);
	}

	
	/**
	 * @param index the index of the segment to return
	 * @return Returns a path segment.
	 */
	public XMPPathSegment getSegment(int index)
	{
		return (XMPPathSegment) segments.get(index);
	}
	
	
	/**
	 * @return Returns the size of the xmp path. 
	 */
	public int size()
	{
		return segments.size();
	}


	/**
	 * Return a single String explaining which certificate was verified, how and why.
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		int index = 1;
		while (index < size())
		{
			result.append(getSegment(index));
			if (index < size() - 1)
			{
				int kind = getSegment(index + 1).getKind(); 
				if (kind == STRUCT_FIELD_STEP  || 
					kind == QUALIFIER_STEP)
				{	
					// all but last and array indices
					result.append('/');
				}	
			}
			index++;			
		}
		
		return result.toString();
	}
}