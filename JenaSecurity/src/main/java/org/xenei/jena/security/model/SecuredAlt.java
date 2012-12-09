/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xenei.jena.security.model;

import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceF;

import org.xenei.jena.security.AccessDeniedException;

/**
 * The interface for secured Alt instances.
 * 
 * Use the SecuredAlt.Factory to create instances
 */
public interface SecuredAlt extends Alt, SecuredContainer
{
	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredRDFNode getDefault() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt getDefaultAlt() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredBag getDefaultBag() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public boolean getDefaultBoolean() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public byte getDefaultByte() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public char getDefaultChar() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public double getDefaultDouble() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public float getDefaultFloat() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public int getDefaultInt() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public String getDefaultLanguage() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredLiteral getDefaultLiteral() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public long getDefaultLong() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredResource getDefaultResource() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	@Deprecated
	public SecuredResource getDefaultResource( final ResourceF f )
			throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredSeq getDefaultSeq() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public short getDefaultShort() throws AccessDeniedException;

	/**
	 * @graphSec Read
	 * @tripleSec Read SecTriple(this, RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public String getDefaultString() throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final boolean o )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final char o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final double o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final float o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final long o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final Object o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final RDFNode o )
			throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final String o ) throws AccessDeniedException;

	/**
	 * @graphSec Update
	 * @tripleSec Update SecTriple(this, RDF.li(1), existing ), SecTriple(this,
	 *            RDF.li(1), o )
	 * @throws AccessDeniedException
	 */
	@Override
	public SecuredAlt setDefault( final String o, final String l )
			throws AccessDeniedException;

}
