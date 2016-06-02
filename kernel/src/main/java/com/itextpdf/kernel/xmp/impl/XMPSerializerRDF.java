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
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Serializes the <code>XMPMeta</code>-object using the standard RDF serialization format. 
 * The output is written to an <code>OutputStream</code> 
 * according to the <code>SerializeOptions</code>. 
 * 
 * @since   11.07.2006
 */
public class XMPSerializerRDF
{
	/** default padding */
	private static final int DEFAULT_PAD = 2048;	
	/** */
	private static final String PACKET_HEADER  =
		"<?xpacket begin=\"\uFEFF\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>";
	/** The w/r is missing inbetween */
	private static final String PACKET_TRAILER = "<?xpacket end=\"";
	/** */
	private static final String PACKET_TRAILER2 = "\"?>"; 
	/** */
	private static final String RDF_XMPMETA_START = 
		"<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"";
	/** */
	private static final String RDF_XMPMETA_END   = "</x:xmpmeta>";
	/** */
	private static final String RDF_RDF_START = 
		"<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">";
	/** */
	private static final String RDF_RDF_END       = "</rdf:RDF>";
	
	/** */
	private static final String RDF_SCHEMA_START  = "<rdf:Description rdf:about=";
	/** */
	private static final String RDF_SCHEMA_END    = "</rdf:Description>";
	/** */
	private static final String RDF_STRUCT_START  = "<rdf:Description";
	/** */
	private static final String RDF_STRUCT_END    = "</rdf:Description>";
	/** */
	private static final String RDF_EMPTY_STRUCT  = "<rdf:Description/>";
	/** a set of all rdf attribute qualifier */
	static final Set<String> RDF_ATTR_QUALIFIER = new HashSet<>(Arrays.asList(new String[] {
			XMPConst.XML_LANG, "rdf:resource", "rdf:ID", "rdf:bagID", "rdf:nodeID" }));
	
	/** the metadata object to be serialized. */ 
	private XMPMetaImpl xmp; 
	/** the output stream to serialize to */ 
	private CountOutputStream outputStream;
	/** this writer is used to do the actual serialization */
	private OutputStreamWriter writer;
	/** the stored serialization options */
	private SerializeOptions options;
	/** the size of one unicode char, for UTF-8 set to 1 
	 *  (Note: only valid for ASCII chars lower than 0x80),
	 *  set to 2 in case of UTF-16 */
	private int unicodeSize = 1; // UTF-8
	/** the padding in the XMP Packet, or the length of the complete packet in
	 *  case of option <em>exactPacketLength</em>. */ 
	private int padding;

	
	/**
	 * The actual serialization.
	 * 
	 * @param xmp the metadata object to be serialized
	 * @param out outputStream the output stream to serialize to
	 * @param options the serialization options
	 * 
	 * @throws XMPException If case of wrong options or any other serialization error.
	 */
	public void serialize(XMPMeta xmp, OutputStream out, 
			SerializeOptions options) throws XMPException
	{
		try
		{
			outputStream = new CountOutputStream(out); 
			this.xmp = (XMPMetaImpl) xmp;
			this.options = options;
			this.padding = options.getPadding();

			writer = new OutputStreamWriter(outputStream, options.getEncoding());

			checkOptionsConsistence();
			
			// serializes the whole packet, but don't write the tail yet 
			// and flush to make sure that the written bytes are calculated correctly
			String tailStr = serializeAsRDF();
			writer.flush();
			
			// adds padding
			addPadding(tailStr.length());

			// writes the tail
			write(tailStr);
			writer.flush();
			
			outputStream.close();
		}
		catch (IOException e)
		{
			throw new XMPException("Error writing to the OutputStream", XMPError.UNKNOWN);
		}
	}


	/**
	 * Calculates the padding according to the options and write it to the stream.
	 * @param tailLength the length of the tail string 
	 * @throws XMPException thrown if packet size is to small to fit the padding
	 * @throws java.io.IOException forwards writer errors
	 */
	private void addPadding(int tailLength) throws XMPException, IOException
	{
		if (options.getExactPacketLength())
		{
			// the string length is equal to the length of the UTF-8 encoding
			int minSize = outputStream.getBytesWritten() + tailLength * unicodeSize;
			if (minSize > padding)
			{
				throw new XMPException("Can't fit into specified packet size",
					XMPError.BADSERIALIZE);
			}
			padding -= minSize;	// Now the actual amount of padding to add.
		}

		// fix rest of the padding according to Unicode unit size.
		padding /= unicodeSize;

		int newlineLen = options.getNewline().length();
		if (padding >= newlineLen)
		{
			padding -= newlineLen;	// Write this newline last.
			while (padding >= (100 + newlineLen))
			{
				writeChars(100, ' ');
				writeNewline();
				padding -= (100 + newlineLen);
			}
			writeChars(padding, ' ');
			writeNewline();
		}
		else
		{
			writeChars(padding, ' ');
		}
	}


	/**
	 * Checks if the supplied options are consistent.
	 * @throws XMPException Thrown if options are conflicting
	 */
	protected void checkOptionsConsistence() throws XMPException
	{
		if (options.getEncodeUTF16BE() | options.getEncodeUTF16LE())
		{
			unicodeSize = 2;
		}

		if (options.getExactPacketLength())
		{
			if (options.getOmitPacketWrapper() | options.getIncludeThumbnailPad())
			{
				throw new XMPException("Inconsistent options for exact size serialize",
						XMPError.BADOPTIONS);
			}
			if ((options.getPadding() & (unicodeSize - 1)) != 0)
			{
				throw new XMPException("Exact size must be a multiple of the Unicode element",
						XMPError.BADOPTIONS);
			}
		}
		else if (options.getReadOnlyPacket())
		{
			if (options.getOmitPacketWrapper() | options.getIncludeThumbnailPad())
			{
				throw new XMPException("Inconsistent options for read-only packet",
						XMPError.BADOPTIONS);
			}
			padding = 0;
		}
		else if (options.getOmitPacketWrapper())
		{
			if (options.getIncludeThumbnailPad())
			{
				throw new XMPException("Inconsistent options for non-packet serialize",
						XMPError.BADOPTIONS);
			}
			padding = 0;
		}
		else
		{
			if (padding == 0)
			{
				padding = DEFAULT_PAD * unicodeSize;
			}

			if (options.getIncludeThumbnailPad())
			{
				if (!xmp.doesPropertyExist(XMPConst.NS_XMP, "Thumbnails"))
				{
					padding += 10000 * unicodeSize;
				}
			}
		}
	}


	/**
	 * Writes the (optional) packet header and the outer rdf-tags.
	 * @return Returns the packet end processing instraction to be written after the padding.
	 * @throws java.io.IOException Forwarded writer exceptions.
	 * @throws XMPException
	 */
	private String serializeAsRDF() throws IOException, XMPException
	{
		int level = 0;

		// Write the packet header PI.
		if (!options.getOmitPacketWrapper())
		{
			writeIndent(level);
			write(PACKET_HEADER);
			writeNewline();
		}

		// Write the x:xmpmeta element's start tag.
		if (!options.getOmitXmpMetaElement())
		{
			writeIndent(level);
			write(RDF_XMPMETA_START);
			// Note: this flag can only be set by unit tests
			if (!options.getOmitVersionAttribute())
			{
				write(XMPMetaFactory.getVersionInfo().getMessage());
			}
			write("\">");
			writeNewline();
			level++;
		}

		// Write the rdf:RDF start tag.
		writeIndent(level);
		write(RDF_RDF_START);
		writeNewline();

		// Write all of the properties.
		if (options.getUseCanonicalFormat())
		{
			serializeCanonicalRDFSchemas(level);
		}
		else
		{
			serializeCompactRDFSchemas(level);
		}

		// Write the rdf:RDF end tag.
		writeIndent(level);
		write(RDF_RDF_END);
		writeNewline();

		// Write the xmpmeta end tag.
		if (!options.getOmitXmpMetaElement())
		{
			level--;
			writeIndent(level);
			write(RDF_XMPMETA_END);
			writeNewline();
		}
		// Write the packet trailer PI into the tail string as UTF-8.
		String tailStr = "";
		if (!options.getOmitPacketWrapper())
		{
			for (level = options.getBaseIndent(); level > 0; level--)
			{
				tailStr += options.getIndent();
			}

			tailStr += PACKET_TRAILER;
			tailStr += options.getReadOnlyPacket() ? 'r' : 'w';
			tailStr += PACKET_TRAILER2;
		}

		return tailStr;
	}


	/**
	 * Serializes the metadata in pretty-printed manner.
	 * @param level indent level
	 * @throws java.io.IOException Forwarded writer exceptions
	 * @throws XMPException
	 */
	private void serializeCanonicalRDFSchemas(int level) throws IOException, XMPException
	{
		if (xmp.getRoot().getChildrenLength() > 0)
		{
			startOuterRDFDescription(xmp.getRoot(), level);

			for (Iterator it = xmp.getRoot().iterateChildren(); it.hasNext(); )
			{
				XMPNode currSchema = (XMPNode) it.next();
				serializeCanonicalRDFSchema(currSchema, level);
			}

			endOuterRDFDescription(level);
		}
		else
		{
			writeIndent(level + 1);
			write(RDF_SCHEMA_START); // Special case an empty XMP object.
			writeTreeName();
			write("/>");
			writeNewline();
		}
	}


	/**
	 * @throws java.io.IOException
	 */
	private void writeTreeName() throws IOException
	{
		write('"');
		String name = xmp.getRoot().getName();
		if (name != null)
		{
			appendNodeValue(name, true);
		}
		write('"');
	}


	/**
	 * Serializes the metadata in compact manner.
	 * @param level indent level to start with
	 * @throws java.io.IOException Forwarded writer exceptions
	 * @throws XMPException
	 */
	private void serializeCompactRDFSchemas(int level) throws IOException, XMPException
	{
		// Begin the rdf:Description start tag.
		writeIndent(level + 1);
		write(RDF_SCHEMA_START);
		writeTreeName();

		// Write all necessary xmlns attributes.
		Set<String> usedPrefixes = new HashSet<>();
		usedPrefixes.add("xml");
		usedPrefixes.add("rdf");

		for (Iterator it = xmp.getRoot().iterateChildren(); it.hasNext();)
		{
			XMPNode schema = (XMPNode) it.next();
			declareUsedNamespaces(schema, usedPrefixes, level + 3);
		}

		// Write the top level "attrProps" and close the rdf:Description start tag.
		boolean allAreAttrs = true;
		for (Iterator it = xmp.getRoot().iterateChildren(); it.hasNext();)
		{
			XMPNode schema = (XMPNode) it.next();
			allAreAttrs &= serializeCompactRDFAttrProps (schema, level + 2);
		}

		if (!allAreAttrs)
		{
			write('>');
			writeNewline();
		}
		else
		{
			write("/>");
			writeNewline();
			return;	// ! Done if all properties in all schema are written as attributes.
		}

		// Write the remaining properties for each schema.
		for (Iterator it = xmp.getRoot().iterateChildren(); it.hasNext();)
		{
			XMPNode schema = (XMPNode) it.next();
			serializeCompactRDFElementProps (schema, level + 2);
		}

		// Write the rdf:Description end tag.
		writeIndent(level + 1);
		write(RDF_SCHEMA_END);
		writeNewline();
	}



	/**
	 * Write each of the parent's simple unqualified properties as an attribute. Returns true if all
	 * of the properties are written as attributes.
	 *
	 * @param parentNode the parent property node
	 * @param indent the current indent level
	 * @return Returns true if all properties can be rendered as RDF attribute.
	 * @throws java.io.IOException
	 */
	private boolean serializeCompactRDFAttrProps(XMPNode parentNode, int indent) throws IOException
	{
		boolean allAreAttrs = true;

		for (Iterator it = parentNode.iterateChildren(); it.hasNext();)
		{
			XMPNode prop = (XMPNode) it.next();

			if (canBeRDFAttrProp(prop))
			{
				writeNewline();
				writeIndent(indent);
				write(prop.getName());
				write("=\"");
				appendNodeValue(prop.getValue(), true);
				write('"');
			}
			else
			{
				allAreAttrs = false;
			}
		}
		return allAreAttrs;
	}


	/**
	 * Recursively handles the "value" for a node that must be written as an RDF
	 * property element. It does not matter if it is a top level property, a
	 * field of a struct, or an item of an array. The indent is that for the
	 * property element. The patterns bwlow ignore attribute qualifiers such as
	 * xml:lang, they don't affect the output form.
	 *
	 * <blockquote>
	 *
	 * <pre>
	 *  	&lt;ns:UnqualifiedStructProperty-1
	 *  		... The fields as attributes, if all are simple and unqualified
	 *  	/&gt;
	 *
	 *  	&lt;ns:UnqualifiedStructProperty-2 rdf:parseType=&quot;Resource&quot;&gt;
	 *  		... The fields as elements, if none are simple and unqualified
	 *  	&lt;/ns:UnqualifiedStructProperty-2&gt;
	 *
	 *  	&lt;ns:UnqualifiedStructProperty-3&gt;
	 *  		&lt;rdf:Description
	 *  			... The simple and unqualified fields as attributes
	 *  		&gt;
	 *  			... The compound or qualified fields as elements
	 *  		&lt;/rdf:Description&gt;
	 *  	&lt;/ns:UnqualifiedStructProperty-3&gt;
	 *
	 *  	&lt;ns:UnqualifiedArrayProperty&gt;
	 *  		&lt;rdf:Bag&gt; or Seq or Alt
	 *  			... Array items as rdf:li elements, same forms as top level properties
	 *  		&lt;/rdf:Bag&gt;
	 *  	&lt;/ns:UnqualifiedArrayProperty&gt;
	 *
	 *  	&lt;ns:QualifiedProperty rdf:parseType=&quot;Resource&quot;&gt;
	 *  		&lt;rdf:value&gt; ... Property &quot;value&quot;
	 *  			following the unqualified forms ... &lt;/rdf:value&gt;
	 *  		... Qualifiers looking like named struct fields
	 *  	&lt;/ns:QualifiedProperty&gt;
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * *** Consider numbered array items, but has compatibility problems. ***
	 * Consider qualified form with rdf:Description and attributes.
	 *
	 * @param parentNode the parent node
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards writer exceptions
	 * @throws XMPException If qualifier and element fields are mixed.
	 */
	private void serializeCompactRDFElementProps(XMPNode parentNode, int indent)
			throws IOException, XMPException
	{
		for (Iterator it = parentNode.iterateChildren(); it.hasNext();)
		{
			XMPNode node = (XMPNode) it.next();
			if (canBeRDFAttrProp (node))
			{
				continue;
			}

			boolean emitEndTag = true;
			boolean indentEndTag = true;

			// Determine the XML element name, write the name part of the start tag. Look over the
			// qualifiers to decide on "normal" versus "rdf:value" form. Emit the attribute
			// qualifiers at the same time.
			String elemName = node.getName();
			if (XMPConst.ARRAY_ITEM_NAME.equals(elemName))
			{
				elemName = "rdf:li";
			}

			writeIndent(indent);
			write('<');
			write(elemName);

			boolean hasGeneralQualifiers = false;
			boolean hasRDFResourceQual   = false;

			for (Iterator iq = 	node.iterateQualifier(); iq.hasNext();)
			{
				XMPNode qualifier = (XMPNode) iq.next();
				if (!RDF_ATTR_QUALIFIER.contains(qualifier.getName()))
				{
					hasGeneralQualifiers = true;
				}
				else
				{
					hasRDFResourceQual = "rdf:resource".equals(qualifier.getName());
					write(' ');
					write(qualifier.getName());
					write("=\"");
					appendNodeValue(qualifier.getValue(), true);
					write('"');
				}
			}


			// Process the property according to the standard patterns.
			if (hasGeneralQualifiers)
			{
				serializeCompactRDFGeneralQualifier(indent, node);
			}
			else
			{
				// This node has only attribute qualifiers. Emit as a property element.
				if (!node.getOptions().isCompositeProperty())
				{
					boolean[] result = serializeCompactRDFSimpleProp(node);
					emitEndTag = result[0];
					indentEndTag = result[1];
				}
				else if (node.getOptions().isArray())
				{
					serializeCompactRDFArrayProp(node, indent);
				}
				else
				{
					emitEndTag = serializeCompactRDFStructProp(
						node, indent, hasRDFResourceQual);
				}

			}

			// Emit the property element end tag.
			if (emitEndTag)
			{
				if (indentEndTag)
				{
					writeIndent(indent);
				}
				write("</");
				write(elemName);
				write('>');
				writeNewline();
			}

		}
	}


	/**
	 * Serializes a simple property.
	 *
	 * @param node an XMPNode
	 * @return Returns an array containing the flags emitEndTag and indentEndTag.
	 * @throws java.io.IOException Forwards the writer exceptions.
	 */
	private boolean[] serializeCompactRDFSimpleProp(XMPNode node) throws IOException
	{
		// This is a simple property.
		boolean emitEndTag = true;
		boolean indentEndTag = true;

		if (node.getOptions().isURI())
		{
			write(" rdf:resource=\"");
			appendNodeValue(node.getValue(), true);
			write("\"/>");
			writeNewline();
			emitEndTag = false;
		}
		else if (node.getValue() == null  ||  node.getValue().length() == 0)
		{
			write("/>");
			writeNewline();
			emitEndTag = false;
		}
		else
		{
			write('>');
			appendNodeValue (node.getValue(), false);
			indentEndTag = false;
		}

		return new boolean[] {emitEndTag, indentEndTag};
	}


	/**
	 * Serializes an array property.
	 *
	 * @param node an XMPNode
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards the writer exceptions.
	 * @throws XMPException If qualifier and element fields are mixed.
	 */
	private void serializeCompactRDFArrayProp(XMPNode node, int indent) throws IOException,
			XMPException
	{
		// This is an array.
		write('>');
		writeNewline();
		emitRDFArrayTag (node, true, indent + 1);

		if (node.getOptions().isArrayAltText())
		{
			XMPNodeUtils.normalizeLangArray (node);
		}

		serializeCompactRDFElementProps(node, indent + 2);

		emitRDFArrayTag(node, false, indent + 1);
	}


	/**
	 * Serializes a struct property.
	 *
	 * @param node an XMPNode
	 * @param indent the current indent level
	 * @param hasRDFResourceQual Flag if the element has resource qualifier
	 * @return Returns true if an end flag shall be emitted.
	 * @throws java.io.IOException Forwards the writer exceptions.
	 * @throws XMPException If qualifier and element fields are mixed.
	 */
	private boolean serializeCompactRDFStructProp(XMPNode node, int indent,
			boolean hasRDFResourceQual) throws XMPException, IOException
	{
		// This must be a struct.
		boolean hasAttrFields = false;
		boolean hasElemFields = false;
		boolean emitEndTag = true;

		for (Iterator ic = node.iterateChildren(); ic.hasNext(); )
		{
			XMPNode field = (XMPNode) ic.next();
			if (canBeRDFAttrProp(field))
			{
				hasAttrFields = true;
			}
			else
			{
				hasElemFields = true;
			}

			if (hasAttrFields  &&  hasElemFields)
			{
				break;	// No sense looking further.
			}
		}

		if (hasRDFResourceQual && hasElemFields)
		{
			throw new XMPException(
					"Can't mix rdf:resource qualifier and element fields",
					XMPError.BADRDF);
		}

		if (!node.hasChildren())
		{
			// Catch an empty struct as a special case. The case
			// below would emit an empty
			// XML element, which gets reparsed as a simple property
			// with an empty value.
			write(" rdf:parseType=\"Resource\"/>");
			writeNewline();
			emitEndTag = false;

		}
		else if (!hasElemFields)
		{
			// All fields can be attributes, use the
			// emptyPropertyElt form.
			serializeCompactRDFAttrProps(node, indent + 1);
			write("/>");
			writeNewline();
			emitEndTag = false;

		}
		else if (!hasAttrFields)
		{
			// All fields must be elements, use the
			// parseTypeResourcePropertyElt form.
			write(" rdf:parseType=\"Resource\">");
			writeNewline();
			serializeCompactRDFElementProps(node, indent + 1);

		}
		else
		{
			// Have a mix of attributes and elements, use an inner rdf:Description.
			write('>');
			writeNewline();
			writeIndent(indent + 1);
			write(RDF_STRUCT_START);
			serializeCompactRDFAttrProps(node, indent + 2);
			write(">");
			writeNewline();
			serializeCompactRDFElementProps(node, indent + 1);
			writeIndent(indent + 1);
			write(RDF_STRUCT_END);
			writeNewline();
		}
		return emitEndTag;
	}


	/**
	 * Serializes the general qualifier.
	 * @param node the root node of the subtree
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards all writer exceptions.
	 * @throws XMPException If qualifier and element fields are mixed.
	 */
	private void serializeCompactRDFGeneralQualifier(int indent, XMPNode node)
			throws IOException, XMPException
	{
		// The node has general qualifiers, ones that can't be
		// attributes on a property element.
		// Emit using the qualified property pseudo-struct form. The
		// value is output by a call
		// to SerializePrettyRDFProperty with emitAsRDFValue set.
		write(" rdf:parseType=\"Resource\">");
		writeNewline();

		serializeCanonicalRDFProperty(node, false, true, indent + 1);

		for (Iterator iq = 	node.iterateQualifier(); iq.hasNext();)
		{
			XMPNode qualifier = (XMPNode) iq.next();
			serializeCanonicalRDFProperty(qualifier, false, false, indent + 1);
		}
	}


	/**
	 * Serializes one schema with all contained properties in pretty-printed
	 * manner.<br>
	 * Each schema's properties are written to a single
	 * rdf:Description element. All of the necessary namespaces are declared in
	 * the rdf:Description element. The baseIndent is the base level for the
	 * entire serialization, that of the x:xmpmeta element. An xml:lang
	 * qualifier is written as an attribute of the property start tag, not by
	 * itself forcing the qualified property form.
	 *
	 * <blockquote>
	 *
	 * <pre>
	 *  	 &lt;rdf:Description rdf:about=&quot;TreeName&quot; xmlns:ns=&quot;URI&quot; ... &gt;
	 *
	 *  	 	... The actual properties of the schema, see SerializePrettyRDFProperty
	 *
	 *  	 	&lt;!-- ns1:Alias is aliased to ns2:Actual --&gt;  ... If alias comments are wanted
	 *
	 *  	 &lt;/rdf:Description&gt;
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param schemaNode a schema node
	 * @param level
	 * @throws java.io.IOException Forwarded writer exceptions
	 * @throws XMPException
	 */
	private void serializeCanonicalRDFSchema(XMPNode schemaNode, int level) throws IOException, XMPException
	{
		// Write each of the schema's actual properties.
		for (Iterator it = schemaNode.iterateChildren(); it.hasNext();)
		{
			XMPNode propNode = (XMPNode) it.next();
			serializeCanonicalRDFProperty(propNode, options.getUseCanonicalFormat(), false, level + 2);
		}
	}


	/**
	 * Writes all used namespaces of the subtree in node to the output.
	 * The subtree is recursivly traversed.
	 * @param node the root node of the subtree
	 * @param usedPrefixes a set containing currently used prefixes
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards all writer exceptions.
	 */
	private void declareUsedNamespaces(XMPNode node, Set<String> usedPrefixes, int indent)
			throws IOException
	{
		if (node.getOptions().isSchemaNode())
		{
			// The schema node name is the URI, the value is the prefix.
			String prefix = node.getValue().substring(0, node.getValue().length() - 1);
			declareNamespace(prefix, node.getName(), usedPrefixes, indent);
		}
		else if (node.getOptions().isStruct())
		{
			for (Iterator it = node.iterateChildren(); it.hasNext();)
			{
				XMPNode field = (XMPNode) it.next();
				declareNamespace(field.getName(), null, usedPrefixes, indent);
			}
		}

		for (Iterator it = node.iterateChildren(); it.hasNext();)
		{
			XMPNode child = (XMPNode) it.next();
			declareUsedNamespaces(child, usedPrefixes, indent);
		}

		for (Iterator it = node.iterateQualifier(); it.hasNext();)
		{
			XMPNode qualifier = (XMPNode) it.next();
			declareNamespace(qualifier.getName(), null, usedPrefixes, indent);
			declareUsedNamespaces(qualifier, usedPrefixes, indent);
		}
	}


	/**
	 * Writes one namespace declaration to the output.
	 * @param prefix a namespace prefix (without colon) or a complete qname (when namespace == null)
	 * @param namespace the a namespace
	 * @param usedPrefixes a set containing currently used prefixes
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards all writer exceptions.
	 */
	private void declareNamespace(String prefix, String namespace, Set<String> usedPrefixes, int indent)
			throws IOException
	{
		if (namespace == null)
		{
			// prefix contains qname, extract prefix and lookup namespace with prefix
			QName qname = new QName(prefix);
			if (qname.hasPrefix())
			{
				prefix = qname.getPrefix();
				// add colon for lookup
				namespace = XMPMetaFactory.getSchemaRegistry().getNamespaceURI(prefix + ":");
				// prefix w/o colon
				declareNamespace(prefix, namespace, usedPrefixes, indent);
			}
			else
			{
				return;
			}
		}

		if (!usedPrefixes.contains(prefix))
		{
			writeNewline();
			writeIndent(indent);
			write("xmlns:");
			write(prefix);
			write("=\"");
			write(namespace);
			write('"');
			usedPrefixes.add(prefix);
		}
	}


	/**
	 * Start the outer rdf:Description element, including all needed xmlns attributes.
	 * Leave the element open so that the compact form can add property attributes.
	 *
	 * @throws java.io.IOException If the writing to
	 */
	private void startOuterRDFDescription(XMPNode schemaNode, int level) throws IOException
	{
		writeIndent(level + 1);
		write(RDF_SCHEMA_START);
		writeTreeName();

		Set<String> usedPrefixes = new HashSet<>();
		usedPrefixes.add("xml");
		usedPrefixes.add("rdf");

		declareUsedNamespaces(schemaNode, usedPrefixes, level + 3);

		write('>');
		writeNewline();
	}


	/**
	 *  Write the </rdf:Description> end tag.
	 */
	private void endOuterRDFDescription(int level) throws IOException
	{
		writeIndent(level + 1);
		write(RDF_SCHEMA_END);
		writeNewline();
	}


	/**
	 * Recursively handles the "value" for a node. It does not matter if it is a
	 * top level property, a field of a struct, or an item of an array. The
	 * indent is that for the property element. An xml:lang qualifier is written
	 * as an attribute of the property start tag, not by itself forcing the
	 * qualified property form. The patterns below mostly ignore attribute
	 * qualifiers like xml:lang. Except for the one struct case, attribute
	 * qualifiers don't affect the output form.
	 *
	 * <blockquote>
	 *
	 * <pre>
	 * 	&lt;ns:UnqualifiedSimpleProperty&gt;value&lt;/ns:UnqualifiedSimpleProperty&gt;
	 *
	 * 	&lt;ns:UnqualifiedStructProperty&gt; (If no rdf:resource qualifier)
	 * 		&lt;rdf:Description&gt;
	 * 			... Fields, same forms as top level properties
	 * 		&lt;/rdf:Description&gt;
	 * 	&lt;/ns:UnqualifiedStructProperty&gt;
	 *
	 * 	&lt;ns:ResourceStructProperty rdf:resource=&quot;URI&quot;
	 * 		... Fields as attributes
	 * 	&gt;
	 *
	 * 	&lt;ns:UnqualifiedArrayProperty&gt;
	 * 		&lt;rdf:Bag&gt; or Seq or Alt
	 * 			... Array items as rdf:li elements, same forms as top level properties
	 * 		&lt;/rdf:Bag&gt;
	 * 	&lt;/ns:UnqualifiedArrayProperty&gt;
	 *
	 * 	&lt;ns:QualifiedProperty&gt;
	 * 		&lt;rdf:Description&gt;
	 * 			&lt;rdf:value&gt; ... Property &quot;value&quot; following the unqualified
	 * 				forms ... &lt;/rdf:value&gt;
	 * 			... Qualifiers looking like named struct fields
	 * 		&lt;/rdf:Description&gt;
	 * 	&lt;/ns:QualifiedProperty&gt;
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param node the property node
	 * @param emitAsRDFValue property shall be rendered as attribute rather than tag
	 * @param useCanonicalRDF use canonical form with inner description tag or
	 * 		  the compact form with rdf:ParseType=&quot;resource&quot; attribute.
	 * @param indent the current indent level
	 * @throws java.io.IOException Forwards all writer exceptions.
	 * @throws XMPException If &quot;rdf:resource&quot; and general qualifiers are mixed.
	 */
	private void serializeCanonicalRDFProperty(
		XMPNode node, boolean useCanonicalRDF, boolean emitAsRDFValue, int indent)
			throws IOException, XMPException
	{
		boolean emitEndTag   = true;
		boolean indentEndTag = true;

		// Determine the XML element name. Open the start tag with the name and
		// attribute qualifiers.

		String elemName = node.getName();
		if (emitAsRDFValue)
		{
			elemName = "rdf:value";
		}
		else if (XMPConst.ARRAY_ITEM_NAME.equals(elemName))
		{
			elemName = "rdf:li";
		}

		writeIndent(indent);
		write('<');
		write(elemName);

		boolean hasGeneralQualifiers = false;
		boolean hasRDFResourceQual   = false;

		for (Iterator it = node.iterateQualifier(); it.hasNext();)
		{
			XMPNode qualifier = (XMPNode) it.next();
			if (!RDF_ATTR_QUALIFIER.contains(qualifier.getName()))
			{
				hasGeneralQualifiers = true;
			}
			else
			{
				hasRDFResourceQual = "rdf:resource".equals(qualifier.getName());
				if (!emitAsRDFValue)
				{
					write(' ');
					write(qualifier.getName());
					write("=\"");
					appendNodeValue(qualifier.getValue(), true);
					write('"');
				}
			}
		}

		// Process the property according to the standard patterns.

		if (hasGeneralQualifiers &&  !emitAsRDFValue)
		{
			// This node has general, non-attribute, qualifiers. Emit using the
			// qualified property form.
			// ! The value is output by a recursive call ON THE SAME NODE with
			// emitAsRDFValue set.

			if (hasRDFResourceQual)
			{
				throw new XMPException("Can't mix rdf:resource and general qualifiers",
						XMPError.BADRDF);
			}

			// Change serialization to canonical format with inner rdf:Description-tag
			// depending on option
			if (useCanonicalRDF)
			{
				write(">");
				writeNewline();

				indent++;
				writeIndent(indent);
				write(RDF_STRUCT_START);
				write(">");
			}
			else
			{
				write(" rdf:parseType=\"Resource\">");
			}
			writeNewline();

			serializeCanonicalRDFProperty(node, useCanonicalRDF, true, indent + 1);

			for (Iterator it = node.iterateQualifier(); it.hasNext();)
			{
				XMPNode qualifier = (XMPNode) it.next();
				if (!RDF_ATTR_QUALIFIER.contains(qualifier.getName()))
				{
					serializeCanonicalRDFProperty(qualifier, useCanonicalRDF, false, indent + 1);
				}
			}

			if (useCanonicalRDF)
			{
				writeIndent(indent);
				write(RDF_STRUCT_END);
				writeNewline();
				indent--;
			}
		}
		else
		{
			// This node has no general qualifiers. Emit using an unqualified form.

			if (!node.getOptions().isCompositeProperty())
			{
				// This is a simple property.

				if (node.getOptions().isURI())
				{
					write(" rdf:resource=\"");
					appendNodeValue(node.getValue(), true);
					write("\"/>");
					writeNewline();
					emitEndTag = false;
				}
				else if (node.getValue() == null ||  "".equals(node.getValue()))
				{
					write("/>");
					writeNewline();
					emitEndTag = false;
				}
				else
				{
					write('>');
					appendNodeValue(node.getValue(), false);
					indentEndTag = false;
				}
			}
			else if (node.getOptions().isArray())
			{
				// This is an array.
				write('>');
				writeNewline();
				emitRDFArrayTag(node, true, indent + 1);
				if (node.getOptions().isArrayAltText())
				{
					XMPNodeUtils.normalizeLangArray(node);
				}
				for (Iterator it = node.iterateChildren(); it.hasNext();)
				{
					XMPNode child = (XMPNode) it.next();
					serializeCanonicalRDFProperty(child, useCanonicalRDF, false,  indent + 2);
				}
				emitRDFArrayTag(node, false, indent + 1);


			}
			else if (!hasRDFResourceQual)
			{
				// This is a "normal" struct, use the rdf:parseType="Resource" form.
				if (!node.hasChildren())
				{
					// Change serialization to canonical format with inner rdf:Description-tag
					// if option is set
					if (useCanonicalRDF)
					{
						write(">");
						writeNewline();
						writeIndent(indent + 1);
						write(RDF_EMPTY_STRUCT);
					}
					else
					{
						write(" rdf:parseType=\"Resource\"/>");
						emitEndTag = false;
					}
					writeNewline();
				}
				else
				{
					// Change serialization to canonical format with inner rdf:Description-tag
					// if option is set
					if (useCanonicalRDF)
					{
						write(">");
						writeNewline();
						indent++;
						writeIndent(indent);
						write(RDF_STRUCT_START);
						write(">");
					}
					else
					{
						write(" rdf:parseType=\"Resource\">");
					}
					writeNewline();

					for (Iterator it = node.iterateChildren(); it.hasNext();)
					{
						XMPNode child = (XMPNode) it.next();
						serializeCanonicalRDFProperty(child, useCanonicalRDF, false, indent + 1);
					}

					if (useCanonicalRDF)
					{
						writeIndent(indent);
						write(RDF_STRUCT_END);
						writeNewline();
						indent--;
					}
				}
			}
			else
			{
				// This is a struct with an rdf:resource attribute, use the
				// "empty property element" form.
				for (Iterator it = node.iterateChildren(); it.hasNext();)
				{
					XMPNode child = (XMPNode) it.next();
					if (!canBeRDFAttrProp(child))
					{
						throw new XMPException("Can't mix rdf:resource and complex fields",
								XMPError.BADRDF);
					}
					writeNewline();
					writeIndent(indent + 1);
					write(' ');
					write(child.getName());
					write("=\"");
					appendNodeValue(child.getValue(), true);
					write('"');
				}
				write("/>");
				writeNewline();
				emitEndTag = false;
			}
		}

		// Emit the property element end tag.
		if (emitEndTag)
		{
			if (indentEndTag)
			{
				writeIndent(indent);
			}
			write("</");
			write(elemName);
			write('>');
			writeNewline();
		}
	}


	/**
	 * Writes the array start and end tags.
	 *
	 * @param arrayNode an array node
	 * @param isStartTag flag if its the start or end tag
	 * @param indent the current indent level
	 * @throws java.io.IOException forwards writer exceptions
	 */
	private void emitRDFArrayTag(XMPNode arrayNode, boolean isStartTag, int indent)
		throws IOException
	{
		if (isStartTag  ||  arrayNode.hasChildren())
		{
			writeIndent(indent);
			write(isStartTag ? "<rdf:" : "</rdf:");

			if (arrayNode.getOptions().isArrayAlternate())
			{
				write("Alt");
			}
			else if (arrayNode.getOptions().isArrayOrdered())
			{
				write("Seq");
			}
			else
			{
				write("Bag");
			}

			if (isStartTag && !arrayNode.hasChildren())
			{
				write("/>");
			}
			else
			{
				write(">");
			}

			writeNewline();
		}
	}


	/**
	 * Serializes the node value in XML encoding. Its used for tag bodies and
	 * attributes. <em>Note:</em> The attribute is always limited by quotes,
	 * thats why <code>&amp;apos;</code> is never serialized. <em>Note:</em>
	 * Control chars are written unescaped, but if the user uses others than tab, LF
	 * and CR the resulting XML will become invalid.
	 *
	 * @param value the value of the node
	 * @param forAttribute flag if value is an attribute value
	 * @throws java.io.IOException
	 */
	private void appendNodeValue(String value, boolean forAttribute) throws IOException
	{
		if (value == null)
		{
			value = "";
		}
		write (Utils.escapeXML(value, forAttribute, true));
	}


	/**
	 * A node can be serialized as RDF-Attribute, if it meets the following conditions:
	 * <ul>
	 *  	<li>is not array item
	 * 		<li>don't has qualifier
	 * 		<li>is no URI
	 * 		<li>is no composite property
	 * </ul>
	 *
	 * @param node an XMPNode
	 * @return Returns true if the node serialized as RDF-Attribute
	 */
	private boolean canBeRDFAttrProp(XMPNode node)
	{
		return
			!node.hasQualifier()  &&
			!node.getOptions().isURI()  &&
			!node.getOptions().isCompositeProperty()  &&
            !node.getOptions().containsOneOf(PropertyOptions.SEPARATE_NODE) &&
			!XMPConst.ARRAY_ITEM_NAME.equals(node.getName());
	}


	/**
	 * Writes indents and automatically includes the baseindend from the options.
	 * @param times number of indents to write
	 * @throws java.io.IOException forwards exception
	 */
	private void writeIndent(int times) throws IOException
	{
		for (int i = options.getBaseIndent() + times; i > 0; i--)
		{
			writer.write(options.getIndent());
		}
	}


	/**
	 * Writes a char to the output.
	 * @param c a char
	 * @throws java.io.IOException forwards writer exceptions
	 */
	private void write(int c) throws IOException
	{
		writer.write(c);
	}


	/**
	 * Writes a String to the output.
	 * @param str a String
	 * @throws java.io.IOException forwards writer exceptions
	 */
	private void write(String str) throws IOException
	{
		writer.write(str);
	}


	/**
	 * Writes an amount of chars, mostly spaces
	 * @param number number of chars
	 * @param c a char
	 * @throws java.io.IOException
	 */
	private void writeChars(int number, char c) throws IOException
	{
		for (; number > 0; number--)
		{
			writer.write(c);
		}
	}


	/**
	 * Writes a newline according to the options.
	 * @throws java.io.IOException Forwards exception
	 */
	private void writeNewline() throws IOException
	{
		writer.write(options.getNewline());
	}
}