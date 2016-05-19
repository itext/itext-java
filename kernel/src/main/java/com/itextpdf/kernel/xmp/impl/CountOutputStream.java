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

import java.io.IOException;
import java.io.OutputStream;


/**
 * An <code>OutputStream</code> that counts the written bytes.
 * 
 * @since   08.11.2006
 */
public final class CountOutputStream extends OutputStream
{
	/** the decorated output stream */
	private final OutputStream output;
	/** the byte counter */
	private int bytesWritten = 0;


	/**
	 * Constructor with providing the output stream to decorate.
	 * @param output an <code>OutputStream</code>
	 */
	CountOutputStream(OutputStream output)
	{
		this.output = output;
	}

	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] buf, int off, int len) throws IOException
	{
		output.write(buf, off, len);
		bytesWritten += len;
	}

	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] buf) throws IOException
	{
		output.write(buf);
		bytesWritten += buf.length;
	}

	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException
	{
		output.write(b);
		bytesWritten++;
	}

	/**
	 * @return the bytesWritten
	 */
	public int getBytesWritten()
	{
		return bytesWritten;
	}
}