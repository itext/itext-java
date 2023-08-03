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

package com.itextpdf.kernel.xmp;

import java.util.Iterator;


/**
 * Interface for the {@code XMPMeta} iteration services.
 * {@code XMPIterator} provides a uniform means to iterate over the
 * schema and properties within an XMP object.
 * <p>
 * The iteration over the schema and properties within an XMP object is very
 * complex. It is helpful to have a thorough understanding of the XMP data tree.
 * One way to learn this is to create some complex XMP and examine the output of
 * {@code XMPMeta#toString}. This is also described in the XMP
 * Specification, in the XMP Data Model chapter.
 * <p>
 * The top of the XMP data tree is a single root node. This does not explicitly
 * appear in the dump and is never visited by an iterator (that is, it is never
 * returned from {@code XMPIterator#next()}). Beneath the root are
 * schema nodes. These are just collectors for top level properties in the same
 * namespace. They are created and destroyed implicitly. Beneath the schema
 * nodes are the property nodes. The nodes below a property node depend on its
 * type (simple, struct, or array) and whether it has qualifiers.
 * <p>
 * An {@code XMPIterator} is created by {@code XMPMeta#interator()} constructor
 * defines a starting point for the iteration and options that control how it
 * proceeds. By default the iteration starts at the root and visits all nodes
 * beneath it in a depth first manner. The root node is not visited, the first
 * visited node is a schema node. You can provide a schema name or property path
 * to select a different starting node. By default this visits the named root
 * node first then all nodes beneath it in a depth first manner.
 * <p>
 * The {@code XMPIterator#next()} method delivers the schema URI, path,
 * and option flags for the node being visited. If the node is simple it also
 * delivers the value. Qualifiers for this node are visited next. The fields of
 * a struct or items of an array are visited after the qualifiers of the parent.
 * <p>
 * The options to control the iteration are:
 * <ul>
 * <li>JUST_CHILDREN - Visit just the immediate children of the root. Skip
 * the root itself and all nodes below the immediate children. This omits the
 * qualifiers of the immediate children, the qualifier nodes being below what
 * they qualify, default is to visit the complete subtree.
 * <li>JUST_LEAFNODES - Visit just the leaf property nodes and their
 * qualifiers.
 * <li>JUST_LEAFNAME - Return just the leaf component of the node names.
 * The default is to return the full xmp path.
 * <li>OMIT_QUALIFIERS - Do not visit the qualifiers.
 * <li>INCLUDE_ALIASES - Adds known alias properties to the properties in the iteration.
 * 		<em>Note:</em> Not supported in Java XMPCore!
 * </ul>
 * <p>
 * {@code next()} returns {@code XMPPropertyInfo}-objects and throws
 * a {@code NoSuchElementException} if there are no more properties to
 * return.
 *
 * @since 25.01.2006
 */
public interface XMPIterator extends Iterator
{
	/**
	 * Skip the subtree below the current node when <code>next()</code> is
	 * called.
	 */
	void skipSubtree();


	/**
	 * Skip the subtree below and remaining siblings of the current node when
	 * <code>next()</code> is called.
	 */
	void skipSiblings();
}